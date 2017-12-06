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

import java.util.function.Supplier;

class ParseStatus {

	static ParseStatus success() {
		return new ParseStatus(null);
	}

	static ParseStatus missingOperatorBetween(int lhsPosition, String lhsRepresentation, int rhsPosition,
			String rhsRepresentation) {
		return new ParseStatus("missing operator between " + lhsRepresentation + " <" + lhsPosition + "> and "
				+ rhsRepresentation + " <" + rhsPosition + ">");
	}

	static ParseStatus problemParsing(int position, String representation) {
		return Create(position, representation, "problem parsing");
	}

	static ParseStatus missingOpeningParenthesis(int position, String representation) {
		return Create(position, representation, "missing opening parenthesis");
	}

	static ParseStatus missingClosingParenthesis(int position, String representation) {
		return Create(position, representation, "missing closing parenthesis");
	}

	static ParseStatus Create(int position, String operatorRepresentation, String message) {
		return new ParseStatus(operatorRepresentation + " at <" + position + "> " + message);
	}

	static ParseStatus missingOperator() {
		return new ParseStatus("missing operator");
	}

	static ParseStatus emptyTagExpression() {
		return new ParseStatus("empty tag expression");
	}

	final String message;

	private ParseStatus(String message) {
		this.message = message;
	}

	public ParseStatus process(Supplier<ParseStatus> step) {
		if (noError()) {
			return step.get();
		}
		return this;
	}

	public boolean noError() {
		return null == message;
	}

	public boolean isError() {
		return !noError();
	}
}
