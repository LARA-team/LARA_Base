/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
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
