package de.cesr.lara.testing.components.container;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.container.LaraCapacityManageableContainer;
import de.cesr.lara.components.container.LaraContainer;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.memory.impl.LDefaultLimitedCapacityMemory;
import de.cesr.lara.components.util.impl.LCapacityManagers;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;
import de.cesr.lara.testing.components.container.LContainerTestUtils.MyProperty;


/**
 * Tests the memory functionality with respect to the capacity management
 */
public class LDefaultLimitedCapacityMemoryTest {

	/**
	 * Logger
	 */
	static private Logger	logger	= Log4jLogger.getLogger(LDefaultLimitedCapacityMemoryTest.class);

	LaraMemory<MyProperty>	memory;

	/**
	 * 
	 */
	@Before
	public void setUp() {
		memory = new LDefaultLimitedCapacityMemory<MyProperty>(LCapacityManagers.<MyProperty> makeFIFO(), 7);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCapacity() {
		storeSomeEntries(6, 1);

		assertTrue("memory was initialised with a capacity of 7", memory.getCapacity() == 7);
		assertTrue("6 properties were memorised", memory.getSize() == 6);

		memory.memorize(new MyProperty("key07", "value07", 2));
		assertTrue("6 + 1 properties were memorised", memory.getSize() == 7);
		assertTrue("7 properties added at capacity of 7 means full", memory.isFull());

		memory.memorize(new MyProperty("key08", "value08", 2));
		assertTrue(memory.getSize() == 7);
		assertFalse("The first property should have been removed now", memory.contains("key01"));

		logger.info(memory);

		MyProperty propertyX = null;
		propertyX = memory.recall("key08");
		assertEquals(propertyX.getValue(), "value08");

		storeSomeEntries(6, 2);
		assertTrue("After inserting several properties memory size should remain 7", memory.getSize() == 7);

		// change capacity to 0:
		LaraCapacityManageableContainer<MyProperty> cmStorage = (LaraCapacityManageableContainer<MyProperty>) memory;
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);

		assertTrue(memory.getCapacity() == LaraContainer.UNLIMITED_CAPACITY);
		storeSomeEntries(100, 2);
		assertTrue("7 + 100 (at unlimited capcaity) = 107", memory.getSize() == 107);

		logger.info(memory);

		// change capacity to 10:
		cmStorage.setCapacity(10);
		logger.info(memory);

		assertTrue("After setting capacity of memory of size 107 to 10 size should be 10", memory.getSize() == 10);
		assertTrue(memory.getCapacity() == 10);
		logger.info(memory);

		// change capacity to 0:
		cmStorage.setCapacity(0);
		assertTrue(memory.getSize() == 0);
		assertTrue(memory.getCapacity() == 0);
		logger.info(memory);

		// change capacity back to 0:
		cmStorage.setCapacity(LaraContainer.UNLIMITED_CAPACITY);
		storeSomeEntries(100, 2);
		assertTrue(memory.getSize() == 100);
		logger.info(memory);
	}

	/**
	 * 
	 */
	@After
	public void tearDown() {
		memory = null;
	}

	/**
	 * counter for labeling properties
	 */
	private static int	count	= 0;

	private void storeSomeEntries(int num, int step) {
		for (int i = 0; i < num; i++) {
			memory.memorize(new MyProperty("key" + count, "value" + count++, step));
		}
	}

}
