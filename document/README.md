# 宇健口腔 - 项目文档目录

## 目录说明

| 路径 | 说明 |
|------|------|
| `sql/` | 数据库脚本 |
| `sql/01_system_schema.sql` | 系统管理表结构 |
| `sql/02_system_data.sql` | 系统管理初始化数据 |
| `design/` | 设计文档（可继续补充） |

## 快速初始化

```bash
mysql -uroot -p < sql/01_system_schema.sql
mysql -uroot -p < sql/02_system_data.sql
mysql -uroot -p < sql/03_biz_schema.sql
mysql -uroot -p < sql/04_biz_data.sql
```

## 设计文档

- `design/system-management.md` 系统管理
- `design/biz-patient-appointment.md` 患者/预约/基础数据
