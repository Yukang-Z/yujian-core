# 宇健口腔 - 项目文档目录

## 目录说明

| 路径 | 说明 |
|------|------|
| `sql/` | 数据库脚本 |
| `sql/00_full_schema.sql` | **完整库表结构**（t_ 前缀 / is_delete / TINYINT(4)） |
| `sql/01_init_data.sql` | **初始化数据**（系统 + 业务 + 字典） |
| `design/` | 设计文档（可继续补充） |

## 快速初始化

```bash
mysql -uroot -p < sql/00_full_schema.sql
mysql -uroot -p < sql/01_init_data.sql
```

## 设计文档

- `design/system-management.md` 系统管理
- `design/biz-patient-appointment.md` 患者/预约/基础数据
- `design/gap-analysis-appointment.md` 预约查漏补缺
- `design/gap-analysis-patient.md` 患者详情查漏补缺
