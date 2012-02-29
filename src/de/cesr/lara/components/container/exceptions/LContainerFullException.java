package de.cesr.lara.components.container.exceptions;

import de.cesr.lara.components.container.LaraContainer;

/**
 * Indicates a full {@link LaraContainer}
 */
public class LContainerFullException extends LContainerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -193908038628145937L;

	public LContainerFullException() {
	}

	/**
	 * @param string
	 */
	public LContainerFullException(String string) {
		super(string);
	}

}
