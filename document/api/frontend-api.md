# 宇健口腔 · 前端联调接口说明

> 模块：`yujian-admin`（Web 管理端）  
> 用途：地址、鉴权、传参、响应结构一览，供前端联调使用。  
> 在线文档（启动 admin 后）：http://localhost:8081/doc.html  

---

## 1. 环境与地址

| 方式 | Base URL | 说明 |
|------|----------|------|
| 直连 Admin | `http://localhost:8081` | 本地开发推荐 |
| 经 Gateway | `http://localhost:8080/admin` | 网关 `StripPrefix=1`，业务路径与直连一致 |

**示例**

```text
直连登录：  POST http://localhost:8081/auth/login
网关登录：  POST http://localhost:8080/admin/auth/login

直连患者：  GET  http://localhost:8081/biz/patient/list
网关患者：  GET  http://localhost:8080/admin/biz/patient/list
```

默认账号：`admin` / `123456`

---

## 2. 通用约定

### 2.1 鉴权

| 项 | 说明 |
|----|------|
| Header | `Authorization: Bearer {token}` |
| 白名单 | `POST /auth/login`（及 Swagger / Actuator） |
| 其余接口 | 均需携带 Token |

### 2.2 统一响应 `R<T>`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 401 | 未登录 / Token 失效 |
| 500 | 业务或系统失败（见 `msg`） |

### 2.3 分页 `PageResult<T>`

```json
{
  "total": 100,
  "pageNum": 1,
  "pageSize": 20,
  "records": []
}
```

分页查询通用 Query：`pageNum`（默认 1）、`pageSize`（默认 20）

### 2.4 时间与字段

- 日期时间：`yyyy-MM-dd HH:mm:ss`，时区 `GMT+8`
- 纯日期：`yyyy-MM-dd`
- JSON 字段：驼峰 camelCase
- 逻辑删除字段：`isDelete`（0 否 / 1 是），前端一般忽略
- 审计字段（多数实体）：`createBy` / `createTime` / `updateBy` / `updateTime` / `remark`

---

## 3. 推荐联调顺序

```text
1. POST /auth/login                         → 取 token
2. GET  /auth/info                          → 用户 / 菜单 / 权限
3. GET  /biz/basic/dict/{dictType}          → 字典下拉
4. GET  /biz/basic/doctor/list              → 医生列
5. GET  /biz/patient/list | /sidebar        → 患者
6. GET  /biz/appointment/dayGrid|calendar   → 预约视图
```

---

## 4. 登录鉴权 `/auth`

### 4.1 登录

`POST /auth/login`（无需 Token）

**Body**

```json
{
  "username": "admin",
  "password": "123456"
}
```

**data**

```json
{
  "token": "eyJhbGciOi...",
  "user": { "id": 1, "name": "管理员", "clinicId": 1, "deptId": 1, "username": "admin", "...": "..." }
}
```

### 4.2 当前用户信息

`GET /auth/info`

**data**：`{ user, roles, menus, permissions }`

### 4.3 指定员工信息

`GET /auth/info/{employeeId}`

### 4.4 退出

`POST /auth/logout`

---

## 5. 系统管理

### 5.1 诊所 `/system/clinic`

| 方法 | 路径 | 说明 | 传参 |
|------|------|------|------|
| GET | `/system/clinic/list` | 列表 | Query：`clinicName?` `clinicCode?` `status?` `parentId?` |
| GET | `/system/clinic/tree` | 树（当前实现为列表） | 同上 |
| GET | `/system/clinic/{id}` | 详情 | Path：`id` |
| POST | `/system/clinic` | 新增 | Body：`SysClinic` |
| PUT | `/system/clinic` | 修改 | Body：`SysClinic`（含 `id`） |
| DELETE | `/system/clinic/{id}` | 删除 | Path：`id` |

**SysClinic 主要字段**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| parentId | Long | 父诊所，0=总部 |
| clinicName | String | 名称 |
| clinicCode | String | 编码（唯一） |
| shortName | String | 简称 |
| contactName / contactPhone | String | 联系人/电话 |
| province / city / district / address | String | 地址 |
| businessHours | String | 营业时间 |
| logo | String | Logo URL |
| sortOrder | Integer | 排序 |
| status | Integer | 0正常 1停用 |
| openDate | String | 开业日 yyyy-MM-dd |

