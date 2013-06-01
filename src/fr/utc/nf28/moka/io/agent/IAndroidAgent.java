package fr.utc.nf28.moka.io.agent;

/**
 * interface use to communicate with the agent
 */
public interface IAndroidAgent {
	//TODO add methods to communicate with other Jade agents

	/**
	 * send information about the user
	 */
	public void connectPlatform(String firstName, String lastName, String ip);

	/**
	 * create new item on platform
	 *
	 * @param type item type (uml, text, img)
	 */
	public void createItem(String type);

	/**
	 * delete item on platform
	 */
	public void deleteItem(int itemId);

	/**
	 * move item
	 */
	public void moveItem(int itemId, int direction, int velocity);

	/**
	 * resize item
	 */
	public void resizeItem(int itemId, int direction);

	/**
	 * lock item for edition
	 */
	public void lockItem(int itemId);

	/**
	 * unlock item after edition
	 *
	 * @param itemId item id
	 */
	public void unlockItem(int itemId);

	/**
	 * send real time modification
	 */
	public void editItem();
}
