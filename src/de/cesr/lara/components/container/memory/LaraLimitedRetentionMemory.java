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
package de.cesr.lara.components.container.memory;


import de.cesr.lara.components.LaraProperty;


/**
 * The interface for memory implementations that add temporal retention functionality (temporal decay) to the basic
 * memory component of LARA (see {@link AncientLaraMemory}).
 * 
 * @author elbers
 * 
 */
public interface LaraLimitedRetentionMemory<PropertyType extends LaraProperty<PropertyType, ?>> extends
		LaraMemory<PropertyType> {

	/**
	 * Constant to indicate that the retention of the item/items is to be (virtually) unlimited, i.e. the item/items
	 * will not be forgotten (removed) unless capacity management demands it.
	 */
	public static final int	UNLIMITED_RETENTION_TIME	= -1;

	/**
	 * Returns the default retention time currently set. Newly memorised items that have not been assigned a retention
	 * time explicitly will be initialised with the default retention time.
	 * 
	 * @return the current default retention time.
	 */
	@Override
	public int getDefaultRetentionTime();

	/**
	 * Attempts to add a new item to memory having the specified {@code float} value and retention time and identified
	 * by the specified key.
	 * 
	 * @param key
	 *        identifier describing the given value, e.g. on the basis of a user-defined ontology.
	 * @param value
	 *        the value associated with the given key.
	 * @param retentionTime
	 *        the initial time-to-live for this item.
	 */
	public void memorise(String key, float value, int retentionTime);

	/**
	 * Attempts to add a new item to memory having the specified {@code int} value and retention time and identified by
	 * the specified key.
	 * 
	 * @param key
	 *        identifier describing the given value, e.g. on the basis of a user-defined ontology.
	 * @param value
	 *        the value associated with the given key.
	 * @param retentionTime
	 *        the initial time-to-live for this item.
	 */
	public void memorise(String key, int value, int retentionTime);

	/**
	 * Attempts to add a new item to memory having the specified ({@link String} ) value and retention time and
	 * identified by the specified key.
	 * 
	 * @param key
	 *        identifier describing the given value, e.g. on the basis of a user-defined ontology.
	 * @param value
	 *        the value associated with the given key.
	 * @param retentionTime
	 *        the initial time-to-live for this item.
	 */
	public void memorise(String key, String value, int retentionTime);

	/**
	 * Sets the default retention time. Newly memorised items that have not been assigned a retention time explicitly
	 * will be initialised with the default retention time.
	 * 
	 * @param defaultRetentionTime
	 */
	@Override
	public void setDefaultRetentionTime(int defaultRetentionTime);
}
