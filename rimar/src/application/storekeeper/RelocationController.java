package application.storekeeper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import application.Event;
import application.LoginController;
import application.MySqlConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RelocationController {

	@FXML
	private Label lblName;

	@FXML
	private Button btnBack;

	@FXML
	private TextField txtWorkingField;

	@FXML
	private Button btnTrigger;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblCurrentPosition;

	@FXML
	private Label lblPalletId;
	
	@FXML
	private void initialize() {

		lblName.setText(LoginController.getUsername());
		btnTrigger.setText("Palettakód leolvasása");
		Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), evt -> lblPalletId.setVisible(false)),
				new KeyFrame(Duration.seconds(0.8), evt -> lblPalletId.setVisible(true)));
		timeline.setCycleCount(Animation.INDEFINITE);
		timeline.play();

	}
	
	@FXML
	private void relocationLogic() {

		lblStatus.setText("");

		String pallet = "";

		while (lblPalletId.getText().isEmpty()) {
			pallet = txtWorkingField.getText().toUpperCase();

			if (!isValidPallet(pallet)) {
				lblStatus.setText("Hiba! Ismeretlen raklapazonosító!");
				lblStatus.setTextFill(Color.web("#ff2d00"));
				pallet = "";
				return;
			}
			if (!isRelocatable(pallet)) {
				lblStatus.setText("Hiba! A raklap kitárolás alatt van!");
				lblStatus.setTextFill(Color.web("#ff2d00"));
				pallet = "";
				return;
			}

			if (!pallet.isEmpty()) {
				lblPalletId.setText(pallet);
				txtWorkingField.setText("");
				txtWorkingField.setPromptText("Kérem a tárhelyet");
				lblCurrentPosition.setText(getCurrentPosition(pallet));
				btnTrigger.setText("Tárhely leolvasása");
				return;
			}
		}

		String position = "";

		while (position.isEmpty()) {

			position = txtWorkingField.getText();

			if (!isValidPosition(position)) {

				lblStatus.setText("Hiba! A tárhely nem létezik!");
				lblStatus.setTextFill(Color.web("#ff2d00"));

				return;
			}

			int positionId = getPositionId(position);

			performRelocation(lblPalletId.getText(), positionId);

			lblPalletId.setText("");
			txtWorkingField.setText("");
			txtWorkingField.setPromptText("Kérem a palettaazonosítót");
			lblStatus.setText("Paletta áthelyezve");
			lblStatus.setTextFill(Color.web("#1bff00"));
			lblCurrentPosition.setText("");
			btnTrigger.setText("Palettakód leolvasása");

		}
	}

	private void performRelocation(String palletId, int positionId) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE pallet SET pallet_position = ? WHERE pallet_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, positionId);
			ps.setString(2, palletId);

			ps.executeUpdate();

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Áthelyezés",
					"Áthelyezte a " + palletId + " azonosítójú palettát, új tárhely: " + txtWorkingField.getText());

			event.createEvent();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	private String getCurrentPosition(String palletId) {

		String position = "Jelenlegi pozíció: ";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_name FROM positions, pallet WHERE pallet.pallet_position = positions.position_id AND pallet_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				position += rs.getString(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return position;

	}

	private int getPositionId(String position) {

		int positionId = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_id FROM positions WHERE position_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, position);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				positionId = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return positionId;
	}

	private boolean isValidPallet(String input) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id FROM pallet WHERE pallet_id = ? AND archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, input);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		return false;
	}

	private boolean isRelocatable(String input) {
		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id FROM pallet WHERE pallet_id = ? AND offloaded = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, input);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		return false;
	}

	private boolean isValidPosition(String input) {

		input = input.toUpperCase();

		if (input.equals("KAPU") || input.equals("KAPU BEVET") || input.equals("KAPUN MARADT ARU")) {
			return false;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_name FROM positions WHERE position_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, input);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {

			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		return false;

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