### 5.2 部门 `/system/dept`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/dept/list` | Query：`clinicId?` `deptName?` `status?` |
| GET | `/system/dept/{id}` | Path：`id` |
| POST | `/system/dept` | Body：`SysDept` |
| PUT | `/system/dept` | Body：`SysDept` |
| DELETE | `/system/dept/{id}` | Path：`id` |

**SysDept 主要字段**：`id` `clinicId` `parentId` `deptName` `deptCode` `leader` `phone` `sortOrder` `status`

### 5.3 员工 `/system/employee`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/employee/list` | Query：`keyword?` `clinicId?` `deptId?` `employStatus?` `pageNum` `pageSize` |
| GET | `/system/employee/{id}` | Path：`id` |
| POST | `/system/employee` | Body：`SysEmployee`（可含 `roleIds: Long[]`） |
| PUT | `/system/employee` | Body：`SysEmployee` |
| DELETE | `/system/employee/{id}` | Path：`id` |
| PUT | `/system/employee/resetPwd` | Body：`{ "id": 1, "password": "新密码" }` |
| PUT | `/system/employee/sort/{id}/{direction}` | Path：`direction` = `up` \| `down` |

**SysEmployee 主要字段**

| 字段 | 说明 |
|------|------|
| name / empNo / username / password | 姓名、工号、账号、密码 |
| gender | 0女 1男 2未知 |
| birthday | yyyy-MM-dd |
| mobile / email | 手机 / 邮箱 |
| clinicId / deptId | 诊所 / 部门 |
| position | 岗位 |
| employStatus | 1在职 0离职 |
| mobileLink | 1允许手机端 0不允许 |
| idType / idNumber | 证件 |
| avatar | 头像 |
| entryDate / leaveDate | 入职/离职 |
| sortOrder / status | 排序 / 0正常1停用 |
| roleIds | 角色ID列表（非表字段，保存时用） |

### 5.4 角色 `/system/role`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/role/list` | Query：`roleName?` `roleKey?` `status?` |
| GET | `/system/role/{id}` | Path：`id` |
| POST | `/system/role` | Body：`SysRole`（可含 `menuIds`） |
| PUT | `/system/role` | Body：`SysRole` |
| DELETE | `/system/role/{id}` | Path：`id` |
| PUT | `/system/role/auth` | Body：`{ "roleId": 1, "menuIds": [1,2,3] }` |
| GET | `/system/role/{id}/menus` | 已选菜单ID列表 |
| PUT | `/system/role/move/{id}/{direction}` | `up` \| `down` |

**SysRole**：`roleName` `roleKey` `sortOrder` `dataScope`(1全部/2本诊所/3本部门/4仅本人/5自定义) `status` `menuIds`

### 5.5 菜单 `/system/menu`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/menu/list` | Query：`menuName?` `platform?` `status?` `menuType?` |
| GET | `/system/menu/tree` | Query：`platform` 默认 `web` |
| GET | `/system/menu/{id}` | Path：`id` |
| POST | `/system/menu` | Body：`SysMenu` |
| PUT | `/system/menu` | Body：`SysMenu` |
| DELETE | `/system/menu/{id}` | Path：`id` |
| GET | `/system/menu/employee/{employeeId}` | Query：`platform` 默认 `web` |
| GET | `/system/menu/employee/{employeeId}/perms` | 权限标识字符串列表 |

**SysMenu**：`menuName` `parentId` `sortOrder` `path` `component` `perms` `menuType`(M目录/C菜单/F按钮) `platform`(web/mobile) `icon` `visible`(0显示1隐藏) `status`

---

## 6. 基础数据 `/biz/basic`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/basic/dict/types` | — |
| GET | `/biz/basic/dict/{dictType}` | Path：字典类型编码，如 `appoint_status` |
| POST | `/biz/basic/dict/data` | Body：`BizDictData` |
| DELETE | `/biz/basic/dict/data/{id}` | Path：`id` |
| GET | `/biz/basic/tag/list` | Query：`clinicId?` |
| POST | `/biz/basic/tag` | Body：`BizPatientTag` |
| DELETE | `/biz/basic/tag/{id}` | — |
| GET | `/biz/basic/source/tree` | Query：`clinicId?` |
| POST | `/biz/basic/source` | Body：`BizPatientSource` |
| DELETE | `/biz/basic/source/{id}` | — |
| GET | `/biz/basic/item/list` | Query：`clinicId?` |
| POST | `/biz/basic/item` | Body：`BizTreatItem` |
| DELETE | `/biz/basic/item/{id}` | — |
| GET | `/biz/basic/doctor/list` | Query：`clinicId?` → `{ id, name, empNo, position, clinicId }` |

