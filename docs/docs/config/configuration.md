# Spark配置详解
## MaxCompute账号相关配置

* `spark.hadoop.odps.project.name`
  + **默认值** `无`
  + **配置说明**   `MaxCompute项目名称`
* `spark.hadoop.odps.access.id`
  + **默认值** `无`
  + **配置说明**   `MaxCompute项目accessId`
* `spark.hadoop.odps.access.key`
  + **默认值** `无`
  + **配置说明**   `MaxCompute项目accessKey`
* `spark.hadoop.odps.access.security.token`
  + **默认值** `无`
  + **配置说明**   `MaxCompute项目STS Token`
* `spark.hadoop.odps.end.point`
  + **建议值** 可以采用中国公共云通用外网endpoint：`http://service.cn.maxcompute.aliyun.com/api`，也可以采用各自region独享的endpoint，参考文档[外网Endpoint](https://help.aliyun.com/document_detail/34951.html?spm=5176.11065259.1996646101.searchclickresult.58c77a0dlXCR54)
  + **配置说明**   `MaxCompute项目endPoint`

## Spark版本配置
* `spark.hadoop.odps.spark.version`
  + **默认值** `spark-2.3.0-odps0.33.0，如果使用spark-2.4.5, 请将该参数设置为spark-2.4.5-odps0.34.0，如果使用spark-3.1.1, 请将该参数设置为spark-3.1.1-odps0.34.1`
  + **配置说明**   `该值指定了提交spark任务所用的spark版本`
  + **注意**  `可以通过该配置切换到spark-2.4.5/spark-3.1.1`

* `spark.hadoop.odps.spark.libs.public.enable`
  + **默认值** `false`
  + **配置说明**   `设置为true之后，可以免上传jars，直接从服务端拉取，加速上传`
  + **注意**  `需要同时配置spark.hadoop.odps.spark.version指定版本后才能生效`

## 资源申请相关配置

* `spark.executor.instances`
  + **默认值** `1`
  + **配置说明**   `executor worker个数`
* `spark.executor.cores`
  + **默认值** `1`
  + **配置说明**   `executor worker核数`
* `spark.executor.memory`
  + **默认值** `2g`
  + **配置说明**   `executor worker内存`
* `spark.driver.cores`
  + **默认值** `1`
  + **配置说明**   `driver核数`
* `spark.driver.memory`
  + **默认值** `2g`
  + **配置说明**   `driver内存`
* `spark.master`
  + **默认值** `yarn-cluster`
  + **配置说明**   `作业提交运行方式，目前支持yarn-cluster以及local[N]`
* `spark.yarn.executor.memoryOverhead`
  + **默认值** `参考社区配置`
  + **配置说明**   `当堆外内存使用比较多时建议提高此值避免整体内存超出被Kill` 
  + **注意**  `单个executor的内存总量是spark.executor.memory+spark.yarn.executor.memoryOverhead`
* `spark.yarn.driver.memoryOverhead`
  + **默认值** `参考社区配置`
  + **配置说明**   `当堆外内存使用比较多时建议提高此值避免整体内存超出被Kill`
  + **注意**  `driver的内存总量是spark.driver.memory+spark.yarn.driver.memoryOverhead`
* `spark.hadoop.odps.cupid.disk.driver.device_size`
  + **默认值** `20g`
  + **配置说明**   `本地网盘大小，当出现No space left on device时可适当调大该值，最大支持100g`
  + **注意**  `注意：必须配置在spark-conf文件或者dataworks的配置项中，不能配置在代码中`

## MaxCompute数据互通配置

* `spark.sql.catalogImplementation`
  + **配置说明**   `spark 2.3.0 需要设置为odps，spark 2.4.5及以上的版本需要设置hive`
* `spark.hadoop.odps.cupid.resources`
  + **配置说明** `该配置项指定了任务运行所需要的`[Maxcompute资源](https://help.aliyun.com/document_detail/27831.html?spm=5176.11065259.1996646101.searchclickresult.d55650ea0QU1qd&aly_as=45TiiTdO2)，`格式为<projectname>.<resourcename>，可指定多个，逗号分隔`
  + **配置示例** spark.hadoop.odps.cupid.resources=public.python-python-2.7-ucs4.zip,public.myjar.jar
  + **使用说明** `指定的资源将被下载到driver和executor的当前工作目录，资源下载到工作目录后默认的名字是<projectname>.<resourcename>`
  + **文件重命名** `在配置时通过<projectname>.<resourcename>:<newresourcename>进行重命名`
  + **重命名示例** spark.hadoop.odps.cupid.resources=public.myjar.jar:myjar.jar
  + **注意** `该配置项必须要配置在spark-default.conf中或dataworks的配置项中才能生效，而不能写在代码中`
* `spark.hadoop.odps.cupid.vectorization.enable`
  + **建议值** `true`
  + **配置说明**   `当设置为true时，会应用批读写优化，读写数据性能显著提升。
* `spark.hadoop.odps.input.split.size`
  + **默认值** `256`
  + **配置说明**   `该配置可以用来调节读Maxcompute表的并发度，默认每个分区为256MB


## OSS相关配置

* `spark.hadoop.fs.oss.endpoint`
  + **建议值** `无`
  + **配置说明**   `阿里云OSS控制台上可查看Bucket对应的endpoint`
* `spark.hadoop.fs.oss.ststoken.roleArn`
  + **建议值** `无`
  + **配置说明**   `StsToken授权方式`
* `spark.hadoop.fs.oss.credentials.provider`
  + **建议值** `org.apache.hadoop.fs.aliyun.oss.AliyunStsTokenCredentialsProvider`
  + **配置说明**   `StsToken授权方式`

[OSS StsToken授权步骤](https://github.com/aliyun/MaxCompute-Spark/wiki/08.-Oss-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)

## VPC服务访问相关配置

* `spark.hadoop.odps.cupid.vpc.domain.list`
  + **建议值** `无`
  + **配置说明**   `参见以下JSON格式 配置值为压缩去除空格后的字符串`
  + **压缩为字符串网址**    http://www.bejson.com/

```
See. http://www.bejson.com/
粘贴VPC Domain List内容并选择压缩得到压缩于一行的字符串作为spark.hadoop.odps.cupid.vpc.domain.list的配置值
{
    "regionId": "cn-beijing",
    "vpcs": [
        {
            "vpcId": "vpc-2zeaeq21mb1dmkqh0exox",
            "zones": [
                {
                    "urls": [
                        {
                            "domain": "zky-test",
                            "port": 9092
                        }
                    ],
                    "zoneId": "9b7ce89c6a6090e114e0f7c415ed9fef"
                }
            ]
        }
    ]
}
```
* `spark.hadoop.odps.cupid.pvtz.rolearn`
  + **建议值** `acs:ram::********:role/aliyunodpsdefaultrole`
  + **配置说明**   `当spark作业需要访问云上其他VPC域内服务，比如redis、mysql、kafka等等需要配置该参数`
* `spark.hadoop.odps.cupid.smartnat.enable`
  + **配置说明**   `北京和上海region需要配置该参数为true`    

[VPC访问文档说明](https://github.com/aliyun/MaxCompute-Spark/wiki/09.-VPC-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)

## 流式作业相关配置

* `spark.hadoop.odps.cupid.engine.running.type`
  + **建议值** `longtime`
  + **配置说明**   `普通作业3天没跑完就会被强制回收，流式作业需要设置此值`
* `spark.hadoop.odps.cupid.job.capability.duration.hours`
  + **建议值** `8640`
  + **配置说明**   `流式作业权限文件expired时间，单位小时`
* `spark.hadoop.odps.moye.trackurl.dutation`
  + **建议值** `8640`
  + **配置说明**   `流式作业jobview expired时间，单位小时`
* `spark.yarn.maxAppAttempts`
  + **建议值** `5`
  + **配置说明**   `流式作业failover次数限制`
* `spark.yarn.am.maxAttemptValidityInterval`
  + **建议值** `1h`
  + **配置说明**   `流式作业failover次数限制窗口验证`

## 灰度相关配置

* `spark.hadoop.odps.task.major.version`
  + **建议值** `default`

## 隔离相关配置

* `spark.hadoop.odps.cupid.container.image.enable`
  + **建议值** `true`
  + **配置说明**   `安全隔离相关配置请保持默认值，专有云需要去掉该配置`
* `spark.hadoop.odps.cupid.container.vm.engine.type`
  + **建议值** `hyper`
  + **配置说明**   `安全隔离相关配置请保持默认值，专有云需要去掉该配置`
