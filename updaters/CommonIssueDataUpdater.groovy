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

import org.samearch.jira.util.migration.api.config.UpdateConfig
import org.samearch.jira.util.migration.api.dto.Issue
import org.samearch.jira.util.migration.api.updater.IssueUpdater

/**
 * Вносит изменения в основных полях задачи:
 * * assignee, reporter
 * * тип задачи (mappingConfig.issueTypeMapping)
 * * статус задачи (mappingConfig.statusMapping)
 * * добавляет поле externalId для корректной работы механизма создания связей во время импорта
 *   так же это поле принимает участие в заполнении информации о связях между задачами в момент формирования
 *   данных для импорта
 */
class CommonIssueDataUpdater implements IssueUpdater {

    private final UpdateConfig updateConfig
    private final CommonUpdaterUtils commonUpdaterUtils

    CommonIssueDataUpdater(UpdateConfig updateConfig, CommonUpdaterUtils commonUpdaterUtils) {
        this.updateConfig = updateConfig
        this.commonUpdaterUtils = commonUpdaterUtils
    }

    @Override
    Optional<Issue> updateIssue(Issue issue) {
        mapIssueKey(issue)
        mapIssueCommonUsers(issue)
        mapIssueType(issue)
        mapIssueStatus(issue)
        return Optional.of(issue)
    }

    private void mapIssueKey(Issue issue) {
        issue.setKey(commonUpdaterUtils.getActualIssueKey(issue.getKey()))
    }

    private void mapIssueCommonUsers(Issue issue) {
        var mappedAssignee = commonUpdaterUtils.mapApplicationUserLogin(issue.getAssignee())
        issue.setAssignee(mappedAssignee)
        var mappedReporter = commonUpdaterUtils.mapApplicationUserLogin(issue.getReporter())
        issue.setReporter(mappedReporter)
    }

    private void mapIssueType(Issue issue) {
        updateConfig
            .mappingConfig()
            .getMappedIssueType(issue.getIssueType())
            .ifPresent(issue::setIssueType)
    }

    private void mapIssueStatus(Issue issue) {
        updateConfig
            .mappingConfig()
            .getMappedStatus(issue.getStatus())
            .ifPresent(issue::setStatus)
    }

}
