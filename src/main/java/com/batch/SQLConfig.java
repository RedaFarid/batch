//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.batch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.mapping.NamingStrategy;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.util.ParsingUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.sql.DataSource;

@Configuration
@EnableJdbcAuditing
@EnableJdbcRepositories
@EnableTransactionManagement
public class SQLConfig extends AbstractJdbcConfiguration {
    private static final Logger log = LogManager.getLogger(SQLConfig.class);

    @Bean
    NamedParameterJdbcOperations operations() {
        return new NamedParameterJdbcTemplate(this.dataSource());
    }

    @Bean
    PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(this.dataSource());
    }

    @Bean
    DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        dataSource.setUsername("sa");
        dataSource.setPassword("123456");
        dataSource.setUrl("jdbc:sqlserver://localhost:1434;databaseName=MIXINGBase;;encrypt=true;trustServerCertificate=true");
        return dataSource;
    }

    @Bean
    NamingStrategy namingStrategy() {
        return new NamingStrategy() {
            public String getColumnName(RelationalPersistentProperty property) {
                Assert.notNull(property, "Property must not be null.");
                return ParsingUtils.reconcatenateCamelCase(property.getName(), "");
            }
        };
    }
}
