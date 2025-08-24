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

package org.samearch.jira.util.migration.application.groovy;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.Phases;
import org.codehaus.groovy.tools.GroovyClass;
import org.samearch.jira.util.migration.api.updater.DisabledIssueUpdater;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class GroovyIntegration {

	private final GroovyClassLoader groovyClassLoader;
	private final String path;
	private final GroovyClassLoader sandboxClassloader = new GroovyClassLoader();

	public GroovyIntegration(ClassLoader classLoader, String path) {
		this.groovyClassLoader = new GroovyClassLoader(classLoader);
		this.path = path;
		this.groovyClassLoader.addClasspath(path);
	}

	public ClassLoader getGroovyClassLoader() {
		return groovyClassLoader;
	}

	public Set<Class<?>> loadGroovyClasses() {
		var compilationUnit = new CompilationUnit();

		var groovyScripts = findGroovyFiles(path);
		groovyScripts.forEach(compilationUnit::addSource);
		compilationUnit.setClassLoader(groovyClassLoader);
		compilationUnit.compile(Phases.CLASS_GENERATION);

		return compilationUnit.getClasses().stream()
			.map(this::loadGroovyClass)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
	}

	private static List<File> findGroovyFiles(String path) {
		var files = new ArrayList<File>();
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
					if (!Files.isDirectory(path) && path.getFileName().toString().endsWith(".groovy")) {
						files.add(path.toFile());
					}
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception ignore) {}
		return files;
	}

	private Class<?> loadGroovyClass(GroovyClass compiledClass) {
		var className = compiledClass.getName();

		sandboxClassloader.defineClass(className, compiledClass.getBytes());

		Class<?> definedClass = groovyClassLoader.defineClass(className, compiledClass.getBytes());
		var classDisabledAnnotation = definedClass.getAnnotation(DisabledIssueUpdater.class);
		if (classDisabledAnnotation != null) {
			return null;
		}
		try {
			return groovyClassLoader.loadClass(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

}
