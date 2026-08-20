# 宇健口腔 · 前端联调接口说明

> 模块：`yujian-admin`（Web 管理端）  
> 用途：地址、鉴权、传参、响应结构一览。  
> 在线文档（启动 admin 后）：http://localhost:8081/doc.html  

---

## 1. 环境与地址

| 方式 | Base URL | 说明 |
|------|----------|------|
| 直连 Admin | `http://localhost:8081` | 本地开发推荐 |
| 经 Gateway | `http://localhost:8080/admin` | 网关 `StripPrefix=1`，业务路径与直连一致 |

```text
直连登录：  POST http://localhost:8081/auth/login
网关登录：  POST http://localhost:8080/admin/auth/login
```

默认账号：`admin` / `123456`

---

## 2. 通用约定

### 2.1 只允许 GET / POST

系统接口 **仅使用 GET、POST**，不再使用 PUT / DELETE / PATCH。

| 操作 | 约定 |
|------|------|
| 查询 | `GET` |
| 新增 | `POST /资源` |
| 修改 | `POST /资源/edit` |
| 删除 | `POST /资源/remove/{id}` |
| 其它动作 | `POST /资源/动作` |

### 2.2 鉴权（Sa-Token）

| 项 | 说明 |
|----|------|
| Header | `Authorization: Bearer {token}` |
| 白名单 | `POST /auth/login`、Swagger、Actuator |
| 其余接口 | 均需有效 Token |
| Token 有效期 | 7200 秒（2 小时） |

### 2.3 诊所隔离（一对多）

一个员工可关联多个诊所（`t_employee_clinic`）。

登录后：

1. 返回 `clinics`（可进入诊所列表）和 `needSelectClinic`
2. 仅 1 个诊所：自动选中，`needSelectClinic=false`
3. 多个诊所：必须调用 `POST /auth/selectClinic` 后再进业务页
4. **写操作**（新增患者/预约等）：一律写入**会话当前诊所**，请求里的 `clinicId` 忽略
5. **查询类**（医生列表 / 预约天视图 / 状态计数）：请求 `clinicId` 在账号授权诊所内**生效**；空则回退会话诊所；无权返回 `code=403`

未选诊所访问业务接口：`code=400`，`msg=请先选择诊所后再操作`

切换诊所：再次调用 `POST /auth/selectClinic`

### 2.4 统一响应 `R<T>`

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "success": true
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 / 未选诊所 / JSON 格式错误 |
| 401 | 未登录 / Token 失效 |
| 403 | 无权限 |
| 500 | 业务或系统失败（见 `msg`） |

### 2.5 分页 `PageResult<T>`

```json
{
  "total": 100,
  "pageNum": 1,
  "pageSize": 20,
  "records": []
}
```

Query：`pageNum`（默认 1）、`pageSize`（默认 20）

### 2.6 时间与字段

- 日期时间：`yyyy-MM-dd HH:mm:ss`，时区 `GMT+8`
- 纯日期：`yyyy-MM-dd`
- JSON 字段：驼峰 camelCase
- Header：`Content-Type: application/json`；Body 必须是原始 JSON，不要写成 `{ \"username\": \"admin\" }`

---

## 3. 推荐联调顺序

```text
1. POST /auth/login
2. 若 needSelectClinic=true → POST /auth/selectClinic
3. GET  /auth/info
4. GET  /biz/patient/list
5. GET  /biz/appointment/dayGrid?day=yyyy-MM-dd
6. GET  /system/employee/list
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
  "token": "uuid-token-string",
  "user": { "id": 1, "name": "管理员", "username": "admin", "clinicIds": [1] },
  "clinics": [{ "id": 1, "clinicName": "宇健口腔", "clinicCode": "YJ01" }],
  "needSelectClinic": false,
  "currentClinicId": 1
}
```

| 字段 | 说明 |
|------|------|
| token | 后续请求 Header：`Authorization: Bearer {token}` |
| clinics | 该员工可进入的诊所 |
| needSelectClinic | `true` 时必须先选诊所再进业务页 |
| currentClinicId | 已自动选中时有值；多诊所未选则为 null |

### 4.2 选择诊所

`POST /auth/selectClinic`（需 Token）

```json
{ "clinicId": 1 }
```

**data**：`{ "clinicId": 1, "clinicName": "宇健口腔" }`

