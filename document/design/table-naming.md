# 表命名规范

## 统一规则

1. **表名**：一律 `t_` 前缀，语义清晰（如 `t_patient`、`t_appointment`）
2. **时间/删除字段**统一为：

```sql
`create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '删除标识 0-否 1-是',
```

3. **禁止** `TINYINT(1)`（JDBC 易映射成 boolean），状态类字段统一 `TINYINT(4)`
4. Java 字段：`isDelete`，MyBatis-Plus `@TableLogic` + `logic-delete-field: isDelete`

## 表清单

| 表名 | 含义 |
|------|------|
| t_clinic | 诊所 |
| t_dept | 部门 |
| t_employee | 员工 |
| t_role | 角色 |
| t_menu | 菜单/权限 |
| t_employee_role | 员工-角色 |
| t_role_menu | 角色-菜单 |
| t_patient | 患者 |
| t_appointment | 预约 |
| t_appointment_log | 预约操作日志 |
| t_schedule | 员工日程 |
| t_dict_type | 字典类型 |
| t_dict_data | 字典数据 |
| t_patient_tag | 患者标签 |
| t_patient_tag_rel | 患者-标签 |
| t_patient_source | 患者来源 |
| t_treatment_item | 诊疗项目 |
| t_patient_relation | 患者亲友 |
| t_patient_log | 患者操作日志 |
| t_visit | 就诊 |
| t_medical_record | 电子病历 |
| t_treatment_record | 处置记录 |
| t_charge_record | 收费记录 |
| t_follow_up | 回访 |
| t_patient_file | 患者附件 |
| t_treatment_plan | 治疗计划 |
| t_consult_record | 咨询沟通 |

## 初始化

```bash
mysql -uroot -p < sql/00_full_schema.sql
mysql -uroot -p < sql/01_init_data.sql
```
