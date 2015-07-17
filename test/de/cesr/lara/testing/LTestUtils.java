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
import de.cesr.lara.components.eventbus.events.LModelInstantiatedEvent;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.events.LModelStepFinishedEvent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LAbstractModel;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraPreferenceRegistry;
import de.cesr.lara.components.util.impl.LPrefEntry;


/**
 * @author Sascha Holzhauer
 * 
 */
public class LTestUtils {

	static LaraPreferenceRegistry preg;

	static {
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
		 * @param name
		 */
		public LTestAgent(String name) {
			super(LModel.getModel(), new LEnvironment(), name);
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

		public LTestBo(LTestAgent agent, Map<LaraPreference, Double> utilities) {
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

		public LTestBo(String key, LTestAgent agent,
				Map<LaraPreference, Double> utilities) {
			super(key, agent, utilities);
		}

		@Override
		public LTestBo getModifiedBO(LTestAgent agent,
				Map<LaraPreference, Double> preferenceUtilities) {
			return new LTestBo(this.getKey(), agent, preferenceUtilities);
		}

		@Override
		public Map<LaraPreference, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return this.getValue();
		}
	};

	/**
	 * Inits a {@link LaraModel} as test model.
	 * 
	 * @param id
	 */
	public static void initBareTestModel(Object id) {
		LModel.reset();
		LaraModel model = new LAbstractStandaloneSynchronisedModel(id) {
			@Override
			public void onEvent(LaraEvent event) {
			}

			@Override
			public String toString() {
				return "TestModel";
			}
		};
		((LAbstractModel) model).init();

		preg = LModel.getModel(id).getPrefRegistry();
		preg.register("LTestPreference1");
		preg.register("LTestPreference2");

		Collection<LaraPreference> prefs = new HashSet<LaraPreference>();
		prefs.add(preg.get("LTestPreference1"));
		prefs.add(preg.get("LTestPreference2"));
	}

	/**
	 * Inits a {@link LaraModel} as test model.
	 * 
	 * @param dConfiguration
	 *            decision configuration
	 */
	public static void initTestModel(
			final LaraDecisionConfiguration dConfiguration) {
		LModel.reset();
		LaraModel model = new LAbstractStandaloneSynchronisedModel(null) {
			@Override
			public void onEvent(LaraEvent event) {
				if (event instanceof LModelStepEvent) {
					// perceive
					eventBus.publish(new LAgentPerceptionEvent(dConfiguration));
					// preprocess
					eventBus.publish(new LAgentPreprocessEvent(dConfiguration));
					// decide
					eventBus.publish(new LAgentDecideEvent(dConfiguration));
					// postprocess
					eventBus.publish(new LAgentPostprocessEvent(dConfiguration));
					// execute
					eventBus.publish(new LAgentExecutionEvent(dConfiguration));
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

		preg = LModel.getModel().getPrefRegistry();
		preg.register("LTestPreference1");
		preg.register("LTestPreference2");

		model.getLEventbus().publish(new LModelInstantiatedEvent());
	}

	public static LaraDecisionConfiguration initTestModel() {
		LaraDecisionConfiguration dConfig = new LDecisionConfiguration();
		LTestUtils.initTestModel(dConfig);
		LTestUtils.initDConfig(dConfig);
		return dConfig;
	}

	/**
	 * Intended for applications when a specific
	 * {@link LaraDecisionConfiguration} is required. This needs to be
	 * instantiated before and initialised (set preferences) thereafter.
	 * 
	 * @param dConfig
	 */
	public static void initDConfig(LaraDecisionConfiguration dConfig) {
		Collection<LaraPreference> prefs = new HashSet<LaraPreference>();
		prefs.add(preg.get("LTestPreference1"));
		prefs.add(preg.get("LTestPreference2"));
		dConfig.setPreferences(prefs);
	}
}
