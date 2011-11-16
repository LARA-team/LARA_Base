/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 10.02.2010
 */
package de.cesr.lara.testing.components.impl;


/**
 *
 * @author Sascha Holzhauer
 * @date 10.02.2010 
 *
 */
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( { DefaultLaraAgentCompTest.class, LGeneralBoTest.class, AbstractLaraAgentTest.class })
public class AllComponentsImplTests {

}