无权进入该诊所：`无权进入该诊所`

### 4.3 可进入诊所列表

`GET /auth/clinics`

### 4.4 当前用户信息

`GET /auth/info`

**data**：`{ user, roles, menus, permissions, clinics, currentClinicId, currentClinicName }`

### 4.5 退出

`POST /auth/logout`

---

## 5. 系统管理

### 5.1 诊所 `/system/clinic`

| 方法 | 路径 | 说明 | 传参 |
|------|------|------|------|
| GET | `/system/clinic/list` | 列表 | Query：`clinicName?` `clinicCode?` `status?` `parentId?` |
| GET | `/system/clinic/tree` | 树数据 | 同上 |
| GET | `/system/clinic/{id}` | 详情 | Path：`id` |
| POST | `/system/clinic` | 新增 | Body：`SysClinic` |
| POST | `/system/clinic/edit` | 修改 | Body：含 `id` |
| POST | `/system/clinic/remove/{id}` | 删除 | Path：`id` |

**SysClinic**：`parentId` `clinicName` `clinicCode` `shortName` `contactName` `contactPhone` `province` `city` `district` `address` `businessHours` `logo` `sortOrder` `status`(0正常1停用) `openDate`

### 5.2 部门 `/system/dept`（当前诊所）

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/dept/list` | Query：`deptName?` `status?` |
| GET | `/system/dept/{id}` | Path：`id` |
| POST | `/system/dept` | Body：`SysDept` |
| POST | `/system/dept/edit` | Body：`SysDept` |
| POST | `/system/dept/remove/{id}` | Path：`id` |

**SysDept**：`clinicId` `parentId` `deptName` `deptCode` `leader` `phone` `sortOrder` `status`

### 5.3 员工 `/system/employee`（当前诊所）

列表只返回 **关联了当前诊所** 的员工。一个员工可关联多个诊所（`clinicIds`）。

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/employee/list` | Query：`keyword?` `employStatus?` `pageNum` `pageSize` |
| GET | `/system/employee/{id}` | Path：`id` |
| POST | `/system/employee` | Body：`SysEmployee`（可含 `roleIds`、`clinicIds`） |
| POST | `/system/employee/edit` | Body：见更新约定 |
| POST | `/system/employee/status` | `{ "id": 1, "status": 0\|1 }` |
| POST | `/system/employee/remove/{id}` | Path：`id` |
| POST | `/system/employee/resetPwd` | `{ "id": 1, "password": "新密码" }` |
| POST | `/system/employee/sort/{id}/{direction}` | `direction` = `up` \| `down` |

**SysEmployee 主要字段**

| 字段 | 说明 |
|------|------|
| name / empNo / username / password | 姓名、工号、账号、密码 |
| gender | 0女 1男 2未知 |
| birthday | yyyy-MM-dd |
| mobile / email | 手机 / 邮箱 |
| clinicId | 主诊所冗余字段，以前端 `clinicIds` 为准 |
| clinicIds | 关联诊所 ID 列表（一对多） |
| clinicNames | 关联诊所名称（列表返回） |
| position | 岗位（自由文本） |
| employStatus | 1在职 0离职 |
| sortOrder / status | 排序 / 0正常1停用 |
| roleIds / roleNames | 角色 ID / 名称 |

**修改约定**

| 场景 | 怎么做 |
|------|--------|
| 改基础资料 | `POST /system/employee/edit`，`id` 必填 |
| 角色 | `roleIds == null` 不改；非 null（含 `[]`）全量同步 |
| 诊所 | `clinicIds == null` 不改；非 null 全量同步，**不能传空数组** |
| 密码 | 只用 `POST /system/employee/resetPwd` |
| 仅启停 | `POST /system/employee/status` |

新增未传 `clinicIds` 时，默认绑定当前所选诊所。

### 5.4 角色 `/system/role`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/role/list` | Query：`roleName?` `roleKey?` `status?` |
| GET | `/system/role/{id}` | Path：`id` |
| POST | `/system/role` | Body：可含 `menuIds` |
| POST | `/system/role/edit` | Body：`SysRole` |
| POST | `/system/role/remove/{id}` | Path：`id` |
| POST | `/system/role/auth` | `{ "roleId": 1, "menuIds": [1,2,3] }` |
| GET | `/system/role/{id}/menus` | 已选菜单 ID |
| POST | `/system/role/move/{id}/{direction}` | `up` \| `down` |

