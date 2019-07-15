package com.aliyun.odps.spark.examples.clientmode

import java.util

import com.aliyun.odps.cupid.client.spark.client.CupidSparkClientRunner


class ClientModeInitApp(cupidSparkClient: CupidSparkClientRunner)
  extends ClientModeBaseApp (cupidSparkClient) {

  def run(jarName: String): String = {
    val configArgs = new util.HashMap[String, String]()
    val cacheSourceRddName = "CachedRDD"
    configArgs.put("CacheSourceRddName", cacheSourceRddName)

    val className = "com.aliyun.odps.spark.examples.clientmode.CacheRDD"
    val cacheRddAndCountJobId =
      cupidSparkClient.startJob(className, jarName, configArgs)
    try {
      val useCacheRddResult = getJobResult[java.lang.String](cacheRddAndCountJobId)
      println("Jobid = " + cacheRddAndCountJobId + ", Result: ")
      println(useCacheRddResult)
      useCacheRddResult
    } catch {
      case e: Exception => {
        println("Jobid = " + cacheRddAndCountJobId + ",printStackTrace is:")
        e.printStackTrace()
        throw e
      }
    }
  }
}
