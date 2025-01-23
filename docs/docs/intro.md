---
title: ODPS SDK for Java 介绍
sidebar_position: 1
---

# ODPS SDK for Java 介绍

MaxCompute(ODPS) SDK for Java 允许开发者将他们的Java应用程序与阿里云的 MaxCompute 服务集成，MaxCompute
是一个大规模并行处理的数据仓库系统。

该SDK提供了一组丰富的API，使您能够轻松地与 MaxCompute 进行交互，执行各种数据处理任务。

# 目录

- [快速开始](../quick-start)
- [代码示例](../category/代码示例)
- [API 参考](../category/api-参考)
- [常见问题](../question)
- [更新日志](../changelog)

## Maven 安装

要在您的Maven项目中使用ODPS SDK for Java，您需要将以下依赖添加到项目的`pom.xml`文件中：

```xml
<!-- 添加ODPS SDK for Java的依赖项 -->
<dependency>
    <groupId>com.aliyun.odps</groupId>
    <artifactId>odps-sdk-core</artifactId>
    <version>0.50.3-public</version>
</dependency>
```

请确保您使用的是[Maven Central Repository](https://mvnrepository.com/artifact/com.aliyun.odps/odps-sdk-core)
或[阿里云Maven仓库](https://developer.aliyun.com/mvn/)中可用的最新稳定版本。

# 贡献

想要为ODPS SDK for
Java做出贡献的开发者，请访问我们的[Github仓库](https://github.com/aliyun/aliyun-odps-java-sdk)。

# 声明

本文档尚处于编写阶段，内容尚不完善，如有任何问题，或文档需求，请在[Github仓库](https://github.com/aliyun/aliyun-odps-java-sdk)
提交issue，我们会尽快解答和完善。