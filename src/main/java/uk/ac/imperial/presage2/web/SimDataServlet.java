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

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SimDataServlet extends GenericPresageServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	public SimDataServlet(DatabaseService db, StorageService sto)
			throws Exception {
		super(db, sto);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logRequest(req);
		// find simid parameter
		long simId = 0;
		if (req.getParameter("filter") != null) {
			// extjs param
			try {
				JSONArray filter = new JSONArray(req.getParameter("filter"));
				JSONObject first = (JSONObject) filter.get(0);
				if (first.getString("property").equals(
						"presage2.model.simulation_id")) {
					simId = first.getInt("value");
				} else {
					resp.setStatus(400);
					return;
				}
			} catch (JSONException e) {
				resp.setStatus(400);
				return;
			}
		} else if (req.getParameter("id") != null) {
			simId = Long.parseLong(req.getParameter("id").toString());
		} else {
			resp.setStatus(400);
			return;
		}
		if (simId > 0) {
			// list transient data for simulation specified
			final int start = getIntegerParameter(req, "start", 0);
			final int limit = getIntegerParameter(req, "limit", 25);

			// get simulation
			PersistentSimulation sim = sto.getSimulationById(simId);
			if (sim == null) {
				resp.setStatus(401);
				return;
			}
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put("simId", simId);
				// get number of data points
				responseJson.put("totalCount", sim.getCurrentTime());

				JSONArray datapoints = new JSONArray();
				if (sim.getCurrentTime() > start) {
					int current = start;
					while (current < start + limit
							&& current < sim.getCurrentTime()) {
						JSONObject point = new JSONObject();
						point.put("time", current);
						for (Map.Entry<String, String> entry : sim
								.getEnvironment().getProperties().entrySet()) {
							point.put(entry.getKey(), entry.getValue());
						}
						JSONArray agents = new JSONArray();
						for (PersistentAgent agent : sim.getAgents()) {
							JSONObject agentJson = new JSONObject();
							agentJson.put("aid", agent.getID());
							agentJson.put("data", agent.getState(current)
									.getProperties());
							agents.put(agentJson);
						}
						point.put("agents", agents);
						datapoints.put(point);
						current++;
					}
				}
				responseJson.put("data", datapoints);
				responseJson.put("success", true);
				resp.setStatus(200);
				resp.getWriter().write(responseJson.toString());
			} catch (JSONException e) {
				resp.setStatus(500);
			}
		} else {
			resp.setStatus(400);
		}

	}

}
