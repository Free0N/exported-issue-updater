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

package org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.action.customfield;

import org.samearch.jira.util.migration.impl.data_import.util.xml.IssueDataReadContext;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.Fsm;
import org.samearch.jira.util.migration.impl.data_import.util.xml.fsm.FsmAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.HashMap;
import java.util.List;

public class CustomFieldDataReaderAction implements FsmAction {

    private static final Logger LOG = LoggerFactory.getLogger("xmlCfDataReader");

    private final HashMap<String, CustomFieldReader> cfDataReaders = new HashMap<>();

    public CustomFieldDataReaderAction(List<CustomFieldReader> cfDataReaders) {
        cfDataReaders.forEach(this::mapToCfType);
    }

    private void mapToCfType(CustomFieldReader customFieldReader) {
        if (!customFieldReader.getClass().isAnnotationPresent(CustomFieldReaderType.class)) {
            return;
        }
        var typeAnnotation = customFieldReader.getClass().getAnnotation(CustomFieldReaderType.class);
        var cfTypeNames = typeAnnotation.value();
        for (String cfTypeName: cfTypeNames) {
            if (cfTypeName == null || cfTypeName.isBlank()) {
                return;
            }
            cfDataReaders.put(cfTypeName, customFieldReader);
        }
    }


    @Override
    public void accept(Fsm fsm, IssueDataReadContext dataReadContext) {
        var reader = dataReadContext.reader();
        var customFieldBuilder = dataReadContext.customFieldBuilder();
        if (customFieldBuilder == null) {
            var cfTypeName = reader.getAttributeValue(null, "key");
            var cfDataReader = cfDataReaders.get(cfTypeName);
            if (cfDataReader == null) {
                LOG.warn("Can't find data reader for field type {}", cfTypeName);
                skipToEndTag(reader);
                fsm.pop();
                return;
            }
            dataReadContext.refreshCfXmlDtoBuilder();
            dataReadContext.customFieldBuilder().setType(cfTypeName);
            fsm.push(cfDataReader);
        } else if (customFieldBuilder.isCfDataHaveBeenRead()) {
            customFieldBuilder.build()
                    .ifPresent(dataReadContext.issueBuilder()::addCustomFieldValue);
            dataReadContext.resetCfXmlDtoBuilder();
            fsm.pop();
        } else {
            fsm.pop();
            throw new IllegalStateException("Custom field data is not fully read");
        }
    }

    private void skipToEndTag(XMLStreamReader reader) {
        try {
            while (reader.hasNext()) {
                var event = reader.next();
                if (event == XMLEvent.END_ELEMENT && "customfield".equals(reader.getLocalName())) {
                    return;
                }
            }
        } catch (XMLStreamException ignore) {
        }
    }

}
