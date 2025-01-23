# Client 模式
为了让Spark Session作为业务框架的后端数据处理服务，MaxCompute Spark 团队开发了“Client”模式来覆盖业务框架向同一个Spark Session 动态提交多个作业、实时获取作业状态的场景。

<a name="5gUvs"></a>
## Client模式开发初衷
社区spark生产主要使用"yarn-cluster"、"yarn-client"两种模式。“yarn-cluster”模式将spark作业提交到集群运行，运行完毕客户端打印状态日志；这种模式无法向一个Spark
Session动态多次提交作业，且客户端无法获取每个job的状态及结果。“yarn-client”模式，主要解决spark交互式场景问题，需要在客户端机器启动Driver,无法将Spark
 Session作为一个服务。因此我们基于Spark On MaxCompute开发了"Client"模式来解决上面的问题，该模式具有以下特点：

- 客户端轻量级，不用再启动spark的Driver;
- 客户端有一套API向MaxCompute集群的同一个Spark Session动态提交作业并监控状态；
- 客户端可以通过监控作业状态及结果构建作业之间的依赖关系；
- 用户可以动态编译应用程序jar通过客户端提交到原有的Spark Session运行；
- 客户端可以集成在业务的WebServer中，且可进行水平扩展;

<a name="tHDW4"></a>
## Client模式简介
client模式是为了解决交互式／在线任务需求。由于cluster模式必须把Driver放置在MaxCompute集群里面，如果我们有在线查询或者交互式的需求，由于网络隔离的原因，无法直接访问到Driver。

自研client模式同yarn-cluster模式一样也是把作业提交到MaxCompute集群，跟cluster模式最大的区别是client模式是由用户client端驱动，而cluster模式是由提交到计算集群的应用程序驱动。client模式把spark引擎作为一个在线服务来用，用户可以把client嵌入到在线业务系统进行实时分析。

client模式提供如下接口，允许多个Spark Job串行/并行执行，并提供多个spark作业共享的Context，允许多个spark作业共享数据。
```
/**
 * Add the local jar file ,which contains user SparkJobs
 * @param localFilePath the local jar file path
 * @return return the jarName ,the startjob() will use
 */
def addJar(localFilePath: String): String

/**
 * After add the jar,can start the sparkjob in the jar
 * @param className the class name in the jar
 * @param jarName jar name return from the addJar()
 * @param conf the conf when sparkjob run need
 * @return the jobId, getJobStatus/killJob will use
 */
def startJob(className: String, jarName: String, conf: Map[String,String]): String

/**
 * get the jobstatus after the job start
 * @param jobId jobId return from the startJob()
 * @return the job status ,eg: JobStart,JobSuccess,JobFailed,JobKilled
 */
def getJobStatus(jobId: String): Any

/**
 * stop the remote driver,then can not submit sparkjob
 */
def stopRemoteDriver()

/**
 * kill the sparkjob running
 * @param jobId the jobid will kill
 */
def killJob(jobId: String)
```


<a name="zz4goa"></a>
## Client模式作业提交方式
Client模式与传统 spark-submit 命令行提交方式的最大不同在于再依赖Spark客户端。这带来了两大优势：

1. 由于摆脱了Spark客户端的依赖，用户不再需要下载配置Spark环境，大大增加了Client模式的易用性，同时降低了用户的学习成本

2. 由于不再需要上传Spark libraries，启动Client时不再需要上传200M左右的spark libs，既节省了时间又节省了网络开销，真正做到了让用户随时随地都可以提交Spark作业


Client模式提供了非常直观的提交参数接口，将在下文详细介绍。

<a name="lg4goi"></a>
## [](#lg4goi)提交参数接口
```java
public class SubmitParam {

    // Primary resource
    private String file;

    // This field is for Livy, don't have to care if you're using new Client Mode
    private String proxyUser;

    // --classname, your driver's classname
    private String className;

    // --args, arguments for your spark application
    private List<String> args;

    // --jars, extra jars to distribute to driver & executors
    private List<String> jars;

    // --py-files, extra python files to distribute to driver & executors
    private List<String> pyFiles;

    // --files, extra files to distribute to driver & executors
    private List<String> files;

    // --archives, extra archives to distribute to driver & executors
    private List<String> archives;

    // --driver-memory
    private String driverMemory;

    // --driver-cores
    private String driverCores;

    // --executor-memory
    private String executorMemory;

    // --executor-cores
    private String executorCores;

    // --num-executors
    private String numExecutors;

    // --queue, you can ignore it
    private String queue;

    // --name, name of the spark application
    private String name;

    // --conf, other spark configurations
    private Map<String, String> conf;
    ...
}
```
* [使用示例](https://github.com/aliyun/MaxCompute-Spark/blob/clientmode-snapshot/spark-2.x/src/main/scala/com/aliyun/odps/spark/examples/clientmode/ClientModeDemo.scala)

* 由于提交时不再依赖Spark客户端，因此提交参数的接口有一定变化。现在参数统一通过SubmitParam这个接口传递，有两种传参方式：

1. 在代码中传递参数：

```
SubmitParam param = new SubmitParam();
param.setFile("/path/to/primary/resource");
param.setClassName("classname");
```

2. 使用配置文件：

```
SubmitParam param = new SubmitParam();
param.loadConfFromFile("/path/to/submitparam.conf");
```
3. Demo
checkout到clientmode-snapshot分支
```
git checkout clientmode-snapshot
```
编译
```
cd spark-2.x
mvn clean package
```
提交执行
```
java -cp ./odps-spark-client_2.11-0.0.1-DEV-SNAPSHOT-jar-with-dependencies.jar:./target/spark-examples_2.11-1.0.0-SNAPSHOT-shaded.jar com.aliyun.odps.spark.examples.clientmode.ClientModeDemo
```