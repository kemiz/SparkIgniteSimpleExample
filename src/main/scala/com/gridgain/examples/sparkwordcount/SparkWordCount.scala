/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gridgain.examples.sparkwordcount

import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spark.IgniteContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * The first example counts the occurrence of each word in a corpus and then counts the
 * occurrence of each character in the most popular words.
 */
object SparkWordCount {
  def main(args: Array[String]) {
    // create the spark context
    val sc = new SparkContext(new SparkConf().setAppName("Spark Count"))

    // create ignite rdd
    val resultsIgniteRDD = new IgniteContext[Char, Int](sc, () => new IgniteConfiguration()).fromCache("resultsIgniteRDD")
    val igniteWordsRDD = new IgniteContext[String, Int](sc, () => new IgniteConfiguration()).fromCache("igniteWordsRDD")

    // set the count threshold
    val threshold = args(1).toInt

    // split each document into words
    val tokenized = sc.textFile(args(0)).flatMap(_.split(" "))
    
    // count the occurrence of each word
    igniteWordsRDD.savePairs(tokenized.map((_, 1)).reduceByKey(_ + _))
    
    // filter out words with less than threshold occurrences
    val filtered = igniteWordsRDD.filter(_._2 >= threshold)
    
    // count characters
    val charCounts = filtered.flatMap(_._1.toCharArray).map((_, 1)).reduceByKey(_ + _)

    // save results to IgniteRDD
    resultsIgniteRDD.savePairs(charCounts)

    System.out.println(charCounts.collect().mkString(", "))
  }
}
