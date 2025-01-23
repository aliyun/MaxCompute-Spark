# Streaming读写LogHub
MaxCompute支持Spark Streaming和Spark Structured Streaming，本文介绍Streaming作业流式接收LogHub(日志服务的一个组件，日志服务详见[官方文档](https://www.aliyun.com/product/sls))的示例。

## Spark Streaming(DStream)
该示例是基于一个LogHub的Receiver（类似基于Spark之上接收Kafka流的Receiver），适用于DStream的接口。

> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/loghub/LogHubStreamingDemo.scala

运行这个Demo，需要在Spark的配置中给出LogHub的如下几个配置：

```
spark.logservice.accessKeyId : loghub的accessId
spark.logservice.accessKeySecret : loghub的accessKey
spark.logservice.endpoint : loghub的endpoint，需要根据project所在的region进行选择
spark.logservice.project : 需要读取的loghub的project名字
spark.logservice.logstore : 需要读取的logstore的名字
```
另外StreamingParam#setCursor(LogHubCursorPosition.END_CURSOR) 和 StreamingParam#setGroup("test") 这俩配置的含义可以参考[LogHub官方文档的介绍](https://help.aliyun.com/document_detail/28998.html?spm=a2c4g.11186623.6.877.2ea24bbcd6eDg5)。 

## LogHub回流到MaxCompute
利用DStream+Dataframe可以把LogHub数据回流到MaxCompute。

> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/loghub/LogHub2OdpsDemo.scala

## Spark Structured Streaming
> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/structuredStreaming/loghub/LoghubStructuredStreamingDemo.scala

source的示例如下(请参考代码):
```
val stream = spark.readStream
  .format("loghub")
  .option("loghub.endpoint", "http://....")
  .option("loghub.project", "project")
  .option("loghub.logstores", "store1,store2")
  .option("loghub.accessId", "accessId")
  .option("loghub.accessKey", "accessKey")
  .option("loghub.startingoffsets", "latest")
  .load()
```

sink的示例如下:
```
val query = spark.writeStream
  .format("loghub")
  .option("loghub.endpoint", "http://....")
  .option("loghub.project", "project")
  .option("loghub.logstores", "store1,store2")
  .option("loghub.accessId", "accessId")
  .option("loghub.accessKey", "accessKey")
  .start()
```

其中loghub.endpoint请使用**经典/VPC网络服务入口**，各region对应的endpoint参考[此文](https://help.aliyun.com/document_detail/29008.html#h2-url-2)。此外，需要将endpoint配置在VPC访问配置中，参考[VPC访问](https://github.com/aliyun/MaxCompute-Spark/wiki/09.-VPC-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)。示例如下：
```
{
  "regionId":"cn-beijing",
  "vpcs":[
    {
      "zones":[
        {
          "urls":[
            {
              "domain":"cn-beijing-intranet.log.aliyuncs.com",
              "port":80
            }
          ]
        }
      ]
    }
  ]
}
```

**注意：** 目前所给的这个Demo，没有启用checkpoint，checkpoint需要使用oss作为checkpoint的存储，另外Spark Streaming作业处于试用阶段，**作业最长运行时间不能超过3天，如果需要投入长时间正式运行使用，请联系我们开通相关权限。**