**常用 dictType**（以初始化数据为准）：`appoint_status` `visit_type` `cancel_reason` `gender` 等。

**BizDictData**：`dictType` `dictLabel` `dictValue` `cssClass` `sortOrder` `status`  
**BizPatientTag**：`clinicId` `tagName` `tagColor` `sortOrder` `status`  
**BizPatientSource**：`clinicId` `parentId` `sourceName` `sortOrder` `status`  
**BizTreatItem**：`clinicId` `itemName` `itemCode` `duration` `itemColor` `sortOrder` `status`

---

## 7. 患者 `/biz/patient`

### 7.1 主接口

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/patient/list` | Query：`keyword?` `clinicId?` `doctorId?` `firstDoctorId?` `tagId?` `pageNum` `pageSize` |
| GET | `/biz/patient/sidebar` | Query：`type`=`today`\|`all`\|`recent`（默认 all），`clinicId?` `keyword?` `day?`(yyyy-MM-dd) `pageNum` `pageSize`(默认50) |
| GET | `/biz/patient/search` | Query：`keyword`**必填** `clinicId?` `limit`(默认20) |
| GET | `/biz/patient/{id}` | 详情 |
| GET | `/biz/patient/{id}/profile` | 画像：`{ patient, cards, logs }` |
| GET | `/biz/patient/{id}/timeline` | Query：`clinicId?` `beginTime?` `endTime?` |
| POST | `/biz/patient` | Body：`BizPatient` |
| PUT | `/biz/patient` | Body：`BizPatient` |
| DELETE | `/biz/patient/{id}` | — |
| POST | `/biz/patient/saveWithAction` | Query：`action`=`save`\|`arrive`\|`appoint`；Body：`BizPatient`；返回 `{ patientId, action }` |

**BizPatient 主要字段**

| 字段 | 说明 |
|------|------|
| clinicId | 诊所 |
| medicalRecordNo | 病历号（空则后端按诊所生成） |
| name / namePinyin | 姓名 / 拼音 |
| gender | 0女 1男 2未知 |
| starLevel | 星级 |
| birthday / age | 生日 / 年龄 |
| mobile / mobileRelation | 手机 / 关系 |
| phone / phoneRelation | 电话 / 关系 |
| idNumber | 证件号 |
| medicareCardNo / medicareBalance | 医保 |
| province / city / district / address / residence | 地址 |
| avatar | 头像 |
| patientType / patientCategory | 类型/分类 |
| sourceId | 来源 |
| introducerType / introducerId / introducerName | 介绍人 |
| doctorId / firstDoctorId / lastDoctorId | 医生 |
| firstVisitTime / nextVisitTime / lastVisitTime | 就诊时间 |
| oweAmount / paidAmount / prepayAmount / totalAmount / avgAmount | 金额 |
| referralCount | 转介数 |
| status | 状态 |
| tagIds | 标签ID列表（保存用，非表字段） |

**profile.cards 示例字段**：`referralCount` `prepayAmount` `lastVisitTime` `totalAmount` `avgAmount` `oweAmount` `paidAmount`

### 7.2 患者详情 Tab（同前缀 `/biz/patient`）

#### 亲友关系

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/patient/{patientId}/relations` | — |
| POST | `/biz/patient/{patientId}/relations` | Body：需 `relatedId` `relationType`；`clinicId` 可省略 |
| DELETE | `/biz/patient/relations/{id}` | — |

#### 操作日志

| 方法 | 路径 |
|------|------|
| GET | `/biz/patient/{patientId}/logs` |

#### 就诊

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/visits` | — |
| POST | `/biz/patient/{patientId}/visits` | `BizVisit` |
| PUT | `/biz/patient/visits` | `BizVisit` |

**BizVisit**：`appointmentId` `doctorId` `nurseId` `consultantId` `visitType` `visitStatus` `itemName` `startTime` `endTime`

#### 病历

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/patient/{patientId}/medicalRecords` | Query：`visitType?` `beginTime?` `endTime?` |
| POST | `/biz/patient/{patientId}/medicalRecords` | Body：`BizMedicalRecord` |
| PUT | `/biz/patient/medicalRecords` | Body：`BizMedicalRecord` |
| DELETE | `/biz/patient/medicalRecords/{id}` | — |

