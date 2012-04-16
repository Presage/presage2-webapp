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

import java.util.Map;

class EqualsCondition implements Condition {

	final private String testField;
	final private Condition delegate;

	EqualsCondition(String testField, Object testValue) {
		super();
		this.testField = testField;
		if (testValue instanceof Boolean)
			delegate = new BooleanTest((Boolean) testValue);
		else if (testValue instanceof Integer)
			delegate = new IntegerTest((Integer) testValue);
		else if (testValue instanceof Double)
			delegate = new DoubleTest((Double) testValue);
		else
			delegate = new StringTest(testValue.toString());
	}

	@Override
	public boolean test(Map<String, String> input) {
		return input.get(testField) != null && delegate.test(input);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	class StringTest implements Condition {

		final private String testValue;

		StringTest(String testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return input.get(testField).equalsIgnoreCase(testValue);
		}

		@Override
		public String toString() {
			return testField + " == " + testValue;
		}
	}

	class BooleanTest implements Condition {

		final private boolean testValue;

		BooleanTest(boolean testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return Boolean.parseBoolean(input.get(testField)) == testValue;
		}

		@Override
		public String toString() {
			return testField + " == " + testValue;
		}
	}

	class IntegerTest implements Condition {

		final private int testValue;

		IntegerTest(int testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return Integer.parseInt(input.get(testField)) == testValue;
		}

		@Override
		public String toString() {
			return testField + " == " + testValue;
		}
	}

	class DoubleTest implements Condition {

		final private double testValue;

		DoubleTest(double testValue) {
			super();
			this.testValue = testValue;
		}

		@Override
		public boolean test(Map<String, String> input) {
			return Math.abs(Double.parseDouble(input.get(testField))
					- testValue) < 0.00001;
		}

		@Override
		public String toString() {
			return testField + " == " + testValue;
		}
	}

}
