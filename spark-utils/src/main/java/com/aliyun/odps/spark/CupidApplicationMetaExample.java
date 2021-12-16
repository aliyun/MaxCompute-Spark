package com.aliyun.odps.spark;
import apsara.odps.cupid.protocol.CupidTaskParamProtos;
import com.aliyun.odps.cupid.CupidConf;
import com.aliyun.odps.cupid.CupidSession;
import com.aliyun.odps.cupid.requestcupid.ApplicationMetaUtil;
import com.aliyun.odps.cupid.requestcupid.CupidProxyTokenUtil;

import java.util.List;
import java.util.stream.Collectors;

public class CupidApplicationMetaExample {

    // cd target
    // java -cp ../libs/cupid-sdk-3.3.14.jar:spark-utils-1.0.0-shaded.jar com.aliyun.odps.spark.CupidApplicationMetaExample
    public static void main(String[] args) throws Exception {
        CupidConf conf = new CupidConf();
        conf.set("odps.access.id", "");
        conf.set("odps.access.key", "");
        conf.set("odps.project.name", "");
        conf.set("odps.end.point", "");
        CupidSession session = new CupidSession(conf);

        /*
         * list application metas
         * yarnApplicationStates: https://hadoop.apache.org/docs/r2.7.3/api/org/apache/hadoop/yarn/api/records/YarnApplicationState.html
         * 注意：list开销较大，调用频率不建议太高
         */
        CupidTaskParamProtos.ApplicationMetaList applicationMetaList = ApplicationMetaUtil.listApplicationMeta(
                "SPARK",
                "5",
                session);
        List<CupidTaskParamProtos.ApplicationMeta> applicationMetas = applicationMetaList.getApplicationMetaListList()
                .stream()
                .collect(Collectors.toList());
        if (applicationMetas.size() > 0) {
            applicationMetas.forEach(System.out::println);
        }

        /*
         * get application meta by instanceid
         */
        String instanceId = "20211214074136554gqpk7659";
        CupidTaskParamProtos.ApplicationMeta applicationMeta= ApplicationMetaUtil.getCupidInstanceMeta(instanceId, session);
        System.out.println(applicationMeta.toString());
    }
}


