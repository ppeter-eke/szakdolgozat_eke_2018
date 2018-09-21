package application.storekeeper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

public class MovementController {

	@FXML
	private Button btnAskTaskNumber;

	@FXML
	private Button btnAskPalletId;

	@FXML
	private Button btnAskPartNumber;

	@FXML
	private Button btnAskGate;

	@FXML
	private Button btnBack;

	@FXML
	private Button btnQuitTask;

	@FXML
	private TextField txtWorkingField;

	@FXML
	private Label lblStatus;

	@FXML
	private Label lblName;

	@FXML
	private Label lblNumOfPallets;

	@FXML
	private Label lblPallet;

	@FXML
	private Label lblCurrentTaskNumber;

	@FXML
	private Label lblCurrentPalletId;

	@FXML
	private Button btnNext;

	@FXML
	private Button btnPrevious;

	private int remainingPalletNum;

	private int totalPalletNum;

	private int taskNumber;

	private String palletIdInput;

	private Stage stage;

	private ArrayList<String> palletIds;

	private ArrayList<String> positions;

	private int palletIdsIndex = 0;
	private int positionsIndex = 0;

	public void initialize() {

		btnNext.setVisible(false);
		btnPrevious.setVisible(false);
		btnAskPalletId.setVisible(false);
		btnAskPartNumber.setVisible(false);
		btnAskGate.setVisible(false);
		lblName.setText(LoginController.getUsername());
		btnQuitTask.setDisable(true);

		StorekeeperController.setTaskNumber(0);

	}

