package application.administrator;


public class OffloadList {

	private int offLoadListTaskNumber;

	private int numberOfPallets; 
	
	private String creationDate;

	private String completionDate;

	private String userName;

	private int active;

	private int started;

	private int complete;

	private int archived;

	public OffloadList(int offLoadListTaskNumber, int numberOfPallets, String creationDate, String completionDate,
			String userName, int active, int started, int complete) {
		super();
		this.offLoadListTaskNumber = offLoadListTaskNumber;
		this.numberOfPallets = numberOfPallets;
		this.creationDate = creationDate;
		this.completionDate = completionDate;
		this.userName = userName;
		this.active = active;
		this.started = started;
		this.complete = complete;
	}

	public int getOffLoadListTaskNumber() {
		return offLoadListTaskNumber;
	}

	public void setOffLoadListTaskNumber(int offLoadListTaskNumber) {
		this.offLoadListTaskNumber = offLoadListTaskNumber;
	}

	public int getNumberOfPallets() {
		return numberOfPallets;
	}

	public void setNumberOfPallets(int numberOfPallets) {
		this.numberOfPallets = numberOfPallets;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public int getStarted() {
		return started;
	}

	public void setStarted(int started) {
		this.started = started;
	}

	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}

	public int getArchived() {
		return archived;
	}

	public void setArchived(int archived) {
		this.archived = archived;
	}

}