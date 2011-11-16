/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * 
 */
package de.cesr.lara.components.preprocessor;

import de.cesr.lara.components.agents.LaraAgent;

/**
 * Marker Interface for all preprocessor components. This is necessary as a
 * common super type for handling generic components in the
 * {@link LaraPreprocessorFactory}. see
 * http://java.sun.com/docs/books/jls/third_edition/html/typesValues.html#4.5 A
 * Type parameter cannot be generic. Furthermore, one cannot define more than
 * one type in one ActualTypeArgument (T<S>). So one needs to workaround with
 * two ActualTypeArguments declarations (S extends LaraAgent, T extends
 * Interface<S>)
 * 
 * @param <A>
 *            agent type
 */
public interface LaraPreprocessorComp<A extends LaraAgent<A, ?>> {

}
