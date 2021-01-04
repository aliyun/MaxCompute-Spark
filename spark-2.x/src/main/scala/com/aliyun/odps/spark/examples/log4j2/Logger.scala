package com.aliyun.odps.spark.examples.log4j2

import org.apache.logging.log4j
import org.apache.logging.log4j.LogManager

trait Logger {
  val log: log4j.Logger = LogManager.getLogger(this.getClass)
  log
}
