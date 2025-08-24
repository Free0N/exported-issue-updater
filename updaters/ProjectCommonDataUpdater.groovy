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

import org.samearch.jira.util.migration.api.dto.Component
import org.samearch.jira.util.migration.api.dto.Project
import org.samearch.jira.util.migration.api.updater.ProjectUpdater

/**
 * Вносит изменения о проекте
 * * изменяет логин владельца проекта (project lead)
 */
class ProjectCommonDataUpdater implements ProjectUpdater {

    private final CommonUpdaterUtils commonUtils

    ProjectCommonDataUpdater(CommonUpdaterUtils commonUtils) {
        this.commonUtils = commonUtils
    }

    @Override
    Optional<Project> updateProject(Project project) {
        updateProjectKey(project)
        updateProjectLeadLogin(project)
        updateComponentsLeadLogin(project)
        return Optional.of(project)
    }

    private void updateProjectKey(Project project) {
        project.key = commonUtils.mapProjectKey(project.key)
    }

    private void updateProjectLeadLogin(Project project) {
        project.lead = commonUtils.mapApplicationUserLogin(project.lead)
    }

    private void updateComponentsLeadLogin(Project project) {
        List<Component> components = project.components
        if (components == null || components.isEmpty()) {
            return
        }
        components.forEach { componentDto ->
            componentDto.lead = commonUtils.mapApplicationUserLogin(componentDto.lead)
        }
        project.setComponents(components)
    }

}
