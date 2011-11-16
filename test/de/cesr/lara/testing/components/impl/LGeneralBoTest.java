/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 11.02.2010
 */
package de.cesr.lara.testing.components.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraRandom;
import de.cesr.lara.components.util.impl.LRandomService;
import de.cesr.lara.testing.TestUtils;
import de.cesr.lara.testing.TestUtils.TestAgent;

/**
 * 
 * @author Sascha Holzhauer
 * @date 11.02.2010
 * 
 */
public class LGeneralBoTest {

	private static class TestGoal implements LaraPreference {

	}

	TestAgent agent1;
	TestAgent agent2;
	Class<? extends LaraPreference> goal1;
	LGeneralBehaviouralOption<TestAgent> bo1;
	LGeneralBehaviouralOption<TestAgent> bo2;
	LaraEnvironment env;
	Map<Class<? extends LaraPreference>, Double> utilities1;

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 11.02.2010
	 */
	@Before
	public void setUp() throws Exception {
		LModel.setNewModel(new LAbstractStandaloneSynchronisedModel() {

			@Override
			public LaraRandom getLRandom() {
				return new LRandomService((int) System.currentTimeMillis());
			}

			@Override
			public <T extends LaraEvent> void onInternalEvent(T event) {
				// TODO Auto-generated method stub

			}
		});
		env = new LEnvironment();
		agent1 = new TestUtils.TestAgent("TestAgent1");
		agent2 = new TestUtils.TestAgent("TestAgent2");
		utilities1 = new HashMap<Class<? extends LaraPreference>, Double>();
		goal1 = TestGoal.class;
		utilities1.put(goal1, new Double(1.0));
		bo1 = new LGeneralBehaviouralOption<TestAgent>("BO1", agent1,
				utilities1);
		bo2 = new LGeneralBehaviouralOption<TestAgent>("BO2", agent1,
				utilities1);

	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 11.02.2010
	 */
	@After
	public void tearDown() throws Exception {
		env = null;
		agent1 = null;
		utilities1 = null;
		bo1 = null;
		bo2 = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.impl.AbstractBO#getAgent()}.
	 */
	@Test
	public final void testGetAgent() {
		assertEquals("Returned agent should be agent", agent1, bo1.getAgent());
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.impl.AbstractBO#getValue()}.
	 */
	@Test(expected = UnsupportedOperationException.class)
	public final void testGetValue() {
		assertEquals("Returned value should be utilities1", utilities1,
				bo1.getValue());
		bo1.getValue();
		assertEquals("Returmed map should contain the inserted value for goal",
				1.0, bo1.getValue().get(goal1), 0.1);

		assertEquals("Returmed map should contain the inserted value for goal",
				1.0, bo1.getValue().get(TestGoal.class), 0.1);

		bo1.getValue().put(TestGoal.class, new Double(Double.NaN));
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.impl.AbstractBO#getModifiableUtilities()}.
	 */
	@Test
	public final void testGetModifiableUtilities() {
		Map<Class<? extends LaraPreference>, Double> utilities2 = bo1
				.getModifiableUtilities();

		assertEquals(
				"Returned map should contain same key-value-mappings as utilities1",
				utilities1, utilities2);
	}

	/**
	 * 
	 */
	@Test
	public final void testEquals() {
		assertFalse(bo1.equals(bo2));

		assertTrue(bo1.equals(bo1));

		LGeneralBehaviouralOption<TestAgent> bo12 = new LGeneralBehaviouralOption<TestAgent>(
				"BO1", agent2, utilities1);

		assertTrue(bo1.getModifiedBO(bo12.getAgent(), bo12.getValue()).equals(
				bo12));
		assertTrue(bo1.getModifiedAgentBO(bo12.getAgent()).equals(bo12));

		assertFalse(bo1.getModifiedUtilitiesBO(bo2.getValue()).equals(bo12));

		assertTrue(bo1.equals(new LGeneralBehaviouralOption<TestAgent>("BO1",
				agent1, utilities1)));
	}

	/**
	 * 
	 */
	@Test
	public final void testHashCode() {
		assertEquals("hash codes of equal objects need to be equal",
				bo1.hashCode(), bo1.hashCode());
		LGeneralBehaviouralOption<TestAgent> bo12 = new LGeneralBehaviouralOption<TestAgent>(
				"BO1", agent2, utilities1);
		assertEquals("hash codes of equal objects need to be equal",
				bo1.hashCode(), new LGeneralBehaviouralOption<TestAgent>("BO1",
						agent1, utilities1).hashCode());
		assertEquals("hash codes of equal objects need to be equal", bo1
				.getModifiedAgentBO(bo12.getAgent()).hashCode(),
				bo12.hashCode());
		assertEquals("hash codes of equal objects need to be equal", bo1
				.getModifiedBO(bo12.getAgent(), bo12.getValue()).hashCode(),
				bo12.hashCode());
	}

	/**
	 * Test method for {@link de.cesr.lara.components.impl.AbstractBO#getKey()}.
	 */
	@Test
	public final void testGetKey() {
		assertEquals("Returned key shoud be BO1", "BO1", bo1.getKey());
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.impl.AbstractBO#getModifiedAgentBO(de.cesr.lara.components.agents.LaraAgent)}
	 * .
	 */
	@Test
	public final void testGetModifiedAgentBO() {
		bo2 = bo1.getModifiedAgentBO(agent2);
		assertEquals("Return BO should contain the new agent2", agent2,
				bo2.getAgent());
		assertEquals("The returned BO should contain the same key",
				bo1.getKey(), bo2.getKey());
		assertEquals("The returned BO should contain the same utilities",
				bo1.getValue(), bo2.getValue());
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.impl.AbstractBO#getModifiedUtilitiesBO(java.util.Map)}
	 * .
	 */
	@Test
	public final void testGetModifiedUtilitiesBO() {
		Map<Class<? extends LaraPreference>, Double> utilities2 = bo1
				.getModifiableUtilities();
		utilities2.put(TestGoal.class, new Double(2.0));
		bo2 = bo1.getModifiedUtilitiesBO(utilities2);
		assertEquals("Return BO should contain the same agent2",
				bo1.getAgent(), bo2.getAgent());
		assertEquals("The returned BO should contain the same key",
				bo1.getKey(), bo2.getKey());
		assertEquals("The returned BO should contain utilities2", utilities2,
				bo2.getValue());
		assertEquals("utility value of goal of bo2 is 2.0", 2.0, bo2.getValue()
				.get(TestGoal.class), 0.1);
	}

	/**
	 * 
	 */
	@Test
	public final void testUnauthorizedModification() {
		assertEquals("The utilities map should contain only one entry", 1, bo1
				.getValue().size());
		utilities1.put(LaraPreference.class, new Double(42.0));
		assertEquals("The utilities map should contain only one entry", 1, bo1
				.getValue().size());
		utilities1.put(goal1, new Double(42.0));
		assertEquals("The utility for 'goal1' should be 1.0", 1.0, bo1
				.getValue().get(goal1).doubleValue(), 0.1);
	}

}
