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

/**
 * <p>
 * Base class for testing key:value data against boolean criteria. Criteria are
 * defined as JSON.
 * </p>
 * 
 * <p>
 * Criteria are split into two categories, static and transient. Transient
 * encompasses all terms under the <code>$t</code> key. All other terms are
 * static.
 * </p>
 * 
 * @author Sam Macbeth
 * 
 */
public class RootCondition {

	final Condition staticCondition;
	final Condition transientCondition;

	public RootCondition(JSONObject json) throws JSONException {
		super();
		// remove transient condition if it exists and process it separately
		if (json.has("$t")) {
			JSONObject transientConditionJSON = json.getJSONObject("$t");
			json.remove("$t");
			transientCondition = new AndCondition(
					parseConditions(transientConditionJSON));
		} else {
			transientCondition = Condition.TRUE;
		}
		staticCondition = new AndCondition(parseConditions(json));
	}

	static Condition[] parseConditions(JSONObject json) throws JSONException {
		String[] fields = JSONObject.getNames(json);
		if (fields == null || fields.length == 0)
			return new Condition[] {};

		Set<Condition> conditions = new HashSet<Condition>();
		for (int i = 0; i < fields.length; i++) {
			if (fields[i].startsWith("$")) {
				// meta condition
				if (fields[i].equalsIgnoreCase("$and")) {
					conditions.add(new AndCondition(parseConditions(json
							.getJSONObject("$and"))));
				} else if (fields[i].equalsIgnoreCase("$or")) {
					conditions.add(new OrCondition(parseConditions(json
							.getJSONObject("$or"))));
				}
			} else {
				// property condition
				try {
					JSONObject conditionObject = json.getJSONObject(fields[i]);
					// no exception thrown, so there is an inequality inside
					// json object.
					conditions.add(new InequalityCondition(fields[i],
							conditionObject));
				} catch (JSONException e) {
					// if not a JSONObject, therefore it is an EqualsCondition
					conditions.add(new EqualsCondition(fields[i], json
							.get(fields[i])));
				}
			}
		}
		return conditions.toArray(new Condition[] {});
	}

	public boolean testStatic(Map<String, String> input) {
		return staticCondition.test(input);
	}

	public boolean testTransient(Map<String, String> input) {
		return transientCondition.test(input);
	}

}
