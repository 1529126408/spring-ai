# Spring AI 项目

## 项目简介
这是一个基于Spring Boot和Vue.js的AI应用项目，集成了多种AI模型服务，包括OpenAI、Ollama等，提供了统一的AI服务接口和友好的用户界面。

## 技术栈
### 后端技术
- Spring Boot 3.4.4
- Spring AI Framework
- Java 17
- Maven

### 前端技术
- Vue.js 3
- Vite
- Naive UI
- Axios
- Marked & Highlight.js

## 功能特点
- 集成多种AI模型服务
  - OpenAI
  - Ollama
  - DashScope
- 统一的AI服务接口
- 现代化的Web界面
- 实时Markdown渲染
- 代码高亮显示

## 快速开始

### 后端服务
1. 确保已安装Java 17和Maven
2. 克隆项目到本地
3. 在项目根目录执行：
```bash
./mvnw spring-boot:run
```

### 前端应用
1. 进入app目录：
```bash
cd app
```

2. 安装依赖：
```bash
npm install
```

3. 开发环境运行：
```bash
npm run dev
```

4. 生产环境构建：
```bash
npm run build
```

## 配置说明
### AI服务配置
在`application.properties`或`application.yml`中配置相应的AI服务参数：
- OpenAI API密钥
- Ollama服务地址
- DashScope配置

### 前端配置
可以在`vite.config.js`中自定义开发服务器配置，详见[Vite配置参考](https://vitejs.dev/config/)。

## 开发指南
- 后端API位于`src/main/java`目录
- 前端源码位于`app/src`目录
- 使用标准Git工作流进行开发
- 遵循项目既定的代码规范

## 贡献指南
欢迎提交Issue和Pull Request来帮助改进项目。请确保：
1. 提交前已经完成充分的测试
2. 遵循现有的代码风格
3. 提供清晰的提交信息

## 许可证
本项目采用MIT许可证