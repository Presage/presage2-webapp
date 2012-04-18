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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
		inputJson.put("parameters", new JSONArray(new String[] { "time" }));
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
				mean, count }));

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
		inputJson.put("parameters", new JSONArray(new String[] { "time" }));
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
				mean, count }));

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
	public void testTransientAgentPropertyWithCondition() throws JSONException {
		Random rand = new Random();

		// prepare test data
		logger.info("Creating conditioned TransientAgentProperty test data set");
		int simCount = rand.nextInt(9) + 1;
		long[] simIds = new long[simCount];

		// expectations of test data.
		int[] expectedCount1 = new int[100];
		int[] expectedCount2 = new int[100];
		Arrays.fill(expectedCount2, 0);
		int[] expectedCount3 = new int[100];
		Arrays.fill(expectedCount3, 0);

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
				boolean test = rand.nextBoolean();
				ag.setProperty("testA", Boolean.toString(test));
				if (test) {
					for (int j = 0; j < finishAt; j++) {
						expectedCount1[j]++;
					}
				}
			}
			for (int i = 0; i < finishAt; i++) {
				for (PersistentAgent a : agents) {
					int datapoint = rand.nextInt(100);

					a.getState(i).setProperty("test",
							Integer.toString(datapoint));
					if (datapoint > 50)
						expectedCount2[i]++;
					if (datapoint <= 75
							&& a.getProperty("testA").equalsIgnoreCase("true"))
						expectedCount3[i]++;
				}
			}
			simIds[s] = sim.getID();
			logger.info("Sim " + sim.getID() + ", " + finishAt + " steps, "
					+ agentCount + " agents.");
		}

		// prepare input JSON
		JSONObject inputJson = new JSONObject();
		inputJson.put("sources", new JSONArray(simIds));
		inputJson.put("parameters", new JSONArray(new String[] { "time" }));

		JSONObject test1 = new JSONObject();
		test1.put("type", "AGENT");
		test1.put("property", "test");
		test1.put("function", "COUNT");
		JSONObject test1Condition = new JSONObject();
		test1Condition.put("testA", true);
		test1.put("condition", test1Condition);

		JSONObject test2 = new JSONObject();
		test2.put("type", "AGENT");
		test2.put("property", "test");
		test2.put("function", "COUNT");
		JSONObject test2Condition = new JSONObject(
				"{\"$t\":{\"test\":{\"$gt\":50}}}");
		test2.put("condition", test2Condition);

		JSONObject test3 = new JSONObject();
		test3.put("type", "AGENT");
		test3.put("property", "test");
		test3.put("function", "COUNT");
		JSONObject test3Condition = new JSONObject();
		test3Condition.put("testA", true);
		test3Condition.put("$t", new JSONObject("{\"test\":{\"$lte\":75}}"));
		test3.put("condition", test3Condition);

		inputJson.put("columns", new JSONArray(new JSONObject[] { test1, test2,
				test3 }));

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
						assertEquals(expectedCount1[row],
								Integer.parseInt(string));
						break;
					// max col
					case 2:
						assertEquals(expectedCount2[row],
								Integer.parseInt(string));
						break;
					// mean col
					case 3:
						assertEquals(expectedCount3[row],
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
	public void testIndependentVariables() throws JSONException {
		Random rand = new Random();

		// prepare test data
		logger.info("Creating testIndependentVariables data set");
		int simCount = 10;
		long[] simIds = new long[simCount];
		Map<String, Map<String, Integer>> expected = new HashMap<String, Map<String, Integer>>();

		for (int s = 0; s < simCount; s++) {
			int finishAt = 100;
			PersistentSimulation sim = sto.createSimulation("Test", "test",
					"TEST", finishAt);
			sim.setCurrentTime(finishAt);
			String x = Integer.toString(s % 4);
			String y = (s < 5) ? "a" : "b";
			sim.addParameter("x", x);
			sim.addParameter("y", y);
			PersistentEnvironment env = sim.getEnvironment();
			int datapoint = rand.nextInt(100);
			env.setProperty("test", Integer.toString(datapoint));
			if (!expected.containsKey(x)) {
				expected.put(x, new HashMap<String, Integer>());
			}
			if (!expected.get(x).containsKey(y)) {
				expected.get(x).put(y, datapoint);
			} else {
				expected.get(x).put(y, expected.get(x).get(y) + datapoint);
			}
			simIds[s] = sim.getID();
			logger.info("Sim " + sim.getID() + ", " + finishAt + " steps.");
		}

		JSONObject inputJson = new JSONObject();
		inputJson.put("sources", new JSONArray(simIds));
		inputJson.put("parameters", new JSONArray(new String[] { "x", "y" }));

		JSONObject test1 = new JSONObject();
		test1.put("type", "ENV");
		test1.put("property", "test");
		test1.put("function", "SUM");

		inputJson.put("columns", new JSONArray(new JSONObject[] { test1 }));

		logger.info("Test json input: " + inputJson.toString());

		// test servlet
		Iterable<Iterable<String>> actual = servletUnderTest
				.processRequest(inputJson);

		int row = -1;
		int col = 0;
		for (Iterable<String> iterable : actual) {
			logger.info("Test results row: " + row);
			logger.info(iterable.toString());
			String x = null;
			String y = null;
			for (String string : iterable) {
				if (row > -1) {
					switch (col) {
					// x col
					case 0:
						x = string;
						break;
					// y col
					case 1:
						y = string;
						break;
					// sum col
					case 2:
						assertEquals(expected.get(x).get(y).intValue(),
								Integer.parseInt(string));
						break;
					}
					col++;
				}
				col = 0;
				row++;
			}
		}
	}
}
