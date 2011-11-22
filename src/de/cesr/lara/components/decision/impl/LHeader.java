/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.decision.impl;


import java.util.List;

import de.cesr.lara.components.decision.LaraHeader;


/**
 * header (row) of utility matrix
 * 
 * @author klemm
 * 
 */
public class LHeader implements LaraHeader {

	List<String>	cells	= null;

	/**
	 * constructor - requires a list of cells
	 * 
	 * @param cells
	 */
	public LHeader(List<String> cells) {
		this.cells = cells;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraHeader#addCell(java.lang.String)
	 */
	@Override
	public void addCell(String label) {
		cells.add(label);
	}

}
