/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.testing.util;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * 
 * @author Sascha Holzhauer
 * @date 10.02.2010
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { DefaultLaraStorageImplTest.class, DefaultLaraLimitedCapacityStorageImplTest.class,
		DefaultLaraMemoryImplTest.class, DefaultLaraLimitedCapacityMemoryImplTest.class,
		DefaultLLimitedCapacityOverwriteMemoryTest.class, LRandomTest.class })
public class AllUtilTests {

}
