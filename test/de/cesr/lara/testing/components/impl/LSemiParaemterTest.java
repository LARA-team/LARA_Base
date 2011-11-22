package de.cesr.lara.testing.components.impl;

import java.util.Map;

import org.junit.Before;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.testing.components.impl.ParameterTest.AbstractEmployee;

public class LSemiParaemterTest {

	
	/***********************************************************************************************************
	 * BEHAVIOURAL OPTIONS
	 ***********************************************************************************************************/
	
	/**
	 * Example's base BO.
	 *  > Requires AbstractEmployee as "host"
	 *  > Requires its host only to support LBO or subclasses (minimal requirement)
	 *    (restricting the BO parameter is not useful since on the one hand
	 *    the agent is only required to support super-classes (? super ...), and on
	 *    the other hand certain agent classes may want to restrict BOs)
	 *  
	 * @param <A> the agent type this BO may deal with need to be a AbstractEmployee that may deal with 
	 * LaraBehaviouralOption (need to be a super class of CompanyBO because of storing)
	 * 
	 * @param <BO> the BO type, the agent type <A> may deal 
	 */
	static class CompanyBO<A extends LaraAgent<? super A, ?>, BO extends CompanyBO<A, ? extends BO>> extends
			LaraBehaviouralOption<A,BO> {
		
		/**
		 * @param key
		 * @param agent
		 */
		public CompanyBO(String key, A agent) {
			super(key, agent);
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getSituationalUtilities(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(LaraDecisionConfiguration dBuilder) {
			return null;
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getModifiedBO(de.cesr.lara.components.agents.LaraAgent, java.util.Map)
		 */
		@Override
		public BO getModifiedBO(A agent, Map<Class<? extends LaraPreference>, Double> utilities) {
			return null;
		}

		@Override
		public int compareTo(LaraBehaviouralOption<A,BO> o) {
			return 0;
		}
	}
	
	static class EmployeeBO<A extends EmployeeB<A, BO>, BO extends CompanyBO<A,BO>> extends
		CompanyBO<A,BO> {

		public EmployeeBO(String key, A agent) {
			super(key, agent);
		}
	}
	
	static class EmployeeBOB<A extends EmployeeB<A, EmployeeBOB<A>>> extends
	CompanyBO<A, EmployeeBOB<A>> {

	public EmployeeBOB(String key, A agent) {
		super(key, agent);
	}
}
	
	static class EmployeeBOC<A extends EmployeeC<CompanyBO<?,?>>> extends
	CompanyBO<A, EmployeeBOC<A>> {
	
		public EmployeeBOC(String key, A agent) {
			super(key, agent);
		}
	}
	
	/**
	 * Extends {@link CompanyBO}
	 *  > requires a {@link AbstractEmployee} or a subclass of {@link AbstractEmployee} as host (e.g. to calculate utility)
	 *  TODO why does Manager not work here??
	 *  
	 *  > requires its host to deal with CompanyBOs
	 */
	static class PowerBO<A extends Manager<? extends CompanyBO<?,?>>> extends
		CompanyBO<A, PowerBO<A>> {

		public PowerBO(String key, A agent) {
			super(key, agent);
		}
	}
	
	static class PowerBOB<A extends ManagerB> extends
	CompanyBO<A, PowerBOB<A>> {

	public PowerBOB(String key, A agent) {
		super(key, agent);
	}
}
	
	static class HireAndFireBo extends CompanyBO<Boss,HireAndFireBo> {

		public HireAndFireBo(String key, Boss agent) {
			super(key, agent);
		}
		
		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getTotalSituationalUtility(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public float getTotalSituationalUtility(LaraDecisionConfiguration dBuilder) {
			return getAgent().getHireAndFireUtility();
		}
		
	}
	
	
	/***********************************************************************************************************
	 * AGENTS
	 ***********************************************************************************************************/

	/**
	 * Example's agent base class
	 * 
	 * @param <A>
	 * @param <BO> BOs the agent class may deal with (must extends CompanyBO to be storable in this agent)
	 * 															A extends AbstractEmployee<A, ? super CompanyBO<A>>>
	 */
	static class Employee<A extends Employee<A, BO>, BO extends LaraBehaviouralOption<?,? extends BO>> implements
			LaraAgent<A, BO> {
		// static class AbstractEmployee<BO extends CompanyBO<AbstractEmployee<BO>>> implements
		// LaraAgent<AbstractEmployee<BO>, BO> {

		@Override
		public void clean(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public String getAgentId() {
			return null;
		}

		@Override
		public LaraAgentComponent<A, BO> getLaraComp() {
			return null;
		}

		@Override
		public void laraExecute(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public void laraPerceive(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public void laraPostProcess(LaraDecisionConfiguration ddBuilder) {
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}
		
		
	}
	
	static class EmployeeB<A extends Employee<A, BO>, BO extends LaraBehaviouralOption<?,? extends BO>> extends
		Employee<A, BO> {
	}
	
	static class EmployeeC<BO extends CompanyBO<?, ? extends BO>> extends
	Employee<EmployeeC<BO>, BO> {
}

	/**
	 * Manager is a subclass of {@link AbstractEmployee}
	 * 
	 * > requires {@link CompanyBO}s (i.e. may also deal with PowerBos)
	 * 
	 * @param <T> The agent type associated BOs work with
	 * @param <BO> The BO type
	 * 
	 * NOTE: It is important to use the wildcard for in <BO extends CompanyBO<Manager<?>>>
	 */
	static class Manager<BO extends CompanyBO<?, ? extends BO>> extends Employee<Manager<BO>, BO> {
		
		/**
		 * @return
		 */
		public float getInstructionsUtility() {
			return 0.5f;
		}
	}

	/**
	 * Manager is a subclass of {@link AbstractEmployee}
	 * 
	 * > requires {@link CompanyBO}s (i.e. may also deal with PowerBos)
	 * 
	 * @param <T> The agent type associated BOs work with
	 * @param <BO> The BO type
	 */
	static class ManagerB extends Employee<ManagerB, CompanyBO<?, ?>> {
		
		/**
		 * @return
		 */
		public float getInstructionsUtility(CompanyBO<ManagerB, ?> bo) {
			return 0.5f;
		}
	}
	
	/**
	 * > requires {@link HireAndFireBo}s
	 * >> PowerBO is not possible here since it is not fully defined (requires also BO parameter)
	 */
	static class Boss extends Manager<CompanyBO<?,?>> {

		/**
		 * @return 1.0
		 */
		public float getHireAndFireUtility() {
			return 1.0f;
		}
	}
	
	/**
	 * > requires {@link PowerBO}s
	 */
	static class SuperBoss extends Manager<PowerBO<Boss>> {

		/**
		 * @return 1.0
		 */
		public float getHireAndFireUtility() {
			return 1.0f;
		}
	}

	LaraDecisionConfiguration dBuilder;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		dBuilder = new LDecisionConfiguration();
	}


	public void test() {
		//Manager manager = new Manager<CompanyBO<?>>();

		Boss boss = new Boss();
		boss.laraExecute(dBuilder);
		boss.getLaraComp().getBOMemory().memorize(new HireAndFireBo("test", boss));
		
		Manager<CompanyBO<?,?>> manager = new Manager<CompanyBO<?,?>>();

		PowerBO<Manager<CompanyBO<?,?>>> powerbo = new PowerBO<Manager<CompanyBO<?,?>>>("Test",manager);
		
		manager.getLaraComp().getBOMemory().memorize(powerbo);

		
		PowerBO<Boss> bossPowerbo = new PowerBO<Boss>("Test",boss); // should work but does not as long as 
		
		boss.getLaraComp().getBOMemory().memorize(bossPowerbo);  // does not work since Boss only accepts HireAndFireBo
		
		SuperBoss superboss = new SuperBoss();
		superboss.getLaraComp().getBOMemory().memorize(bossPowerbo);
	}

}