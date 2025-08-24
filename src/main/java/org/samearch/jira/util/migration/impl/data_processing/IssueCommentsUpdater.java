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

package org.samearch.jira.util.migration.impl.data_processing;

import org.samearch.jira.util.migration.api.dto.Comment;
import org.samearch.jira.util.migration.api.dto.Issue;
import org.samearch.jira.util.migration.api.updater.CommentUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IssueCommentsUpdater implements PartialIssueDataUpdater {

    private final List<CommentUpdater> commentUpdaters;

    @Autowired
    public IssueCommentsUpdater(List<CommentUpdater> commentUpdaters) {
        this.commentUpdaters = commentUpdaters;
    }

    @Override
    public void updateIssue(Issue issue) {
        if (commentUpdaters.isEmpty()) {
            return;
        }
        var updatedComments = new ArrayList<Comment>();
        for (var comment: issue.getComments()) {
            var updatedComment = updateComment(comment);
            if (updatedComment != null) {
                updatedComments.add(updatedComment);
            }
        }
        issue.setComments(updatedComments);
    }

    private Comment updateComment(Comment comment) {
        var updatedComment = comment;
        for (var commentUpdater: commentUpdaters) {
            var commentUpdateResult = commentUpdater.updateComment(updatedComment);
            if (commentUpdateResult.isEmpty()) {
                return null;
            }
            updatedComment = commentUpdateResult.get();
        }
        return updatedComment;
    }

}
