/**
 * 	Copyright (C) 2011 Sam Macbeth <sm1106 [at] imperial [dot] ac [dot] uk>
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ListSimulations extends HttpServlet {

	private final Logger logger = Logger.getLogger(ListSimulations.class);
	private static final long serialVersionUID = 1L;
	StorageService sto;

	private List<PersistentSimulation> cachedSimulations = null;
	private long cacheTime = 0;

	private final static String[] simulationFields = { "name", "classname",
			"state", "currentTime", "created", "started", "finished", "id" };

	@Inject
	public ListSimulations(DatabaseService db, StorageService sto)
			throws Exception {
		super();
		if (!db.isStarted())
			db.start();
		this.sto = sto;
	}

	@Override
	protected synchronized void doGet(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException, IOException {
		// paging parameters
		final int start = getIntegerParameter(req, "start", 0);
		final int limit = getIntegerParameter(req, "limit", 30);
		// sorting parameters
		String sort = req.getParameter("sort");
		String direction = req.getParameter("dir");
		if (sort == null || sort.isEmpty()) {
			sort = "id";
			direction = "ASC";
		} else if (direction == null || direction.isEmpty()) {
			direction = "ASC";
		}
		logger.info("GET " + req.getRequestURI() + "?" + req.getQueryString());

		// init response object
		JSONObject jsonResp = new JSONObject();
		try {
			// check sim cache (30s ttl)
			if (this.cacheTime < System.currentTimeMillis() - 30000) {
				logger.info("Refreshing simulation cache");
				// update cache from db
				List<Long> simulationIds = sto.getSimulations();
				this.cachedSimulations = new ArrayList<PersistentSimulation>(
						simulationIds.size());
				for (Long simId : simulationIds) {
					this.cachedSimulations.add(sto.getSimulationById(simId));
				}
				this.cacheTime = System.currentTimeMillis();
			}
			jsonResp.put("totalCount", this.cachedSimulations.size());

			// sort simulations
			Collections.sort(this.cachedSimulations, new SimulationComparator(
					sort, direction));

			// extract sims to JSON
			JSONArray simulations = new JSONArray();
			int count = 0;
			for (ListIterator<PersistentSimulation> it = this.cachedSimulations
					.listIterator(start); it.hasNext();) {
				PersistentSimulation sim = it.next();
				JSONObject jsonSim = new JSONObject();
				jsonSim.put("id", sim.getID());
				jsonSim.put("name", sim.getName());
				jsonSim.put("classname", sim.getClassName());
				jsonSim.put("state", sim.getState());
				jsonSim.put("finishTime", sim.getFinishTime());
				jsonSim.put("currentTime", sim.getCurrentTime());
				jsonSim.put("createdAt", sim.getCreatedAt());
				jsonSim.put("startedAt", sim.getStartedAt());
				jsonSim.put("finishedAt", sim.getFinishedAt());
				JSONObject parameters = new JSONObject();
				for (Entry<String, Object> param : sim.getParameters()
						.entrySet()) {
					parameters.put(param.getKey(), param.getValue().toString());
				}
				jsonSim.put("parameters", parameters);
				simulations.put(jsonSim);
				count++;
				if (count > limit)
					break;
			}
			jsonResp.put("simulations", simulations);

		} catch (JSONException e) {
			logger.error("JSON write error.", e);
			resp.setStatus(500);
		} finally {
			try {
				jsonResp.put("success", true);
				resp.setStatus(200);
				resp.getWriter().write(jsonResp.toString());
			} catch (JSONException e) {
				logger.error("Failed to write JSON success", e);
				resp.setStatus(500);
			}
		}

	}

	private int getIntegerParameter(HttpServletRequest req, String name,
			int defaultValue) {
		String param = req.getParameter(name);
		if (param == null || param == "") {
			return defaultValue;
		} else {
			return Integer.parseInt(param.toString());
		}
	}

	private class SimulationComparator implements
			Comparator<PersistentSimulation> {

		int field = simulationFields.length - 1;
		final int direction;
		final Comparator<String> stringComparator = String.CASE_INSENSITIVE_ORDER;

		SimulationComparator(String field, String direction) {
			super();
			for (int i = 0; i < simulationFields.length; i++) {
				if (field.equalsIgnoreCase(simulationFields[i])) {
					this.field = i;
					break;
				}
			}
			if (direction.equalsIgnoreCase("DESC"))
				this.direction = -1;
			else
				this.direction = 1;
		}

		@Override
		public int compare(PersistentSimulation s1, PersistentSimulation s2) {
			int diff = 0;
			switch (field) {
			case 0: // name
				diff = stringComparator.compare(s1.getName(), s1.getName());
				break;
			case 1: // classname
				diff = stringComparator.compare(s1.getClassName(),
						s2.getClassName());
				break;
			case 2: // state
				diff = stringComparator.compare(s1.getState(), s2.getState());
				break;
			case 3: // currentTime
				diff = s1.getCurrentTime() - s2.getCurrentTime();
				break;
			case 4: // created at
				diff = (int) (s1.getCreatedAt() - s2.getCreatedAt());
				break;
			case 5: // started at
				diff = (int) (s1.getStartedAt() - s2.getStartedAt());
				break;
			case 6: // finished at
				diff = (int) (s1.getFinishedAt() - s2.getFinishedAt());
				break;
			// id
			default:
				diff = (int) (s1.getID() - s2.getID());
				break;
			}
			diff *= this.direction;
			return diff;
		}

	}

}
