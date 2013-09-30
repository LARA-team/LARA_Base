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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.impl.LDefaultAgentComp;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.postprocessor.LaraPostprocessorComp;
import de.cesr.lara.components.util.impl.LPrefEntry;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;


/**
 * 
 * @author Sascha Holzhauer
 * @date 25.01.2010
 * 
 */
public class LDefaultAgentCompTest {

	LTestAgent agent;

	Class<? extends LaraPreference> goal1;

	int testInt = 0;

	/**
	 * Does not contribute to any goal
	 */
	LTestBo bo1;

	LaraDecisionConfiguration dConfig = LTestUtils.dConfig;

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LTestUtils.initTestModel(dConfig);

		goal1 = new LaraPreference() {
		}.getClass();

		agent = new LTestAgent("LTestAgent");

		Map<Class<? extends LaraPreference>, Double> utilities = new HashMap<Class<? extends LaraPreference>, Double>();
		bo1 = new LTestBo(agent, utilities);
		utilities.put(goal1, 0.0);

		agent.getLaraComp().getBOMemory().memorize(bo1);

		dConfig = new LDecisionConfiguration("TestDecision");
		LDefaultAgentComp.setDefaultDeliberativeChoiceComp(dConfig,
				LDeliberativeChoiceComp_MaxLineTotalRandomAtTie.getInstance(null));
		List<Class<? extends LaraPreference>> goals = new ArrayList<Class<? extends LaraPreference>>();
		goals.add(goal1);
		dConfig.setPreferences(goals);
	}
	
	/**
	 * 
	 */
	@Test
	public void testPreferenceSetting() {
		agent.getLaraComp().addPreferenceWeights(new LPrefEntry(goal1, new Double(1.0)));
		assertEquals(1.0, agent.getLaraComp().getPreferenceWeight(goal1), 0.001);
	}


	/**
	 * Test correct execution of post processing.
	 */
	@Test
	public void testPostprocessing() {
		agent.getLaraComp().setPostProcessor(new LaraPostprocessorComp<LTestUtils.LTestAgent, LTestUtils.LTestBo>() {

			@Override
			public void postProcess(LTestAgent agent, LaraDecisionConfiguration dConfig) {
				// UNDO
				System.out.println("----------TEST");
				LDefaultAgentCompTest.this.testInt = 1;
			}
		});
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(1, this.testInt);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
}