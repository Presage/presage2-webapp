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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

@Singleton
public class SimulationsTreeServlet extends GenericPresageServlet {

	private static final long serialVersionUID = 1L;
	private final static Pattern ID_REGEX = Pattern.compile("/(\\d+)$");

	@Inject
	protected SimulationsTreeServlet(DatabaseService db, StorageService sto)
			throws Exception {
		super(db, sto);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logRequest(req);
		// check which node to check from
		String node = req.getParameter("node");
		if (node == null) {
			resp.setStatus(400);
			return;
		}
		long nodeId;
		if (node.equalsIgnoreCase("root")) {
			nodeId = 0;
		} else {
			try {
				nodeId = Long.parseLong(node);
			} catch (NumberFormatException e) {
				resp.setStatus(400);
				return;
			}
		}

		List<PersistentSimulation> sims = new LinkedList<PersistentSimulation>();
		for (Long simId : this.sto.getSimulations()) {
			PersistentSimulation sim = this.sto.getSimulationById(simId);
			PersistentSimulation parent = sim.getParentSimulation();
			if ((nodeId == 0 && parent == null)
					|| (parent != null && nodeId > 0 && parent.getID() == nodeId)) {
				sims.add(sim);
			}
		}
		// build JSON response
		try {
			JSONObject jsonResp = new JSONObject();
			JSONArray jsonSims = new JSONArray();
			for (PersistentSimulation sim : sims) {
				jsonSims.put(SimulationServlet.simulationToJSON(sim));
			}
			jsonResp.put("data", jsonSims);
			jsonResp.put("success", true);
			resp.setStatus(200);
			resp.getWriter().write(jsonResp.toString());
		} catch (JSONException e) {
			resp.setStatus(500);
		}
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logRequest(req);
		String path = req.getPathInfo();
		Matcher matcher = ID_REGEX.matcher(path);
		if (matcher.matches()) {
			long simId = Integer.parseInt(matcher.group(1));
			try {
				// get data sent to us
				JSONObject input = new JSONObject(new JSONTokener(
						req.getReader()));
				if (simId > 0
						&& (input.getString("parentId").equalsIgnoreCase("root") || input
								.getLong("parentId") > 0)) {
					PersistentSimulation sim = sto.getSimulationById(simId);
					PersistentSimulation parent = null;
					if (input.getString("parentId").equalsIgnoreCase("root")) {
						parent = null;
					} else if (input.getLong("parentId") > 0
							&& input.getLong("parentId") != simId) {
						parent = sto.getSimulationById(input
								.getLong("parentId"));
					} else {
						resp.setStatus(400);
						return;
					}
					if (sim != null) {
						sim.setParentSimulation(parent);
						resp.setStatus(200);
						return;
					}
				}
				resp.setStatus(400);
			} catch (JSONException e) {
				resp.setStatus(400);
			}
		} else {
			resp.setStatus(400);
		}
	}

}
