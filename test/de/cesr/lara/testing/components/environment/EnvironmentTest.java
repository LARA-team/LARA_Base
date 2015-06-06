/**
 * This file is part of
 * 
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 * 
 * Copyright (C) 2012 Center for Environmental Systems Research, Kassel, Germany
 * 
 * LARA is free software: You can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * LARA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package de.cesr.lara.testing.components.environment;


import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.LaraEnvironmentListener;
import de.cesr.lara.components.environment.impl.LAbstractEnvironmentalProperty;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LModel;


/**
 * 
 * @author Sascha Holzhauer
 * @date 18.12.2009
 * 
 */
public class EnvironmentTest {

	/**
	 * @return test
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(EnvironmentTest.class);
	}

	LaraModel model;
	LaraEnvironment env;
	LEnvironmentalIntProperty prop1;
	LEnvironmentalIntProperty prop2;
	LaraEnvironmentListener listener1;
	LaraEnvironmentListener listener2;

	int indicator;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		// required for stepping:
		// TODO: adapt to new LModel
		// model = LModel.getNewModel();

		this.env = new LEnvironment();
		this.prop1 = new LEnvironmentalIntProperty(LModel.getModel(),
				"Property1", 1, env);
		this.prop2 = new LEnvironmentalIntProperty(LModel.getModel(),
				"Property2", 2, env);
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
	// assertEquals("Number of registered listeners should be 1", 1,
	// env.getAllListeners().size());
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
	// assertEquals("Number of registered listeners should be 1", 1,
	// env.getAllListeners().size());
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
	 * @throws java.lang.Exception
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
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironment#addEnvListener(de.cesr.lara.components.environment.LaraEnvironmentListener)}
	 * .
	 */
	@Test
	public void testAddEnvListenerLaraEnvironmentListener() {
		env.addEnvListener(listener1);
		assertEquals("Number of registered listeners should be 1", 1, env.getAllListeners().size());
	}

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

}