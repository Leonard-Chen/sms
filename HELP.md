# 使用手册

该手册用于在 Windows 本地启动并使用该项目的后端微服务应用 + 前端Web页面。

---

## 运行环境要求

- **JDK**：21（或 ≥17，推荐与仓库保持一致）
- **Node.js**：>= 20.19（推荐 22.12+）
- **MySQL**：已创建数据库 `sms`
- **Redis**：默认 `localhost:6379`
- **Nacos**：默认 `localhost:8848`

---

## 端口说明

- **前端（Vite dev）**：默认 80（若被占用会自动换到 81 等）
- **网关**：9000（以你当前仓库配置为准，下同）
- **系统/认证（sms-system）**：9100
- **客户（sms-customer）**：9200
- **订单（sms-order）**：9300
- **员工（sms-employee）**：9400
- **统计（sms-statistics）**：9500
- **AI 助手（sms-ai）**：9600
- **Nacos**：8848
- **Redis**：6379
- **MySQL**：3306

对外访问统一走网关：`http://localhost:9000`

---

## 一次性初始化数据库（推荐）

本仓库的表结构与测试数据在：

- `sms-dao/src/main/resources/db/schema.sql`
- `sms-dao/src/main/resources/db/data.sql`

建议用脚本一次性初始化（避免 “库里没数据/表结构不一致”）：

1. 确保 MySQL 已创建数据库：`sms`
2. 在 `sms-dao/src/main/resources/db/` 目录执行：

```bash
mysql -u root -p sms < init.sql
```

---

## 启动顺序（后端）

1. 启动 **MySQL**（库名 `sms`）
2. 启动 **Redis**（默认 `localhost:6379`）
3. 启动 **Nacos Server**（默认 `localhost:8848`）
4. 启动 Spring Boot 微服务（建议顺序）
    - `sms-system`
    - `sms-ai`
    - `sms-customer` / `sms-order` / `sms-employee` / `sms-statistics`
    - `sms-gateway`

### 用脚本启动（Windows）

仓库内有脚本说明：`scripts/启动说明.txt`

一键启动多个窗口：

```bat
scripts\run-all-new-windows.bat
```

### 用 Maven 启动（示例）

在仓库根目录 `sms`：

```bash
mvn -pl sms-system -am spring-boot:run
mvn -pl sms-ai -am spring-boot:run
mvn -pl sms-customer -am spring-boot:run
mvn -pl sms-order -am spring-boot:run
mvn -pl sms-employee -am spring-boot:run
mvn -pl sms-statistics -am spring-boot:run
mvn -pl sms-gateway -am spring-boot:run
```

---

## 启动前端（Vite + Vue）

前端目录：`sms-frontend`

### Node 版本（nvm）

```powershell
nvm install 22.12.0
nvm use 22.12.0
node -v
```

### 安装依赖 + 启动

```powershell
cd e:\sms\sms-frontend
npm install
npm run dev
```

访问地址（以实际输出为准）：

- `http://localhost/`
- 如果 80 被占用，Vite 可能会启到 `http://localhost:81/`

---

## AI 助手（新增）

系统已接入 AI 助手模块：

- 后端服务：`sms-ai`
- 网关转发：`/ai/**`
- 前端入口：业务页面左下角 `AI` 悬浮球（登录后显示）

### AI 配置（必须）

在启动 `sms-ai` 前配置以下参数（环境变量或 `sms-ai/src/main/resources/application.yml`）：

- `AI_BASE_URL`：模型服务地址，Moonshot/Kimi 建议 `https://api.moonshot.cn/v1`
- `AI_API_KEY`：你的模型 API Key（仅填 key，不要加 `Bearer ` 前缀）
- `AI_MODEL`：模型名，例如 `kimi-k2-0711-preview`

示例（PowerShell）：

```powershell
$env:AI_BASE_URL = "https://api.moonshot.cn/v1"
$env:AI_API_KEY = "your_api_key"
$env:AI_MODEL = "kimi-k2-0711-preview"
```

### AI 接口

- 健康检查：`GET /api/ai/health`
- 对话接口：`POST /api/ai/chat`

---

## 测试账号密码（内置数据）

这些账号来自 `sms-dao/src/main/resources/db/data.sql` 中 `sys_user` 的初始化数据：

- **monika** / **justmonika**
- **hikari** / **testify**
- **tairitsu** / **tempestissimo**
- **nitro** / **vernon**
- **guy** / **anton**

> 如登录提示 401，请确认你连接的是同一个 `sms` 库，并且已执行初始化脚本。

---

## 常见问题

1. **登录遇到 401 错误或自动退出**
    - 检查网关、system 是否启动
    - 重新登录获取新 token

2. **OAuth2 授权页 400**
    - 通常是 `redirect_uri` 与授权服务器登记的不一致。前端回调url应为：`http://localhost:<前端端口>/callback`<br/>
    - 若更换了端口（例如 Vite/Vue CLI 运行前端项目时所使用的 5173），需将对应回调地址加入授权服务器允许列表（即 `sms-client` 的 redirectUri）。

3. **AI 返回 401 Invalid Authentication**
    - 检查 `AI_API_KEY`
    - `AI_BASE_URL` 用 `.../v1`
    - `AI_API_KEY` 不要带 `Bearer `

4. **前端数据为空**
    - 首先检查数据库初始化是否完成，以及对应模块服务是否启动
    - 按F12打开开发者工具→网络（Network），刷新页面，查看请求是否有正确得到响应数据，若有，通常是因为**前端表格字段名（prop）与后端 JSON 字段不一致**。
      请以后端实体/接口返回字段为准（例如 `customerNo/customerName/contactPhone`）。

5. **CORS问题**
    - 本仓库前端已通过 Vite devServer（或 nginx）代理将 `/api` 开头的请求转发到网关，解决了跨域问题。
   若更改了端口或地址，请确保前端请求仍是： 
      - 浏览器访问前端：`http://localhost:80` 或 `http://localhost:81`
      - API 通过代理访问：`/api/...`
