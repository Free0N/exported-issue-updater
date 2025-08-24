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

import org.samearch.jira.util.migration.api.IssuesDataRepository;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.samearch.jira.util.migration.api.dto.Project;
import org.samearch.jira.util.migration.api.IssueDataExporter;
import org.samearch.jira.util.migration.api.IssueDataFetcher;
import org.samearch.jira.util.migration.api.exception.DataFetchException;
import org.samearch.jira.util.migration.api.IssueDataProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExportedIssuesUpdater {

    private static final Logger LOG = LoggerFactory.getLogger("mainProcess");

    private final String updatedExportedIssuesFilePath;

    private final UpdateConfig updateConfig;

    private final List<IssueDataFetcher> dataFetchers;
    private final IssuesDataRepository issuesDataRepository;

    private final IssueDataProcessor issueDataProcessor;

    private final List<IssueDataExporter> dataExporters;

    @Autowired
    public ExportedIssuesUpdater(List<IssueDataFetcher> issueDataFetchers,
                                 IssuesDataRepository issuesDataRepository,
                                 @Value("${updatedExportedIssuesFilePath}") String updatedExportedIssuesFilePath,
                                 UpdateConfig updateConfig,
                                 IssueDataProcessor issueDataProcessor,
                                 List<IssueDataExporter> dataExporters) {
        this.dataFetchers = issueDataFetchers;
        this.issuesDataRepository = issuesDataRepository;
        this.updatedExportedIssuesFilePath = updatedExportedIssuesFilePath;
	    this.updateConfig = updateConfig;
        this.issueDataProcessor = issueDataProcessor;
        this.dataExporters = dataExporters;
    }

    public void fetchAndUpdateIssuesData() {
        checkArgs();
        var jqlQueries = updateConfig.dataFetchConfig().partialDataJql();
        for (var jqlQuery: jqlQueries) {
            var projectsData = new ArrayList<Project>();
            for (var dataFetcher: dataFetchers) {
                try {
                    var jqlProjectsData = dataFetcher.fetchIssuesData(jqlQuery);
                    projectsData.addAll(jqlProjectsData);
                } catch (DataFetchException ignore) {}
            }
            for (var projectData: projectsData) {
                issuesDataRepository.mergeIssuesData(projectData);
            }
        }
        issueDataProcessor.process(issuesDataRepository);
        for (var dataExporter: dataExporters) {
            dataExporter.exportIssuesData(issuesDataRepository);
        }
    }

    private void checkArgs() {
        if (updatedExportedIssuesFilePath == null || updatedExportedIssuesFilePath.startsWith("$")) {
            LOG.error("Updated data file path not set. Use --updatedExportedIssuesFilePath=path/to/file to define it");
            System.exit(1);
        }
    }

}
