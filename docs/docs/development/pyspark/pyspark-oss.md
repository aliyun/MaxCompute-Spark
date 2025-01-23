# PySpark 访问 Oss
## 参数配置
- 首先需要参考[文档](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)配置ossid和key：
```
spark.hadoop.fs.oss.accessKeyId = xxxxxx
spark.hadoop.fs.oss.accessKeySecret = xxxxxx
spark.hadoop.fs.oss.endpoint = oss-xxxxxx-internal.aliyuncs.com
```

- 配置Hadoop实现类（二选一即可）
```
### 使用jindo sdk（推荐方式，性能更优）
spark.hadoop.fs.AbstractFileSystem.oss.impl=com.aliyun.emr.fs.oss.OSS
spark.hadoop.fs.oss.impl=com.aliyun.emr.fs.oss.JindoOssFileSystem

### 使用hadoop-fs-oss
spark.hadoop.fs.oss.impl=org.apache.hadoop.fs.aliyun.oss.AliyunOSSFileSystem
```

- 【公共云】需要引用hadoop oss依赖，添加以下配置（二选一即可）：
```
### 使用jindo sdk（推荐方式，性能更优）
spark.hadoop.odps.cupid.resources=public.jindofs-sdk-3.7.2.jar

### 使用hadoop-fs-oss 
spark.hadoop.odps.cupid.resources=public.hadoop-fs-oss-shaded.jar 
```

- 【专有云】需要引用hadoop-fs-oss.jar包，需要按照以下步骤上传资源并添加配置：
```
（1）下载hadoop-fs-oss.jar包，下载地址(https://odps-repo.oss-cn-hangzhou.aliyuncs.com/spark/hadoop-fs-oss-shaded.jar)
（2）将jar包上传为MaxCompute资源，参考文档（https://help.aliyun.com/document_detail/27831.html?spm=a2c4g.27797.0.i1#section-533-s8q-d9w）
（3）添加参数：spark.hadoop.odps.cupid.resources=<projectname>.hadoop-fs-oss-shaded.jar 
```

- 需要注意：如果已经配置过spark.hadoop.odps.cupid.resources这个参数，则引用多个资源需要用逗号隔开，参考[文档](https://github.com/aliyun/MaxCompute-Spark/wiki/03.-Spark%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3#maxcompute%E6%95%B0%E6%8D%AE%E4%BA%92%E9%80%9A%E9%85%8D%E7%BD%AE)

## 例子1：判断oss文件是否存在
```
from pyspark.sql import SparkSession

spark = SparkSession.builder.appName('testoss').getOrCreate()
sc = spark.sparkContext
conf = sc._jsc.hadoopConfiguration()
conf.set("fs.oss.accessKeyId", "xxxx")
conf.set("fs.oss.accessKeySecret", "xxx")
conf.set("fs.oss.endpoint", "xxxx")
conf.set("fs.oss.impl", "org.apache.hadoop.fs.aliyun.oss.AliyunOSSFileSystem")

path = sc._jvm.org.apache.hadoop.fs.Path("oss://xxxxx")
fs = sc._jvm.org.apache.hadoop.fs.FileSystem.get(path.toUri(), conf)
exist = fs.exists(path)
```


## 例子2：写oss
```
spark = SparkSession.builder.appName('testoss').getOrCreate()
data = [i for i in range(0, 100)]
df = spark.sparkContext.parallelize(data, 2).map(lambda s: ("name-%s" % s, s)).toDF("name: string, num: int")
df.show(n=10)
## write to oss
pathout = 'oss://[替换为实际Bucket]/test.csv'
df.write.csv(pathout)
```
