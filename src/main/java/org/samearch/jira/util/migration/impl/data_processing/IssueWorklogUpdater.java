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

import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.dto.Worklog;
import org.samearch.jira.util.migration.api.updater.WorklogUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IssueWorklogUpdater implements PartialIssueDataUpdater {

    private final List<WorklogUpdater> worklogUpdaters;

    @Autowired
    public IssueWorklogUpdater(List<WorklogUpdater> worklogUpdaters) {
        this.worklogUpdaters = worklogUpdaters;
    }

    @Override
    public void updateIssue(Issue issue) {
        if (worklogUpdaters.isEmpty()) {
            return;
        }
        var updatedWorklogs = new ArrayList<Worklog>();
        for (var worklog: issue.getWorklogs()) {
            var updatedWorklog = updateWorklog(worklog);
            if (updatedWorklog != null) {
                updatedWorklogs.add(updatedWorklog);
            }
        }
        issue.setWorklogs(updatedWorklogs);
    }

    private Worklog updateWorklog(Worklog worklog) {
        var updatedWorklog = worklog;
        for (var worklogUpdater: worklogUpdaters) {
            var worklogUpdateResult = worklogUpdater.updateWorklog(worklog);
            if (worklogUpdateResult.isEmpty()) {
                return null;
            } else {
                updatedWorklog = worklogUpdateResult.get();
            }
        }
        return updatedWorklog;
    }

}
