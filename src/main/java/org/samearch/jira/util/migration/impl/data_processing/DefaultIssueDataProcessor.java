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

package org.samearch.jira.util.migration.impl.data_processing;

import org.samearch.jira.util.migration.api.IssuesDataRepository;
import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.dto.Project;
import org.samearch.jira.util.migration.api.updater.IssueUpdater;
import org.samearch.jira.util.migration.api.updater.ProjectUpdater;
import org.samearch.jira.util.migration.api.IssueDataProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultIssueDataProcessor implements IssueDataProcessor {

    private final List<ProjectUpdater> projectUpdaters;
    private final List<IssueUpdater> issueUpdaters;

    private final List<PartialIssueDataUpdater> partialIssueDataUpdaters;

    @Autowired
    public DefaultIssueDataProcessor(List<IssueUpdater> issueUpdaters,
                                     List<ProjectUpdater> projectUpdaters,
                                     List<PartialIssueDataUpdater> partialIssueDataUpdaters) {
        this.issueUpdaters = issueUpdaters;
        this.projectUpdaters = projectUpdaters;
        this.partialIssueDataUpdaters = partialIssueDataUpdaters;
    }

    @Override
    public void process(IssuesDataRepository issuesDataRepository) {
        for (var project: issuesDataRepository.getProjects()) {
            var projectUpdateResult = updateProject(issuesDataRepository, project);
            if (!projectUpdateResult) {
                continue;
            }
            var updatedIssues = new ArrayList<Issue>();
            for (var issue: project.getIssues()) {
                var updatedIssue = updateIssue(issue);
                if (updatedIssue == null) {
                    continue;
                }
                partialIssueDataUpdaters.forEach(partialIssueDataUpdater -> partialIssueDataUpdater.updateIssue(updatedIssue));
                updatedIssues.add(updatedIssue);
            }
            project.setIssues(updatedIssues);
        }
    }

    private boolean updateProject(IssuesDataRepository issuesDataRepository, Project project) {
        var oldProjectKey = project.getKey();
        for (var projectUpdater: projectUpdaters) {
            var projectUpdateResult = projectUpdater.updateProject(project);
            if (projectUpdateResult.isEmpty()) {
                issuesDataRepository.removeProject(oldProjectKey);
                return false;
            } else {
                issuesDataRepository.refreshProject(oldProjectKey, projectUpdateResult.get());
            }
        }
        return true;
    }

    private Issue updateIssue(Issue issue) {
        var updatedIssue = issue;
        for (var issueUpdater: issueUpdaters) {
            var issueUpdateResult = issueUpdater.updateIssue(updatedIssue);
            if (issueUpdateResult.isEmpty()) {
                return null;
            } else {
                updatedIssue = issueUpdateResult.get();
            }
        }
        return updatedIssue;
    }

}
