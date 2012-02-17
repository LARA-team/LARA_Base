package de.cesr.lara.testing.components;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.LaraProperty;


/**
 * 
 */
public class LaraPropertyTest {

	private LaraProperty<?, Integer> prop1, prop2, prop3, prop4;

	private class TestProperty extends LaraProperty<TestProperty, Integer> {

		private final int	value;

		public TestProperty(String key, int value) {
			super(key);
			this.value = value;
		}

		public TestProperty(String key, int timestamp, int value) {
			super(key, timestamp);
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

	};

	/**
	 * 
	 */
	@Before
	public void setUp() {
		prop1 = new TestProperty("key01", 1, 1);
		prop2 = new TestProperty("key01", 2, 1);
		prop3 = new TestProperty("key01", 1, 3);
		prop4 = new TestProperty("key01", 1, 1);
	}

	/**
	 * 
	 */
	@Test
	public void testLaraProperty() {
		assertEquals("key01", prop1.getKey());
		assertTrue(prop1.getValue() == 1);
		assertTrue(prop1.getTimestamp() == 1);

		assertEquals(prop1, prop1);
		assertEquals(prop1, prop4);
		assertFalse(prop1.equals(prop2));
		assertFalse(prop1.equals(prop3));
		assertFalse(prop2.equals(prop3));

	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		prop1 = prop2 = prop3 = null;
	}

}
