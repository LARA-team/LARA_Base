/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 22.12.2009
 */
package de.cesr.lara.testing.components.environment;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty;


/**
 * 
 * @author Sascha Holzhauer
 * @date 22.12.2009
 * 
 */
public class EnvironmentalIntPropertyTest {

	LaraEnvironment	env;
	LEnvironmentalIntProperty	prop1, prop2, prop3;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 22.12.2009
	 */
	@Before
	public void setUp() throws Exception {
		this.env = new LEnvironment();
		this.prop1 = new LEnvironmentalIntProperty("Property1", 1, env);
		this.prop2 = new LEnvironmentalIntProperty("Property1", 1, env);
		this.prop3 = new LEnvironmentalIntProperty("Property3", 3, env);
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals("Proeprties are equal", prop1.equals(prop1), true);
		assertEquals("Proeprties are equal", prop1.equals(prop2), true);
		assertEquals("Proeprties are not equal", prop1.equals(prop3), false);
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 22.12.2009
	 */
	@After
	public void tearDown() throws Exception {
		env = null;
		prop1 = prop2 = prop3 = null;
	}
}
