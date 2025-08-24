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

package org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.readers;

import org.samearch.jira.util.migration.impl.data_import.util.xml.IssueDataReadContext;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.CustomFieldReader;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield.CustomFieldReaderType;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;

@Component
@CustomFieldReaderType({"com.atlassian.jira.plugin.system.customfieldtypes:multiuserpicker"})
public class StringListCfDataReader implements CustomFieldReader {

    @Override
    public void accept(Fsm fsm, IssueDataReadContext dataReadContext) {
        var reader = dataReadContext.reader();
        try {
            while (reader.hasNext()) {
                var event = reader.next();
                if (event == XMLEvent.START_ELEMENT) {
                    var currentElementName = reader.getLocalName();
                    if ("customfieldname".equals(currentElementName)) {
                        dataReadContext.customFieldBuilder().setName(reader.getElementText());
                    } else if ("customfieldvalues".equals(currentElementName)) {
                        fsm.push(this::readCfValues);
                        break;
                    }
                } else if (event == XMLEvent.END_ELEMENT) {
                    var currentElementName = reader.getLocalName();
                    if ("customfield".equals(currentElementName)) {
                        dataReadContext.customFieldBuilder().setCfDataHaveBeenRead(true);
                        fsm.pop();
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    private void readCfValues(Fsm fsm, IssueDataReadContext dataReadContext) {
        var cfValue = new ArrayList<>();
        var reader = dataReadContext.reader();
        try {
            while (reader.hasNext()) {
                var event = reader.next();
                if (event == XMLEvent.START_ELEMENT && "customfieldvalue".equals(reader.getLocalName())) {
                    var userName = reader.getElementText();
                    cfValue.add(userName);
                } else if (event == XMLEvent.END_ELEMENT && "customfieldvalues".equals(reader.getLocalName())) {
                    break;
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        if (!cfValue.isEmpty()) {
            dataReadContext.customFieldBuilder().setValue(cfValue);
        }
        fsm.pop();
    }

}
