# elder-care-system

## 项目简介

`elder-care-system` 是一个面向颐养中心后台管理场景的前后端分离项目，主要用于管理床位、客户入住、护理服务、健康管家、膳食安排、系统用户和 AI 对话等业务。

当前项目的工程规范重点包括：

| 方向 | 说明 |
| --- | --- |
| 目录结构 | 后端按 controller、service、mapper、entity、dto、vo 分层，前端按 api、views、router、stores、utils 组织 |
| 数据对象 | 请求对象放入 dto，响应对象放入 vo，数据库实体放入 entity |
| 接口风格 | 后端统一 `/api` 前缀，统一返回 `ApiResult<T>` 和分页 `PageResult<T>` |
| 数据脚本 | SQL 拆分为建表脚本和演示数据脚本，脚本保留中文注释 |
| 协作规范 | 统一命名、注释、包结构和启动说明，方便后续多人维护 |

## 功能模块

| 模块 | 当前页面或能力 |
| --- | --- |
| 首页 | 管理员首页、健康管家首页 |
| 床位管理 | 床位示意图、床位列表、床位调整 |
| 客户管理 | 入住登记、外出申请、外出审批、退住申请、退住审批 |
| 护理管理 | 护理级别、护理项目、护理项目配置、客户护理项目、护理记录 |
| 健康管家 | 服务对象、服务关注、日常护理、购买护理项目、护理记录 |
| 膳食管理 | 菜品管理、套餐配置、膳食日历、膳食分配、膳食状态 |
| 系统管理 | 用户列表、用户管理、角色统计 |
| 公共能力 | 登录认证、Spring Security/JWT、登录算术验证码、邮箱验证码、头像上传、请求访问日志、操作审计、AI 对话 |

## 技术栈

| 层级 | 技术 |
| --- | --- |
| 后端 | Spring Boot 3.2.4、Spring Security、MyBatis-Plus、MySQL、Redis、JWT、Spring Mail、Spring AI |
| 前端 | Vue 3、Vite、TypeScript、Element Plus、Pinia、Vue Router、Axios、ECharts |
| 数据库 | MySQL |
| 构建工具 | Maven、npm |

## 项目结构

```text
elder-care-system/
|-- backend/                  后端 Spring Boot 项目
|   |-- pom.xml
|   `-- src/main/
|       |-- java/com/eldercare/system/
|       |   |-- config/        配置类
|       |   |-- controller/    REST 接口
|       |   |-- dto/           请求 DTO
|       |   |-- entity/        数据库实体
|       |   |-- exception/     异常处理
|       |   |-- interceptor/   请求拦截器
|       |   |-- mapper/        MyBatis-Plus Mapper
|       |   |-- service/       业务接口
|       |   |   `-- impl/      业务实现
|       |   |-- util/          工具类
|       |   `-- vo/            响应 VO
|       `-- resources/
|           |-- application.yml
|           `-- mapper/        MyBatis XML
|-- frontend/                 前端 Vue 3 项目
|   |-- package.json
|   `-- src/
|       |-- api/              接口请求封装
|       |-- assets/           静态资源
|       |-- components/       通用组件
|       |-- router/           路由配置
|       |-- stores/           Pinia 状态管理
|       |-- utils/            前端工具
|       `-- views/            页面视图
|-- docs/                     项目过程文档
|   |-- plans/                设计与改造计划
|   |-- reviews/              审核与修复记录
|   `-- progress/             阶段进度记录
|-- sql/
|   |-- schema.sql            建表脚本
|   `-- demo-data.sql         演示数据脚本
`-- README.md
```

## 项目文档

| 文档 | 说明 |
| --- | --- |
| [角色权限改造计划](docs/plans/role-permission-plan.md) | 记录角色权限改造的设计方案和阶段计划 |
| [登录验证码设计计划](docs/plans/login-captcha-plan.md) | 记录登录页算术验证码、Redis 校验和配置开关设计 |
| [请求与操作日志计划](docs/plans/request-operation-log-plan.md) | 记录请求访问日志和操作审计日志设计 |
| [角色权限审核记录](docs/reviews/role-permission-review.md) | 记录角色权限改造过程中的问题、审核结论和修复状态 |
| [开发进度记录](docs/progress/PROGRESS.md) | 记录项目阶段性进度和当前待办 |

## 环境要求