**SysRole**：`roleName` `roleKey` `sortOrder` `dataScope`(1全部/2本诊所/3本部门/4仅本人/5自定义) `status` `menuIds`

### 5.5 菜单 `/system/menu`

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/system/menu/list` | Query：`menuName?` `platform?` `status?` `menuType?` |
| GET | `/system/menu/tree` | Query：`platform` 默认 `web` |
| GET | `/system/menu/{id}` | Path：`id` |
| POST | `/system/menu` | Body：`SysMenu` |
| POST | `/system/menu/edit` | Body：`SysMenu` |
| POST | `/system/menu/remove/{id}` | Path：`id` |
| GET | `/system/menu/employee/{employeeId}` | Query：`platform` 默认 `web` |
| GET | `/system/menu/employee/{employeeId}/perms` | 权限标识列表 |

**SysMenu**：`menuName` `parentId` `sortOrder` `path` `component` `perms` `menuType`(M目录/C菜单/F按钮) `platform`(web/mobile) `icon` `visible`(0显示1隐藏) `status`

---

## 6. 基础数据 `/biz/basic`（当前诊所）

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/basic/dict/types` | — |
| GET | `/biz/basic/dict/{dictType}` | 如 `appoint_status` |
| POST | `/biz/basic/dict/data` | Body：`BizDictData` |
| POST | `/biz/basic/dict/data/remove/{id}` | Path：`id` |
| GET | `/biz/basic/tag/list` | — |
| POST | `/biz/basic/tag` | Body：`BizPatientTag` |
| POST | `/biz/basic/tag/remove/{id}` | — |
| GET | `/biz/basic/source/tree` | — |
| POST | `/biz/basic/source` | Body：`BizPatientSource` |
| POST | `/biz/basic/source/remove/{id}` | — |
| GET | `/biz/basic/item/list` | Query：`clinicId?`（授权范围内生效）`keyword?`（名称/编码） |
| POST | `/biz/basic/item` | Body：`BizTreatItem` |
| POST | `/biz/basic/item/remove/{id}` | — |
| GET | `/biz/basic/doctor/list` | Query：`clinicId?`（授权范围内生效）`keyword?`（姓名/手机） |
| GET | `/biz/basic/consultant/list` | Query：`clinicId?`（授权范围内生效）`keyword?`（姓名/手机） |

**医生 / 咨询师列表说明**

- `clinicId` 空 = 会话当前诊所；有值且在账号 `clinics` 内 → 按该诊所列；无权 → `403`
- 返回字段：`id` `name` `empNo` `position` `clinicId` `mobile`
- 医生：在职、启用、职位含「医生/医师」
- 咨询师：在职、启用、职位含「咨询」

**诊疗项目列表说明（新增预约右栏）**

- `clinicId` 规则同医生列表；仅返回该诊所启用项目
- 返回含 `duration`（分钟，空则后端补 30）、`itemName` `itemCode` `itemColor` `sortOrder`

**常用 dictType**：`appoint_status` `visit_type` `cancel_reason` `gender`

---

## 7. 患者 `/biz/patient`（当前诊所）

