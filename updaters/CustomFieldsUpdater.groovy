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

import org.samearch.jira.util.migration.api.config.MappingConfig
import org.samearch.jira.util.migration.api.config.UpdateConfig
import org.samearch.jira.util.migration.api.dto.CustomFieldValue
import org.samearch.jira.util.migration.api.updater.CustomFieldUpdater

import java.util.stream.Collectors

/**
 * Обновляет информацию о пользовательских полях:
 * * удаляет поле, если его тип указан в конфигурации (mappingConfig.removedFieldTypes)
 * * значение поля, если в нем содержится логин пользователя (определяется по типу поля, mappingConfig.userNameFieldTypes)
 * * тип поля (mappingConfig.customFieldTypeMappings)
 * * название поля (mappingConfig.customFieldsMapping)
 */
class CustomFieldsUpdater implements CustomFieldUpdater {

    private final MappingConfig mappingConfig
    private final CommonUpdaterUtils commonUpdaterUtils

    CustomFieldsUpdater(UpdateConfig updateConfig, CommonUpdaterUtils commonUpdaterUtils) {
        this.mappingConfig = updateConfig.mappingConfig()
        this.commonUpdaterUtils = commonUpdaterUtils
    }

    @Override
    Optional<CustomFieldValue> updateCustomField(CustomFieldValue cfValue) {
        if (mustRemoveField(cfValue)) {
            return Optional.empty()
        }
        applyCustomFieldMappings(cfValue)
        return Optional.of(cfValue)
    }

    private void applyCustomFieldMappings(CustomFieldValue cf) {
        mapApplicationUserField(cf)
        mapCustomFieldType(cf)
        mapCustomFieldName(cf)
    }

    private boolean mustRemoveField(CustomFieldValue cfValue) {
        return removeFieldOfInappropriateName(cfValue) || removeFieldOfInappropriateType(cfValue)
    }

    private boolean removeFieldOfInappropriateType(CustomFieldValue cfValue) {
        return mappingConfig.removedFieldTypes().contains(cfValue.fieldType)
    }

    private boolean removeFieldOfInappropriateName(CustomFieldValue cfValue) {
        return mappingConfig.removedFieldNames().contains(cfValue.fieldName)
    }

    private void mapApplicationUserField(CustomFieldValue cf) {
        if (mappingConfig.userNameFieldTypes().contains(cf.getFieldType())) {
            Object cfValue = cf.getValue()
            if (cfValue instanceof String) {
                String currentCfValue = cfValue as String
                String updatedCfValue = commonUpdaterUtils.mapApplicationUserLogin(currentCfValue)
                cf.value = updatedCfValue
            } else if (cfValue instanceof List) {
                List<String> currentCfValue = cfValue as List<String>
                List<String> updatedCfValue = currentCfValue.stream()
                    .map { commonUpdaterUtils.mapApplicationUserLogin(it) }
                    .collect(Collectors.toList())
                cf.value = updatedCfValue
            }
        }
    }

    private void mapCustomFieldType(CustomFieldValue cf) {
        mappingConfig.getMappedCfType(cf.getFieldName(), cf.getFieldType())
            .ifPresent(cf::setFieldType)
    }

    private void mapCustomFieldName(CustomFieldValue cf) {
        mappingConfig.getMappedCustomFieldName(cf.getFieldName())
            .ifPresent(cf::setFieldName)
    }

}
