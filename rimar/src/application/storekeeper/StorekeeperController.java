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

package application.storekeeper;

import application.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

import application.LoginController;
import application.MySqlConnection;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class StorekeeperController {

	@FXML
	private Label lblName;

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnPasswordChange;

	@FXML
	private Button btnRelocation;

	@FXML
	private Button btnDataSupervisor;

	@FXML
	private Button btnMovement;

	@FXML
	private Button btnPalletBack;

	@FXML
	private Button btnHelp;

	@FXML
	private Button btnOK;

	private static int taskNumber;

	@FXML
	public void initialize() {

		lblName.setText(LoginController.getUsername());
	}

	@FXML
	private void logout() {
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/Login.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Kijelentkezés",
					"Kijelentkezett a rendszerből");

			event.createEvent();

			closeWindow();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void closeWindow() {

		Stage stage1 = (Stage) btnLogout.getScene().getWindow();

		stage1.close();
	}

	@FXML
	private void openPasswordChange() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/PasswordChange.fxml"));

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
	private void openRelocation() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/Relocation.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

		closeWindow();
	}

	@FXML
	private void openDataSupervisor() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/DataSupervisor.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

		closeWindow();
	}

	@FXML
	private void openPalletBack() {
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/PalletBack.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

		closeWindow();

	}

	@FXML
	private void openMovement() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/Movement.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.setOnHiding(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					quitTask();
				}
			});

			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

		closeWindow();
	}

	private void quitTask() {

		if (taskNumber == 0) {
			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE offload_list SET active = 0 WHERE offload_list_task_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, taskNumber);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	public static void setTaskNumber(int num) {

		taskNumber = num;

	}

	@FXML
	private void closeHelp() {

		Stage stage1 = (Stage) btnOK.getScene().getWindow();

		stage1.close();

	}

	@FXML
	private void openHelp() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/Help.fxml"));

			Scene scene = new Scene(root);

			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
