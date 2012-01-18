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
package de.cesr.lara.testing;


import java.util.HashMap;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LAbstractModel;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.impl.LRandomService;


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

	/**
	 * Test behavioural option for TestAgent
	 * 
	 * @author Sascha Holzhauer
	 * 
	 */
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

	/**
	 * Inits a {@link LaraModel} as test model.
	 */
	public static void initTestModel() {
		LModel.setNewModel(new LAbstractStandaloneSynchronisedModel() {

			@Override
			public LaraRandom getLRandom() {
				return new LRandomService((int) System.currentTimeMillis());
			}

			@Override
			public void onInternalEvent(LaraEvent event) {
			}
		});
		((LAbstractModel) LModel.getModel()).init();
	}
}