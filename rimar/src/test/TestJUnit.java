package test;

import static org.junit.Assert.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import application.HashPassword;
import application.MySqlConnection;
import application.administrator.AddPalletController;
import application.administrator.AddUserController;

public class TestJUnit {

	@Test
	public void databaseTest() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT number_of_storage_positions FROM self_info";

		int result = 0;

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result = rs.getInt(1);
			}
			assertEquals(5000, result);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	@Test
	public void passwordHashTest() throws NoSuchAlgorithmException, InvalidKeySpecException {

		String testPassword = "correcthorsebatterystaple";

		String testPasswordHash = HashPassword.generateStrongPasswordHash(testPassword);

		assertTrue(HashPassword.validatePassword(testPassword, testPasswordHash));

	}

	@Test
	public void removeDashesTest() {

		AddUserController controller = new AddUserController();

		String date = LocalDate.now().toString();

		String result = controller.removeDashes(date);

		boolean match = result.matches(("\\d+"));

		assertTrue(match);
	}

	@Test
	public void generatePalletIdTest() {

		AddPalletController controller = new AddPalletController();

		String latestPalletId = "";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id FROM pallet ORDER BY pallet_id DESC LIMIT 1";


		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				latestPalletId = rs.getString(1);
			}
			
			
			String generatedPalletId = controller.generatePalletId();
			
			Pattern pattern = Pattern.compile("P?N?[1-9]+");
			
			Matcher matcher1 = pattern.matcher(generatedPalletId);
			
			int newId = 0;
			int prevId = 0;
			
			if(matcher1.find()) {
				
				newId = Integer.parseInt(matcher1.group(0));
			}
			
			Matcher matcher2 = pattern.matcher(latestPalletId);
			
			if(matcher2.find()) {
				
				prevId = Integer.parseInt(matcher2.group(0));
			}
			
			prevId++;
			
			assertEquals(newId, prevId);
			

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		
	}

}
