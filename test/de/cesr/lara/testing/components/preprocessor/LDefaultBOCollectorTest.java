/**
 * LARA - Lightweight Architecture for bounded Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 12.02.2010
 */
package de.cesr.lara.testing.components.preprocessor;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.container.memory.LaraBOMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityBOMemory;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionHeuristicComponent_MaxLineTotalRandomAtTie;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.impl.LGeneralBehaviouralOption;
import de.cesr.lara.components.preprocessor.LaraBOCollector;
import de.cesr.lara.components.preprocessor.impl.LContributingBOPreselector;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.testing.TestUtils.TestAgent;


/**
 * 
 * TODO complement tests and add to AllPreprocessorTests! (SH) > SH
 * 
 * @author Sascha Holzhauer
 * @date 12.02.2010
 * 
 */
public class LDefaultBOCollectorTest {

	TestAgent															agent;
	LaraBOMemory<LGeneralBehaviouralOption<TestAgent>>					memory;

	LaraPreference														goal1;
	LaraPreference														goal2;
	LaraPreference														goal3;

	/**
	 * Does not contribute to any goal
	 */
	LGeneralBehaviouralOption<TestAgent>								bo1;
	/**
	 * Contributes to goal1 by 0.0
	 */
	LGeneralBehaviouralOption<TestAgent>								bo2;
	/**
	 * Contributes to goal1 by 1.0 and goal2 by 0.0
	 */
	LGeneralBehaviouralOption<TestAgent>								bo3;

	LaraDecisionConfiguration													dBuilder;

	LaraBOCollector<TestAgent, LGeneralBehaviouralOption<TestAgent>>	scanner;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 12.02.2010
	 */
	@SuppressWarnings("unchecked")
	// type parameter for agent does not matter
	@Before
	public void setUp() throws Exception {
		Class<? extends LaraPreference> goal1 = new LaraPreference() {
		}.getClass();
		Class<? extends LaraPreference> goal2 = new LaraPreference() {
		}.getClass();

		LaraEnvironment env = new LEnvironment();
		agent = new TestAgent("TestAgent");
		Map<Class<? extends LaraPreference>, Double> utilities = new HashMap<Class<? extends LaraPreference>, Double>();
		bo1 = new LGeneralBehaviouralOption<TestAgent>(agent, utilities);
		utilities.put(goal1, 0.0);
		bo2 = new LGeneralBehaviouralOption<TestAgent>(agent, utilities);
		utilities.put(goal1, 1.0);
		utilities.put(goal2, 0.0);
		bo3 = new LGeneralBehaviouralOption<TestAgent>(agent, utilities);

		memory = new LDefaultLimitedCapacityBOMemory<LGeneralBehaviouralOption<TestAgent>>(LCapacityManagers
				.<LGeneralBehaviouralOption<TestAgent>> makeNINO());

		dBuilder = new LDecisionConfiguration("TestDecision",
				new LDecisionHeuristicComponent_MaxLineTotalRandomAtTie());
		List<Class<? extends LaraPreference>> goals = new ArrayList<Class<? extends LaraPreference>>();
		goals.add(goal1);
		goals.add(goal2);
		dBuilder.setPreferences(goals);

		scanner = new LContributingBOPreselector<TestAgent, LGeneralBehaviouralOption<TestAgent>>();
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 12.02.2010
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.preprocessor.impl.LContributingBOPreselector#collectBOs(LaraAgent, LaraBOMemory, LaraDecisionConfiguration)}
	 */
	@Test
	public final void testGetBOs() {
		assertEquals("No BO in memory contributes to the decision's preferenceWeights (since there is no bo inserted)", 0, scanner
				.collectBOs(agent, memory, dBuilder).size());
		memory.memorize(bo1);
		assertEquals(
				"No BO in memory contributes to the decision's preferenceWeights (since bo1 is inserted but has no utility for any goal)",
				0, scanner.collectBOs(agent, memory, dBuilder).size());
		memory.memorize(bo2);
		assertEquals(
				"1 BO in memory contributes to the decision's preferenceWeights (since bo2 is inserted but has utility 0.0 for goal1)",
				1, scanner.collectBOs(agent, memory, dBuilder).size());
		memory.clear();
		memory.memorize(bo2);
		memory.memorize(bo3);
		assertEquals(
				"2 BO in memory contribute to the decision's preferenceWeights (since bo2 with utility 0.0 for goal1 and bo3 with 1.0 for "
						+ "goal1 and 0.0 for goal2 are inserted)", 2, scanner.collectBOs(agent, memory, dBuilder)
						.size());
	}
}
