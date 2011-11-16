/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 18.12.2009
 */
package de.cesr.lara.components.container.memory;

import java.util.Set;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.container.exceptions.LRetrieveException;

/**
 * 
 * Interface for memories of behavioural options that ensures that properties
 * are of type {@link LaraBehaviouralOption}.
 * 
 * @param <BO>
 *            type of behavioural options this memory may store
 * 
 * @author Sascha Holzhauer
 * @date 18.12.2009
 * 
 */
public interface LaraBOMemory<BO extends LaraBehaviouralOption<?, ?>> extends
		LaraMemory<BO> {

	/**
	 * Memorises the given set of behavioural options
	 * 
	 * @param bos
	 *            behavioural options to memorise
	 */
	public void memoriseAll(Set<BO> bos);

	/**
	 * Generic method that returns a collection of the most recently memorised
	 * BO for all behavioural options.
	 * 
	 * @return for every behavioural option the one that was stored at last.
	 * @throws LRetrieveException
	 */
	public Set<BO> recallAllMostRecent() throws LRetrieveException;
}
