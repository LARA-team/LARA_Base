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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cern.jet.random.Uniform;
import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraBoRow;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.decision.impl.LLightBoRow;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.testing.LTestUtils;
import de.cesr.lara.testing.LTestUtils.LTestAgent;
import de.cesr.lara.testing.LTestUtils.LTestBo;

/**
 * 
 */
public class LDeliberativeChoiceComponent_MaxLineTotalRandomAtTieTest {

	LDeliberativeChoiceComp_MaxLineTotalRandomAtTie deliberativeChoiceComp;
	Collection<LaraBoRow<LTestBo>> laraBoRows;
	LTestBo[] bos;
	LaraDecisionConfiguration dConfig = LTestUtils.dConfig;
	LTestAgent agent;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel(dConfig);
		deliberativeChoiceComp = LDeliberativeChoiceComp_MaxLineTotalRandomAtTie
				.getInstance(LModel.getModel(), null);
		laraBoRows = new LinkedHashSet<LaraBoRow<LTestBo>>();
		dConfig = new LDecisionConfiguration();

		// add behavioural options as row to laraBoRows
		double[] valuesSum = { 0, 1, 2, 3, 5, 5, 5, 7, 8, 9 };
		bos = new LTestBo[10];

		LTestAgent agent = new LTestUtils.LTestAgent("LTestAgent");

		for (int i = 0; i < 10; i++) {
			bos[i] = new LTestBo("" + i, agent,
					new HashMap<LaraPreference, Double>());
			laraBoRows.add(new LLightBoRow<LTestBo>(bos[i], valuesSum[i]));
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie#getBestBehaviouralOption(de.cesr.lara.components.decision.LaraUtilityMatrix)}
	 * .
	 */
	@Test
	public final void testGetBestBehaviouralOption() {
		LTestBo bo = deliberativeChoiceComp.getSelectedBo(dConfig, laraBoRows);
		assertEquals(bos[9], bo);
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDeliberativeChoiceComp_MaxLineTotalRandomAtTie#getKBestBehaviouralOptions(de.cesr.lara.components.decision.LaraUtilityMatrix, int)}
	 * .
	 */
	@Test
	public final void testGetKBestBehaviouralOptions() {
		Set<? extends LaraBehaviouralOption<LTestAgent, LTestBo>> bosSet = deliberativeChoiceComp
				.getKSelectedBos(dConfig, laraBoRows, 3);
		assertEquals(3, bosSet.size());
		assertFalse(bosSet.contains(bos[0]));
		assertFalse(bosSet.contains(bos[1]));
		assertFalse(bosSet.contains(bos[2]));
		assertFalse(bosSet.contains(bos[3]));
		assertFalse(bosSet.contains(bos[4]));
		assertFalse(bosSet.contains(bos[5]));
		assertFalse(bosSet.contains(bos[6]));
		assertTrue(bosSet.contains(bos[7]));
		assertTrue(bosSet.contains(bos[8]));
		assertTrue(bosSet.contains(bos[9]));

		bosSet = deliberativeChoiceComp.getKSelectedBos(dConfig, laraBoRows, 6);
		assertEquals(6, bosSet.size());
		assertFalse(bosSet.contains(bos[0]));
		assertFalse(bosSet.contains(bos[1]));
		assertFalse(bosSet.contains(bos[2]));
		assertFalse(bosSet.contains(bos[3]));
		assertTrue(bosSet.contains(bos[4]));
		assertTrue(bosSet.contains(bos[5]));
		assertTrue(bosSet.contains(bos[6]));
		assertTrue(bosSet.contains(bos[7]));
		assertTrue(bosSet.contains(bos[8]));
		assertTrue(bosSet.contains(bos[9]));

		bosSet = deliberativeChoiceComp.getKSelectedBos(dConfig, laraBoRows, 5);
		assertEquals(5, bosSet.size());
		assertFalse(bosSet.contains(bos[0]));
		assertFalse(bosSet.contains(bos[1]));
		assertFalse(bosSet.contains(bos[2]));
		assertFalse(bosSet.contains(bos[3]));
		assertTrue(bosSet.contains(bos[7]));
		assertTrue(bosSet.contains(bos[8]));
		assertTrue(bosSet.contains(bos[9]));
	}
	
	protected static class CustomUniform extends Uniform {

		public CustomUniform(double arg0, double arg1, int arg2) {
			super(arg0, arg1, arg2);
		}
		
		int counter = 0;
		int[] randomNumbers = {0, 1};

		private static final long serialVersionUID = -6074066644642361799L;

		@Override
		public int nextIntFromTo(int from, int to) {
			return randomNumbers[counter++];
		}
	}
	
	@Test
	public final void testGetSelectedBO() {
		// set up manipulated random number distribution
		Uniform manipulatedDist = new CustomUniform(0.0, 1.0, 1);
		LModel.getModel()
		.getLRandom()
		.registerDistribution(manipulatedDist,
				"Manipulated Test Distribution");
		LDeliberativeChoiceComp_MaxLineTotalRandomAtTie choiceComp = LDeliberativeChoiceComp_MaxLineTotalRandomAtTie
				.getInstance(LModel.getModel(), "Manipulated Test Distribution");
		
		Collection<LaraBoRow<LTestBo>> boRows = new ArrayList<LaraBoRow<LTestBo>>();
		Iterator<LaraBoRow<LTestBo>> iterator = laraBoRows.iterator();
		iterator.next();
		iterator.next();
		iterator.next();
		iterator.next();
		boRows.add(iterator.next());
		boRows.add(iterator.next());
		assertEquals(bos[4], choiceComp.getSelectedBo(dConfig, boRows));
		assertEquals(bos[5], choiceComp.getSelectedBo(dConfig, boRows));
	}
}