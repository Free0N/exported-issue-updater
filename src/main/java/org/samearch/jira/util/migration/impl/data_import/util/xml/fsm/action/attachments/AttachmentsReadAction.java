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

package org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.attachments;

import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.FsmAction;
import org.samearch.jira.util.migration.impl.dto.xml.AttachmentXmlDto;
import org.samearch.jira.util.migration.impl.data_import.util.xml.IssueDataReadContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class AttachmentsReadAction implements FsmAction {

	@Override
	public void accept(Fsm fsm, IssueDataReadContext dataReadContext) {
		var reader = dataReadContext.reader();
		try {
			while (reader.hasNext()) {
				var event = reader.next();
				if (event == XMLEvent.START_ELEMENT && "attachment".equals(reader.getLocalName())) {
					var id = reader.getAttributeValue(null, "id");
					var name = reader.getAttributeValue(null, "name");
					var author = reader.getAttributeValue(null, "author");
					dataReadContext.issueBuilder().addAttachment(new AttachmentXmlDto(id, name, author));
					break;
				} else if (event == XMLEvent.END_ELEMENT && "attachments".equals(reader.getLocalName())) {
					fsm.pop();
					break;
				}
			}
		} catch (XMLStreamException e) {
			throw new RuntimeException(e);
		}
	}

}
