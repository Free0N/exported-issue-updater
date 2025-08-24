/*
 * Copyright (c) 2025 Pavel Afanasev (afanasev.p@gmail.com)
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.samearch.jira.util.migration.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.samearch.jira.util.migration.application.util.UpdateConfigChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "org.samearch.jira.util.migration")
public class SpringApplicationConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger("init");

	@Value("${updateConfiguration:issueUpdateConfig.yaml}")
	private String yamlConfigurationPath;

	@Bean
	public ObjectMapper objectMapper() {
		var objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		return objectMapper;
	}

	@Bean
	public UpdateConfig updateConfig(UpdateConfigChecker updateConfigChecker) throws IOException {
		var yamlObjectMapper = new ObjectMapper(new YAMLFactory());
		yamlObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		yamlObjectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		var updateConfigFile = new File(yamlConfigurationPath);
		if (!updateConfigFile.exists()) {
			LOG.error("Expected configuration file does not exists: {}", yamlConfigurationPath);
			System.exit(1);
		}
		try {
			var updateConfig = yamlObjectMapper.readValue(new File(yamlConfigurationPath), UpdateConfig.class);
			updateConfigChecker.checkConfig(updateConfig);
			return updateConfig;
		} catch (JacksonException e) {
			LOG.error("Can't read configuration file {}: {}", yamlConfigurationPath, e.getMessage());
			System.exit(1);
			return null;
		}
	}

}
