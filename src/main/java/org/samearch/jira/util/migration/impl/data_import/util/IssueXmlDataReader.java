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

package org.samearch.jira.util.migration.impl.data_import.util;

import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.CustomFieldReader;
import org.samearch.jira.util.migration.impl.data_import.util.xml.FsmIssuesDataXmlReader;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.springframework.stereotype.Component;

import org.samearch.jira.util.migration.impl.dto.xml.IssueXmlDto;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class IssueXmlDataReader {

	private final List<CustomFieldReader> cfDataReaders;

	public IssueXmlDataReader(List<CustomFieldReader> cfDataReaders) {
		this.cfDataReaders = cfDataReaders;
	}

	public Map<String, IssueXmlDto> readIssuesData(InputStream issuesXmlData) {

		try {
			var fsm = new Fsm();
			var xmlReader = new FsmIssuesDataXmlReader(issuesXmlData, fsm, cfDataReaders);
			var issues = xmlReader.processXmlData();
			return issues.stream().collect(Collectors.toMap(IssueXmlDto::key, i -> i));
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}

	}

}
