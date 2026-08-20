# 新增预约弹窗 — 后端接口缺口说明

> 用途：对照 Dentalink「新增预约」三栏交互，核对现有 `frontend-api.md` / 实体是否满足。  
> 日期：2026-08-20  
> 前端暂不依赖「立即发送预约短信」「预约界面设置」「待确定预约」。

统一联调文档：`yujian-core/document/api/frontend-api.md`

---

## 1. 产品交互摘要（前端将实现）

| 区域 | 能力 |
|------|------|
| 左栏 | 患者信息（可选已有患者 / 现场建档）、预约门诊、普通预约、来源、医生、咨询师、就诊类型、预约人、备注 |
| 中栏 | 选日期；按医生拉当日已有预约；**30 分钟**为基数拖选/点选时段 |
| 右栏 | 预约项目多选列表（可搜索名称/首拼） |
| 底部 | 清空 / 确定 / 取消（短信、界面设置不做） |

约定：

- **预约门诊**：默认 = 登录后 `selectClinic` 的会话诊所；下拉 = 账号关联 `clinics`
- **预约类型**：默认普通预约（`appointType=normal`）；待确定不做
- **预约医生**：按所选门诊调 `doctor/list`；选中后中栏展示该医生当日预约
- **预约项目**：右侧多选勾选

---

## 2. 结论总览

| 优先级 | 结论 |
|--------|------|
| **不够用** | 跨授权门诊新建、项目列表按门诊、多选项目、咨询师下拉、就诊类型「新诊」 |
| **基本够用** | 选已有患者 + 会话诊所内预约；医生列表；dayGrid 看忙闲；单项目保存 |
| **前端可绕过** | 现场建档可先 `POST /biz/patient` 再 `POST /biz/appointment`（仅限会话诊所） |
| **明确不做** | 短信、界面设置、待确定预约 |

---

## 3. 已有且可用（无需新开）

| 能力 | 接口 | 说明 |
|------|------|------|
| 账号关联门诊 | 登录 / `auth/info` 的 `clinics` | 下拉数据源 |
| 医生列表 | `GET /biz/basic/doctor/list?clinicId=&keyword=` | 授权诊所已生效 |
| 医生当日预约 | `GET /biz/appointment/dayGrid?day=&clinicId=&doctorIds=` | 中栏忙闲展示 |
| 患者搜索 | `GET /biz/patient/search` | 选已有患者 |
| 患者建档 | `POST /biz/patient` | 现场建档两步走 |
| 患者来源树 | `GET /biz/basic/source/tree` | 写入患者 `sourceId` |
| 新建预约 | `POST /biz/appointment` | 见下表字段 |
| 普通预约默认 | `appointType` 默认 `normal` | 后端已默认 |

当前 `POST /biz/appointment` 已支持字段：

| 字段 | 现状 |
|------|------|
| patientId | 必填 |
| doctorId | 可空=未指定 |
| startTime / endTime | 必填 |
| visitType | 文档仅 1初诊 / 2复诊 |
| itemId / itemName / itemColor | 单项目 |
| consultantId | 实体有，可存 |
| appointType | 默认 normal |
| appointSource | 默认 clinic（渠道，≠患者来源） |
| remark / creatorName | 有 |
| clinicId | **写入被忽略，强制会话诊所** |

---

## 4. 必须改造 / 补充（请后端改）

### 4.1 【P0】新建预约支持授权 `clinicId`

**原因**：弹窗要「预约门诊」下拉；选非会话诊所时，写库仍进会话诊所会串数据。  
**场景**：账号有 A/B 两店，会话在 A，弹窗选 B 店为张医生预约。

**接口**：`POST /biz/appointment`（改现有）

**建议 Body 增量**

```json
{
  "clinicId": 2,
  "patientId": 2001,
  "doctorId": 12,
  "startTime": "2026-08-20 13:30:00",
  "endTime": "2026-08-20 14:00:00",
  "visitType": 1,
  "itemId": 3,
  "itemIds": [3, 5],
  "consultantId": 20,
  "appointType": "normal",
  "remark": ""
}
```

