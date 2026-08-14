package com.yujian.common.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatis-Plus 配置：分页插件与自动填充
 *
 * @author Zhangyk
 * @date 2026-08-14 16:50
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器，启用 MySQL 分页
     *
     * @return 分页拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 按 MySQL 方言处理分页 SQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 注册字段自动填充处理器：插入/更新时填充公共审计字段
     *
     * @return 元对象处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 插入填充：创建时间、更新时间、逻辑删除标记及操作人
             *
             * @param metaObject 元对象
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "isDelete", Integer.class, 0);
                Long userId = com.yujian.common.core.context.SecurityContextHolder.getUserId();
                if (userId != null) {
                    this.strictInsertFill(metaObject, "createBy", Long.class, userId);
                    this.strictInsertFill(metaObject, "updateBy", Long.class, userId);
                }
            }

            /**
             * 更新填充：更新时间及更新人
             *
             * @param metaObject 元对象
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
                Long userId = com.yujian.common.core.context.SecurityContextHolder.getUserId();
                if (userId != null) {
                    this.strictUpdateFill(metaObject, "updateBy", Long.class, userId);
                }
            }
        };
    }
}
