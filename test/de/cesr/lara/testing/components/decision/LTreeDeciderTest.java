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

import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDeciderFactory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LAbstractBinaryDecisionLeave;
import de.cesr.lara.components.decision.impl.LAbstractBinaryDecsionTree;
import de.cesr.lara.components.decision.impl.LTreeDecider;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.impl.LAbstractPpComp;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.components.util.impl.LPrefEntry;
import de.cesr.lara.components.util.impl.LSet;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;
import de.cesr.lara.testing.LTestUtils.LTestDecisionConfig;
import de.cesr.lara.testing.LTestUtils.LTestPreference1;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LTreeDeciderTest {

	static class LTreeDeciderModeSelector extends
			LAbstractPpComp<LTestAgent, LTestBo> implements
			LaraDecisionModeSelector<LTestAgent, LTestBo> {

		@Override
		public void onInternalEvent(LaraEvent e) {
			final LPpModeSelectorEvent event = castEvent(
					LPpModeSelectorEvent.class, e);
			final LTestAgent agent = (LTestAgent) event.getAgent();

			LaraDeciderFactory<LTestAgent, LTestBo> factory = new LaraDeciderFactory<LTestAgent, LTestBo>() {

				@Override
				public LaraDecider<LTestBo> getDecider(LTestAgent agent,
						LaraDecisionConfiguration dConfiguration) {
					return new LTreeDecider<LTestAgent, LTestBo, LaraDecisionConfiguration>(
							new LAbstractBinaryDecsionTree<LTestAgent, LTestBo, LaraDecisionConfiguration>(
									new LAbstractBinaryDecisionLeave<LTestAgent, LTestBo, LaraDecisionConfiguration>() {

										@Override
										public Set<LTestBo> getBos(
												LTestAgent agent,
												LaraDecisionConfiguration parameter) {
											return new LSet<LTestBo>(
													new LTestBo(
															"Bo1",
															agent,
															new LPrefEntry(
																	LTestPreference1.class,
																	new Double(
																			1.0))));
										}

									},
									new LAbstractBinaryDecisionLeave<LTestAgent, LTestBo, LaraDecisionConfiguration>() {

										@Override
										public Set<LTestBo> getBos(
												LTestAgent agent,
												LaraDecisionConfiguration parameter) {
											return new LSet<LTestBo>(
													new LTestBo(
															"Bo2",
															agent,
															new LPrefEntry(
																	LTestPreference1.class,
																	new Double(
																			0.5))));
										}

									}) {

								@Override
								protected boolean evaluate(LTestAgent agent,
										LaraDecisionConfiguration parameter) {
									return LTreeDeciderTest.getState();
								}
							}, agent, event.getdConfig());
				}
			};
			agent.getLaraComp().getDecisionData(event.getdConfig())
					.setDeciderFactory(factory);
		}

	}

	static private boolean state = false;

	static boolean getState() {
		return state;
	}
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

		LPreprocessorConfigurator<LTestAgent, LTestBo> ppConfig = LPreprocessorConfigurator
				.<LTestAgent, LTestBo> getNewPreprocessorConfigurator();
		ppConfig.setDecisionModeSelector(new LTreeDeciderModeSelector());
		agent.getLaraComp().setPreprocessor(ppConfig.getPreprocessor());

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
		LEventbus.getInstance().subscribe(agent, LAgentDecideEvent.class);

		state = true;
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
		LEventbus.getInstance().publish(new LAgentDecideEvent(dConfig));
		assertEquals(LTreeDecider.class,
				agent.getLaraComp().getDecisionData(dConfig).getDecider()
						.getClass());
		assertEquals(one, agent.getLaraComp().getDecisionData(dConfig)
				.getDecider().getSelectedBo());

		state = false;
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
		LEventbus.getInstance().publish(new LAgentDecideEvent(dConfig));
		assertEquals(LTreeDecider.class,
				agent.getLaraComp().getDecisionData(dConfig).getDecider()
						.getClass());
		assertEquals(two, agent.getLaraComp().getDecisionData(dConfig)
				.getDecider().getSelectedBo());

		state = true;
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
		LEventbus.getInstance().publish(new LAgentDecideEvent(dConfig));
		assertEquals(LTreeDecider.class,
				agent.getLaraComp().getDecisionData(dConfig).getDecider()
						.getClass());
		assertEquals(one, agent.getLaraComp().getDecisionData(dConfig)
				.getDecider().getSelectedBo());
	}
}
