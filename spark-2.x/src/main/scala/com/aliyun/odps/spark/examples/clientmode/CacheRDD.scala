package com.aliyun.odps.spark.examples.clientmode

import com.aliyun.odps.cupid.client.spark.api.{JobContext, SparkJob}
import com.aliyun.odps.cupid.client.spark.util.serializer.JavaSerializerInstance
import com.typesafe.config.Config

object CacheRDD extends SparkJob {

  override def runJob(jobContext: JobContext, jobConf: Config): Array[Byte] = {
    val parallRdd = jobContext.sc().parallelize(Seq[Int](1, 2, 3), 1)
    val cacheRDDName = jobConf.getString("CacheSourceRddName")
    println(s"CacheRDD: CacheSourceRddName: $cacheRDDName")
    jobContext.putNamedObject(cacheRDDName, parallRdd)
    JavaSerializerInstance.getInstance.serialize(cacheRDDName).array()
  }
}
