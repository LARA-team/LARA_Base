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

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.preprocessor.impl.LAbstractPpComp;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;

/**
 * 
 */
public class LPpEventBusTest {
	
	static protected int timeModeSelector = 0;
	static protected int timeBOCollector = 0;
	static protected int timeBoPreselector = 0;
	static protected int timeBOUtilityUpdater = 0;
	static protected int timePreferenceUpdater = 0;

	static protected Integer counter = new Integer(0);
	
	protected LTestAgent agent1, agent2, agent3;

	protected LaraDecisionConfiguration dConfig = new LDecisionConfiguration();

	// PP components that allow monitoring:
	static class LTestDecisionModeSelector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO>
		implements LaraDecisionModeSelector<A, BO> {

		@Override
		public void onInternalEvent(LaraEvent e) {
			LPpModeSelectorEvent event = castEvent(LPpModeSelectorEvent.class,
					e);
			LPpEventBusTest.timeModeSelector = counter++;
			@SuppressWarnings("unchecked")
			// the event will only be published by agents of type A
			A agent = (A) event.getAgent();
			LEventbus eBus = LEventbus.getInstance(agent);
			if (agent.getAgentId().equals("Test-Agent01")) {

				eBus.publish(new LPpBoCollectorEvent(event.getAgent(), event
						.getdConfig()));
				eBus.publish(new LPpBoPreselectorEvent(event.getAgent(), event
						.getdConfig()));
				eBus.publish(new LPpBoUtilityUpdaterEvent(event.getAgent(),
						event.getdConfig()));
				eBus.publish(new LPpPreferenceUpdaterEvent(event.getAgent(),
						event.getdConfig()));
			} else {
				eBus.publish(new LPpBoCollectorEvent(event.getAgent(), event
						.getdConfig()));
				eBus.publish(new LPpBoPreselectorEvent(event.getAgent(), event
						.getdConfig()));
				eBus.publish(new LPpBoUtilityUpdaterEvent(event.getAgent(),
						event.getdConfig()));
			}
		}
	}
	
	static class LTestBoCollector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractPpComp<A, BO> implements LaraBOCollector<A, BO> {

		@Override
		public void onInternalEvent(LaraEvent e) {
			LPpEventBusTest.timeBOCollector = counter++;
		}
	}
	
	static class LTestBoPreselector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractPpComp<A, BO> implements LaraBOPreselector<A, BO> {

		@Override
		public void onInternalEvent(LaraEvent event) {
			LPpEventBusTest.timeBoPreselector = counter++;
		}
	}
	
	static class LTestBoUtilityUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractPpComp<A, BO> implements
			LaraBOUtilityUpdater<A, BO> {

		@Override
		public void onInternalEvent(LaraEvent event) {
			LPpEventBusTest.timeBOUtilityUpdater = counter++;
		}
	}

	static class LTestPrefenceUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractPpComp<A, BO> implements
			LaraPreferenceUpdater<A, BO> {

		@Override
		public void onInternalEvent(LaraEvent event) {
			LPpEventBusTest.timePreferenceUpdater = counter++;
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LEventbus.resetAll();

		LTestUtils.initTestModel();

		// init two agents with different properties to trigger different PP behaviours
		agent1 = new LTestAgent("Test-Agent01");
		agent2 = new LTestAgent("Test-Agent02");
		
		LEventbus.getInstance().unsubscribe(agent1.getLaraComp());
		LEventbus.getInstance().unsubscribe(agent2.getLaraComp());

		// init preprocesor
		LaraPreprocessorConfigurator<LTestAgent, LTestBo> configurator1 = LPreprocessorConfigurator
				.getNewPreprocessorConfigurator();
		configurator1
				.setDecisionModeSelector(new LTestDecisionModeSelector<LTestAgent, LTestBo>());
		configurator1.setBOCollector(new LTestBoCollector<LTestAgent, LTestBo>());
		configurator1.setBoPreselector(new LTestBoPreselector<LTestAgent, LTestBo>());
		configurator1
				.setBOAdapter(new LTestBoUtilityUpdater<LTestAgent, LTestBo>());
		configurator1.setPreferenceUpdater(new LTestPrefenceUpdater<LTestAgent, LTestBo>());

		// configure PP-Factory
		LaraPreprocessor<LTestAgent, LTestBo> pp = configurator1
				.getPreprocessor();
		agent1.getLaraComp().setPreprocessor(pp);
		agent2.getLaraComp().setPreprocessor(pp);
	}

	@Test
	public void testAgent1() {
		// subscribe agents at global eventbus
		LEventbus.getInstance().subscribe(agent1.getLaraComp(),
				LAgentPreprocessEvent.class);

		// trigger pre-processing
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));
		
		// check for execution of PP components
		assertTrue(timeModeSelector < timeBOCollector);
		assertTrue(timeBOCollector < timeBoPreselector);
		assertTrue(timeBoPreselector + "<" + timeBOUtilityUpdater,
				timeBoPreselector < timeBOUtilityUpdater);
		assertTrue(timeBOUtilityUpdater < timePreferenceUpdater);
	}

	@Test
	public void testAgent2() {
		// subscribe agents at global eventbus
		LEventbus.getInstance().subscribe(agent2.getLaraComp(),
				LAgentPreprocessEvent.class);

		// trigger pre-processing
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));

		// check for execution of PP components
		assertTrue(timeModeSelector < timeBOCollector);
		assertTrue(timeBOCollector < timeBoPreselector);
		assertTrue(timeBoPreselector + "<" + timeBOUtilityUpdater,
				timeBoPreselector < timeBOUtilityUpdater);
		assertTrue(timePreferenceUpdater == 0);
	}

	/**
	 * 
	 */
	@Test
	public void testAgent1Bos() {
		agent3 = new LTestAgent("Test-Agent03");

		LEventbus.getInstance().unsubscribe(agent1.getLaraComp());
		LEventbus.getInstance().unsubscribe(agent2.getLaraComp());

		agent3.getLaraComp().setPreprocessor(
				LPreprocessorConfigurator
						.<LTestAgent, LTestBo> getNewPreprocessorConfigurator()
						.getPreprocessor());

		LTestBo testBo = new LTestBo(agent3);
		agent3.getLaraComp().getBOMemory().memorize(testBo);

		// subscribe agents at global eventbus
		LEventbus.getInstance().subscribe(agent3.getLaraComp(),
				LAgentPreprocessEvent.class);

		// trigger pre-processing
		LEventbus.getInstance().publish(new LAgentPreprocessEvent(dConfig));

		// check for execution of PP components
		assertTrue(agent3.getLaraComp().getDecisionData(dConfig).getBos()
				.contains(testBo));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		timeModeSelector = 0;
		timeBOCollector = 0;
		timeBoPreselector = 0;
		timeBOUtilityUpdater = 0;
		timePreferenceUpdater = 0;

		LEventbus.resetAll();
	}
	
}