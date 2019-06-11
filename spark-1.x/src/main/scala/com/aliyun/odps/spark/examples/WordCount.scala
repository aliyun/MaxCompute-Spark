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

package com.aliyun.odps.spark.examples

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

/**
  * WordCount
  * Step 1. build aliyun-cupid-sdk
  * Step 2. properly set spark.defaults.conf
  * Step 3. bin/spark-submit --master yarn-cluster --class com.aliyun.odps.spark.examples.WordCount \
  * ${ProjectRoot}/spark/spark-1.x/spark-examples/target/spark-examples_2.10-version-shaded.jar
  */
object WordCount {
  def main(args: Array[String]) {

    // for local mode
    // val conf = new SparkConf().setMaster("local[4]").setAppName("WordCount")

    // for cluster mode
    val conf = new SparkConf().setAppName("WordCount")
    val sc = new SparkContext(conf)
    try {
      sc.parallelize(1 to 100, 10).map(word => (word, 1)).reduceByKey(_ + _, 10).take(100).foreach(println)
    } finally {
      sc.stop()
    }
  }
}