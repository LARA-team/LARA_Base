/**
 * LARA
 *
 * KUBUS ABM Prototype
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 13.11.2009
 */
package de.cesr.lara.components.preprocessor.impl;


import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;


/**
 * Lets all behavioural options pass the pre-selection.
 * 
 * @param <A>
 *        type of agents this BO preselector is intended for
 * @param <BO>
 *        type of behavioural options that are checked
 * 
 * @author Sascha Holzhauer
 * @date 13.11.2009
 */
public class LPseudoBOPreselector<A extends LaraAgent<? super A, BO>, BO extends LaraBehaviouralOption<?, ? extends BO>>
		implements LaraBOPreselector<A, BO> {

	/**
	 * @see de.cesr.lara.components.preprocessor.LaraBOPreselector#preselectBOs(de.cesr.lara.components.agents.LaraAgent,
	 *      java.util.Collection)
	 */
	@Override
	public Collection<BO> preselectBOs(A agent, Collection<BO> bOptions) {
		return bOptions;
	}
}