### 7.1 主接口

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/patient/list` | Query：`keyword?` `doctorId?` `firstDoctorId?` `tagId?` `pageNum` `pageSize` |
| GET | `/biz/patient/sidebar` | `type`=`today`\|`all`\|`recent`，`keyword?` `day?` `pageNum` `pageSize` |
| GET | `/biz/patient/search` | `keyword` 必填，`limit` 默认 20 |
| GET | `/biz/patient/{id}` | 详情（须属当前诊所） |
| GET | `/biz/patient/{id}/profile` | `{ patient, cards, logs }` |
| GET | `/biz/patient/{id}/timeline` | `beginTime?` `endTime?` |
| POST | `/biz/patient` | Body：`BizPatient`（clinicId 由后端写入） |
| POST | `/biz/patient/edit` | Body：`BizPatient` |
| POST | `/biz/patient/remove/{id}` | — |
| POST | `/biz/patient/saveWithAction` | Query：`action`=`save`\|`arrive`\|`appoint` |

**BizPatient 主要字段**：`name` `namePinyin` `gender` `mobile` `medicalRecordNo` `doctorId` `firstDoctorId` `tagIds` 等。新增不传 `clinicId`，后端用当前诊所。

### 7.2 详情 Tab

| 模块 | 查询 | 新增 | 修改 | 删除 |
|------|------|------|------|------|
| 亲友 | GET `/{patientId}/relations` | POST 同路径 | — | POST `/relations/remove/{id}` |
| 日志 | GET `/{patientId}/logs` | — | — | — |
| 就诊 | GET `/{patientId}/visits` | POST 同路径 | POST `/visits/edit` | — |
| 病历 | GET `/{patientId}/medicalRecords` | POST 同路径 | POST `/medicalRecords/edit` | POST `/medicalRecords/remove/{id}` |
| 处置 | GET `/{patientId}/treatments` | POST 同路径 | POST `/treatments/edit` | POST `/treatments/remove/{id}` |
| 收费 | GET `/{patientId}/charges` | POST 同路径 | POST `/charges/edit` | — |
| 回访 | GET `/{patientId}/followUps` | POST 同路径 | POST `/followUps/edit` | POST `/followUps/remove/{id}` |
| 附件 | GET `/{patientId}/files` | POST 同路径，必填 `fileUrl` | — | POST `/files/remove/{id}` |
| 计划 | GET `/{patientId}/plans` | POST 同路径 | POST `/plans/edit` | POST `/plans/remove/{id}` |
| 咨询 | GET `/{patientId}/consults` | POST 同路径 | POST `/consults/edit` | — |

---

## 8. 预约 `/biz/appointment`

> `POST` 新建的 `clinicId`、以及 `dayGrid` / `statusCount` 的 `clinicId` 均在授权范围内生效；列表等其它写读默认仍按会话诊所。

### 8.1 状态

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

`visitType`：`1` 初诊 / `2` 复诊 / `3` 新诊

`appointType`（字典 `appoint_type`）：

| 值 | 含义 | 说明 |
|----|------|------|
| `normal` | 普通预约 | 默认 |
| `walkin` | 散客/到店 | |
| `online` | 网络预约 | |
| **`pending`** | **待确定** | 左侧「待确定预约」筛选用此字段，**不是** status |

`appointSource`（字典 `appoint_source`）：`clinic` 院内 / `online` 网络 / `wechat` 微信

### 8.2 接口

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/appointment/list` | 见 8.5 |
| GET | `/biz/appointment/calendar` | **`beginTime`** **`endTime`** `doctorId?` `status?`(逗号多状态) |
| GET | `/biz/appointment/dayGrid` | **`day`** `clinicId?` `status?` `doctorIds?` |
| GET | `/biz/appointment/stats/statusCount` | `clinicId?` `beginTime?` `endTime?` `doctorId?` |
| GET | `/biz/appointment/stats/today` | 今日卡片（会话诊所） |
| GET | `/biz/appointment/{id}` | 详情（含 `items` / `itemIds`） |
| POST | `/biz/appointment` | Body：见 8.4（`clinicId` 授权生效） |
| POST | `/biz/appointment/edit` | Body：`BizAppointment`（可传 `itemIds` 覆盖项目） |
| POST | `/biz/appointment/remove/{id}` | Query：`cancelReason?`（进回收站） |
| POST | `/biz/appointment/status` | `{ "id": 1, "status": 3, "remark": "" }` |
| POST | `/biz/appointment/confirm/{id}` | 确认 |
| POST | `/biz/appointment/cancel` | `{ "id": 1, "cancelReason": "..." }` |
| POST | `/biz/appointment/seat/{id}` | 入座 |
| GET | `/biz/appointment/{id}/logs` | 操作日志 |
| GET | `/biz/appointment/recycle/list` | `clinicId?` 授权生效 + keyword/doctorId/consultantId/beginTime/endTime/page |
| POST | `/biz/appointment/recycle/restore/{id}` | 还原 |
| POST | `/biz/appointment/recycle/remove/{id}` | 彻底删除 |
| POST | `/biz/appointment/recycle/clear` | 清空回收站：`clinicId?` `beginTime?` `endTime?`（按预约开始时间，可选） |

### 8.3 天视图 `dayGrid`（预约日历 / 医生查询）

**Query**