**BizMedicalRecord**：`visitId` `doctorId` `visitType` `visitTime` `chiefComplaint` `treatment` `advice`

#### 处置

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/treatments` | — |
| POST | `/biz/patient/{patientId}/treatments` | `BizTreatmentRecord` |
| PUT | `/biz/patient/treatments` | `BizTreatmentRecord` |
| DELETE | `/biz/patient/treatments/{id}` | — |

**BizTreatmentRecord**：`visitId` `doctorId` `nurseId` `itemId` `itemName` `toothPositions` `visitType` `amount` `treatTime`

#### 收费

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/charges` | — |
| POST | `/biz/patient/{patientId}/charges` | `BizChargeRecord`（可自动生成单号/金额状态） |
| PUT | `/biz/patient/charges` | `BizChargeRecord` |

**BizChargeRecord**：`visitId` `chargeNo` `totalAmount` `paidAmount` `oweAmount` `payMethod` `chargeStatus` `chargeTime` `cashierId`

#### 回访

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/followUps` | — |
| POST | `/biz/patient/{patientId}/followUps` | `BizFollowUp` |
| PUT | `/biz/patient/followUps` | `BizFollowUp` |
| DELETE | `/biz/patient/followUps/{id}` | — |

**BizFollowUp**：`visitId` `planTime` `actualTime` `followType` `followStatus` `content` `result` `ownerId`

#### 附件（影像/文档/协议）

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/patient/{patientId}/files` | Query：`fileCategory?` `fileType?` `beginTime?` `endTime?` |
| POST | `/biz/patient/{patientId}/files` | Body：**必填** `fileUrl`；`fileCategory` 默认 `document` |
| DELETE | `/biz/patient/files/{id}` | — |

**fileCategory**：`image` \| `document` \| `agreement`  
**BizPatientFile**：`visitId` `fileCategory` `fileType` `fileName` `fileUrl` `fileSize` `uploadTime`

#### 治疗计划

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/plans` | — |
| POST | `/biz/patient/{patientId}/plans` | `BizTreatPlan` |
| PUT | `/biz/patient/plans` | `BizTreatPlan` |
| DELETE | `/biz/patient/plans/{id}` | — |

**BizTreatPlan**：`doctorId` `planName` `planContent` `estimateAmount` `planStatus`

#### 咨询沟通

| 方法 | 路径 | Body |
|------|------|------|
| GET | `/biz/patient/{patientId}/consults` | — |
| POST | `/biz/patient/{patientId}/consults` | `BizConsultRecord` |
| PUT | `/biz/patient/consults` | `BizConsultRecord` |

**BizConsultRecord**：`consultantId` `consultTime` `content` `intention`

---

## 8. 预约 `/biz/appointment`

### 8.1 状态字典

| 值 | 含义 |
|----|------|
| 1 | 已预约 |
| 2 | 已确认 |
| 3 | 已到达 |
| 4 | 治疗中 |
| 5 | 已离开 |
| 6 | 已过期 |
| 7 | 已流失（取消） |
| 8 | 预约未到 |

`visitType`：`1` 初诊 / `2` 复诊

### 8.2 接口列表

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/appointment/list` | Query：`keyword?` `clinicId?` `doctorId?` `consultantId?` `visitType?` `status?` `appointSource?` `beginTime?` `endTime?` `pageNum` `pageSize` |
| GET | `/biz/appointment/calendar` | Query：`clinicId?` **`beginTime`** **`endTime`** `doctorId?` `status?`(可逗号多状态) |
| GET | `/biz/appointment/dayGrid` | Query：`clinicId?` **`day`**(yyyy-MM-dd) `status?` → `{ day, columns[{doctorId,doctorName,count,appointments}], total }` |
| GET | `/biz/appointment/stats/statusCount` | Query：`clinicId?` `beginTime?` `endTime?` `doctorId?` → `{ all, booked, confirmed, arrived, treating, left, expired, lost, missed, byStatus }` |
| GET | `/biz/appointment/stats/today` | Query：`clinicId?` → 今日卡片统计 |
| GET | `/biz/appointment/{id}` | 详情 |
| POST | `/biz/appointment` | Body：`BizAppointment` |
| PUT | `/biz/appointment` | Body：`BizAppointment` |
| DELETE | `/biz/appointment/{id}` | Query：`cancelReason?`（逻辑删进回收站） |
| PUT | `/biz/appointment/status` | Body：`{ "id": 1, "status": 3, "remark": "" }` |
| PUT | `/biz/appointment/confirm/{id}` | 确认 |
| PUT | `/biz/appointment/cancel` | Body：`{ "id": 1, "cancelReason": "..." }` |
| PUT | `/biz/appointment/seat/{id}` | 接诊入位 |
| GET | `/biz/appointment/{id}/logs` | 操作日志 |
| GET | `/biz/appointment/recycle/list` | 回收站分页（同 list 筛选字段） |
| PUT | `/biz/appointment/recycle/restore/{id}` | 还原 |
| DELETE | `/biz/appointment/recycle/{id}` | 彻底删除 |

