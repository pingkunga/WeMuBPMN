package com.cu.thesis.WuMuBPMN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@SpringBootApplication
public class WuMuBpmnApplication {

	private static final Logger LOGGER=LoggerFactory.getLogger(WuMuBpmnApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(WuMuBpmnApplication.class, args);

		LOGGER.info("Start WeMuBPMN");
	}

	@Bean
	public CommonsRequestLoggingFilter requestLoggingFilter() {
		CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
		loggingFilter.setIncludeClientInfo(true);
		loggingFilter.setIncludeQueryString(true);
		loggingFilter.setIncludePayload(true);
    	return loggingFilter;
	}
}
