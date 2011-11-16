/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 30.09.2010
 */
package de.cesr.lara.testing.components.impl;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.testing.TestUtils;
import de.cesr.lara.testing.TestUtils.TestAgent;


/**
 * Tests hashCode() and equals()...
 */
public class AbstractLaraAgentTest {

	LAbstractAgent<TestUtils.TestAgent, LGeneralBehaviouralOption<TestAgent>>	agent1;
	LAbstractAgent<TestUtils.TestAgent, LGeneralBehaviouralOption<TestAgent>>	agent2;
	LAbstractAgent<TestUtils.TestAgent, LGeneralBehaviouralOption<TestAgent>>	agent3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agent1 = new TestUtils.TestAgent("Agent1");
		agent2 = new TestUtils.TestAgent("Agent2");
		agent3 = new TestUtils.TestAgent("Agent2");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		agent1 = null;
		agent2 = null;
	}

	/**
	 * Test method for {@link de.cesr.lara.components.agents.impl.LAbstractAgent#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		assertEquals(agent1.hashCode(), new String("Agent1").hashCode());
		assertEquals(agent2.hashCode(), new String("Agent2").hashCode());
		assertEquals(agent3.hashCode(), new String("Agent2").hashCode());
		assertEquals(agent2.hashCode(), agent3.hashCode());
	}

	/**
	 * Test method for {@link de.cesr.lara.components.agents.impl.LAbstractAgent#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals(agent2, agent3);
		assertNotSame(agent1, agent2);
	}

}
