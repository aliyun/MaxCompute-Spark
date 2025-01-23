# ODPS SDK 文档目录 README

## 简介

此目录用于维护和构建 ODPS SDK 的官方文档，采用 Docusaurus 进行文档的编写与管理，并利用 GitHub Pages 功能将其托管于 [https://aliyun.github.io/aliyun-odps-java-sdk/](https://aliyun.github.io/aliyun-odps-java-sdk/) 。当前文档正处于积极开发阶段，并且仅提供中文版本。

## 文档构建工具 - Docusaurus

Docusaurus 是一个强大的静态站点生成器，特别适合构建和维护开源项目的文档网站。它的中文文档可以在 [Docusaurus 中文官网](https://docusaurus.io/zh-CN/docs) 找到，这里详细介绍了如何开始、配置以及进阶使用 Docusaurus。

## 文档目录结构

文档源文件位于 `docs/docs` 目录下。请在此目录中添加、修改或删除文档内容。

## 开发环境搭建与本地调试

### 初始化项目

在 docs 目录下，请确保运行以下命令以安装所有依赖：

```bash
yarn install
```

### 本地运行与预览

安装完依赖后，你可以通过以下命令启动本地开发服务器，进行实时预览和调试：

```bash
yarn start
```

这将自动打开浏览器并显示文档的本地预览版。

## 部署文档

### 当前部署流程

目前文档部署为手动过程，但考虑未来可能采用 GitHub Actions 自动化部署。

#### 手动部署步骤

1. 确保你的文档是最新的，并且你已经测试过。
2. 在项目根目录下的 `docs` 目录中执行以下命令：

```bash
USE_SSH=true yarn deploy
```

该命令会使用 SSH 方式（如果配置了）将编译好的网站发布到 `gh-pages` 分支。此过程包括创建一个临时目录，复制编译后的文件至该目录，然后推送至 GitHub。

#### 注意事项

- 如果因 Git Hooks 或其他原因导致自动推送失败，你可以手动进入该临时目录，并执行 `git push` 来完成部署。
- 确保你有正确的权限推送至 `gh-pages` 分支。

---

文档持续更新中，对于任何问题、建议或想要贡献的意愿，请随时开启 Issue 或发起 Pull Request。让我们共同完善 ODPS SDK 的文档资源！