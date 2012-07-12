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
 * Super class of all LContainer*Exceptions.
 */
public class LContainerException extends RuntimeException {

	private static final long serialVersionUID = 6700984975941024617L;

	/**
	 * Exception message
	 */
	private String message;

	/**
	 * 
	 */
	public LContainerException() {
		message = "";
	}

	/**
	 * @param message
	 */
	public LContainerException(String message) {
		this.message = message;
	}

	/**
	 * Returns the exception's message.
	 * 
	 * @see java.lang.Throwable#toString()
	 */
	@Override
	public String toString() {
		return message;
	}

}
