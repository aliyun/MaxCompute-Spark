package com.aliyun.odps.spark.examples.clientmode

import com.aliyun.odps.cupid.client.spark.api.{JobContext, SparkJob}
import com.aliyun.odps.cupid.client.spark.util.serializer.JavaSerializerInstance
import com.typesafe.config.Config
import org.apache.spark.rdd.RDD

object UseCachedRDD extends SparkJob{

  override def runJob(jobContext: JobContext, jobConf: Config): Array[Byte] = {
    val cacheRDDName = jobConf.getString("CacheSourceRddName")
    val parallRdd = jobContext.getNamedObject(cacheRDDName).asInstanceOf[RDD[Int]]
    println(s"UseCachedRDD: CacheSourceRddName: $cacheRDDName")
    val sum = parallRdd.reduce(_ + _)
    println(s"sum: $sum")
    JavaSerializerInstance.getInstance.serialize(sum).array()
  }
}
