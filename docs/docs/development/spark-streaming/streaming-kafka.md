# Streaming读写kafka
MaxCompute支持Spark Streaming和Spark Structured Streaming，本文介绍Streaming作业流式读写kafka的示例。

## Spark Streaming(DStream)
该示例是基于一个Kafka的Receiver，适用于DStream的接口。

> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/kafka/KafkaStreamingDemo.scala

## Kafka回流到MaxCompute
通过DStreaming+Dataframe把Kafka数据导入MaxCompute

> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/streaming/kafka/Kafka2OdpsDemo.scala

## Spark Structured Streaming
> 详细代码请参考：https://github.com/aliyun/MaxCompute-Spark/blob/master/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/structuredStreaming/kafka/KafkaStructuredStreamingDemo.scala

source的示例如下(请参考代码):
```
val df = spark
        .readStream
        .format("kafka")
        .option("kafka.bootstrap.servers", "192.168.72.224:9202,192.168.72.225:9202,192.168.72.226:9202")
        .option("subscribe", "zkytest")
        .load()
```

由于kafka在VPC内，需要将endpoint配置在VPC访问配置中，参考[VPC访问](https://github.com/aliyun/MaxCompute-Spark/wiki/09.-VPC-Access%E6%96%87%E6%A1%A3%E8%AF%B4%E6%98%8E)。示例如下：
```
{
  "regionId":"cn-beijing",
  "vpcs":[
    {
      "vpcId":"vpc-2zeaeq21mb1dmkqh0exox"
      "zones":[
        {
          "urls":[
            {
              "domain":"192.168.72.224",
              "port":9202
            },
            {
              "domain":"192.168.72.225",
              "port":9202
            },
            {
              "domain":"192.168.72.226",
              "port":9202
            }
          ]
        }
      ]
    }
  ]
}
```

**注意：** 目前所给的这个Demo，没有启用checkpoint，checkpoint需要使用oss作为checkpoint的存储，另外Spark Streaming作业处于试用阶段，**作业最长运行时间不能超过3天，如果需要投入长时间正式运行使用，请联系我们开通相关权限。**
