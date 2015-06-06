package de.cesr.lara.testing.components.model;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.cesr.lara.components.eventbus.events.LaraEvent;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LAbstractModel;
import de.cesr.lara.components.model.impl.LAbstractStandaloneSynchronisedModel;
import de.cesr.lara.components.model.impl.LModel;

public class LaraMultipleModelTest {

	LaraModel model1;

	@Before
	public void setUp() throws Exception {
		this.model1 = new LAbstractStandaloneSynchronisedModel("1") {
			@Override
			public void onEvent(LaraEvent event) {
			}

			@Override
			public String toString() {
				return "TestModel1";
			}
		};
		((LAbstractModel) model1).init();
	}

	@Test
	public void test() {
		LaraModel model2 = new LAbstractStandaloneSynchronisedModel("2") {
			@Override
			public void onEvent(LaraEvent event) {
			}

			@Override
			public String toString() {
				return "TestModel2";
			}
		};
		((LAbstractModel) model2).init();
		assertTrue(LModel.getModel("1") != LModel.getModel("2"));
		
		this.model1.getPrefRegistry().register("PreferenceModel1");
		assertTrue(LModel.getModel("1").getPrefRegistry()
				.isRegistered("PreferenceModel1"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSameNameModel() {
		LaraModel model2 = new LAbstractStandaloneSynchronisedModel("1") {
			@Override
			public void onEvent(LaraEvent event) {
			}

			@Override
			public String toString() {
				return "TestModel1";
			}
		};
		((LAbstractModel) model2).init();
	}

	@After
	public void tidyUp() {
		LModel.reset();
	}
}
