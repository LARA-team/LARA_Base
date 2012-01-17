/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.testing.components.preprocessor;


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
@SuiteClasses({ DefaultConfiguratorTest.class, LDefaultBOCollectorTest.class,
		LPpEventBusTest.class, LPreprocessorBuilderTest.class })
public class AllPreprocessorTests {

}
