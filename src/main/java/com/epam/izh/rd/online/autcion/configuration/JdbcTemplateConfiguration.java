package com.epam.izh.rd.online.autcion.configuration;

import com.epam.izh.rd.online.autcion.mappers.BidMapper;
import com.epam.izh.rd.online.autcion.mappers.ItemMapper;
import com.epam.izh.rd.online.autcion.mappers.UserMapper;
import com.epam.izh.rd.online.autcion.repository.JdbcTemplatePublicAuction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.ResourceBundle;

@Configuration
public class JdbcTemplateConfiguration {
    private ResourceBundle prop = ResourceBundle.getBundle("application");

    @Autowired
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return jdbcTemplate(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        DataSourceBuilder dsb = DataSourceBuilder.create();
        dsb.driverClassName(prop.getString("spring.datasource.driverClassName"));
        dsb.url(prop.getString("spring.datasource.url"));
        dsb.username(prop.getString("spring.datasource.username"));
        dsb.password(prop.getString("spring.datasource.password"));
        return dsb.build();
    }

    @Bean
    public BidMapper bidMapper() {
        return new BidMapper();
    }

    @Bean
    public ItemMapper itemMapper() {
        return new ItemMapper();
    }

    @Bean
    public UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    public JdbcTemplatePublicAuction jdbcTemplatePublicAuction(JdbcTemplate jdbcTemplate, DataSource dataSource, BidMapper bidMapper, ItemMapper itemMapper, UserMapper userMapper) {
        return new JdbcTemplatePublicAuction(jdbcTemplate, dataSource, bidMapper, itemMapper, userMapper);
    }
}
