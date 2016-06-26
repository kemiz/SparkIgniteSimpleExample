Simple Spark & Ignite Integration
==============

Two simple Spark applications that demonstrate integration with Apache Ignite.

<h3> Prerequisites </h3>
==========

1. Install Apache Ignite
    - https://apacheignite.readme.io/docs/getting-started#installation
2. Install Apache Spark
    - http://spark.apache.org/docs/latest/
3. Start Ignite
    - `ignite.sh`
4. Start Spark cluster
    - `start-master.sh`
    - `start-slave.sh <master host>:<master port>`
5. Build the application:
    - `mvn clean package`

<h3> Spark Word Count using IgniteRDD </h3>
==========

The first example counts the occurrence of each word in a corpus and then counts the
occurrence of each character in the most popular words.

To run from a master node in a Spark cluster:

`bin/spark-submit --class com.gridgain.examples.sparkwordcount.SparkWordCount --master local --jars <IGNITE_HOME>/libs/ignite-core-1.5.11.jar,<IGNITE_HOME>/libs/optional/ignite-spark_2.10/ignite-spark_2.10-1.5.11.jar,<IGNITE_HOME>/libs/cache-api-1.0.0.jar, spark-ignite-example-1.0.jar`

This will run the application in a single local process.  If the cluster is running a Spark standalone
cluster manager, you can replace "--master local" with "--master spark://`<master host>`:`<master port>`".

If the cluster is running YARN, you can replace "--master local" with "--master yarn".

<h3> Spark SQL Join using IgniteRDD </h3>
==========

This example demonstrates joining 2 RDDs via the use of DataFrames. 
The first RDD is create from a file and the second is an IgniteRDD.

To run from a master node in a Spark cluster:

`bin/spark-submit --class com.gridgain.examples.sparkwordcount.SparkWordCount --master local --jars <IGNITE_HOME>/libs/ignite-core-1.5.11.jar,<IGNITE_HOME>/libs/optional/ignite-spark_2.10/ignite-spark_2.10-1.5.11.jar,<IGNITE_HOME>/libs/cache-api-1.0.0.jar,<IGNITE_HOME>/libs/ignite-spring/ignite-spring-1.5.11.jar,<IGNITE_HOME>/libs/ignite-spring/commons-logging-1.1.1.jar,<IGNITE_HOME>/libs/ignite-spring/spring-aop-4.1.0.RELEASE.jar,<IGNITE_HOME>/libs/ignite-spring/spring-beans-4.1.0.RELEASE.jar,<IGNITE_HOME>/libs/ignite-spring/spring-context-4.1.0.RELEASE.jar,<IGNITE_HOME>/libs/ignite-spring/spring-core-4.1.0.RELEASE.jar,<IGNITE_HOME>/libs/ignite-spring/spring-expression-4.1.0.RELEASE.jar spark-ignite-example-1.0.jar`

This will run the application in a single local process.  If the cluster is running a Spark standalone
cluster manager, you can replace "--master local" with "--master spark://`<master host>`:`<master port>`".

If the cluster is running YARN, you can replace "--master local" with "--master yarn".