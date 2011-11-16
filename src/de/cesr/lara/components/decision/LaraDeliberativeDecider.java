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
 * A special {@link LaraDecider} that adds functionality required for matrix
 * decisions.
 * 
 * @param <BO>
 *            type of behavioural options
 */
public interface LaraDeliberativeDecider<BO extends LaraBehaviouralOption<?, BO>>
		extends LaraDecider<BO> {

	/**
	 * @return Returns the header.
	 */
	public LaraHeader getHeader();

	/**
	 * @return Returns the preference.
	 */
	public Map<Class<? extends LaraPreference>, Double> getPreferenceWeights();

	/**
	 * @return Returns the behaviouralOption.
	 */
	public Collection<? extends BO> getSelectableBOs();

	/**
	 * @return the best situational behavioural option
	 */
	@Override
	public abstract BO getSelectedBo();

	/**
	 * @param deliberativeChoiceComponent
	 *            Created by klemm on 04.02.2010
	 */
	public void setDeliberativeChoiceComponent(
			LaraDeliberativeChoiceComponent<BO> deliberativeChoiceComponent);

	/**
	 * Setter of the property <tt>i_Header</tt>
	 * 
	 * @param header
	 */
	public void setHeader(LaraHeader header);

	/**
	 * Setter of the property <tt>i_Preference</tt>
	 * 
	 * @param preferenceWeights
	 */
	public void setPreferences(
			Map<Class<? extends LaraPreference>, Double> preferenceWeights);

	/**
	 * Setter of the property <tt>i_BehaviouralOption</tt>
	 * 
	 * @param selectableBOs
	 */
	public void setSelectableBOs(Collection<? extends BO> selectableBOs);
}
