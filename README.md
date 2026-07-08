<<<<<<< HEAD
# 在线考试系统

基于 Spring Boot 3.1.8 + Vue 3 + Element Plus 构建的在线考试系统。

## 功能特性

### 学生端
- 用户注册与登录
- 考试列表查看
- 考试须知确认
- 在线答题（支持单选、多选、填空、编程题）
- 倒计时与自动交卷
- 切屏检测
- 答题结果查看与错题本

### 管理员端
- 考试管理（创建、编辑、删除）
- 题库管理（添加、编辑、删除题目）
- 成绩管理（查看所有考生成绩）
- 管理员重置密码

## 技术栈

### 后端
- Java 17
- Spring Boot 3.1.8
- Spring Security
- MyBatis-Plus 3.5.7
- JWT 认证
- Hutool 工具库

### 前端
- Vue 3 (Composition API)
- Vite 6.5.0
- Element Plus
- Vue Router
- Pinia
- Axios

### 数据库
- MySQL 5.1.73（兼容 MySQL 5.x 版本）

## 环境要求

- JDK 17+
- MySQL 5.x（推荐 5.1.73，不兼容 MySQL 8.x）
- Node.js 18+
- Maven 3.6+

## 快速开始

### 方式一：使用共享数据库（推荐）

如果团队成员共享同一个数据库，请按照以下配置：

#### 1. 后端配置

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/exam_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: your_password_here
    driver-class-name: com.mysql.jdbc.Driver
```

**重要配置说明：**
- MySQL 5.1.73 必须使用 JDBC 驱动版本 5.1.49
- 驱动类必须为 `com.mysql.jdbc.Driver`（而非 `com.mysql.cj.jdbc.Driver`）
- 数据库连接 URL 必须包含 `characterEncoding=utf8` 以支持中文

#### 2. 数据库权限（如果使用远程数据库）

如果数据库部署在远程服务器，需要确保数据库用户有远程访问权限：

```sql
GRANT ALL PRIVILEGES ON exam_db.* TO 'root'@'%' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
```

并在服务器防火墙开放 3306 端口。

---

### 方式二：本地数据库（独立开发）

如果需要在本地搭建数据库：

#### 1. 安装 MySQL 5.x

下载 MySQL 5.1.73（或兼容版本）：
- Windows：从 MySQL 官方网站下载安装包
- Linux：`sudo apt-get install mysql-server-5.7`（或对应版本）

#### 2. 创建数据库

```sql
CREATE DATABASE exam_db CHARACTER SET utf8 COLLATE utf8_general_ci;
USE exam_db;
```

#### 3. 导入 schema

```bash
mysql -u root -p exam_db < backend/src/main/resources/schema.sql
```

#### 4. 配置后端

同上，编辑 `application.yml` 使用本地数据库连接。

### 3. 启动后端

**推荐方式（VS Code）：**
1. 安装 Spring Boot Extension Pack
2. 打开 `backend/src/main/java/com/kaoshi/exam/ExamApplication.java`
3. 点击编辑器右上角的 "Run" 按钮或使用 Ctrl+F5

**命令行方式：**
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动。

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 `http://localhost:5173` 启动。

### 5. VS Code 开发环境配置

#### 推荐扩展
- **Spring Boot Extension Pack** - 后端开发必备
- **Vetur** 或 **Vue Language Features (Volar)** - Vue 3 支持
- **ESLint** - 代码检查
- **Prettier** - 代码格式化

#### 调试配置
在 `.vscode/launch.json` 中添加：

```json
{
  "configurations": [
    {
      "type": "java",
      "name": "ExamApplication",
      "request": "launch",
      "mainClass": "com.kaoshi.exam.ExamApplication",
      "projectName": "exam"
    }
  ]
}
```

#### 前端调试
1. 运行 `npm run dev`
2. 在 VS Code 中使用 "Launch Chrome against localhost" 配置进行调试

## 管理员账号

### 默认管理员
- 用户名：`admin`
- 密码：`admin123`

### 重置管理员密码

如果忘记管理员密码，可以通过以下方式重置：

**方法一：API 接口**
```bash
curl -X POST http://localhost:8080/api/auth/reset-admin
```

