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
