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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.imperial.presage2.core.db.DatabaseModule;
import uk.ac.imperial.presage2.core.db.DatabaseService;
import uk.ac.imperial.presage2.core.db.StorageService;
import uk.ac.imperial.presage2.core.db.persistent.PersistentAgent;
import uk.ac.imperial.presage2.core.db.persistent.PersistentEnvironment;
import uk.ac.imperial.presage2.core.db.persistent.PersistentSimulation;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestDataExport {

	private final Logger logger = Logger.getLogger(TestDataExport.class);

	DatabaseModule dbmodule = DatabaseModule.load();
	DatabaseService db;
	StorageService sto;
	DataExportServlet servletUnderTest;

	@Before
	public void setUp() throws Exception {
		Injector injector = Guice.createInjector(dbmodule);
		db = injector.getInstance(DatabaseService.class);
		db.start();
		sto = injector.getInstance(StorageService.class);

		servletUnderTest = new DataExportServlet(db, sto);
	}

	@After
	public void tearDown() throws Exception {
		db.stop();
	}

	@Test
	public void testTransientEnvironmentProperty() throws JSONException,
			IOException {
		Random rand = new Random();

		// prepare test data
		logger.info("Creating TransientEnvironmentProperty test data set");
		int simCount = rand.nextInt(9) + 1;
		long[] simIds = new long[simCount];

		// expectations of test data.
		int[] expectedCount = new int[100];
		double[] expectedSum = new double[100];
		int[] expectedMin = new int[100];
		int[] expectedMax = new int[100];
		Arrays.fill(expectedCount, 0);
		Arrays.fill(expectedSum, 0.0);
		Arrays.fill(expectedMin, 100);
		Arrays.fill(expectedMax, 0);

		for (int s = 0; s < simCount; s++) {
			int finishAt = rand.nextInt(100);
			PersistentSimulation sim = sto.createSimulation("Test", "test",
					"TEST", finishAt);
			sim.setCurrentTime(finishAt);
			PersistentEnvironment env = sim.getEnvironment();
			for (int i = 0; i < finishAt; i++) {
				int datapoint = rand.nextInt(100);
				env.setProperty("test", i, Integer.toString(datapoint));
				expectedCount[i]++;
				expectedSum[i] += datapoint;
				if (datapoint < expectedMin[i])
					expectedMin[i] = datapoint;
				if (datapoint > expectedMax[i])
					expectedMax[i] = datapoint;
			}
			simIds[s] = sim.getID();
			logger.info("Sim " + sim.getID() + ", " + finishAt + " steps.");
		}

		// prepare input JSON
		JSONObject inputJson = new JSONObject();
		inputJson.put("sources", new JSONArray(simIds));
		inputJson.put("type", "TRANSIENT");
		JSONObject min = new JSONObject();
		min.put("type", "ENV");
		min.put("property", "test");
		min.put("function", "MIN");
		JSONObject max = new JSONObject();
		max.put("type", "ENV");
		max.put("property", "test");
		max.put("function", "MAX");
		JSONObject mean = new JSONObject();
		mean.put("type", "ENV");
		mean.put("property", "test");
		mean.put("function", "MEAN");
		JSONObject count = new JSONObject();
		count.put("type", "ENV");
		count.put("property", "test");
		count.put("function", "COUNT");
		inputJson.put("columns", new JSONArray(new JSONObject[] { min, max,
				mean }));

		logger.info("Test json input: " + inputJson.toString());

		// test servlet
		Iterable<Iterable<String>> actual = servletUnderTest
				.processRequest(inputJson);

		// check expectations
		int row = -1;
		int col = 0;
		for (Iterable<String> iterable : actual) {
			logger.info("Test results row: " + row);
			for (String string : iterable) {
				if (row == -1) {
					switch (col) {
					case 0:
						assertEquals("timestep", string);
						break;
					}
				} else {
					switch (col) {
					// timestep col
					case 0:
						assertEquals(row, Integer.parseInt(string));
						break;
					// min col
					case 1:
						assertEquals(expectedMin[row],
								Double.parseDouble(string), 0.0001);
						break;
					// max col
					case 2:
						assertEquals(expectedMax[row],
								Double.parseDouble(string), 0.0001);
						break;
					// mean col
					case 3:
						assertEquals(expectedSum[row] / expectedCount[row],
								Double.parseDouble(string), 0.0001);
						break;
					// count col
					case 4:
						assertEquals(expectedCount[row],
								Integer.parseInt(string));
						break;
					}
				}
				col++;
			}
			col = 0;
			row++;
		}
	}

	@Test
	public void testTransientAgentProperty() throws JSONException {
		Random rand = new Random();

		// prepare test data
		logger.info("Creating TransientAgentProperty test data set");
		int simCount = rand.nextInt(9) + 1;
		long[] simIds = new long[simCount];

		// expectations of test data.
		int[] expectedCount = new int[100];
		double[] expectedSum = new double[100];
		int[] expectedMin = new int[100];
		int[] expectedMax = new int[100];
		Arrays.fill(expectedCount, 0);
		Arrays.fill(expectedSum, 0.0);
		Arrays.fill(expectedMin, 100);
		Arrays.fill(expectedMax, 0);

		for (int s = 0; s < simCount; s++) {
			int finishAt = 1 + rand.nextInt(99);
			int agentCount = 1 + rand.nextInt(20);
			PersistentSimulation sim = sto.createSimulation("Test", "test",
					"TEST", finishAt);
			sto.setSimulation(sim);
			sim.setCurrentTime(finishAt);
			Set<PersistentAgent> agents = new HashSet<PersistentAgent>();
			for (int a = 0; a < agentCount; a++) {
				PersistentAgent ag = sto.createAgent(
						uk.ac.imperial.presage2.core.util.random.Random
								.randomUUID(), "agent" + a);
				agents.add(ag);
			}
			for (int i = 0; i < finishAt; i++) {
				for (PersistentAgent a : agents) {
					int datapoint = rand.nextInt(100);
					a.getState(i).setProperty("test",
							Integer.toString(datapoint));
					expectedCount[i]++;
					expectedSum[i] += datapoint;
					if (datapoint < expectedMin[i])
						expectedMin[i] = datapoint;
					if (datapoint > expectedMax[i])
						expectedMax[i] = datapoint;
				}
			}
			simIds[s] = sim.getID();
			logger.info("Sim " + sim.getID() + ", " + finishAt + " steps, "
					+ agentCount + " agents.");
		}

		// prepare input JSON
		JSONObject inputJson = new JSONObject();
		inputJson.put("sources", new JSONArray(simIds));
		inputJson.put("type", "TRANSIENT");
		JSONObject min = new JSONObject();
		min.put("type", "AGENT");
		min.put("property", "test");
		min.put("function", "MIN");
		JSONObject max = new JSONObject();
		max.put("type", "AGENT");
		max.put("property", "test");
		max.put("function", "MAX");
		JSONObject mean = new JSONObject();
		mean.put("type", "AGENT");
		mean.put("property", "test");
		mean.put("function", "MEAN");
		JSONObject count = new JSONObject();
		count.put("type", "AGENT");
		count.put("property", "test");
		count.put("function", "COUNT");
		inputJson.put("columns", new JSONArray(new JSONObject[] { min, max,
				mean }));

		logger.info("Test json input: " + inputJson.toString());

		// test servlet
		Iterable<Iterable<String>> actual = servletUnderTest
				.processRequest(inputJson);

		// check expectations
		int row = -1;
		int col = 0;
		for (Iterable<String> iterable : actual) {
			logger.info("Test results row: " + row);
			for (String string : iterable) {
				if (row == -1) {
					switch (col) {
					case 0:
						assertEquals("timestep", string);
						break;
					}
				} else {
					switch (col) {
					// timestep col
					case 0:
						assertEquals(row, Integer.parseInt(string));
						break;
					// min col
					case 1:
						assertEquals(expectedMin[row],
								Double.parseDouble(string), 0.0001);
						break;
					// max col
					case 2:
						assertEquals(expectedMax[row],
								Double.parseDouble(string), 0.0001);
						break;
					// mean col
					case 3:
						assertEquals(expectedSum[row] / expectedCount[row],
								Double.parseDouble(string), 0.0001);
						break;
					// count col
					case 4:
						assertEquals(expectedCount[row],
								Integer.parseInt(string));
						break;
					}
				}
				col++;
			}
			col = 0;
			row++;
		}
	}

}
