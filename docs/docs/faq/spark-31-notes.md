# Spark 3.1.1 使用注意事项
## 如何使用Spark 3.1.1提交作业
* 直接使用Yarn-cluster模式在本地提交任务

* 通过DataWorks平台选择Spark 3.x选项。若提交任务报错，则需要提单升级独享资源组版本。

## Spark 3.1.1 使用变化
* 如果使用Yarn-cluster模式从本地提交任务，需要新增环境变量 export HADOOP_CONF_DIR=$SPARK_HOME/conf

* 如果使用Yarn-cluster模式提交Pyspark作业，需要添加以下参数使用Python3
```
spark.hadoop.odps.cupid.resources = public.python-3.7.9-ucs4.tar.gz
spark.pyspark.python = ./public.python-3.7.9-ucs4.tar.gz/python-3.7.9-ucs4/bin/python3
```

* 如果使用local模式进行调试，需要在类路径下新建odps.conf文件，并添加以下配置：
```
odps.project.name = 
odps.access.id = 
odps.access.key =
odps.end.point =
```

* 如果使用local模式进行调试，需要添加spark.hadoop.fs.defaultFS = file:///
```
val spark = SparkSession
  .builder()
  .config("spark.hadoop.fs.defaultFS", "file:///")
  .enableHiveSupport()
  .getOrCreate()
```

## Spark 3.1.1 参数配置

* `spark.sql.defaultCatalog`
  + **配置值** `odps`
* `spark.sql.catalog.odps`
  + **配置值** `org.apache.spark.sql.execution.datasources.v2.odps.OdpsTableCatalog`
* `spark.sql.sources.partitionOverwriteMode`
  + **配置值** `dynamic`
* `spark.sql.extensions`
  + **配置值** `org.apache.spark.sql.execution.datasources.v2.odps.extension.OdpsExtensions`
* `spark.sql.catalog.odps.enableVectorizedReader`
  + **默认值** `true`
  + **配置说明**  `开启向量化读`
* `spark.sql.catalog.odps.enableVectorizedWriter`
  + **默认值** `true`
  + **配置说明**  `开启向量化写`
* `spark.sql.catalog.odps.splitSizeInMB`
  + **默认值** `256`
  + **配置说明**  `该配置可以用来调节读Maxcompute表的并发度，默认每个分区为256MB`