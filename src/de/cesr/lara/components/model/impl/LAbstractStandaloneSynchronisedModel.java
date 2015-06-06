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
package de.cesr.lara.components.model.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * The class provides a default implementation of {@link LModel} that does not
 * depend on any MAS framework.
 * 
 * Instructions: Implement createAgents() to create your agents and call
 * addAgent(agent) for every created agent. Fill the method getAgentIterable()
 * that returns an Iterable over your agents.
 * 
 * @author Sascha Holzhauer
 * @date 09.11.2009
 * 
 */
public abstract class LAbstractStandaloneSynchronisedModel extends
		LAbstractModel {

	private static Logger logger = Log4jLogger
			.getLogger(LAbstractStandaloneSynchronisedModel.class);


	protected Collection<LaraAgent<?, ?>> agents = new ArrayList<LaraAgent<?, ?>>();

	public LAbstractStandaloneSynchronisedModel() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 */
	public LAbstractStandaloneSynchronisedModel(Object id) {
		super(id);
	}

	/**
	 * 
	 * @param agent
	 */
	public void addAgent(LaraAgent<?, ?> agent) {
		agents.add(agent);
	}

	/**
	 * 
	 * @see de.cesr.lara.components.model.impl.LAbstractModel#init()
	 */
	@Override
	public void init() {
		super.init();
		
		agents = new ArrayList<LaraAgent<?, ?>>();

		// <- LOGGING
		if (logger.isDebugEnabled()) {
			logger.debug("LAbstractStandaloneSynchronisedModel " + this
					+ " initialised.");
		}
		// LOGGING ->
	}
}