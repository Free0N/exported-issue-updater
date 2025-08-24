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

import org.samearch.jira.util.migration.impl.data_import.util.xml.IssueDataReadContext;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.FsmAction;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class LinksDataReaderAction implements FsmAction {

	private IssueLinkDtoBuilder linkDtoBuilder;

	@Override
	public void accept(Fsm fsm, IssueDataReadContext dataReadContext) {
		var reader = dataReadContext.reader();
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT) {
					LinksDataElement.getByTagName(reader.getLocalName())
						.ifPresent(e -> this.tryToParseLinkDataElement(fsm, e));
					break;
				} else if (event == XMLEvent.END_ELEMENT) {
					var elementName = reader.getLocalName();
					if ("issuelinktype".equals(elementName)) {
						if (linkDtoBuilder != null && linkDtoBuilder.canBuild()) {
							var linkDto = linkDtoBuilder.build();
							dataReadContext.issueBuilder().addLink(linkDto);
						}
					} else if ("issuelinks".equals(elementName)) {
						fsm.pop();
						break;
					}
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private void tryToParseLinkDataElement(Fsm fsm, LinksDataElement element) {
		FsmAction nextAction = switch (element) {
			case ISSUE_LINK_TYPE -> this::readIssueLinkTypeName;
			case INWARD_LINKS -> this::readInwardIssueLink;
			case OUTWARD_LINKS -> this::readOutwardIssueLink;
		};
		fsm.push(nextAction);
	}

	private void readIssueLinkTypeName(Fsm fsm, IssueDataReadContext dataReadContext) {
		var reader = dataReadContext.reader();
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT && "name".equals(reader.getLocalName())) {
					var linkTypeName = reader.getElementText();
					linkDtoBuilder = new IssueLinkDtoBuilder(linkTypeName);
					fsm.pop();
					break;
				} else if (event == XMLEvent.END_ELEMENT && "issuelinktype".equals(reader.getLocalName())) {
					fsm.pop();
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private void readOutwardIssueLink(Fsm fsm, IssueDataReadContext dataReadContext) {
		var reader = dataReadContext.reader();
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT && "issuekey".equals(reader.getLocalName())) {
					var currentIssueId = dataReadContext.issueBuilder().getId();
					var currentIssueKey = dataReadContext.issueBuilder().getIssueKey();
					var linkedIssueId = reader.getAttributeValue(null, "id");
					var linkedIssueKey = reader.getElementText();
					linkDtoBuilder.setSourceIssueId(currentIssueId);
					linkDtoBuilder.setSourceIssueKey(currentIssueKey);
					linkDtoBuilder.setDestinationIssueId(linkedIssueId);
					linkDtoBuilder.setDestinationIssueKey(linkedIssueKey);
					var linkDto = linkDtoBuilder.build();
					dataReadContext.issueBuilder().addLink(linkDto);
				} else if (event == XMLEvent.END_ELEMENT && "outwardlinks".equals(reader.getLocalName())) {
					fsm.pop();
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

	private void readInwardIssueLink(Fsm fsm, IssueDataReadContext dataReadContext) {
		var reader = dataReadContext.reader();
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT && "issuekey".equals(reader.getLocalName())) {
					var currentIssueId = dataReadContext.issueBuilder().getId();
					var currentIssueKey = dataReadContext.issueBuilder().getIssueKey();
					var linkedIssueId = reader.getAttributeValue(null, "id");
					var linkedIssueKey = reader.getElementText();
					linkDtoBuilder.setSourceIssueId(linkedIssueId);
					linkDtoBuilder.setSourceIssueKey(linkedIssueKey);
					linkDtoBuilder.setDestinationIssueId(currentIssueId);
					linkDtoBuilder.setDestinationIssueKey(currentIssueKey);
					var linkDto = linkDtoBuilder.build();
					dataReadContext.issueBuilder().addLink(linkDto);
				} else if (event == XMLEvent.END_ELEMENT && "inwardlinks".equals(reader.getLocalName())) {
					fsm.pop();
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

}
