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

import java.util.Collection;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeDecider;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotal;
import de.cesr.lara.components.decision.impl.LDeliberativeDecider;
import de.cesr.lara.components.util.impl.LPrefEntry;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;
import de.cesr.lara.testing.LTestUtils.LTestPreference1;
import de.cesr.lara.testing.LTestUtils.LTestPreference2;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LDeliberativeDeciderTest {

	LTestAgent agent;
	LaraDecisionConfiguration dConfig;

	LTestBo bo1, bo2;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		LTestUtils.initTestModel();
		agent = new LTestAgent("TestAgent");

		agent.getLaraComp().addPreferenceWeights(
				new LPrefEntry(LTestPreference1.class, new Double(1.0)),
				new LPrefEntry(LTestPreference2.class, new Double(0.1)));

		Collection<Class<? extends LaraPreference>> prefs = new HashSet<Class<? extends LaraPreference>>();
		prefs.add(LTestPreference1.class);
		prefs.add(LTestPreference2.class);

		dConfig = new LDecisionConfiguration();
		dConfig.setPreferences(prefs);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		agent = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeDecider#getSelectedBO()}
	 * .
	 */
	@Test
	public void testGetSelectedBO() {
		bo1 = new LTestBo("TestBo1", agent, new LPrefEntry(
				LTestPreference1.class, new Double(1.5)), new LPrefEntry(
				LTestPreference2.class, new Double(1.5)));

		// flip order of preferences (if first preference is treated as second,
		// the test below fails)
		bo2 = new LTestBo("TestBo2", agent, new LPrefEntry(
				LTestPreference2.class, new Double(1.0)), new LPrefEntry(
				LTestPreference1.class, new Double(2.0)));

		agent.getLaraComp().getDecisionData(dConfig).setBos(bo1, bo2);

		LaraDeliberativeDecider<LTestBo> decider = new LDeliberativeDecider<LTestBo>(
				dConfig);
		decider.setSelectableBos(agent.getLaraComp().getDecisionData(dConfig)
				.getBos());
		decider.setDeliberativeChoiceComponent(LDeliberativeChoiceComp_MaxLineTotal
				.getInstance());
		decider.setPreferenceWeights(agent.getLaraComp().getPreferenceWeights());

		decider.decide();

		assertEquals(bo2.getKey(), decider.getSelectedBo().getKey());

	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeDecider#getSelectedBO()}
	 * . Checks if preferences are considered.
	 */
	@Test
	public void testGetSelectedBoAgentPreferences() {
		// bo1 wins only when agent preferences (high for pref 1) are considered
		bo1 = new LTestBo("TestBo1", agent, new LPrefEntry(
				LTestPreference1.class, new Double(1.7)), new LPrefEntry(
				LTestPreference2.class, new Double(1.0)));

		bo2 = new LTestBo("TestBo2", agent, new LPrefEntry(
				LTestPreference1.class, new Double(1.5)), new LPrefEntry(
				LTestPreference2.class, new Double(2.0)));

		agent.getLaraComp().getDecisionData(dConfig).setBos(bo1, bo2);

		LaraDeliberativeDecider<LTestBo> decider = new LDeliberativeDecider<LTestBo>(
				dConfig);
		decider.setSelectableBos(agent.getLaraComp().getDecisionData(dConfig)
				.getBos());
		decider.setDeliberativeChoiceComponent(LDeliberativeChoiceComp_MaxLineTotal
				.getInstance());

		agent.getLaraComp().addPreferenceWeights(
				new LPrefEntry(LTestPreference1.class, new Double(1.0)),
				new LPrefEntry(LTestPreference2.class, new Double(1.0)));
		decider.setPreferenceWeights(agent.getLaraComp().getPreferenceWeights());
		decider.decide();
		assertEquals(bo2.getKey(), decider.getSelectedBo().getKey());

		agent.getLaraComp().addPreferenceWeights(
				new LPrefEntry(LTestPreference1.class, new Double(1.0)),
				new LPrefEntry(LTestPreference2.class, new Double(0.1)));
		decider.setPreferenceWeights(agent.getLaraComp().getPreferenceWeights());
		decider.decide();
		assertEquals(bo1.getKey(), decider.getSelectedBo().getKey());
	}
}
