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


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.eventbus.events.LAgentDecideEvent;
import de.cesr.lara.components.eventbus.events.LAgentExecutionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPerceptionEvent;
import de.cesr.lara.components.eventbus.events.LAgentPostprocessEvent;
import de.cesr.lara.components.eventbus.events.LAgentPreprocessEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LModelStepFinishedEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LAbstractModel;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.impl.LPrefEntry;


/**
 * @author Sascha Holzhauer
 * 
 */
public class LTestUtils {

	static public LaraDecisionConfiguration dConfig = new LTestDecisionConfig();

	static {
		Collection<Class<? extends LaraPreference>> prefs = new HashSet<Class<? extends LaraPreference>>();
		prefs.add(LTestPreference1.class);
		prefs.add(LTestPreference2.class);
		dConfig.setPreferences(prefs);
	}

	/**
	 * test agent
	 */
	public static class LTestAgent extends LAbstractAgent<LTestAgent, LTestBo> {

		static int counter = 0;

		int id;

		/**
		 * constructor
		 * 
		 * @param env
		 * @param name
		 */
		public LTestAgent(String name) {
			super(new LEnvironment(), name);
			this.id = counter++;
		}

		@Override
		public LTestAgent getThis() {
			return this;
		}

		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}
	};

	/**
	 * Test behavioural option for LTestAgent
	 * 
	 * @author Sascha Holzhauer
	 * 
	 */
	public static class LTestBo extends LaraBehaviouralOption<LTestAgent, LTestBo> {

		static int counter = 0;

		public LTestBo(LTestAgent agent) {
			super("LTestBo" + LModel.getModel().getIntegerFormat().format(counter++), agent);
		}

		public LTestBo(LTestAgent agent, Map<Class<? extends LaraPreference>, Double> utilities) {
			super("LTestBo" + LModel.getModel().getIntegerFormat().format(counter++), agent, utilities);
		}

		/**
		 * @param key
		 * @param agent
		 * @param prefEntry
		 */
		public LTestBo(String key, LTestAgent agent, LPrefEntry... prefEntry) {
			super(key, agent, prefEntry);
		}

		public LTestBo(String key, LTestAgent agent, Map<Class<? extends LaraPreference>, Double> utilities) {
			super(key, agent, utilities);
		}

		@Override
		public LTestBo getModifiedBO(LTestAgent agent, Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
			return new LTestBo(this.getKey(), agent, preferenceUtilities);
		}

		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(LaraDecisionConfiguration dBuilder) {
			return this.getValue();
		}
	};

	static public class LTestDecisionConfig extends LDecisionConfiguration {
		public LTestDecisionConfig() {
			Collection<Class<? extends LaraPreference>> prefs = new HashSet<Class<? extends LaraPreference>>();
			prefs.add(LTestPreference1.class);
			prefs.add(LTestPreference2.class);

			this.setPreferences(prefs);
		}
	}

	static public class LTestPreference1 implements LaraPreference {
	}

	static public class LTestPreference2 implements LaraPreference {
	}

	/**
	 * Inits a {@link LaraModel} as test model.
	 */
	public static void initTestModel(final LaraDecisionConfiguration dConfig) {
		LaraModel model = new LAbstractStandaloneSynchronisedModel() {
			@Override
			public void onEvent(LaraEvent event) {
				if (event instanceof LModelStepEvent) {
					// perceive
					eventBus.publish(new LAgentPerceptionEvent(dConfig));
					// preprocess
					eventBus.publish(new LAgentPreprocessEvent(dConfig));
					// decide
					eventBus.publish(new LAgentDecideEvent(dConfig));
					// postprocess
					eventBus.publish(new LAgentPostprocessEvent(dConfig));
					// execute
					eventBus.publish(new LAgentExecutionEvent(dConfig));
					// tell output to output
					eventBus.publish(new LModelStepFinishedEvent());
				}
			}

			@Override
			public String toString() {
				return "TestModel";
			}
		};
		((LAbstractModel) model).init();
	}
}
