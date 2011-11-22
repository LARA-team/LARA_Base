package de.cesr.lara.components.decision;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;

/**
 * the non-persistent matrix
 * 
 * @param <BO>
 */
public interface LaraUtilityMatrix<BO extends LaraBehaviouralOption<?, ?>> {

	/**
	 * @return Returns the header.
	 */
	public LaraHeader getHeader();

	/**
	 * @return the number of rows in the matrix
	 */
	public int getNumRows();

	/**
	 * @return Returns the rows.
	 */
	public Collection<LaraRow<BO>> getRows();

	/**
	 * Setter of the property <tt>i_Header</tt>
	 * 
	 * @param header
	 */
	public void setHeader(LaraHeader header);

	/**
	 * Setter of the property <tt>i_Row</tt>
	 * 
	 * @param rows
	 */
	public void setRows(Collection<LaraRow<BO>> rows);

}
