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
package de.cesr.lara.components.environment.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.MultiMap;
import org.apache.commons.collections15.multimap.MultiHashMap;
import org.apache.log4j.Logger;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.LaraEnvironmentListener;
import de.cesr.lara.components.environment.LaraSuperEnvironment;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;

/**
 * This default implementation of {@link LaraSuperEnvironment} does not delegate
 * to sub-environments for {@link LEnvironment#containsProperty(String)},
 * {@link #removeProperty(LAbstractEnvironmentalProperty)}, etc.
 * 
 * TODO integrate LaraStorage
 * 
 * TODO implement getters for filter properties
 * 
 * TODO parameterise LaraEnvironment
 * 
 * TODO add property functions that deal with sub-envs (like
 * removePopertySubenv)
 * 
 * TODO Test sub-environments
 * 
 * TODO consider Composite Design Pattern for property provision from
 * sub-environments
 * 
 * @author Sascha Holzhauer
 * @date 22.12.2009
 * 
 */
public class LEnvironment implements LaraSuperEnvironment {

	/**
	 * Logger
	 */
	static private Logger logger = Log4jLogger.getLogger(LEnvironment.class);
	
	/**
	 */
	protected Map<String, LAbstractEnvironmentalProperty<?>> abstractProperties;

	/**
	 * Contains listeners that observe all properties of this environment.
	 */
	protected Set<LaraEnvironmentListener> allObserver;

	/**
	 * Contains listeners that observe certain properties.
	 */
	protected Map<String, Set<LaraEnvironmentListener>> propertyObserver;

	/**
	 * Container for sub-environments
	 */
	protected MultiMap<Object, LaraEnvironment> subEnvironments;

