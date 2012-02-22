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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.impl.LAbstractAgent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.environment.impl.LEnvironment;
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

	static public class LTestPreference1 implements LaraPreference {
	};

	static public class LTestPreference2 implements LaraPreference {
	};

	static public class LTestDecisionConfig extends LDecisionConfiguration {
		public LTestDecisionConfig() {
			Collection<Class<? extends LaraPreference>> prefs = new HashSet<Class<? extends LaraPreference>>();
			prefs.add(LTestPreference1.class);
			prefs.add(LTestPreference2.class);

			this.setPreferences(prefs);
		}
	}

	/**
	 * test agent
	 */
	public static class LTestAgent extends LAbstractAgent<LTestAgent, LTestBo> {

		/**
		 * constructor
		 * 
		 * @param env
		 * @param name
		 */
		public LTestAgent(String name) {
			super(new LEnvironment(), name);
		}

		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public LTestAgent getThis() {
			return this;
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}
	}

	/**
	 * Test behavioural option for LTestAgent
	 * 
	 * @author Sascha Holzhauer
	 * 
	 */
	public static class LTestBo extends LaraBehaviouralOption<LTestAgent, LTestBo> {

		static int counter = 0;
		public LTestBo(LTestAgent agent) {
			super("LTestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent);
		}

		public LTestBo(LTestAgent agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super("LTestBo"
					+ LModel.getModel().getIntegerFormat().format(counter++),
					agent, utilities);
		}

		public LTestBo(String key, LTestAgent agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			super(key, agent, utilities);
		}

		/**
		 * @param key
		 * @param agent
		 * @param prefEntry
		 */
		public LTestBo(String key, LTestAgent agent, LPrefEntry... prefEntry) {
			super(key, agent, prefEntry);
		}

		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return new HashMap<Class<? extends LaraPreference>, Double>();
		}

		@Override
		public LTestBo getModifiedBO(LTestAgent agent,
				Map<Class<? extends LaraPreference>, Double> preferenceUtilities) {
			return new LTestBo(this.getKey(), agent, preferenceUtilities);
		}
	}

	/**
	 * Inits a {@link LaraModel} as test model.
	 */
	public static void initTestModel() {
		LaraModel model = new LAbstractStandaloneSynchronisedModel() {
		};
		((LAbstractModel) model).init();
	}
}
