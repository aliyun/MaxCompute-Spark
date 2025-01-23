---
slug: /mode
sidebar_position: 1
---

# 运行模式
目前MaxCompute Spark支持以下几种运行方式：client 模式，local模式，cluster模式，以及支持在DataWorks中执行。

## Local模式
local模式可用于小批量数据以及计算本地验证，local模式验证通过后再提交到yarn-cluster模式

**说明** 
具体使用可参考[Local模式](./local-mode.md)

```
## /path/to/MaxCompute-Spark 请指向正确的编译出来后的application jar包
cd $SPARK_HOME
bin/spark-submit --master local[4] --class com.aliyun.odps.spark.examples.SparkPi \
/path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

## Cluster模式
**说明** 
具体使用可参考[Yarn Cluster模式](./yarn-cluster.md)

```
## /path/to/MaxCompute-Spark 请指向正确的编译出来后的application jar包
cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class com.aliyun.odps.spark.examples.SparkPi \
/path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

## 在 DataWorks 上执行
Spark作业可以在DataWorks中进行调度，本质上也是采用了Yarn Cluster模式进行任务提交

**说明** 
具体使用可参考[Spark on Dataworks](../dataworks-integration.md)
