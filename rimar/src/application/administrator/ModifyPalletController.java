package application.administrator;


import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import javafx.scene.control.Alert;

import javafx.scene.control.Alert.AlertType;
import application.Event;
import application.LoginController;
import application.MySqlConnection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ModifyPalletController {

	@FXML
	private Button btnBack;

	@FXML
	private ComboBox<String> cbPartner;

	@FXML
	private TableView<Pallet> tblPallet;

	@FXML
	private TableColumn<Pallet, String> palletId;

	@FXML
	private TableColumn<Pallet, String> partNo;

	@FXML
	private TableColumn<Pallet, Double> palletQuantity;

	@FXML
	private TableColumn<Pallet, String> positionName;

	@FXML
	private TableColumn<Pallet, String> license1;

	@FXML
	private TableColumn<Pallet, String> license2;

	@FXML
	private TableColumn<Pallet, String> deliverynoteNumber;

	@FXML
	private TableColumn<Pallet, String> arrivalDate;

	@FXML
	private TableColumn<Pallet, Integer> quarantine;

	@FXML
	private Label lblPalletId;

	@FXML
	private TextField txtPartNumber;

	@FXML
	private TextField txtQuantity;

	@FXML
	private TextField txtLicense1;

	@FXML
	private TextField txtLicense2;

	@FXML
	private TextField txtDeliverynoteNumber;

	@FXML
	private ComboBox<String> cbQuarantine = new ComboBox<>();

	@FXML
	private Label lblPartNumberStatus;

	@FXML
	private Label lblQuantityStatus;

	@FXML
	private Label lblLicense1Status;

	@FXML
	private Label lblLicense2Status;

	@FXML
	private Label lblDeliverynoteStatus;

	@FXML
	private Button btnModifyPallet;

	@FXML
	private Label lblSuccess;

	@FXML
	private Button btnMakeBarcode;

	@FXML
	private ImageView imgBarcode;

	@FXML
	private Button btnDeletePallet;

	@FXML
	private void initialize() {

		tblPallet.setPlaceholder(new Label("Válasszon partnert a paletták megjelenítéséhez!"));

		ObservableList<String> quarantineValues = FXCollections.observableArrayList();

		quarantineValues.add("Igen");
		quarantineValues.add("Nem");

		cbQuarantine.setItems(quarantineValues);

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

		cbPartner.getSelectionModel().selectFirst();

	}
	
	@FXML
	private void fillTable() {

		MySqlConnection myConnection = new MySqlConnection();

		int partnerId = getPartnerId();

		String sql = "SELECT pallet_id, part_number, pallet_quantity, position_name, truck_licenseplate1, truck_licenseplate2, deliverynote_number, arrival_date, quarantine FROM pallet, positions WHERE partner_id = ? AND pallet.pallet_position = positions.position_id AND offloaded = 0 AND archived = 0";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setInt(1, partnerId);

			ResultSet rs = st.executeQuery();

			ObservableList<Pallet> data = FXCollections.observableArrayList();

			while (rs.next()) {

				String palletId = rs.getString(1);
				String partNumber = rs.getString(2);
				double quantity = rs.getDouble(3);
				String positionName = rs.getString(4);
				String license1 = rs.getString(5);
				String license2 = rs.getString(6);
				String deliverynoteNumber = rs.getString(7);
				String arrivalDate = rs.getString(8);
				int quarantine = rs.getInt(9);

				Pallet pallet = new Pallet(palletId, partNumber, quantity, positionName, license1, license2,
						deliverynoteNumber, arrivalDate, quarantine);

				data.add(pallet);
			}

			palletId.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletId"));
			partNo.setCellValueFactory(new PropertyValueFactory<Pallet, String>("partNumber"));
			palletQuantity.setCellValueFactory(new PropertyValueFactory<Pallet, Double>("palletQuantity"));
			positionName.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletPosition"));
			license1.setCellValueFactory(new PropertyValueFactory<Pallet, String>("truckLicense1"));
			license2.setCellValueFactory(new PropertyValueFactory<Pallet, String>("truckLicense2"));
			deliverynoteNumber.setCellValueFactory(new PropertyValueFactory<Pallet, String>("deliverynoteNumber"));
			arrivalDate.setCellValueFactory(new PropertyValueFactory<Pallet, String>("arrivalDate"));
			quarantine.setCellValueFactory(new PropertyValueFactory<Pallet, Integer>("quarantine"));

			tblPallet.setItems(data);
			tblPallet.setVisible(true);

			colorRow();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}
	@FXML
	private void selectRow() {

		if (tblPallet.getSelectionModel().getSelectedItem() != null) {

			Pallet selectedPallet = tblPallet.getSelectionModel().getSelectedItem();

			lblPalletId.setText(selectedPallet.getPalletId());
			txtPartNumber.setText(selectedPallet.getPartNumber());
			txtQuantity.setText(selectedPallet.getPalletQuantity() + "");
			txtLicense1.setText(selectedPallet.getTruckLicense1());
			txtLicense2.setText(selectedPallet.getTruckLicense2());
			txtDeliverynoteNumber.setText(selectedPallet.getDeliverynoteNumber());
			int quarantine = selectedPallet.getQuarantine();

			if (quarantine == 1) {

				cbQuarantine.setValue("Igen");
			} else {
				cbQuarantine.setValue("Nem");
			}

			btnModifyPallet.setDisable(false);
			btnMakeBarcode.setDisable(false);
			btnDeletePallet.setDisable(false);
		}

	}
	
	
	private void colorRow() {

		tblPallet.setRowFactory(row -> new TableRow<Pallet>() {

			@Override
			public void updateItem(Pallet item, boolean empty) {

				super.updateItem(item, empty);

				if (item == null || empty) {
					setStyle("");
				} else {
					// Now 'item' has all the info of the Person in this row
					if (item.getQuarantine() == 1) {
						// We apply now the changes in all the cells of the row
						for (int i = 0; i < getChildren().size(); i++) {

							(getChildren().get(i)).setStyle("-fx-text-fill: red");

						}

					} else {
						for (int i = 0; i < getChildren().size(); i++) {

							(getChildren().get(i)).setStyle("");
						}
					}
				}
			}

		});

	}

	private boolean checkFields() {

		if (txtPartNumber.getText().isEmpty() || txtPartNumber.getText().length() > 30) {

			lblPartNumberStatus.setText("Üres vagy túl hosszú!");
			lblPartNumberStatus.setTextFill(Color.web("#ff2d00"));
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

		if (txtDeliverynoteNumber.getText().isEmpty() || txtDeliverynoteNumber.getText().length() > 30) {

			lblDeliverynoteStatus.setText("Üres vagy túl hosszú!");
			lblDeliverynoteStatus.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		if (txtLicense1.getText().isEmpty() || txtLicense1.getText().length() > 20) {

			lblLicense1Status.setText("Üres vagy túl hosszú!");
			lblLicense1Status.setTextFill(Color.web("#ff2d00"));
			return false;

		}

		if (txtLicense2.getText().length() > 20) {

			lblLicense2Status.setText("Túl hosszú!");
			lblLicense2Status.setTextFill(Color.web("#ff2d00"));
			return false;
		}

		return true;
	}
	
	@FXML
	private void modifyPallet() {

		lblPartNumberStatus.setText("");
		lblQuantityStatus.setText("");
		lblLicense1Status.setText("");
		lblLicense2Status.setText("");
		lblDeliverynoteStatus.setText("");
		lblSuccess.setText("");

		if (!checkFields()) {

			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE pallet SET part_number = ?, pallet_quantity = ?, truck_licenseplate1 = ?, truck_licenseplate2 = ?, deliverynote_number = ?, quarantine = ? WHERE pallet_id = ?";

		String partNumber = txtPartNumber.getText();
		double quantity = Double.parseDouble(txtQuantity.getText());
		String license1 = txtLicense1.getText();
		String license2 = txtLicense2.getText();
		String deliveryNote = txtDeliverynoteNumber.getText();
		int quarantine = 0;

		String quarantineValue = cbQuarantine.getValue();

		if (quarantineValue.equals("Igen")) {
			quarantine = 1;
		}

		String palletId = lblPalletId.getText();

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setString(1, partNumber);
			st.setDouble(2, quantity);
			st.setString(3, license1);
			st.setString(4, license2);
			st.setString(5, deliveryNote);
			st.setInt(6, quarantine);
			st.setString(7, palletId);

			st.executeUpdate();

			lblSuccess.setText("Sikeres módosítás!");
			lblSuccess.setTextFill(Color.web("#1bff00"));
			
			String time = LocalDateTime.now().toString();
			
			Event event = new Event(time, LoginController.getUsername(), "Módosítás", "Paletta adatait módosította, azonosító: " + palletId);
			
			event.createEvent();
			
			fillTable();

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}
	
	@FXML
	private void deletePallet() {

		Alert alert = new Alert(AlertType.CONFIRMATION, "Biztosan törli a palettát?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.YES) {

			MySqlConnection myConnection = new MySqlConnection();

			String sql = "UPDATE pallet SET archived = 1 WHERE pallet_id = ?";

			String palletId = lblPalletId.getText();

			try {
				PreparedStatement ps = myConnection.connect().prepareStatement(sql);

				ps.setString(1, palletId);

				ps.executeUpdate();

				Alert alert2 = new Alert(AlertType.INFORMATION, "Paletta sikeresen törölve!");
				alert2.showAndWait();

				fillTable();

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				myConnection.disconnect();
			}

		}

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
	
	
	private int getPartnerId() {

		int partnerId = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_id FROM partners WHERE partner_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, cbPartner.getValue());

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
		
	@FXML
	private void generateBarcode() throws Exception {

		String palletId = lblPalletId.getText();
		String partNo = txtPartNumber.getText();
		double quantity = Double.parseDouble(txtQuantity.getText());
		
		Barcode barcode = new Barcode();

		barcode.palletCode(palletId, partNo, quantity);

		File file = new File("barcode_" + palletId + "_" + partNo + "_" + quantity + "_.png");

		Image image = new Image(file.toURI().toString());
		
		
		imgBarcode.setImage(image);

		
		
	}

	private void closeWindow() {

		Stage stage1 = (Stage) btnBack.getScene().getWindow();

		stage1.close();

	}

}
