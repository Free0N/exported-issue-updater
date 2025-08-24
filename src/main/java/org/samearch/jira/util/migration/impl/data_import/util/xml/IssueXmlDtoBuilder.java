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

package org.samearch.jira.util.migration.impl.data_import.util.xml;

import org.samearch.jira.util.migration.impl.dto.xml.AttachmentXmlDto;
import org.samearch.jira.util.migration.impl.dto.xml.CustomFieldValueXmlDto;
import org.samearch.jira.util.migration.impl.dto.xml.IssueLinkXmlDto;
import org.samearch.jira.util.migration.impl.dto.xml.IssueXmlDto;

import java.util.ArrayList;
import java.util.List;

public class IssueXmlDtoBuilder {

	private String id;
	private String issueKey;
	private final List<AttachmentXmlDto> attachments = new ArrayList<>();
	private final List<IssueLinkXmlDto> links = new ArrayList<>();
	private final List<CustomFieldValueXmlDto> customFields = new ArrayList<>();

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setIssueKey(String issueKey) {
		this.issueKey = issueKey;
	}

	public String getIssueKey() {
		return issueKey;
	}

	public void addAttachment(AttachmentXmlDto attachment) {
		attachments.add(attachment);
	}

	public void addCustomFieldValue(CustomFieldValueXmlDto customFieldValue) {
		customFields.add(customFieldValue);
	}

	public IssueXmlDto build() {
		if (id == null) {
			throw new IllegalStateException("Issue id can not be null");
		}
		if (issueKey == null || issueKey.isBlank()) {
			throw new IllegalStateException("Issue key can not be empty");
		}
		return new IssueXmlDto(id, issueKey, attachments, links, customFields);
	}

	public void addLink(IssueLinkXmlDto issueLinkDto) {
		links.add(issueLinkDto);
	}

}
