package de.cesr.lara.testing.components.preprocessor;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraDecisionModeProvidingAgent;
import de.cesr.lara.components.agents.impl.LDefaultAgentComp;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.impl.LContributingBoCollector;
import de.cesr.lara.components.preprocessor.impl.LDefaultDecisionModeSelector;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;
import de.cesr.parma.core.PmParameterManager;

public class LDefaultDecisionModeSelectorTest {

	LTestAgent agent, delibAgent, habitAgent;
	LaraBOMemory<LTestBo> memory;

	LaraPreference goal1;

	/**
	 * Does not contribute to any goal
	 */
	LTestBo bo1, bo1D, bo1H;
	/**
	 * Contributes to goal1 by 0.0
	 */
	LTestBo bo2, bo2D, bo2H;


	LaraDecisionConfiguration dConfig = LTestUtils.dConfig;

	/**
	 * test agent
	 */
	public static class LDelegateDeliberativeTestAgent extends LTestAgent implements LaraDecisionModeProvidingAgent {

		public LDelegateDeliberativeTestAgent(String name) {
			super(name);
		}

		@Override
		public LaraDecisionModes getDecisionModeSuggestion() {
			return LaraDecisionModes.DELIBERATIVE;
		}
	};


	/**
	 * test agent
	 */
	public static class LDelegateHabitTestAgent extends LTestAgent implements LaraDecisionModeProvidingAgent {

		public LDelegateHabitTestAgent(String name) {
			super(name);
		}

		@Override
		public LaraDecisionModes getDecisionModeSuggestion() {
			return LaraDecisionModes.DELIBERATIVE;
		}
	};

	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel(dConfig);

		Class<? extends LaraPreference> goal1 = new LaraPreference() {
		}.getClass();

		agent = new LTestAgent("LTestAgent");
		delibAgent = new LDelegateDeliberativeTestAgent("DeliberativeAgent");
		habitAgent = new LDelegateHabitTestAgent("HabitAgent");

		// Adjust preprocessor:
		LaraPreprocessorConfigurator<LTestAgent, LTestBo> ppConfigurator =
				LPreprocessorConfigurator.<LTestAgent, LTestBo> getNewPreprocessorConfigurator();

		ppConfigurator.setBOCollector(new LContributingBoCollector<LTestAgent, LTestBo>(), dConfig);
		ppConfigurator.setDecisionModeSelector(new LDefaultDecisionModeSelector<LTestAgent, LTestBo>(), dConfig);

		agent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());
		delibAgent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());
		habitAgent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());

		Map<Class<? extends LaraPreference>, Double> utilities = new HashMap<Class<? extends LaraPreference>, Double>();
		bo1 = new LTestBo(agent, utilities);
		bo1D = new LTestBo(delibAgent, utilities);
		bo1H = new LTestBo(habitAgent, utilities);

		utilities.put(goal1, 0.0);
		bo2 = new LTestBo(agent, utilities);
		bo2D = new LTestBo(delibAgent, utilities);
		bo2H = new LTestBo(habitAgent, utilities);
		utilities.put(goal1, 1.0);

		memory = new LDefaultLimitedCapacityBOMemory<LTestBo>(LCapacityManagers.<LTestBo> makeNINO());
		agent.getLaraComp().setBOMemory(memory);
		delibAgent.getLaraComp().setBOMemory(memory);
		habitAgent.getLaraComp().setBOMemory(memory);

		memory.memorize(bo1);
		memory.memorize(bo2);

		dConfig = new LDecisionConfiguration("TestDecision");
		LDefaultAgentComp.setDefaultDeliberativeChoiceComp(dConfig,
				LDeliberativeChoiceComp_MaxLineTotalRandomAtTie.getInstance(null));
		List<Class<? extends LaraPreference>> goals = new ArrayList<Class<? extends LaraPreference>>();
		goals.add(goal1);
		dConfig.setPreferences(goals);

		PmParameterManager.setParameter(LDecisionMakingPa.HABIT_THRESHOLD, new Integer(2));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testDecisionModeSelection() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(agent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
			LaraDecisionModes.DELIBERATIVE);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(agent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
				LaraDecisionModes.HABIT);
	}

	@Test
	public final void testDecisionModeSelectionDelegateDeliberative() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(delibAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
				LaraDecisionModes.DELIBERATIVE);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(delibAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
				LaraDecisionModes.DELIBERATIVE);
	}

	@Test
	public final void testDecisionModeSelectionDelegateHabit() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(habitAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
				LaraDecisionModes.DELIBERATIVE);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(habitAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode(),
				LaraDecisionModes.HABIT);
	}
}
