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


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.event.LaraPpEvent;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;


/**
 * 
 * @author Sascha Holzhauer
 * @date 08.02.2010
 * 
 */
public class DefaultConfiguratorTest {

	LaraPreprocessorConfigurator<LTestAgent, LTestBo>	configurator1;
	LaraDecisionConfiguration							decision1;

	LaraBOCollector<LTestAgent, LTestBo>				collector;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		configurator1 = LPreprocessorConfigurator.getNewPreprocessorConfigurator();
		decision1 = new LaraDecisionConfiguration() {

			@Override
			public String getId() {
				return "TestDecision";
			}

			@Override
			public Collection<Class<? extends LaraPreference>> getPreferences() {
				return null;
			}

			@Override
			public void setPreferences(Collection<Class<? extends LaraPreference>> goals) {
				// nothing to do
			}
		};

		configurator1
				.setDecisionModeSelector(((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_DECISION_MODE_SELECTOR);
		configurator1
				.setBOCollector(((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_BO_COLLECTOR);
		configurator1.setBOPreselector(
				((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_BO_PRESELECTOR, decision1);
		configurator1
				.setBOAdapter(((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_BO_UPDATE_BUILDER);
		configurator1
				.setPreferenceUpdater(((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_PREFERENCE_UPDATER);

		collector = new LaraBOCollector<LTestAgent, LTestBo>() {

			@Override
			public <E extends LaraPpEvent> E castEvent(Class<E> clazz, LaraEvent event) {
				return null;
			}

			@Override
			public void onInternalEvent(LaraEvent event) {
				throw new LPreprocessorTest.PreprocessorTestException();
			}
		};
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		configurator1 = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessor.DefaultConfigurator#get(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.lang.Class)}
	 * .
	 */
	@Test
	public final void testGet() {
		assertEquals("default preselector needs to be default preselector",
				((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_BO_PRESELECTOR,
				configurator1.get(decision1, LaraBOPreselector.class));
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessor.DefaultConfigurator#getMap(java.lang.Class)} .
	 */
	@Test
	public final void testGetMap() {
		configurator1.set(decision1, LaraBOCollector.class, collector);
		Map<LaraDecisionConfiguration, LaraBOCollector<LTestAgent, LTestBo>> scannerMap =
				configurator1.<LaraBOCollector<LTestAgent, LTestBo>> getMap(LaraBOCollector.class);
		assertEquals("collector map needs to contain collector for decsion1", collector, scannerMap.get(decision1));

		Map<LaraDecisionConfiguration, LaraBOPreselector<LTestAgent, LTestBo>> checkerMap =
				configurator1.<LaraBOPreselector<LTestAgent, LTestBo>> getMap(LaraBOPreselector.class);
		assertEquals("preselector map needs to contain collector for decsion1",
				((LPreprocessorConfigurator<LTestAgent, LTestBo>) configurator1).DEFAULT_BO_PRESELECTOR,
				checkerMap.get(decision1));
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessor.DefaultConfigurator#set(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.lang.Class, LaraPreprocessorComp)}
	 * .
	 */
	@Test
	public final void testSet() {
		configurator1.set(decision1, LaraBOCollector.class, collector);
		assertEquals("collector for decision1 needs to be collector", collector,
				configurator1.get(decision1, LaraBOCollector.class));
	}
}