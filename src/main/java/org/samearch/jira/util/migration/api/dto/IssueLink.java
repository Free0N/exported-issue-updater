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

package org.samearch.jira.util.migration.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class IssueLink extends AbstractDtoObject<IssueLink> {

    private String name;
    private String sourceId;
    private String sourceKey;
    private String destinationId;
    private String destinationKey;

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String sourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String sourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public String destinationId() {
		return destinationId;
	}

	public void setDestinationId(String destinationId) {
		this.destinationId = destinationId;
	}

	public String destinationKey() {
		return destinationKey;
	}

	public void setDestinationKey(String destinationKey) {
		this.destinationKey = destinationKey;
	}

	@Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IssueLink) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.sourceId, that.sourceId) &&
                Objects.equals(this.sourceKey, that.sourceKey) &&
                Objects.equals(this.destinationId, that.destinationId) &&
                Objects.equals(this.destinationKey, that.destinationKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sourceId, sourceKey, destinationId, destinationKey);
    }

    @Override
    public String toString() {
        return "IssueLink[" +
                "name=" + name + ", " +
                "sourceId=" + sourceId + ", " +
                "sourceKey=" + sourceKey + ", " +
                "destinationId=" + destinationId + ", " +
                "destinationKey=" + destinationKey + ']';
    }

	@Override
	public String getKey() {
		return name + sourceId + destinationId;
	}

	@Override
	public void merge(IssueLink otherObject) {
		runIfBlank(name, () -> name = otherObject.name);
		runIfBlank(sourceId, () -> sourceId = otherObject.sourceId);
		runIfBlank(sourceKey, () -> sourceKey = otherObject.sourceKey);
		runIfBlank(destinationId, () -> destinationId = otherObject.destinationId);
		runIfBlank(destinationKey, () -> destinationKey = otherObject.destinationKey);
	}

	public List<Issue> toIssues() {
		var issues = new ArrayList<Issue>();
		var srcIssue = new Issue();
		srcIssue.setId(sourceId);
		srcIssue.setKey(sourceKey);
		issues.add(srcIssue);
		var dstIssue = new Issue();
		dstIssue.setId(destinationId);
		dstIssue.setKey(destinationKey);
		issues.add(dstIssue);
		return issues;
	}

}
