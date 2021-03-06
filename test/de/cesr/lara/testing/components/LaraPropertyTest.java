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
package de.cesr.lara.testing.components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.eventbus.events.LModelStepEvent;
import de.cesr.lara.components.eventbus.impl.LEventbus;
import de.cesr.lara.components.model.impl.LModel;
import de.cesr.lara.testing.LTestUtils;

/**
 * 
 */
public class LaraPropertyTest {

	private class TestProperty extends LaraProperty<TestProperty, Integer> {

		private final int value;

		public TestProperty(String key, int value) {
			super(LModel.getModel(), key);
			this.value = value;
		}

		@Override
		public TestProperty getModifiedProperty(Integer value) {
			return null; // Test unnecessary
		}

		@Override
		public Integer getValue() {
			return value;
		}
	}

	private LaraProperty<?, Integer> prop1, prop2, prop3, prop4;;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		LTestUtils.initTestModel();
		LEventbus.getInstance().publish(new LModelStepEvent());

		prop1 = new TestProperty("key01", 1);
		prop3 = new TestProperty("key01", 3);
		prop4 = new TestProperty("key01", 1);

		LEventbus.getInstance().publish(new LModelStepEvent());

		prop2 = new TestProperty("key01", 1);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		prop1 = prop2 = prop3 = null;
	}

	/**
	 * 
	 */
	@Test
	public void testLaraProperty() {
		assertEquals("key01", prop1.getKey());
		assertEquals(1, prop1.getValue().intValue());
		assertEquals(1, prop1.getTimestamp());

		assertEquals(prop1, prop1);
		assertEquals(prop1, prop4);
		assertFalse(prop1.equals(prop2));
		assertFalse(prop1.equals(prop3));
		assertFalse(prop2.equals(prop3));
	}
}
