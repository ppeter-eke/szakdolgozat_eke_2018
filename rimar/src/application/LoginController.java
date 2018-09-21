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

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class LoginController {

	@FXML
	private Button btnLogin;
	@FXML
	private TextField txtUsername;
	@FXML
	private TextField txtPassword;
	@FXML
	private Label lblStatus;
	
	@FXML
	private Button btnAbout;
	
	@FXML
	private Button btnOK;

	private static int userid;
	private static String username;
	private static int user_roleid;

	@FXML
	private void login() throws Exception {

		String sql = "SELECT * FROM users WHERE user_login = ?";
		MySqlConnection myConnection = new MySqlConnection();

		try {

			PreparedStatement statement = myConnection.connect().prepareStatement(sql);

			statement.setString(1, txtUsername.getText());

			ResultSet rs = statement.executeQuery();

			if (rs.next()) {

				String password = rs.getString("user_pass");

				if (HashPassword.validatePassword(txtPassword.getText(), password)) {

					user_roleid = rs.getInt("user_role_id");
					username = rs.getString("user_name");
					userid = rs.getInt("user_id");

					Stage primaryStage = new Stage();

					Parent root = null;

					String role = "";

					if (user_roleid == 1) {

						role = " raktáros";

						root = FXMLLoader.load(getClass().getResource("/application/storekeeper/StorekeeperMain.fxml"));

					} else if (user_roleid == 2) {

						role = " adminisztrátor";

						root = FXMLLoader
								.load(getClass().getResource("/application/administrator/AdministratorMain.fxml"));
					} else {

						role = " raktárvezető";

						root = FXMLLoader.load(
								getClass().getResource("/application/warehousemanager/WarehouseManagerMain.fxml"));
					}

					Scene scene = new Scene(root);
					scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
					primaryStage.setScene(scene);
					primaryStage.show();
					primaryStage.setResizable(false);
					
					String time = LocalDateTime.now().toString();
					
					Event event = new Event(time, username, "Bejelentkezés", "Bejelentkezett a rendszerbe mint" + role);
					event.createEvent();

					closeLogin();

				} else {
					lblStatus.setText("Hibás felhasználónév vagy jelszó");
					txtPassword.setText("");
					return;
				}

			} else {
				lblStatus.setText("Hibás felhasználónév vagy jelszó");
				txtPassword.setText("");
				return;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			myConnection.disconnect();
		}

	}

	private void closeLogin() {

		Stage stage1 = (Stage) txtUsername.getScene().getWindow();

		stage1.close();

	}

	public static String getUsername() {

		return username;
	}

	public static int getUserid() {

		return userid;

	}
	
	@FXML
	private void openAbout() {
		
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/About.fxml"));

			Scene scene = new Scene(root);
			
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

			

		} catch (Exception e) {
			e.printStackTrace();

		}

		
	}
	
	@FXML
	private void closeAbout() {
		
		Stage stage1 = (Stage) btnOK.getScene().getWindow();

		stage1.close();
	}

}
