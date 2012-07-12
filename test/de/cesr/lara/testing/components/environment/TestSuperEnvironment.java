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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.environment.LaraEnvironment;
import de.cesr.lara.components.environment.LaraSuperEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironment;
import de.cesr.lara.components.environment.impl.LEnvironmentalProperty;
import de.cesr.lara.testing.LTestUtils;

/**
 * @author Sascha Holzhauer
 * 
 */
public class TestSuperEnvironment {

	protected LaraSuperEnvironment superEnv = null;
	protected LaraEnvironment subEnv = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initTestModel();

		subEnv = new LEnvironment();
		subEnv.addProperty(new LEnvironmentalProperty<Object>("Prop1",
				new Object(), subEnv));

		superEnv = new LEnvironment();
		superEnv.registerEnvironment(null, subEnv);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		superEnv = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironment#removePropertySubenv(java.lang.String)}
	 * .
	 */
	@Test
	public void testRemovePropertySubenv() {
		assertFalse(superEnv.removeProperty("Prop1"));
		assertTrue(superEnv.removePropertySubenv(
				LaraSuperEnvironment.ALL_CATEGORIES, "Prop1"));
		assertFalse(superEnv.removePropertySubenv(
				LaraSuperEnvironment.ALL_CATEGORIES, "Prop1"));

		subEnv.addProperty(new LEnvironmentalProperty<Object>("Prop1",
				new Object(), subEnv));
		assertTrue(superEnv.removePropertySubenv(null, "Prop1"));
		assertFalse(superEnv.removePropertySubenv(null, "Prop1"));
	}
}
