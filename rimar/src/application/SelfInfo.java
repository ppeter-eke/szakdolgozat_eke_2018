package application;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SelfInfo {

	private String companyName;
	private String companyAddress;
	private String companyPhone;
	private String companyEmail;
	private int numOfStoragePositions;
	private String notes;

	public SelfInfo() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT company_name, company_address, company_phone, company_email, number_of_storage_positions, notes from self_info";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				this.companyName = rs.getString(1);
				this.companyAddress = rs.getString(2);
				this.companyPhone = rs.getString(3);
				this.companyEmail = rs.getString(4);
				this.numOfStoragePositions = rs.getInt(5);
				this.notes = rs.getString(6);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	public String getCompany() {

		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyAddress() {
		return companyAddress;
	}

	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getCompanyEmail() {
		return companyEmail;
	}

	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}

	public int getNumOfStoragePositions() {
		return numOfStoragePositions;
	}

	public void setNumOfStoragePositions(int numOfStoragePositions) {
		this.numOfStoragePositions = numOfStoragePositions;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
