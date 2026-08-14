# yujian-nacos（注册中心 + 配置中心）

Nacos 服务端建议**独立部署**，不内嵌到 Spring Boot 进程中。  
本模块存放接入说明与可导入 Nacos 的示例配置。

## 1. 版本对应

| 组件 | 版本 |
|------|------|
| JDK | 8 |
| Spring Boot | 2.3.12.RELEASE |
| Spring Cloud | Hoxton.SR12 |
| Spring Cloud Alibaba | 2.2.9.RELEASE |
| Nacos Server | 2.0.4 / 2.1.x / 2.2.x（推荐 2.0.4+） |

## 2. 快速启动 Nacos（单机）

1. 下载：[Nacos Releases](https://github.com/alibaba/nacos/releases)
2. 解压后进入 `bin` 目录：

```bash
# Windows
startup.cmd -m standalone

# Linux / Mac
sh startup.sh -m standalone
```

3. 控制台：http://127.0.0.1:8848/nacos  
   默认账号：`nacos` / `nacos`

## 3. 命名空间

建议创建命名空间：

| Namespace | 说明 |
|-----------|------|
| `yujian` / 或自定义 ID | 宇健口腔业务环境 |

各服务 `bootstrap.yml` 中：

```yaml
spring.cloud.nacos.discovery.namespace: yujian
spring.cloud.nacos.config.namespace: yujian
```

> 若使用命名空间 ID（UUID），请填控制台显示的 Namespace ID，不要只填显示名。

## 4. 配置中心 Data ID 约定

| Data ID | Group | 说明 |
|---------|-------|------|
| `yujian-common.yml` | `DEFAULT_GROUP` | 公共配置（可选，shared-configs） |
| `yujian-admin.yml` | `DEFAULT_GROUP` | admin 服务专属 |
| `yujian-api.yml` | `DEFAULT_GROUP` | api 服务专属 |
| `yujian-gateway.yml` | `DEFAULT_GROUP` | gateway 专属 |
| `yujian-listener.yml` | `DEFAULT_GROUP` | listener 专属 |
| `yujian-task.yml` | `DEFAULT_GROUP` | task 专属 |
| `yujian-xxl.yml` | `DEFAULT_GROUP` | xxl 专属 |

示例内容见本目录 `config-example/`。

在 Nacos 控制台：配置管理 → 配置列表 → 创建配置 → 粘贴示例 YAML → 发布。

## 5. 业务服务接入要点

依赖（已在各服务 pom 中）：

```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
</dependency>
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
</dependency>
```

启动类使用 `@EnableDiscoveryClient`（或 Spring Cloud 自动装配即可）。

本地仍可保留 `application.yml` 作为兜底；生产优先以 Nacos 配置为准。

## 6. 启动顺序

```text
1. Nacos Server（8848）
2. Redis / MySQL
3. yujian-gateway
4. yujian-admin / yujian-api / ...
```
