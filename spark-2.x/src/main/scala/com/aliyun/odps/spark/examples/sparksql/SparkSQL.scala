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

import java.math.BigDecimal
import java.sql.Timestamp

import com.aliyun.odps.{OdpsType, TableSchema}
import com.aliyun.odps.`type`.{TypeInfo, TypeInfoFactory}
import com.aliyun.odps.data.Record
import com.aliyun.odps.data.Binary
import com.aliyun.odps.data.SimpleStruct
import org.apache.spark.odps.OdpsOps
import org.apache.spark.sql.{AnalysisException, SparkSession}

import scala.collection.JavaConverters._
import scala.collection.mutable

object SparkSQL {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .appName("spark_sql_ddl")
      .config("spark.sql.broadcastTimeout", 20 * 60)
      .config("spark.sql.crossJoin.enabled", true)
      .config("odps.exec.dynamic.partition.mode", "nonstrict")
      .getOrCreate()
    val project = spark.conf.get("odps.project.name")
    val sc = spark.sparkContext

    import spark._

    //init and test drop table
    try {
      sql("USE " + project)
      sql("DROP TABLE IF EXISTS spark_sql_test_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_new_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_partition_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_partition_table_copy")
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
      case ex: java.lang.UnsupportedOperationException =>
        println("======= test drop database success ======")
      case e: Throwable =>
        println("======= test drop database failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test create database: throw java.lang.UnsupportedOperationException
    try {
      sql("CREATE DATABASE IF NOT EXISTS " + project)
      println("======= test create database failed ======")
      throw new RuntimeException
    } catch {
      case ex: java.lang.UnsupportedOperationException =>
        println("======= test create database success ======")
      case e: Throwable =>
        println("======= test create database failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test create table and desc table
    try {
      sql("CREATE TABLE spark_sql_test_table(name STRING, num BIGINT)")
      sql("CREATE TABLE spark_sql_test_partition_table(name STRING, num BIGINT) PARTITIONED BY (p1 STRING, p2 STRING)")
      sql("desc spark_sql_test_table")
      sql("desc spark_sql_test_partition_table")
      println("======= test create table success ======")
    } catch {
      case e: Throwable =>
        println("======= test create table failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test insert into/overwrite
    try {
      sql("INSERT INTO spark_sql_test_table SELECT 'abc', 100000")
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

    //test alter table rename
    try {
      sql("ALTER TABLE " + project + ".spark_sql_test_table RENAME TO " + project + ".spark_sql_test_new_table")
      sql("DESC " + project + ".spark_sql_test_new_table")
      println("======= test alter table rename success ======")
    } catch {
      case e: Throwable =>
        println("======= test alter table rename failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test alter table add/drop/show partitions
    try {
      sql("ALTER TABLE spark_sql_test_partition_table ADD IF NOT EXISTS PARTITION (p1='2017',p2='hangzhou')")
      sql("ALTER TABLE spark_sql_test_partition_table ADD IF NOT EXISTS PARTITION (p1='2017',p2='shanghai')")
      sql("ALTER TABLE spark_sql_test_partition_table ADD IF NOT EXISTS PARTITION (p1='2019',p2='shanghai')")
      val parts1 = sql("SHOW PARTITIONS spark_sql_test_partition_table").collect()
      assert(parts1.length == 3)
      sql("ALTER TABLE spark_sql_test_partition_table DROP IF EXISTS PARTITION (p1='2019',p2='shanghai')")
      val parts2 = sql("SHOW PARTITIONS spark_sql_test_partition_table").collect()
      assert(parts2.length == 2)
      println("======= test alter table add/drop/show partitions success ======")
    } catch {
      case e: Throwable =>
        println("======= test alter table add/drop/show partitions failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test show columns
    try {
      val cols = sql("SHOW COLUMNS FROM spark_sql_test_partition_table").collect()
      assert(cols.length == 4)
      assert(cols(0).get(0) == "name")
      assert(cols(1).get(0) == "num")
      assert(cols(2).get(0) == "p1")
      assert(cols(3).get(0) == "p2")
      println("======= test show columns success ======")
    } catch {
      case e: Throwable =>
        println("======= test show columns failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test explain
    try {
      sql("EXPLAIN SELECT name, SUM(num) FROM spark_sql_test_new_table GROUP BY name").collect()
      sql("EXPLAIN EXTENDED SELECT name, SUM(num) FROM spark_sql_test_new_table GROUP BY name").collect()
      sql("EXPLAIN CODEGEN SELECT name, SUM(num) FROM spark_sql_test_new_table GROUP BY name").collect()
      println("======= test explain success ======")
    } catch {
      case e: Throwable =>
        println("======= test explain failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test insert into/overwrite partition
    try {
      sql("INSERT INTO spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 100")
      sql("INSERT OVERWRITE TABLE  spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 160")
      sql("INSERT INTO  spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 200")
      sql("INSERT INTO  spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 300")
      sql("INSERT INTO  spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 400")
      sql("INSERT INTO  spark_sql_test_partition_table PARTITION (p1='2017',p2='shanghai') SELECT 'sh', 500")
      sql("INSERT INTO  spark_sql_test_partition_table PARTITION (p1='2017',p2='hangzhou') SELECT 'hz', 600")
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
      sql("CREATE TABLE spark_sql_test_partition_table_copy SELECT * FROM spark_sql_test_partition_table")
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

    //test show partitions for non-partitioned table
    try {
      sql("SHOW PARTITIONS spark_sql_test_partition_table_copy")
      println("======= test show partitions for non-partition table failed ======")
      throw new RuntimeException
    } catch {
      case ex: AnalysisException =>
        println("======= test show partitions for non-partitioned table success ======")
      case e: Throwable =>
        e.printStackTrace(System.out)
        println("======= test show partitions for non-partitioned table failed ======")
        throw e
    }

    //test show partitions for partitioned table and create dynamic partition
    try {
      val parts1 = sql("SHOW PARTITIONS spark_sql_test_partition_table").collect()
      assert(parts1.length == 2)
      sql("INSERT INTO spark_sql_test_partition_table SELECT name, num, '2018', 'beijing' FROM spark_sql_test_partition_table")
      val parts2 = sql("SHOW PARTITIONS spark_sql_test_partition_table").collect()
      assert(parts2.length == 3)
      println("======= test show partitions for partitioned table and create dynamic partition success ======")
    } catch {
      case e: Throwable =>
        println("======= test show partitions for partitioned table and create dynamic partition failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test register udf
    try {
      spark.udf.register("myUpper", (input: String) => input.toUpperCase)
      val funcs = sql("SHOW FUNCTIONS like 'myUpper'").collect()
      assert(funcs.length == 1)
      val data = sql("SELECT myUpper(name) FROM spark_sql_test_partition_table WHERE name = 'hz'").collect()
      assert(data(0).get(0) == "HZ")
      println("======= test register udf success ======")
    } catch {
      case e: Throwable =>
        println("======= test register udf failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test show create table
    try {
      sql("SHOW CREATE TABLE spark_sql_test_partition_table").collect
      println("======= test show create table success ======")
    } catch {
      case e: Throwable =>
        println("======= test show create table failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test truncate table
    try {
      val data1 = sql("SELECT * FROM spark_sql_test_new_table").collect()
      assert(data1.length > 0)
      sql("TRUNCATE TABLE spark_sql_test_new_table")
      val data2 = sql("SELECT * FROM spark_sql_test_new_table").collect()
      assert(data2.length == 0)
      println("======= test truncate table success ======")
    } catch {
      case e: Throwable =>
        println("======= test truncate table failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test drop table
    try {
      val tbls1 = sql("show tables like 'spark_sql_test_new_table'").collect
      assert(tbls1.length == 1)
      sql("DROP TABLE spark_sql_test_new_table")
      val tbls2 = sql("show tables like 'spark_sql_test_new_table'").collect
      assert(tbls2.length == 0)
      println("======= test drop table success ======")
    } catch {
      case e: Throwable =>
        println("======= test drop table failed ======")
        e.printStackTrace(System.out)
        throw e
    }

    //test new type
    try {
      sql("DROP TABLE IF EXISTS spark_sql_test_new_type_table")
      sql("DROP TABLE IF EXISTS spark_sql_test_new_type_table_copy")
      sql("CREATE TABLE `spark_sql_test_new_type_table`(`col0` int, `col1` bigint, `col2` string, `col3` decimal(6,3), `col4` tinyint, `col5` smallint, `col6` double, `col7` float, `col8` boolean, `col9` date, `col10` datetime, `col11` timestamp, `col12` char(2), `col13` varchar(4), `col14` binary, `col15` array<int>, `col16` map<bigint,string>, `col17` struct<name:string,age:int,parents:map<string,string>,salary:float,hobbies:array<string>>)")
      sql("DESC spark_sql_test_new_type_table").collect()

      val m1 = new java.util.HashMap[Long, String]()
      m1.put(Long.MinValue + 1, "Long.MinValue + 1")
      m1.put(0l, "zero")
      val m2 = new java.util.HashMap[Long, String]()
      m2.put(Long.MaxValue, "Long.MaxValue")
      val m3 = new java.util.HashMap[String, String]()
      m3.put("father", "Jack")
      m3.put("mather", "Rose")
      val m4 = new java.util.HashMap[String, String]()
      m4.put("mather", "Mary")
      val date = new java.sql.Date(System.currentTimeMillis())
      val timestamp = new Timestamp(System.currentTimeMillis())
      val s1 = Seq("tom", 20, m3, 100.1f, List("a", "b").asJava)
      val s2 = Seq("jack", 21, m4, 2001.2f, List("a", "b", "c").asJava)

      val data = sc.parallelize(Seq(
        Seq(1, Long.MaxValue, "abc", new BigDecimal("123.456"), Byte.MinValue, Short.MinValue, Double.MinValue, Float.MinValue, false, date, timestamp, timestamp, "ab", "abcd", "abcd".getBytes, List(1, 2, 3).asJava, m1, s1),
        Seq(2, Long.MinValue + 1l, "bcd", new BigDecimal("456.789"), Byte.MaxValue, Short.MaxValue, Double.MaxValue, Float.MaxValue, true, date, timestamp, timestamp, "abcd", "abcde", "abcde".getBytes, List(3, 4, 5).asJava, m2, s2)
      ))

      def transfer(v: Seq[Any],
                   record: Record, schema: TableSchema): Unit = {
        v.zipWithIndex.foreach(
          x => {
            if (x._1.isInstanceOf[Array[Byte]]) {
              record.set(x._2.asInstanceOf[Int], new Binary(x._1.asInstanceOf[Array[Byte]]))
            } else if (x._1.isInstanceOf[Seq[Any]]) {
              val sti = TypeInfoFactory.getStructTypeInfo(
                List("name", "age", "parents", "salary", "hobbies").asJava,
                List(TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.STRING).asInstanceOf[TypeInfo], TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.INT).asInstanceOf[TypeInfo],
                  TypeInfoFactory.getMapTypeInfo(TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.STRING).asInstanceOf[TypeInfo], TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.STRING).asInstanceOf[TypeInfo]).asInstanceOf[TypeInfo],
                  TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.FLOAT).asInstanceOf[TypeInfo], TypeInfoFactory.getArrayTypeInfo(TypeInfoFactory.getPrimitiveTypeInfo(OdpsType.STRING).asInstanceOf[TypeInfo]).asInstanceOf[TypeInfo]
                ).asJava
              )
              val ss = new SimpleStruct(sti, new java.util.ArrayList[AnyRef](x._1.asInstanceOf[Seq[Any]].map(_.asInstanceOf[AnyRef]).asJavaCollection))
              record.set(x._2.asInstanceOf[Int], ss)
            } else {
              record.set(x._2.asInstanceOf[Int], x._1)
            }
          }
        )
      }

      val odpsOps = new OdpsOps(sc)

      odpsOps.saveToTable(project, "spark_sql_test_new_type_table", "", data, transfer, true)

      var new_type_data = sql("SELECT * FROM spark_sql_test_new_type_table ORDER BY col0").collect()

      assert(new_type_data.length == 2)

      assert(new_type_data(0).get(0).equals(1))
      assert(new_type_data(0).get(1).equals(Long.MaxValue))
      assert(new_type_data(0).get(2).equals("abc"))
      assert(new_type_data(0).get(3).equals(new BigDecimal("123.456")))
      assert(new_type_data(0).get(4).equals(Byte.MinValue))
      assert(new_type_data(0).get(5).equals(Short.MinValue))
      assert(new_type_data(0).get(6).equals(Double.MinValue))
      assert(new_type_data(0).get(7).equals(Float.MinValue))
      assert(new_type_data(0).get(8).equals(false))
      assert(new_type_data(0).get(9).toString.equals(date.toString))
      assert(new_type_data(0).get(10).equals(timestamp))
      assert(new_type_data(0).get(11).equals(timestamp))
      assert(new_type_data(0).get(12).equals("ab"))
      assert(new_type_data(0).get(13).equals("abcd"))
      var a1 = new_type_data(0).get(14).asInstanceOf[Array[Byte]]
      var a2 = "abcd".getBytes()
      assert(a1.length == a2.length)
      a1.zip(a2).foreach(x => assert(x._1 == x._2))
      assert(new_type_data(0).get(15).asInstanceOf[mutable.WrappedArray[Int]].asJava.equals(List(1, 2, 3).asJava))
      assert(new java.util.HashMap[Long, String](new_type_data(0).get(16).asInstanceOf[Map[Long, String]].asJava).equals(m1))
      var a3 = new_type_data(0).get(17).asInstanceOf[org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema]
      assert(a3.length == s1.size)

      var s = a3.toSeq.asInstanceOf[mutable.WrappedArray[Any]].array
      assert(s(0).equals(s1(0)))
      assert(s(1).equals(s1(1)))
      assert(new java.util.HashMap[String, String](s(2).asInstanceOf[Map[String, String]].asJava).equals(s1(2)))
      assert(s(3).equals(s1(3)))
      assert(s(4).asInstanceOf[mutable.WrappedArray[Int]].asJava.equals(s1(4)))


      sql("CREATE TABLE spark_sql_test_new_type_table_copy AS SELECT * FROM spark_sql_test_new_type_table")
      sql("DESC spark_sql_test_new_type_table_copy").collect()

      new_type_data = sql("SELECT * FROM spark_sql_test_new_type_table_copy ORDER BY col0").collect()

      assert(new_type_data.length == 2)

      assert(new_type_data(0).get(0).equals(1))
      assert(new_type_data(0).get(1).equals(Long.MaxValue))
      assert(new_type_data(0).get(2).equals("abc"))
      assert(new_type_data(0).get(3).equals(new BigDecimal("123.456")))
      assert(new_type_data(0).get(4).equals(Byte.MinValue))
      assert(new_type_data(0).get(5).equals(Short.MinValue))
      assert(new_type_data(0).get(6).equals(Double.MinValue))
      assert(new_type_data(0).get(7).equals(Float.MinValue))
      assert(new_type_data(0).get(8).equals(false))
      assert(new_type_data(0).get(9).toString.equals(date.toString))
      assert(new_type_data(0).get(10).equals(timestamp))
      assert(new_type_data(0).get(11).equals(timestamp))
      assert(new_type_data(0).get(12).equals("ab"))
      assert(new_type_data(0).get(13).equals("abcd"))
      a1 = new_type_data(0).get(14).asInstanceOf[Array[Byte]]
      a2 = "abcd".getBytes()
      assert(a1.length == a2.length)
      a1.zip(a2).foreach(x => assert(x._1 == x._2))
      assert(new_type_data(0).get(15).asInstanceOf[mutable.WrappedArray[Int]].asJava.equals(List(1, 2, 3).asJava))
      assert(new java.util.HashMap[Long, String](new_type_data(0).get(16).asInstanceOf[Map[Long, String]].asJava).equals(m1))
      a3 = new_type_data(0).get(17).asInstanceOf[org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema]
      assert(a3.length == s1.size)

      s = a3.toSeq.asInstanceOf[mutable.WrappedArray[Any]].array
      assert(s(0).equals(s1(0)))
      assert(s(1).equals(s1(1)))
      assert(new java.util.HashMap[String, String](s(2).asInstanceOf[Map[String, String]].asJava).equals(s1(2)))
      assert(s(3).equals(s1(3)))
      assert(s(4).asInstanceOf[mutable.WrappedArray[Int]].asJava.equals(s1(4)))

      println("======= test new type success ======")
    } catch {
      case e: Throwable =>
        println("======= test new type failed ======")
        e.printStackTrace(System.out)
        throw e
    }
  }
}

