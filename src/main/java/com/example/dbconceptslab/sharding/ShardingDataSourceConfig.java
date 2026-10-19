package com.example.dbconceptslab.sharding;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@Profile("sharding")
public class ShardingDataSourceConfig {

    /** Original database used by the existing JPA repositories. */
    @Bean(name = "dataSource")
    @Primary
    DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    /** Shard data sources are separate from the primary JPA data source. */
    @Bean("shard0DataSource")
    @ConfigurationProperties("spring.datasource.shard0")
    DataSource shard0DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("shard1DataSource")
    @ConfigurationProperties("spring.datasource.shard1")
    DataSource shard1DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("shard2DataSource")
    @ConfigurationProperties("spring.datasource.shard2")
    DataSource shard2DataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean("shard0JdbcTemplate")
    JdbcTemplate shard0JdbcTemplate(@Qualifier("shard0DataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean("shard1JdbcTemplate")
    JdbcTemplate shard1JdbcTemplate(@Qualifier("shard1DataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean("shard2JdbcTemplate")
    JdbcTemplate shard2JdbcTemplate(@Qualifier("shard2DataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
