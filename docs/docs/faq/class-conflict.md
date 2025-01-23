# Java Scala类冲突问题

## 类冲突问题概述
* 这类报错一般会抛出异常java.lang.NoClassDefFoundError或者方法找不到等问题，需要检查pom并将冲突的依赖排除掉
* 原因在于：用户jar包中很可能打了一些依赖进去，这些依赖的jar包与spark jars目录下的jar包由于版本不一致，jvm在加载类的时候优先加载了用户的jar包

## 需要注意的问题

### 依赖为provided和compile的区别

* provided：代码依赖该jar包，但只在编译的时候需要用，而运行时不需要，运行时会去集群中去寻找的相应的jar包，很多时候把jar包的类型设置为provided类型，就是因为这些jar包已经在集群中提供了（主要是spark客户端的jars目录，该目录中包含的jar包通常应该设置为provided），如果不设置为provided，某些时候可能可以正常运行，某些时候就会发生类冲突，类/方法找不到等各种问题。
* compile：代码依赖该jar包，在编译、运行时候都需要，也就是说集群中不存在这些jar包，需要用户打到自己的jar包中。这种类型的jar包一般是一些三方库，且与spark运行无关，与用户代码逻辑有关。

### 主jar包必须是一个fat jar
* 必须要把compile类型的依赖都打到用户jar包中，这样在代码运行时才能加载到这些依赖类


## POM自检

### 需要设置为provided的jar包
* groupId为org.apache.spark的jar包
  + **说明** 这类jar包主要是社区版spark的jar包，已经在spark客户端的jars目录下提供，不需要打进用户的jar包，会在spark客户端提交任务时自动上传到MaxCompute集群中

* cupid-sdk
  + **说明** 该jar包在任务提交时自动上传到MaxCompute集群中

* odps-sdk
  + **说明** 该jar包在任务提交时自动上传到MaxCompute集群中

* hadoop-yarn-client
  + **说明** 该jar包用于任务上传
  + **注意** 该jar包可能会被间接依赖，因此最好在打包之前检查并将该依赖排除
  
### 不能设置为provided的jar包
* oss相关的jar包
  + **举例** hadoop-fs-oss
  + **说明** 该jar包属于第三方jar包，如果需要访问oss，需要打到用户jar包中

* 流式相关的jar包
  + **举例** streaming-lib
  + **说明** 该jar包提供了一些spark streaming的接口来访问datahub和loghub，如果用户需要使用，则需要打到用户jar包中

* 用户访问其他服务用到的jar包
  + **举例** 访问mysql等其他第三方服务需要用到的jar包


