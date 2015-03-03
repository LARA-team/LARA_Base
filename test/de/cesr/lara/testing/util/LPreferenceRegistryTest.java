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
package de.cesr.lara.testing.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraPreferenceRegistry;

/**
 * @author Sascha Holzhauer
 *
 */
public class LPreferenceRegistryTest {

	LaraPreferenceRegistry registry;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.registry = LModel.getModel().getPrefRegistry();
		this.registry.reset();
	}

	@Test()
	public void testRegisterAndRetrieve() {
		this.registry.register("test.some");
		assertEquals("test.some", this.registry.get("test.some").getId());

		this.registry.register("test.some.one");
		this.registry.register("test.some.two");
		this.registry.register("test.some.three");
		assertEquals("test.some.one", this.registry.get("test.some.one")
				.getId());
		assertEquals("test.some.two", this.registry.get("test.some.two")
				.getId());
		assertEquals("test.some.three", this.registry.get("test.some.three")
				.getId());
	}

	@Test
	public void testRemove() {
		this.registry.register("test.remove");
		assertTrue(this.registry.isRegistered("test.remove"));
		assertTrue(this.registry.remove("test.remove"));
		assertFalse(this.registry.isRegistered("test.remove"));
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveUnregiseredWithError() {
		this.registry.get("test.same");
	}

	@Test(expected = IllegalStateException.class)
	public void testReregisterWithError() {
		this.registry.register("test.same");
		this.registry.register("test.same");
	}

	public void testModelReset() {
		this.registry.register("test.some.one");
		this.registry.register("test.some.two");
		assertTrue(this.registry.isRegistered("test.some.one"));
		assertTrue(this.registry.isRegistered("test.some.two"));
		LModel.getModel().resetLara();
		assertFalse(this.registry.isRegistered("test.some.one"));
		assertFalse(this.registry.isRegistered("test.some.two"));
	}
}
