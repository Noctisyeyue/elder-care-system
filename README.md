# 颐养中心后台管理系统

东软颐养中心后台管理系统，基于 Spring Boot + Vue 3 构建。

## 技术栈

| 层 | 技术 |
|------|------|
| 后端 | Java 17 / Spring Boot 3.2.4 / MyBatis-Plus 3.5.6 |
| 前端 | Vue 3 / Vite / Element Plus / Pinia / TypeScript |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 鉴权 | JWT |

## 项目结构

```
elder-care-system/
├── backend/         后端 Spring Boot 项目
├── frontend/        前端 Vue 3 + Vite 项目
├── sql/             数据库脚本
│   ├── schema.sql   建表语句
│   └── demo-data.sql 基础数据 + 演示数据
├── docs/            项目文档
└── README.md
```

## 快速开始

### 1. 环境要求

- Java 17
- Maven 3.8+
- Node.js 18+
- MySQL 8.0+
- Redis

### 2. 数据库初始化

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/demo-data.sql
```

### 3. 启动后端

```bash
cd backend
mvn spring-boot:run -DskipTests
```

后端运行在 `http://localhost:8080/api`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`

### 5. 登录

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |

健康管家账号由管理员在系统中创建。

## 配置说明

后端配置文件：`backend/src/main/resources/application.yml`

主要配置项：

| 配置 | 说明 |
|------|------|
| `spring.datasource` | 数据库连接，默认 `localhost:3306/elder_care_system` |
| `spring.data.redis` | Redis 连接，默认 `localhost:6379` |
| `elder-care.jwt.secret-key` | JWT 签名密钥 |
| `server.servlet.context-path` | 接口前缀 `/api` |
