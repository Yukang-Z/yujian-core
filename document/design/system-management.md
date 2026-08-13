# 系统管理 ER 说明

## 核心关系

```text
sys_clinic 1 ---- n sys_dept
sys_clinic 1 ---- n sys_employee
sys_dept   1 ---- n sys_employee
sys_employee n ---- n sys_role     (sys_employee_role)
sys_role     n ---- n sys_menu     (sys_role_menu)
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
| 工作诊所 | clinic_id |
| 在职状态 | employ_status |
| 所属部门 | dept_id |
| 角色 | 关联 sys_role |
| 岗位 | position |
| 手机关联 | mobile_link |
| 证件类型 | id_type |
