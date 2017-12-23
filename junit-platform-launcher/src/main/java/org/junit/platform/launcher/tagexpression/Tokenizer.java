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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

class Tokenizer {

	List<String> tokenize(String infixTagExpression) {
		if (null == infixTagExpression) {
			return Collections.emptyList();
		}
		String[] parts = infixTagExpression.replaceAll("([()!|&])", " $1 ").split("\\s");
		return Arrays.stream(parts).filter(part -> !part.isEmpty()).collect(Collectors.toList());
	}

}