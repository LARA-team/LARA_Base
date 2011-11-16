/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 25.01.2010
 */
package de.cesr.lara.testing.components.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.testing.TestUtils;


/**
 * 
 * @author Sascha Holzhauer
 * @date 25.01.2010
 * 
 */
public class DefaultLaraAgentCompTest {

	LaraAgent		testAgent;
	LaraEnvironment	env;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 25.01.2010
	 */
	@Before
	public void setUp() throws Exception {
		env = new LEnvironment();
		testAgent = new TestUtils.TestAgent("TestAgent");
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 25.01.2010
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.agents.impl.LDefaultAgentComp#getEnvironment(java.lang.reflect.Type)}.
	 */
	@Test
	@Ignore
	public final void testGetEnvironment() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * 
	 * Created by Sascha Holzhauer on 25.01.2010
	 */
	@Test
	public final void testDoubleProperty() {
		testAgent.getLaraComp().setDoubleProperty("NewProperty1", 42.0);
		testAgent.getLaraComp().setDoubleProperty("NewProperty2", 43.0);
		assertEquals("NewProperty1 should be 42.0", 42.0, testAgent.getLaraComp().getDoubleProperty("NewProperty1"),
				0.0);
		assertEquals("NewProperty1 should be 43.0", 43.0, testAgent.getLaraComp().getDoubleProperty("NewProperty2"),
				0.0);
		assertEquals("UnknownProperty should yield NaN", Double.NaN, testAgent.getLaraComp().getDoubleProperty(
				"UnknownProperty"), 0.0);
		testAgent.getLaraComp().setDoubleProperty("NewProperty2", 40.0);
		assertEquals("NewProperty2 should no be 40.0", 40.0, testAgent.getLaraComp().getDoubleProperty("NewProperty2"),
				0.0);
	}

}
