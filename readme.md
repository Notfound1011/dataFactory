# 项目说明
> 包含测试平台使用的接口、压测使用脚本、一些学习使用的工具

### 注解
- @Configuration: 这个注解表示这是一个配置类，在 Spring 中用于声明该类是用于配置的类。
- @MapperScan: 这个注解指定了需要扫描的 Mapper 接口的包路径，用于自动扫描并注册 MyBatis 的 Mapper 接口。
- @EnableTransactionManagement: 这个注解开启了事务管理功能，用于支持数据库事务。
- @Bean: 这个注解表示这是一个Bean的定义方法，用于创建和配置对象。
- @ConditionalOnMissingBean: 这个注解表示在没有指定特定Bean的情况下创建Bean实例。
- @Primary: 这个注解指定了在多个Bean候选项中优先选择的主要Bean。
- @ConfigurationProperties: 这个注解将配置文件中的属性值绑定到对应的属性上。
- @Configuration: 这个注解表示这是一个配置类，在 Spring 中用于声明该类是用于配置的类。 
- @ConditionalOnProperty: 这个注解指定了一个条件，当指定的属性名为 "server.ssl.enabled"，并且属性值为 "true" 时，才会创建这个配置类的实例。
- @Value: 这个注解用于将配置文件中的属性值注入到对应的属性中。


### hikari连接池
- 在 Spring Boot 中，spring.datasource.hikari 配置项用于配置主数据源的连接池。
- 连接池是一种管理数据库连接的技术，它可以提供一组可重复使用的数据库连接，从而减少每次与数据库建立连接的开销，提高应用程序的性能和响应速度。 
- 使用 @ConfigurationProperties(prefix = "spring.datasource.hikari") 注解，从应用程序的配置文件 （如 application.properties 或 application.yml）中读取以 spring.datasource.hikari 为前缀的配置项， 用于配置 Hikari 连接池。

### quartz调度器
- spring.datasource.quartz 配置项用于配置 Quartz 调度器所使用的数据源。
- Quartz 是一个功能强大的任务调度框架，可以用于在应用程序中执行定时任务和调度作业。
- 使用 @ConfigurationProperties(prefix = "spring.datasource.quartz.hikari") 注解，从应用程序的配置文件中读取以 spring.datasource.quartz.hikari 为前缀的配置项，用于配置 Quartz 数据源的连接池。