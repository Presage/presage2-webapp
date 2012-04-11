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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;
import uk.ac.imperial.presage2.web.GenericPresageServlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * Servlet to export tabular data from a {@link StorageService}.
 * </p>
 * 
 * <p>
 * There are two entities to this servlet:
 * </p>
 * 
 * <ul>
 * <li>An exporter engine which takes a specification for a table in JSON and
 * processes this to export a file representation of this in the desired format.
 * </li>
 * <li>A html form to submit table specifications to the former.</li>
 * 
 * @author Sam Macbeth
 * 
 */
@Singleton
public class DataExportServlet extends GenericPresageServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	protected DataExportServlet(DatabaseService db, StorageService sto)
			throws Exception {
		super(db, sto);
	}

	/**
	 * Generates html form to submit table queries.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logRequest(req);
		PrintWriter out = resp.getWriter();
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("	<title>Export data</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("	<form method=\"POST\">");
		out.println("		<textarea name=\"query\" cols=\"70\" rows=\"20\"></textarea>");
		out.println("		<select name=\"format\">");
		out.println("			<option value=\"csv\">csv</option>");
		out.println("		</select>");
		out.println("		<input type=\"submit\" value=\"submit\"/>");
		out.println("	</form>");
		out.println("</body>");
		out.println("</html>");
	}

	/**
	 * Takes a POST request with <code>query</code> parameter being a JSON table
	 * specification and writes the resulting table back as the response. By
	 * default we return a CSV file with the {@link CSVTableExporter}. Alternate
	 * {@link TableExporter}s can be specified with the <code>format</code>
	 * parameter.
	 * 
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logRequest(req);
		JSONObject query = null;
		try {
			query = new JSONObject(new JSONTokener(req.getParameter("query")));
			logger.info(query.toString());
		} catch (JSONException e) {
			logger.warn("Failed to parse postdata", e);
			resp.sendError(400, "Failed to parse postdata: " + e.getMessage());
			return;
		}
		try {
			Iterable<Iterable<String>> table = processRequest(query);
			TableExporter exporter = new CSVTableExporter();
			exporter.httpExportTable(table, resp);
		} catch (JSONException e) {
			logger.warn("Failed to process request", e);
			resp.sendError(400, "Failed to process request");
		}
	}

	/**
	 * Processes the JSON query and returns a table in the form of a double
	 * {@link Iterable}.
	 * 
	 * @param query
	 *            JSON table specification.
	 * @return An {@link Iterable} which iterates over each row in the result
	 *         set, which is also given as an {@link Iterable}.
	 * @throws JSONException
	 *             If there is an error in the specification.
	 */
	Iterable<Iterable<String>> processRequest(JSONObject query)
			throws JSONException {

		// build source set
		Set<PersistentSimulation> sourceSims = new HashSet<PersistentSimulation>();

		Set<Long> sources = new HashSet<Long>();
		JSONArray sourcesJSON = query.getJSONArray("sources");
		for (int i = 0; i < sourcesJSON.length(); i++) {
			long simId = sourcesJSON.getLong(i);
			PersistentSimulation sim = sto.getSimulationById(simId);
			if (sim != null) {
				// add simulation and all of it's decendents.
				getDecendents(simId, sources);
			}
		}
		// build sourceSims set from simIds we have collected.
		for (Long sim : sources) {
			sourceSims.add(sto.getSimulationById(sim));
		}

		// check type

		String typeName = query.getString("type");
		if (typeName.equalsIgnoreCase("transient")) {

			JSONArray columns = query.getJSONArray("columns");
			ColumnDefinition[] columnDefs = new ColumnDefinition[columns
					.length()];
			for (int i = 0; i < columns.length(); i++) {
				columnDefs[i] = ColumnDefinition.createColumn(
						columns.getJSONObject(i), sourceSims);
			}

			return new IterableTable(new TimeColumn(sourceSims), columnDefs);
		}
		throw new JSONException("Unknown type.");
	}

	/**
	 * Parses the simulation hierarchy to ensure the <code>decendents</code> set
	 * contains all simulations which decend from <code>simId</code>.
	 * 
	 * @param simId
	 *            simulation Id to start traversal from.
	 * @param decendents
	 *            {@link Set} to store decendents in.
	 */
	private void getDecendents(long simId, Set<Long> decendents) {
		if (decendents.contains(simId)) {
			return;
		}
		PersistentSimulation sim = sto.getSimulationById(simId);
		if (sim != null) {
			decendents.add(simId);
			for (long child : sim.getChildren()) {
				if (!decendents.contains(child)) {
					getDecendents(child, decendents);
				}
			}
		}
	}

	/**
	 * Implementation of a table of data as an {@link Iterable}. Uses one
	 * {@link IndependentVariable} to generate values for each
	 * {@link ColumnDefinition} in the table.
	 * 
	 * @author Sam Macbeth
	 * 
	 */
	private class IterableTable implements Iterable<Iterable<String>> {

		final IndependentVariable independentVar;
		final ColumnDefinition[] dependentVars;

		IterableTable(IndependentVariable independentVar,
				ColumnDefinition... dependentVars) {
			super();
			this.independentVar = independentVar;
			this.dependentVars = dependentVars;
		}

		@Override
		public Iterator<Iterable<String>> iterator() {
			if (independentVar == null || dependentVars == null)
				return null;

			return new Iterator<Iterable<String>>() {

				boolean header = true;
				Iterator<String> input = independentVar.iterator();

				@Override
				public boolean hasNext() {
					return header || input.hasNext();
				}

				@Override
				public Iterable<String> next() {
					// return next row
					List<String> row = new LinkedList<String>();
					if (header) {
						row.add(independentVar.getName());
						for (int i = 0; i < dependentVars.length; i++) {
							row.add(dependentVars[i].getName());
						}
						header = false;
					} else {
						String in = input.next();
						row.add(in);
						for (int i = 0; i < dependentVars.length; i++) {
							row.add(dependentVars[i].getColumnValue(in));
						}
					}
					return row;
				}

				@Override
				public void remove() {
				}
			};
		}

	}
}
