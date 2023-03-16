package com.aliyun.odps.spark;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.util.ConverterUtils;
import org.apache.spark.launcher.SparkLauncher;
import org.apache.spark.launcher.SparkAppHandle;

public class SparkLauncherTest {

    private static String accessId = "";
    private static String accessKey = "";
    private static String projectName = "";
    private static String endPoint = "";

    // cd target
    // java -cp ../libs/*:spark-utils-1.0.0-shaded.jar com.aliyun.odps.spark.SparkLauncherTest
    public static void main(String[] args) throws Exception {
        Map<String, String> env = new HashMap<>();
        // relace here
        env.put("SPARK_HOME", "/Users/wusj/software/spark/spark-2.3.0-odps0.33.0");

        CountDownLatch countDownLatch = new CountDownLatch(1);
        SparkLauncher launcher = new SparkLauncher(env);
        launcher.setConf(SparkLauncher.DRIVER_EXTRA_CLASSPATH, System.getProperty("java.class.path"))
                .setConf("spark.hadoop.odps.access.id", accessId)
                .setConf("spark.hadoop.odps.access.key", accessKey)
                .setConf("spark.hadoop.odps.project.name", projectName)
                .setConf("spark.hadoop.odps.end.point", endPoint)
                .setMainClass("JavaSparkPi")
                // relace here
                .setAppResource("/Users/wusj/code/spark/test.jar")
                .setMaster("yarn")
                .setDeployMode("cluster")
                .startApplication(new SparkAppHandle.Listener(){
                    @Override
                    public void stateChanged(SparkAppHandle handle){
                        System.out.println("State changed to:" + handle.getState().toString());
                        if (handle.getState().equals(SparkAppHandle.State.RUNNING)) {
                            // Test kill application
                            killApplication(handle.getAppId());
                        }
                        if (handle.getState().isFinal()) {
                            countDownLatch.countDown();
                        }
                    }
                    @Override
                    public void infoChanged(SparkAppHandle handle) {
                    }
                });
        countDownLatch.await();
    }

    public static void killApplication(String applicationId) {
        YarnClient client = YarnClient.createYarnClient();
        Configuration conf = new Configuration();
        conf.set("odps.access.id", accessId);
        conf.set("odps.access.key", accessKey);
        conf.set("odps.project.name", projectName);
        conf.set("odps.end.point", endPoint);
        client.init(conf);
        client.start();

        ApplicationId appId = ConverterUtils.toApplicationId(applicationId);
        try {
            ApplicationReport appReport = client.getApplicationReport(appId);
            if (appReport.getYarnApplicationState() == YarnApplicationState.FINISHED
                    || appReport.getYarnApplicationState() == YarnApplicationState.KILLED
                    || appReport.getYarnApplicationState() == YarnApplicationState.FAILED) {
                System.out.println("Application " + applicationId + " has already finished ");
            } else {
                System.out.println("Killing application " + applicationId);
                client.killApplication(appId);
            }
        } catch (Exception e) {
            System.out.println("Kill application with id '" + applicationId + "' failed: " + e.getMessage());
        }
    }
}


