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
package de.cesr.lara.testing.components.decision;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeDecider;
import de.cesr.lara.components.decision.impl.LHabitDecider;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.util.impl.LPrefEntry;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;
import de.cesr.lara.testing.LTestUtils.LTestDecisionConfig;
import de.cesr.lara.testing.LTestUtils.LTestPreference1;
import de.cesr.parma.core.PmParameterManager;

/**
 * @author Sascha Holzhauer
 */
public class LHabitDeciderTest {

	LTestAgent agent;
	LTestBo one;
	LTestBo two;

	LaraDecisionConfiguration dConfig = new LTestDecisionConfig();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel();

		agent = new LTestAgent("TestAgent");

		one = new LTestBo("Bo1", agent, new LPrefEntry(LTestPreference1.class,
				new Double(1.0)));
		two = new LTestBo("Bo2", agent, new LPrefEntry(LTestPreference1.class,
				new Double(0.5)));

		agent.getLaraComp().getBOMemory().memorize(one);
		agent.getLaraComp().getBOMemory().memorize(two);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		one = null;
		two = null;
		agent = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LHabitDecider#getSelectedBo()}
	 * .
	 */
	@Test
	public void testGetSelectedBo() {
		// The default mode selector is LDefaultDecisionModeSelector
		// which supports habit.
		LEventbus.getInstance().subscribe(agent, LAgentPreprocessEvent.class);
		LEventbus.getInstance().subscribe(agent, LAgentDecideEvent.class);
		LEventbus.getInstance().subscribe(agent, LAgentPostprocessEvent.class);

		// deliberative decision making for X steps:
		for (int i = 0; i < (Integer) PmParameterManager
				.getParameter(LDecisionMakingPa.HABIT_THRESHOLD); i++) {
			LEventbus.getInstance().publish(new LModelStepEvent());
			LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
			LEventbus.getInstance().publish(new LAgentDecideEvent(dConfig));
			LEventbus.getInstance()
					.publish(new LAgentPostprocessEvent(dConfig));

			assertEquals(i + 1, agent.getLaraComp().getGeneralMemory()
					.getSize());
			assertEquals(LDeliberativeDecider.class, agent.getLaraComp()
					.getDecisionData(dConfig).getDecider().getClass());
		}

		// X + 1 should be habit:
		LEventbus.getInstance().publish(new LModelStepEvent());
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
		LEventbus.getInstance().publish(new LAgentDecideEvent(dConfig));
		LEventbus.getInstance().publish(new LAgentPostprocessEvent(dConfig));
		assertEquals(
				(Integer) PmParameterManager
						.getParameter(LDecisionMakingPa.HABIT_THRESHOLD) + 1,
				agent.getLaraComp().getGeneralMemory().getSize());
		assertEquals(LHabitDecider.class,
				agent.getLaraComp().getDecisionData(dConfig).getDecider()
						.getClass());
		assertEquals(one.getKey(), agent.getLaraComp().getDecisionData(dConfig)
				.getDecider().getSelectedBo().getKey());
	}
}
