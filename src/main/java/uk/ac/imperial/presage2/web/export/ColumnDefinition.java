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

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

/**
 * A column in a table of data.
 * 
 * @author Sam Macbeth
 * 
 */
abstract class ColumnDefinition {

	enum ColumnType {
		ENVIRONMENT_PROPERTY, AGENT_PROPERTY
	};

	Set<PersistentSimulation> sources;

	final ColumnType type;

	final String name;

	final String property;

	final GroupFunction function;

	final boolean timeSeries;

	final static String JSON_TYPE_KEY = "type";
	final static String JSON_NAME_KEY = "name";
	final static String JSON_PROPERTY_KEY = "property";
	final static String JSON_FUNCTION_KEY = "function";

	ColumnDefinition(ColumnType type, String name, String property,
			GroupFunction function, boolean timeSeries) {
		super();
		this.type = type;
		this.name = name;
		this.property = property;
		this.function = function;
		this.timeSeries = timeSeries;
	}

	/**
	 * Create a {@link ColumnDefinition} from a {@link JSONObject}.
	 * 
	 * @param column
	 * @param sources
	 * @return
	 * @throws JSONException
	 */
	static ColumnDefinition createColumn(JSONObject column, boolean timeSeries)
			throws JSONException {
		if (column.getString(JSON_TYPE_KEY).equalsIgnoreCase("ENV")) {
			return new EnvironmentPropertyColumn(column, timeSeries);
		} else if (column.getString(JSON_TYPE_KEY).equalsIgnoreCase("AGENT")) {
			return new AgentPropertyColumn(column, timeSeries);
		} else {
			throw new JSONException("Invalid column type: '"
					+ column.getString(JSON_TYPE_KEY));
		}
	}

	/**
	 * @return the name
	 */
	String getName() {
		return name;
	}

	/**
	 * @param sources
	 *            the sources to set
	 */
	void setSources(Set<PersistentSimulation> sources) {
		this.sources = sources;
	}

	/**
	 * Get the value of this column given an array of <code>inputs</code> given
	 * to it.
	 * 
	 * @param inputs
	 * @return {@link String} value of the column given <code>inputs</code>
	 */
	abstract public String getColumnValue(String... inputs);

}
