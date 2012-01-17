package de.cesr.lara.components.model.impl;

import de.cesr.lara.components.model.LaraModel;
import de.cesr.lara.components.util.impl.LVersionInfo;

/**
 * Base class for models using LARA that should ensure that only one instance of
 * LaraModel is created. However, since it should be possible to create
 * subclasses of {@link LAbstractModel} it is not possible to ensure that there
 * are no more than one instances of {@link LaraModel}.
 * 
 * @note tasks: initialise model context, initialise database, read config,
 *       setup simulation, setup agents, setup logger, simulate time stamp
 * @author Daniel Klemm
 * @author Sascha Holzhauer
 * 
 *         changes: - extract abstract makeDecisions() (SH) - abstract
 *         initAgents() (SH)
 * 
 */
public final class LModel {

	/**
	 * model
	 */
	protected static LaraModel model;

	/**
	 * @return model
	 */
	public static LaraModel getModel() {
		if (model == null) {
			throw new IllegalStateException("model was not set");
		}
		return model;
	}

	/**
	 * Returns a string containing current SVN revision number and data and time
	 * of last build.
	 * 
	 * @return version information
	 */
	public static String getVersionInfo() {
		return "LARA: " + LVersionInfo.revisionNumber + "/"
				+ LVersionInfo.timeStamp;
	}

	/**
	 * @param model
	 * @return new model Created by klemm on 16.02.2010
	 */
	public static LaraModel setNewModel(LaraModel model) {
		LModel.model = model;
		return getModel();
	}

	private LModel() {
	}
}