| 环境 | 建议版本 | 说明 |
| --- | --- | --- |
| JDK | 17+ | Spring Boot 3 需要 JDK 17 或更高版本 |
| Maven | 3.8+ | 后端依赖管理和构建 |
| Node.js | 18+ | 前端运行环境 |
| npm | 9+ | 前端依赖管理 |
| MySQL | 8.0+ | 业务数据库 |
| Redis | 6.0+ | 登录令牌和缓存 |

## 数据库初始化

当前 `sql` 目录只有两个脚本：

| 脚本 | 作用 | 是否必须 |
| --- | --- | --- |
| `sql/schema.sql` | 创建业务表结构 | 必须 |
| `sql/demo-data.sql` | 插入演示数据和可登录账号 | 本地演示需要 |

创建数据库：

```sql
CREATE DATABASE elder_care_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

执行顺序：

```text
1. sql/schema.sql
2. sql/demo-data.sql
```

说明：

- 当前项目没有 `sql/data.sql`。
- `schema.sql` 负责表结构，不放演示业务数据。
- `demo-data.sql` 负责演示数据，当前包含角色、用户、床位、客户、护理、膳食等初始化数据。
- 当前 `demo-data.sql` 包含管理员、护工、医生、护士等演示角色。后续如果角色统一收敛为“管理员、健康管家”，需要同步调整 SQL、后端映射和 README。

## 后端启动

进入后端目录：

```bash
cd elder-care-system/backend
```

编译项目：

```bash
mvn clean compile
```

启动项目：

```bash
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/api
```

主要配置文件：

```text
backend/src/main/resources/application.yml
```

当前默认配置：

| 配置项 | 当前值 | 说明 |
| --- | --- | --- |
| `server.port` | `8080` | 后端端口 |
| `server.servlet.context-path` | `/api` | 后端接口统一前缀 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/elder_care_system` | MySQL 数据库 |
| `spring.datasource.username` | `your-mysql-username` | MySQL 用户名，本地真值建议放入 `application-local.yml` |
| `spring.datasource.password` | `your-mysql-password` | MySQL 密码，本地真值建议放入 `application-local.yml` |
| `spring.data.redis.host` | `127.0.0.1` | Redis 地址 |
| `spring.data.redis.port` | `6379` | Redis 端口 |
| `elder-care.captcha.enabled` | `true` | 登录页算术验证码开关 |
| `elder-care.captcha.expire-seconds` | `120` | 登录验证码 Redis 过期时间 |

启动前请确认：

- MySQL 已启动，并已创建 `elder_care_system` 数据库。
- Redis 已启动。登录 token、邮箱验证码和登录算术验证码都依赖 Redis。
- 已执行 `schema.sql`，本地演示时已执行 `demo-data.sql`。
- `application.yml` 中的数据库账号、密码与本机一致。

## 前端启动

进入前端目录：

```bash
cd elder-care-system/frontend
```

安装依赖：

```bash
npm install
```

启动开发服务：

```bash
npm run dev
```

构建生产包：

```bash
npm run build
```

预览生产包：

```bash
npm run preview
```

前端接口地址当前写在：

```text
frontend/src/utils/request.ts
```

当前业务接口默认请求：

```text
http://localhost:8080/api
```

## 默认账号

当前演示账号来自 `sql/demo-data.sql`。

| 用户名 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | 管理员 | 管理端演示账号 |
| `admin1` | 管理员 | 管理端演示账号 |
| `admin2` | 管理员 | 管理端演示账号 |
| `caregiver` | 护工/健康管家相关演示角色 | 健康管家相关页面演示账号 |

注意：

- 密码为 BCrypt 加密后的值，明文密码请以你生成演示数据时的约定为准。
- 登录页默认开启算术验证码，需要先填写图片中的计算结果；如需临时关闭，可将 `elder-care.captcha.enabled` 改为 `false`。
- 如果登录失败，优先检查 `demo-data.sql` 是否完整导入，以及后端登录逻辑使用的密码加密规则是否一致。

## 接口约定

后端接口统一使用 `/api` 前缀。

示例：

```text
POST http://localhost:8080/api/user/login
GET  http://localhost:8080/api/common/captcha
GET  http://localhost:8080/api/bed/list
GET  http://localhost:8080/api/customer/list
```

登录验证码接口返回示例：

```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "captchaEnabled": true,
    "uuid": "验证码唯一标识",
    "img": "data:image/png;base64,..."
  }
}
```