**逻辑**

1. `clinicId` 空 → 会话诊所；有值 → `resolveAuthorizedClinicId`（无权 403）。
2. 校验患者属于该 `clinicId`（不要写死会话诊所）。
3. 冲突检测、项目归属按该 `clinicId`。
4. 与查询侧 dayGrid/doctor/list 规则一致。

**当前问题**：`insertAppointment` → `requireClinicId` **忽略**入参；`validateAppointment` 用会话诊所校验患者。

---

### 4.2 【P0】诊疗项目列表支持授权 `clinicId` + 搜索

**原因**：右栏项目应按「预约门诊」加载；现 `GET /biz/basic/item/list` 注释写明 clinicId 忽略。  
**场景**：切换门诊后右侧项目列表跟着变；支持名称/首拼搜索更佳。

**接口**：`GET /biz/basic/item/list`（改现有）

| 参数 | 必填 | 说明 |
|------|------|------|
| clinicId | 否 | 授权诊所；空=会话 |
| keyword | 否 | 项目名称 / 编码 / 首拼 |

**反参建议**

```json
[
  {
    "id": 1,
    "itemName": "洗牙",
    "itemCode": "XY",
    "duration": 30,
    "itemColor": "#67C23A",
    "categoryName": "未分类",
    "pinyin": "xy",
    "sortOrder": 1
  }
]
```

**逻辑**：同医生列表的授权诊所解析；仅返回该诊所启用项目；`duration` 供前端默认 30 分钟选段。

**缺口**：无 `categoryName` / `pinyin` 时前端可先本地过滤名称；**clinicId 生效是硬需求**。

---

### 4.3 【P0】预约支持多项目（截图为多选）

**原因**：表结构 / 实体仅 `item_id` + `item_name` 单条；截图为 checkbox 多选。  
**场景**：一次预约勾选「补牙 + 洗牙」。

**方案（二选一，推荐 A）**

#### 方案 A：Body 增加 `itemIds`，主表仍保留首项

| 字段 | 说明 |
|------|------|
| itemIds | `Long[]`，有序；第一个同步写入 `itemId/itemName/itemColor` |
| 明细表 | 新建 `t_appointment_item(appointment_id, item_id, item_name, duration, sort)` |

详情/dayGrid 回显可带 `items: [{id,itemName,duration}]`。

#### 方案 B：仅支持单选

产品降级为单选；与截图不一致，需产品确认。

**请后端明确采用方案**；前端按 A 对接。

---

### 4.4 【P1】咨询师下拉列表

**原因**：实体有 `consultantId`，无「按诊所列咨询师」专用接口；员工分页为会话诊所且无职位过滤约定。  
**场景**：左栏「咨询师」下拉。

**建议新建**：`GET /biz/basic/consultant/list`

| 参数 | 必填 | 说明 |
|------|------|------|
| clinicId | 否 | 授权诊所；空=会话 |
| keyword | 否 | 姓名/手机 |

**反参**：与医生列表类似 `{ id, name, empNo, position, clinicId, mobile }`

**逻辑**：该诊所在职员工，职位含「咨询」；授权校验同 doctor/list。

**临时绕过**（不推荐）：`GET /system/employee/list` + 前端按职位过滤（clinicId 仍被忽略）。

---

### 4.5 【P1】就诊类型支持「新诊」= 3

**原因**：截图有 初诊 / 复诊 / 新诊；实体注释与文档仅 1、2。  
**场景**：左栏就诊类型三选一。

| visitType | 含义 |
|-----------|------|
| 1 | 初诊 |
| 2 | 复诊 |
| 3 | 新诊（需落库认可） |

请后端：字典 `visit_type`、校验、统计一并支持 3；或书面确认不做新诊（前端去掉）。

---

### 4.6 【P1】新建预约「日期不能早于今天」

**原因**：前端拖格已提示；后端应同样拦截防绕过。  
**接口**：`POST /biz/appointment`

