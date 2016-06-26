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

import org.apache.ignite.{IgniteCache, Ignition, Ignite}
import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.spark.IgniteContext
import org.apache.spark.{SparkConf, SparkContext}

/**
 * This example demonstrates joining 2 RDDs via the use of DataFrames.
 * The first RDD is create from a file and the second is an IgniteRDD.
 */
object SparkSQLJoin {

  // Create a custom class to represent the Customer
  case class Customer(customer_id: Int, name: String, city: String, state: String, zip_code: String, company_id: Int)
  case class Company(company_id: Int, name: String)

  def main(args: Array[String]) {

    // Load the Ignite cache
    val ignite: Ignite = Ignition.start
    val companyCache: IgniteCache[Integer, String] = ignite.getOrCreateCache("CompanyCache")
    companyCache.put(0, "GridGain")
    companyCache.put(1, "Apple")
    companyCache.put(2, "IBM")
    companyCache.put(3, "Oracle")
    companyCache.put(4, "Microsoft")

    val sc = new SparkContext(new SparkConf().setAppName("Spark SQL Join Example"))
    // Create the SQLContext first from the existing Spark Context
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)

    // Import statement to implicitly convert an RDD to a DataFrame
    import sqlContext.implicits._

    // create igniteRDD
    val companyCacheIgnite = new IgniteContext[Int, String](sc, () => new IgniteConfiguration()).fromCache("CompanyCache")

    // create company dataframe
    val dfCompany = sqlContext.createDataFrame(companyCacheIgnite.map(p => Company(p._1, p._2)))

//    val dfCompany = companyCacheIgnite.map(p => Company(p._1, p._2)

    // Register DataFrame as a table.
    dfCompany.registerTempTable("company")

    // Display the content of DataFrame
    dfCompany.show()

    // Print the DF schema
    dfCompany.printSchema()

    // Create a DataFrame of Customer objects from the dataset text file.
    val dfCustomers = sc.textFile("data/customers.txt")
      .map(_.split(","))
      .map(p => Customer(p(0).trim.toInt, p(1), p(2), p(3), p(4), p(5).trim.toInt))
      .toDF()

    // Register DataFrame as a table.
    dfCustomers.registerTempTable("customers")

    // Display the content of DataFrame
    dfCustomers.show()

    // Print the DF schema
    dfCustomers.printSchema()

    // Select customer name column
    dfCustomers.select("name").show()

    // Select customer name and city columns
    dfCustomers.select("name", "city").show()

    // Select a customer by id
    dfCustomers.filter(dfCustomers("customer_id").equalTo(500)).show()

    // Count the customers by zip code
    dfCustomers.groupBy("zip_code").count().show()

    dfCustomers.join(dfCompany, dfCustomers("company_id") === dfCompany("company_id")).show()

  }
}
