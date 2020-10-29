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

package com.aliyun.odps.spark.examples.sparksql;

import com.aliyun.odps.Odps;
import com.aliyun.odps.cupid.CupidSession;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.odps.OdpsContext;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

import org.apache.spark.sql.types.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.types.StructField;

public class JavaSparkSQL {

  public static void main(String[] args) {
    SparkConf conf = new SparkConf()
            .set("spark.hadoop.odps.exec.dynamic.partition.mode", "nonstrict")
            .setAppName("sparkSQL");
    JavaSparkContext sc = new JavaSparkContext(conf);
    OdpsContext odpsContext = new OdpsContext(sc);

    String project = sc.getConf().get("odps.project.name");
    String tableName = "mc_test_table";
    String tableNameCopy = "mc_test_table_copy";
    String ptTableName = "mc_test_pt_table";


    odpsContext.sql("DROP TABLE IF EXISTS " + tableName);
    odpsContext.sql("DROP TABLE IF EXISTS " + tableNameCopy);
    odpsContext.sql("DROP TABLE IF EXISTS " + ptTableName);


    odpsContext.sql("CREATE TABLE " + tableName + " (name STRING, num BIGINT)");
    odpsContext.sql("CREATE TABLE " + ptTableName+ " (name STRING, num BIGINT) PARTITIONED BY (pt1 STRING, pt2 STRING)");

    odpsContext.sql("DESCRIBE " + tableName);
    odpsContext.sql("DESCRIBE " + ptTableName);

    List<Integer> data = new ArrayList<Integer>();
    for (int i = 0; i < 100 ; i++) {
      data.add(i);
    }

    JavaRDD<Row> dfRDD = sc.parallelize(data, 2).map(new Function<Integer, Row>() {
      public Row call(Integer i) {
        return RowFactory.create(
                "name-" + i.toString(),
                Long.valueOf(i));
      }
    });

    JavaRDD<Row> ptDfRDD = sc.parallelize(data, 2).map(new Function<Integer, Row>() {
      public Row call(Integer i) {
        return RowFactory.create(
                "name-" + i.toString(),
                Long.valueOf(i),
                "2018",
                "0601");
      }
    });

    List<StructField> structFilelds = new ArrayList<StructField>();
    structFilelds.add(DataTypes.createStructField("name", DataTypes.StringType, true));
    structFilelds.add(DataTypes.createStructField("num", DataTypes.LongType, true));
    DataFrame df = odpsContext.createDataFrame(dfRDD, DataTypes.createStructType(structFilelds));

    structFilelds.add(DataTypes.createStructField("pt1", DataTypes.StringType, true));
    structFilelds.add(DataTypes.createStructField("pt2", DataTypes.StringType, true));
    DataFrame ptDf = odpsContext.createDataFrame(ptDfRDD, DataTypes.createStructType(structFilelds));

    // 写 普通表
    df.write().insertInto(tableName); // insertInto语义
    df.write().mode("overwrite").insertInto(tableName);// insertOverwrite语义

    // 读 普通表
    DataFrame rdf =odpsContext.sql("select name, num from "+ tableName);
    System.out.println("rdf count: "+ rdf.count());
    rdf.printSchema();

    //create table as select
    odpsContext.sql("CREATE TABLE " + tableNameCopy +" AS SELECT name, num FROM " + tableName);
    odpsContext.sql("SELECT * FROM " + tableNameCopy).show();

    // 写 分区表
    // DataFrameWriter 无法指定分区写入 需要通过临时表再用SQL写入特定分区
    df.registerTempTable(ptTableName +"_tmp_view");
    odpsContext.sql("insert into table " + ptTableName + " partition (pt1='2018', pt2='0601') select * from " + ptTableName + "_tmp_view");
    odpsContext.sql("insert overwrite table " + ptTableName+ " partition (pt1='2018', pt2='0601') select * from " + ptTableName+ "_tmp_view");

    ptDf.write().partitionBy("pt1", "pt2").insertInto(ptTableName);// 动态分区 insertInto语义
    ptDf.write().partitionBy("pt1", "pt2").mode("overwrite").insertInto(ptTableName); // 动态分区 insertOverwrite语义
    
    // 读 分区表
    DataFrame rptdf = odpsContext.sql("select name, num, pt1, pt2 from " + ptTableName + " where pt1 = '2018' and pt2 = '0601'");
    System.out.println("rptdf count: "+ rptdf.count());
    rptdf.printSchema();


    Odps odps = CupidSession.get().odps();
    System.out.println(odps.tables().get(ptTableName).getPartitions().size());
    System.out.println(odps.tables().get(ptTableName).getPartitions().get(0).getPartitionSpec());
  }
}
