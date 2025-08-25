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

package org.samearch.jira.util.migration.application;

import org.samearch.jira.util.migration.api.updater.AttachmentUpdater;
import org.samearch.jira.util.migration.api.updater.CommentUpdater;
import org.samearch.jira.util.migration.api.updater.CustomFieldUpdater;
import org.samearch.jira.util.migration.api.updater.IssueUpdater;
import org.samearch.jira.util.migration.api.updater.ProjectUpdater;
import org.samearch.jira.util.migration.api.updater.WorklogUpdater;
import org.samearch.jira.util.migration.application.groovy.GroovyIntegration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

public class App {

    public static void main(String[] args) {
        try (var applicationContext = new AnnotationConfigApplicationContext()) {
            var groovyIntegration = new GroovyIntegration(applicationContext.getClassLoader(), "./updaters");
            applicationContext.setClassLoader(groovyIntegration.getGroovyClassLoader());
            var cliPropertiesSource = new SimpleCommandLinePropertySource(args);
            applicationContext.getEnvironment().getPropertySources().addFirst(cliPropertiesSource);
            applicationContext.register(SpringApplicationConfiguration.class);
            var groovyDataUpdaters = groovyIntegration.loadGroovyClasses();
            groovyDataUpdaters.stream()
                    .filter(App::isDataUpdater)
                    .forEach(applicationContext::register);
            applicationContext.refresh();
            applicationContext.getBean(ExportedIssuesUpdater.class)
                    .fetchAndUpdateIssuesData();
        }
    }

    private static boolean isDataUpdater(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class)
                || AttachmentUpdater.class.isAssignableFrom(clazz)
                || CommentUpdater.class.isAssignableFrom(clazz)
                || CustomFieldUpdater.class.isAssignableFrom(clazz)
                || IssueUpdater.class.isAssignableFrom(clazz)
                || ProjectUpdater.class.isAssignableFrom(clazz)
                || WorklogUpdater.class.isAssignableFrom(clazz)
                ;
    }

}
