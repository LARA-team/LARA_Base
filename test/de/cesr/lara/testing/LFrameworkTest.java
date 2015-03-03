package de.cesr.lara.testing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.agents.impl.LDefaultAgentComp;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPerceptionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LInternalModelInitializedEvent;
import de.cesr.lara.components.eventbus.events.LModelFinishEvent;
import de.cesr.lara.components.eventbus.events.LModelInstantiatedEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LUpdateEnvironmentEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.impl.LPreferenceWeightMap;

public class LFrameworkTest {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(LFrameworkTest.FrameworkTestModel.class);

	private class FrameworkTestModel extends
			LAbstractStandaloneSynchronisedModel implements LaraModel,
			LaraEventSubscriber {

		private LaraEnvironment environment;

		private LaraDecisionConfiguration testDecisionConfiguration;

		public FrameworkTestModel() {
			super();
			eventBus.subscribe(this, LInternalModelInitializedEvent.class);
			eventBus.subscribe(this, LModelStepEvent.class);
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			if (event instanceof LInternalModelInitializedEvent) {
				environment = new LEnvironment();
				environment.updateProperty(new LEnvironmentalIntProperty(
						KEY_TESTPROPERTY, 0, environment));
				Set<LaraPreference> goals = new HashSet<LaraPreference>();
				goals.add(LModel.getModel().getPrefRegistry().get("TestGoal"));
				testDecisionConfiguration = new LDecisionConfiguration(
						"test decision");
				testDecisionConfiguration.setPreferences(goals);
				new TestAgent("TestAgent 1", environment);
			} else if (event instanceof LModelStepEvent) {
				logger.info("step");
				eventBus.publish(new LUpdateEnvironmentEvent());
				eventBus.publish(new LAgentPerceptionEvent(
						testDecisionConfiguration));
				eventBus.publish(new LAgentPreprocessEvent(
						testDecisionConfiguration));
				eventBus.publish(new LAgentDecideEvent(
						testDecisionConfiguration));
				eventBus.publish(new LAgentPostprocessEvent(
						testDecisionConfiguration));
				eventBus.publish(new LAgentExecutionEvent(
						testDecisionConfiguration));
				eventBus.publish(new LAgentPostExecutionEvent(
						testDecisionConfiguration));
			}
		}
	}

	private class TestAgent implements
			LaraAgent<TestAgent, TestBehaviouralOption<TestAgent>> {

		private LaraAgentComponent<TestAgent, TestBehaviouralOption<TestAgent>> laraAgentComponent;
		private int perceivedEnvironmentalProperty;
		private String agentId;

		public TestAgent(String agentId, LaraEnvironment environment) {
			this.agentId = agentId;
			LEventbus eventBus = LEventbus.getInstance();
			laraAgentComponent = new LDefaultAgentComp<TestAgent, TestBehaviouralOption<TestAgent>>(
					this, environment);
			Set<TestBehaviouralOption<TestAgent>> behaviouralOptions = new HashSet<TestBehaviouralOption<TestAgent>>();
			behaviouralOptions.add(new TestBehaviouralOption(
					"TestBehaviouralOption", this, new LPreferenceWeightMap()));
			getLaraComp().getBOMemory().memoriseAll(behaviouralOptions);
			eventBus.subscribe(this, LAgentPerceptionEvent.class);
			eventBus.subscribe(this, LAgentPreprocessEvent.class);
			eventBus.subscribe(this, LAgentDecideEvent.class);
			eventBus.subscribe(this, LAgentPostprocessEvent.class);
			eventBus.subscribe(this, LAgentExecutionEvent.class);
		}

		@Override
		public String getAgentId() {
			return agentId;
		}

		@Override
		public LaraAgentComponent<TestAgent, TestBehaviouralOption<TestAgent>> getLaraComp() {
			return laraAgentComponent;
		}

		public int getPerceivedEnvironmentalProperty() {
			return perceivedEnvironmentalProperty;
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			if (event instanceof LAgentPerceptionEvent) {
				perceivedEnvironmentalProperty = ((LEnvironmentalIntProperty) getLaraComp()
						.getEnvironment().getPropertyByName(KEY_TESTPROPERTY))
						.getIntValue();
			} else if (event instanceof LAgentPreprocessEvent) {
			} else if (event instanceof LAgentDecideEvent) {
				TestBehaviouralOption<TestAgent> currentBestSituationalBO = ((LaraDecider<TestBehaviouralOption<TestAgent>>) getLaraComp()
						.getDecisionData(
								((LAgentDecideEvent) event)
										.getDecisionConfiguration())
						.getDecider()).getSelectedBo();
				System.out.println(currentBestSituationalBO);
			} else if (event instanceof LAgentPostprocessEvent) {
			} else if (event instanceof LAgentExecutionEvent) {

			}
		}

	}

	private class TestBehaviouralOption<A extends LaraAgent<? super A, ?>>
			extends LaraBehaviouralOption<A, TestBehaviouralOption<A>> {
		public TestBehaviouralOption(String key, A agent,
				Map<LaraPreference, Double> preferences) {
			super(key, agent, preferences);
		}

		@Override
		public TestBehaviouralOption<A> getModifiedBO(A agent,
				Map<LaraPreference, Double> preferenceUtilities) {
			return new TestBehaviouralOption(this.getKey(), agent,
					preferenceUtilities);
		}

		@Override
		public Map<LaraPreference, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			Map<LaraPreference, Double> utilities = new HashMap<LaraPreference, Double>();
			utilities.put(LModel.getModel().getPrefRegistry().get("TestGoal"),
					((TestAgent) getAgent())
					.getPerceivedEnvironmentalProperty() + 1d);
			return utilities;
		}
	}


	private static final String KEY_TESTPROPERTY = "TestProperty";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LFrameworkTest();
	}

	public LFrameworkTest() {
		new FrameworkTestModel();

		LModel.getModel().getPrefRegistry().register("TestGoal");

		LEventbus eventBus = LEventbus.getInstance();
		eventBus.publish(new LModelInstantiatedEvent());
		for (int step = 1; step <= 3; step++) {
			System.out.println("step: " + step);
			eventBus.publish(new LModelStepEvent());
		}
		eventBus.publish(new LModelFinishEvent());
	}

}