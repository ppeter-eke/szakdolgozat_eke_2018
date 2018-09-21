package application.warehousemanager;

import java.time.LocalDateTime;

import application.Event;
import application.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class WarehouseManagerController {

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnPasswordChange;

	@FXML
	private Button btnPerformance;

	@FXML
	private Button btnEventViewer;

	@FXML
	private Label lblName;

	@FXML
	private void initialize() {

		lblName.setText(LoginController.getUsername());
	}

	@FXML
	private void openPerformance() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/warehousemanager/Performance.fxml"));

			Scene scene = new Scene(root);
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

			closeWindow();

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	@FXML
	private void openEventViewer() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/warehousemanager/EventViewer.fxml"));

			Scene scene = new Scene(root);

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

			closeWindow();

		} catch (Exception e) {
			e.printStackTrace();

		}
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
			
			Event event = new Event(time, LoginController.getUsername(), "Kijelentkezés", "Kijelentkezett a rendszerből");
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

			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);

		} catch (Exception e) {
			e.printStackTrace();

		}

	}

}
