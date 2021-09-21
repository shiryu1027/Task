package com.example.demo.config;

import javax.sql.DataSource;

import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;


/*
 * 参考:https://github.com/springtestdbunit/spring-test-dbunit/issues/104
 * ・@ExpectedDatabaseで判定するテーブル名がusersのとき、Errorが発生する
 * ・詳しい原因は記事参照
 * ・以下二つのBeanを追加することによって、そのエラーを回避できる
 */
@Configuration
public class SpringH2Config {
    
    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        final DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(new H2DataTypeFactory());
        bean.setEscapePattern("`");
        return bean;
    }
    
    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource) {
        final DatabaseDataSourceConnectionFactoryBean bean = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        bean.setDatabaseConfig(dbUnitDatabaseConfig());
        bean.setSchema("PUBLIC");
        return bean;
    }
}

