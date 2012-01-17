package de.cesr.lara.testing;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.cesr.lara.testing.components.AllComponentTests;
import de.cesr.lara.testing.util.AllUtilTests;


/**
 * run all tests at once
 */
@RunWith(Suite.class)
@SuiteClasses({ AllComponentTests.class, AllUtilTests.class })
public class AllLaraTests {
}
