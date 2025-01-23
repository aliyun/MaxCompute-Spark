新版本Spark支持读取ACID表，需要添加以下参数切换到新版本

### Spark 2.3.0
<pre>
spark.hadoop.odps.task.major.version = default
spark.hadoop.odps.cupid.resources = public.__spark_libs__2.3.0-odps0.34.0.zip
spark.driver.extraClassPath = ./public.__spark_libs__2.3.0-odps0.34.0.zip/* 
spark.executor.extraClassPath = ./public.__spark_libs__2.3.0-odps0.34.0.zip/*
</pre>

### Spark 2.4.5
<pre>
spark.hadoop.odps.task.major.version = default
spark.hadoop.odps.cupid.resources = public.__spark_libs__2.4.5-odps0.34.0.zip
spark.driver.extraClassPath = ./public.__spark_libs__2.4.5-odps0.34.0.zip/* 
spark.executor.extraClassPath = ./public.__spark_libs__2.4.5-odps0.34.0.zip/*
</pre>