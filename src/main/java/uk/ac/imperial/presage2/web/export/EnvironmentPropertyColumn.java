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

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

/**
 * A Column whose source is from a transient environment property over a set of
 * simulations.
 * 
 * @author Sam Macbeth
 * 
 */
public class EnvironmentPropertyColumn extends ColumnDefinition {

	EnvironmentPropertyColumn(JSONObject column, boolean timeSeries)
			throws JSONException {
		super(ColumnType.ENVIRONMENT_PROPERTY, column.optString(
				JSON_NAME_KEY,
				column.getString(JSON_PROPERTY_KEY) + "-"
						+ column.getString(JSON_FUNCTION_KEY)), column
				.getString(JSON_PROPERTY_KEY), GroupFunction.get(column
				.getString(JSON_FUNCTION_KEY)), timeSeries);
	}

	@Override
	public String getColumnValue(String... inputs) {
		final int t = timeSeries ? Integer.parseInt(inputs[0]) : 0;
		final Iterator<PersistentSimulation> sourceIterator = this.sources
				.iterator();

		// Iterator which calculates and holds the next value before it is asked
		// for.
		// This allows us to skip over simulations which don't have the value
		// we're looking for while still always returning a correct value from
		// hasNext().
		return this.function.getValue(new Iterator<Number>() {
			Number next = getNext();

			Number getNext() {
				if (sourceIterator.hasNext()) {
					PersistentSimulation current = sourceIterator.next();
					String prop = timeSeries ? current.getEnvironment()
							.getProperty(property, t) : current
							.getEnvironment().getProperty(property);
					if (prop == null)
						return getNext();
					else {
						try {
							return Double.parseDouble(prop);
						} catch (NumberFormatException e) {
							return getNext();
						}
					}
				} else
					return null;
			}

			@Override
			public void remove() {
			}

			@Override
			public Number next() {
				Number current = next;
				next = getNext();
				return current;
			}

			@Override
			public boolean hasNext() {
				return next != null;
			}
		}).toString();
	}

}
