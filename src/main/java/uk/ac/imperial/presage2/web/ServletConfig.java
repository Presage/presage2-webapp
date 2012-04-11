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
package uk.ac.imperial.presage2.web;

import uk.ac.imperial.presage2.core.cli.run.ExecutorModule;
import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.web.export.DataExportServlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

public class ServletConfig extends GuiceServletContextListener {

	@Override
	protected Injector getInjector() {

		DatabaseModule db = DatabaseModule.load();
		if (db == null) {
			throw new RuntimeException(
					"Cannot start webapp without database connection.");
		}
		return Guice.createInjector(db, ExecutorModule.load(),
				new ServletModule() {

					@Override
					protected void configureServlets() {
						super.configureServlets();
						serve("/simulations/tree*").with(
								SimulationsTreeServlet.class);
						serve("/simulations/data").with(SimDataServlet.class);
						serve("/simulations*").with(SimulationServlet.class);
						serve("/export").with(DataExportServlet.class);
					}
				});
	}

}
