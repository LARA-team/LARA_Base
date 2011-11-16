/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 28.02.2011
 */
package de.cesr.lara.components.environment;

import java.util.Set;

/**
 * Extends {@link LaraEnvironment} and allows registering sub-environments that
 * are accessed by this super environment. NOTE: Classes that implement this
 * interface need to state which methods they delegate to registered
 * sub-environments!
 * 
 * @author Sascha Holzhauer
 */
public interface LaraSuperEnvironment extends LaraEnvironment {

	/**
	 * Returns all registered sub-environments.
	 * 
	 * @return all sub-environments
	 */
	public Set<LaraEnvironment> getSubEnvironments();

	/**
	 * Registers a new {@link LaraEnvironment} at this super environment. The
	 * category object allows the modeller to define categories of environment
	 * and to look up environments by this category. For instance, there are
	 * agent classes that are embedded in environments that are specialised but
	 * belong to a certain category. The category may be used for methods across
	 * agent classes. Furthermore, several environments may be grouped by
	 * category.
	 * 
	 * @param cat
	 *            category object to identify the (type of) environment. Null is
	 *            allowed.
	 * @param environment
	 *            environment to register
	 */
	public void registerEnvironment(Object cat, LaraEnvironment environment);

	/**
	 * Removes a {@link LaraEnvironment} from this super environment.
	 * 
	 * @param environment
	 *            environment to remove
	 */
	public void removeEnvironment(LaraEnvironment environment);

	/**
	 * Removes all {@link LaraEnvironment} from this super environment that
	 * belong to the given category.
	 * 
	 * @param cat
	 *            category object to identify the (type of) environment
	 */
	public void removeEnvironment(Object cat);
}
