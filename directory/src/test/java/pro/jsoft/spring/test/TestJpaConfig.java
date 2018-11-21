package pro.jsoft.spring.test;

import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import pro.jsoft.spring.config.JpaConfig;

@PropertySource("classpath:test.properties")
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
