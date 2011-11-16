/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 30.09.2010
 */
package de.cesr.lara.testing;


import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;


/**
 * 
 */
public class TestUtils {

	/**
	 * test agent
	 */
	public static class TestAgent extends LAbstractAgent<TestAgent, LGeneralBehaviouralOption<TestAgent>> {

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
			// TODO Auto-generated method stub
			
		}
	}
}
