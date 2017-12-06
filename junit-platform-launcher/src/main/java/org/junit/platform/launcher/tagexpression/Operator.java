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

import static org.junit.platform.launcher.tagexpression.Associativity.Left;

class Operator {

	static Operator nullaryOperator(String representation, int precedence) {
		return new Operator(representation, precedence, 0, null, (expressions, position) -> ParseStatus.success());
	}

	static Operator unaryOperator(String representation, int precedence, Associativity associativity,
			ExpressionCreator expressionCreator) {
		return new Operator(representation, precedence, 1, associativity, expressionCreator);
	}

	static Operator binaryOperator(String representation, int precedence, Associativity associativity,
			ExpressionCreator expressionCreator) {
		return new Operator(representation, precedence, 2, associativity, expressionCreator);
	}

	private final String representation;
	private final int precedence;
	private final int arity;
	private final Associativity associativity;
	private final ExpressionCreator expressionCreator;

	private Operator(String representation, int precedence, int arity, Associativity associativity,
			ExpressionCreator expressionCreator) {
		this.representation = representation;
		this.precedence = precedence;
		this.arity = arity;
		this.associativity = associativity;
		this.expressionCreator = expressionCreator;
	}

    boolean represents(String token) {
        return representation.equals(token);
    }

	String representation() {
		return representation;
	}

	boolean hasLowerPrecedenceThan(Operator operator) {
		return this.precedence < operator.precedence;
	}

    boolean hasSamePrecedenceAs(Operator operator) {
        return this.precedence == operator.precedence;
    }

    boolean isLeftAssociative() {
        return Left == associativity;
    }

	ParseStatus createAndAddExpressionTo(Stack<Position<Expression>> expressions, int position) {
		if (expressions.size() < arity) {
			String message = createMissingOperandMessage(position, expressions);
			return ParseStatus.Create(position, representation, message);
		}
		return expressionCreator.createExpressionAndAddTo(expressions, position);
	}

	private String createMissingOperandMessage(int position, Stack<Position<Expression>> expressions) {
		if (1 == arity) {
			return missingOneOperand(associativity == Left ? "lhs" : "rhs");
		}

		if (2 == arity) {
			int mismatch = arity - expressions.size();
			if (2 == mismatch) {
				return "missing lhs and rhs operand";
			}
			return missingOneOperand(position < expressions.peek().position ? "lhs" : "rhs");
		}
		return "missing operand";
	}

	private String missingOneOperand(String side) {
		return "missing " + side + " operand";
	}
}
