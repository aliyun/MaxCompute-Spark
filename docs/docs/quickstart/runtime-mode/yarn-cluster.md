# Yarn Cluster 模式
快速导航
  + [下载MaxCompute Spark客户端](#1)
  + [设置环境变量](#2)
  + [配置spark-defaults.conf](#3)
  + [准备项目工程](#4)
  + [SparkPi 冒烟测试](#5)
-----------------

<h1 id="1">下载MaxCompute Spark客户端</h1>

MaxCompute Spark发布包集成了MaxCompute认证功能。作为客户端工具，它用于通过spark-submit方式提交作业到MaxCompute项目中运行。

目前Spark版本支持如下，请优先使用Spark 2以上的版本!
* [spark-1.6.3](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/1.6.3-public/spark-1.6.3-public.tar.gz)

专有云：
* [spark-2.3.0](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/2.3.0-odps0.33.0/spark-2.3.0-odps0.33.0.tar.gz)
* [spark-2.4.5](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/2.4.5-odps0.33.4/spark-2.4.5-odps0.33.4.tar.gz)
* [spark-3.1.1](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/3.1.1-odps0.33.0/spark-3.1.1-odps0.33.0.tar.gz)

公共云：
* [spark-2.3.0](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/2.3.0-odps0.34.0/spark-2.3.0-odps0.34.0.tar.gz)
* [spark-2.4.5](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/2.4.5-odps0.34.0/spark-2.4.5-odps0.34.0.tar.gz)
* [spark-3.1.1](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/3.1.1-odps0.34.1/spark-3.1.1-odps0.34.1.tar.gz)

<h1 id="2">设置环境变量</h1>

* JAVA_HOME设置

```
## 推荐使用JDK 1.8
export JAVA_HOME=/path/to/jdk
export CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
export PATH=$JAVA_HOME/bin:$PATH
```

* SPARK_HOME设置

```
## 下载上文提到的MaxCompute Spark客户端并解压到本地任意路径
## 请不要直接设置SPARK_HOME等于以下路径下述路径仅做展示用途
## 请指向正确的路径
export SPARK_HOME=/path/to/spark_extracted_package
export PATH=$SPARK_HOME/bin:$PATH
```

* PySpark的用户请安装Python2.7版本，并设置PATH

```
export PATH=/path/to/python/bin/:$PATH
```

* HADOOP_CONF_DIR设置：注意Spark 2.4.5和Spark 3必须要设置该参数

```
export HADOOP_CONF_DIR=$SPARK_HOME/conf
```

<h1 id="3">配置spark-defaults.conf</h1>

+ 第一次下载MaxCompute Spark客户端后，需要配置spark-defaults.conf
+ 在 $SPARK_HOME/conf/ 下面有一个文件名称为 spark-defaults.conf.template。请将其重命名为 spark-defaults.conf 后再进行相关配置（很多人会忽略这一步，导致配置无法生效）

```
## spark-defaults.conf
## 一般来说默认的template只需要再填上MaxCompute相关的账号信息就可以使用Spark
spark.hadoop.odps.project.name =
spark.hadoop.odps.access.id =
spark.hadoop.odps.access.key =

## 其他的配置直接采用以下参数即可
spark.hadoop.odps.end.point = http://service.cn.maxcompute.aliyun.com/api
spark.hadoop.odps.runtime.end.point = http://service.cn.maxcompute.aliyun-inc.com/api

##########-------注意catalog设置-------##########
### spark 2.3.0请将该参数设置为odps
spark.sql.catalogImplementation=odps

### spark 2.4.5请将该参数设置为hive
spark.sql.catalogImplementation=hive

### spark 3.1.1参数变化
spark.sql.defaultCatalog=odps
spark.sql.catalog.odps=org.apache.spark.sql.execution.datasources.v2.odps.OdpsTableCatalog 
spark.sql.sources.partitionOverwriteMode=dynamic
spark.sql.extensions=org.apache.spark.sql.execution.datasources.v2.odps.extension.OdpsExtensions

```

如果有一些特殊的场景还有功能，还可以开启另外的一些配置，见[Spark配置详解](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Spark%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3)

Spark 2.4.5的参数变化，详见[Spark 2.4.5使用注意事项](https://github.com/aliyun/MaxCompute-Spark/wiki/06.-Spark-2.4.5-%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)

Spark 3.1.1的参数变化，详见[Spark 3.1.1使用注意事项](https://github.com/aliyun/MaxCompute-Spark/wiki/06.-Spark-3.1.1-%E4%BD%BF%E7%94%A8%E6%B3%A8%E6%84%8F%E4%BA%8B%E9%A1%B9)

<h1 id="4">准备项目工程</h1>

+ MaxCompute Spark提供了项目工程模版，建议开发者下载模版复制后直接在模版里开发

+ 可以看到模版工程里的关于spark的依赖的scope都是provided的，这个请务必不要更改，否则提交的作业无法正常运行

spark-1.x 模板及编译

```
git clone https://github.com/aliyun/MaxCompute-Spark.git
cd spark-1.x
mvn clean package
```

spark-2.x 模板及编译

```
git clone https://github.com/aliyun/MaxCompute-Spark.git
cd spark-2.x
mvn clean package
```

spark-3.x 模板及编译

```
git clone https://github.com/aliyun/MaxCompute-Spark.git
cd spark-3.x
mvn clean package
```

<h1 id="5">SparkPi 冒烟测试</h1>

在完成了以上的工作后，可以来进行冒烟测试，验证MaxCompute Spark是否E2E走通，需要以下前提:

* 准备MaxCompute项目以及对应的accessId，accessKey
* 下载MaxCompute Spark客户端
* 环境变量准备
* spark-defaults.conf配置
* 下载工程模版并编译

以 spark-2.x 为例，我们可以提交一个SparkPi来验证功能是否正常，提交命令如下:

```
## /path/to/MaxCompute-Spark 请指向正确的编译出来后的application jar包

## bash环境
cd $SPARK_HOME
bin/spark-submit --master yarn-cluster --class com.aliyun.odps.spark.examples.SparkPi \
/path/to/MaxCompute-Spark/spark-2.x/target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar

## 在windows环境提交
cd $SPARK_HOME/bin
spark-submit.cmd --master yarn-cluster --class com.aliyun.odps.spark.examples.SparkPi
\path\to\MaxCompute-Spark\spark-2.x\target\spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar

## 当看到以下日志则表明冒烟作业成功
19/06/11 11:57:30 INFO Client: 
         client token: N/A
         diagnostics: N/A
         ApplicationMaster host: 11.222.166.90
         ApplicationMaster RPC port: 38965
         queue: queue
         start time: 1560225401092
         final status: SUCCEEDED
```