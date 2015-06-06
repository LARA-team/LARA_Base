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

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraBOUtilityUpdater;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;
import de.cesr.lara.components.preprocessor.LaraPreferenceUpdater;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.event.LPpBoCollectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoPreselectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpBoUtilityUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LPpModeSelectorEvent;
import de.cesr.lara.components.preprocessor.event.LPpPreferenceUpdaterEvent;
import de.cesr.lara.components.preprocessor.event.LaraPpEvent;
import de.cesr.lara.components.preprocessor.impl.LAbstractPpComp;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LPreprocessorTestUtils {

	public static abstract class LAbstractTestPpComp<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractPpComp<A, BO> {

		protected boolean isCalled = false;

		public abstract Class<? extends LaraPpEvent> getRelevantEventClass();

		@Override
		public void onInternalEvent(LaraEvent event) {
			if (getRelevantEventClass().isAssignableFrom(event.getClass())) {
				this.isCalled = true;
			}
		}

		public boolean isCalled() {
			return this.isCalled;
		}
	}

	public static class LTestModeSelector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractTestPpComp<A, BO> implements
			LaraDecisionModeSelector<A, BO> {

		@Override
		public Class<? extends LaraPpEvent> getRelevantEventClass() {
			return LPpModeSelectorEvent.class;
		}
	}

	public static class LTestBoCollector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractTestPpComp<A, BO> implements
			LaraBOCollector<A, BO> {

		@Override
		public Class<? extends LaraPpEvent> getRelevantEventClass() {
			return LPpBoCollectorEvent.class;
		}
	}

	public static class LTestBoPreselector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractTestPpComp<A, BO> implements
			LaraBOPreselector<A, BO> {

		@Override
		public Class<? extends LaraPpEvent> getRelevantEventClass() {
			return LPpBoPreselectorEvent.class;
		}
	}

	public static class LTestBoUtilityUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractTestPpComp<A, BO> implements
			LaraBOUtilityUpdater<A, BO> {

		@Override
		public Class<? extends LaraPpEvent> getRelevantEventClass() {
			return LPpBoUtilityUpdaterEvent.class;
		}
	}

	public static class LTestPreferenceUpdater<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends LAbstractTestPpComp<A, BO> implements
			LaraPreferenceUpdater<A, BO> {

		@Override
		public Class<? extends LaraPpEvent> getRelevantEventClass() {
			return LPpPreferenceUpdaterEvent.class;
		}
	}

	public static <A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>> LaraPreprocessorConfigurator<A, BO> getTestPreprocessorConfig() {
		LaraPreprocessorConfigurator<A, BO> ppConfig = LPreprocessorConfigurator.getNewPreprocessorConfigurator();

		ppConfig.setDecisionModeSelector(new LPreprocessorTestUtils.LTestModeSelector<A, BO>());
		ppConfig.setBOCollector(new LPreprocessorTestUtils.LTestBoCollector<A, BO>());
		ppConfig.setBoPreselector(new LPreprocessorTestUtils.LTestBoPreselector<A, BO>());
		ppConfig.setBOAdapter(new LPreprocessorTestUtils.LTestBoUtilityUpdater<A, BO>());
		ppConfig.setPreferenceUpdater(new LPreprocessorTestUtils.LTestPreferenceUpdater<A, BO>());

		return ppConfig;
	}
}
