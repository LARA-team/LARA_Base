/** 
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the 
 * terms of the GNU General Public License as published by the Free Software 
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.components.agents;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.eventbus.LaraEventSubscriber;

/**
 * The class uses recursive generics since the {@link LaraAgentComponent}
 * requires the type of agent. See Maurice, M. N. & Wadler, P. Java Generics and
 * Collections O'Reilly Media, 2006, p. 133ff.
 * 
 * See also http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.5 A Type parameter cannot be generic.
 * Furthermore, one cannot define more than one type in one ActualTypeArgument (T<S>). So one needs to workaround with
 * two ActualTypeArguments declarations (S extends LaraAgent, T extends Interface<S>).
 * 
 * Agents also work with BOs that require (only) a super class of their (agent)
 * class.
 * 
 * @param <A>
 *            the agent type of the implementing class
 * @param <BO>
 *            the type of behavioural options the implementing agent class works
 *            with
 * 
 */
public interface LaraAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ?>>
		extends LaraEventSubscriber {

	/**
	 * Returns the {@link LaraAgentComponent} of this agent.
	 * 
	 * @return component Lara agent component
	 */
	public LaraAgentComponent<A, BO> getLaraComp();

	/**
	 * Get the custom agent id.
	 * 
	 * @return agent id string
	 */
	public String getAgentId();
}