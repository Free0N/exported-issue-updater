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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import java.util.regex.Matcher
import java.util.regex.Pattern

@Component
class CommonUpdaterUtils {

    private static final Logger LOG_USER_MAPPING = LoggerFactory.getLogger("userMapping")

    private static final Pattern PROJECT_KEY_FROM_ISSUE_KEY_PATTERN = Pattern.compile("^(?<projectKey>[A-Z]+)-(?<issueNumber>[0-9]+)\$");

    private final UpdateConfig updateConfig

    CommonUpdaterUtils(UpdateConfig updateConfig) {
        this.updateConfig = updateConfig
    }

    String mapApplicationUserLogin(String applicationUserLogin) {
        if (applicationUserLogin == null) {
            return null
        }
        var ignoredUserNames = updateConfig.mappingConfig().ignoredUserNames()
        if (ignoredUserNames != null && ignoredUserNames.contains(applicationUserLogin)) {
            LOG_USER_MAPPING.info("skip login mapping for '{}' due ignoring configuration", applicationUserLogin)
            return applicationUserLogin
        }
        if (applicationUserLogin.startsWith("JIRAUSER")) {
            LOG_USER_MAPPING.info("skip login mapping for '{}' due starts with 'JIRAUSER'", applicationUserLogin)
            return applicationUserLogin
        }
        return updateConfig.mappingConfig().getMappedUserName(applicationUserLogin)
                .orElse(applicationUserLogin)
    }

    String getActualIssueKey(String issueKey) {
        if (issueKey == null || issueKey.isEmpty()) {
            return issueKey
        }
        Matcher projectKeyMatcher = PROJECT_KEY_FROM_ISSUE_KEY_PATTERN.matcher(issueKey)
        if (!projectKeyMatcher.matches()) {
            return issueKey
        }
        String mappedProjectKey = projectKeyMatcher.group("projectKey")
        String issueNumber = projectKeyMatcher.group("issueNumber")
        String projectKey = actualProjectKey(mappedProjectKey)
        return "${projectKey}-${issueNumber}"
    }

    String mapProjectKey(String projectKey) {
        if (!projectKeyMappingConfigured()) {
            return projectKey
        }
        return Optional.ofNullable(updateConfig.migratedProjects().get(projectKey))
            .orElse(projectKey)
    }

    String actualProjectKey(String projectKey) {
        if (!projectKeyMappingConfigured()) {
            return projectKey
        }
        return updateConfig.migratedProjects().getOrDefault(projectKey, projectKey)
    }

    private boolean projectKeyMappingConfigured() {
        Map<String, String> projectKeyMappings = updateConfig.migratedProjects()
        return projectKeyMappings != null && !projectKeyMappings.isEmpty()
    }

}
