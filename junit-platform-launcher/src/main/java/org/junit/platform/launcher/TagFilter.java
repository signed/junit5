/*
 * Copyright 2015-2018 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.launcher;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.apiguardian.api.API.Status.STABLE;

import java.util.List;

import org.apiguardian.api.API;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.platform.commons.util.PreconditionViolationException;
import org.junit.platform.commons.util.Preconditions;
import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestTag;
import org.junit.platform.launcher.tagexpression.TagExpression;

/**
 * Factory methods for creating {@link PostDiscoveryFilter PostDiscoveryFilters}
 * based on <em>included</em> and <em>excluded</em> tag expressions.
 *
 * @since 1.0
 * @see #includeTags(String...)
 * @see #excludeTags(String...)
 */
@API(status = STABLE, since = "1.0")
public final class TagFilter {

	private static final Logger logger = LoggerFactory.getLogger(TagFilter.class);

	///CLOVER:OFF
	private TagFilter() {
		/* no-op */
	}
	///CLOVER:ON

	/**
	 * Create an <em>include</em> filter based on the supplied {@code tagExpressions}.
	 *
	 * <p>Containers and tests will only be executed if they match
	 * at least one of the supplied <em>included</em> tagExpressions.
	 *
	 * @param tagExpressions the included tagExpressions; never {@code null} or empty
	 * @throws PreconditionViolationException if the supplied tagExpressions array is
	 * {@code null} or empty, or if any individual tag expression is not syntactically
	 * valid
	 * @see #includeTags(List)
	 * @see TestTag#isValid(String)
	 */
	public static PostDiscoveryFilter includeTags(String... tagExpressions) throws PreconditionViolationException {
		Preconditions.notNull(tagExpressions, "tagExpressions array must not be null");
		return includeTags(asList(tagExpressions));
	}

	/**
	 * Create an <em>include</em> filter based on the supplied {@code tagExpressions}.
	 *
	 * <p>Containers and tests will only be executed if they match
	 * at least one of the supplied <em>included</em> tagExpressions.
	 *
	 * @param tagExpressions the included tagExpressions; never {@code null} or empty
	 * @throws PreconditionViolationException if the supplied tagExpressions list is
	 * {@code null} or empty, or if any individual tag expression is not syntactically
	 * valid
	 * @see #includeTags(String...)
	 * @see TestTag#isValid(String)
	 */
	public static PostDiscoveryFilter includeTags(List<String> tagExpressions) throws PreconditionViolationException {
		Preconditions.notEmpty(tagExpressions, "tagExpressions list must not be null or empty");
		return includeMatching(orExpressionFor(tagExpressions));
	}

	/**
	 * Create an <em>exclude</em> filter based on the supplied {@code tagExpressions}.
	 *
	 * <p>Containers and tests will only be executed if they do <em>not</em>
	 * match any of the supplied <em>excluded</em> tagExpressions.
	 *
	 * @param tagExpressions the excluded tagExpressions; never {@code null} or empty
	 * @throws PreconditionViolationException if the supplied tagExpressions array is
	 * {@code null} or empty, or if any individual tag expression is not syntactically
	 * valid
	 * @see #excludeTags(List)
	 * @see TestTag#isValid(String)
	 */
	public static PostDiscoveryFilter excludeTags(String... tagExpressions) throws PreconditionViolationException {
		Preconditions.notNull(tagExpressions, "tagExpressions array must not be null");
		return excludeTags(asList(tagExpressions));
	}

	/**
	 * Create an <em>exclude</em> filter based on the supplied {@code tagExpressions}.
	 *
	 * <p>Containers and tests will only be executed if they do <em>not</em>
	 * match any of the supplied <em>excluded</em> tagExpressions.
	 *
	 * @param tagExpressions the excluded tagExpressions; never {@code null} or empty
	 * @throws PreconditionViolationException if the supplied tagExpressions list is
	 * {@code null} or empty, or if any individual tag expression is not syntactically
	 * valid
	 * @see #excludeTags(String...)
	 * @see TestTag#isValid(String)
	 */
	public static PostDiscoveryFilter excludeTags(List<String> tagExpressions) throws PreconditionViolationException {
		Preconditions.notEmpty(tagExpressions, "tagExpressions list must not be null or empty");
		return includeMatching("! (" + orExpressionFor(tagExpressions) + ")");
	}

	/**
	 * Create an <em>include</em> filter based on the supplied {@code tagExpressionString}.
	 *
	 * <p>Containers and tests will only be executed if their tags match the supplied <em>tagExpressionString</em>.
	 *
	 * @param tagExpressionString to parse and evaluate against a {@link TestDescriptor}; never {@code null} or empty
	 * @throws PreconditionViolationException if the supplied tagExpressionString can not be parsed.
	 * @since 1.1
	 */
	public static PostDiscoveryFilter includeMatching(String tagExpressionString) {
		TagExpression tagExpression = TagExpression.parseFrom(tagExpressionString).tagExpressionOrThrow(
			(message) -> new PreconditionViolationException(
				"Unable to parse tag expression [" + tagExpressionString + "]: " + message));
		logger.config(() -> "parsed tag expression: " + tagExpression);
		return descriptor -> FilterResult.includedIf(tagExpression.evaluate(descriptor.getTags()));
	}

	private static String orExpressionFor(List<String> tags) {
		return tags.stream().map(tag -> null == tag ? "" : tag).collect(joining(" | "));
	}
}
