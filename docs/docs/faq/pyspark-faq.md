# PySpark 常见问题
## Local 模式（Spark 2.4.5）

- 新建一个odps.conf文件，包含以下odps参数：
```
odps.project.name=***
odps.access.id=***
odps.access.key=***
odps.end.point=***
```

- 在PyCharm中添加以下环境变量：
```
SPARK_HOME=/path/to/spark_home
PYTHONPATH=/path/to/spark_home/python
ODPS_CONF_FILE=/path/to/odps.conf
```

- 在代码中添加以下配置：
```
spark = SparkSession.builder\
   .appName("spark sql")\
   .config("spark.eventLog.enabled", False)\
   .getOrCreate()
```

- 直接运行pyspark作业即可

## Cluster 模式（Spark 2.4.5）

#### 作业执行抛出异常：***.so: cannot open shared object file: No such file or directory

上述抛出的异常，提示用户作业在执行加载时缺少对应的依赖，具体解决步骤如下：
##### MaxCompute Spark客户端
* 公网下载对应的依赖文件
* 提交作业时通过参数 **--files /path/to/[lib名]** 将对应的依赖文件加载至driver与executor的工作目录内

##### Dataworks Spark节点
* 公网下载对应的依赖文件
* 通过DataWorks，添加对应的依赖资源，即，创建MaxCompute资源
* 作业提交新增补充参数，spark.hadoop.odps.cupid.resources = public.python-2.7.13-ucs4.tar.gz,[project名].[resource名].so:[resource名].so，

###### 注意事项
```
由于上传的依赖资源是以project名称为前缀，所以需要对上传的resource名称进行重命名为需要的依赖，即，去掉project名称的前缀，这样才可以正确加载依赖
```