**BizAppointment 主要字段**

| 字段 | 说明 |
|------|------|
| clinicId / patientId | 诊所 / 患者 |
| doctorId / nurseId / consultantId | 医生 / 护士 / 咨询师 |
| startTime / endTime | 开始/结束 |
| visitType / status | 初复诊 / 状态 |
| itemId / itemName / itemColor | 项目与日历色 |
| triaged / registered | 分诊/挂号标记 |
| appointType / appointSource | 类型/来源 |
| cancelReason | 取消原因 |

新增/修改会做医生时间冲突校验。

---

## 9. 日程 `/biz/schedule`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/schedule/list` | Query：`clinicId?` `doctorId?` **`beginTime`** **`endTime`** |
| POST | `/biz/schedule` | Body：必填 `title` `startTime` `endTime` |
| PUT | `/biz/schedule` | Body：`BizSchedule` |
| DELETE | `/biz/schedule/{id}` | — |

**BizSchedule**：`clinicId` `doctorId` `title` `startTime` `endTime` `color` `status`

---

## 10. 前端请求示例

```http
### 登录
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}

### 带 Token 查患者列表
GET http://localhost:8081/biz/patient/list?pageNum=1&pageSize=20&keyword=
Authorization: Bearer {{token}}

### 日视图
GET http://localhost:8081/biz/appointment/dayGrid?day=2026-08-14
Authorization: Bearer {{token}}

### 新增预约
POST http://localhost:8081/biz/appointment
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "clinicId": 1,
  "patientId": 1,
  "doctorId": 1,
  "startTime": "2026-08-14 09:00:00",
  "endTime": "2026-08-14 09:30:00",
  "visitType": 1,
  "status": 1,
  "itemId": 1,
  "itemName": "洗牙"
}
```

Axios 建议：

```js
axios.defaults.baseURL = 'http://localhost:8081' // 或 'http://localhost:8080/admin'
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
```

---

## 11. 接口路径速查（全量）

```text
# 鉴权
POST   /auth/login
GET    /auth/info
GET    /auth/info/{employeeId}
POST   /auth/logout

# 系统
GET|POST|PUT|DELETE  /system/clinic...
GET|POST|PUT|DELETE  /system/dept...
GET|POST|PUT|DELETE  /system/employee...  (+ resetPwd / sort)
GET|POST|PUT|DELETE  /system/role...      (+ auth / menus / move)
GET|POST|PUT|DELETE  /system/menu...      (+ tree / employee menus|perms)

# 基础数据
GET|POST|DELETE      /biz/basic/dict...
GET|POST|DELETE      /biz/basic/tag...
GET|POST|DELETE      /biz/basic/source...
GET|POST|DELETE      /biz/basic/item...
GET                  /biz/basic/doctor/list

# 患者
GET|POST|PUT|DELETE  /biz/patient...
GET|POST|DELETE      /biz/patient/{id}/relations|logs|visits|medicalRecords|treatments|charges|followUps|files|plans|consults ...

# 预约 / 日程
GET|POST|PUT|DELETE  /biz/appointment...
GET|POST|PUT|DELETE  /biz/schedule...
```

---

## 12. 相关文档

| 文件 | 说明 |
|------|------|
| `document/sql/00_full_schema.sql` | 表结构 |
| `document/sql/01_init_data.sql` | 初始化数据 |
| `document/design/table-naming.md` | 表命名规范 |
| Knife4j | http://localhost:8081/doc.html |
