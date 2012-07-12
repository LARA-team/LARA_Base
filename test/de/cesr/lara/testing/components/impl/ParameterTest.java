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

import org.junit.After;
import org.junit.Before;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraAgentComponent;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.eventbus.events.LaraEvent;

/**
 * 
 * This test class illustrates the parameterisation of agent classes and
 * behavioural option classes.
 * 
 * Agents: AbstractEmployeeAgent implements LaraAgent Manager requires PowerBOs
 * (has memory that may only store PowerBOs) Boss extends Manager
 * 
 * BOs: CompanyBO PowerBO HireAndFireBo
 * 
 * CompanyBO<AgentT extends AbstractEmployee<? super CompanyBo> extends
 * LaraBehaviouralOption<AgentT>
 * 
 * @author Sascha Holzhauer
 * @date 21.02.2011
 */
public class ParameterTest {

	/***********************************************************************************************************
	 * BEHAVIOURAL OPTIONS
	 ***********************************************************************************************************/

	/**
	 * Example's agent base class
	 * 
	 * @param <A>
	 * @param <BO>
	 *            BOs the agent class may deal with (must extends CompanyBO to
	 *            be storable in this agent) A extends AbstractEmployee<A, ?
	 *            super CompanyBO<A>>>
	 */
	static class AbstractEmployee<A extends AbstractEmployee<A, BO>, BO extends LaraBehaviouralOption<A, BO>>
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

	/**
	 * > requires {@link HireAndFireBo}s >> PowerBO is not possible here since
	 * it is not fully defined (requires also BO parameter)
	 */
	static class Boss extends AbstractEmployee<Boss, HireAndFireBo> {

		/**
		 * @return 1.0
		 */
		public float getHireAndFireUtility() {
			return 1.0f;
		}
	}

	/**
	 * Example's base BO. > Requires AbstractEmployee as "host" > Requires its
	 * host only to support LBO or subclasses (minimal requirement)
	 * 
	 * @param <A>
	 *            the agent type this BO may deal with need to be a
	 *            AbstractEmployee that may deal with LaraBehaviouralOption
	 *            (need to be a super class of CompanyBO because of storing)
	 * 
	 * @param <BO>
	 *            the BO type, the agent type <A> may deal
	 */
	static class CompanyBO<A extends AbstractEmployee<A, BO>, BO extends LaraBehaviouralOption<A, BO>>
			extends LaraBehaviouralOption<A, BO> {

		/**
		 * @param key
		 * @param agent
		 */
		public CompanyBO(String key, A agent) {
			super(key, agent);
		}

		@Override
		public int compareTo(LaraBehaviouralOption<A, BO> o) {
			return 0;
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getModifiedBO(de.cesr.lara.components.agents.LaraAgent,
		 *      java.util.Map)
		 */
		@Override
		public BO getModifiedBO(A agent,
				Map<Class<? extends LaraPreference>, Double> utilities) {
			return null;
		}

		/**
		 * @see de.cesr.lara.components.LaraBehaviouralOption#getSituationalUtilities(de.cesr.lara.components.decision.LaraDecisionConfiguration)
		 */
		@Override
		public Map<Class<? extends LaraPreference>, Double> getSituationalUtilities(
				LaraDecisionConfiguration dBuilder) {
			return null;
		}
	}

	/***********************************************************************************************************
	 * AGENTS
	 ***********************************************************************************************************/

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
	 */
	static class Manager<BO extends CompanyBO<Manager<BO>, BO>> extends
			AbstractEmployee<Manager<BO>, BO> {

		/**
		 * @return
		 */
		public float getInstructionsUtility() {
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
	static class PowerBO<A extends AbstractEmployee<A, BO>, BO extends CompanyBO<A, BO>>
			extends CompanyBO<A, BO> {

		public PowerBO(String key, A agent) {
			super(key, agent);
		}
	}

	// PowerBOs require a Leader:

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
}
