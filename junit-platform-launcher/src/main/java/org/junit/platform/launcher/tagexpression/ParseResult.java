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

import static org.apiguardian.api.API.Status.INTERNAL;

import java.util.function.Function;

import org.apiguardian.api.API;

/**
 * Either contains a successfully parsed {@link Expression} or an <em>error message</em> describing the parse error.
 *
 * @since 1.1
 */
@API(status = INTERNAL, since = "1.1")
public class ParseResult {

	static ParseResult success(Expression expression) {
		return new ParseResult(expression, null);
	}

	static ParseResult error(String errorMessage) {
		return new ParseResult(null, errorMessage);
	}

	private final String errorMessage;
	private final Expression expression;

	private ParseResult(Expression expression, String errorMessage) {
		this.errorMessage = errorMessage;
		this.expression = expression;
	}

	public Expression expressionOrThrow(Function<String, RuntimeException> error) {
		if (null != errorMessage) {
			throw error.apply(errorMessage);
		}
		return expression;
	}
}
