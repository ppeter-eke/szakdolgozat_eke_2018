/*******************************************************************************
 * Szakdolgozat
 * Raktári információs rendszer fejlesztése Java nyelven
 *
 * Eszterházy Károly Egyetem
 * Alkalmazott Informatika Tanszék
 *
 * Készítette: Pálosi Péter gazdaságinformatikus BSc levelező tagozat
 *
 * Gyöngyös, 2018
 *******************************************************************************/



package application;

import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Event {

	private String time;
	private String userName;
	private String activityName;
	private String activityDescription;

	public Event(String time, String userName, String activityName, String activityDescription) {

		this.time = time;
		this.userName = userName;
		this.activityName = activityName;
		this.activityDescription = activityDescription;
	}
	
	public void createEvent() {
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "INSERT INTO event_table (activity_timestamp, user_name, activity_name, activity_description) VALUES (?,?,?,?)";
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			
			ps.setString(1, this.time);
			ps.setString(2, this.userName);
			ps.setString(3, this.activityName);
			ps.setString(4, this.activityDescription);
			
			ps.executeUpdate();
			
		}catch(SQLException e) {
			
			e.printStackTrace();
		}finally {
			
			myConnection.disconnect();
		}
		
		
	}
	

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityDescription() {
		return activityDescription;
	}

	public void setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
	}

}