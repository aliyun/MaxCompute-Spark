package com.aliyun.odps.spark.examples.clientmode

import java.nio.ByteBuffer

import com.aliyun.odps.cupid.client.spark.api.JobStatus.{JobFailed, JobKilled, JobSuccess}
import com.aliyun.odps.cupid.client.spark.client.CupidSparkClientRunner
import com.aliyun.odps.cupid.client.spark.util.serializer.JavaSerializerInstance

import scala.reflect.ClassTag

class ClientModeBaseApp(cupidSparkClient: CupidSparkClientRunner) {

  def getJobResult[T: ClassTag](jobId: String): T = {
    var resultBytes: Array[Byte] = null
    cupidSparkClient.getJobResult(jobId) match {
      case JobSuccess(receivedJobId, msg) =>
        resultBytes = msg
        JavaSerializerInstance.getInstance.deserialize[T](ByteBuffer.wrap(resultBytes))
      case JobFailed(receivedJobId, msg) =>
        throw new Exception("jobid = " + receivedJobId + ",jobStatus = jobfailed," + ",msg=" + msg)
      case JobKilled(receivedJobId) =>
        throw new Exception("jobid = " + receivedJobId + ",jobStatus = jobkilled")
    }
  }
}
