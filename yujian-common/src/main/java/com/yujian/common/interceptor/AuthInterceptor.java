package com.yujian.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.yujian.common.constant.Constants;
import com.yujian.common.core.context.SecurityContextHolder;
import com.yujian.common.core.domain.LoginUser;
import com.yujian.common.core.domain.R;
import com.yujian.common.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 登录 Token 拦截器
 */
public class AuthInterceptor implements HandlerInterceptor {

    public static final String HEADER_TOKEN = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final RedisTemplate<String, Object> redisTemplate;

    public AuthInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = resolveToken(request);
        if (StringUtils.isBlank(token)) {
            writeUnauthorized(response, "未登录或Token为空");
            return false;
        }
        try {
            Claims claims = JwtUtils.parseToken(token);
            Long userId = JwtUtils.getUserId(token);
            String username = claims.getSubject();

            if (redisTemplate != null) {
                Object cache = redisTemplate.opsForValue().get(Constants.REDIS_TOKEN_KEY + userId);
                if (cache == null) {
                    writeUnauthorized(response, "登录已过期，请重新登录");
                    return false;
                }
                // 滑动续期
                redisTemplate.expire(Constants.REDIS_TOKEN_KEY + userId, Constants.TOKEN_EXPIRE, TimeUnit.SECONDS);
            }

            LoginUser loginUser = new LoginUser();
            loginUser.setUserId(userId);
            loginUser.setUsername(username);
            loginUser.setToken(token);
            // clinicId 等扩展信息可从 Redis 缓存补全
            if (redisTemplate != null) {
                Object userCache = redisTemplate.opsForValue().get(Constants.REDIS_LOGIN_USER_KEY + userId);
                if (userCache instanceof LoginUser) {
                    LoginUser cached = (LoginUser) userCache;
                    loginUser.setName(cached.getName());
                    loginUser.setClinicId(cached.getClinicId());
                    loginUser.setDeptId(cached.getDeptId());
                    loginUser.setPermissions(cached.getPermissions());
                    loginUser.setRoles(cached.getRoles());
                }
            }
            SecurityContextHolder.setLoginUser(loginUser);
            return true;
        } catch (Exception e) {
            writeUnauthorized(response, "Token无效或已过期");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER_TOKEN);
        if (StringUtils.isNotBlank(header)) {
            if (header.startsWith(TOKEN_PREFIX)) {
                return header.substring(TOKEN_PREFIX.length()).trim();
            }
            return header.trim();
        }
        return request.getParameter("token");
    }

    private void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(401);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JSON.toJSONString(R.fail(401, msg)));
    }
}
