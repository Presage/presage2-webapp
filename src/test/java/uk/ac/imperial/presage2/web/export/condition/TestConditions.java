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
package uk.ac.imperial.presage2.web.export.condition;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConditions {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws JSONException {
		// set up condition JSON object
		JSONObject conditionSpec = new JSONObject();
		conditionSpec.put("role", "a");
		JSONObject gt = new JSONObject();
		gt.put("$gt", 5);
		conditionSpec.put("x", gt);
		JSONObject t = new JSONObject();
		t.put("grouphead", true);
		conditionSpec.put("$t", t);

		// create a root condition
		RootCondition condition = new RootCondition(conditionSpec);

		// test condition
		Map<String, String> testInput = new HashMap<String, String>();

		assertFalse(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("role", "a");
		assertFalse(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("x", "1");
		assertFalse(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("x", "5");
		assertFalse(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("grouphead", "false");
		assertFalse(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("x", "10");
		assertTrue(condition.testStatic(testInput));
		assertFalse(condition.testTransient(testInput));
		
		testInput.put("grouphead", "true");
		assertTrue(condition.testStatic(testInput));
		assertTrue(condition.testTransient(testInput));
		
		testInput.remove("role");
		assertFalse(condition.testStatic(testInput));
		assertTrue(condition.testTransient(testInput));
	}

}
