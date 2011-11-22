/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.components.agents;


import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.preprocessor.LaraBOPreselector;


/**
 * 
 * The defined method is invoked by {@link LaraBOPreselector} in case of {@link LaraBOPreselector.LAccuracy} =
 * <code>ASK_AGENT.</code>
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *        type of agent
 * @param <BO>
 *        type of behavioural option
 * @date 10.02.2010
 * 
 */
public interface LaraBOPreselectingAgent<A extends LaraAgent<A, BO>, BO extends LaraBehaviouralOption<?,BO>> {

	/**
	 * Invoked by {@link LaraBOPreselector} in case of {@link LaraBOPreselector.LAccuracy} = <code>ASK_AGENT.</code>
	 * 
	 * @param option
	 *        behavioural option to preselect
	 * @return true if the behavioural option passes the check
	 */
	public boolean preselect(BO option);

}
