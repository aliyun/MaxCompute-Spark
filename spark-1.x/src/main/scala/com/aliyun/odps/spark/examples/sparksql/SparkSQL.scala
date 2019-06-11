/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  * <p>
  * http://www.apache.org/licenses/LICENSE-2.0
  * <p>
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.aliyun.odps.spark.examples.sparksql

import org.apache.spark.sql.odps.OdpsContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * SparkSQL
  * Step 1. build aliyun-cupid-sdk
  * Step 2. properly set spark.defaults.conf
  * Step 3. bin/spark-submit --master yarn-cluster --class com.aliyun.odps.spark.examples.sparksql.SparkSQL \
  * ${ProjectRoot}/spark/spark-1.x/spark-examples/target/spark-examples_2.10-version-shaded.jar
  */
object SparkSQL {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("sparkSQL")
    val sc = new SparkContext(conf)
    val sqlContext = new OdpsContext(sc)
    import sqlContext._

    val project = sc.getConf.get("odps.project.name")

    //init and test drop table
    try {
      sql("DROP TABLE IF EXISTS spark_sql_test_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_new_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_partition_table")
      val df = sql("DROP TABLE IF EXISTS spark_sql_test_partition_table_copy")
      df.show()
      println("======= init success ======")
    } catch {
      case e: Throwable =>
        println("======= init failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test drop database : throw java.lang.UnsupportedOperationException
    try {
      sql("DROP DATABASE IF EXISTS " + project)
      println("======= test drop database failed ======")
      throw new RuntimeException
    } catch {
      case e: Throwable =>
        println("======= test drop database success ======")
    }

    //test create database: throw java.lang.UnsupportedOperationException
    try {
      sql("CREATE DATABASE IF NOT EXISTS " + project)
      println("======= test create database failed ======")
      throw new RuntimeException
    } catch {
      case ex: Throwable =>
        println("======= test create database success ======")
    }

    //test create table and desc table
    try {
      sql("CREATE TABLE spark_sql_test_table(name STRING, num BIGINT)")
      sql("CREATE TABLE spark_sql_test_partition_table(name STRING, num BIGINT) PARTITIONED BY (p1 STRING, p2 STRING)")
      sql("DESCRIBE spark_sql_test_table")
      sql("DESCRIBE spark_sql_test_partition_table")
      println("======= test create table success ======")
    } catch {
      case e: Throwable =>
        println("======= test create table failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test insert into/overwrite
    try {
      sql("INSERT INTO TABLE spark_sql_test_table SELECT 'abc', 100000")
      val data1 = sql("SELECT * FROM spark_sql_test_table").collect
      assert(data1.length == 1)
      assert(data1(0).get(0) == "abc")
      assert(data1(0).get(1) == 100000)

      val count1 = sql("SELECT COUNT(*) FROM spark_sql_test_table").collect
      assert(count1(0).get(0) == 1)

      sql("INSERT OVERWRITE TABLE spark_sql_test_table SELECT 'abcd', 200000")
      val data2 = sql("SELECT * FROM spark_sql_test_table").collect
      assert(data2.length == 1)
      assert(data2(0).get(0) == "abcd")
      assert(data2(0).get(1) == 200000)

      val count2 = sql("SELECT COUNT(*) FROM spark_sql_test_table").collect
      assert(count2(0).get(0) == 1)

      sql("INSERT INTO TABLE spark_sql_test_table SELECT 'aaaa', 140000")
      sql("INSERT INTO TABLE spark_sql_test_table SELECT 'bbbb', 160000")
      val data3 = sql("SELECT * FROM spark_sql_test_table order by num").collect
      assert(data3.length == 3)
      assert(data3(0).get(0) == "aaaa")
      assert(data3(0).get(1) == 140000)
      assert(data3(1).get(0) == "bbbb")
      assert(data3(1).get(1) == 160000)
      assert(data3(2).get(0) == "abcd")
      assert(data3(2).get(1) == 200000)

      println("======= test insert into and insert overwrite success ======")
    } catch {
      case e: Throwable =>
        println("======= test insert into and insert overwrite failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test show columns
    try {
      val tables = sql("SHOW TABLES").collect()
      tables foreach println
      assert(tables.size > 0)
      println("======= test show tables success ======")
    } catch {
      case e: Throwable =>
        println("======= test show tables failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test insert into/overwrite partition
    try {
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 100")
      sql("INSERT OVERWRITE TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 160")
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 200")
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 300")
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 400")
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 500")
      sql("INSERT INTO TABLE spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 600")
      val data = sql("SELECT * FROM spark_sql_test_partition_table order by num").collect()
      assert(data.length == 6)
      assert(data(0).get(0) == "hz")
      assert(data(0).get(1) == 160)
      assert(data(0).get(2) == "2017")
      assert(data(0).get(3) == "hangzhou")
      assert(data(3).get(0) == "hz")
      assert(data(3).get(1) == 400)
      assert(data(3).get(2) == "2017")
      assert(data(3).get(3) == "hangzhou")
      println("======= test insert into/overwrite partition success ======")
    } catch {
      case e: Throwable =>
        println("======= test insert into/overwrite partition failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test create table as select
    try {
      sql("CREATE TABLE spark_sql_test_partition_table_copy AS SELECT * FROM spark_sql_test_partition_table")
      val data = sql("SELECT * FROM spark_sql_test_partition_table_copy order by num").collect()
      assert(data.length == 6)
      assert(data(0).get(0) == "hz")
      assert(data(0).get(1) == 160)
      assert(data(0).get(2) == "2017")
      assert(data(0).get(3) == "hangzhou")
      assert(data(3).get(0) == "hz")
      assert(data(3).get(1) == 400)
      assert(data(3).get(2) == "2017")
      assert(data(3).get(3) == "hangzhou")
      println("======= test create table as select success ======")
    } catch {
      case e: Throwable =>
        println("======= test create table as select failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test register udf
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
        println("======= test register udf failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test drop table
    try {
      sql("create table if not exists spark_sql_test_new_table(id string)")
      val tbls1 = sql("show tables").collect
      tbls1 foreach println
      assert(tbls1.exists(x => x.toString().contains("spark_sql_test_new_table")))
      sql("DROP TABLE spark_sql_test_new_table")
      val tbls2 = sql("show tables").collect
      assert(!tbls2.exists(x => x.toString().contains("spark_sql_test_new_table")))
      println("======= test drop table success ======")
    } catch {
      case e: Throwable =>
        println("======= test drop table failed ======")
        e.printStackTrace(System.out)
        throw e
    }
  }
}
