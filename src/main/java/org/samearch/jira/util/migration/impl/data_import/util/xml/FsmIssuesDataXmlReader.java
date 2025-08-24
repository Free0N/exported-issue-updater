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


import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.attachments.AttachmentsReadAction;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.common.CommonDataReadAction;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.CustomFieldDataReaderAction;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.CustomFieldReader;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.links.LinksDataReaderAction;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.links.ParentIssueDataReaderAction;
import org.samearch.jira.util.migration.impl.dto.xml.IssueXmlDto;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FsmIssuesDataXmlReader {

	private final XMLStreamReader reader;
	private final Fsm fsm;

	private final CommonDataReadAction commonDataReader;
	private final AttachmentsReadAction attachmentsReader;
	private final LinksDataReaderAction linksReader;
	private final ParentIssueDataReaderAction parentIssueDataReader;
	private final CustomFieldDataReaderAction customFieldDataReaderAction;

	private final List<IssueXmlDto> issues = new ArrayList<>();

	public FsmIssuesDataXmlReader(InputStream xmlDataStream, Fsm fsm, List<CustomFieldReader> cfDataReaders) throws XMLStreamException {
		this.reader = XMLInputFactory.newInstance().createXMLStreamReader(xmlDataStream);
		this.fsm = fsm;
		this.commonDataReader = new CommonDataReadAction();
		this.attachmentsReader = new AttachmentsReadAction();
		this.linksReader = new LinksDataReaderAction();
		this.parentIssueDataReader = new ParentIssueDataReaderAction();
		this.customFieldDataReaderAction = new CustomFieldDataReaderAction(cfDataReaders);
	}

	public List<IssueXmlDto> processXmlData() throws XMLStreamException {
		fsm.push(this::findIssueData);
		var issueDataReadContext = new IssueDataReadContext(reader);
		while (reader.hasNext() && fsm.hasAction()) {
			fsm.update(issueDataReadContext);
		}
		reader.close();
		return issues;
	}

	private void findIssueData(Fsm fsm, IssueDataReadContext dataReadContext) {
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT && "item".equals(reader.getLocalName())) {
					saveIssueData(dataReadContext);
					fsm.push(this::findUsefulIssueData);
					break;
				}
				if (event == XMLEvent.END_ELEMENT && "channel".equals(reader.getLocalName())) {
					fsm.pop();
					saveIssueData(dataReadContext);
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private void findUsefulIssueData(Fsm fsm, IssueDataReadContext dataReadContext) {
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT) {
					IssueDataElement.getByTagName(reader.getLocalName())
						.ifPresent(issueDataElement -> {
							var nextAction = switch (issueDataElement) {
								case KEY -> commonDataReader;
								case ATTACHMENTS -> attachmentsReader;
								case LINKS -> linksReader;
								case PARENT_ISSUE -> parentIssueDataReader;
								case CUSTOM_FIELD -> customFieldDataReaderAction;
							};
							fsm.push(nextAction);
						});
					break;
				} else if (event == XMLEvent.END_ELEMENT && "item".equals(reader.getLocalName())) {
					fsm.pop();
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private void saveIssueData(IssueDataReadContext dataReadContext) {
		var issueDataBuilder = dataReadContext.issueBuilder();
		if (issueDataBuilder != null) {
			var issueXmlDto = issueDataBuilder.build();
			issues.add(issueXmlDto);
		}
		dataReadContext.refreshIssueBuilder();
	}

}
