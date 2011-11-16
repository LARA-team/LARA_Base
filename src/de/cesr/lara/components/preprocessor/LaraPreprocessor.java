/**
 * LARA
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 25.06.2009
 */
package de.cesr.lara.components.preprocessor;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;

/**
 * LARA Pre-Processor
 * 
 * @author Sascha Holzhauer
 * @param <A>
 *            the type of agents this preprocessor is intended for
 * @param <BO>
 *            the type of behavioural options the preprocessor shall manage
 * @date 10.11.2009
 */
public interface LaraPreprocessor<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraPreprocessorComp<A> {

	/**
	 * @param accuracy
	 * @param agent
	 */
	public abstract void preprocess(LaraBOPreselector.Accuracy accuracy, A agent);
}
