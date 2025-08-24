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

import org.samearch.jira.util.migration.api.dto.CustomFieldValue;
import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.updater.CustomFieldUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IssueCustomFieldUpdater implements PartialIssueDataUpdater {

    private final List<CustomFieldUpdater> customFieldUpdaters;

    @Autowired
    public IssueCustomFieldUpdater(List<CustomFieldUpdater> customFieldUpdaters) {
        this.customFieldUpdaters = customFieldUpdaters;
    }

    @Override
    public void updateIssue(Issue issue) {
        if (customFieldUpdaters.isEmpty()) {
            return;
        }
        var updatedCfValues = new ArrayList<CustomFieldValue>();
        for (var cfValue: issue.getCustomFieldValues()) {
            var updatedCfValue = updateCfValue(cfValue);
            if (updatedCfValue != null) {
                updatedCfValues.add(updatedCfValue);
            }
        }
        issue.setCustomFieldValues(updatedCfValues);
    }

    private CustomFieldValue updateCfValue(CustomFieldValue cfValue) {
        var updatedCfValue = cfValue;
        for (var cfUpdater: customFieldUpdaters) {
            var cfUpdateResult = cfUpdater.updateCustomField(updatedCfValue);
            if (cfUpdateResult.isEmpty()) {
                return null;
            } else {
                updatedCfValue = cfUpdateResult.get();
            }
        }
        return updatedCfValue;
    }
}
