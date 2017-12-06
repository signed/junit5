/*
 * Copyright 2015-2017 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 */

package org.junit.platform.launcher.tagexpression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ParserTests {

	private final Parser parser = new Parser();

	@Test
	void notHasHigherPrecedenceThanAnd() {
		assertThat(expressionParsedFrom("! foo & bar")).hasToString("(!foo & bar)");
	}

	@Test
	void andHasHigherPrecedenceThanOr() {
		assertThat(expressionParsedFrom("foo | bar & baz")).hasToString("(foo | (bar & baz))");
	}

	@Test
	void notIsRightAssociative() {
		assertThat(expressionParsedFrom("foo &! bar")).hasToString("(foo & !bar)");
	}

	@Test
	void andIsLeftAssociative() {
		assertThat(expressionParsedFrom("foo & bar & baz")).hasToString("((foo & bar) & baz)");
	}

	@Test
	void orIsLeftAssociative() {
		assertThat(expressionParsedFrom("foo | bar | baz")).hasToString("((foo | bar) | baz)");
	}

	@ParameterizedTest
	@MethodSource("data")
	void acceptanceTests(String tagExpression, String expression) {
		assertThat(expressionParsedFrom(tagExpression)).hasToString(expression);
	}

	private static Stream<Arguments> data() {
		// @formatter:off
		return Stream.of(
				Arguments.of("foo", "foo"),
				Arguments.of("! foo", "!foo"),
				Arguments.of("foo & bar", "(foo & bar)"),
				Arguments.of("foo | bar", "(foo | bar)"),
				Arguments.of("( ! foo & bar | baz)", "((!foo & bar) | baz)"),
				Arguments.of("(foo & bar ) | baz & quux", "((foo & bar) | (baz & quux))"),
				Arguments.of("! foo | bar & ! baz | ! quux | quuz & corge", "(((!foo | (bar & !baz)) | !quux) | (quuz & corge))"),
                Arguments.of("(foo & bar ) | baz & quux", "((foo & bar) | (baz & quux))"),
                Arguments.of("foo | bar & baz|quux", "((foo | (bar & baz)) | quux)")
        );
		// @formatter:on
	}

	private Expression expressionParsedFrom(String tagExpression) {
		return parser.parse(tagExpression).expressionOrThrow(
			(error) -> new RuntimeException("[" + tagExpression + "] should be parsable"));
	}
}
