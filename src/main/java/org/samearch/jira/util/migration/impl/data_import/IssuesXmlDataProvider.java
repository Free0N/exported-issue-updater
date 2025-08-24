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

package org.samearch.jira.util.migration.impl.data_import;

import org.samearch.jira.util.migration.api.IssueDataFetcher;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.dto.Project;
import org.samearch.jira.util.migration.impl.data_import.util.IssueXmlDataReader;
import org.samearch.jira.util.migration.api.exception.DataFetchException;
import org.samearch.jira.util.migration.impl.data_import.util.AbstractJiraDataFetcher;
import org.samearch.jira.util.migration.impl.data_import.util.IssueUtil;
import org.samearch.jira.util.migration.impl.dto.xml.IssueXmlDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IssuesXmlDataProvider extends AbstractJiraDataFetcher implements IssueDataFetcher {

	private static final String ISSUE_EXPORTER_ENDPOINT = "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml";

	private final IssueXmlDataReader issueXmlObjectMapper;

	public IssuesXmlDataProvider(UpdateConfig updateConfig, IssueXmlDataReader issueXmlObjectMapper) {
		super(updateConfig.dataFetchConfig());
		this.issueXmlObjectMapper = issueXmlObjectMapper;
	}

	@Override
	protected String getQueryUrl(String jqlQuery) {
		var url = String.format("%s%s", dataFetchConfig.jiraAuthSettings().baseUrl(), ISSUE_EXPORTER_ENDPOINT);
		var urlEncodedJqlString = URLEncoder.encode(jqlQuery, StandardCharsets.UTF_8);
		var queryString = "jqlQuery=" + urlEncodedJqlString;
		return String.format("%s?%s", url, queryString);
	}

	@Override
	public List<Project> fetchIssuesData(String jql) throws DataFetchException {
		var xmlData = fetchData(jql);
		var xmlDataStream = new ByteArrayInputStream(xmlData.getBytes(StandardCharsets.UTF_8));
		Map<String, IssueXmlDto> structuredXmlData = issueXmlObjectMapper.readIssuesData(xmlDataStream);
		var projectsIssues = new HashMap<String, List<Issue>>();
		structuredXmlData.values().forEach(issueXmlDto -> {
			var issueKey = issueXmlDto.key();
			var issueProjectKey = IssueUtil.extractProjectKey(issueKey)
							.orElseThrow(() -> new RuntimeException("Can't extract project key from issue key '" + issueKey + "'"));
			var projectIssues = projectsIssues.computeIfAbsent(issueProjectKey, k -> new ArrayList<>());
			projectIssues.add(issueXmlDto.toIssue());
		});
		List<Project> projects = new ArrayList<>();
		projectsIssues.forEach((projectKey, projectIssues) -> {
			var project = new Project();
			project.setKey(projectKey);
			project.setIssues(projectIssues);
			projects.add(project);
		});
		return projects;
	}

}
