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
import de.cesr.lara.components.decision.LaraDecisionMode;
import de.cesr.lara.components.decision.LaraDecisionModes;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.param.LBasicPa;
import de.cesr.lara.components.param.LDecisionMakingPa;
import de.cesr.lara.components.postprocessor.impl.LSelectedBoProperty;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.impl.LContributingBoCollector;
import de.cesr.lara.components.preprocessor.impl.LDefaultDecisionModeSelector;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;
import de.cesr.parma.core.PmParameterManager;


public class LDefaultDecisionModeSelectorTest implements LaraEventSubscriber {

	LTestAgent agent, delibAgent, habitAgent, initialHabitAgent;
	LaraBOMemory<LTestBo> memory, iHMemory;

	LaraPreference goal1;

	LaraDecisionMode decisionModeLaraTestAgent;
	LaraDecisionMode decisionModeHabit;
	LaraDecisionMode decisionModeDeliberative;
	LaraDecisionMode decisionModeInitialHabit;

	/**
	 * Does not contribute to any goal
	 */
	LTestBo bo1, bo1D, bo1H, bo1IH;
	/**
	 * Contributes to goal1 by 0.0
	 */
	LTestBo bo2, bo2D, bo2H;


	LaraDecisionConfiguration dConfig = new LDecisionConfiguration("TestDecision");

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
			return LaraDecisionModes.HABIT;
		}
	};

	@Before
	public void setUp() throws Exception {
		PmParameterManager.setParameter(LBasicPa.EVENTBUS_FORCE_SEQUENTIAL, true);
		LTestUtils.initTestModel(dConfig);
		LEventbus.getInstance().subscribe(this, LAgentPostprocessEvent.class);

		LaraPreference goal1 = LModel.getModel().getPrefRegistry()
				.register("TestPreference1");

		agent = new LTestAgent("LTestAgent");
		delibAgent = new LDelegateDeliberativeTestAgent("DeliberativeAgent");
		habitAgent = new LDelegateHabitTestAgent("HabitAgent");
		initialHabitAgent = new LTestAgent("IntialHabitAgent");

		// Adjust preprocessor:
		LaraPreprocessorConfigurator<LTestAgent, LTestBo> ppConfigurator =
				LPreprocessorConfigurator.<LTestAgent, LTestBo> getNewPreprocessorConfigurator();


		ppConfigurator.setBOCollector(new LContributingBoCollector<LTestAgent, LTestBo>(), dConfig);
		ppConfigurator.setDecisionModeSelector(new LDefaultDecisionModeSelector<LTestAgent, LTestBo>(), dConfig);

		agent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());
		delibAgent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());
		habitAgent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());
		initialHabitAgent.getLaraComp().setPreprocessor(ppConfigurator.getPreprocessor());

		Map<LaraPreference, Double> utilities = new HashMap<LaraPreference, Double>();
		bo1 = new LTestBo(agent, utilities);
		bo1D = new LTestBo(delibAgent, utilities);
		bo1H = new LTestBo(habitAgent, utilities);
		bo1IH = new LTestBo(initialHabitAgent, utilities);
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

		iHMemory = new LDefaultLimitedCapacityBOMemory<LTestBo>(LCapacityManagers.<LTestBo> makeNINO());
		iHMemory.memorize(bo1IH);
		iHMemory.memorize(bo2);
		initialHabitAgent.getLaraComp().setBOMemory(iHMemory);

		LDefaultAgentComp.setDefaultDeliberativeChoiceComp(dConfig,
				LDeliberativeChoiceComp_MaxLineTotalRandomAtTie.getInstance(null));
		List<LaraPreference> goals = new ArrayList<LaraPreference>();
		goals.add(goal1);
		dConfig.setPreferences(goals);

		PmParameterManager.setParameter(LDecisionMakingPa.HABIT_THRESHOLD, new Integer(1));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testDecisionModeSelection() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.DELIBERATIVE, this.decisionModeLaraTestAgent);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.HABIT, this.decisionModeLaraTestAgent);
	}

	@Test
	public final void testDecisionModeSelectionDelegateDeliberative() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.DELIBERATIVE, this.decisionModeDeliberative);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.DELIBERATIVE, this.decisionModeDeliberative);
	}

	@Test
	public final void testDecisionModeSelectionDelegateHabit() {
		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.DELIBERATIVE, this.decisionModeHabit);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.HABIT, this.decisionModeHabit);
	}

	@Test
	public final void testInitialHabit() {
		initialHabitAgent
				.getLaraComp()
				.getGeneralMemory()
				.memorize(new LSelectedBoProperty<LTestBo>(dConfig, bo1),
						(Integer) PmParameterManager.getParameter(LDecisionMakingPa.HABIT_THRESHOLD) + 1);

		LEventbus.getInstance().publish(new LModelStepEvent());
		assertEquals(LaraDecisionModes.HABIT, this.decisionModeInitialHabit);
	}

	@Override
	public <T extends LaraEvent> void onEvent(T event) {
		if (event instanceof LAgentPostprocessEvent) {
			this.decisionModeLaraTestAgent =
					agent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode();
			this.decisionModeHabit = habitAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode();
			this.decisionModeDeliberative =
					delibAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode();

			this.decisionModeInitialHabit =
					initialHabitAgent.getLaraComp().getDecisionData(dConfig).getDecider().getDecisionMode();
		}
	}
}
