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
package de.cesr.lara.components.model;

import de.cesr.lara.components.model.impl.LModel;

/**
 * This was introduced for a component in LARA Toolbox to be notified of reset
 * events (LPersister).
 * 
 * @author Sascha Holzhauer
 * 
 */
public interface LaraModelResetObserver {

	/**
	 * Called by {@link LModel} when {@link LModel#resetModel(Object id)} was
	 * called.
	 * 
	 * @param id
	 */
	public void getNotified(Object id);

	/**
	 * Called by {@link LModel} when {@link LModel#reset()} was called.
	 */
	public void getNotified();

}
