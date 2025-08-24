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

import org.samearch.jira.util.migration.api.dto.Issue
import org.samearch.jira.util.migration.api.updater.IssueUpdater

import java.util.stream.Collectors

/**
 * Обновляет логины наблюдателей задачи
 */
class WatchersUpdater implements IssueUpdater {

    private final CommonUpdaterUtils commonUpdaterUtils

    WatchersUpdater(CommonUpdaterUtils commonUpdaterUtils) {
        this.commonUpdaterUtils = commonUpdaterUtils
    }

    @Override
    Optional<Issue> updateIssue(Issue issue) {
        var watchers = issue.getWatchers()
        if (watchers != null) {
            var mappedWatchers = issue.getWatchers().stream()
                .map(commonUpdaterUtils::mapApplicationUserLogin)
                .collect(Collectors.toList())
            issue.setWatchers(mappedWatchers)
        }
        return Optional.of(issue)
    }

}
