# PySpark 使用mmlspark和analytics zoo
## 使用mmlspark
#### 背景
- mmlspark开源库地址：https://github.com/microsoft/SynapseML
- 由于MaxCompute Spark访问外部网络有限制，因此提供以下方案在MaxCompute Spark中使用mmlspark

#### 使用方式
- 第一步：下载Jar包：首先需要在本地客户端下载mmlspark的所有jar包
```
1. 在本地下载一个spark客户端

2. 配置spark-defaults.conf，添加以下参数
spark.jars.repositories=https://mmlspark.azureedge.net/maven

3. 使用local模式在本地执行以下命令：
$SPARK_HOME/bin/pyspark --packages com.microsoft.ml.spark:mmlspark_2.11:1.0.0-rc1

4. jar包通常会下载到以下目录：
$HOME/.ivy2/jars

5. 将所有的jar包压缩为一个zip包：
cd $HOME/.ivy2/jars
zip -r mml_spark.zip .
```

- 第二步：修改spark-defaults.conf
```
spark.executor.extraClassPath=./mml_spark.zip/*
spark.driver.extraClassPath=./mml_spark.zip/*
```

- 第三步：使用Yarn-cluster模式提交任务到集群中，注意需要包含 --py-files
```
./bin/spark-submit --archives mml_spark.zip --py-files mml_spark/com.microsoft.ml.spark_mmlspark_2.11-1.0.0-rc1.jar,mml_spark/com.microsoft.ml.lightgbm_lightgbmlib-2.3.100.jar spark_mml.py
```

## 使用analytics-zoo
#### 相关开源库
- https://github.com/intel-analytics/analytics-zoo
- https://github.com/intel-analytics/BigDL
- https://analytics-zoo.github.io/master/#release-download/

#### 参考使用方式
- 注意：下文使用analytics-zoo 0.11.0版本

- 第一步：Python打包
```
1. ./bin/pip3 install analytics-zoo -i http://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com

2. 注意：Python包比较大，可以卸载Pyspark（Maxcompute Spark中已包含PySpark）
./bin/pip3 uninstall pyspark

3. 打包为压缩包，下文使用该名称：python-3.6.14-big-dl.tar.gz

```

- 第二步：将Python包上传到Maxcompute resource中

- 第三步：将需要的三个Jar包拷贝出来，路径在
```
$python_home/lib/python3.6/site-packages/zoo/share/lib
  ○ analytics-zoo-bigdl_0.13.0-spark_2.4.6-0.11.1-jar-with-dependencies.jar

$python_home/lib/python3.6/site-packages/bigdl/share/lib
  ○ bigdl-0.13.0-jar-with-dependencies.jar
  ○ bigdl-0.13.0-python-api.zip
```

- 第四步：BigDL重新打包（解决log4j类冲突）

```
1. 首先找到对应BigDL的版本：如0.11.0对应BigDL的版本是0.13.0

2. 下载BigDL源码

   git clone https://github.com/intel-analytics/BigDL.git

3. 切换到0.13分支
   
   git checkout branch-0.13

4. 编译打包
   cd BigDL/spark/dl/
   mvn clean package -DskipTests;

5. 替换Jar包
   用target目录下生成的bigdl-0.13.1-SNAPSHOT-jar-with-dependencies.jar文件来替换第三步中的bigdl-0.13.0-jar-with-dependencies.jar

```

- 第五步：在spark-defaults.conf中配置Python包
```
spark.hadoop.odps.cupid.resources = [projectname].python-3.6.14-big-dl.tar.gz
spark.pyspark.python = ./[projectname].python-3.6.14-big-dl.tar.gz/python-3.6.14-big-dl/bin/python3
```

- 第六步：提交任务，需要携带第三步和第四步中生成的jar包：
```
./bin/spark-submit --jars analytics-zoo-bigdl_0.13.0-spark_2.4.6-0.11.1-jar-with-dependencies.jar,bigdl-0.13.1-SNAPSHOT-jar-with-dependencies.jar,bigdl-0.13.0-python-api.zip spark_test.py
```

