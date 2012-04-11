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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

public class AgentPropertyColumn extends ColumnDefinition {

	List<PersistentAgent> agents;

	AgentPropertyColumn(JSONObject column, Set<PersistentSimulation> sources)
			throws JSONException {
		super(sources, ColumnType.AGENT_PROPERTY, column.optString(
				JSON_NAME_KEY, column.getString(JSON_PROPERTY_KEY) + "-"
						+ column.getString(JSON_FUNCTION_KEY)), column
				.getString(JSON_PROPERTY_KEY), GroupFunction.get(column
				.getString(JSON_FUNCTION_KEY)));
		agents = new LinkedList<PersistentAgent>();
		for (PersistentSimulation sim : sources) {
			agents.addAll(sim.getAgents());
		}
	}

	@Override
	public String getColumnValue(String... inputs) {
		final int t = Integer.parseInt(inputs[0]);
		final Iterator<PersistentAgent> sourceIterator = this.agents.iterator();

		return this.function.getValue(new Iterator<Number>() {

			Number next = getNext();

			@Override
			public boolean hasNext() {
				return next != null;
			}

			private Number getNext() {
				if (sourceIterator.hasNext()) {
					PersistentAgent current = sourceIterator.next();
					String prop = current.getState(t).getProperty(property);
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
			public Number next() {
				Number current = next;
				next = getNext();
				return current;
			}

			@Override
			public void remove() {
			}
		}).toString();
	}

}
