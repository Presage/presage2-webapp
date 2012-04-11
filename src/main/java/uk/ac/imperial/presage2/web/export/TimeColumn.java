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
import java.util.Set;

import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

/**
 * The timestep column for a table. This will iterate to the maximum current
 * time value of all the source simulations.
 * 
 * @author Sam Macbeth
 * 
 */
class TimeColumn extends IndependentVariable {

	final int MAX;

	TimeColumn(Set<PersistentSimulation> sources) {
		super("timestep");
		int maxT = 0;
		for (PersistentSimulation s : sources) {
			if (s.getCurrentTime() > maxT)
				maxT = s.getCurrentTime();
		}
		MAX = maxT;
	}

	@Override
	public Iterator<String> iterator() {
		return new Iterator<String>() {
			int current = 0;

			@Override
			public boolean hasNext() {
				return current < MAX;
			}

			@Override
			public String next() {
				return Integer.toString(current++);
			}

			@Override
			public void remove() {
			}
		};
	}

}