| 参数 | 必填 | 说明 |
|------|------|------|
| day | 是 | `yyyy-MM-dd` |
| clinicId | 否 | 授权诊所；空=会话诊所；无权 `403` |
| status | 否 | 状态逗号分隔，如 `1,2,3` |
| doctorIds | 否 | 医生 ID 逗号分隔，如 `0,12,15`；`0`=未指定医生；空=全部列 |

**data**

```json
{
  "day": "2026-08-20T00:00:00.000+0800",
  "total": 12,
  "columns": [
    {
      "doctorId": 0,
      "doctorName": "未指定医生",
      "count": 0,
      "appointments": []
    },
    {
      "doctorId": 12,
      "doctorName": "张翰",
      "count": 5,
      "appointments": [ { "id": 1001, "patientName": "王小明", "startTime": "...", "endTime": "...", "status": 1 } ]
    }
  ]
}
```

- 固定含 `doctorId=0`「未指定医生」列（若 `doctorIds` 未排除 0）
- `columns[].count` = 该列 `appointments` 条数（与 status 筛选一致）；左侧「张翰 [5]」可直接用
- `total` = 返回列内 count 合计

### 8.4 新建预约

- `clinicId` 空 = 会话诊所；有值须在账号授权诊所内，否则 `403`
- 患者必须属于该 `clinicId`；项目（`itemIds`/`itemId`）也须属于该诊所
- `startTime` 的日期不得早于今天
- 多选项目：传 `itemIds`（有序）；首项同步写入主表 `itemId/itemName/itemColor`；明细表 `t_appointment_item`
- 兼容旧入参：仅传 `itemId` 视为单项目
- 详情 / `dayGrid` 回显带 `items: [{itemId,itemName,duration,sortOrder}]` 与 `itemIds`

**Body 示例**

```json
{
  "clinicId": 2,
  "patientId": 2001,
  "doctorId": 12,
  "consultantId": 20,
  "startTime": "2026-08-20 09:00:00",
  "endTime": "2026-08-20 09:45:00",
  "visitType": 1,
  "itemIds": [3, 5],
  "appointType": "normal",
  "remark": ""
}
```

| 字段 | 说明 |
|------|------|
| clinicId | 可选；授权诊所，空=会话 |
| patientId | 必填 |
| doctorId | 可空 / null 表示未指定医生 |
| consultantId | 可选；咨询师 |
| startTime / endTime | 必填，`endTime > startTime`；预约日 ≥ 今天 |
| visitType | 1 初诊 / 2 复诊 / 3 新诊 |
| itemIds | 推荐；多选项目 ID 数组 |
| itemId / itemName | 兼容单项目 |
| remark | 可选 |
| appointType | normal / walkin / online / **pending**（待确定） |

### 8.5 预约列表 `GET /list`

**Query**

| 参数 | 必填 | 说明 |
|------|------|------|
| clinicId | 否 | 授权诊所（门诊筛选）；空=会话；无权 `403` |
| keyword | 否 | 患者姓名/手机/病历号 |
| doctorId | 否 | 预约医生 |
| consultantId | 否 | 咨询师 |
| visitType | 否 | 1/2/3 |
| status | 否 | 状态，逗号多选如 `1,2,6` |
| appointType | 否 | `normal`/`walkin`/`online`/`pending`；左侧「待确定」= `pending` |
| appointSource | 否 | clinic/online/wechat |
| beginTime / endTime | 否 | 按 **预约开始时间** `start_time` |
| createBeginTime / createEndTime | 否 | 按 **创建时间** `create_time` |
| pageNum / pageSize | 否 | 默认 1 / 20 |

**左侧快捷筛选约定**

| 左侧项 | 传参 |
|--------|------|
| 所有预约 | 不传特殊筛选 |
| 待确定预约 | `appointType=pending` |
| 已过期预约 | `status=6` |
| 已流失预约 | `status=7` |

**反参**：在原有字段上补 `clinicName`、`items`/`itemIds`。

### 8.6 回收站清空

`POST /biz/appointment/recycle/clear?clinicId=&beginTime=&endTime=`

- `clinicId` 授权生效；空=会话诊所
- `beginTime`/`endTime` 可选，按预约 `start_time` 过滤后物理删除
- 同步删除明细与操作日志；返回删除条数

---

## 9. 日程 `/biz/schedule`（当前诊所）

