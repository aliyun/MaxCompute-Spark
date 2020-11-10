package com.aliyun.odps.spark.examples.udf

import org.apache.spark.sql.odps.OdpsContext
import org.apache.spark.{SparkConf, SparkContext}

object SparkUDF {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("sparkUDF")
    val sc = new SparkContext(conf)

    val sqlContext = new OdpsContext(sc)
    import sqlContext._

    sql("DROP TABLE IF EXISTS spark_sql_test_partition_table")
    sql("CREATE TABLE spark_sql_test_partition_table(name STRING, num BIGINT) PARTITIONED BY (p1 STRING, p2 STRING)")

    sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2020',p2='hangzhou') SELECT 'hz', 400")
    sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2020',p2='shanghai') SELECT 'sh', 500")
    sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2020',p2='hangzhou') SELECT 'hz', 600")

    try {
      udf.register("myUpper", (input: String) => input.toUpperCase)
      val funcs = sql("SHOW FUNCTIONS myupper").collect()
      funcs foreach println
      assert(funcs.length == 1)
      val data = sql("SELECT myupper(name) FROM spark_sql_test_partition_table WHERE name = 'hz'").collect()
      assert(data(0).get(0) == "HZ")
      println("======= test register udf success ======")
    } catch {
      case e: Throwable =>
        e.printStackTrace(System.out)
        throw e
    }
  }
}

