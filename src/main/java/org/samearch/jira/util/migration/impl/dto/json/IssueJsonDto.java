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

import org.samearch.jira.util.migration.api.dto.Issue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IssueJsonDto {

    private String externalId;
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
    private List<CustomFieldValueJsonDto> customFieldValues;
    private List<CommentJsonDto> comments;
    private List<WorklogJsonDto> worklogs;
    private List<AttachmentJsonDto> attachments;

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
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

    public List<CustomFieldValueJsonDto> getCustomFieldValues() {
        return customFieldValues;
    }

    public void setCustomFieldValues(List<CustomFieldValueJsonDto> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

    public List<CommentJsonDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentJsonDto> comments) {
        this.comments = comments;
    }

    public List<WorklogJsonDto> getWorklogs() {
        return worklogs;
    }

    public void setWorklogs(List<WorklogJsonDto> worklogs) {
        this.worklogs = worklogs;
    }

    public List<AttachmentJsonDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentJsonDto> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(AttachmentJsonDto attachment) {
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        attachments.add(attachment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IssueJsonDto issueJsonDto)) return false;
        return Objects.equals(created, issueJsonDto.created) && Objects.equals(updated, issueJsonDto.updated) && Objects.equals(resolutionDate, issueJsonDto.resolutionDate) && Objects.equals(key, issueJsonDto.key) && Objects.equals(summary, issueJsonDto.summary) && Objects.equals(reporter, issueJsonDto.reporter) && Objects.equals(assignee, issueJsonDto.assignee) && Objects.equals(description, issueJsonDto.description) && Objects.equals(issueType, issueJsonDto.issueType) && Objects.equals(status, issueJsonDto.status) && Objects.equals(priority, issueJsonDto.priority) && Objects.equals(resolution, issueJsonDto.resolution) && Objects.equals(customFieldValues, issueJsonDto.customFieldValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, summary, reporter, assignee, description, issueType, status, priority, resolution, created, updated, resolutionDate, customFieldValues);
    }

    public Issue toIssue() {
        var issue = new Issue();
        issue.setId(externalId);
        issue.setKey(key);
        issue.setSummary(summary);
        issue.setReporter(reporter);
        issue.setAssignee(assignee);
        issue.setDescription(description);
        issue.setIssueType(issueType);
        issue.setStatus(status);
        issue.setPriority(priority);
        issue.setResolution(resolution);
        issue.setCreated(created);
        issue.setUpdated(updated);
        issue.setDuedate(duedate);
        issue.setResolutionDate(resolutionDate);
        issue.setLabels(copyObjectList(labels));
        issue.setComponents(copyObjectList(components));
        issue.setAffectedVersions(copyObjectList(affectedVersions));
        issue.setFixedVersions(copyObjectList(fixedVersions));
        issue.setWatchers(copyObjectList(watchers));
        var customFieldValues = mapIfNotEmpty(this.customFieldValues, CustomFieldValueJsonDto::toCustomFieldValue);
        issue.setCustomFieldValues(customFieldValues);
        var comments = mapIfNotEmpty(this.comments, CommentJsonDto::toComment);
        issue.setComments(comments);
        var workLogs = mapIfNotEmpty(this.worklogs, WorklogJsonDto::toWorklog);
        issue.setWorklogs(workLogs);
        var attachments = mapIfNotEmpty(this.attachments, AttachmentJsonDto::toAttachment);
        issue.setAttachments(attachments);
        return issue;
    }

    public static IssueJsonDto fromIssue(Issue issue) {
        var issueJsonDto = new IssueJsonDto();
        issueJsonDto.setExternalId(issue.getId());
        issueJsonDto.setKey(issue.getKey());
        issueJsonDto.setSummary(issue.getSummary());
        issueJsonDto.setReporter(issue.getReporter());
        issueJsonDto.setAssignee(issue.getAssignee());
        issueJsonDto.setDescription(issue.getDescription());
        issueJsonDto.setIssueType(issue.getIssueType());
        issueJsonDto.setStatus(issue.getStatus());
        issueJsonDto.setPriority(issue.getPriority());
        issueJsonDto.setResolution(issue.getResolution());
        issueJsonDto.setCreated(issue.getCreated());
        issueJsonDto.setUpdated(issue.getUpdated());
        issueJsonDto.setDuedate(issue.getDuedate());
        issueJsonDto.setResolutionDate(issue.getResolutionDate());
        issueJsonDto.setLabels(issue.getLabels() == null ? null : new ArrayList<>(issue.getLabels()));
        issueJsonDto.setComponents(issue.getComponents() == null ? null : new ArrayList<>(issue.getComponents()));
        issueJsonDto.setAffectedVersions(issue.getAffectedVersions() == null ? null : new ArrayList<>(issue.getAffectedVersions()));
        issueJsonDto.setFixedVersions(issue.getFixedVersions() == null ? null : new ArrayList<>(issue.getFixedVersions()));
        issueJsonDto.setWatchers(issue.getWatchers() == null ? null : new ArrayList<>(issue.getWatchers()));
        var cfValuesDto = mapIfNotEmpty(issue.getCustomFieldValues(), CustomFieldValueJsonDto::fromCustomField);
        issueJsonDto.setCustomFieldValues(cfValuesDto);
        var commentDtos = mapIfNotEmpty(issue.getComments(), CommentJsonDto::fromComment);
        issueJsonDto.setComments(commentDtos);
        var attachmentDtos = mapIfNotEmpty(issue.getAttachments(), AttachmentJsonDto::fromAttachment);
        issueJsonDto.setAttachments(attachmentDtos);
        var worklogDtos = mapIfNotEmpty(issue.getWorklogs(), WorklogJsonDto::fromWorklog);
        issueJsonDto.setWorklogs(worklogDtos);
        return issueJsonDto;
    }

    private static <T, R> List<R> mapIfNotEmpty(List<T> list, Function<T, R> mapFunction) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().filter(Objects::nonNull).map(mapFunction).collect(Collectors.toList());
    }

    private <T> List<T> copyObjectList(List<T> srcList) {
        return srcList != null
            ? new ArrayList<>(srcList)
            : null;
    }

}
