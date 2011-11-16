/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 21.02.2011
 */
package de.cesr.lara.testing.components.impl;


import java.util.Map;

import org.junit.After;
import org.junit.Before;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;


/**
 * CompanyBO<AgentT extends AbstractEmployee<? super CompanyBo> extends LaraBehaviouralOption<AgentT>
 */
public class ParameterTest {

	// static class CompanyBO<AgentT extends AbstractEmployee<? extends CompanyBO<AgentT>> extends
	// LaraBehaviouralOption<AgentT> {
	// static class CompanyBO<T extends LaraAgent<T, BO>, BO extends CompanyBO<T, BO>> extends LaraBehaviouralOption<T,
	// BO> {
	// works A (static class): CompanyBO<A extends AbstractEmployee<A>> extends LaraBehaviouralOption<A, CompanyBO<A>> {
	// works B (any BO for AbstractEmployee (here any, that extends CompanyBO):
	// class CompanyBO<A extends AbstractEmployee<A, ? super CompanyBO<A>>> extends LaraBehaviouralOption<A,
	// CompanyBO<A>> {
	// works B (any LaraAgent for CompanyBO, any BO for AbstractEmployee (here any, that extends CompanyBO):
	static class CompanyBO<A extends LaraAgent<A, BO>, BO extends CompanyBO<A, BO>> extends
			LaraBehaviouralOption<A, BO> {
		// ///////
		// static class CompanyBO<AgentT extends LaraAgent<AgentT, ? extends LaraBehaviouralOption<AgentT>>> extends
		// LaraBehaviouralOption<AgentT> {

		public CompanyBO(String key, A agent) {
			super(key, agent);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(LaraDecisionConfiguration dBuilder) {
			return null;
		}

		@Override
		public BO getModifiedBO(A agent, Map<Class<? extends LaraPreference>, Double> utilities) {
			return null;
		}
		// subclassen von Employee, die als BO mindestens ComponyBO brauchen
	}

	// static class AbstractEmployee<T extends AbstractEmployee<T, BO>, BO extends CompanyBO<AbstractEmployee<T, BO>,
	// BO>> implements LaraAgent<AbstractEmployee<T, BO>, BO> {
	// works A (concrete BO class) : static class AbstractEmployee<A extends AbstractEmployee<A>> implements
	// LaraAgent<A, CompanyBO<A>> {
	// works B (any class for BO) : static class AbstractEmployee<A extends AbstractEmployee<A, BO>, BO extends
	// LaraBehaviouralOption <A,BO>> implements LaraAgent<A, BO> {
	// works C (CompanyBO for BO) : static class AbstractEmployee<A extends AbstractEmployee<A, BO>, BO extends
	// CompanyBO < A , BO>> implements LaraAgent<A, BO> {
	// works D (CompanyBO for BO, super A for CompanyBO)
	static class AbstractEmployee<A extends AbstractEmployee<A, BO>, BO extends CompanyBO<? super A, BO>> implements
			LaraAgent<A, BO> {
		// static class AbstractEmployee<BO extends CompanyBO<AbstractEmployee<BO>>> implements
		// LaraAgent<AbstractEmployee<BO>, BO> {

		@Override
		public String getAgentId() {
			return null;
		}

		@Override
		public LaraAgentComponent<A, BO> getLaraComp() {
			return null;
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
			// TODO Auto-generated method stub
			
		}

	}

	// agent subclass that uses BOs that require a super class agent type:
	static class SubLeader extends Leader<SubLeader, PowerBO> {

	}

	// Leader work with CompanyBOs
	static class Leader<T extends Leader<T, BO>, BO extends CompanyBO<? super T, BO>> extends AbstractEmployee<T, BO> {

	}

	// // Leader work with CompanyBOs
	// static class Leader extends AbstractEmployee < Leader , CompanyBO<Leader>> {
	//		
	// }

	// PowerBOs require a Leader:
	static class PowerBO extends CompanyBO<SubLeader, PowerBO> {

		public PowerBO(String key, SubLeader agent) {
			super(key, agent);
		}

	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	public void test() {

	}

}
