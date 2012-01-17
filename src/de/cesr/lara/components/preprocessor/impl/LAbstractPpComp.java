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
package de.cesr.lara.components.preprocessor.impl;

import java.text.DecimalFormat;
import java.util.Collection;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraPreprocessorComp;
import de.cesr.lara.components.preprocessor.event.LaraPpEvent;
import de.cesr.lara.components.util.logging.impl.LAgentLevel;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * @author Sascha Holzhauer
 * 
 * @param <A>
 * @param <BO>
 */
public abstract class LAbstractPpComp<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?, ?>> 
		implements LaraPreprocessorComp<A, BO> {
	
	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LAbstractPpComp.class);

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraPreprocessorComp#castEvent(java.lang.Class,
	 *      de.cesr.lara.components.eventbus.events.LaraEvent)
	 */
	@Override
	public <E extends LaraPpEvent> E castEvent(Class<E> clazz, LaraEvent event) {
		if (clazz.isInstance(event)) {
			return clazz.cast(event);
		} else {
			logger.error("The preprocessor component (" + this
					+ "( may not be subscribed for this event: " + event
					+ "! The component is suitable for events of type " + clazz);
			throw new IllegalStateException("The preprocessor component ("
					+ this + "( may not be subscribed " + "for this event: "
					+ event + "! The component is suitable for events of type "
					+ clazz);
		}
	}

	/**
	 * 
	 * TODO check
	 * 
	 * Logs BOs to agentLogger
	 * (<agent>.de.cesr.lara.components.preprocessor.impl.LPreprocessor) if its
	 * not null and to logger if debug is enabled otherwise.
	 * 
	 * @param bos
	 *            BOs to log
	 * @param desc
	 *            description that comments to BOs origin
	 * @param agent
	 */
	protected void logBOs(Logger logger, Collection<BO> bos, String desc,
			A agent) {

		Logger agentLogger = null;

		// initialise agent specific logger (agent id is first part of logger
		// name):
		if (Log4jLogger.getLogger(
				agent.getAgentId() + "." + LPreprocessor.class.getName())
				.isEnabledFor(LAgentLevel.AGENT)) {
			agentLogger = Log4jLogger.getLogger(agent.getAgentId() + "."
					+ LPreprocessor.class.getName());
		}

		StringBuffer buffer = new StringBuffer();
		buffer.append(desc + "for " + agent + ":"
				+ System.getProperty("line.separator"));
		int i = 0;
		for (BO bo : bos) {
			buffer.append(new DecimalFormat("00").format(i) + "th BO: " + bo
					+ System.getProperty("line.separator"));
			i++;
		}
		if (agentLogger != null) {
			agentLogger.debug(buffer);
		} else if (logger.isDebugEnabled()) {
			logger.debug(buffer);
		}
	}
}
