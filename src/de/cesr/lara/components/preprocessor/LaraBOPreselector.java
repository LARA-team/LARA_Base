/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.preprocessor;

import java.util.Collection;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.agents.LaraAgent;
import de.cesr.lara.components.agents.LaraBOPreselectingAgent;

/**
 * BOChecker
 * 
 * @param <A>
 *            type of agents this BO preselector is intended for
 * @param <BO>
 *            type of behavioural options that are checked
 */
public interface LaraBOPreselector<A extends LaraAgent<A, ? super BO>, BO extends LaraBehaviouralOption<? super A, BO>>
		extends LaraPreprocessorComp<A> {

	/**
	 * A common interface for preprocessor accuracy statements which enables the
	 * user to provide his own set of accuracies.
	 * 
	 * @author Sascha Holzhauer
	 * @date 10.02.2010
	 * 
	 */
	public interface Accuracy {
	}

	/**
	 * accuracy
	 * 
	 * @author klemm
	 * @date 16.02.2010
	 * 
	 */
	public enum LAccuracy implements LaraBOPreselector.Accuracy {
		/**
		 * Most accurate preprocessor handling
		 */
		ACCURATE,

		/**
		 * Using ASK_AGENT will cause the preprocessor to step back to the agent
		 * (this requires the user to implement {@link LaraBOPreselectingAgent}
		 * ).
		 */
		ASK_AGENT,

		/**
		 * Moderate preprocessor handling
		 */
		MODERATE,

		/**
		 * Tolerant preprocessor handling
		 */
		TOLERANT;
	}

	/**
	 * @param agent
	 *            the agent the BO belongs to
	 * @param bOptions
	 *            behavioural options to preselect
	 * @return behavioural options
	 * 
	 */
	public abstract Collection<BO> preselectBOs(A agent, Collection<BO> bOptions);
}
