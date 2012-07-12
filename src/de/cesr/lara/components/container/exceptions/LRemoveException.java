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
package de.cesr.lara.components.container.exceptions;

/**
 * Exception thrown by underlying storage classes in case a requested property
 * can not be found to be removed.
 */
public class LRemoveException extends LContainerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3973412911918125974L;

	/**
	 * @param string
	 */
	public LRemoveException(String string) {
		super(string);
	}

}