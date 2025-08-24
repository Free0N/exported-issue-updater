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

package org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.links;

import org.samearch.jira.util.migration.impl.dto.xml.IssueLinkXmlDto;

public class IssueLinkDtoBuilder {

	private final String linkTypeName;
	private String sourceIssueId;
	private String sourceIssueKey;
	private String destinationIssueId;
	private String destinationIssueKey;

	public IssueLinkDtoBuilder(String linkTypeName) {
		if (linkTypeName == null || linkTypeName.isBlank()) {
			throw new IllegalStateException("Issue link type name can't be empty");
		}
		this.linkTypeName = linkTypeName;
	}

	public IssueLinkXmlDto build() {
		if (sourceIssueId == null) {
			throw new IllegalStateException("Can't create issue link: source issue id not set");
		}
		if (sourceIssueKey == null) {
			throw new IllegalStateException("Can't create issue link: source issue key not set");
		}
		if (destinationIssueId == null) {
			throw new IllegalStateException("Can't create issue link: destination issue id not set");
		}
		if (destinationIssueKey == null) {
			throw new IllegalStateException("Can't create issue link: source issue key not set");
		}
		var linkDto = new IssueLinkXmlDto(linkTypeName, sourceIssueId, sourceIssueKey, destinationIssueId, destinationIssueKey);
		sourceIssueId = null;
		sourceIssueKey = null;
		destinationIssueId = null;
		destinationIssueKey = null;
		return linkDto;
	}

	public void setSourceIssueId(String sourceIssueId) {
		this.sourceIssueId = sourceIssueId;
	}

	public void setSourceIssueKey(String sourceIssueKey) {
		this.sourceIssueKey = sourceIssueKey;
	}

	public void setDestinationIssueId(String destinationIssueId) {
		this.destinationIssueId = destinationIssueId;
	}

	public void setDestinationIssueKey(String destinationIssueKey) {
		this.destinationIssueKey = destinationIssueKey;
	}

	public boolean canBuild() {
		return linkTypeName != null && !linkTypeName.isBlank()
			&& sourceIssueId != null && !sourceIssueId.isBlank()
			&& sourceIssueKey != null && !sourceIssueKey.isBlank()
			&& destinationIssueId != null && !destinationIssueId.isBlank()
			&& destinationIssueKey != null && !destinationIssueKey.isBlank();
	}

}
