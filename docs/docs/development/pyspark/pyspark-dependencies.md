# PySpark Python版本和依赖支持
## Package依赖
很多情况下pyspark依赖其他python库/插件/项目，由于odps集群无法自由安装python库，需在本地打包后通过spark-submit上传。对于特定依赖，打包环境需跟线上环境保持一致。我们提供了以下多种打包方式：

* 方案一：不打包直接采用公共资源（无需上传额外资源，只能使用默认提供的python环境）
* 方案二：上传单个wheel包（适用于需要的额外python依赖数量较少、较为简单）
* 方案三：利用脚本一键生成python环境（基于docker提供当前流行的若干版本的python环境，结合用户提供的requirements文件，利用脚本一键生成完整python包）
* 方案四：利用docker容器打包Python环境（可以任意选择python版本，docker容器只是提供了一个linux环境，最终需要把python环境上传到Maxcompute的资源中）

## 1. 不打包直接采用公共资源
### 默认提供的python 2.7.13环境配置
```
spark.hadoop.odps.cupid.resources = public.python-2.7.13-ucs4.tar.gz
spark.pyspark.python = ./public.python-2.7.13-ucs4.tar.gz/python-2.7.13-ucs4/bin/python
```
三方库List: [Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py27/py27-default_req.txt)

### 默认提供的python 3.6.12环境配置
```
spark.hadoop.odps.cupid.resources = public.python-3.6.12.tar.gz
spark.pyspark.python = ./public.python-3.6.12.tar.gz/python-3.6.12/bin/python3
```

### 默认提供的python 3.7.9环境配置
```
spark.hadoop.odps.cupid.resources = public.python-3.7.9-ucs4.tar.gz
spark.pyspark.python = ./public.python-3.7.9-ucs4.tar.gz/python-3.7.9-ucs4/bin/python3
```
三方库List: [Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py37/py37-default_req.txt)

## 2. 上传单个wheel包
如果依赖较为简单，则可以只上传单个wheel包，通常需要选用manylinux版本，wheel包下载地址：https://pypi.org/
### 使用方式：
```
（1）需要将wheel包重命名为zip包，例如将pymysql的wheel包重命名为pymysql.zip

（2）将重命名后的zip包上传（文件类型为archive）

（3）在Dataworks spark节点引用（archive类型）

（4）在spark-defaults.conf或dataworks配置项中添加配置以下后即可import
    # 配置
    spark.executorEnv.PYTHONPATH=pymysql  
    spark.yarn.appMasterEnv.PYTHONPATH=pymysql
    
    # 代码
    import pymysql
```

