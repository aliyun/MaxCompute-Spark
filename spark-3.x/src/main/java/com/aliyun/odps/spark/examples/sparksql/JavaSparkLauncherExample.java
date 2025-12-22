package com.aliyun.odps.spark.examples.sparksql;

import org.apache.spark.launcher.SparkLauncher;

public class JavaSparkLauncherExample {

    public static void main(String[] args) throws Exception {
        // set spark home from env or args
        String sparkHome = System.getenv("SPARK_HOME");
        if (sparkHome == null) {
            // please set your own spark client path
            sparkHome = "/path/to/spark";
        }
        // please set your own AppResource, PropertiesFile and MainClass
        // you can also use setConf to replace setPropertiesFile
        SparkLauncher launcher = new SparkLauncher()
                .setAppName("Spark Launcher Example")
                .setSparkHome(sparkHome)
                .setAppResource("./target/spark-examples_2.12-1.0.0-SNAPSHOT.jar")
                .setPropertiesFile(sparkHome + "/conf/spark-defaults.conf")
                .setConf("spark.launcher.childProcLoggerName", "launcherLogger")
                .setMainClass("com.aliyun.odps.spark.examples.SparkPi");
        launcher.launch().waitFor();
    }
}
