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

package application.administrator;

import javafx.scene.Parent;
import application.*;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class AddUserController {

	@FXML
	private Button btnBack;

	@FXML
	private Button btnAddUser;

	@FXML
	private RadioButton rdbStorekeeper;

	@FXML
	private RadioButton rdbAdministrator;

	@FXML
	private TextField txtLogin;

	@FXML
	private TextField txtName;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker dtpBirthday;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblRequiredLogin;

	@FXML
	private Label lblRequiredName;

	@FXML
	private Label lblRequiredEmail;

	private LocalDate today;

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	@FXML
	private void initialize() {

		rdbStorekeeper.setSelected(true);

		today = LocalDate.now();

		dtpBirthday.setValue(today);

	}

	private static boolean validateEmail(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	@FXML
	private void add() throws Exception {

		lblRequiredLogin.setText("");
		lblRequiredName.setText("");
		lblRequiredEmail.setText("");
		lblStatus.setText("");

		if (txtLogin.getText().isEmpty()) {

			lblRequiredLogin.setText("Kötelező mező!");
			return;
		}

		if (txtName.getText().isEmpty()) {

			lblRequiredName.setText("Kötelező mező!");
			return;

		}

		if (txtEmail.getText().isEmpty()) {
			lblRequiredEmail.setText("Kötelező mező!");
			return;
		}

		if (!validateEmail(txtEmail.getText())) {

			lblRequiredEmail.setText("Hibás formátum!");

		}

		String login = txtLogin.getText();

		if (!isValidLogin(login)) {
			lblRequiredLogin.setText("Foglalt név!");
			return;
		}

		String name = txtName.getText();
		String email = txtEmail.getText();
		String birthday = dtpBirthday.getValue().toString();

		String defaultPassword = removeDashes(birthday);

		String hashedDefaultPassword = HashPassword.generateStrongPasswordHash(defaultPassword);

		int roleid = 0;

		if (rdbStorekeeper.isSelected()) {

			roleid = 1;

		} else {
			roleid = 2;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "INSERT INTO users (user_login, user_name, user_pass, user_email, user_birthdate, user_registrationdate, user_role_id) VALUES (?,?,?,?,?,?,?)";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setString(1, login);
			st.setString(2, name);
			st.setString(3, hashedDefaultPassword);
			st.setString(4, email);
			st.setString(5, birthday);
			st.setString(6, today.toString());
			st.setInt(7, roleid);

			st.executeUpdate();

			String roleEvent = "";

			if (roleid == 1) {

				roleEvent = "raktáros";

			} else {
				roleEvent = "adminisztrátor";
			}

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Regisztráció",
					"Új felhasználót regisztrált, jogosultsága " + roleEvent + " azonosítója: " + login);
			
			event.createEvent();

			lblStatus.setText("Új felhasználó sikeresen hozzáadva!");

			txtLogin.setText("");
			txtName.setText("");
			txtEmail.setText("");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();

		}

	}

	private boolean isValidLogin(String loginName) {

		MySqlConnection myConnection = new MySqlConnection();

		try {

			String sql = "SELECT user_login FROM users WHERE user_login = ?";

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setString(1, loginName);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return true;
	}

	public String removeDashes(String date) {

		String dashLess = "";

		char[] characters = date.toCharArray();

		for (int i = 0; i < characters.length; i++) {

			if (Character.isDigit(characters[i])) {
				dashLess += characters[i];
			}
		}

		return dashLess;
	}

	@FXML
	private void back() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/AdministratorMain.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

			closeWindow();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	private void closeWindow() {

		Stage stage1 = (Stage) btnBack.getScene().getWindow();

		stage1.close();

	}

}
