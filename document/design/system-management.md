# 系统管理 ER 说明

## 核心关系

```text
t_clinic 1 ---- n t_dept
t_employee n ---- n t_clinic   (t_employee_clinic)
t_employee n ---- n t_role     (t_employee_role)
t_role     n ---- n t_menu     (t_role_menu)
```

## 权限模型（RBAC）

1. 员工绑定一个或多个角色
2. 角色绑定菜单/按钮权限（支持 web / mobile）
3. 角色 `data_scope` 控制数据范围：
   - 1 全部
   - 2 本诊所
   - 3 本部门
   - 4 仅本人
   - 5 自定义

## 员工字段对照（参考诊所管理系统）

| 业务字段 | 表字段 |
|----------|--------|
| 姓名 | name |
| 工号 | emp_no |
| 性别 | gender |
| 生日 | birthday |
| 手机号码 | mobile |
| 工作诊所 | clinic_id / t_employee_clinic |
| 在职状态 | employ_status |
| 角色 | 关联 t_role |
| 岗位 | position |
| 证件类型 | id_type |
| 证件号码 | id_number |

## 公共审计字段

所有业务主表统一：

```sql
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
```

关联表（`t_employee_role` / `t_role_menu` / `t_patient_tag_rel`）仅保留关联字段，不带逻辑删除。
