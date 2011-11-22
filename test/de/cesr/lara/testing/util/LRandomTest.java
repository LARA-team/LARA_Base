/**
 * LARA - Lightweight Architecture for boundedly Rational citizen Agents
 *
 * Center for Environmental Systems Research, Kassel
 * Created by Sascha Holzhauer on 29.09.2010
 */
package de.cesr.lara.testing.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;
import de.cesr.lara.components.util.impl.LRandomService;
import de.cesr.lara.components.util.impl.LUniformController;
import de.cesr.lara.components.util.logging.impl.Log4jLogger;


/**
 * 
 */
public class LRandomTest {

	LRandomService			random;
	int						randomSeed	= 0;

	/**
	 * Logger
	 */
	static private Logger	logger		= Log4jLogger.getLogger(LRandomTest.class);

	private static final String RANDOM_STREAM = "Random Stream";
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		random = new LRandomService(randomSeed);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		random = null;
	}

	/**
	 * Test method for {@link de.cesr.lara.components.util.impl.LRandomService#getUniform()}.
	 */
	@Test
	public final void testGetUniform() {
		Uniform uniform = random.getUniform();
		for (int i = 0; i < 10; i++) {
			System.out.println(uniform.nextDouble());
		}
		for (int i = 0; i < 10; i++) {
			System.out.println(uniform.nextIntFromTo(0, 10));
		}
	}

	/**
	 * Test method for {@link de.cesr.lara.components.util.impl.LRandomService#setSeed(int)}.
	 */
	@Test
	public final void testSetSeed() {
		random.createNormal(1, 1);
		Uniform uniform = random.getUniform();
		random.setSeed(10);

		assertNotSame(uniform, random.getUniform());
		assertNull(random.getNormal());

		logger
				.warn("If a WARN message (Normal distributions has not been created!) was printed above this is intented");

	}

	/**
	 * Check whether a registered distribution can be returned
	 */
	@Test
	public final void testDistributionRegistration() {
		AbstractDistribution dist = new LUniformController(new MersenneTwister(9));
		random.registerDistribution(dist, RANDOM_STREAM);
		assertEquals(dist, random.getDistribution(RANDOM_STREAM));
	}

	/**
	 * Test method for {@link de.cesr.lara.components.util.impl.LRandomService#getSeed()}.
	 */
	@Test
	public final void testGetSeed() {
		assertEquals(randomSeed, random.getSeed());
	}

}
