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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Issue extends AbstractDtoObject<Issue> {

    private String id;
    private String key;
    private String summary;
    private String reporter;
    private String assignee;
    private String description;
    private String issueType;
    private String status;
    private String priority;
    private String resolution;
    private Long created;
    private Long updated;
    private Long duedate;
    private Long resolutionDate;
    private List<String> labels;
    private List<String> components;
    private List<String> affectedVersions;
    private List<String> fixedVersions;
    private List<String> watchers;
    private DtoObjectsList<IssueLink> issueLinks;
    private DtoObjectsList<CustomFieldValue> customFieldValues;
    private DtoObjectsList<Comment> comments;
    private DtoObjectsList<Worklog> worklogs;
    private DtoObjectsList<Attachment> attachments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Long getDuedate() {
        return duedate;
    }

    public void setDuedate(Long duedate) {
        this.duedate = duedate;
    }

    public Long getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(Long resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public List<String> getAffectedVersions() {
        return affectedVersions;
    }

    public void setAffectedVersions(List<String> affectedVersions) {
        this.affectedVersions = affectedVersions;
    }

    public List<String> getFixedVersions() {
        return fixedVersions;
    }

    public void setFixedVersions(List<String> fixedVersions) {
        this.fixedVersions = fixedVersions;
    }

    public List<String> getWatchers() {
        return watchers;
    }

    public void setWatchers(List<String> watchers) {
        this.watchers = watchers;
    }

    public List<IssueLink> issueLinks() {
        if (issueLinks == null) {
            return new ArrayList<>();
        } else {
            return issueLinks.getObjects();
        }
    }

    public void setIssueLinks(List<IssueLink> issueLinks) {
        this.issueLinks = new DtoObjectsList<>(issueLinks);
    }

    public List<CustomFieldValue> getCustomFieldValues() {
        if (customFieldValues == null) {
            return new ArrayList<>();
        } else {
            return customFieldValues.getObjects();
        }
    }

    public void setCustomFieldValues(List<CustomFieldValue> customFieldValues) {
        this.customFieldValues = new DtoObjectsList<>(customFieldValues);
    }

    public List<Comment> getComments() {
        if (comments == null) {
            return new ArrayList<>();
        } else {
            return comments.getObjects();
        }
    }

    public void setComments(List<Comment> comments) {
        this.comments = new DtoObjectsList<>(comments);
    }

    public List<Worklog> getWorklogs() {
        if (worklogs == null) {
            return new ArrayList<>();
        } else {
            return worklogs.getObjects();
        }
    }

    public void setWorklogs(List<Worklog> worklogs) {
        this.worklogs = new DtoObjectsList<>(worklogs);
    }

    public List<Attachment> getAttachments() {
        if (attachments == null) {
            return new ArrayList<>();
        } else {
            return attachments.getObjects();
        }
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = new DtoObjectsList<>(attachments);
    }

    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new DtoObjectsList<>();
        }
        attachments.add(attachment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue issueJsonDto)) return false;
        return Objects.equals(created, issueJsonDto.created) && Objects.equals(updated, issueJsonDto.updated) && Objects.equals(resolutionDate, issueJsonDto.resolutionDate) && Objects.equals(key, issueJsonDto.key) && Objects.equals(summary, issueJsonDto.summary) && Objects.equals(reporter, issueJsonDto.reporter) && Objects.equals(assignee, issueJsonDto.assignee) && Objects.equals(description, issueJsonDto.description) && Objects.equals(issueType, issueJsonDto.issueType) && Objects.equals(status, issueJsonDto.status) && Objects.equals(priority, issueJsonDto.priority) && Objects.equals(resolution, issueJsonDto.resolution) && Objects.equals(customFieldValues, issueJsonDto.customFieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, summary, reporter, assignee, description, issueType, status, priority, resolution, created, updated, resolutionDate, customFieldValues);
    }

    @Override
    public void merge(Issue otherIssue) {
        runIfBlank(id, () -> id = otherIssue.id);
        runIfBlank(key, () -> key = otherIssue.key);
        runIfBlank(summary, () -> summary = otherIssue.assignee);
        runIfBlank(reporter, () -> reporter = otherIssue.reporter);
        runIfBlank(assignee, () -> assignee = otherIssue.assignee);
        runIfBlank(description, () -> description = otherIssue.description);
        runIfBlank(issueType, () -> issueType = otherIssue.issueType);
        runIfBlank(status, () -> status = otherIssue.status);
        runIfBlank(priority, () -> priority = otherIssue.priority);
        runIfBlank(resolution, () -> resolution = otherIssue.resolution);
        runIfBlank(created, () -> created = otherIssue.created);
        runIfBlank(updated, () -> updated = otherIssue.updated);
        runIfBlank(duedate, () -> duedate = otherIssue.duedate);
        runIfBlank(resolutionDate, () -> resolutionDate = otherIssue.resolutionDate);
        labels = mergeStringList(labels, otherIssue.labels);
        components = mergeStringList(components, otherIssue.components);
        affectedVersions = mergeStringList(affectedVersions, otherIssue.affectedVersions);
        fixedVersions = mergeStringList(fixedVersions, otherIssue.fixedVersions);
        watchers = mergeStringList(watchers, otherIssue.watchers);
        issueLinks = DtoObjectsList.createOrMerge(issueLinks, otherIssue.issueLinks);
        customFieldValues = DtoObjectsList.createOrMerge(customFieldValues, otherIssue.customFieldValues);
        comments = DtoObjectsList.createOrMerge(comments, otherIssue.comments);
        attachments = DtoObjectsList.createOrMerge(attachments, otherIssue.attachments);
        worklogs = DtoObjectsList.createOrMerge(worklogs, otherIssue.worklogs);
    }

    private List<String> mergeStringList(List<String> currentList, List<String> otherList) {
        if (otherList == null || otherList.isEmpty()) {
            return currentList;
        }
        if (currentList == null) {
            return new ArrayList<>(otherList);
        } else {
            var mergedValues = new HashSet<>(currentList);
            mergedValues.addAll(otherList);
            return new ArrayList<>(mergedValues);
        }
    }

}
