# Jindo sdk接入说明
参考[jindo-sdk的说明](https://github.com/aliyun/alibabacloud-jindodata/blob/master/docs/spark/jindosdk_on_spark.md)，jindo-sdk接入有如下几个步骤。

- spark默认使用hadoop-oss，增加特殊配置项才可以改为使用jindo-sdk。
- 设置访问OSS需要的配置
- 部署spark应用。

> jindo-sdk 相比于hadoop-oss 使用更多的本地磁盘空间，如果出现*No space left on device*，可以调整`spark.hadoop.odps.cupid.disk.driver.device_size`增大本地磁盘空间。

## 引用jindo-sdk

修改spark-defaults.conf增加配置项，增加spark.hadoop.odps.cupid.resources配置。使用外部文件的方法参考[引用外部文件](https://github.com/aliyun/MaxCompute-Spark/wiki/06.-%E5%BC%95%E7%94%A8%E5%A4%96%E9%83%A8%E6%96%87%E4%BB%B6%E9%97%AE%E9%A2%98)，样例配置如下：

```text
spark.hadoop.odps.cupid.resources = public.jindofs-sdk-3.7.2.jar
```

## 使用jindo-sdk

在`SparkConf`中设置`spark.hadoop.fs.AbstractFileSystem.oss.impl`及`spark.hadoop.fs.oss.impl`, 样例代码如下：

```scala
val conf = new SparkConf()
  .setAppName("jindo-sdk-demo")
  .set("spark.hadoop.fs.AbstractFileSystem.oss.impl", "com.aliyun.emr.fs.oss.OSS")
  .set("spark.hadoop.fs.oss.impl", "com.aliyun.emr.fs.oss.JindoOssFileSystem")
```

## 配置OSS

涉及到的配置项有Oss Endpoint和Oss鉴权参数，参考[访问OSS](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)获得合法的Endpoint值和OSS鉴权参数值。OSS鉴权有两种方式AccessKey鉴权及云服务角色扮演，不同鉴权方式需要使用不同的鉴权参数。


## 使用AccessKey鉴权

spark-defaults.conf无需变更, `SparkConf`中设置`spark.hadoop.fs.oss.endpoint`、`spark.hadoop.fs.oss.accessKeyId`、`spark.hadoop.fs.oss.accessKeySecret`。

```scala
val conf = new SparkConf()
  .setAppName("jindo-sdk-demo")
  .set("spark.hadoop.fs.AbstractFileSystem.oss.impl", "com.aliyun.emr.fs.oss.OSS")
  .set("spark.hadoop.fs.oss.impl", "com.aliyun.emr.fs.oss.JindoOssFileSystem")

  # 配置endpoint
  .set("spark.hadoop.fs.oss.endpoint", "endpoint-value")

  # 配置access-key鉴权参数
  .set("spark.hadoop.fs.oss.accessKeyId", "xxx")
  .set("spark.hadoop.fs.oss.accessKeySecret", "xxx")
```

## 使用云服务角色鉴权
云服务角色描述字符串格式为`acs:ram::12345678:role/${role-name}`，其中纯数字部分'12345678'是aliyun-uid，斜线后面的字符串是角色名称。这两个值需要配置在spark应用里。

spark-defaults.conf需要添加`spark.hadoop.odps.cupid.http.server.enable`, 如下：
```text
spark.hadoop.odps.cupid.http.server.enable = true
```

`SparkConf`中设置`spark.hadoop.odps.cupid.http.server.enable`、`spark.hadoop.fs.jfs.cache.oss.credentials.provider`、`spark.hadoop.aliyun.oss.provider.url`, 样例代码如下:

```scala
val conf = new SparkConf()
  .setAppName("jindo-sdk-demo")
  .set("spark.hadoop.fs.AbstractFileSystem.oss.impl", "com.aliyun.emr.fs.oss.OSS")
  .set("spark.hadoop.fs.oss.impl", "com.aliyun.emr.fs.oss.JindoOssFileSystem")

  # 配置endpoint
  .set("spark.hadoop.fs.oss.endpoint", "endpoint-value")

  # 配置云服务角色鉴权
  # ${aliyun-uid}是阿里云用户UID
  # ${role-name}是角色名称
  .set("spark.hadoop.fs.jfs.cache.oss.credentials.provider", "com.aliyun.emr.fs.auth.CustomCredentialsProvider")
  .set("spark.hadoop.aliyun.oss.provider.url", "http://localhost:10011/sts-token-info?user_id=${aliyun-uid}&role=${role-name}")
```

## 打包上传

```shell
./bin/spark-submit --class xxx spark-app.jar
```


