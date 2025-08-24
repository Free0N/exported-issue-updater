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

package org.samearch.jira.util.migration.application.util;

import org.samearch.jira.util.migration.api.config.DataFetchConfig;
import org.samearch.jira.util.migration.api.config.JiraInstanceConfig;
import org.samearch.jira.util.migration.api.config.UpdateConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Инкапсулирует логику проверки конфигурации приложения.
 */
@Component
public class UpdateConfigChecker {

    private static final Logger LOG = LoggerFactory.getLogger("check");

    public void checkConfig(UpdateConfig updateConfig) {
        checkDataFetchConfigSection(updateConfig);
    }

    private static void checkDataFetchConfigSection(UpdateConfig updateConfig) {
        var dataFetchConfig = updateConfig.dataFetchConfig();
        expectDataFetchConfigSectionExists(dataFetchConfig);
        expectPartialDataJqlSectionIsNotEmpty(dataFetchConfig);
    }

    private static void expectDataFetchConfigSectionExists(DataFetchConfig dataFetchConfig) {
        if (dataFetchConfig == null) {
            LOG.error("Data fetch config is not defined or empty. Add correct data to dataFetchConfig section in configuration file");
            System.exit(1);
        }
        expectConnectionSettingsIsSet(dataFetchConfig);
    }

    private static void expectPartialDataJqlSectionIsNotEmpty(DataFetchConfig dataFetchConfig) {
        if (dataFetchConfig.partialDataJql() == null || dataFetchConfig.partialDataJql().isEmpty()) {
            LOG.error("No partialDataJql defined. Add dataFetchConfig.partialDataJql section with jql queries list to configuration file");
            System.exit(1);
        }
    }

    private static void expectConnectionSettingsIsSet(DataFetchConfig dataFetchConfig) {
        var jiraInstanceConfig = dataFetchConfig.jiraAuthSettings();
        if (jiraInstanceConfig == null) {
            LOG.error("No Jira auth settings found or it is empty. Add correct dataFetchConfig.jiraAuthSettings section to configuration file");
            System.exit(1);
        }
        expectJiraBaseUrlIsSet(jiraInstanceConfig);
        expectJiraAuthDataIsSet(jiraInstanceConfig);
    }

    private static void expectJiraBaseUrlIsSet(JiraInstanceConfig jiraInstanceConfig) {
        if (jiraInstanceConfig.baseUrl() == null || jiraInstanceConfig.baseUrl().isBlank()) {
            LOG.error("Jira base url not set. Add dataFetchConfig.jiraAuthSettings.baseUrl parameter to configuration file");
            System.exit(1);
        }
    }

    private static void expectJiraAuthDataIsSet(JiraInstanceConfig jiraInstanceConfig) {
        if (
                (jiraInstanceConfig.bearerToken() == null || jiraInstanceConfig.bearerToken().isEmpty())
                && (
                        jiraInstanceConfig.basicLogin() == null || jiraInstanceConfig.basicLogin().isEmpty()
                        || jiraInstanceConfig.basicPassword() == null || jiraInstanceConfig.basicPassword().isEmpty()
                )
        ) {
            LOG.error("No Bearer or Basic auth is set. Add this parameters to dataFetchConfig.jiraAuthSettings section:");
            LOG.error("\tbearerToken - for Bearer auth");
            LOG.error("\tbasicLogin and basicPassword - for Basic auth");
            System.exit(1);
        }
    }

}
