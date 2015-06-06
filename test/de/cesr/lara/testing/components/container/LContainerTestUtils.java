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
package de.cesr.lara.testing.components.container;


import de.cesr.lara.components.LaraProperty;
import de.cesr.lara.components.container.memory.LaraMemory;
import de.cesr.lara.components.container.storage.LaraStorage;
import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.model.impl.LModel;


/**
 * @author Sascha Holzhauer
 * 
 */
public class LContainerTestUtils {

	public static class LSubTestProperty extends LTestProperty {

		public LSubTestProperty(LaraModel lmodel, String key, String value) {
			super(lmodel, key, value);
		}
	}

	public static class LTestProperty extends LaraProperty<LTestProperty, String> {

		String value;

		LaraModel lmodel;

		public LTestProperty(String key, String value) {
			this(LModel.getModel(), key, value);
		}

		public LTestProperty(LaraModel lmodel, String key, String value) {
			super(lmodel, key);
			this.lmodel = lmodel;
			this.value = value;
		}

		@Override
		public LTestProperty getModifiedProperty(String value) {
			return new LTestProperty(lmodel, getKey(), value);
		}

		@Override
		public String getValue() {
			return value;
		}
	}

	/**
	 * counter for labeling properties
	 */
	private static int count = 0;

	public static void storeSixStandardEntries(LaraMemory<? super LTestProperty> storage) {
		storage.memorize(new LTestProperty(LModel.getModel(), "key01",
				"value01"));
		storage.memorize(new LTestProperty(LModel.getModel(), "key02",
				"value02"));
		storage.memorize(new LTestProperty(LModel.getModel(), "key03",
				"value03"));
		storage.memorize(new LTestProperty(LModel.getModel(), "key04",
				"value04"));
		storage.memorize(new LTestProperty(LModel.getModel(), "key05",
				"value05"));
		storage.memorize(new LTestProperty(LModel.getModel(), "key06",
				"value06"));
	}

	public static void storeSixStandardEntries(LaraStorage<? super LTestProperty> storage) {
		storage.store(new LTestProperty(LModel.getModel(), "key01", "value01"));
		storage.store(new LTestProperty(LModel.getModel(), "key02", "value02"));
		storage.store(new LTestProperty(LModel.getModel(), "key03", "value03"));
		storage.store(new LTestProperty(LModel.getModel(), "key04", "value04"));
		storage.store(new LTestProperty(LModel.getModel(), "key05", "value05"));
		storage.store(new LTestProperty(LModel.getModel(), "key06", "value06"));
	}

	public static void storeSomeEntries(LaraMemory<? super LTestProperty> memory, int num) {
		for (int i = 0; i < num; i++) {
			memory.memorize(new LTestProperty(LModel.getModel(), "key" + count,
					"value" + count++));
		}
	}

	public static void storeSomeEntries(LaraStorage<? super LTestProperty> storage, int num) {
		for (int i = 0; i < num; i++) {
			storage.store(new LTestProperty(LModel.getModel(), "key" + count,
					"value" + count++));
		}
	}
}