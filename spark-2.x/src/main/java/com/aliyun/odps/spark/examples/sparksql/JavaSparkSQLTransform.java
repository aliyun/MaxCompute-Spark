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

import org.apache.spark.sql.SparkSession;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.catalyst.TableIdentifier;
import org.apache.spark.sql.catalyst.catalog.CatalogTable;

import scala.Tuple2;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

import org.apache.spark.sql.types.StructField;

public class JavaSparkSQLTransform {

    public static void main(String[] args) throws Exception {
        SparkSession spark = SparkSession
                .builder()
                .appName("SparkSQL-on-MaxCompute")
                .config("spark.sql.broadcastTimeout", 20 * 60)
                .config("spark.sql.crossJoin.enabled", true)
                .config("odps.exec.dynamic.partition.mode", "nonstrict")
                .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
                .getOrCreate();


        JavaSparkContext sparkContext = new JavaSparkContext(spark.sparkContext());

        String tableName = "mc_test_table";

        spark.sql("DROP TABLE IF EXISTS " + tableName);
        spark.sql("CREATE TABLE IF NOT EXISTS " + tableName +
                "(`col0` int, `col1` bigint, `col2` string, `col3` decimal(6,3), `col4` tinyint, `col5` smallint, `col6` double, `col7` float, `col8` boolean, `col9` date, `col10` datetime, `col11` timestamp, `col12` char(2), `col13` varchar(4), `col14` binary, `col15` array<int>, `col16` map<bigint,string>, `col17` struct<name:string,age:int,parents:map<string,string>,salary:float,hobbies:array<string>>)");

        scala.collection.mutable.HashMap<Long, String> m1 = new scala.collection.mutable.HashMap<>();
        m1.put(1L, "one");
        m1.put(0L, "zero");

        scala.collection.mutable.HashMap<Long, String> m2 = new scala.collection.mutable.HashMap<>();
        m2.put(2L, "two");

        scala.collection.mutable.HashMap<String, String> m3 = new scala.collection.mutable.HashMap<>();
        m3.put("father", "Jack");
        m3.put("mather", "Rose");

        scala.collection.mutable.HashMap<String, String> m4 = new scala.collection.mutable.HashMap<>();
        m4.put("mather", "Mary");

        Calendar calendar = new Calendar.Builder()
                .setCalendarType("iso8601")
                .setLenient(true)
                .setTimeZone(TimeZone.getTimeZone("GMT"))
                .build();
        calendar.set(2023, Calendar.FEBRUARY, 1);
        Date date = new Date(calendar.getTimeInMillis());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Row s1 = RowFactory.create("tom", 20, m3,100.1f, new String[]{"a","b"});
        Row s2 = RowFactory.create("jack", 21, m4, 2001.2f, new String[]{"a","b", "c"});

        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < 2; i++) {
            data.add(i);
        }

        JavaRDD<Row> dfRDD = sparkContext.parallelize(data, 2).map(new Function<Integer, Row>() {
            public Row call(Integer i) {
                if (i % 2 == 0) {
                    return RowFactory.create(
                            1, 1L,"abc", new BigDecimal("123.456"),
                            Byte.MIN_VALUE, Short.MIN_VALUE, Double.MIN_VALUE, Float.MIN_VALUE,
                            false, date, timestamp, timestamp, "ab", "abcd", "abcd".getBytes(), new int[]{1,2,3}, m1, s1); //
                } else {
                    return RowFactory.create(
                            2, 2L,"bcd", new BigDecimal("456.789"),
                            Byte.MAX_VALUE, Short.MAX_VALUE, Double.MAX_VALUE, Float.MAX_VALUE,
                            true, date, timestamp, timestamp, "cd", "bcde", "abcde".getBytes(), new int[]{3,4,5}, m2, s2); //
                }
            }
        });

        CatalogTable table = spark.sessionState().catalog().getTableMetadata(new TableIdentifier(tableName));
        StructType schema = table.schema();
        StructField[] tableFields = schema.fields();
        Dataset<Row> df = spark.createDataFrame(dfRDD, DataTypes.createStructType(tableFields));

        // 写 普通表
        df.write().insertInto(tableName); // insertInto语义
        df.write().mode("overwrite").insertInto(tableName);// insertOverwrite语义

        // 读 普通表
        Dataset<Row> rdf = spark.sql("select * from " + tableName);
        System.out.println("rdf count: " + rdf.count());
        rdf.printSchema();

        DataConverters.RowDataToListConverter converter = DataConverters.createRecordConverter(tableFields);

        JavaPairRDD<Long, List<Object>> testRdd = rdf.toJavaRDD()
                .mapToPair(new PairFunction<Row, Long, List<Object>>() {
                    public Tuple2<Long, List<Object>> call(Row row) throws Exception {
                        List<Object> res = new ArrayList();
                        converter.convert(row, res);
                        return new Tuple2(row.get(1), res);
                    }
                });
        System.out.println("test rdd count: " + testRdd.collect());
    }
}