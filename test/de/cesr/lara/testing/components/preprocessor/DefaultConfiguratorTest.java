/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 08.02.2010
 */
package de.cesr.lara.testing.components.preprocessor;


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.testing.TestUtils.TestAgent;


/**
 * 
 * @author Sascha Holzhauer
 * @date 08.02.2010
 * 
 */
public class DefaultConfiguratorTest {

	LaraPreprocessorConfiguration<TestAgent, LGeneralBehaviouralOption<TestAgent>>	configurator1;
	LaraDecisionConfiguration														decision1;

	LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>				collector;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 08.02.2010
	 */
	@Before
	public void setUp() throws Exception {
		configurator1 = LPreprocessorConfigurator.getDefaultPreprocessConfigurator();
		decision1 = new LaraDecisionConfiguration() {

			@Override
			public Collection<Class<? extends LaraPreference>> getPreferences() {
				return null;
			}

			@Override
			public void setPreferences(Collection<Class<? extends LaraPreference>> goals) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getId() {
				return "TestDecision";
			}
		};

		configurator1.setDecisionModeSelector(((LPreprocessorConfigurator) configurator1).DEFAULT_DECISION_MODE_SELECTOR);
		configurator1.setBOCollector(((LPreprocessorConfigurator) configurator1).DEFAULT_BO_COLLECTOR);
		configurator1.setBOPreselector(((LPreprocessorConfigurator) configurator1).DEFAULT_BO_PRESELECTOR, decision1);
		configurator1.setBOAdapter(((LPreprocessorConfigurator) configurator1).DEFAULT_BO_UPDATE_BUILDER);
		configurator1.setPreferenceUpdater(((LPreprocessorConfigurator) configurator1).DEFAULT_PREFERENCE_UPDATER);

		collector = new LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>() {
			@Override
			public Collection<LGeneralBehaviouralOption<TestAgent>> collectBOs(TestAgent agent,
					LaraBOMemory<LGeneralBehaviouralOption<TestAgent>> memory, LaraDecisionConfiguration decision1) {
				throw new LPreprocessorBuilderTest.PreprocessorTestException();
			}
		};
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 08.02.2010
	 */
	@After
	public void tearDown() throws Exception {
		configurator1 = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessFactory.DefaultConfigurator#set(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.lang.Class, LaraPreprocessorComp)}
	 * .
	 */
	@Test
	public final void testSet() {
		configurator1.set(decision1, LaraBOCollector.class, collector);
		assertEquals("collector for decision1 needs to be collector", collector, configurator1.get(decision1,
				LaraBOCollector.class));
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessFactory.DefaultConfigurator#get(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.lang.Class)}
	 * .
	 */
	@Test
	public final void testGet() {
		assertEquals("default preselector needs to be default preselector",
				((LPreprocessorConfigurator) configurator1).DEFAULT_BO_PRESELECTOR, configurator1.get(decision1,
						LaraBOPreselector.class));
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessFactory.DefaultConfigurator#getMap(java.lang.Class)}.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public final void testGetMap() {
		configurator1.set(decision1, LaraBOCollector.class, collector);
		Map<LaraDecisionConfiguration, LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>> scannerMap = configurator1
				.<LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>, LaraBOCollector> getMap(LaraBOCollector.class);
		assertEquals("collector map needs to contain collector for decsion1", collector, scannerMap.get(decision1));

		Map<LaraDecisionConfiguration, LaraBOPreselector<TestAgent, LGeneralBehaviouralOption<TestAgent>>> checkerMap = configurator1
				.<LaraBOPreselector<TestAgent, LGeneralBehaviouralOption<TestAgent>>, LaraBOPreselector> getMap(LaraBOPreselector.class);
		assertEquals("preselector map needs to contain collector for decsion1",
				((LPreprocessorConfigurator) configurator1).DEFAULT_BO_PRESELECTOR, checkerMap.get(decision1));
	}
}
