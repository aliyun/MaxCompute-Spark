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
import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.sql.types.StructField;

public class JavaSparkSQL {

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("SparkSQL-on-MaxCompute")
                .config("spark.sql.defaultCatalog","odps")
                .config("spark.sql.catalog.odps", "org.apache.spark.sql.execution.datasources.v2.odps.OdpsTableCatalog")
                .config("spark.sql.sources.partitionOverwriteMode", "dynamic")
                .config("spark.sql.extensions", "org.apache.spark.sql.execution.datasources.v2.odps.extension.OdpsExtensions")
                .config("spark.sql.catalogImplementation","hive")
                .getOrCreate();
        JavaSparkContext sparkContext = new JavaSparkContext(spark.sparkContext());


        String tableName = "mc_test_table";
        String tableNameCopy = "mc_test_table_copy";
        String ptTableName = "mc_test_pt_table";


        spark.sql("DROP TABLE IF EXISTS " + tableName);
        spark.sql("DROP TABLE IF EXISTS " + tableNameCopy);
        spark.sql("DROP TABLE IF EXISTS " + ptTableName);

        spark.sql("CREATE TABLE " + tableName + " (name STRING, num BIGINT)");
        spark.sql("CREATE TABLE " + ptTableName + " (name STRING, num BIGINT) PARTITIONED BY (pt1 STRING, pt2 STRING)");

        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < 100; i++) {
            data.add(i);
        }

        JavaRDD<Row> dfRDD = sparkContext.parallelize(data, 2).map(new Function<Integer, Row>() {
            public Row call(Integer i) {
                return RowFactory.create(
                        "name-" + i.toString(),
                        Long.valueOf(i));
            }
        });

        JavaRDD<Row> ptDfRDD = sparkContext.parallelize(data, 2).map(new Function<Integer, Row>() {
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
        Dataset<Row> df = spark.createDataFrame(dfRDD, DataTypes.createStructType(structFilelds));

        structFilelds.add(DataTypes.createStructField("pt1", DataTypes.StringType, true));
        structFilelds.add(DataTypes.createStructField("pt2", DataTypes.StringType, true));
        Dataset<Row> ptDf = spark.createDataFrame(ptDfRDD, DataTypes.createStructType(structFilelds));

        // 写 普通表
        df.write().insertInto(tableName); // insertInto语义
        df.writeTo(tableName).overwritePartitions(); // insertOverwrite use datasourcev2

        // 读 普通表
        Dataset<Row> rdf = spark.sql("select name, num from " + tableName);
        System.out.println("rdf count: " + rdf.count());
        rdf.printSchema();

        //create table as select
        spark.sql("CREATE TABLE " + tableNameCopy + " AS SELECT name, num FROM " + tableName);
        spark.sql("SELECT * FROM " + tableNameCopy).show();

        // 写 分区表
        // DataFrameWriter 无法指定分区写入 需要通过临时表再用SQL写入特定分区
        df.registerTempTable(ptTableName + "_tmp_view");
        spark.sql("insert into table " + ptTableName + " partition (pt1='2018', pt2='0601') select * from " + ptTableName + "_tmp_view");
        spark.sql("insert overwrite table " + ptTableName + " partition (pt1='2018', pt2='0601') select * from " + ptTableName + "_tmp_view");

        ptDf.write().insertInto(ptTableName);// 动态分区 insertInto语义
        ptDf.write().mode("overwrite").insertInto(ptTableName); // 动态分区 insertOverwrite语义

        // 读 分区表
        Dataset<Row> rptdf = spark.sql("select name, num, pt1, pt2 from " + ptTableName + " where pt1 = '2018' and pt2 = '0601'");
        System.out.println("rptdf count: " + rptdf.count());
        rptdf.printSchema();

        // example for use odps
        Odps odps = CupidSession.get().odps();
        System.out.println(odps.tables().get(ptTableName).getPartitions().size());
        System.out.println(odps.tables().get(ptTableName).getPartitions().get(0).getPartitionSpec());
    }
}