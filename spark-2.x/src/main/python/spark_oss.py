# -*- coding: utf-8 -*-
import sys
from pyspark.sql import SparkSession

try:
    # for python 2
    reload(sys)
    sys.setdefaultencoding('utf8')
except:
    # python 3 not needed
    pass

if __name__ == '__main__':
    spark = SparkSession.builder\
        .config("spark.hadoop.fs.AbstractFileSystem.oss.impl", "com.aliyun.emr.fs.oss.OSS")\
        .config("spark.hadoop.fs.oss.impl", "com.aliyun.emr.fs.oss.JindoOssFileSystem")\
        .config("spark.hadoop.fs.oss.endpoint", "oss-cn-hangzhou-internal.aliyuncs.com")\
        .config("spark.hadoop.fs.oss.accessKeyId", "xxx")\
        .config("spark.hadoop.fs.oss.accessKeySecret", "xxx")\
        .appName("spark write df to oss")\

        .getOrCreate()

    data = [i for i in range(0, 100)]

    df = spark.sparkContext.parallelize(data, 2).map(lambda s: ("name-%s" % s, s)).toDF("name: string, num: int")

    df.show(n=10)

    # write to oss
    pathout = 'oss://[bucket]/test.csv'
    df.write.csv(pathout)
