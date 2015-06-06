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

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.decision.LaraDecisionTree;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.LaraDecisionModeSelector;

/**
 * Applies {@link LaraDecisionTree} to select decision mode.
 * 
 * This is especially helpful for configurations via XML deserialisation.
 * 
 * TODO test and implement LTreeDecisionModeSelector
 * 
 * @author Sascha Holzhauer
 * 
 * @param <A> 
 * @param <BO> 
 * 
 */
public class LTreeDecisionModeSelector<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		extends LAbstractPpComp<A, BO> implements
		LaraDecisionModeSelector<A, BO> {

	@Override
	public void onInternalEvent(LaraEvent event) {
		// TODO Auto-generated method stub

	}

}
