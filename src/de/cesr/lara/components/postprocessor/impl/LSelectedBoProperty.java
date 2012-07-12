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
package de.cesr.lara.components.postprocessor.impl;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.decision.LaraDecisionConfiguration;

/**
 * @author Sascha Holzhauer
 * 
 */
public class LSelectedBoProperty<BO extends LaraBehaviouralOption<?, ?>>
		extends LaraProperty<LSelectedBoProperty<BO>, BO> {

	protected BO bo;
	protected LaraDecisionConfiguration dConfig;

	/**
	 * @param dConfig
	 * @param bo
	 */
	public LSelectedBoProperty(LaraDecisionConfiguration dConfig, BO bo) {
		super(dConfig.getId());
		this.bo = bo;
		this.dConfig = dConfig;
	}

	/**
	 * @return decision configuration of this selected BO property
	 */
	public LaraDecisionConfiguration getDConfig() {
		return this.dConfig;
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getModifiedProperty(java.lang.Object)
	 */
	@Override
	public LSelectedBoProperty<BO> getModifiedProperty(BO value) {
		return new LSelectedBoProperty<BO>(this.dConfig, bo);
	}

	/**
	 * @see de.cesr.lara.components.LaraProperty#getValue()
	 */
	@Override
	public BO getValue() {
		return this.bo;
	}
}
