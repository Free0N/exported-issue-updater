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

package org.samearch.jira.util.migration.impl;

import org.samearch.jira.util.migration.api.IssuesDataRepository;
import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.dto.Project;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class DefaultIssuesDataRepository implements IssuesDataRepository {

    private static final Pattern PROJECT_KEY_PATTERN = Pattern.compile("^(?<projectKey>[A-Z]+)-[0-9]+$");

    private final HashMap<String, Project> projectsByKey = new HashMap<>();
    private final HashMap<String, Issue> issuesByKey = new HashMap<>();

    @Override
    public void mergeIssuesData(Project mergedProject) {
        var mergedProjectKey = mergedProject.getKey();
        if (mergedProjectKey == null || mergedProjectKey.isBlank()) {
            return;
        }
        var updatedProject = projectsByKey.computeIfAbsent(mergedProject.getKey(), projectKey -> new Project());
        updatedProject.merge(mergedProject);
        updatedProject.getIssues().forEach(issue -> issuesByKey.put(issue.getKey(), issue));
    }

    @Override
    public void mergeIssuesData(Collection<Issue> issues) {
        if (issues == null || issues.isEmpty()) {
            return;
        }
        var issuesByProjectKey = new HashMap<String, HashSet<Issue>> ();
        for (var issue: issues) {
            var m = PROJECT_KEY_PATTERN.matcher(issue.getKey());
            if (!m.matches()) {
                return;
            }
            var projectKey = m.group("projectKey");
            issuesByProjectKey.computeIfAbsent(projectKey, pKey -> new HashSet<>()).add(issue);
        }
        for (var projectKey: issuesByProjectKey.keySet()) {
            var project = new Project();
            project.setKey(projectKey);
            project.setIssues(issuesByProjectKey.get(projectKey));
            mergeIssuesData(project);
        }
    }

    @Override
    public Optional<Project> getProjectByKey(String projectKey) {
        return Optional.ofNullable(projectsByKey.get(projectKey));
    }

    @Override
    public Optional<Issue> getIssueByKey(String issueKey) {
        return Optional.ofNullable(issuesByKey.get(issueKey));
    }

    @Override
    public List<Project> getProjects() {
        return new ArrayList<>(projectsByKey.values());
    }

    @Override
    public void removeProject(String projectKey) {
        projectsByKey.remove(projectKey);
    }

    @Override
    public void refreshProject(String oldProjectKey, Project project) {
        projectsByKey.remove(oldProjectKey);
        projectsByKey.put(project.getKey(), project);
    }

}
