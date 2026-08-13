package com.yujian.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 审计字段自动填充处理器
 *
 * @author Zhangyk
 * @date 2026-08-13 15:50
 */
@Component
public class MybatisMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MybatisMetaObjectHandler.class);

    /**
     * 无登录上下文时的默认操作人ID
     */
    private static final Long DEFAULT_OPERATOR_ID = 0L;

    /**
     * 插入时自动填充创建人、创建时间、更新人、更新时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = resolveOperatorId();
        log.debug("【审计字段填充-插入】operatorId={}, time={}", operatorId, now);

        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", Long.class, operatorId);
        this.strictInsertFill(metaObject, "updateBy", Long.class, operatorId);
    }

    /**
     * 更新时自动填充更新人、更新时间
     *
     * @param metaObject 元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long operatorId = resolveOperatorId();
        log.debug("【审计字段填充-更新】operatorId={}, time={}", operatorId, now);

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictUpdateFill(metaObject, "updateBy", Long.class, operatorId);
    }

    /**
     * 解析当前操作人ID，无登录上下文时返回默认值 0L
     *
     * @return 操作人ID
     */
    private Long resolveOperatorId() {
        // TODO 接入登录上下文后，从 SecurityContext 或 ThreadLocal 获取当前员工ID
        return DEFAULT_OPERATOR_ID;
    }
}
