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

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

/**
 * An independent variable in a table.
 * 
 * @author Sam Macbeth
 * 
 */
abstract class IndependentVariable implements Iterable<String> {

	final String name;

	IndependentVariable(String name) {
		super();
		this.name = name;
	}

	String getName() {
		return name;
	}

	/**
	 * Get a subset of <code>sources</code> which match the given
	 * <code>value</code> for this field.
	 * 
	 * @param value
	 * @param sources
	 * @return
	 */
	Set<PersistentSimulation> getMatchingSubset(String value,
			Set<PersistentSimulation> sources) {
		return sources;
	}

}
