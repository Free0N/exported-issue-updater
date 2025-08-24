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

package org.samearch.jira.util.migration.api.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record MappingConfig(
	List<String> removedFieldTypes,
	List<String> removedFieldNames,
	List<String> userNameFieldTypes,
	List<String> ignoredUserNames,
	List<String> updatedUserNamesInHistory,
	List<CustomFieldTypeMapping> customFieldTypeMappings,
	Map<String, String> userNameMapping,
	Map<String, String> issueTypeMapping,
	Map<String, String> customFieldsMapping,
	Map<String, String> statusMapping,
	Map<String, String> linkNameMapping
) {

	public Optional<String> getMappedCfType(String cfName, String oldCfType) {
		if (customFieldTypeMappings == null) {
			return Optional.empty();
		}
		return customFieldTypeMappings.stream()
			.filter(cfTypeMapping -> Objects.equals(cfName, cfTypeMapping.fieldName()))
			.filter(cfTypeMapping -> Objects.equals(oldCfType, cfTypeMapping.oldType()))
			.map(CustomFieldTypeMapping::newType)
			.findFirst();
	}

	public Optional<String> getMappedUserName(String oldUserName) {
		if (userNameMapping == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(userNameMapping.get(oldUserName));
	}

	public Optional<String> getMappedIssueType(String srcIssueType) {
		if (issueTypeMapping == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(issueTypeMapping.get(srcIssueType));
	}

	public Optional<String> getMappedCustomFieldName(String oldCfName) {
		if (customFieldsMapping == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(customFieldsMapping.get(oldCfName));
	}

	public Optional<String> getMappedStatus(String oldStatus) {
		if (statusMapping == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(statusMapping.get(oldStatus));
	}

	public Optional<String> getMappedLinkName(String oldLinkName) {
		if (linkNameMapping == null) {
			return Optional.empty();
		}
		return Optional.ofNullable(linkNameMapping.get(oldLinkName));
	}

}
