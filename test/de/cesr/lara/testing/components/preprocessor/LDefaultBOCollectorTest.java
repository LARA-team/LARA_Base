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
package de.cesr.lara.testing.components.preprocessor;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.impl.LDefaultAgentComp;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.impl.LContributingBoCollector;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;

/**
 * @author Sascha Holzhauer
 * @date 12.02.2010
 * 
 */
public class LDefaultBOCollectorTest {

	LTestAgent agent;
	LaraBOMemory<LTestBo> memory;

	LaraPreference goal1;
	LaraPreference goal2;
	LaraPreference goal3;

	/**
	 * Does not contribute to any goal
	 */
	LTestBo bo1;
	/**
	 * Contributes to goal1 by 0.0
	 */
	LTestBo bo2;
	/**
	 * Contributes to goal1 by 1.0 and goal2 by 0.0
	 */
	LTestBo bo3;

	LaraDecisionConfiguration dBuilder;

	LaraBOCollector<LTestAgent, LTestBo> scanner;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel();

		Class<? extends LaraPreference> goal1 = new LaraPreference() {
		}.getClass();
		Class<? extends LaraPreference> goal2 = new LaraPreference() {
		}.getClass();

		agent = new LTestAgent("LTestAgent");
		Map<Class<? extends LaraPreference>, Double> utilities = new HashMap<Class<? extends LaraPreference>, Double>();
		bo1 = new LTestBo(agent, utilities);
		utilities.put(goal1, 0.0);
		bo2 = new LTestBo(agent, utilities);
		utilities.put(goal1, 1.0);
		utilities.put(goal2, 0.0);
		bo3 = new LTestBo(agent, utilities);

		memory = new LDefaultLimitedCapacityBOMemory<LTestBo>(
				LCapacityManagers.<LTestBo> makeNINO());
		agent.getLaraComp().setBOMemory(memory);

		dBuilder = new LDecisionConfiguration("TestDecision");
		LDefaultAgentComp.setDefaultDeliberativeChoiceComp(dBuilder,
				LDeliberativeChoiceComp_MaxLineTotalRandomAtTie
						.getInstance(null));
		List<Class<? extends LaraPreference>> goals = new ArrayList<Class<? extends LaraPreference>>();
		goals.add(goal1);
		goals.add(goal2);
		dBuilder.setPreferences(goals);

		scanner = new LContributingBoCollector<LTestAgent, LTestBo>();
		LEventbus.getInstance(agent).subscribe(scanner,
				LPpBoCollectorEvent.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LContributingBoCollector#collectBOs(LaraAgent, LaraBOMemory, LaraDecisionConfiguration)}
	 */
	@Test
	public final void testGetBOs() {

		assertEquals(
				"No BO in memory contributes to the decision's preferenceWeights "
						+ "(since there is no bo inserted)", 0,
				getNumOfSelectedBos());
		memory.memorize(bo1);
		assertEquals(
				"No BO in memory contributes to the decision's preferenceWeights "
						+ "(since bo1 is inserted but has no utility for any goal)",
				0, getNumOfSelectedBos());
		memory.memorize(bo2);
		assertEquals(
				"1 BO in memory contributes to the decision's preferenceWeights "
						+ "(since bo2 is inserted and has utility 1.0 for goal1)",
				1, getNumOfSelectedBos());
		memory.clear();
		memory.memorize(bo2);
		memory.memorize(bo3);
		assertEquals(
				"2 BO in memory contribute to the decision's preferenceWeights "
						+ "(since bo2 with utility 0.0 for goal1 and bo3 with 1.0 for "
						+ "goal1 and 0.0 for goal2 are inserted)", 2,
				getNumOfSelectedBos());
	}

	protected int getNumOfSelectedBos() {
		agent.getLaraComp().getDecisionData(dBuilder)
				.setBos(new HashSet<LTestBo>());
		// LPpBoCollectorEvent requires LPpModeSelectorEvent!
		LEventbus.getInstance(agent).publish(
				new LPpModeSelectorEvent(agent, dBuilder));
		LEventbus.getInstance(agent).publish(
				new LPpBoCollectorEvent(agent, dBuilder));
		return agent.getLaraComp().getDecisionData(dBuilder).getBos().size();
	}
}
