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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.LaraRow;
import de.cesr.lara.components.decision.LaraUtilityMatrix;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionHeuristicComponent_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.decision.impl.LRow;
import de.cesr.lara.components.decision.impl.LUtilityMatrix;
import de.cesr.lara.testing.TestUtils;
import de.cesr.lara.testing.TestUtils.TestAgent;
import de.cesr.lara.testing.TestUtils.TestBo;


/**
 * 
 */
public class LDecisionHeuristicComponent_MaxLineTotalRandomAtTieTest {

	LDecisionHeuristicComponent_MaxLineTotalRandomAtTie	heuristic;
	LaraUtilityMatrix<TestBo> matrix;
	TestBo[] bos;
	LaraDecisionConfiguration																			dBuilder;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// TODO there is a memory error (no key...)
		heuristic = new LDecisionHeuristicComponent_MaxLineTotalRandomAtTie();
		matrix = new LUtilityMatrix<TestBo>();
		dBuilder = new LDecisionConfiguration();

		Collection<LaraRow<TestBo>> utilityMatrixRows = new ArrayList<LaraRow<TestBo>>();
		// add behavioural options as row to matrix
		int[] utValues = { 0, 1, 2, 3, 5, 5, 5, 7, 8, 9 };
		bos = new TestBo[10];

		TestAgent agent = new TestUtils.TestAgent("TestAgent");

		for (int i = 0; i < 10; i++) {
			bos[i] = new TestBo("" + i, agent,
					new HashMap<Class<? extends LaraPreference>, Double>()) {

				@Override
				public TestBo getModifiedBO(TestAgent agent,
						Map<Class<? extends LaraPreference>, Double> utilities) {
					return null;
				}

				@Override
				public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(LaraDecisionConfiguration dBuilder) {
					return null;
				}
			};
			List<Double> values = new ArrayList<Double>();
			values.add(new Double(utValues[i]));
			utilityMatrixRows.add(new LRow<TestBo>(bos[i], values));
		}
		matrix.setRows(utilityMatrixRows);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDecisionHeuristicComponent_MaxLineTotalRandomAtTie#getBestBehaviouralOption(de.cesr.lara.components.decision.LaraUtilityMatrix)}
	 * .
	 */
	@Test
	@Ignore
	public final void testGetBestBehaviouralOption() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.decision.impl.LDecisionHeuristicComponent_MaxLineTotalRandomAtTie#getKBestBehaviouralOptions(de.cesr.lara.components.decision.LaraUtilityMatrix, int)}
	 * .
	 */
	@Test
	public final void testGetKBestBehaviouralOptions() {
		Set<? extends LaraBehaviouralOption<TestAgent, TestBo>> bosSet = heuristic
				.getKSelectedBOs(dBuilder, matrix, 3);
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

		bosSet = heuristic.getKSelectedBOs(dBuilder, matrix, 6);
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

		bosSet = heuristic.getKSelectedBOs(dBuilder, matrix, 5);
		assertEquals(5, bosSet.size());
		assertFalse(bosSet.contains(bos[0]));
		assertFalse(bosSet.contains(bos[1]));
		assertFalse(bosSet.contains(bos[2]));
		assertFalse(bosSet.contains(bos[3]));
		assertTrue(bosSet.contains(bos[7]));
		assertTrue(bosSet.contains(bos[8]));
		assertTrue(bosSet.contains(bos[9]));

	}

}
