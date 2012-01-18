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
package de.cesr.lara.components.environment;

import java.util.Set;

/**
 * 
 * TODO add property functions that deal with sub-envs (see
 * removePropertySubenv) (SH)
 * 
 * Extends {@link LaraEnvironment} and allows registering sub-environments that
 * are accessed by this super environment. NOTE: Classes that implement this
 * interface need to state which methods they delegate to registered
 * sub-environments!
 * 
 * @date 28.02.2011
 * @author Sascha Holzhauer
 */
public interface LaraSuperEnvironment extends LaraEnvironment {

	public static final Object ALL_CATEGORIES = new Object();

	/**
	 * Returns all registered sub-environments.
	 * 
	 * @return all sub-environments
	 */
	public Set<LaraEnvironment> getSubEnvironments();

	/**
	 * Returns all registered sub-environments of the given category.
	 * 
	 * @return all sub-environments
	 */
	public Set<LaraEnvironment> getSubEnvironments(Object category);

	/**
	 * Registers a new {@link LaraEnvironment} at this super environment. The
	 * category object allows the modeler to define categories of environment
	 * and to look up environments by this category. For instance, there are
	 * agent classes that are embedded in environments that are specialised but
	 * belong to a certain category. The category may be used for methods across
	 * agent classes. Furthermore, several environments may be grouped by
	 * category. Examples for categories are social and bio-physical
	 * environments.
	 * 
	 * @param cat
	 *            category object to identify the (type of) environment. Null is
	 *            allowed.
	 * @param environment
	 *            environment to register
	 */
	public void registerEnvironment(Object cat, LaraEnvironment environment);

	/**
	 * Removes a {@link LaraEnvironment} from this super environment.
	 * 
	 * @param environment
	 *            environment to remove
	 */
	public void removeEnvironment(LaraEnvironment environment);

	/**
	 * Removes all {@link LaraEnvironment} from this super environment that
	 * belong to the given category.
	 * 
	 * @param cat
	 *            category object to identify the (type of) environment
	 */
	public void removeEnvironment(Object cat);

	/********************************************
	 * PROPERTY METHODS
	 * ******************************************/

	/**
	 * Removes properties with the given name also in sub-environments of the
	 * given category. ALL_CATEGORIES removes it in all categories. Null is
	 * allowed as category.
	 * 
	 * @param name
	 *            of the properties to remove
	 * @return true if any property was removed
	 */
	public boolean removePropertySubenv(Object category, String name);
}
