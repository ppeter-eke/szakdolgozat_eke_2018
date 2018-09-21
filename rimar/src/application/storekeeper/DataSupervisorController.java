package application.storekeeper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class DataSupervisorController {

	@FXML
	private Button btnTrigger;

	@FXML
	private Button btnBack;

	@FXML
	private TextField txtWorkingField;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblPalletId;

	@FXML
	private Label lblPosition;

	@FXML
	private Label lblPartNo;

	@FXML
	private Label lblQuantity;

	@FXML
	private Label lblArrivalDate;

	@FXML
	private Label lblOffloadStatus;

	@FXML
	private Label lblName;

	@FXML
	private void initialize() {

		lblName.setText(LoginController.getUsername());
	}

	private boolean isValidPallet(String input) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(*) FROM pallet WHERE pallet_id = ? AND archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, txtWorkingField.getText());

			int result = 0;

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result = rs.getInt(1);

			}

			if (result == 1) {
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
	private void dataSupervisor() {

		lblStatus.setText("");
		lblPalletId.setText("");
		lblPosition.setText("");
		lblPartNo.setText("");
		lblQuantity.setText("");
		lblArrivalDate.setText("");
		lblOffloadStatus.setText("");

		if (!isValidPallet(txtWorkingField.getText())) {

			lblStatus.setText("Hiba! Imeretlen raklapazonosító");
			lblStatus.setTextFill(Color.web("#ff2d00"));
			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id, position_name, part_number, pallet_quantity, arrival_date, offloaded, quarantine FROM pallet, positions WHERE pallet_id = ? AND positions.position_id = pallet.pallet_position";

		String palletId = txtWorkingField.getText().toUpperCase();
		int offloadStatus = 0;
		int quarantine = 0;

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				lblPalletId.setText("Azonosító: " + rs.getString(1));
				lblPosition.setText("Pozíció: " + rs.getString(2));
				lblPartNo.setText("Cikkszám: " + rs.getString(3));
				lblQuantity.setText("Mennyiség: " + rs.getString(4));
				lblArrivalDate.setText("Beérkezés dátuma: " + rs.getString(5));
				offloadStatus = rs.getInt(6);
				quarantine = rs.getInt(7);

			}

		} catch (SQLException e) {

		} finally {
			myConnection.disconnect();
		}

		if (offloadStatus != 0) {

			lblOffloadStatus.setText("KITÁROLÁS ALATT");
			lblOffloadStatus.setTextFill(Color.web("#ff2d00"));

		}

		if (quarantine != 0) {

			lblOffloadStatus.setText("ZÁROLT");
			lblOffloadStatus.setTextFill(Color.web("#ff2d00"));

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
