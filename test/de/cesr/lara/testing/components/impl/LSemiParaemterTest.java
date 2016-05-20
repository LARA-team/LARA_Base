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
import de.cesr.lara.testing.components.impl.LParameterTest.AbstractEmployee;

public class LSemiParaemterTest {

	/***********************************************************************************************************
	 * BEHAVIOURAL OPTIONS
	 ***********************************************************************************************************/

	/**
	 * > requires {@link HireAndFireBo}s >> PowerBO is not possible here since
	 * it is not fully defined (requires also BO parameter)
	 */
	static class Boss extends Manager<CompanyBO<?, ?>> {

		/**
		 * @return 1.0
		 */
		public float getHireAndFireUtility() {
			return 1.0f;
		}
	}

	/**
	 * Example's base BO. > Requires AbstractEmployee as "host" > Requires its
	 * host only to support LBO or subclasses (minimal requirement) (restricting
	 * the BO parameter is not useful since on the one hand the agent is only
	 * required to support super-classes (? super ...), and on the other hand
	 * certain agent classes may want to restrict BOs)
	 * 
	 * @param <A>
	 *            the agent type this BO may deal with need to be a
	 *            AbstractEmployee that may deal with LaraBehaviouralOption
	 *            (need to be a super class of CompanyBO because of storing)
	 * 
	 * @param <BO>
	 *            the BO type, the agent type <A> may deal
	 */
	static class CompanyBO<A extends LaraAgent<? super A, ?>, BO extends CompanyBO<A, ?>>
			extends LaraBehaviouralOption<A, BO> {

		/**
		 * @param key
		 * @param agent
		 */
		public CompanyBO(String key, A agent) {
			super(key, agent);
		}

		@Override
		public int compareTo(LaraBehaviouralOption<?, ?> o) {
			return 0;
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getModifiedBO(de.cesr.lara.components.agents.LaraAgent,
		 *      java.util.Map)
		 */
		@Override
		public BO getModifiedBO(A agent,
 Map<LaraPreference, Double> utilities) {
			return null;
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getSituationalUtilities(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public Map<LaraPreference, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return null;
		}
	}

	/**
	 * Example's agent base class
	 * 
	 * @param <A>
	 * @param <BO>
	 *            BOs the agent class may deal with (must extends CompanyBO to
	 *            be storable in this agent) A extends AbstractEmployee<A, ?
	 *            super CompanyBO<A>>>
	 */
	static class Employee<A extends Employee<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			implements LaraAgent<A, BO> {
		// static class AbstractEmployee<BO extends
		// CompanyBO<AbstractEmployee<BO>>> implements
		// LaraAgent<AbstractEmployee<BO>, BO> {

		@Override
		public String getAgentId() {
			return null;
		}

		@Override
		public LaraAgentComponent<A, BO> getLaraComp() {
			return null;
		}

		public void laraExecute(LaraDecisionConfiguration dBuilder) {
		}

		@Override
		public <T extends LaraEvent> void onEvent(T event) {
		}

	}

	static class EmployeeB<A extends Employee<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
			extends Employee<A, BO> {
	}

	static class EmployeeBO<A extends EmployeeB<A, BO>, BO extends CompanyBO<A, BO>>
			extends CompanyBO<A, BO> {

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

	static class EmployeeBOC<A extends EmployeeC<CompanyBO<?, ?>>> extends
			CompanyBO<A, EmployeeBOC<A>> {

		public EmployeeBOC(String key, A agent) {
			super(key, agent);
		}
	}

	/***********************************************************************************************************
	 * AGENTS
	 ***********************************************************************************************************/

	static class EmployeeC<BO extends CompanyBO<?, ? extends BO>> extends
			Employee<EmployeeC<BO>, BO> {
	}

	static class HireAndFireBo extends CompanyBO<Boss, HireAndFireBo> {

		public HireAndFireBo(String key, Boss agent) {
			super(key, agent);
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getTotalSituationalUtility(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public float getTotalSituationalUtility(
				LaraDecisionConfiguration dBuilder) {
			return getAgent().getHireAndFireUtility();
		}

	}

	/**
	 * Manager is a subclass of {@link AbstractEmployee}
	 * 
	 * > requires {@link CompanyBO}s (i.e. may also deal with PowerBos)
	 * 
	 * @param <T>
	 *            The agent type associated BOs work with
	 * @param <BO>
	 *            The BO type
	 * 
	 *            NOTE: It is important to use the wildcard for in <BO extends
	 *            CompanyBO<Manager<?>>>
	 */
	static class Manager<BO extends CompanyBO<?, ? extends BO>> extends
			Employee<Manager<BO>, BO> {

		/**
		 * @return instructions utility
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
	 * @type <ManagerB> The agent type associated BOs work with
	 * @type <CompanyBO> The BO type
	 */
	static class ManagerB extends Employee<ManagerB, CompanyBO<?, ?>> {

		/**
		 * @param bo
		 *            behavioural option
		 * @return instructions utility
		 */
		public float getInstructionsUtility(CompanyBO<ManagerB, ?> bo) {
			return 0.5f;
		}
	}

	/**
	 * Extends {@link CompanyBO} > requires a {@link AbstractEmployee} or a
	 * subclass of {@link AbstractEmployee} as host (e.g. to calculate utility)
	 * TODO why does Manager not work here??
	 * 
	 * > requires its host to deal with CompanyBOs
	 */
	static class PowerBO<A extends Manager<? extends CompanyBO<?, ?>>> extends
			CompanyBO<A, PowerBO<A>> {

		public PowerBO(String key, A agent) {
			super(key, agent);
		}
	}

	static class PowerBOB<A extends ManagerB> extends CompanyBO<A, PowerBOB<A>> {

		public PowerBOB(String key, A agent) {
			super(key, agent);
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
		// Manager manager = new Manager<CompanyBO<?>>();

		Boss boss = new Boss();
		boss.laraExecute(dBuilder);
		boss.getLaraComp().getBOMemory()
				.memorize(new HireAndFireBo("test", boss));

		Manager<CompanyBO<?, ?>> manager = new Manager<CompanyBO<?, ?>>();

		PowerBO<Manager<CompanyBO<?, ?>>> powerbo = new PowerBO<Manager<CompanyBO<?, ?>>>(
				"Test", manager);

		manager.getLaraComp().getBOMemory().memorize(powerbo);

		PowerBO<Boss> bossPowerbo = new PowerBO<Boss>("Test", boss); // should
																		// work
																		// but
																		// does
																		// not
																		// as
																		// long
																		// as

		boss.getLaraComp().getBOMemory().memorize(bossPowerbo); // does not work
																// since Boss
																// only accepts
																// HireAndFireBo

		SuperBoss superboss = new SuperBoss();
		superboss.getLaraComp().getBOMemory().memorize(bossPowerbo);
	}

}