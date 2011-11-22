/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.02.2010
 */
package de.cesr.lara.testing.components.preprocessor;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;
import de.cesr.lara.components.preprocessor.LaraPreprocessorFactory;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfiguration;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.testing.TestUtils.TestAgent;


/**
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public class LPreprocessorBuilderTest {

	static class PreprocessorTestException extends RuntimeException {

		private static final long	serialVersionUID	= 1L;
	}

	LaraDecisionConfiguration																decision1;
	LaraDecisionConfiguration																decision2;

	LaraPreprocessorConfiguration<TestAgent, LGeneralBehaviouralOption<TestAgent>>	configurator1;
	LaraPreprocessorConfiguration<TestAgent, LGeneralBehaviouralOption<TestAgent>>	configurator2;
	LaraPreprocessorConfiguration<TestAgent, LGeneralBehaviouralOption<TestAgent>>	configurator3;

	LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>				scanner;

	LaraPreprocessorFactory<TestAgent, LGeneralBehaviouralOption<TestAgent>>			builder1;
	LaraPreprocessorFactory<TestAgent, LGeneralBehaviouralOption<TestAgent>>			builder2;
	LaraPreprocessorFactory<TestAgent, LGeneralBehaviouralOption<TestAgent>>			builder3;
	LaraPreprocessorFactory<TestAgent, LGeneralBehaviouralOption<TestAgent>>			builder12;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 05.02.2010
	 */
	/**
	 * @throws Exception
	 *         Created by Sascha Holzhauer on 08.02.2010
	 */
	/**
	 * @throws Exception
	 *         Created by Sascha Holzhauer on 08.02.2010
	 */
	@Before
	public void setUp() throws Exception {
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

		decision2 = new LaraDecisionConfiguration() {

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

		/**
		 * Default Configurator
		 */
		configurator1 = LPreprocessorConfigurator.getDefaultPreprocessConfigurator();

		/**
		 * Default LaraPreprocessorFactory
		 */
		builder1 = configurator1.getPreprocessorFactory();

		/**
		 * Second Configurator with another LaraBOCollector
		 */
		configurator2 = LPreprocessorConfigurator.getDefaultPreprocessConfigurator();
		scanner = new LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>() {
			@Override
			public Collection<LGeneralBehaviouralOption<TestAgent>> collectBOs(TestAgent agent,
					LaraBOMemory<LGeneralBehaviouralOption<TestAgent>> memory, LaraDecisionConfiguration dBuilder) {
				throw new LPreprocessorBuilderTest.PreprocessorTestException();
			}
		};
		configurator2.setBOCollector(scanner);
		builder2 = configurator2.getPreprocessorFactory();

		configurator3 = LPreprocessorConfigurator.getDefaultPreprocessConfigurator();
		configurator3.setBOCollector(scanner, decision1);

		builder3 = configurator3.getPreprocessorFactory();

		builder12 = configurator1.getPreprocessorFactory();
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 05.02.2010
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * @see LPreprocessorBuilderTest#testEqualsObject() Created by Sascha Holzhauer on 08.02.2010
	 */
	@Test
	public final void testGetPreprocessorBuilder() {
		LaraPreprocessorFactory<TestAgent, LGeneralBehaviouralOption<TestAgent>> builder2 = configurator2
				.getPreprocessorFactory();
		assertNotSame("Bilders of different configuration need to be different", builder1, builder2);
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessFactory#getPreprocessor(de.cesr.lara.components.decision.LaraDecisionConfiguration)}
	 * .
	 */
	@Test(expected = LPreprocessorBuilderTest.PreprocessorTestException.class)
	public final void testGetPreprocessor() {
		builder2.getPreprocessor(decision1).preprocess(LaraBOPreselector.LAccuracy.MODERATE, new TestAgent("TestAgent"));
	}

	/**
	 * meets configurator
	 */
	@Test
	public final void testMeetsConfigurator() {
		assertEquals("Generating configurator needs to meet generated builder1", true, builder1
				.meetsConfiguration(configurator1));
		assertEquals("Second configurator may not match generated builder1", false, builder1
				.meetsConfiguration(configurator2));

		assertEquals(
				"Configurator3 (collector for decision1) may not meet configurator/builder 2 (collector as default)",
				false, builder2.meetsConfiguration(configurator3));

		assertEquals("Configurator3 (more definitions) may not meet Builder1", false, builder1
				.meetsConfiguration(configurator3));
		// in the current implementation null is used as default - therefore, a difference between nothing set and
		// default
		// is not identifiable!
		// assertEquals("Configurator1 (less definitions) may not meet Builder3", false, builder3
		// .meetsConfigurator(configurator1));
	}

	/**
	 * @see LPreprocessorBuilderTest#testMeetsConfigurator() Test method for
	 *      {@link de.cesr.lara.components.preprocessor.impl.LPreprocessFactory#equals(java.lang.Object)}. Created by
	 *      Sascha Holzhauer on 08.02.2010
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals("Both preprocessor builders should be equal because requested by same configurator", builder1,
				builder12);

		assertEquals(
				"Configurator3 (collector for decision1, condition) may not meet configurator 2 (collector as default)",
				false, builder2.equals(builder3));

	}
}