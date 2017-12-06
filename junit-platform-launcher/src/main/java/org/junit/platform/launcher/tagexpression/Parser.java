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

import java.util.List;

public class Parser {

	public static ParseResult parseExpressionFrom(String infixTagExpression) {
		return new Parser().parse(infixTagExpression);
	}

	private final Tokenizer tokenizer = new Tokenizer();

	ParseResult parse(String infixTagExpression) {
		return constructExpressionFrom(tokensDerivedFrom(infixTagExpression));
	}

	private List<String> tokensDerivedFrom(String infixTagExpression) {
		return tokenizer.tokenize(infixTagExpression);
	}

	private ParseResult constructExpressionFrom(List<String> tokens) {
		return new ShuntingYard(tokens).execute();
	}

}
