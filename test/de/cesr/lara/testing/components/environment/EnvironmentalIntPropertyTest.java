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

	LaraEnvironment env;
	LEnvironmentalIntProperty prop1, prop2, prop3;

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 22.12.2009
	 */
	@Before
	public void setUp() throws Exception {
		this.env = new LEnvironment();
		this.prop1 = new LEnvironmentalIntProperty("Property1", 1, env);
		this.prop2 = new LEnvironmentalIntProperty("Property1", 1, env);
		this.prop3 = new LEnvironmentalIntProperty("Property3", 3, env);
	}

	/**
	 * @throws java.lang.Exception
	 *             Created by Sascha Holzhauer on 22.12.2009
	 */
	@After
	public void tearDown() throws Exception {
		env = null;
		prop1 = prop2 = prop3 = null;
	}

	/**
	 * Test method for
	 * {@link de.cesr.lara.components.environment.impl.LEnvironmentalIntProperty#equals(java.lang.Object)}
	 * .
	 */
	@Test
	public final void testEqualsObject() {
		assertEquals("Proeprties are equal", prop1.equals(prop1), true);
		assertEquals("Proeprties are equal", prop1.equals(prop2), true);
		assertEquals("Proeprties are not equal", prop1.equals(prop3), false);
	}

}