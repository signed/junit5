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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.junit.platform.launcher.tagexpression.Associativity.Left;
import static org.junit.platform.launcher.tagexpression.Associativity.Right;
import static org.junit.platform.launcher.tagexpression.Expressions.and;
import static org.junit.platform.launcher.tagexpression.Expressions.not;
import static org.junit.platform.launcher.tagexpression.Expressions.or;

import java.util.Map;
import java.util.stream.Stream;

class Operators {

	private static final Operator Not = Operator.unaryOperator("!", 3, Right, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		if (position < rhs.position) {
			Expression not = not(rhs.element);
			expressions.push(new Position<>(position, not));
			return ParseStatus.success();
		}
		return ParseStatus.Create(position, "!", "missing rhs operand");
	});

	private static final Operator And = Operator.binaryOperator("&", 2, Left, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		Position<Expression> lhs = expressions.pop();
		if (lhs.position < position && position < rhs.position) {
			expressions.push(new Position<>(position, and(lhs.element, rhs.element)));
			return ParseStatus.success();
		}

		if (position > rhs.position) {
			return ParseStatus.Create(position, "&", "missing rhs operand");
		}
		if (position < lhs.position) {
			return ParseStatus.missingOperatorBetween(lhs.position, lhs.element.toString(), rhs.position,
				rhs.element.toString());
		}
		return ParseStatus.problemParsing(position, "&");
	});

	private static final Operator Or = Operator.binaryOperator("|", 1, Left, (expressions, position) -> {
		Position<Expression> rhs = expressions.pop();
		Position<Expression> lhs = expressions.pop();
		if (lhs.position < position && position < rhs.position) {
			expressions.push(new Position<>(position, or(lhs.element, rhs.element)));
			return ParseStatus.success();
		}
		if (position > rhs.position) {
			return ParseStatus.Create(position, "|", "missing rhs operand");
		}
		if (position < lhs.position) {
			return ParseStatus.missingOperatorBetween(lhs.position, lhs.element.toString(), rhs.position,
				rhs.element.toString());
		}
		return ParseStatus.problemParsing(position, "|");
	});

	private final Map<String, Operator> representationToOperator = Stream.of(Not, And, Or).collect(
		toMap(Operator::representation, identity()));

	boolean isOperator(String token) {
		return representationToOperator.containsKey(token);
	}

	Operator operatorFor(String token) {
		return representationToOperator.get(token);
	}

}
