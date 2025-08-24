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

package org.samearch.jira.util.migration.api.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class DtoObjectsList<T extends DtoObject<T>> {

    private final HashMap<String, T> objects;

    public DtoObjectsList() {
        objects = new HashMap<>();
    }

    public DtoObjectsList(Collection<T> objects) {
        this();
        addAll(objects);
    }

    public DtoObjectsList(DtoObjectsList<T> dtoObjectsList) {
        this();
        addAll(dtoObjectsList);
    }

    public List<T> getObjects() {
        return new ArrayList<>(objects.values());
    }

    public void add(T object) {
        var objectKey = object.getKey();
        if (objectKey == null || objectKey.isBlank()) {
            return;
        }
        var updatedObject = objects.computeIfAbsent(objectKey, k -> object);
        if (updatedObject != object) {
            updatedObject.merge(object);
        }
        objects.remove(objectKey);
        objects.put(updatedObject.getKey(), updatedObject);
    }

    public void addAll(Collection<T> objects) {
        if (objects != null && !objects.isEmpty()) {
            objects.forEach(this::add);
        }
    }

    public void addAll(DtoObjectsList<T> otherDtoList) {
        if (otherDtoList != null && !otherDtoList.isEmpty()) {
            otherDtoList.objects.values().forEach(this::add);
        }
    }

    public static <Y extends DtoObject<Y>> DtoObjectsList<Y> createOrMerge(DtoObjectsList<Y> currentDtosList, DtoObjectsList<Y> existedDtosList) {
        var dtoObjectList = new DtoObjectsList<Y>();
        if (currentDtosList != null && !currentDtosList.isEmpty()) {
            dtoObjectList.addAll(currentDtosList);
        }
        if (existedDtosList != null && !existedDtosList.isEmpty()) {
            dtoObjectList.addAll(existedDtosList);
        }
        return (!dtoObjectList.isEmpty())
                ? dtoObjectList
                : null;
    }

    public boolean isEmpty() {
        return objects.isEmpty();
    }

}
