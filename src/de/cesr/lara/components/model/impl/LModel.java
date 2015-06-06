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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.LaraModelResetObserver;
import de.cesr.lara.components.util.impl.LVersionInfo;

/**
 * Registry for LARA models.
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
	private static Map<Object, LaraModel> models = new HashMap<Object, LaraModel>();

	private static Set<LaraModelResetObserver> resetObservers = new HashSet<LaraModelResetObserver>();

	/**
	 * @return model
	 */
	public static LaraModel getModel() {
		if (models.get(null) == null) {
			throw new IllegalStateException("Default model was not set");
		}
		return models.get(null);
	}

	/**
	 * @param id
	 * @return model associated with given id
	 */
	public static LaraModel getModel(Object id) {
		if (id == null)
			return getModel();

		if (models.get(id) == null) {
			throw new IllegalStateException("Model for id " + id
					+ " was not set");
		}
		return models.get(id);
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
		LModel.models.put(null, model);
		return getModel();
	}

	/**
	 * @param model
	 * @param id
	 * @return new model
	 */
	public static LaraModel setNewModel(Object id, LaraModel model) {
		if (LModel.models.containsKey(id)) {
			throw new IllegalArgumentException(
					"There is already a model registered with ID "
							+ id
							+ ". Use resetModel(id) to remove an already registered model!");
		}
		LModel.models.put(id, model);
		return getModel(id);
	}

	/**
	 * @param id
	 */
	public static void resetModel(Object id) {
		LModel.models.remove(id);
		for (LaraModelResetObserver observer : resetObservers) {
			observer.getNotified(id);
		}
	}

	/**
	 * Clear map of registered models.
	 */
	public static void reset() {
		LEventbus.resetAll();
		for (LaraModelResetObserver observer : resetObservers) {
			observer.getNotified();
		}
		LModel.models = new HashMap<Object, LaraModel>();
	}

	/**
	 * Prevent instantiation.
	 */
	private LModel() {
	}

	/**
	 * @param observer
	 */
	public static void registerResetObserver(LaraModelResetObserver observer) {
		resetObservers.add(observer);
	}
}