| 方法 | 路径 | 传参 |
|------|------|------|
| GET | `/biz/schedule/list` | `doctorId?` **`beginTime`** **`endTime`** |
| POST | `/biz/schedule` | 必填 `title` `startTime` `endTime` |
| POST | `/biz/schedule/edit` | Body：`BizSchedule` |
| POST | `/biz/schedule/remove/{id}` | — |

---

## 10. 请求示例

```http
### 1. 登录
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}

### 2. 多诊所时选择诊所
POST http://localhost:8081/auth/selectClinic
Authorization: Bearer {{token}}
Content-Type: application/json

{ "clinicId": 1 }

### 3. 患者列表（按会话诊所）
GET http://localhost:8081/biz/patient/list?pageNum=1&pageSize=20
Authorization: Bearer {{token}}

### 3b. 医生查询：授权诊所下列医生
GET http://localhost:8081/biz/basic/doctor/list?clinicId=2&keyword=
Authorization: Bearer {{token}}

### 3b2. 咨询师列表 / 诊疗项目（按预约门诊）
GET http://localhost:8081/biz/basic/consultant/list?clinicId=2&keyword=
Authorization: Bearer {{token}}

GET http://localhost:8081/biz/basic/item/list?clinicId=2&keyword=
Authorization: Bearer {{token}}

### 3c. 医生查询：天视图（含人数 columns[].count）
GET http://localhost:8081/biz/appointment/dayGrid?day=2026-08-20&clinicId=2&status=1,2,3
Authorization: Bearer {{token}}

### 4. 员工列表
GET http://localhost:8081/system/employee/list?pageNum=1&pageSize=20
Authorization: Bearer {{token}}

### 5. 修改员工（POST /edit，可带 clinicIds）
POST http://localhost:8081/system/employee/edit
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "id": 3,
  "name": "张三",
  "clinicIds": [1, 2],
  "roleIds": [1]
}

### 6. 删除（POST /remove）
POST http://localhost:8081/system/employee/remove/3
Authorization: Bearer {{token}}
```

Axios：

```js
axios.defaults.baseURL = 'http://localhost:8081' // 或 'http://localhost:8080/admin'
axios.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
```

前端登录后建议：

```text
login → 若 needSelectClinic → 弹出诊所选择 → selectClinic → 进入首页
切换诊所 → selectClinic → 刷新列表
```

---

## 11. 接口路径速查

```text
# 鉴权
POST /auth/login
POST /auth/selectClinic
GET  /auth/clinics
GET  /auth/info
POST /auth/logout

# 系统（仅 GET/POST）
GET|POST  /system/clinic...     (+ /edit /remove/{id})
GET|POST  /system/dept...       (+ /edit /remove/{id})  当前诊所
GET|POST  /system/employee...   (+ /edit /status /resetPwd /sort /remove/{id})  当前诊所
GET|POST  /system/role...       (+ /edit /auth /move /remove/{id})
GET|POST  /system/menu...       (+ /edit /tree /remove/{id})

# 业务（当前诊所）
GET|POST  /biz/basic/...
GET|POST  /biz/patient...       (+ /edit /remove/{id} /saveWithAction + Tab)
GET|POST  /biz/appointment...   (+ /edit /status /confirm /cancel /seat /recycle)
GET|POST  /biz/schedule...      (+ /edit /remove/{id})
```

---

## 12. 相关文档

| 文件 | 说明 |
|------|------|
| `document/sql/00_full_schema.sql` | 完整表结构（含 `t_employee_clinic`、`t_appointment_item`） |
| `document/sql/01_init_data.sql` | 初始化数据 |
| `document/sql/04_appointment_create_gap.sql` | 已有库增量：预约多项目 + 新诊字典 |
| `document/sql/05_appointment_list_gap.sql` | 已有库增量：appoint_type 待确定 pending |
| `document/api/appointment-create-api-gap.md` | 新增预约弹窗缺口说明（已落地） |
| `document/api/appointment-list-api-gap.md` | 预约列表/回收站缺口说明（已落地） |
| `document/sql/02_employee_clinic.sql` | 已有库增量：建关联表并迁移原 `clinic_id` |
| `document/sql/03_drop_employee_dept_mobile.sql` | 已有库增量：去掉员工 `dept_id`、`mobile_link` |
| Knife4j | http://localhost:8081/doc.html |
