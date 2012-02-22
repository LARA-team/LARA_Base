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
import static org.junit.Assert.assertNotSame;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestBo;


/**
 * Tests hashCode() and equals()...
 */
public class LAbstractAgentTest {

	LAbstractAgent<LTestUtils.LTestAgent, LTestBo> agent1;
	LAbstractAgent<LTestUtils.LTestAgent, LTestBo> agent2;
	LAbstractAgent<LTestUtils.LTestAgent, LTestBo> agent3;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		agent1 = new LTestUtils.LTestAgent("Agent1");
		agent2 = new LTestUtils.LTestAgent("Agent2");
		agent3 = new LTestUtils.LTestAgent("Agent2");
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
