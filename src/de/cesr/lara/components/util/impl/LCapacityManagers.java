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
package de.cesr.lara.components.util.impl;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.LaraCapacityManagementView;
import de.cesr.lara.components.container.LaraCapacityManager;
import de.cesr.lara.components.container.memory.LaraLimitedRetentionMemory;

/**
 * Assumes that the storage has already checked that it is full!
 * 
 * Capacity Management Policies:
 * <p>
 * {@code UNLIMITED_CAPACITY}: Memory is <i>(virtually) unlimited in its
 * capacity</i>, i.e. items can be added at any time without forcing the removal
 * of other items already in memory.
 * </p>
 * <p>
 * {@code FIFO}: First In - First Out (queue), i.e. when a new item is to be
 * added to memory, but memory has already reached its maximum capacity, the
 * oldest item in memory will be removed in order to free the necessary space
 * for the new item. If there is more than one oldest item in terms of steps,
 * then the one that actually entered memory first will be removed.
 * </p>
 * <p>
 * {@code FILO}: First In - Last Out (stack), i.e. when a new item is to be
 * added to memory, but memory has already reached its maximum capacity, the
 * item the newest item in memory will be removed in order to free the necessary
 * space for the new item. If there is more than one newest item in terms of
 * steps, then the one that actually entered memory last will be removed.
 * </p>
 * <p>
 * {@code FIFO_RANDOM}: The same as {@code FIFO}, except that if there is more
 * than one oldest item in terms of steps, the one to be removed will be
 * selected <i>at random</i>.
 * </p>
 * <p>
 * {@code FILO_RANDOM}: The same as {@code FILO}, except that if there is more
 * than one newest item in terms of steps, the one to be removed will be
 * selected <i>at random</i>.
 * </p>
 * <p>
 * {@code NINO}: None In - None Out, i.e. no more items can be added to memory
 * once it has reached its maximum capacity, except if items in memory are
 * removed explicitly or as a result of some temporal decay (cf.
 * {@link LaraLimitedRetentionMemory}).
 * </p>
 * <p>
 * {@code RANDOM}: When a new item is to be added to memory, but memory has
 * already reached its maximum capacity, an item in memory will be selected
 * <i>at random</i> and removed in order to free the necessary space for the new
 * item.
 * 
 * 
 * 
 * TODO Capacity management, the way it is done here, i.e. by iterating over the
 * whole set of entries, can be very time consuming. However, it should rarely
 * be necessary anyway. (ME)
 * 
 * A possible solution (for FIFO/LILO and FILO/LIFO at least) may be adding
 * methods like removeFirst() and removeLast() to the LaraCapacityManagementView
 * and leaving the definition of these up to the container that is viewed. (ME)
 * 
 * TODO implement other managers (UNLIMITED_CAPACITY, RANDOM, FIFO_RANDOM,
 * FILO_RANDOM)
 * 
 * 
 */
public final class LCapacityManagers {

	/**
	 * @param <PropertyType>
	 *            the type of properties this container shall store
	 * @return capacity manager
	 */
	public static final <PropertyType extends LaraProperty<? extends PropertyType, ?>> LaraCapacityManager<PropertyType> makeFIFO() {

		return new LaraCapacityManager<PropertyType>() {

			@Override
			public boolean freeSpace(
					LaraCapacityManagementView<PropertyType> view) {

				if (view == null) {
					throw new NullPointerException(view
							+ " return null as " + "LaraCapacityManagementView");
				}

				PropertyType firstInProperty = null;
				for (PropertyType property : view) {
					if (firstInProperty == null
							|| property.getTimestamp() < firstInProperty
									.getTimestamp()) {
						firstInProperty = property;
					}
				}
				if (firstInProperty != null) {
					view.remove(firstInProperty);
					return true;
				}
				return false;
			}

		};

	}

	/**
	 * @param <PropertyType>
	 *            the type of properties the container stores this manager shall
	 *            manage
	 * @return capacity manager
	 */
	public static final <PropertyType extends LaraProperty<? extends PropertyType, ?>> LaraCapacityManager<PropertyType> makeFILO() {

		return new LaraCapacityManager<PropertyType>() {

			/**
			 * @see de.cesr.lara.components.container.LaraCapacityManager#freeSpace(de.cesr.lara.components.container.LaraCapacityManageableContainer)
			 */
			@Override
			public boolean freeSpace(
					LaraCapacityManagementView<PropertyType> view) {

				if (view == null) {
					throw new NullPointerException(view
							+ " return null as " + "LaraCapacityManagementView");
				}

				PropertyType lastInProperty = null;
				for (PropertyType property : view) {
					if (lastInProperty == null
							|| property.getTimestamp() > lastInProperty
									.getTimestamp()) {
						lastInProperty = property;
					}
				}
				if (lastInProperty != null) {
					view.remove(lastInProperty);
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * @param <PropertyType>
	 *            the type of properties the container stores this manager shall
	 *            manage
	 * @return capacity manager
	 */
	public static final <PropertyType extends LaraProperty<? extends PropertyType, ?>> LaraCapacityManager<PropertyType> makeNINO() {

		return new LaraCapacityManager<PropertyType>() {
			/**
			 * Always return false.
			 * 
			 * @see de.cesr.lara.components.container.LaraCapacityManager#freeSpace(de.cesr.lara.components.container.LaraCapacityManageableContainer)
			 */
			@Override
			public boolean freeSpace(
					LaraCapacityManagementView<PropertyType> view) {
				return false;
			}
		};
	}
}