**逻辑**：`startTime` 的日期 `< 今天(诊所时区)` → 业务错误「预约日期不能小于今天」。

---

### 4.7 【P2】现场建档 + 预约一步完成（可选）

**原因**：截图左栏直接填姓名/手机/性别/生日，不一定先搜患者。  
**现状**：可两步：`POST /biz/patient` → `POST /biz/appointment`（仅会话诊所稳妥）。

**可选增强**：`POST /biz/appointment/withPatient`

```json
{
  "clinicId": 2,
  "patient": {
    "name": "张三",
    "mobile": "13800138000",
    "mobileRelation": "本人",
    "phone": "",
    "phoneRelation": "本人",
    "gender": 1,
    "birthday": "1990-01-01",
    "sourceId": 10
  },
  "doctorId": 12,
  "startTime": "...",
  "endTime": "...",
  "visitType": 1,
  "itemIds": [1, 2],
  "consultantId": 20,
  "remark": ""
}
```

有 `patientId` 则不建患者；无则先建再约。非必须，有则体验更好。

---

## 5. 字段对照（截图 → 后端）

| UI 字段 | 后端字段/接口 | 是否满足 |
|---------|---------------|----------|
| 病历号 | 患者 `medicalRecordNo` 自动生成 | ✅ 建档时 |
| 姓名* | 患者 / 搜索 | ✅ |
| 预约门诊* | `clinicId` 写入 | ❌ 写入忽略，需 4.1 |
| 手机* + 关系 | `mobile` / `mobileRelation` | ✅ 患者 |
| 电话 + 关系 | `phone` / `phoneRelation` | ✅ 患者 |
| 出生日期 / 年龄 | `birthday` | ✅ 患者 |
| 性别* | `gender` | ✅ |
| 预约类型 | `appointType=normal` | ✅ 待确定不做 |
| 患者来源 | 患者 `sourceId` + source/tree | ✅（勿与 appointSource 混淆） |
| 预约医生* | `doctorId` + doctor/list | ✅ 查询侧 |
| 咨询师 | `consultantId` | ⚠️ 缺列表接口 4.4 |
| 就诊类型* | `visitType` | ⚠️ 缺「新诊=3」4.5 |
| 预约人 | `creatorName` 会话用户 | ✅ 一般只读 |
| 预约备注 | `remark` | ✅ |
| 预约时段 30m | 前端 + dayGrid | ✅ 展示；保存用 start/end |
| 预约项目多选 | `itemId` 单条 | ❌ 需 4.3 |
| 项目按门诊 | item/list | ❌ 需 4.2 |
| 短信 / 界面设置 | — | 不做 |

---

## 6. 建议联调顺序

1. **4.1** 新建预约 `clinicId` 授权生效 + 患者校验按该诊所  
2. **4.2** `item/list` 的 `clinicId` 生效（`duration` 尽量返回）  
3. **4.3** 多项目 `itemIds`（或产品改单选）  
4. **4.4** 咨询师 list  
5. **4.5** visitType=3  
6. **4.6** 日期 ≥ 今天  
7. （可选）4.7 一步建档预约  

---

## 7. 前端临时策略（后端未改完时）

| 点 | 策略 |
|----|------|
| 预约门诊 | 下拉可展示；保存仍进会话诊所，并提示「请先切换顶栏诊所」或禁用非当前项 |
| 项目 | 拉会话诊所 item/list；单选或本地多选但只提交第一项 |
| 咨询师 | 暂手工不选 / 员工列表凑合 |
| 新诊 | 可先展示，提交前若后端拒 3 则提示 |
| 现场建档 | 先建患者再预约（同会话诊所） |

---

## 8. 修订记录

| 日期 | 说明 |
|------|------|
| 2026-08-20 | 初稿：新增预约三栏 vs 现网接口缺口 |
| 2026-08-20 | **已落地**：4.1～4.6（方案 A 多项目）；4.7 建档+预约一步暂不做 |
