package com.aliyun.odps.spark.examples.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class ConfigLog4j2 {

  private static final LoggerContext CONTEXT;
  public static final String DEFAULT_APPENDER = "MY_STDOUT";
  public static final String
      DEFAULT_PATTERN =
      "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger - %msg %ex %n";

  static {
    CONTEXT = (LoggerContext) LogManager.getContext(false);
  }

  /**
   * @Description: add specific logger for specific package
   * @Param: packageName, such as com.xx.yy
   * @return: void
   * @Author: lcj265802@alibaba-inc.com
   * @Date: 2020/12/29
   */
  public static void initPackageLogger(String packageName) {
    LoggerContext loggerContext = CONTEXT;
    Configuration config = loggerContext.getConfiguration();

    ConsoleAppender.Builder builder = ConsoleAppender.newBuilder();
    builder.setName(DEFAULT_APPENDER);
    builder.setLayout(PatternLayout.newBuilder().withPattern(DEFAULT_PATTERN).build());
    Appender stdoutAppender = builder.setTarget(ConsoleAppender.Target.SYSTEM_OUT).build();
    stdoutAppender.start();

    config.addAppender(stdoutAppender);

    AppenderRef ref = AppenderRef.createAppenderRef(DEFAULT_APPENDER, null, null);
    AppenderRef[] refs = new AppenderRef[]{ref};

    LoggerConfig
        loggerConfig =
        LoggerConfig.createLogger(false, Level.INFO, packageName,
                                  "true", refs, null, config, null);
    loggerConfig.addAppender(stdoutAppender, null, null);
    config.addLogger(packageName, loggerConfig);

    loggerContext.updateLoggers();
  }
}
