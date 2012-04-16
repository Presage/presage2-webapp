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

class AndCondition implements Condition {

	private final Condition[] conditions;

	AndCondition(Condition... conditions) {
		super();
		if (conditions.length > 0) {
			this.conditions = conditions;
		} else {
			this.conditions = new Condition[] { Condition.TRUE };
		}
	}

	@Override
	public boolean test(Map<String, String> input) {
		for (int i = 0; i < conditions.length; i++) {
			if (!conditions[i].test(input))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("(");
		for (int i = 0; i < conditions.length; i++) {
			str.append(conditions[i].toString());
			if (i < conditions.length - 1) {
				str.append(" AND ");
			}
		}
		str.append(")");
		return str.toString();
	}

}