**方法二：数据库直接修改**
```sql
UPDATE user SET password = '$2a$10$eHifBG8EkUrVmRVwQTuaHeXNxjTHErwRV9.uiN1tYshc43VFoqW/W' WHERE username = 'admin';
```
（此密码哈希对应的明文为 `admin123`）

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/reset-admin` - 重置管理员密码

### 考试接口
- `GET /api/exam/available` - 获取可用考试列表
- `GET /api/exam/{id}` - 获取考试详情
- `GET /api/exam/{id}/questions` - 获取考试题目
- `POST /api/exam` - 创建考试（管理员）
- `PUT /api/exam/{id}` - 更新考试（管理员）
- `DELETE /api/exam/{id}` - 删除考试（管理员）

### 答题接口
- `POST /api/answer/submit` - 提交答案
- `GET /api/answer/history` - 获取答题历史
- `GET /api/answer/exam/{examId}` - 获取当前用户指定考试的答题记录
- `GET /api/answer/exam/all/{examId}` - 获取指定考试的所有答题记录（管理员）
- `GET /api/answer/sheet/{sheetId}` - 获取答题详情

### 题目接口
- `GET /api/question/exam/{examId}` - 获取考试题目
- `POST /api/question` - 添加题目（管理员）
- `PUT /api/question/{id}` - 更新题目（管理员）
- `DELETE /api/question/{id}` - 删除题目（管理员）

### 错题接口
- `GET /api/wrong` - 获取错题本

## 考试流程

### 学生答题流程
1. 登录系统
2. 选择可用考试
3. 阅读考试须知并确认
4. 进入答题页面（系统开始倒计时）
5. 答题过程中自动保存（每30秒）
6. 提交试卷或超时自动交卷
7. 查看答题结果与错题分析

### 管理员流程
1. 登录管理后台
2. 创建考试（设置时间、时长、随机组卷规则）
3. 添加/编辑题库（支持单选、多选、填空、编程题）
4. 查看考试进度与成绩
5. 导出成绩报表

## 考试防作弊机制

- **倒计时限制**：考试时间到自动交卷
- **切屏检测**：切屏次数超过5次会被记录
- **随机出题**：支持题目顺序和选项顺序随机打乱
- **单次作答**：每场考试只能作答一次，完成后无法重新作答

## 常见问题

### Q1: 后端启动失败 - 端口 8080 被占用
```bash
# Windows
Get-NetTCPConnection -LocalPort 8080 | Stop-Process -Id $_.OwningProcess -Force

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Q2: MySQL 连接失败 - CLIENT_PLUGIN_AUTH is required
确保使用 MySQL 5.x 版本，并在 `application.yml` 中配置：
```yaml
driver-class-name: com.mysql.jdbc.Driver
```

### Q3: 中文乱码问题
确保数据库字符集为 `utf8`，并在连接 URL 中添加 `characterEncoding=utf8`。

### Q4: 管理员登录失败
使用重置密码接口重置密码：
```bash
curl -X POST http://localhost:8080/api/auth/reset-admin
```

### Q5: 成绩管理页面显示 No Data
确保：
1. 考试已结束（状态为"已结束"）
2. 已有学生提交了该考试的答案
3. 后端服务正常运行

### Q6: 前端页面无法访问后端接口
检查：
1. 后端服务是否启动成功（`http://localhost:8080`）
2. 前端代理配置是否正确（`vite.config.js` 中的 proxy 配置）
3. 浏览器控制台是否有跨域错误

### Q7: 考试页面倒计时不显示
确保考试配置了正确的时长（duration），并且后端返回的考试数据包含 duration 字段。

### Q8: VS Code 无法识别 Java 项目
1. 确保安装了 JDK 17 并配置了环境变量
2. 安装 Spring Boot Extension Pack
3. 在 VS Code 设置中配置 `java.jdt.ls.java.home` 指向 JDK 安装目录

### Q9: 如何创建学生账号进行测试
使用注册功能创建学生账号，或者直接在数据库中插入：
```sql
INSERT INTO user (username, password, role, email, phone) 
VALUES ('test', '$2a$10$eHifBG8EkUrVmRVwQTuaHeXNxjTHErwRV9.uiN1tYshc43VFoqW/W', 'STUDENT', 'test@example.com', '13800138000');
```
（密码为 `admin123`）

## 项目结构

```
kaoshisystem/
├── backend/                    # 后端项目
│   ├── src/main/java/com/kaoshi/exam/
│   │   ├── controller/         # REST API 控制层
│   │   ├── service/            # 业务逻辑层
│   │   ├── mapper/             # 数据访问层
│   │   ├── entity/             # 数据库实体
│   │   ├── dto/                # 数据传输对象
│   │   ├── security/           # 安全配置
│   │   └── ExamApplication.java
│   ├── src/main/resources/
│   │   ├── application.yml     # 应用配置
│   │   └── schema.sql          # 数据库初始化脚本
│   └── pom.xml                 # Maven 依赖配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── views/              # 页面组件
│   │   ├── api/                # API 接口封装
│   │   ├── store/              # Pinia 状态管理
│   │   ├── router/             # 路由配置
│   │   ├── utils/              # 工具函数
│   │   └── main.js             # 入口文件
│   ├── public/                 # 静态资源
│   ├── index.html              # HTML 模板
│   ├── package.json            # npm 依赖配置
│   └── vite.config.js          # Vite 配置
└── README.md                   # 项目说明文档
```

## License

MIT License
=======
# Exam-System
专业实训与技能达标实验项目
>>>>>>> 8bc090fc9df84d003de569463eaa1898f80e604f
