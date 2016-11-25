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
package de.cesr.lara.components.decision.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraDecider;
import de.cesr.lara.components.decision.LaraDecisionMode;


/**
 * Kind of pseudo decider when there is no {@link LaraBehaviouralOption} to choose from.
 * 
 * @author Sascha Holzhauer
 * @param <BO>
 * 
 */
public class LNoOptionDecider<BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraDecider<BO> {

	/**
	 * Logger
	 */
	static private Logger logger = Logger.getLogger(LNoOptionDecider.class);

	LaraDecisionMode mode;

	public LNoOptionDecider(LaraDecisionMode mode) {
		this.mode = mode;
	}

	@Override
	public void decide() {
		// nothing to do
	}


	@Override
	public int getNumSelectableBOs() {
		return 0;
	}

	@Override
	public BO getSelectedBo() {
		logger.warn("The LNoOptionDecider has no BO available and returns null!");
		return null;
	}

	@Override
	public LaraDecisionMode getDecisionMode() {
		return this.mode;
	}

	@Override
	public Collection<BO> getSelectableBos() {
		return new ArrayList<>();
	}

	@Override
	public List<BO> getSelectedBos() {
		return new ArrayList<>();
	}

	@Override
	public void setSelectedBos(List<BO> selectedBos) {
		// nothing to do
	}
}
