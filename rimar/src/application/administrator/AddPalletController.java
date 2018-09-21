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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import application.Event;
import application.LoginController;
import application.MySqlConnection;


public class AddPalletController {

	@FXML
	private ComboBox<String> cbPartner;

	@FXML
	private Button btnBack;

	@FXML
	private Button btnAdd;

	@FXML
	private TextField txtPartNo;

	@FXML
	private TextField txtQuantity;

	@FXML
	private TextField txtDeliverynote;

	@FXML
	private TextField txtLicense1;

	@FXML
	private TextField txtLicense2;

	@FXML
	private Label lblPartner;

	@FXML
	private Label lblPartnerStatus;

	@FXML
	private Label lblPartNoStatus;

	@FXML
	private Label lblQuantityStatus;

	@FXML
	private Label lblDeliverynoteStatus;

	@FXML
	private Label lblLicensePlate1Status;

	@FXML
	private Label lblLicensePlate2Status;

	@FXML
	private Label lblSuccess;
	
	@FXML
	private void initialize() {

		fillComboBox();

	}
	
	
	private void fillComboBox() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_name FROM partners";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			ObservableList<String> data = FXCollections.observableArrayList();

			while (rs.next()) {

				data.add(rs.getString("partner_name"));

			}

			cbPartner.setItems(data);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}
	
	@FXML
	private void selectPartner() {

		lblPartner.setText(cbPartner.getValue());
	}

	private boolean checkFields() {

		if (lblPartner.getText().isEmpty()) {

			lblPartnerStatus.setText("Partner választása kötelező!");
			lblPartnerStatus.setTextFill(Color.web("#ff2d00"));
			return false;

		}

		if (txtPartNo.getText().isEmpty() || txtPartNo.getText().length() > 30) {

			lblPartNoStatus.setText("Üres vagy túl hosszú!");
			lblPartNoStatus.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		if (txtQuantity.getText().isEmpty()) {

			lblQuantityStatus.setText("Kötelező Mező!");
			lblQuantityStatus.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		try {

			@SuppressWarnings("unused")
			Double q = Double.parseDouble(txtQuantity.getText());

		} catch (Exception e) {
			lblQuantityStatus.setText("Hibás formátum!");
			lblQuantityStatus.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		if (txtDeliverynote.getText().isEmpty() || txtDeliverynote.getText().length() > 30) {

			lblDeliverynoteStatus.setText("Üres vagy túl hosszú!");
			lblDeliverynoteStatus.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		if (txtLicense1.getText().isEmpty() || txtLicense1.getText().length() > 20) {

			lblLicensePlate1Status.setText("Üres vagy túl hosszú!");
			lblLicensePlate1Status.setTextFill(Color.web("#ff2d00"));
			return false;

		}

		if (txtLicense2.getText().length() > 20) {

			lblLicensePlate2Status.setText("Túl hosszú!");
			lblLicensePlate2Status.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		return true;
	}
	
	@FXML
	private void addPallet() {

		lblPartNoStatus.setText("");
		lblQuantityStatus.setText("");
		lblDeliverynoteStatus.setText("");
		lblLicensePlate1Status.setText("");
		lblLicensePlate2Status.setText("");
		lblSuccess.setText("");

		if (!checkFields()) {
			return;
		}

		String palletId = generatePalletId();
		int partnerId = getPartnerId();
		String partNumber = txtPartNo.getText();
		double quantity = Double.parseDouble(txtQuantity.getText());
		int position = 2; // KAPU BEVÉT
		String license1 = txtLicense1.getText();
		String license2 = txtLicense2.getText();
		String deliverynote = txtDeliverynote.getText();
		String today = LocalDate.now().toString();
		int userId = getUserId();
		int offloaded = 0;
		int quarantine = 0;
		int archived = 0;

		String sql = "INSERT INTO pallet (pallet_id, partner_id, part_number, pallet_quantity, pallet_position, truck_licenseplate1, truck_licenseplate2, deliverynote_number, arrival_date, user_id, offloaded, quarantine, archived) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

		MySqlConnection myConnection = new MySqlConnection();

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletId);
			ps.setInt(2, partnerId);
			ps.setString(3, partNumber);
			ps.setDouble(4, quantity);
			ps.setInt(5, position);
			ps.setString(6, license1);
			ps.setString(7, license2);
			ps.setString(8, deliverynote);
			ps.setString(9, today);
			ps.setInt(10, userId);
			ps.setInt(11, offloaded);
			ps.setInt(12, quarantine);
			ps.setInt(13, archived);

			ps.executeUpdate();
			
			lblSuccess.setText("Termék sikeresen bevételezve!");
			lblSuccess.setTextFill(Color.web("#1bff00"));
				
			String time = LocalDateTime.now().toString();
			
			Event event = new Event(time, LoginController.getUsername(), "Bevételezés", "Új palettát vételezett be, azonosító: " + palletId);
			
			event.createEvent();
			
			Alert alert = new Alert(AlertType.INFORMATION, "Paletta sikeresen bevételezve!");
			alert.showAndWait();
			
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
			
			
		}

	}

	public String generatePalletId() {

		String id = "PN";

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(*) FROM pallet";

		int count = 0;

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		count++;

		String s = count + "";
		int length = s.length();

		int numOfZeroes = 7 - length;

		for (int i = 0; i < numOfZeroes; i++) {

			id += "0";

		}

		id += s;

		return id;
	}

	private int getPartnerId() {

		int partnerId = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_id FROM partners WHERE partner_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, lblPartner.getText());

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				partnerId = rs.getInt("partner_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return partnerId;
	}

	private int getUserId() {

		int userId = 0;

		String userName = LoginController.getUsername();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT user_id FROM users WHERE user_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, userName);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				userId = rs.getInt("user_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return userId;
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
