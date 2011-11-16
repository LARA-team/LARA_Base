/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 18.12.2009
 */
package de.cesr.lara.testing.components.environment;


import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.LaraEnvironmentListener;
import de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
import de.cesr.lara.components.model.LaraModel;


/**
 * 
 * @author Sascha Holzhauer
 * @date 18.12.2009
 * 
 */
public class EnvironmentTest {

	LaraModel					model;
	LaraEnvironment				env;
	LEnvironmentalIntProperty	prop1;
	LEnvironmentalIntProperty	prop2;
	LaraEnvironmentListener		listener1;
	LaraEnvironmentListener		listener2;
	int							indicator;

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 18.12.2009
	 */
	@Before
	public void setUp() throws Exception {

		// required for stepping:
		// TODO: adapt to new LModel
		// model = LModel.getNewModel();

		this.env = new LEnvironment();
		this.prop1 = new LEnvironmentalIntProperty("Property1", 1, env);
		this.prop2 = new LEnvironmentalIntProperty("Property2", 2, env);
		this.listener1 = new LaraEnvironmentListener() {
			@Override
			public void envPropertyChanged(LAbstractEnvironmentalProperty<?> envProperty) {
				indicator = 1;
			}
		};
		;
		;
		this.listener2 = new LaraEnvironmentListener() {

			@Override
			public void envPropertyChanged(LAbstractEnvironmentalProperty<?> envProperty) {
			}
		};
		indicator = 0;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironment#addEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener)}
	 * .
	 */
	@Test
	public void testAddEnvListenerLaraEnvironmentListener() {
		env.addEnvListener(listener1);
		assertEquals("Number of registered listeners should be 1", 1, env.getAllListeners().size());
	}

	// /**
	// * Test method for
	// * {@link
	// de.cesr.lara.components.environment.impl.LaraEnvironmentImpl#addEnvListener(de.cesr.lara.components.LaraEnvironmentListener,
	// de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)}
	// * .
	// */
	// @Test
	// public void testAddEnvListenerForExistingProperty() {
	// env.updateProperty(prop1);
	// env.addEnvListener(listener1, prop1);
	// assertEquals("Number of registered listeners should be 1", 1, env.getAllListeners().size());
	//
	// prop1.setValue(new Integer(11));
	// env.updateProperty(prop1);
	// assertEquals("Listener should have been called", 1, indicator);
	// indicator = 0;
	//
	// prop1.setValue(new Integer(11));
	// env.updateProperty(prop1);
	// assertEquals("Listener should not have been called", 0, indicator);
	// }
	//
	// /**
	// * Test method for
	// * {@link
	// de.cesr.lara.components.environment.impl.LaraEnvironmentImpl#addEnvListener(de.cesr.lara.components.LaraEnvironmentListener,
	// de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty)}
	// * .
	// */
	// @Test
	// public void testAddEnvListenerForNonExistingProperty() {
	// env.addEnvListener(listener1, prop1);
	// assertEquals("Number of registered listeners should be 1", 1, env.getAllListeners().size());
	//
	// env.updateProperty(prop1);
	// prop1.setValue(new Integer(11));
	// env.updateProperty(prop1);
	// assertEquals("Listener should have been called", 1, indicator);
	// indicator = 0;
	//
	// prop1.setValue(new Integer(11));
	// env.updateProperty(prop1);
	// assertEquals("Listener should not have been called", 0, indicator);
	// }

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironment#removeEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener)}
	 * .
	 */
	@Test
	public void testRemoveEnvListener() {
		env.addEnvListener(listener1);
		assertEquals("Number of registered listeners should be 1", 1, env.getAllListeners().size());
		env.removeEnvListener(listener1);
		assertEquals("Number of registered listeners should be 0", 0, env.getAllListeners().size());
	}

	/**
	 * @throws java.lang.Exception
	 *         Created by Sascha Holzhauer on 18.12.2009
	 */
	@After
	public void tearDown() throws Exception {
		env = null;
		prop1 = null;
		prop2 = null;
		listener1 = null;
		listener2 = null;
	}

	/**
	 * @return test
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(EnvironmentTest.class);
	}
}
