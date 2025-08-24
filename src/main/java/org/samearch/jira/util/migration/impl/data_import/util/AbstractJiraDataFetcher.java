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

package org.samearch.jira.util.migration.impl.data_import.util;

import org.samearch.jira.util.migration.api.config.DataFetchConfig;
import org.samearch.jira.util.migration.api.config.JiraInstanceConfig;
import org.samearch.jira.util.migration.api.exception.DataFetchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Base64;
import java.util.Map;

public abstract class AbstractJiraDataFetcher {

	private static final Logger LOG = LoggerFactory.getLogger("dataFetcher");

	protected final DataFetchConfig dataFetchConfig;

	protected final Map.Entry<String, String> authHeader;

	public AbstractJiraDataFetcher(DataFetchConfig dataFetchConfig) {
		this.dataFetchConfig = dataFetchConfig;
		this.authHeader = generateAuthHeader(dataFetchConfig.jiraAuthSettings());
	}

	protected abstract String getQueryUrl(String jqlQuery);

	public String fetchData(String jqlQuery) throws DataFetchException {
		var url = getQueryUrl(jqlQuery);
		for (int attemptsLeft = Math.max(dataFetchConfig.retryCount(), 1); attemptsLeft > 0; attemptsLeft--) {
			try {
				return tryToFetchData(url);
			} catch (ClosedChannelException e) {
				LOG.error("can't read data from {}", url);
				throw new RuntimeException(e);
			} catch (ConnectException e) {
				LOG.error("can't fetch data from {}: can't connect to host", url);
				break;
			} catch (IOException e) {
				LOG.error("can't fetch data from {}: {}", url, e.getMessage());
				LOG.info("attempts left: {}", attemptsLeft - 1);
				LOG.info("sleep {} seconds and retry", dataFetchConfig.retryPause());
				try {
					Thread.sleep(Duration.ofSeconds(dataFetchConfig.retryPause()));
				} catch (InterruptedException ignore) {}
			}
		}
		throw new DataFetchException("Can't fetch data from " + url);
	}

	private String tryToFetchData(String url) throws IOException {
		try (var httpClient = HttpClient.newHttpClient()) {
			var request = HttpRequest.newBuilder()
				.header(authHeader.getKey(), authHeader.getValue())
				.uri(URI.create(url))
				.build();
			LOG.info("fetch data from {}", url);
			var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			if (response.statusCode() != 200) {
				throw new RuntimeException("can't fetch json data from url: " + url + ". Status code: " + response.statusCode());
			}
			LOG.info("data fetched");
			return response.body();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private static Map.Entry<String, String> generateAuthHeader(JiraInstanceConfig jiraInstanceConfig) {
		if (jiraInstanceConfig.bearerToken() != null) {
			return new AbstractMap.SimpleEntry<>("Authorization", "Bearer " + jiraInstanceConfig.bearerToken());
		} else {
			var credentilsString = jiraInstanceConfig.basicLogin() + ":" + jiraInstanceConfig.basicPassword();
			var encodedCredentialsString = Base64.getEncoder().encodeToString(credentilsString.getBytes(StandardCharsets.UTF_8));
			var basicAuthString = String.format("Basic %s", encodedCredentialsString);
			return new AbstractMap.SimpleEntry<>("Authorization", basicAuthString);
		}
	}

}
