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

import org.apache.spark.sql.{SaveMode, SparkSession}

object SparkSQL {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .appName("SparkSQL-on-MaxCompute")
      .config("spark.sql.broadcastTimeout", 20 * 60)
      .config("spark.sql.crossJoin.enabled", true)
      .config("spark.sql.defaultCatalog","odps")
      .config("spark.sql.catalog.odps", "org.apache.spark.sql.execution.datasources.v2.odps.OdpsTableCatalog")
      .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
      .config("spark.sql.extensions", "org.apache.spark.sql.execution.datasources.v2.odps.extension.OdpsExtensions")
      .config("spark.sql.catalogImplementation","hive")
      .getOrCreate()

    import spark._
    import sqlContext.implicits._
    val tableName = "mc_test_table"
    val ptTableName = "mc_test_pt_table"
    // Drop Create
    sql(s"DROP TABLE IF EXISTS ${tableName}")
    sql(s"DROP TABLE IF EXISTS ${ptTableName}")

    sql(s"CREATE TABLE ${tableName} (name STRING, num BIGINT)")
    sql(s"CREATE TABLE ${ptTableName} (name STRING, num BIGINT) PARTITIONED BY (pt1 STRING, pt2 STRING)")

    val df = spark.sparkContext.parallelize(0 to 99, 2).map(f => {
      (s"name-$f", f)
    }).toDF("name", "num")

    val ptDf = spark.sparkContext.parallelize(0 to 99, 2).map(f => {
      (s"name-$f", f, "2018", "0601")
    }).toDF("name", "num", "pt1", "pt2")

    // 写 普通表
    df.write.insertInto(tableName) // insertInto语义
    df.writeTo(tableName).overwritePartitions() // insertOverwrite use datasourceV2

    // 写 分区表
    // DataFrameWriter 无法指定分区写入 需要通过临时表再用SQL写入特定分区
    df.createOrReplaceTempView(s"${ptTableName}_tmp_view")
    sql(s"insert into table ${ptTableName} partition (pt1='2018', pt2='0601') select * from ${ptTableName}_tmp_view")
    sql(s"insert overwrite table ${ptTableName} partition (pt1='2018', pt2='0601') select * from ${ptTableName}_tmp_view")

    ptDf.write.insertInto(ptTableName) // 动态分区 insertInto语义
    ptDf.write.mode("overwrite").insertInto(ptTableName) // 动态分区 insertOverwrite语义

    // 读 普通表
    val rdf = sql(s"select name, num from $tableName")
    println(s"rdf show, ${rdf.count()}")
    rdf.show()
    rdf.printSchema()

    // 读 分区表
    val rptdf = sql(s"select name, num, pt1, pt2 from $ptTableName where pt1 = '2018' and pt2 = '0601'")
    println(s"rptdf show, ${rptdf.count()}")
    rptdf.show()
    rptdf.printSchema()
  }
}

