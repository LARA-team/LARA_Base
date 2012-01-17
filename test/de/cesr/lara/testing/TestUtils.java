/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 30.09.2010
 */
package de.cesr.lara.testing;


import java.util.HashMap;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.impl.LModel;


/**
 * 
 */
public class TestUtils {

	/**
	 * test agent
	 */
	public static class TestAgent extends LAbstractAgent<TestAgent, TestBo> {

		/**
		 * constructor
		 * 
		 * @param env
		 * @param name
		 */
		public TestAgent(String name) {
			super(new LEnvironment(), name);
		}

		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public TestAgent getThis() {
			return this;
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}
	}

	public static class TestBo extends LaraBehaviouralOption<TestAgent, TestBo> {

		static int counter = 0;
		public TestBo(TestAgent agent) {
			super("TestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent);
		}

		public TestBo(TestAgent agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super("TestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent, utilities);
		}

		public TestBo(String key, TestAgent agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super(key, agent, utilities);
		}

		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return new HashMap<Class<? extends LaraPreference>, Double>();
		}

		@Override
		public TestBo getModifiedBO(TestAgent agent,
				Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
			return new TestBo(this.getKey(), agent, preferenceUtilities);
		}
	}
}
