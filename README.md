# sms-小型服务型企业业务管理系统（Web端）

## 项目简介

sms是面向广大中小服务业开发的一套简单易用的业务智能管理系统。采用前后端分离架构，以Spring Boot+Spring Cloud Alibaba+Vue3为核心技术栈。系统包含以下功能模块：

- 客户信息管理
- 订单管理与分派
- 员工及部门管理
- 系统管理，主要包括用户管理等
- 可视化数据看板
- AI 智能助手

---

## 项目组成

| 模块 | 说明                        |
|---|---------------------------|
| `sms-gateway` | 网关模块，对请求统一拦截，端口 `9000`    |
| `sms-system` | 系统管理，提供用户身份认证服务           |
| `sms-customer` | 客户管理微服务                   |
| `sms-order` | 订单管理与服务调度微服务              |
| `sms-employee` | 员工管理微服务，包括员工、部门等信息的管理     |
| `sms-statistics` | 数据统计微服务                   |
| `sms-ai` | AI 助手微服务                  |
| `sms-common` | 通用模块，包含实体类、公共配置、跨服务调用、工具等 |
| `sms-dao` | 数据访问模块，包含数据库配置、初始化脚本等     |
| `sms-frontend` | 前端项目                      |

---

## 运行环境

- JDK 21（至少 17）
- Node.js 20.19+（推荐 22.12+）
- MySQL（库名：`sms`）
- Redis（默认 `localhost:6379`）
- Nacos（默认 `localhost:8848`）

---

## 打包交付建议

若要压缩发给他人并保留可运行能力，建议先清理运行产物：

```powershell
mvn clean
Remove-Item -Recurse -Force .\sms-frontend\dist -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force .\sms-frontend\node_modules -ErrorAction SilentlyContinue
```

---

*关于本项目的详细使用说明，请看：*[使用手册](./HELP.md)
