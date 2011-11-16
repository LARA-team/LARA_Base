package de.cesr.lara.components.container.memory;

/**
 * The interface for memory implementations that add temporal retention
 * functionality (temporal decay) to the basic memory component of LARA (see
 * {@link AncientLaraMemory}).
 * 
 * @author elbers
 * 
 */
public interface LaraLimitedRetentionMemory extends LaraMemory {

	/**
	 * Constant to indicate that the retention of the item/items is to be
	 * (virtually) unlimited, i.e. the item/items will not be forgotten
	 * (removed) unless capacity management demands it.
	 */
	public static final int UNLIMITED_RETENTION_TIME = -1;

	/**
	 * Returns the default retention time currently set. Newly memorised items
	 * that have not been assigned a retention time explicitly will be
	 * initialised with the default retention time.
	 * 
	 * @return the current default retention time.
	 */
	@Override
	public int getDefaultRetentionTime();

	/**
	 * Attempts to add a new item to memory having the specified {@code float}
	 * value and retention time and identified by the specified key.
	 * 
	 * @param key
	 *            identifier describing the given value, e.g. on the basis of a
	 *            user-defined ontology.
	 * @param value
	 *            the value associated with the given key.
	 * @param retentionTime
	 *            the initial time-to-live for this item.
	 */
	public void memorise(String key, float value, int retentionTime);

	/**
	 * Attempts to add a new item to memory having the specified {@code int}
	 * value and retention time and identified by the specified key.
	 * 
	 * @param key
	 *            identifier describing the given value, e.g. on the basis of a
	 *            user-defined ontology.
	 * @param value
	 *            the value associated with the given key.
	 * @param retentionTime
	 *            the initial time-to-live for this item.
	 */
	public void memorise(String key, int value, int retentionTime);

	/**
	 * Attempts to add a new item to memory having the specified ({@link String}
	 * ) value and retention time and identified by the specified key.
	 * 
	 * @param key
	 *            identifier describing the given value, e.g. on the basis of a
	 *            user-defined ontology.
	 * @param value
	 *            the value associated with the given key.
	 * @param retentionTime
	 *            the initial time-to-live for this item.
	 */
	public void memorise(String key, String value, int retentionTime);

	/**
	 * Sets the default retention time. Newly memorised items that have not been
	 * assigned a retention time explicitly will be initialised with the default
	 * retention time.
	 * 
	 * @param defaultRetentionTime
	 */
	@Override
	public void setDefaultRetentionTime(int defaultRetentionTime);

}
