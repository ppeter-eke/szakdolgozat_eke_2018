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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import application.Event;
import application.LoginController;
import application.MySqlConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PalletBackController {

	@FXML
	private Button btnTrigger;

	@FXML
	private Button btnBack;

	@FXML
	private Label lblStatus;

	@FXML
	private TextField txtWorkingField;

	@FXML
	private Label lblName;

	private String palletId = "";
	
	@FXML
	private Label lblPalletId;

	@FXML
	private void initialize() {
		
		lblName.setText(LoginController.getUsername());
		btnTrigger.setText("Palettakód leolvasása");

		

	}

	@FXML
	private void palletBackLogic() {
		
		lblStatus.setText("");
	
		
		while (palletId.isEmpty()) {

			String input = txtWorkingField.getText().toUpperCase();

			if (isValidPallet(input)) {

				palletId = input;
				btnTrigger.setText("Kapu tárhely megadása");
				txtWorkingField.setPromptText("Kérem a kapu tárhelyet");
				txtWorkingField.setText("");
				lblPalletId.setText(palletId);
				return;

			}
			else {
				
				lblStatus.setText("Hiba! Raklap jó?");
				lblStatus.setTextFill(Color.web("#ff2d00"));
				return;
			}

		}
		
		
		
		String gate = txtWorkingField.getText().toUpperCase();
		
		if(gate.equals("KAPU")) {
			
			performPalletBack();
			lblStatus.setText("Paletta sikeresen eltávolítva a kitárolási listából");
			lblStatus.setTextFill(Color.web("#1bff00"));
			
			String time = LocalDateTime.now().toString();
			
			Event event = new Event(time, LoginController.getUsername(), "Paletta vissza", "Paletta visszakerült, új tárhely: KAPUN MARADT ARU, azonosító: " + lblPalletId.getText());
			
			event.createEvent();
			lblPalletId.setText("");
			txtWorkingField.setPromptText("Kérem a palettaazonosítót");
			btnTrigger.setText("Palettakód leolvasása");
			txtWorkingField.setText("");
			palletId = "";
			
		}
		else {
			
			
			lblStatus.setText("Hiba! Tárhely jó?");
			lblStatus.setTextFill(Color.web("#ff2d00"));
		}
		
	}

	private boolean isValidPallet(String palletid) {

		

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(*) FROM pallet WHERE pallet_id = ? AND offloaded = 1 AND archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletid);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				
				int result = rs.getInt(1);
				
				if (result == 1) {
					return true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return false;
	}
	
	private void performPalletBack() {
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "UPDATE pallet SET offloaded = 0, pallet_position = 3 WHERE pallet_id = ?";
		String sql1 = "UPDATE offload_list SET archived = 1, complete = 0, started = 0, active = 0 WHERE pallet_id = ?";
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			PreparedStatement ps1 = myConnection.connect().prepareStatement(sql1);
			
			
			ps.setString(1, palletId);
			ps1.setString(1, palletId);
			
			ps.executeUpdate();
			ps1.executeUpdate();
		}catch(SQLException e) {
			
			e.printStackTrace();
		}finally {
			myConnection.disconnect();
		}
		
		
	}
	
	
	@FXML
	private void back() {

		try {

			Stage primaryStage = new Stage();

			Parent root = FXMLLoader.load(getClass().getResource("/application/storekeeper/StorekeeperMain.fxml"));

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
