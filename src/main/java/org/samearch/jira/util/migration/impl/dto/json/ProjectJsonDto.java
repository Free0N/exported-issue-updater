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

package org.samearch.jira.util.migration.impl.dto.json;

import org.samearch.jira.util.migration.api.dto.Project;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectJsonDto {

    private String externalName;
    private String name;
    private String description;
    private String key;
    private String lead;
    private String projectCategoryName;
    private String type;
    private Integer assigneeType;
    private List<VersionJsonDto> versions;
    private List<ComponentJsonDto> components;
    private List<IssueJsonDto> issues;

    public String getExternalName() {
        return externalName;
    }

    public void setExternalName(String externalName) {
        this.externalName = externalName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getProjectCategoryName() {
        return projectCategoryName;
    }

    public void setProjectCategoryName(String projectCategoryName) {
        this.projectCategoryName = projectCategoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(Integer assigneeType) {
        this.assigneeType = assigneeType;
    }

    public List<VersionJsonDto> getVersions() {
        return versions;
    }

    public void setVersions(List<VersionJsonDto> versions) {
        this.versions = versions;
    }

    public List<ComponentJsonDto> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentJsonDto> components) {
        this.components = components;
    }

    public List<IssueJsonDto> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueJsonDto> issues) {
        this.issues = issues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectJsonDto that)) return false;
        return Objects.equals(assigneeType, that.assigneeType) && Objects.equals(externalName, that.externalName) && Objects.equals(name, that.name) && Objects.equals(key, that.key) && Objects.equals(lead, that.lead) && Objects.equals(projectCategoryName, that.projectCategoryName) && Objects.equals(type, that.type) && Objects.equals(issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalName, name, key, lead, projectCategoryName, type, assigneeType, issues);
    }

    public Project toProject() {
        var project = new Project();
        project.setExternalName(externalName);
        project.setName(name);
        project.setDescription(description);
        project.setKey(key);
        project.setLead(lead);
        project.setProjectCategoryName(projectCategoryName);
        project.setType(type);
        project.setAssigneeType(assigneeType);
        var versions = (this.versions != null && !this.versions.isEmpty())
                ? this.versions.stream().map(VersionJsonDto::toVersion).filter(Objects::nonNull).collect(Collectors.toList())
                : null;
        project.setVersions(versions);
        var components = (this.components != null && !this.components.isEmpty())
                ? this.components.stream().map(ComponentJsonDto::toComponent).filter(Objects::nonNull).collect(Collectors.toList())
                : null;
        project.setComponents(components);
        var issues = (this.issues != null && !this.issues.isEmpty())
                ? this.issues.stream().map(IssueJsonDto::toIssue).filter(Objects::nonNull).collect(Collectors.toList())
                : null;
        project.setIssues(issues);
        return project;
    }

    public static ProjectJsonDto fromProject(Project project) {
        var projectDto = new ProjectJsonDto();
        projectDto.setExternalName(project.getExternalName());
        projectDto.setName(project.getName());
        projectDto.setDescription(project.getDescription());
        projectDto.setKey(project.getKey());
        projectDto.setLead(project.getLead());
        projectDto.setProjectCategoryName(project.getProjectCategoryName());
        projectDto.setType(project.getType());
        projectDto.setAssigneeType(project.getAssigneeType());
        var versionDtos = project.getVersions() == null
            ? null
            : project.getVersions().stream()
                .map(VersionJsonDto::fromVersion)
                .collect(Collectors.toList());
        projectDto.setVersions(versionDtos);
        var componentDtos = project.getComponents() == null
            ? null
            : project.getComponents().stream()
                .map(ComponentJsonDto::fromComponent)
                .collect(Collectors.toList());
        projectDto.setComponents(componentDtos);
        var issueDtos = project.getIssues() == null
            ? null
            : project.getIssues().stream()
                .map(IssueJsonDto::fromIssue)
                .collect(Collectors.toList());
        projectDto.setIssues(issueDtos);
        return projectDto;
    }

}