	/**
	 * constructor
	 */
	public LEnvironment() {
		this.subEnvironments = new MultiHashMap<Object, LaraEnvironment>();
		this.abstractProperties = new HashMap<String, LAbstractEnvironmentalProperty<?>>();

		this.allObserver = new HashSet<LaraEnvironmentListener>();
		this.propertyObserver = new HashMap<String, Set<LaraEnvironmentListener>>();
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#addEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener)
	 */
	@Override
	public void addEnvListener(LaraEnvironmentListener listener) {
		allObserver.add(listener);
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#addEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener,
	 *      java.lang.String)
	 */
	@Override
	public void addEnvListener(LaraEnvironmentListener listener, String name) {
		Set<LaraEnvironmentListener> listeners = propertyObserver.get(name);
		if (listeners == null) {
			listeners = new HashSet<LaraEnvironmentListener>();
			propertyObserver.put(name, listeners);
		}
		listeners.add(listener);
	}

	/**
	 * Calls update.
	 * 
	 * @see de.cesr.lara.components.environment.LaraEnvironment#addProperty(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public void addProperty(LAbstractEnvironmentalProperty<?> property) {
		this.updateProperty(property);
	}

	/**
	 * 
	 * @see de.cesr.lara.components.environment.LaraEnvironment#containsProperty(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public boolean containsProperty(LAbstractEnvironmentalProperty<?> property) {
		if (abstractProperties.containsKey(property.getKey())) {
			return abstractProperties.get(property.getKey()).equals(property);
		} else {
			return false;
		}
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#containsProperty(java.lang.String)
	 */
	@Override
	public boolean containsProperty(String name) {
		return abstractProperties.containsKey(name);
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#getAllListeners()
	 */
	@Override
	public Set<LaraEnvironmentListener> getAllListeners() {
		Set<LaraEnvironmentListener> set = new HashSet<LaraEnvironmentListener>();
		set.addAll(allObserver);
		for (Set<LaraEnvironmentListener> listeners : propertyObserver.values()) {
			set.addAll(listeners);
		}

		return set;
	}

	/**
	 * Returns a shallow copy of all {@link LAbstractEnvironmentalProperty}s
	 * registered at this environment.
	 * 
	 * @see de.cesr.lara.components.environment.LaraEnvironment#getEnvProperties()
	 */
	@Override
	public Collection<LAbstractEnvironmentalProperty<?>> getEnvProperties() {
		return new HashSet<LAbstractEnvironmentalProperty<?>>(
				abstractProperties.values());
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#getPropertyByName(java.lang.String)
	 */
	@Override
	public LAbstractEnvironmentalProperty<?> getPropertyByName(String name) {
		return abstractProperties.get(name);
	}

	/**
	 * Returns a shallow copy of all registered sub-environments.
	 * 
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#getSubEnvironments()
	 */
	@Override
	public Set<LaraEnvironment> getSubEnvironments() {
		return new HashSet<LaraEnvironment>(subEnvironments.values());
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#getSubEnvironments(java.lang.Object)
	 */
	@Override
	public Set<LaraEnvironment> getSubEnvironments(Object category) {
		if (category == LaraSuperEnvironment.ALL_CATEGORIES) {
			return new HashSet<LaraEnvironment>(subEnvironments.values());
		}
		
		if (!subEnvironments.containsKey(category)) {
			// <- LOGGING
			logger.error("This environment does not contain a sub-environment for category " + category);
			// LOGGING ->
			throw new IllegalArgumentException("This environment does not contain a sub-environment for category " + category);
		}
		return new HashSet<LaraEnvironment>(subEnvironments.get(category));
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#getTypedPropertyByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <V> LAbstractEnvironmentalProperty<V> getTypedPropertyByName(
			String name) {
		if (abstractProperties != null) {
			if (abstractProperties.get(name).getKey().equals(name)) {
				try {
					return (LAbstractEnvironmentalProperty<V>) abstractProperties
							.get(name);
				} catch (ClassCastException e) {
					throw new ClassCastException(
							"Requested property is not of desired type!");
				}
			}
		}
		return null;
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#registerEnvironment(Object,
	 *      de.cesr.lara.components.environment.LaraEnvironment)
	 */
	@Override
	public void registerEnvironment(Object cat, LaraEnvironment environment) {
		if (!subEnvironments.containsValue(cat, environment)) {
			subEnvironments.put(cat, environment);
		}
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#removeEnvironment(de.cesr.lara.components.environment.LaraEnvironment)
	 */
	@Override
	public void removeEnvironment(LaraEnvironment environment) {
		subEnvironments.remove(environment);
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#removeEnvironment(Object)
	 */
	@Override
	public void removeEnvironment(Object cat) {
		subEnvironments.remove(cat);
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#removeEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener)
	 */
	@Override
	public boolean removeEnvListener(LaraEnvironmentListener listener) {
		return allObserver.remove(listener);
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#removeEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener,
	 *      java.lang.String)
	 */
	@Override
	public void removeEnvListener(LaraEnvironmentListener listener, String name) {
		if (propertyObserver.get(name) != null) {
			propertyObserver.get(name).remove(listener);
		}
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#removeProperty(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public boolean removeProperty(LAbstractEnvironmentalProperty<?> property) {
		if (containsProperty(property)) {
			assert property == abstractProperties.remove(property.getKey());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraEnvironment#removeProperty(java.lang.String)
	 */
	@Override
	public boolean removeProperty(String name) {
		if (abstractProperties.remove(name) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see de.cesr.lara.components.environment.LaraSuperEnvironment#removePropertySubenv(Object, String)
	 */
	@Override
	public boolean removePropertySubenv(Object category, String name) {
		boolean removedAny = false;
		if (abstractProperties.remove(name) != null) {
			removedAny = true;
		}
		for (LaraEnvironment env : getSubEnvironments(category)) {
			removedAny = removedAny ? true : env.removeProperty(name);
		}
		return removedAny;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Default Environment";
	}

	/**
	 * Informs listeners every time a value changed (should listeners be
	 * informed of an update even if the value has not changed implement a new
	 * registration function <code>addEnvUpdateListeners()</code>!)
	 * 
	 * @see de.cesr.lara.components.environment.LaraEnvironment#updateProperty(de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)
	 */
	@Override
	public void updateProperty(LAbstractEnvironmentalProperty<?> property) {
		if (containsProperty(property.getKey())) {
			if (!abstractProperties.get(property.getKey()).getValue()
					.equals(property.getValue())) {
				// if property is contained update only in case the property's
				// value was changed
				abstractProperties.put(property.getKey(), property);
				inform(property);
			}
		} else {
			// if property is new update in every case:
			abstractProperties.put(property.getKey(), property);
			inform(property);
		}
	}

	/**
	 * Informs all listeners that are either registered for the all properties
	 * or the specifically for the given one.
	 * 
	 * @param property
	 *            new property
	 */
	protected void inform(LAbstractEnvironmentalProperty<?> property) {
		for (LaraEnvironmentListener listener : allObserver) {
			listener.envPropertyChanged(property);
		}
		if (propertyObserver.containsKey(property)) {
			for (LaraEnvironmentListener listener : propertyObserver
					.get(property)) {
				listener.envPropertyChanged(property);
			}
		}
	}

}