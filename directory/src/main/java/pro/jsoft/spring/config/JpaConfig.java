package pro.jsoft.spring.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.val;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"pro.jsoft.spring.persistence.repositories"})
public class JpaConfig {
	@Autowired
	protected Environment env;

	@Bean
	public FactoryBean<Object> dataSource() {
		val jndiObjectFactoryBean = new JndiObjectFactoryBean();
		jndiObjectFactoryBean.setJndiName(env.getProperty("datasource.name"));
		jndiObjectFactoryBean.setResourceRef(true);
		jndiObjectFactoryBean.setProxyInterface(DataSource.class);
		try {
			jndiObjectFactoryBean.afterPropertiesSet();
		} catch (NamingException e) {
			throw new RuntimeException("DataSource is not found");
		}
		return jndiObjectFactoryBean;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		val emFactory = new LocalContainerEntityManagerFactoryBean();
		emFactory.setDataSource(dataSource);
		emFactory.setPackagesToScan(new String[] { "pro.jsoft.spring.persistence.model" });

		val vendorAdapter = new HibernateJpaVendorAdapter();
		emFactory.setJpaVendorAdapter(vendorAdapter);
		emFactory.setJpaProperties(additionalProperties());

		return emFactory;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		val transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);

		return transactionManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	protected Properties additionalProperties() {
		val properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", "validate");
		properties.setProperty("hibernate.dialect", env.getProperty("datasource.type"));

		properties.setProperty("hibernate.cache.use_second_level_cache", "true");
		properties.setProperty("hibernate.cache.use_query_cache", "true");
		properties.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.SingletonEhCacheProvider");
		properties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");

		return properties;
	}
}
