/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.decision;


import java.util.Collection;
import java.util.Map;

import de.cesr.lara.components.LaraBehaviouralOption;
import de.cesr.lara.components.LaraPreference;


/**
 * A special {@link LaraDecider} that adds functionality required for matrix decisions.
 * @param <BO> 
 *			 type of behavioural options
 */
public interface LaraDeliberativeDecider<BO extends LaraBehaviouralOption<?, ? extends BO>> extends LaraDecider<BO> {

	/**
	 * @param deliberativeChoiceComponent
	 */
	public void setDeliberativeChoiceComponent(LaraDeliberativeChoiceComponent deliberativeChoiceComponent);

	/**
	 * @return the best situational behavioural option
	 */
	public abstract BO getSelectedBO();

	/**
	 * @return Returns the behaviouralOption.
	 */
	public Collection< BO> getSelectableBOs();

	/**
	 * Setter of the property <tt>i_BehaviouralOption</tt>
	 * 
	 * @param selectableBOs
	 */
	public void setSelectableBOs(Collection<BO> selectableBOs);

	/**
	 * @return Returns the header.
	 */
	public LaraHeader getHeader();

	/**
	 * Setter of the property <tt>i_Header</tt>
	 * 
	 * @param header
	 */
	public void setHeader(LaraHeader header);

	/**
	 * @return Returns the preference.
	 */
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights();

	/**
	 * Setter of the property <tt>i_Preference</tt>
	 * 
	 * @param preferenceWeights
	 */
	public void setPreferences(Map<Class<? extends LaraPreference>, Double> preferenceWeights);
}
