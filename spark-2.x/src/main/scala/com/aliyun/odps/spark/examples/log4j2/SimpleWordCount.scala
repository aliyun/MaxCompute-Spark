package com.aliyun.odps.spark.examples.log4j2

import com.aliyun.odps.spark.examples.utils.ConfigLog4j2
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object SimpleWordCount extends Logger {
  def main(args: Array[String]): Unit = {

    ConfigLog4j2.initPackageLogger("com.aliyun.odps.spark.examples.log4j2")
    val spark: SparkSession = SparkSession
      .builder()
      .appName("WordCount")
      .getOrCreate()

    log.info("My Test!")
    val wordList = List("Hello", "World", "Hello")
    val rdd: RDD[String] = spark.sparkContext.parallelize(Seq(wordList: _*)).cache()
    val resultRDD: RDD[(String, Int)] = rdd.map(w => (w, 1)).reduceByKey(_ + _)
    resultRDD.collect().foreach(v => {
      log.info(s"${v._1} has num ${v._2}")
    })

    spark.stop()
  }
}
