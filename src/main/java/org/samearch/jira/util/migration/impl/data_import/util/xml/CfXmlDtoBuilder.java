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

import org.samearch.jira.util.migration.impl.dto.xml.CustomFieldValueXmlDto;

import java.util.Optional;

public class CfXmlDtoBuilder {

    private String type;
    private String name;
    private Object value;

    private boolean cfDataHaveBeenRead;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCfDataHaveBeenRead() {
        return cfDataHaveBeenRead;
    }

    public void setCfDataHaveBeenRead(boolean cfDataHaveBeenRead) {
        this.cfDataHaveBeenRead = cfDataHaveBeenRead;
    }

    public Optional<CustomFieldValueXmlDto> build() {
        if (value == null) {
            return Optional.empty();
        }
        if (type == null || type.isBlank()) {
            throw new IllegalStateException("Can't build custom field object without type");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Can't build custom field object without name");
        }
        var cfValueDto = new CustomFieldValueXmlDto();
        cfValueDto.setFieldType(type);
        cfValueDto.setFieldName(name);
        cfValueDto.setValue(value);
        return Optional.of(cfValueDto);
    }
}
