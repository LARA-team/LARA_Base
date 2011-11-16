/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 05.05.2010
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
import de.cesr.lara.components.decision.impl.LDecisionHeuristicComponent_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LRow;
import de.cesr.lara.components.decision.impl.LUtilityMatrix;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.testing.TestUtils;
import de.cesr.lara.testing.TestUtils.TestAgent;


/**
 * 
 */
public class LDecisionHeuristicComponent_MaxLineTotalRandomAtTieTest {

	LDecisionHeuristicComponent_MaxLineTotalRandomAtTie<LGeneralBehaviouralOption<TestAgent>>	heuristic;
	LaraUtilityMatrix<LGeneralBehaviouralOption<TestAgent>>										matrix;
	LGeneralBehaviouralOption<TestAgent>[]														bos;
	LaraDecisionConfiguration																			dBuilder;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// TODO there is a memory error (no key...)
		heuristic = new LDecisionHeuristicComponent_MaxLineTotalRandomAtTie<LGeneralBehaviouralOption<TestAgent>>();
		matrix = new LUtilityMatrix<LGeneralBehaviouralOption<TestAgent>>();
		dBuilder = new LDecisionConfiguration();

		Collection<LaraRow<LGeneralBehaviouralOption<TestAgent>>> utilityMatrixRows = new ArrayList<LaraRow<LGeneralBehaviouralOption<TestAgent>>>();
		// add behavioural options as row to matrix
		int[] utValues = { 0, 1, 2, 3, 5, 5, 5, 7, 8, 9 };
		bos = new LGeneralBehaviouralOption[10];

		TestAgent agent = new TestUtils.TestAgent("TestAgent");

		for (int i = 0; i < 10; i++) {
			bos[i] = new LGeneralBehaviouralOption<TestAgent>("" + i, agent,
					new HashMap<Class<? extends LaraPreference>, Double>()) {

				@Override
				public LGeneralBehaviouralOption<TestAgent> getModifiedBO(TestAgent agent,
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
			utilityMatrixRows.add(new LRow(bos[i], values));
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
		Set<? extends LaraBehaviouralOption> bosSet = heuristic.getKSelectedBOs(dBuilder, matrix, 3);
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
