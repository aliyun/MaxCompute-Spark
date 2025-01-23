---
sidebar_position: 1
---
# 应用开发示例
快速导航
  + [WordCount](#1)
  + [GraphX PageRank](#2)
  + [Mllib Kmeans-ON-OSS](#3)
  + [OSS UnstructuredData](#4)
  + [JindoFs](#5)
  + [MaxCompute Table读写 Java/Scala示例](#6)
  + [MaxCompute Table读写 PySpark示例](#7)
  + [OSS PySpark示例](#8)
  + [Spark Streaming Loghub支持](#9)
  + [Spark streaming Datahub支持](#10)
  + [Spark streaming Kafka支持](#11)

==============================

## 下载模版项目工程

参考 [模板项目工程下载](https://github.com/aliyun/MaxCompute-Spark)

模板项目工程提供了spark-1.x以及spark-2.x的样例代码，提交方式和代码接口比较相仿，由于2.x为主流推荐版本，本文将以spark-2.x为主做案例演示。

## 案例说明

<h3 id="1">WordCount</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/WordCount.scala)
提交方式

```
cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.WordCount \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

<h3 id="2">GraphX PageRank</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/graphx/PageRank.scala)
提交方式

```
cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.graphx.PageRank \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

<h3 id="3">Mllib Kmeans-ON-OSS</h3>

`spark.hadoop.fs.oss.ststoken.roleArn` `spark.hadoop.fs.oss.endpoint` 

如何填写请参考 [OSS StsToken授权文档](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/mllib/KmeansModelSaveToOss.scala)
提交方式

```
# 编辑代码
val modelOssDir = "oss://bucket/kmeans-model" // 填写具体的oss bucket路径
val spark = SparkSession
  .builder()
  .config("spark.hadoop.fs.oss.credentials.provider", "org.apache.hadoop.fs.aliyun.oss.AliyunStsTokenCredentialsProvider")
  .config("spark.hadoop.fs.oss.ststoken.roleArn", "acs:ram::****:role/aliyunodpsdefaultrole")
  .config("spark.hadoop.fs.oss.endpoint", "oss-cn-hangzhou-zmf.aliyuncs.com")
  .appName("KmeansModelSaveToOss")
  .getOrCreate()

cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.mllib.KmeansModelSaveToOss \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

<h3 id="4">OSS UnstructuredData</h3>

`spark.hadoop.fs.oss.ststoken.roleArn` `spark.hadoop.fs.oss.endpoint` 

如何填写请参考 [OSS StsToken授权文档](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/oss/SparkUnstructuredDataCompute.scala)
提交方式

```
# 编辑代码
val pathIn = "oss://bucket/inputdata/" // 填写具体的oss bucket路径
val spark = SparkSession
  .builder()
  .config("spark.hadoop.fs.oss.credentials.provider", "org.apache.hadoop.fs.aliyun.oss.AliyunStsTokenCredentialsProvider")
  .config("spark.hadoop.fs.oss.ststoken.roleArn", "acs:ram::****:role/aliyunodpsdefaultrole")
  .config("spark.hadoop.fs.oss.endpoint", "oss-cn-hangzhou-zmf.aliyuncs.com")
  .appName("SparkUnstructuredDataCompute")
  .getOrCreate()

cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.oss.SparkUnstructuredDataCompute \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

<h3 id="5">JindoFs</h3>

`spark.hadoop.fs.AbstractFileSystem.oss.impl` `spark.hadoop.fs.oss.impl` `spark.hadoop.fs.jfs.cache.oss.credentials.provider` `spark.hadoop.aliyun.oss.provider.url`

如何填写请参考 [Jindo sdk接入说明](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Jindo-sdk%E6%8E%A5%E5%85%A5%E8%AF%B4%E6%98%8E)

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/oss/JindoFsDemo.scala)
提交方式

```
cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)
# 以及 https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Jindo-sdk%E6%8E%A5%E5%85%A5%E8%AF%B4%E6%98%8E

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.oss.JindoFsDemo \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar \
    ${aliyun-uid} ${role} ${oss-bucket} ${oss-path}
```

<h3 id="6">MaxCompute Table读写 Java/Scala示例</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/sparksql/SparkSQL.scala)
提交方式

```
cd /path/to/MaxCompute-Spark/spark-2.x
mvn clean package

# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.sparksql.SparkSQL \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

<h3 id="7">MaxCompute Table读写 PySpark示例</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/python/spark_sql.py)
提交方式

```
# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --jars /path/to/odps-spark-datasource_2.11-3.3.8-public.jar \
    /path/to/MaxCompute-Spark/spark-2.x/src/main/python/spark_sql.py
```

<h3 id="8">PySpark写OSS示例</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/python/spark_oss.py)
提交方式

```
# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)
# oss相关配置参考: https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --jars /path/to/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar \
    /path/to/MaxCompute-Spark/spark-2.x/src/main/python/spark_oss.py
其中需要加入的这个spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar可以从https://github.com/aliyun/MaxCompute-Spark/tree/master/spark-2.x编译得到，主要是需要里面的OSS相关的依赖。
```

<h3 id="9">Spark Streaming Loghub支持</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/loghub/LogHubStreamingDemo.scala)
提交方式

```
# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)
cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.streaming.loghub.LogHubStreamingDemo \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

[更多信息参考](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Streaming%E8%AF%BB%E5%86%99LogHub)

<h3 id="10">Spark Streaming Datahub支持</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/datahub/DataHubStreamingDemo.scala)
提交方式

```
# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.streaming.datahub.DataHubStreamingDemo \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```

[更多信息参考](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Streaming%E8%AF%BB%E5%86%99DataHub)

<h3 id="11">Spark Streaming Kafka支持</h3>

[详细代码](https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/kafka/KafkaStreamingDemo.scala)
提交方式

```
# 环境变量 spark-defaults.conf 等等以及参考下面链接配置完毕
# https://github.com/aliyun/MaxCompute-Spark/wiki/02.-%E4%BD%BF%E7%94%A8Spark%E5%AE%A2%E6%88%B7%E7%AB%AF%E6%8F%90%E4%BA%A4%E4%BB%BB%E5%8A%A1(Yarn-Cluster%E6%A8%A1%E5%BC%8F)

cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class \
    com.aliyun.odps.spark.examples.streaming.kafka.KafkaStreamingDemo \
    /path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar
```
