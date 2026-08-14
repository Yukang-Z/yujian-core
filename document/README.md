# 宇健口腔 - 项目文档目录

## 目录说明

| 路径 | 说明 |
|------|------|
| `api/frontend-api.md` | **前端联调接口说明**（地址 / 鉴权 / 传参） |
| `sql/` | 数据库脚本 |
| `sql/00_full_schema.sql` | **完整库表结构**（t_ 前缀 / is_delete / TINYINT(4)） |
| `sql/01_init_data.sql` | **初始化数据**（系统 + 业务 + 字典） |
| `design/` | 设计文档（可继续补充） |

## 快速初始化

```bash
mysql -uroot -p < sql/00_full_schema.sql
mysql -uroot -p < sql/01_init_data.sql
```

## 前端联调

- 接口文档：[`api/frontend-api.md`](api/frontend-api.md)
- 直连 Admin：`http://localhost:8081`
- 经网关：`http://localhost:8080/admin`
- Knife4j：`http://localhost:8081/doc.html`
- 默认账号：`admin` / `123456`

## 设计文档

- `design/system-management.md` 系统管理
- `design/biz-patient-appointment.md` 患者/预约/基础数据
- `design/gap-analysis-appointment.md` 预约查漏补缺
- `design/gap-analysis-patient.md` 患者详情查漏补缺
- `design/table-naming.md` 表命名规范
