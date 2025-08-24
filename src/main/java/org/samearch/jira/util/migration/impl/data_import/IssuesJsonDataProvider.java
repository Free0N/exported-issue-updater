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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.samearch.jira.util.migration.api.IssueDataFetcher;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.samearch.jira.util.migration.api.dto.Project;
import org.samearch.jira.util.migration.api.exception.DataFetchException;
import org.samearch.jira.util.migration.impl.data_import.util.AbstractJiraDataFetcher;
import org.samearch.jira.util.migration.impl.dto.json.ExportedIssuesDocumentJsonDto;
import org.samearch.jira.util.migration.impl.dto.json.ProjectJsonDto;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class IssuesJsonDataProvider extends AbstractJiraDataFetcher implements IssueDataFetcher {

    private static final String ISSUE_EXPORTER_ENDPOINT = "/sr/com.atlassian.jira.plugins.jira-importers-plugin:searchrequest-json/temp/SearchRequest.json";

    private final ObjectMapper objectMapper;

    public IssuesJsonDataProvider(UpdateConfig updateConfig, ObjectMapper objectMapper) {
        super(updateConfig.dataFetchConfig());
        this.objectMapper = objectMapper;
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
        var jsonData = fetchData(jql);
        try {
            var structuredJsonData = objectMapper.readValue(jsonData, ExportedIssuesDocumentJsonDto.class);
            return structuredJsonData.getProjects().stream()
                    .map(ProjectJsonDto::toProject)
                    .collect(Collectors.toList());
        } catch (JsonProcessingException e) {
            throw new DataFetchException(e.getMessage());
        }
    }
}
