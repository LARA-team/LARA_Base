package de.cesr.lara.components.decision.impl;


import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.decision.LaraHeader;
import de.cesr.lara.components.decision.LaraRow;
import de.cesr.lara.components.decision.LaraUtilityMatrix;


/**
 * the utility matrix attention: when writing decision heuristic care about equal values (set unordered)
 * 
 * @param <BO>
 *        type of behavioural option
 * 
 * @author klemm
 */
public class LUtilityMatrix<BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements
		LaraUtilityMatrix<BO> {

	LaraHeader				header	= null;

	Collection<LaraRow<BO>>	rows	= null;

	/**
	 * constructor
	 */
	public LUtilityMatrix() {
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraUtilityMatrix#getHeader()
	 */
	@Override
	public LaraHeader getHeader() {
		return header;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraUtilityMatrix#getNumRows()
	 */
	@Override
	public int getNumRows() {
		return rows != null ? rows.size() : 0;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraUtilityMatrix#getRows()
	 */
	@Override
	public Collection<LaraRow<BO>> getRows() {
		return rows;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraUtilityMatrix#setHeader(de.cesr.lara.components.decision.LaraHeader)
	 */
	@Override
	public void setHeader(LaraHeader header) {
		this.header = header;
	}

	/**
	 * @see de.cesr.lara.components.decision.LaraUtilityMatrix#setRows(java.util.Collection)
	 */
	@Override
	public void setRows(Collection<LaraRow<BO>> row) {
		this.rows = row;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ret = "LUtilityMatrix - rows:\n";
		for (LaraRow<BO> row : rows) {
			ret = ret + "\t" + row.toString()
					+ System.getProperty("line.separator");
		}
		return ret;
	}

}
