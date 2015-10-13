package spring_mvc_quickstart_archetype.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import spring_mvc_quickstart_archetype.Application;

import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;

/*
 * This only does back-end configuration hence excludes @Controller types see WebMvcConfig for the front-end configuration 
 */
@Configuration
@ComponentScan(basePackageClasses = { Application.class, SrpAccountEntity.class }, excludeFilters = @Filter({
		Controller.class, Configuration.class }))
class ApplicationConfig {
	
	@Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		ppc.setLocations(new ClassPathResource("/persistence.properties"),
				new ClassPathResource("/thinbus.properties"));
		return ppc;
	}
	
}