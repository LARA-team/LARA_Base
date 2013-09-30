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
package de.cesr.lara.components.preprocessor;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.eventbus.LaraInternalEventSubscriber;
import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.preprocessor.event.LaraPpEvent;


/**
 * 
 * Interface for all preprocessor components. This is necessary as a common super type for handling generic components
 * in the {@link LaraPreprocessor}.
 * 
 * @param <A>
 *        agent type
 */
public interface LaraPreprocessorComp<A extends LaraAgent<? super A, ?>, BO extends LaraBehaviouralOption<?, ?>>
		extends LaraInternalEventSubscriber {

	/**
	 * Checks whether the given object is assignable to references of the type of the given class object. It returns a
	 * version of the given object that is casted to the given type. Otherwise, it raises an exception.
	 * 
	 * @param <E>
	 *        The type of event the given event shall be checked for
	 * @param clazz
	 *        class object of the type that shall be checked for
	 * @param event
	 *        the event that whose type is checked
	 * @return the event casted to the given type
	 */
	public <E extends LaraPpEvent> E castEvent(Class<E> clazz, LaraEvent event);
}
