package com.aliyun.odps.spark.examples.clientmode

import java.util

import com.aliyun.odps.cupid.client.spark.client.CupidSparkClientRunner

class ClientModeProcessApp(cupidSparkClient: CupidSparkClientRunner)
  extends ClientModeBaseApp(cupidSparkClient) {
  def run(jarName: String): String = {
    val configArgs = new util.HashMap[String, String]()
    val cacheSourceRddName = "CachedRDD"
    configArgs.put("CacheSourceRddName", cacheSourceRddName)

    val className = "com.aliyun.odps.spark.examples.clientmode.UseCachedRDD"
    val cacheRddAndCountJobId =
      cupidSparkClient.startJob(className, jarName, configArgs)
    try {
      val useCacheRddResult = getJobResult[Int](cacheRddAndCountJobId)
      println("Jobid = " + cacheRddAndCountJobId + ", Result: ")
      println(useCacheRddResult)
      useCacheRddResult.toString
    } catch {
      case e: Exception => {
        println("Jobid = " + cacheRddAndCountJobId + ",printStackTrace is:")
        e.printStackTrace()
        throw e
      }
    }
  }
}
