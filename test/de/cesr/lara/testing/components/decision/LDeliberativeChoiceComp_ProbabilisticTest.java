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
package de.cesr.lara.testing.components.decision;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cern.jet.random.AbstractDistribution;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraDeliberativeChoiceComponent;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_Probabilistic;
import de.cesr.lara.components.decision.impl.LLightBoRow;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LDeliberativeChoiceComp_ProbabilisticTest {

	/**
	 * Logger
	 */
	static private Logger logger = Logger
			.getLogger(LDeliberativeChoiceComp_ProbabilisticTest.class);

	LaraDecisionConfiguration dConfig = LTestUtils.dConfig;

	LTestAgent agent;

	static int counter = 0;

	LaraDeliberativeChoiceComponent choiceComp;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel(dConfig);

		// set up manipulated random number distribution
		AbstractDistribution manipulatedDist = new AbstractDistribution() {

			double[] randomNumbers = {0.7, 0.1, 0.4, 0.48, 0.6, 0.4,
					
					//0.7, 0.4, 0.3, 0.01, 0.99, 0.1,
					//0.01, 0.35, 0.8, 0.4,

					//0.99, 0.1, 0.4, 0.1, 0.01, 0.4, 0.5, 0.33 };
					
					0.5, 0.6, 0.34, 0.33, 0.4, 0.6, 0.1, 0.01, 0.38, 0.42, 0.4};

			private static final long serialVersionUID = -6074066644642361799L;

			@Override
			public double nextDouble() {
				// <- LOGGING
				logger.info("Random Number: " + randomNumbers[counter]);
				// LOGGING ->
				return randomNumbers[counter++];
			}
		};
		LModel.getModel()
				.getLRandom()
				.registerDistribution(manipulatedDist,
						"Manipulated Test Distribution");

		agent = new LTestAgent("TestAgent");
		choiceComp = new LDeliberativeChoiceComp_Probabilistic(
				"Manipulated Test Distribution");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_Probabilistic#getKSelectedBos(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.util.Collection, int)}
	 * .
	 */
	@Test
	public void testGetKSelectedBos() {
		// Only one BO
		LTestBo bo1 = new LTestBo("BO1", agent);
		Collection<LaraBoRow<LTestBo>> boRows = new ArrayList<LaraBoRow<LTestBo>>();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 1.0));

		logger.info("Request more BOs than available.");
		try {
			choiceComp.getKSelectedBos(dConfig, boRows, 2);
			fail("This should have raised an exception.");  // no rand
		} catch (IllegalArgumentException lre) {
		}

		// Test "normal" BOs
		LTestBo bo2 = new LTestBo("BO2", agent);
		boRows.add(new LLightBoRow<LTestBo>(bo2, 1.0));

		logger.info("Request 2 BOs (2 available).");
		Set<? extends LTestBo> resultSet = choiceComp.getKSelectedBos(dConfig,
				boRows, 2);
		assertEquals(2, resultSet.size());
		assertTrue(resultSet.contains(bo1));   // no rand
		assertTrue(resultSet.contains(bo2));   // no rand

		boRows.clear();
		logger.info("Request 1 BO (2 available).");
		boRows.add(new LLightBoRow<LTestBo>(bo1, 1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 1.0));
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 1);
		assertEquals(1, resultSet.size());
		assertFalse(resultSet.contains(bo1));  // rand > 0.5
		assertTrue(resultSet.contains(bo2));

		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 2.0));
		LTestBo bo3 = new LTestBo("BO3", agent);
		boRows.add(new LLightBoRow<LTestBo>(bo3, 3.0));

		logger.info("Request 2 BOs (3 available).");
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 2);
		assertEquals(2, resultSet.size());
		assertTrue(resultSet.contains(bo1)); // rand <= 1/6
		assertTrue(resultSet.contains(bo2)); // rand <= 2/5
		assertFalse(resultSet.contains(bo3));

		// multiple BOs with utility 0.0 only
		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 0.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));

		logger.info("Request 2 BOs (2 available with 0.0).");
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 2);
		assertEquals(2, resultSet.size());
		assertTrue(resultSet.contains(bo1));
		assertTrue(resultSet.contains(bo2));

		logger.info("Request 1 BO (2 available with 0.0).");
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 1);
		assertEquals(1, resultSet.size());
		assertTrue(resultSet.contains(bo1));  // rand <= 0.5
		assertFalse(resultSet.contains(bo2));

		// BOs with negative utility
		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, -1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));

		logger.info("Request 1 BO (2 available with 0.0 and -1.0).");
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 1);
		assertEquals(1, resultSet.size());
		assertFalse(resultSet.contains(bo1));
		assertTrue(resultSet.contains(bo2));  // rand > 0.5

		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, -1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));
		boRows.add(new LLightBoRow<LTestBo>(bo3, -0.5));

		logger.info("Request 1 BO (3 available with -1.0, -0.5 and 0.0).");
		resultSet = choiceComp.getKSelectedBos(dConfig, boRows, 1);
		assertEquals(1, resultSet.size());
		assertFalse(resultSet.contains(bo1));
		assertTrue(resultSet.contains(bo2)); // rand < 0.5
		assertFalse(resultSet.contains(bo3));

		// empty list of LaraRows (exception!)
		try {
			boRows.clear();
			choiceComp.getKSelectedBos(dConfig, boRows, 3);
			fail("This should have raised an exception.");
		} catch (IllegalStateException lre) {
		}
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_Probabilistic#getSelectedBo(de.cesr.lara.components.decision.LaraDecisionConfiguration, java.util.Collection)}
	 * .
	 */
	@Test
	public void testGetSelectedBo() {
		// Only one BO
		LTestBo bo1 = new LTestBo(agent);
		Collection<LaraBoRow<LTestBo>> boRows = new ArrayList<LaraBoRow<LTestBo>>();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 1.0));

		assertEquals(bo1, choiceComp.getSelectedBo(dConfig, boRows));  // no rand

		// Test "normal" BOs
		LTestBo bo2 = new LTestBo(agent);
		boRows.add(new LLightBoRow<LTestBo>(bo2, 1.0));
		logger.info("1st selection for one BO.");
		assertEquals(bo1, choiceComp.getSelectedBo(dConfig, boRows));  // rand <= 0.5

		logger.info("2nd selection for one BO.");
		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows)); // rand > 0.5

		logger.info("2 Bos passed.");
		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 2.0));

		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows)); // rand > 0.33

		assertEquals(bo1, choiceComp.getSelectedBo(dConfig, boRows)); // rand <= 0.33

		// multiple BOs with utility 0.0 only
		boRows.clear();
		boRows.add(new LLightBoRow<LTestBo>(bo1, 0.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));
		logger.info("2 BoRows with utility sum 0.0 passed.");
		assertEquals(bo1, choiceComp.getSelectedBo(dConfig, boRows)); // rand <= 0.5
		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows)); // rand > 0.5

		// BOs with negative utility
		boRows.clear();
		logger.info("One BoRow with utility sum 0.0, one with -1.0 passed.");
		boRows.add(new LLightBoRow<LTestBo>(bo1, -1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));

		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows));  // rand > 0
		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows));  // rand > 0

		boRows.clear();
		logger.info("One BoRow with utility sum 0.0, one with -1.0, one with 0.5 passed.");
		boRows.add(new LLightBoRow<LTestBo>(bo1, -1.0));
		boRows.add(new LLightBoRow<LTestBo>(bo2, 0.0));
		LTestBo bo3 = new LTestBo(agent);
		boRows.add(new LLightBoRow<LTestBo>(bo3, 0.5));

		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows)); // rand <= 2/5
		assertEquals(bo3, choiceComp.getSelectedBo(dConfig, boRows)); // rand > 2/5
		assertEquals(bo2, choiceComp.getSelectedBo(dConfig, boRows)); // rand <= 2/5

		// empty list of LaraRows (exception!)
		try {
			boRows.clear();
			choiceComp.getSelectedBo(dConfig, boRows);
			fail("This should have raised an exception.");
		} catch (IllegalStateException lre) {
		}
	}

}
