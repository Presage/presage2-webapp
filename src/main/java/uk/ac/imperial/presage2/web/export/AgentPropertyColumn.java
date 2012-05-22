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
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.web.export.condition.RootCondition;

public class AgentPropertyColumn extends ColumnDefinition {

	final static String JSON_CONDITION_KEY = "condition";

	List<PersistentAgent> agents;
	final RootCondition condition;

	AgentPropertyColumn(JSONObject column, boolean timeSeries)
			throws JSONException {
		super(ColumnType.AGENT_PROPERTY, column.optString(
				JSON_NAME_KEY,
				column.getString(JSON_PROPERTY_KEY) + "-"
						+ column.getString(JSON_FUNCTION_KEY)), column
				.getString(JSON_PROPERTY_KEY), GroupFunction.get(column
				.getString(JSON_FUNCTION_KEY)), timeSeries);
		if (column.has(JSON_CONDITION_KEY)) {
			condition = new RootCondition(
					column.getJSONObject(JSON_CONDITION_KEY));
		} else {
			condition = null;
		}
	}

	@Override
	void setSources(Set<PersistentSimulation> sources) {
		super.setSources(sources);
		agents = new LinkedList<PersistentAgent>();
		for (PersistentSimulation sim : sources) {
			agents.addAll(sim.getAgents());
		}
		// filter agents
		if (condition != null) {
			Iterator<PersistentAgent> it = agents.iterator();
			while (it.hasNext()) {
				Map<String, String> agentProperties = it.next().getProperties();
				if (!condition.testStatic(agentProperties))
					it.remove();
			}
		}
	}

	@Override
	public String getColumnValue(String... inputs) {
		final int t = timeSeries ? Integer.parseInt(inputs[0]) : 0;
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
					Map<String, String> agentProperties = timeSeries ? current
							.getState(t).getProperties() : current
							.getProperties();
					// check transient conditions.
					if (timeSeries && condition != null
							&& !condition.testTransient(agentProperties)) {
						return getNext();
					}
					if (!agentProperties.containsKey(property))
						return getNext();
					else {
						try {
							return Double.parseDouble(agentProperties.get(property));
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