	@FXML
	private void askTaskNumber() {

		lblStatus.setText("");

		try {
			taskNumber = Integer.parseInt(txtWorkingField.getText());
		} catch (Exception e) {

			lblStatus.setText("Nem megfelelő formátum!");
			lblStatus.setTextFill(Color.web("#ff2d00"));

			return;
		}

		if (isValidOffloadTaskNumber()) {

			int[] offloadInformation = getRemainingAndTotalPalletNumber();
			remainingPalletNum = offloadInformation[0];
			totalPalletNum = offloadInformation[1];

			btnNext.setVisible(true);
			btnPrevious.setVisible(false);

			lblNumOfPallets.setText("Raklapok " + remainingPalletNum + " / " + totalPalletNum);

			btnAskTaskNumber.setVisible(false);
			btnAskPalletId.setVisible(true);
			txtWorkingField.setPromptText("Kérem a raklapazonosítót");
			txtWorkingField.setText("");

			palletIds = getPalletIds(taskNumber);
			positions = getPositions(taskNumber);

			lblPallet.setText(palletIds.get(0) + "\n" + positions.get(0));

			btnNext.setVisible(true);
			btnPrevious.setVisible(true);

			lockTask();

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Belépés feladatba",
					"Kitárolási feladatot elkezdte, feladatszám: " + taskNumber);

			event.createEvent();

			btnQuitTask.setDisable(false);

			lblCurrentTaskNumber.setText("Feladatszám: " + taskNumber);

			StorekeeperController.setTaskNumber(taskNumber);

			return;

		} else {
			lblStatus.setText("Hiba! A feladatszám nem létezik, vagy másik felhasználó már belépett a feladatba!");
			lblStatus.setTextFill(Color.web("#ff2d00"));
			return;
		}

	}

	@FXML
	private void askPalletId() {

		palletIdInput = txtWorkingField.getText().toUpperCase();

		if (isValidPalletId()) {

			btnAskPalletId.setVisible(false);
			lblStatus.setText("");
			btnAskPartNumber.setVisible(true);

			txtWorkingField.setText("");
			txtWorkingField.setPromptText("Kérem a cikkszámot");

			lblCurrentPalletId.setText(palletIdInput);

		} else {

			lblStatus.setText("Hiba! Raklap jó?");
			lblStatus.setTextFill(Color.web("#ff2d00"));
		}

	}

	@FXML
	private void askPartNumber() {

		String partNumberInput = txtWorkingField.getText();

		if (isCorrectPartNumber(palletIdInput, partNumberInput)) {

			btnAskPartNumber.setVisible(false);
			lblStatus.setText("");
			btnAskGate.setVisible(true);
			btnAskGate.setText("Kapu tárhely megadása");

			txtWorkingField.setText("");
			txtWorkingField.setPromptText("Kérem a kapu tárhelyet");

		} else {

			lblStatus.setText("Hiba! Cikkszám jó?");
			lblStatus.setTextFill(Color.web("#ff2d00"));
		}

	}

	@FXML
	private void askGate() {

		String gateInput = txtWorkingField.getText();

		gateInput = gateInput.toUpperCase();

		if (gateInput.equals("KAPU")) {

			lblStatus.setText("");
			btnAskGate.setVisible(false);

			txtWorkingField.setText("");

			performMovement();

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Mozgatás",
					"Kitárolta a palettát, azonosító: " + lblCurrentPalletId.getText());

			event.createEvent();

			int index = palletIds.indexOf(palletIdInput);

			palletIds.remove(index);
			positions.remove(index);

			if (lblPallet.getText().contains(palletIdInput)) {
				try {
					next();
				} catch (Exception e) {

				}
			}

			remainingPalletNum++;

			if (remainingPalletNum == totalPalletNum) {

				lblStatus.setText("Kitárolási feladat elvégezve!");
				lblStatus.setTextFill(Color.web("#1bff00"));
				btnQuitTask.setDisable(true);
				btnAskTaskNumber.setVisible(true);
				btnNext.setVisible(false);
				btnPrevious.setVisible(false);

				String time2 = LocalDateTime.now().toString();

				Event event2 = new Event(time2, LoginController.getUsername(), "Mozgatás",
						"A kitárolási feladatot végrehajtotta, feladatszám: " + taskNumber);

				event2.createEvent();

				lblPallet.setText("");

				txtWorkingField.setPromptText("Kérem a feladatszámot");
				lblNumOfPallets.setText("");
				lblCurrentPalletId.setText("");
				lblCurrentTaskNumber.setText("");
				taskNumber = 0;
				remainingPalletNum = 0;
				totalPalletNum = 0;
				palletIds.clear();
				positions.clear();

			} else {

				lblStatus.setText("Paletta kitárolva");
				lblStatus.setTextFill(Color.web("#1bff00"));
				lblCurrentPalletId.setText("");
				btnAskPalletId.setVisible(true);
				txtWorkingField.setPromptText("Kérem a raklapazonosítót");
				lblNumOfPallets.setText("Raklapok " + remainingPalletNum + " / " + totalPalletNum);
			}

		} else {

			lblStatus.setText("Hiba! Tárhely jó?");
			lblStatus.setTextFill(Color.web("#ff2d00"));
		}
	}

	private boolean isValidOffloadTaskNumber() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT offload_list_task_number FROM offload_list WHERE offload_list_task_number = ? AND active = 0 AND archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, taskNumber);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				return true;

			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return false;
	}

	private int[] getRemainingAndTotalPalletNumber() {

		int[] result = new int[2];

		MySqlConnection myConnection = new MySqlConnection();

		String sql1 = "SELECT COUNT(*) FROM offload_list WHERE offload_list_task_number = ?";
		String sql2 = "SELECT COUNT(*) FROM offload_list WHERE offload_list_task_number = ? AND complete = 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql1);

			ps.setInt(1, taskNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result[1] = rs.getInt(1);
			}

			ps = myConnection.connect().prepareStatement(sql2);

			ps.setInt(1, taskNumber);

			rs = ps.executeQuery();

			while (rs.next()) {

				result[0] = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return result;
	}

	private boolean isValidPalletId() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id FROM offload_list WHERE pallet_id = ? AND offload_list_task_number = ? AND complete = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletIdInput);
			ps.setInt(2, taskNumber);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			myConnection.disconnect();
		} finally {
			myConnection.disconnect();
		}

		return false;
	}

	private boolean isCorrectPartNumber(String palletIdInput, String partNumberInput) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT part_number FROM pallet WHERE pallet_id = ? AND part_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletIdInput);
			ps.setString(2, partNumberInput);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			myConnection.disconnect();
		} finally {
			myConnection.disconnect();
		}

		return false;
	}

	private void performMovement() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE offload_list SET complete = 1, completion_date = ? WHERE pallet_id = ?";

		try {

			String dateTime = LocalDateTime.now().toString();
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, dateTime);
			ps.setString(2, palletIdInput);

			ps.executeUpdate();

		} catch (SQLException e) {
			myConnection.disconnect();
		} finally {
			myConnection.disconnect();
		}

		String sql1 = "UPDATE pallet SET pallet_position = 1 WHERE pallet_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql1);

			ps.setString(1, palletIdInput);

			ps.executeUpdate();

		} catch (SQLException e) {
			myConnection.disconnect();
		} finally {
			myConnection.disconnect();
		}

	}

	private ArrayList<String> getPalletIds(int taskNum) {

		ArrayList<String> result = new ArrayList<>();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id FROM offload_list WHERE offload_list_task_number = ? AND complete = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, taskNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result.add(rs.getString(1));

			}

		} catch (SQLException e) {

		} finally {
			myConnection.disconnect();
		}

		return result;
	}

	private ArrayList<String> getPositions(int taskNum) {

		ArrayList<String> result = new ArrayList<>();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_name FROM positions, offload_list, pallet WHERE offload_list_task_number = ? AND complete = 0 AND offload_list.pallet_id = pallet.pallet_id AND pallet.pallet_position = positions.position_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, taskNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				result.add(rs.getString(1));

			}

		} catch (SQLException e) {

		} finally {
			myConnection.disconnect();
		}

		return result;
	}

	private void lockTask() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE offload_list SET active = 1, started = 1 WHERE offload_list_task_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			ps.setInt(1, taskNumber);

			ps.executeUpdate();

		} catch (SQLException e) {
			myConnection.disconnect();
		} finally {
			myConnection.disconnect();
		}

	}

	@FXML
	public void quitTask() {

		btnAskTaskNumber.setText("Feladatszám megadása");
		txtWorkingField.setPromptText("Kérem a feladatszámot");
		lblStatus.setText("");
		lblNumOfPallets.setText("");
		lblPallet.setText("");
		txtWorkingField.setText("");
		if (palletIds != null && !palletIds.isEmpty()) {

			String time = LocalDateTime.now().toString();

			Event event = new Event(time, LoginController.getUsername(), "Mozgatás",
					"Kilépett a kitárolási feladatból, feladatszám: " + taskNumber);
			event.createEvent();
			palletIds.clear();
			positions.clear();

		}

		btnAskPalletId.setVisible(false);
		btnAskPartNumber.setVisible(false);
		btnAskGate.setVisible(false);

		btnAskTaskNumber.setVisible(true);

		btnNext.setVisible(false);
		btnPrevious.setVisible(false);

		btnQuitTask.setDisable(true);

		lblCurrentTaskNumber.setText("");
		lblCurrentPalletId.setText("");

		quit();

		taskNumber = 0;
		remainingPalletNum = 0;
		totalPalletNum = 0;
		if (palletIds != null) {
			palletIds.clear();
			positions.clear();
		}
	}

	public void quit() {

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

	public int getTaskNumber() {

		return this.taskNumber;
	}

	@FXML
	private void next() {

		try {

			lblPallet.setText(palletIds.get(palletIdsIndex + 1) + "\n" + positions.get(positionsIndex + 1));
			palletIdsIndex++;
			positionsIndex++;

		} catch (IndexOutOfBoundsException e) {

			palletIdsIndex = 0;
			positionsIndex = 0;
			lblPallet.setText(palletIds.get(palletIdsIndex) + "\n" + positions.get(positionsIndex));
			return;
		}

	}

	@FXML
	private void previous() {

		try {

			lblPallet.setText(palletIds.get(palletIdsIndex - 1) + "\n" + positions.get(positionsIndex - 1));
			palletIdsIndex--;
			positionsIndex--;

		} catch (IndexOutOfBoundsException e) {

			palletIdsIndex = palletIds.size() - 1;
			positionsIndex = positions.size() - 1;

			lblPallet.setText(palletIds.get(palletIdsIndex) + "\n" + positions.get(positionsIndex));

		}

	}

	@FXML
	private void back() {

		try {

			quitTask();

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

		stage = (Stage) btnBack.getScene().getWindow();

		stage.close();

	}

}
