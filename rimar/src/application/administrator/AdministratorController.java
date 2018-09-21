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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalDateTime;
import application.Event;
import application.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

public class AdministratorController {

	@FXML
	private Label lblName;

	@FXML
	private Button btnLogout;

	@FXML
	private Button btnPasswordChange;

	@FXML
	private Button btnAddPallet;

	@FXML
	private Button btnModifyPallet;

	@FXML
	private Button btnCreateOffloadList;

	@FXML
	private Button btnCreateDeliverynote;

	@FXML
	private Button btnStockList;

	@FXML
	private Button btnPositionManager;

	@FXML
	private Button btnAddUser;
	
	@FXML
	private Button btnOK;
	
	@FXML
	private Button btnHelp;

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

	@FXML
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
	private void openAddUser() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/AddUser.fxml"));

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
	private void openPositionManager() {
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/PositionManager.fxml"));

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
	private void openAddPallet() {
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/AddPallet.fxml"));

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
	private void openModifyPallet() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/ModifyPallet.fxml"));

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
	private void openStockLists() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/StockLists.fxml"));

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
	private void openCreateOffloadList() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/CreateOffLoadList.fxml"));

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
	private void openCreateDeliverynote() {

		try {
			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/CreateDeliverynote.fxml"));

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
	private void openHelp() {
		
		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/administrator/Help.fxml"));

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
	private void closeHelp() {
		
		Stage stage1 = (Stage) btnOK.getScene().getWindow();

		stage1.close();
		
	}
}
