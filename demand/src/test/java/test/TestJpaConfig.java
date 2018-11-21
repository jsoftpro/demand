package test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;

import pro.jsoft.spring.config.JpaConfig;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = { "pro.jsoft.spring.persistence.repositories" })
@PropertySource("classpath:test.properties")
@ActiveProfiles({"test-jpa"})
public class TestJpaConfig extends JpaConfig {
    @Override
    public FactoryBean<Object> dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("datasource.driver"));
        dataSource.setUrl(env.getProperty("datasource.url"));
        dataSource.setUsername(env.getProperty("datasource.username"));
        dataSource.setPassword(env.getProperty("datasource.password"));

        return new AbstractFactoryBean<Object>() {
            @Override
            public Class<?> getObjectType() {
                return DataSource.class;
            }

            @Override
            protected Object createInstance() throws Exception {
                return dataSource;
            }
        };
    }
}
