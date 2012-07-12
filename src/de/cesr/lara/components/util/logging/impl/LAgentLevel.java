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
package de.cesr.lara.components.util.logging.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

/**
 * Custom Log4j Level that is supposed to be used for agent loggers (see
 * Doc_AgentLogging.doc). By adjusting the priority it is possible to decide for
 * which logging configuration agent loggers are instantiated.
 * 
 * @author Sascha Holzhauer
 * @date 21.09.2010
 * 
 */
public class LAgentLevel extends Level {

	/**
	 * An Log4J level used for agent loggers.
	 */
	public static final LAgentLevel AGENT = new LAgentLevel(
			Priority.DEBUG_INT - 10, "AGENT", 0);

	public static LAgentLevel toLevel(int val, Level defaultLevel) {
		return AGENT;
	}

	public static LAgentLevel toLevel(String sArg, Level defaultLevel) {
		return AGENT;
	}

	/**
	 * @param level
	 * @param levelStr
	 * @param syslogEquivalent
	 */
	protected LAgentLevel(int level, String levelStr, int syslogEquivalent) {
		super(level, levelStr, syslogEquivalent);
	}
	
}