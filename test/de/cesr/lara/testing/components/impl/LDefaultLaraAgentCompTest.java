/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.testing.components.impl;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.testing.LTestUtils.LTestAgent;

/**
 * 
 * @author Sascha Holzhauer
 * @date 25.01.2010
 * 
 */
public class LDefaultLaraAgentCompTest {

	LTestAgent testAgent;
	LaraEnvironment env;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		env = new LEnvironment();
		testAgent = new LTestAgent("LTestAgent");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 
	 */
	@Test
	public final void testDoubleProperty() {
		testAgent.getLaraComp().setDoubleProperty("NewProperty1", 42.0);
		testAgent.getLaraComp().setDoubleProperty("NewProperty2", 43.0);
		assertEquals("NewProperty1 should be 42.0", 42.0, testAgent
				.getLaraComp().getDoubleProperty("NewProperty1"), 0.0);
		assertEquals("NewProperty1 should be 43.0", 43.0, testAgent
				.getLaraComp().getDoubleProperty("NewProperty2"), 0.0);
		assertEquals("UnknownProperty should yield NaN", Double.NaN, testAgent
				.getLaraComp().getDoubleProperty("UnknownProperty"), 0.0);
		testAgent.getLaraComp().setDoubleProperty("NewProperty2", 40.0);
		assertEquals("NewProperty2 should no be 40.0", 40.0, testAgent
				.getLaraComp().getDoubleProperty("NewProperty2"), 0.0);
	}
}