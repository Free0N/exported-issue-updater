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

import org.samearch.jira.util.migration.api.config.AttachmentImportSettings
import org.samearch.jira.util.migration.api.config.ImportConfig
import org.samearch.jira.util.migration.api.config.UpdateConfig
import org.samearch.jira.util.migration.api.dto.Attachment
import org.samearch.jira.util.migration.api.updater.AttachmentUpdater

import java.nio.charset.StandardCharsets

/**
 * Добавляет информацию об аттачах задачи
 * В конфигурации должен быть задан параметр importConfig.attachmentImportSettings.baseUrl
 */
class AttachmentsUpdater implements AttachmentUpdater {

    /**
     * Шаблон URL для загрузки аттачей:
     * 1. baseUrl
     * 2. attach id
     * 3. attach file encoded name
     */
    private static final String ATTACHMENT_URL_TEMPLATE = "%s/secure/attachment/%s/%s"

    private final ImportConfig importConfig
    private final AttachmentImportSettings attachmentImportSettings
    private final CommonUpdaterUtils commonUpdaterUtils

    AttachmentsUpdater(UpdateConfig updateConfig, CommonUpdaterUtils commonUpdaterUtils) {
        this.importConfig = updateConfig.importConfig()
        this.attachmentImportSettings = updateConfig.importConfig().attachmentImportSettings()
        if (attachmentImportSettings == null || attachmentImportSettings.baseUrl() == null) {
            throw new RuntimeException("base url for attachments not set")
        }
        this.commonUpdaterUtils = commonUpdaterUtils
    }

    @Override
    Optional<Attachment> updateAttachment(Attachment attachment) {
        var attachmentUrl = buildAttachmentUrl(attachment)
        attachment.setUri(attachmentUrl)
        attachment.setAttacher(commonUpdaterUtils.mapApplicationUserLogin(attachment.getAttacher()))
        return Optional.of(attachment)
    }

    private String buildAttachmentUrl(Attachment attachment) {
        var attachmentEncodedName = URLEncoder.encode(attachment.name, StandardCharsets.UTF_8)
        var attachmentImportConfig = importConfig.attachmentImportSettings()
        return String.format(ATTACHMENT_URL_TEMPLATE,
                attachmentImportConfig.baseUrl(),
                attachment.id,
                attachmentEncodedName
        )
    }

}
