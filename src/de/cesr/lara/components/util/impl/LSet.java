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


import java.util.Arrays;
import java.util.LinkedHashSet;


/**
 * Enables the filling of a hash set by passing an arbitrary number (> 0) of elements
 * to the constructor.
 * 
 * @author Sascha Holzhauer
 * 
 */
public class LSet<E> extends LinkedHashSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8698440369878570597L;

	/**
	 * @param entries
	 */
	public LSet(E... entries) {
		super();
		addAll(Arrays.asList(entries));
	}

	/**
	 * @param entry
	 */
	public LSet(E entry) {
		super();
		add(entry);
	}
}
