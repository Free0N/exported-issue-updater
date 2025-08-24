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

package org.samearch.jira.util.migration.impl.data_export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.samearch.jira.util.migration.api.IssueDataExporter;
import org.samearch.jira.util.migration.api.IssuesDataRepository;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.samearch.jira.util.migration.api.dto.Project;
import org.samearch.jira.util.migration.impl.DefaultIssuesDataRepository;
import org.samearch.jira.util.migration.impl.dto.json.ExportedIssuesDocumentJsonDto;
import org.samearch.jira.util.migration.impl.dto.json.IssueLinkJsonDto;
import org.samearch.jira.util.migration.impl.dto.json.ProjectJsonDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.samearch.jira.util.migration.api.dto.Issue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JsonFileIssueDataExporter implements IssueDataExporter {

    private static final Logger LOG = LoggerFactory.getLogger("jsonExporter");

    private final ObjectMapper objectMapper;
    private final String outputJsonFilePath;
    private final UpdateConfig updateConfig;

    @Autowired
    public JsonFileIssueDataExporter(ObjectMapper objectMapper, @Value("${updatedExportedIssuesFilePath}") String updatedExportedIssuesFilePath, UpdateConfig updateConfig) {
        this.objectMapper = objectMapper;
        this.outputJsonFilePath = updatedExportedIssuesFilePath;
        this.updateConfig = updateConfig;
    }

    @Override
    public void exportIssuesData(IssuesDataRepository dataRepository) {
        var actualDataRepository = (updateConfig.dataGenerationConfig().generateIssuesData())
                ? dataRepository
                : new DefaultIssuesDataRepository();
        var issuesJsonObject = ExportedIssuesDocumentJsonDto.buildEmpty();

        if (updateConfig.dataGenerationConfig().generateLinksData()) {
            var linkedIssuesData = attachIssueLinksData(issuesJsonObject, dataRepository);
            actualDataRepository.mergeIssuesData(linkedIssuesData);
        }

        var projectDtos = actualDataRepository.getProjects().stream()
            .map(ProjectJsonDto::fromProject)
            .collect(Collectors.toList());
        issuesJsonObject.setProjects(projectDtos);

        try {
            writeIssuesToFile(issuesJsonObject);
        } catch (IOException e) {
            LOG.error("Can't write JSON-data to {}: {}", outputJsonFilePath, e.getMessage());
        }
    }

    private Set<Issue> attachIssueLinksData(ExportedIssuesDocumentJsonDto issuesJsonObject, IssuesDataRepository dataRepository) {
        var additionalIssues = new HashSet<Issue>();
        var issueLinkDtos = new HashSet<IssueLinkJsonDto>();
        dataRepository.getProjects().stream()
                .map(Project::getIssues)
                .flatMap(List::stream)
                .map(Issue::issueLinks)
                .flatMap(List::stream)
                .map(issueLink -> {
                    var srcIssue =  new Issue();
                    srcIssue.setId(issueLink.sourceId());
                    srcIssue.setKey(issueLink.sourceKey());
                    additionalIssues.add(srcIssue);
                    var dstIssue =  new Issue();
                    dstIssue.setId(issueLink.destinationId());
                    dstIssue.setKey(issueLink.destinationKey());
                    additionalIssues.add(dstIssue);
                    return IssueLinkJsonDto.fromIssueLink(issueLink);
                })
                .forEach(issueLinkDtos::add);
        if (!additionalIssues.isEmpty()) {
            dataRepository.mergeIssuesData(additionalIssues);
        }
        issuesJsonObject.setLinks(issueLinkDtos);
        return additionalIssues;
    }

    private void writeIssuesToFile(ExportedIssuesDocumentJsonDto exportedIssuesJsonObject) throws IOException {
        if (exportedIssuesJsonObject == null) {
            LOG.error("can't write issues data - data is empty");
            return;
        }

        LOG.info("write updated issues data to {}", outputJsonFilePath);
        File outFile = new File(outputJsonFilePath);
        if (outFile.exists()) {
            LOG.info("target output file already exists - remove it");
            Files.delete(outFile.toPath());
        }
        objectMapper.writeValue(outFile, exportedIssuesJsonObject);
        LOG.info("issues data write success");
    }

}
