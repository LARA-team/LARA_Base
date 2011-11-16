/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.environment;

import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;

/**
 * Interface for observers of {@link LaraEnvironment}
 * 
 * @author Sascha Holzhauer
 * @date 10.12.2009
 * 
 */
public interface LaraEnvironmentListener {

	/**
	 * Called by the {@link LaraEnvironment} to inform about property changes.
	 * 
	 * @param envProperty
	 *            Created by Sascha Holzhauer on 10.12.2009
	 */
	public abstract void envPropertyChanged(
			LAbstractEnvironmentalProperty<?> envProperty);

}
