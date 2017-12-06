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

import java.util.Optional;
import java.util.function.Function;

public class ParseResult {

	static ParseResult error(String errorMessage) {
		return new ParseResult(null, errorMessage);
	}

	static ParseResult success(Expression expression) {
		return new ParseResult(expression, null);
	}

	private final String errorMessage;
	private final Expression expression;

	private ParseResult(Expression expression, String errorMessage) {
		this.errorMessage = errorMessage;
		this.expression = expression;
	}

	public Optional<String> parseError() {
		return Optional.ofNullable(errorMessage);
	}

	public Expression expressionOrThrow(Function<String, RuntimeException> error) {
		if (null != errorMessage) {
			throw error.apply(errorMessage);
		}
		return expression;
	}
}
