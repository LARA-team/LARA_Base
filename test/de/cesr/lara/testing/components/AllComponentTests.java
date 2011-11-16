/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.testing.components;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.lara.testing.components.decision.AllDecisionTests;
import de.cesr.lara.testing.components.impl.AllComponentsImplTests;
import de.cesr.lara.testing.components.preprocessor.AllPreprocessorTests;

/**
 * 
 * @author Sascha Holzhauer
 * @date 10.02.2010
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ LaraPropertyTest.class, AllComponentsImplTests.class, AllDecisionTests.class, /* AllEnvironmentalTests. class, */ AllPreprocessorTests.class
/*, AllMemoryTests.class*/ })
public class AllComponentTests {

}
