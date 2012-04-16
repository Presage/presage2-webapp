/**
 * 	Copyright (C) 2011-2012 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
 *
 * 	This file is part of Presage2.
 *
 *     Presage2 is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Presage2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser Public License
 *     along with Presage2.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.imperial.presage2.web.export.condition;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

class InequalityCondition implements Condition {

	private final String testField;
	private final Condition delegate;

	InequalityCondition(String field, JSONObject conditionObject)
			throws JSONException {
		super();
		this.testField = field;
		Set<Condition> conditions = new HashSet<Condition>();
		if (conditionObject.has("$gt")) {
			conditions
					.add(new GreaterThanTest(conditionObject.getDouble("$gt")));
		}
		if (conditionObject.has("$gte")) {
			conditions.add(new NotCondition(new LessThanTest(conditionObject
					.getDouble("$gte"))));
		}
		if (conditionObject.has("$lt")) {
			conditions.add(new LessThanTest(conditionObject.getDouble("$lt")));
		}
		if (conditionObject.has("$lte")) {
			conditions.add(new NotCondition(new GreaterThanTest(conditionObject
					.getDouble("$lte"))));
		}

		delegate = new AndCondition(conditions.toArray(new Condition[] {}));
	}

	@Override
	public boolean test(Map<String, String> input) {
		return input.containsKey(testField) && delegate.test(input);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	class GreaterThanTest implements Condition {

		private final Double testValue;

		GreaterThanTest(Double testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return Double.parseDouble(input.get(testField)) > testValue;
		}

		@Override
		public String toString() {
			return testField + " > " + testValue;
		}

	}

	class LessThanTest implements Condition {

		private final Double testValue;

		LessThanTest(Double testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return Double.parseDouble(input.get(testField)) < testValue;
		}

		@Override
		public String toString() {
			return testField + " < " + testValue;
		}

	}

}
