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
package uk.ac.imperial.presage2.web.export;

import java.util.Iterator;

/**
 * A group function returns a single {@link Number} from a collection of
 * {@link Number}s.
 * 
 * @author Sam Macbeth
 * 
 */
abstract class GroupFunction {

	final static GroupFunction MEAN_FN = new GroupFunction() {
		@Override
		Double getValue(Iterator<Number> values) {
			double sum = 0;
			int count = 0;
			while (values.hasNext()) {
				sum += values.next().doubleValue();
				count++;
			}
			if (count == 0)
				return 0.0;
			return sum / count;
		}
	};

	final static GroupFunction MIN_FN = new GroupFunction() {
		@Override
		Double getValue(Iterator<Number> values) {
			double min;
			if (!values.hasNext())
				return 0.0;
			min = values.next().doubleValue();
			while (values.hasNext()) {
				double val = values.next().doubleValue();
				if (min > val) {
					min = val;
				}
			}
			return min;
		}
	};

	final static GroupFunction MAX_FN = new GroupFunction() {
		@Override
		Double getValue(Iterator<Number> values) {
			double max;
			if (!values.hasNext())
				return 0.0;
			max = values.next().doubleValue();
			while (values.hasNext()) {
				double val = values.next().doubleValue();
				if (max < val) {
					max = val;
				}
			}
			return max;
		}
	};

	final static GroupFunction COUNT_FN = new GroupFunction() {
		@Override
		Integer getValue(Iterator<Number> values) {
			int count = 0;
			while (values.hasNext()) {
				values.next();
				count++;
			}
			return count;
		}
	};

	final static GroupFunction SUM_FN = new GroupFunction() {
		@Override
		Double getValue(Iterator<Number> values) {
			double sum = 0;
			while (values.hasNext()) {
				sum += values.next().doubleValue();
			}
			return sum;
		}
	};

	static GroupFunction get(String type) {
		if (type.equalsIgnoreCase("MIN")) {
			return MIN_FN;
		} else if (type.equalsIgnoreCase("MAX")) {
			return MAX_FN;
		} else if (type.equalsIgnoreCase("COUNT")) {
			return COUNT_FN;
		} else if (type.equalsIgnoreCase("SUM")) {
			return SUM_FN;
		} else
			return MEAN_FN;
	}

	/**
	 * Get the result of this {@link GroupFunction} given the input
	 * <code>values</code>.
	 * 
	 * @param values
	 *            {@link Iterator} of input values.
	 * @return {@link Number} result of the function.
	 */
	abstract Number getValue(Iterator<Number> values);
}
