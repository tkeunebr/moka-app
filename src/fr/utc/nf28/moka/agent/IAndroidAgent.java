package fr.utc.nf28.moka.agent;

/**
 * interface use to communicate with the agent
 */
public interface IAndroidAgent {
	//TODO add methods to communicate with other Jade agents

	/**
	 * send information about the user
	 */
	public void connectPlatform();

	/**
	 * create new item on platform
	 */
	public void createItem();

	/**
	 * lock item for edition
	 */
	public void lockItem();

	/**
	 * send real time modification
	 */
	public void editItem();
}