登录请求在验证码开启时需要提交：

```json
{
  "account": "admin",
  "password": "明文密码",
  "code": "计算结果",
  "uuid": "验证码唯一标识"
}
```

普通接口返回结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

分页接口返回结构：

```json
{
  "total": 0,
  "records": []
}
```

## 配置说明

| 配置区域 | 说明 |
| --- | --- |
| `spring.datasource` | MySQL 连接配置 |
| `spring.data.redis` | Redis 连接配置 |
| `spring.ai.openai` | AI 对话服务配置，当前使用 DeepSeek 兼容接口地址 |
| `spring.mail` | 邮件服务配置，用于验证码等能力 |
| `elder-care.jwt` | JWT 密钥和过期时间 |
| `elder-care.alioss` | 阿里云 OSS 文件上传配置 |
| `elder-care.captcha` | 登录算术验证码配置，可控制是否开启、图片尺寸和过期时间 |

开发注意事项：

- 真实数据库密码、邮箱授权码、OSS 密钥、AI API Key 不建议提交到公开仓库。
- 修改 `server.servlet.context-path` 后，需要同步修改前端请求地址。
- 前端目前业务接口和 LLM 接口分别配置，LLM 相关地址也需要按实际服务调整。
- `elder-care.captcha.enabled=true` 时，登录前端会先请求 `/common/captcha` 获取算术验证码，后端在校验账号密码前先校验验证码。

## 开发规范

| 类型 | 约定 |
| --- | --- |
| 后端包结构 | `controller`、`service`、`service.impl`、`mapper`、`entity`、`dto`、`vo`、`util` |
| 请求对象 | 放在 `dto` 包，命名建议使用 `XxxRequest` |
| 响应对象 | 放在 `vo` 包，命名建议使用 `XxxVO` |
| 实体对象 | 放在 `entity` 包，只表达数据库表结构 |
| 返回结果 | 普通接口使用 `ApiResult<T>`，分页接口使用 `PageResult<T>` |
| 注释 | 类、字段、方法使用简短 Javadoc，不使用作者、大横幅分隔线等低价值注释 |
| SQL | 表、字段、重要数据块保留中文注释 |

## 常见问题

### 登录接口返回 500

优先排查：

1. MySQL 是否启动。
2. Redis 是否启动。
3. `application.yml` 中数据库账号密码是否正确。
4. 是否已执行 `schema.sql` 和 `demo-data.sql`。
5. 登录账号是否存在于 `user` 表。

### 登录验证码无法显示或验证码错误

优先排查：

1. Redis 是否启动，验证码答案会写入 Redis。
2. `/api/common/captcha` 是否能正常返回 `captchaEnabled`、`uuid` 和 `img`。
3. `elder-care.captcha.enabled` 是否为 `true`。
4. 前端是否把 `code` 和 `uuid` 一起提交到 `/api/user/login`。
5. 验证码是一次性使用，输错或登录失败后需要使用刷新后的验证码。

### 前端请求失败

优先排查：

1. 后端是否启动在 `http://localhost:8080/api`。
2. `frontend/src/utils/request.ts` 中业务接口 `baseURL` 是否正确。
3. 浏览器控制台是否存在 401、403、500 或跨域错误。
4. 登录 token 是否存在且未过期。

### SQL 执行失败

优先排查：

1. MySQL 版本是否为 8.0 或以上。
2. 是否先执行 `schema.sql`，再执行 `demo-data.sql`。
3. 数据库字符集是否为 `utf8mb4`。
4. 重复导入前是否清理旧表或重新创建数据库。

## 当前状态说明

当前项目仍处于开发和规范化阶段，README 按当前仓库真实文件编写。

已确认的当前事实：

| 项 | 当前状态 |
| --- | --- |
| 后端包名 | `com.eldercare.system` |
| 后端接口前缀 | `/api` |
| 前端包管理 | npm |
| SQL 文件 | `schema.sql`、`demo-data.sql` |
| `data.sql` | 当前不存在 |
| 默认数据库 | `elder_care_system` |
| 默认 Redis | `127.0.0.1:6379` |
| 登录验证码 | 默认开启，接口为 `/api/common/captcha`，Redis key 前缀为 `LOGIN:CAPTCHA:` |

后续如果继续调整角色、SQL 拆分、默认账号、接口路径或配置方式，需要同步更新本文档。
