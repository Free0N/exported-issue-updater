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

package org.samearch.jira.util.migration.impl.dto.json;

import org.samearch.jira.util.migration.api.dto.CustomFieldValue;

import java.util.Objects;

public class CustomFieldValueJsonDto {

    private String fieldName;
    private String fieldType;
    private String searcherType;
    private Object value;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getSearcherType() {
        return searcherType;
    }

    public void setSearcherType(String searcherType) {
        this.searcherType = searcherType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomFieldValueJsonDto that)) return false;
        return Objects.equals(fieldName, that.fieldName) && Objects.equals(fieldType, that.fieldType) && Objects.equals(searcherType, that.searcherType) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType, searcherType, value);
    }

    public CustomFieldValue toCustomFieldValue() {
        var cfValue = new CustomFieldValue();
        cfValue.setFieldName(fieldName);
        cfValue.setFieldType(fieldType);
        cfValue.setSearcherType(searcherType);
        cfValue.setValue(value);
        return cfValue;
    }

    public static CustomFieldValueJsonDto fromCustomField(CustomFieldValue cfValue) {
        var cfValueJsonDto = new CustomFieldValueJsonDto();
        cfValueJsonDto.setFieldName(cfValue.getFieldName());
        cfValueJsonDto.setFieldType(cfValue.getFieldType());
        cfValueJsonDto.setSearcherType(cfValue.getSearcherType());
        cfValueJsonDto.setValue(cfValue.getValue());
        return cfValueJsonDto;
    }
}
