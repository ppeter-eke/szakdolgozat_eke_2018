package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordChangeController {

	@FXML
	private PasswordField pwOld;

	@FXML
	private PasswordField pwNew;

	@FXML
	private PasswordField pwConfirm;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblRequired;

	@FXML
	private Label lblNoMatch;

	@FXML
	private Button btnOK;

	@FXML
	private Button btnBack;
	
	@FXML
	private void changePass() throws Exception {

		lblRequired.setText("");
		lblNoMatch.setText("");
		lblStatus.setText("");

		if (pwOld.getText().isEmpty()) {

			lblRequired.setText("Kérem töltse ki!");

			return;
		}

		if (!pwNew.getText().equals(pwConfirm.getText())) {

			lblNoMatch.setText("Nem egyezik az új jelszó!");

			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		int userid = LoginController.getUserid();

		String sql = "SELECT user_pass FROM users WHERE user_id = ?";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setInt(1, userid);

			ResultSet rs = st.executeQuery();

			rs.next();

			String oldPassInDb = rs.getString("user_pass");

			if (!HashPassword.validatePassword(pwOld.getText(), oldPassInDb)) {

				lblStatus.setText("Régi jelszó nem megfelelő!");
				lblStatus.setStyle("-fx-text-fill: red");
				return;
			}

			String sql1 = "UPDATE users SET user_pass = ? WHERE user_id = ?";

			PreparedStatement st1 = myConnection.connect().prepareStatement(sql1);

			String newPass = HashPassword.generateStrongPasswordHash(pwNew.getText());

			st1.setString(1, newPass);
			st1.setInt(2, userid);

			st1.executeUpdate();

			lblStatus.setText("Jelszó sikeresen megváltoztatva!");
			lblStatus.setStyle("fx-text-fill: green");

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();

			pwNew.setText("");
			pwOld.setText("");
			pwConfirm.setText("");
		}
	}
	
	@FXML
	private void closePasswordChange() {

		Stage stage1 = (Stage) lblStatus.getScene().getWindow();

		stage1.close();

	}

}
