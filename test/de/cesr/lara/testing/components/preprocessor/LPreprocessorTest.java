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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.LaraPreprocessor;
import de.cesr.lara.components.preprocessor.LaraPreprocessorConfigurator;
import de.cesr.lara.components.preprocessor.event.LaraPpEvent;
import de.cesr.lara.components.preprocessor.impl.LPreprocessorConfigurator;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;

/**
 * 
 * @author Sascha Holzhauer
 * @date 05.02.2010
 * 
 */
public class LPreprocessorTest {

	static class PreprocessorTestException extends RuntimeException {

		private static final long serialVersionUID = 1L;
	}

	LaraDecisionConfiguration decision1;
	LaraDecisionConfiguration decision2;

	LaraPreprocessorConfigurator<LTestAgent, LTestBo> configurator1;
	LaraPreprocessorConfigurator<LTestAgent, LTestBo> configurator2;
	LaraPreprocessorConfigurator<LTestAgent, LTestBo> configurator3;

	LaraBOCollector<LTestAgent, LTestBo> scanner;

	LaraPreprocessor<LTestAgent, LTestBo> builder1;
	LaraPreprocessor<LTestAgent, LTestBo> builder2;
	LaraPreprocessor<LTestAgent, LTestBo> builder3;
	LaraPreprocessor<LTestAgent, LTestBo> builder12;

	static boolean preprocessCalled = false;

	/**
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		decision1 = new LaraDecisionConfiguration() {

			@Override
			public String getId() {
				return "TestDecision";
			}

			@Override
			public Collection<LaraPreference> getPreferences() {
				return null;
			}

			@Override
			public void setPreferences(Collection<LaraPreference> goals) {
			}
		};

		decision2 = new LaraDecisionConfiguration() {

			@Override
			public String getId() {
				return "TestDecision";
			}

			@Override
			public Collection<LaraPreference> getPreferences() {
				return null;
			}

			@Override
			public void setPreferences(Collection<LaraPreference> goals) {
			}
		};

		/**
		 * Default Configurator
		 */
		configurator1 = LPreprocessorConfigurator
				.getNewPreprocessorConfigurator();

		/**
		 * Default LaraPreprocessor
		 */
		builder1 = configurator1.getPreprocessor();

		/**
		 * Second Configurator with another LaraBOCollector
		 */
		configurator2 = LPreprocessorConfigurator
				.getNewPreprocessorConfigurator();
		scanner = new LaraBOCollector<LTestAgent, LTestBo>() {

			@Override
			public <E extends LaraPpEvent> E castEvent(Class<E> clazz,
					LaraEvent event) {
				return null;
			}

			@Override
			public void onInternalEvent(LaraEvent event) {
				preprocessCalled = true;
			}
		};
		configurator2.setBOCollector(scanner);
		builder2 = configurator2.getPreprocessor();

		configurator3 = LPreprocessorConfigurator
				.getNewPreprocessorConfigurator();
		configurator3.setBOCollector(scanner, decision1);

		builder3 = configurator3.getPreprocessor();

		builder12 = configurator1.getPreprocessor();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		preprocessCalled = false;
	}

	/**
	 * @see LPreprocessorTest#testMeetsConfigurator() Test method for
	 *      {@link de.cesr.lara.components.preprocessor.impl.LPreprocessor#equals(java.lang.Object)}
	 *      . Created by Sascha Holzhauer on 08.02.2010
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals(
				"Both preprocessor builders should be equal because requested by same configurator",
				builder1, builder12);

		assertEquals(
				"Configurator3 (collector for decision1, condition) may not meet configurator 2 (collector as default)",
				false, builder2.equals(builder3));

	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LPreprocessor#preprocess(LaraDecisionConfiguration, de.cesr.lara.components.agents.LaraAgent)}
	 * .
	 */
	public final void testGetPreprocessor() {
		builder2.preprocess(decision1, new LTestAgent("LTestAgent"));
		assertFalse(preprocessCalled);
	}

	/**
	 * @see LPreprocessorTest#testEqualsObject() Created by Sascha Holzhauer on
	 *      08.02.2010
	 */
	@Test
	public final void testGetPreprocessorBuilder() {
		LaraPreprocessor<LTestAgent, LTestBo> builder2 = configurator2
				.getPreprocessor();
		assertNotSame(
				"Bilders of different configuration need to be different",
				builder1, builder2);
	}

	/**
	 * meets configurator
	 */
	@Test
	public final void testMeetsConfigurator() {
		assertEquals(
				"Generating configurator needs to meet generated builder1",
				true, builder1.meetsConfiguration(configurator1));
		assertEquals("Second configurator may not match generated builder1",
				false, builder1.meetsConfiguration(configurator2));

		assertEquals(
				"Configurator3 (collector for decision1) may not meet configurator/builder 2 (collector as default)",
				false, builder2.meetsConfiguration(configurator3));

		assertEquals("Configurator3 (more definitions) may not meet Builder1",
				false, builder1.meetsConfiguration(configurator3));
		// in the current implementation null is used as default - therefore, a
		// difference between nothing set and
		// default
		// is not identifiable!
		// assertEquals("Configurator1 (less definitions) may not meet Builder3",
		// false, builder3
		// .meetsConfigurator(configurator1));
	}

}