## 3. 利用脚本一键打包
若需要的额外依赖较多，则采取第2个选项`上传单个wheel包`会导致重复操作量倍增。因此这里提供一个脚本（下载地址[Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/scripts/generate_env_pyspark.sh)）,只需提供一个编辑好的requirements文件（格式见[Here](https://pip.pypa.io/en/stable/user_guide/#requirements-files)）,就能够直接生成完整的python环境用于PySpark使用，具体如下。
### 使用
```
$ chmod +x generate_env_pyspark.sh
$ generate_env_pyspark.sh -h 
Usage:
generate_env_pyspark.sh [-p] [-r] [-t] [-c] [-h]
Description:
-p ARG, the version of python, currently supports python 2.7, 3.5, 3.6 and 3.7 versions.
-r ARG, the local path of your python requirements.
-t ARG, the output directory of the gz compressed package.
-c, clean mode, we will only package python according to your requirements, without other pre-provided dependencies.
-h, display help of this script.
```

### 示例
```
# 带有预装依赖的打包方式
$ generate_env_pyspark.sh -p 3.7 -r your_path_to_requirements -t your_output_directory

# 不带预装依赖的打包方式（clean mode）
generate_env_pyspark.sh -p 3.7 -r your_path_to_requirements -t your_output_directory -c
```
### 说明
- 脚本适用于Mac/Linux环境，需要预先安装Docker, 见[官方文档](https://docs.docker.com/engine/install/)。
- 目前仅支持python 2.7、3.5、3.6和3.7版本，若对python版本不敏感，当前强烈推荐使用python 3.7。
- -c选项表示是否开启clean mode，clean mode无法使用预装依赖，但输出的python包更小。
    - 2.7预装依赖：[Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py27/py27_req.txt)
    - 3.5预装依赖：[Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py35/py35_req.txt)
    - 3.6预装依赖：[Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py36/py36_req.txt)
    - 3.7预装依赖：[Here](https://odps-repo.oss-cn-hangzhou.aliyuncs.com/pyspark/py37/py37_req.txt)
- 当前MaxCompute对上传资源的大小有500MB的限制，因此如果大部分预装依赖用不到，强烈推荐使用-c选项打包。

### Spark中使用
generate_env_pyspark.sh脚本的输出为在指定目录下（-t选项）生成指定python版本（-p选项）的gz包，以python3.7为例，将生成py37.tar.gz。后续再将此包上传为archieve资源（以odpscmd执行为例，也可用odps-sdk上传，各种方式上传资源参考：[资源操作](https://help.aliyun.com/document_detail/27831.htm?spm=a2c4g.11186623.2.11.28b21ddbmDbPSM)）：
```
# 在odpscmd中执行
add archive /your/path/to/py37.tar.gz -f;
```
然后在spark配置中增加以下两项配置即可：
```
spark.hadoop.odps.cupid.resources = your_project.py37.tar.gz
spark.pyspark.python = your_project.py37.tar.gz/bin/python
```
若上述两个参数不生效，例如用于zeppelin调试pyspark时notebook中的python环境，还需在spark作业中增加以下两项配置：
```
spark.yarn.appMasterEnv.PYTHONPATH = ./your_project.py37.tar.gz/bin/python
spark.executorEnv.PYTHONPATH = ./your_project.py37.tar.gz/bin/python
```

## 4. 利用docker容器打包Python环境
### 使用条件
（1）需要引入的依赖包含so文件等，无法通过上述zip文件的方式使用 和 无法进行pip install安装
<br/>（2）确实对除2.7、3.5、3.6、3.7以外的python版本有特殊需求
<br/>针对以上特殊情况，我们基于Docker提供以下步骤进行打包（以Python3.8为例）。
1. Docker镜像制作，在安装了docker环境的宿主机新建一个Dockerfile文件，python3.8参考如下：
```
FROM alibaba-cloud-linux-2-registry.cn-hangzhou.cr.aliyuncs.com/alinux2/alinux2:latest
RUN curl -o /etc/yum.repos.d/CentOS-Base.repo https://mirrors.aliyun.com/repo/Centos-7.repo
RUN curl -o /etc/yum.repos.d/epel.repo https://mirrors.aliyun.com/repo/epel-7.repo
RUN set -ex \
    # 预安装所需组件
    && yum clean all \
    && yum makecache \
    && yum install -y wget tar libffi-devel zlib-devel bzip2-devel openssl-devel ncurses-devel sqlite-devel readline-devel tk-devel gcc make initscripts zip\
    && wget https://www.python.org/ftp/python/3.8.20/Python-3.8.20.tgz \
    && tar -zxvf Python-3.8.20.tgz \
    && cd Python-3.8.20 \
    && ./configure prefix=/usr/local/python3 \
    && make \
    && make install \
    && make clean \
    && rm -rf /Python-3.8.20* \
    && yum install -y epel-release \
    && yum install -y python-pip
# 设置默认为python3
RUN set -ex \
    # 备份旧版本python
    && mv /usr/bin/python /usr/bin/python27 \
    && mv /usr/bin/pip /usr/bin/pip-python27 \
    # 配置默认为python3
    && ln -s /usr/local/python3/bin/python3.8 /usr/bin/python \
    && ln -s /usr/local/python3/bin/pip3 /usr/bin/pip
# 修复因修改python版本导致yum失效问题
RUN set -ex \
    && sed -i "s#/usr/bin/python#/usr/bin/python27#" /usr/bin/yum \
    && sed -i "s#/usr/bin/python#/usr/bin/python27#" /usr/libexec/urlgrabber-ext-down \
    && yum install -y deltarpm
# 更新pip版本
RUN pip install --upgrade pip
```
2. 构建镜像并运行容器：
```
# 在Dockerfile文件的目录下运行如下命令：
docker build --platform linux/amd64 -t python-centos:3.8 .
docker run --platform linux/amd64 -itd --name python3.8 python-centos:3.8 bash
```
3. 进入容器安装所需的python依赖库：
```
docker attach python3.8
pip install [所需依赖库]
```
4. 打包python环境：
```
cd /usr/local/
zip -r python3.8.zip python3/
```
5. 拷贝容器中的python环境到宿主机：
```
ctrl+P+Q退出容器
在宿主机运行命令：docker cp python3.8:/usr/local/python3.8.zip .
```
6. 上传python3.8.zip包到Maxcompute资源，可以通过[Maxcompute客户端](https://help.aliyun.com/document_detail/27971.html?spm=a2c4g.11186623.6.958.78685b9ci3MYbu)进行上传，上传类型为archive，命令参考[资源操作](https://help.aliyun.com/document_detail/27831.html?spm=5176.11065259.1996646101.searchclickresult.d55650ea0QU1qd&aly_as=45TiiTdO2)
```
add archive /path/to/python3.8.zip -f;
```
7. 提交作业时只需要在spark-default.conf或dataworks配置项中添加以下配置即可（[spark.hadoop.odps.cupid.resources](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Spark%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3)配置项说明）
```
spark.hadoop.odps.cupid.resources=[project名].python3.8.zip
spark.pyspark.python=./[project名].python3.8.zip/python3/bin/python3.8
```
### 注意事项：
```
通过Docker容器打包，如果遇到so包找不到的情况，则需要手动将so包放到python环境中（一般so包都能在容器中都能找到），并在spark作业中添加以下环境变量：

spark.executorEnv.LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./[project名].python3.8.zip/python3/[创建的so包目录]
spark.yarn.appMasterEnv.LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./[project名].python3.8.zip/python3/[创建的so包目录]
```

## 引用用户自定义的python包
很多情况下用户需要使用自定义的python文件，可以打包提交，这样避免了上传多个py文件，步骤如下：
* 将用户代码打一个zip包，需要在目录下自定义个一个空白的__init__.py
* 将用户代码zip包通过Maxcompute资源上传，并重命名，该资源在工作目录中将会被解压。详见[配置spark.hadoop.odps.cupid.resources](https://github.com/aliyun/MaxCompute-Spark/wiki/07.-Spark%E9%85%8D%E7%BD%AE%E8%AF%A6%E8%A7%A3#maxcompute%E6%95%B0%E6%8D%AE%E4%BA%92%E9%80%9A%E9%85%8D%E7%BD%AE)
* 配置参数:
  spark.executorEnv.PYTHONPATH=.    
  spark.yarn.appMasterEnv.PYTHONPATH=.
* 最后，主python文件就可以import该目录下的python文件了
