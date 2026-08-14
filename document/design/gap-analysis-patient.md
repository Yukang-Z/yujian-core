# 患者管理查漏补缺（对照患者详情截图）

## 一、截图 Tab 对照

| Tab | 状态 | 接口 / 表 |
|-----|------|-----------|
| 患者信息 | ✅ 增强 | `t_patient` + `/biz/patient/{id}/profile` |
| 会员卡券 | ⏳ 未做 | 下期（权益卡独立域） |
| 就诊信息 | ✅ | `/timeline` + `/visits` |
| 处置记录 | ✅ | `/treatments` → `t_treatment_record` |
| 收费信息 | ✅ | `/charges` → `t_charge_record` |
| 口腔检查 | ⏳ 牙位图专业域 | 下期 |
| 影像信息 | ✅ 元数据 | `/files?fileCategory=image` |
| 病历信息 | ✅ | `/medicalRecords` |
| 回访信息 | ✅ | `/followUps` |
| 咨询沟通 | ✅ | `/consults` |
| 外加工 | ⏳ | 下期 |
| 治疗计划 | ✅ | `/plans` |
| 手术记录 | ⏳ | 可先用病历/处置承载 |
| 电子协议 | ✅ 附件 | `/files?fileCategory=agreement` |
| 文档信息 | ✅ | `/files?fileCategory=document` |
| 亲友关系 | ✅ | `/relations` |
| 查看日志 | ✅ | `/logs` |
| 侧栏今日/全部/最近 | ✅ | `/sidebar` |

## 二、请执行 SQL

```bash
document/sql/00_full_schema.sql
document/sql/01_init_data.sql
```

## 三、推荐前端串联

```text
1. GET /biz/patient/sidebar?type=recent
2. GET /biz/patient/{id}/profile          # 头图+价值卡片
3. GET /biz/patient/{id}/timeline         # 就诊信息 Tab
4. GET /biz/patient/{id}/medicalRecords   # 病历
5. GET /biz/patient/{id}/treatments       # 处置
6. GET /biz/patient/{id}/charges          # 收费
7. GET /biz/patient/{id}/files?fileCategory=image
8. GET /biz/patient/{id}/followUps
9. GET /biz/patient/{id}/relations
10. PUT /biz/patient                       # 修改信息
```

文件实际上传（OSS/MinIO）由网关或独立文件服务完成，本模块只存 `fileUrl` 元数据。

## 四、仍未做（非阻断）

- 会员卡/券、口腔检查牙位图、外加工工单、手术专科模板
- 真实文件上传存储
- 病历打印/发送微信
