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

package org.samearch.jira.util.migration.api.dto;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Project extends AbstractDtoObject<Project> {

    private String externalName;
    private String name;
    private String description;
    private String key;
    private String lead;
    private String projectCategoryName;
    private String type;
    private Integer assigneeType;
    private DtoObjectsList<Version> versions;
    private DtoObjectsList<Component> components;
    private DtoObjectsList<Issue> issues;

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

    public List<Version> getVersions() {
        return versions != null
            ? versions.getObjects()
            : null;
    }

    public void setVersions(List<Version> versions) {
        this.versions = new DtoObjectsList<>(versions);
    }

    public List<Component> getComponents() {
        return components != null
            ? components.getObjects()
            : null;
    }

    public void setComponents(List<Component> components) {
        this.components = new DtoObjectsList<>(components);
    }

    public List<Issue> getIssues() {
        return issues != null
            ? issues.getObjects()
            : null;
    }

    public void setIssues(Collection<Issue> issues) {
        this.issues = new DtoObjectsList<>(issues);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project that)) return false;
        return Objects.equals(assigneeType, that.assigneeType) && Objects.equals(externalName, that.externalName) && Objects.equals(name, that.name) && Objects.equals(key, that.key) && Objects.equals(lead, that.lead) && Objects.equals(projectCategoryName, that.projectCategoryName) && Objects.equals(type, that.type) && Objects.equals(issues, that.issues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalName, name, key, lead, projectCategoryName, type, assigneeType, issues);
    }

    @Override
    public void merge(Project otherProject) {
        runIfBlank(externalName, () -> externalName = otherProject.externalName);
        runIfBlank(name, () -> name = otherProject.name);
        runIfBlank(description, () -> description = otherProject.description);
        runIfBlank(key, () -> key = otherProject.key);
        runIfBlank(lead, () -> lead = otherProject.lead);
        runIfBlank(projectCategoryName, () -> projectCategoryName = otherProject.projectCategoryName);
        runIfBlank(type, () -> type = otherProject.type);
        runIfBlank(assigneeType, () -> assigneeType = otherProject.assigneeType);
        versions = DtoObjectsList.createOrMerge(versions, otherProject.versions);
        components = DtoObjectsList.createOrMerge(components, otherProject.components);
        issues = DtoObjectsList.createOrMerge(issues, otherProject.issues);
    }

}
