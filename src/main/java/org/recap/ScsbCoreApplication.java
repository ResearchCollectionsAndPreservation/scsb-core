package org.recap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import brave.sampler.Sampler;
import org.springframework.web.client.RestTemplate;

/**
 * The type SCSB Core Application.
 */
@PropertySource("classpath:application.properties")
@SpringBootApplication
public class ScsbCoreApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ScsbCoreApplication.class, args);
	}
	
    @Bean
    public Sampler defaultSampler() {
          return Sampler.ALWAYS_SAMPLE;
    }

	@Bean()
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
