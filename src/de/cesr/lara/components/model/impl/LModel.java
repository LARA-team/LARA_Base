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

import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.impl.LVersionInfo;

/**
 * Base class for models using LARA that should ensure that only one instance of
 * LaraModel is created. However, since it should be possible to create
 * subclasses of {@link LAbstractModel} it is not possible to ensure that there
 * are no more than one instances of {@link LaraModel}.
 * 
 * @note tasks: initialise model context, initialise database, read config,
 *       setup simulation, setup agents, setup logger, simulate time stamp
 * @author Daniel Klemm
 * @author Sascha Holzhauer
 */
public final class LModel {

	/**
	 * model
	 */
	protected static LaraModel model;

	/**
	 * @return model
	 */
	public static LaraModel getModel() {
		if (model == null) {
			throw new IllegalStateException("model was not set");
		}
		return model;
	}

	/**
	 * Returns a string containing current SVN revision number and data and time
	 * of last build.
	 * 
	 * @return version information
	 */
	public static String getVersionInfo() {
		return "LARA: " + LVersionInfo.revisionNumber + "/"
				+ LVersionInfo.timeStamp;
	}

	/**
	 * @param model
	 * @return new model
	 */
	public static LaraModel setNewModel(LaraModel model) {
		LEventbus.resetAll();
		LModel.model = model;
		return getModel();
	}

	private LModel() {
	}
}
