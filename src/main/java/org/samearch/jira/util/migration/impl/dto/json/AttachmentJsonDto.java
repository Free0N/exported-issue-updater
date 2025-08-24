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

import org.samearch.jira.util.migration.api.dto.Attachment;

import java.util.Objects;

public class AttachmentJsonDto {

    private String name;
    private String uri;
    private String created;
    private String description;
    private String attacher;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttacher() {
        return attacher;
    }

    public void setAttacher(String attacher) {
        this.attacher = attacher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttachmentJsonDto that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(uri, that.uri) && Objects.equals(created, that.created) && Objects.equals(description, that.description) && Objects.equals(attacher, that.attacher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uri, created, description, attacher);
    }

    public Attachment toAttachment() {
        return null;
    }

    public static AttachmentJsonDto fromAttachment(Attachment attachment) {
        var attachmentDto = new AttachmentJsonDto();
        attachmentDto.setName(attachment.getName());
        attachmentDto.setUri(attachment.getUri());
        attachmentDto.setCreated(attachment.getCreated());
        attachmentDto.setDescription(attachment.getDescription());
        attachmentDto.setAttacher(attachment.getAttacher());
        return attachmentDto;
    }
}
