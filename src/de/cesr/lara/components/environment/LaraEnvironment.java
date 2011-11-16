package de.cesr.lara.components.environment;

import java.util.Collection;
import java.util.Set;

import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;

/**
 * This class defines the interface between agents and its various environments:
 * <ul>
 * <li>bio-physical, natural environments</li>
 * <li>socio-economic environments (political, legal and economical boundary
 * conditions)</li>
 * <li>social environments (networks layered by semantics)</li>
 * </ul>
 * 
 * Environments may have sub-environments (see <a
 * href="../../../../../userguide.html#environment">User Guide</a>).
 * 
 * @author Sascha Holzhauer
 * @date 10.12.2009
 * 
 */
public interface LaraEnvironment {

	/**
	 * Adds an {@link LaraEnvironmentListener} to the entire
	 * {@link LaraEnvironment}.
	 * 
	 * @param listener
	 *            listener to add.
	 */
	public void addEnvListener(LaraEnvironmentListener listener);

	/**
	 * Registers a {@link LaraEnvironmentListener} at this environment to
	 * observe only a certain property.
	 * 
	 * @param listener
	 *            the observer to register
	 * @param name
	 *            name of property to observe
	 */
	public abstract void addEnvListener(LaraEnvironmentListener listener,
			String name);

	/**
	 * Checks whether a property is in the list or not.
	 * 
	 * @param property
	 *            property to check
	 * @return true if the property is within the list, false otherwise
	 */
	public boolean containsProperty(LAbstractEnvironmentalProperty<?> property);

	/**
	 * Checks whether a property name is in the environment or not.
	 * 
	 * @param name
	 *            name of property to check
	 * @return true if the name is within the list, false otherwise
	 */
	public boolean containsProperty(String name);

	/**
	 * Returns a set of all listeners - those that are registered at all
	 * properties and those registered at certain properties.
	 * 
	 * @return listeners set of all listeners
	 * @note implemented for testing reasons
	 */
	public abstract Set<LaraEnvironmentListener> getAllListeners();

	/**
	 * Returns a {@link Collection} of all
	 * {@link LAbstractEnvironmentalProperty}s of this environment.
	 * 
	 * @return envProperties all {@link LAbstractEnvironmentalProperty}s stored
	 *         by this {@link LaraEnvironment}
	 */
	public Collection<LAbstractEnvironmentalProperty<?>> getEnvProperties();

	/**
	 * @param name
	 *            the {@link LAbstractEnvironmentalProperty}'s name
	 * @return property of environment
	 */
	public abstract LAbstractEnvironmentalProperty<?> getPropertyByName(
			String name);

	/**
	 * @param <V>
	 *            type of requested property
	 * @param name
	 *            the {@link LAbstractEnvironmentalProperty}'s name
	 * @return property of environment
	 */
	public abstract <V> LAbstractEnvironmentalProperty<V> getTypedPropertyByName(
			String name);

	/**
	 * Remove a {@link LaraEnvironmentListener} from the entire
	 * {@link LaraEnvironment}. This does not impact listeners that are
	 * registered at certain properties!
	 * 
	 * @param listener
	 *            listener to remove.
	 * @return boolean true if the listener was contained and could be removed
	 */
	public boolean removeEnvListener(LaraEnvironmentListener listener);

	/**
	 * Removes a {@link LaraEnvironmentListener} at this environment from the
	 * given property. This does not impact possibly same listeners that are
	 * registered at the whole Environment.
	 * 
	 * @param listener
	 *            the observer to register
	 * @param name
	 *            name of observed property
	 */
	public abstract void removeEnvListener(LaraEnvironmentListener listener,
			String name);

	/**
	 * Removes a property if it is contained in the environment's list.
	 * 
	 * @param property
	 *            property to remove
	 * 
	 * @return true if the property was in the list and could be removed
	 */
	public boolean removeProperty(LAbstractEnvironmentalProperty<?> property);

	/**
	 * Removes a property if it is contained in the environment's list.
	 * 
	 * @param name
	 *            name of property to remove
	 * 
	 * @return true if the property was in the list and could be removed
	 */
	public boolean removeProperty(String name);

	/**
	 * Changes an existing property or adds it in case it does not yet exist.
	 * 
	 * @param property
	 *            property to update or add
	 */
	public void updateProperty(LAbstractEnvironmentalProperty<?> property);
}
