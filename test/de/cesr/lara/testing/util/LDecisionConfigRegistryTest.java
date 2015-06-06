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

import de.cesr.lara.components.decision.LaraDecisionConfiguration;
import de.cesr.lara.components.decision.impl.LDecisionConfiguration;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.components.util.LaraDecisionConfigRegistry;
import de.cesr.lara.testing.LTestUtils;

/**
 * @author Sascha Holzhauer
 *
 */
public class LDecisionConfigRegistryTest {

	LaraDecisionConfigRegistry registry;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		LTestUtils.initBareTestModel(null);
		this.registry = LModel.getModel().getDecisionConfigRegistry();
		this.registry.reset();
	}

	@Test()
	public void testRegisterAndRetrieve() {
		LaraDecisionConfiguration dConfig1 = new LDecisionConfiguration(
				"dconfig.test1");
		this.registry.register(dConfig1);
		assertEquals("dconfig.test1", this.registry.get("dconfig.test1")
				.getId());
		assertEquals(dConfig1, this.registry.get("dconfig.test1"));

		LaraDecisionConfiguration dConfig2 = new LDecisionConfiguration(
				"dconfig.test2");
		this.registry.register(dConfig2);

		assertEquals("dconfig.test2", this.registry.get("dconfig.test2")
				.getId());
		assertEquals(dConfig2, this.registry.get("dconfig.test2"));
	}

	@Test
	public void testRemove() {
		LaraDecisionConfiguration dConfig1 = new LDecisionConfiguration(
				"dconfig.test1");
		this.registry.register(dConfig1);

		assertTrue(this.registry.isRegistered("dconfig.test1"));
		assertTrue(this.registry.isRegistered(dConfig1));

		assertTrue(this.registry.remove("dconfig.test1"));
		assertFalse(this.registry.isRegistered("dconfig.test1"));
		assertFalse(this.registry.isRegistered(dConfig1));

		LaraDecisionConfiguration dConfig2 = new LDecisionConfiguration(
				"dconfig.test2");
		this.registry.register(dConfig2);

		assertTrue(this.registry.isRegistered("dconfig.test2"));
		assertTrue(this.registry.isRegistered(dConfig2));

		assertTrue(this.registry.remove(dConfig2));
		assertFalse(this.registry.isRegistered("dconfig.test2"));
		assertFalse(this.registry.isRegistered(dConfig2));
	}

	@Test(expected = IllegalStateException.class)
	public void testRetrieveUnregiseredWithError() {
		this.registry.get("dconfig.test");
	}

	@Test(expected = IllegalStateException.class)
	public void testReregisterWithError() {
		LaraDecisionConfiguration dConfig1 = new LDecisionConfiguration(
				"dconfig.test1");
		this.registry.register(dConfig1);
		this.registry.register(dConfig1);
	}

	public void testModelReset() {
		LaraDecisionConfiguration dConfig1 = new LDecisionConfiguration(
				"dconfig.test1");
		this.registry.register(dConfig1);
		assertTrue(this.registry.isRegistered(dConfig1));
		assertTrue(this.registry.isRegistered("dconfig.test1"));
		LModel.getModel().resetLara();
		assertFalse(this.registry.isRegistered("dconfig.test1"));
		assertFalse(this.registry.isRegistered(dConfig1));
	}
}
