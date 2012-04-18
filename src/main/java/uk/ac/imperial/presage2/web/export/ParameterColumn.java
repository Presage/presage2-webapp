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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

class ParameterColumn extends IndependentVariable {

	final Set<String> values;

	ParameterColumn(Set<PersistentSimulation> sources, String property) {
		super(property);
		values = new HashSet<String>();
		for (PersistentSimulation sim : sources) {
			Map<String, String> params = sim.getParameters();
			if (params.containsKey(property)
					&& !values.contains(params.get(property)))
				values.add(params.get(property));
		}
	}

	@Override
	public Iterator<String> iterator() {
		return values.iterator();
	}

	@Override
	Set<PersistentSimulation> getMatchingSubset(String value,
			Set<PersistentSimulation> sources) {
		Set<PersistentSimulation> subset = new HashSet<PersistentSimulation>();
		for (PersistentSimulation sim : sources) {
			Map<String, String> params = sim.getParameters();
			if (params.containsKey(name)
					&& params.get(name).equalsIgnoreCase(value))
				subset.add(sim);
		}
		return subset;
	}

}
