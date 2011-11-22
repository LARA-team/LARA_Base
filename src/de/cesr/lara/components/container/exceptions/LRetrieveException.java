package de.cesr.lara.components.container.exceptions;

/**
 * Exception thrown by underlying storage classes in case a requested property
 * can not be found to be retrieved.
 */
public class LRetrieveException extends LContainerException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8500150379705583532L;

	/**
	 * @param string
	 */
	public LRetrieveException(String string) {
		super(string);
	}

}
