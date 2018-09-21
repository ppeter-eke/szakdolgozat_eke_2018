package application.administrator;

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.itextpdf.text.DocumentException;
import application.Event;
import application.LoginController;
import application.MySqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CreateDeliverynoteController {

	@FXML
	private TableView<OffloadList> tblOffloadList;

	@FXML
	private TableColumn<OffloadList, Integer> colOffloadTaskNumber;

	@FXML
	private TableColumn<OffloadList, Integer> colOffloadNumberOfPallets;

	@FXML
	private TableColumn<OffloadList, String> colOffloadCreationDate;

	@FXML
	private TableColumn<OffloadList, String> colOffloadCompletionDate;

	@FXML
	private TableColumn<OffloadList, String> colOffloadCreator;

	@FXML
	private TableColumn<OffloadList, Integer> colOffloadActive;

	@FXML
	private TableColumn<OffloadList, Integer> colOffloadStarted;

	@FXML
	private TableColumn<OffloadList, Integer> colOffloadComplete;

	@FXML
	private TableView<Pallet> tblContents;

	@FXML
	private TableColumn<Pallet, String> colContentsPosition;

	@FXML
	private TableColumn<Pallet, String> colContentsPartNumber;

	@FXML
	private TableColumn<Pallet, Double> colContentsQuantity;

	@FXML
	private TableColumn<Pallet, String> colContentsPalletId;

	@FXML
	private Button btnBack;

	@FXML
	private Button btnGetOffloadLists;

	@FXML
	private Button btnCreateDeliverynote;

	@FXML
	private Button btnDeleteOffloadList;

	@FXML
	private Label lblPartnerName;

	@FXML
	private Label lblPartnerAddress;

	@FXML
	private Label lblPartnerEmail;

	@FXML
	private TextField txtLicense1;

	@FXML
	private TextField txtLicense2;

	@FXML
	private Label lblLicenseStatus;

	private int selectedOffloadListTaskNumber;

	@FXML
	private void initialize() {

		tblOffloadList.setPlaceholder(
				new Label("Az aktív kitárolási listák megjelenítéséhez kattintson a listák lekérése gombra!"));
		tblContents.setPlaceholder(new Label("Válasszon kitárolási listát!"));
	}

	@FXML
	private void fillOffloadListTable() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT offload_list_task_number, COUNT(pallet_id) AS raklapok_szama, creation_date, completion_date, user_name, active, started, complete FROM offload_list, users WHERE users.user_id = offload_list.user_id AND offload_list.archived = 0 GROUP BY offload_list_task_number";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			ObservableList<OffloadList> data = FXCollections.observableArrayList();
			while (rs.next()) {

				int offloadListNumber = rs.getInt(1);
				int numOfPallets = rs.getInt(2);
				String creationDate = rs.getString(3);
				String completionDate = rs.getString(4);
				String userName = rs.getString(5);
				int active = rs.getInt(6);
				int started = rs.getInt(7);
				int complete = rs.getInt(8);

				OffloadList ol = new OffloadList(offloadListNumber, numOfPallets, creationDate, completionDate,
						userName, active, started, complete);

				String sql2 = "SELECT complete FROM offload_list WHERE offload_list_task_number = ?";

				PreparedStatement ps2 = myConnection.connect().prepareStatement(sql2);

				ps2.setInt(1, offloadListNumber);

				ResultSet rs2 = ps2.executeQuery();

				while (rs2.next()) {

					if (rs2.getInt(1) == 0) {

						ol.setComplete(0);

					}
				}

				data.add(ol);
			}

			colOffloadTaskNumber
					.setCellValueFactory(new PropertyValueFactory<OffloadList, Integer>("offLoadListTaskNumber"));
			colOffloadNumberOfPallets
					.setCellValueFactory(new PropertyValueFactory<OffloadList, Integer>("numberOfPallets"));
			colOffloadCreationDate.setCellValueFactory(new PropertyValueFactory<OffloadList, String>("creationDate"));
			colOffloadCompletionDate
					.setCellValueFactory(new PropertyValueFactory<OffloadList, String>("completionDate"));
			colOffloadCreator.setCellValueFactory(new PropertyValueFactory<OffloadList, String>("userName"));
			colOffloadActive.setCellValueFactory(new PropertyValueFactory<OffloadList, Integer>("active"));
			colOffloadStarted.setCellValueFactory(new PropertyValueFactory<OffloadList, Integer>("started"));
			colOffloadComplete.setCellValueFactory(new PropertyValueFactory<OffloadList, Integer>("complete"));

			tblOffloadList.setItems(data);

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	@FXML
	private void colorRow() {

		tblOffloadList.setRowFactory(row -> new TableRow<OffloadList>() {

			@Override
			public void updateItem(OffloadList item, boolean empty) {

				super.updateItem(item, empty);

				if (item == null || empty) {
					setStyle("");
				} else {
					// Now 'item' has all the info of the Person in this row
					if (item.getComplete() == 1) {
						// We apply now the changes in all the cells of the row
						for (int i = 0; i < getChildren().size(); i++) {

							(getChildren().get(i)).setStyle("-fx-text-fill: green");

						}

					} else if (item.getComplete() == 0 && item.getStarted() == 1) {
						for (int i = 0; i < getChildren().size(); i++) {

							(getChildren().get(i)).setStyle("-fx-text-fill: red");

						}
					} else if (item.getComplete() == 0 && item.getStarted() == 0) {

						for (int i = 0; i < getChildren().size(); i++) {

							(getChildren().get(i)).setStyle("");
						}
					}
				}
			}

		});

	}

	@FXML
	private void selectRow() {

		if (tblOffloadList.getSelectionModel().getSelectedItem() != null) {

			btnDeleteOffloadList.setDisable(true);
			btnCreateDeliverynote.setDisable(true);

			OffloadList ol = tblOffloadList.getSelectionModel().getSelectedItem();
			selectedOffloadListTaskNumber = ol.getOffLoadListTaskNumber();
			int taskNumber = ol.getOffLoadListTaskNumber();

			ArrayList<Pallet> palletIds = getPalletIds(taskNumber);

			fillContents(palletIds);

			if (ol.getActive() == 0 && ol.getStarted() == 0) {
				btnDeleteOffloadList.setDisable(false);

			}
			if (ol.getComplete() == 1) {
				btnDeleteOffloadList.setDisable(true);
				btnCreateDeliverynote.setDisable(false);
				txtLicense1.setDisable(false);
				txtLicense2.setDisable(false);

			}
		}

	}

	private ArrayList<Pallet> getPalletIds(int taskNumber) {

		ArrayList<Pallet> palletIdList = new ArrayList<>();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id from offload_list WHERE offload_list_task_number = ? AND archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, taskNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				Pallet p = new Pallet(rs.getString(1));

				palletIdList.add(p);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return palletIdList;
	}

	private void fillContents(ArrayList<Pallet> palletIds) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_name, part_number, pallet_quantity FROM pallet, positions WHERE pallet_id = ? AND positions.position_id = pallet.pallet_position";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			ObservableList<Pallet> data = FXCollections.observableArrayList();

			for (Pallet p : palletIds) {

				ps.setString(1, p.getPalletId());

				ResultSet rs = ps.executeQuery();
				while (rs.next()) {

					p.setPalletPosition(rs.getString(1));
					p.setPartNumber(rs.getString(2));
					p.setPalletQuantity(rs.getDouble(3));

					data.add(p);
				}
			}

			colContentsPosition.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletPosition"));
			colContentsPartNumber.setCellValueFactory(new PropertyValueFactory<Pallet, String>("partNumber"));
			colContentsQuantity.setCellValueFactory(new PropertyValueFactory<Pallet, Double>("palletQuantity"));
			colContentsPalletId.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletId"));

			tblContents.setItems(data);
			tblContents.setVisible(true);

			setPartnerInfo();

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	@FXML
	private void deleteOffloadList() {

		Alert alert = new Alert(AlertType.CONFIRMATION, "Biztosan törli?", ButtonType.YES, ButtonType.NO);
		alert.showAndWait();

		if (alert.getResult() == ButtonType.NO) {
			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE offload_list SET archived = 1 WHERE offload_list_task_number = ?";
		String sql1 = "UPDATE pallet SET offloaded = 0 WHERE pallet_id = ?";

		ObservableList<Pallet> data = tblContents.getItems();
		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, selectedOffloadListTaskNumber);

			ps.executeUpdate();

			PreparedStatement ps2 = myConnection.connect().prepareStatement(sql1);

			for (Pallet p : data) {

				ps2.setString(1, p.getPalletId());

				ps2.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		String time = LocalDateTime.now().toString();

		Event event = new Event(time, LoginController.getUsername(), "Kitárolási lista",
				"Kitárolási listát törölt, feladatszám: " + selectedOffloadListTaskNumber);
		
		event.createEvent();

		fillOffloadListTable();
		tblContents.getItems().clear();
		btnDeleteOffloadList.setDisable(true);
		lblPartnerName.setText("");
		lblPartnerAddress.setText("");
		lblPartnerEmail.setText("");
	}

	private void setPartnerInfo() {

		String palletId = tblContents.getItems().get(0).getPalletId();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_name, partner_address, partner_email FROM partners, pallet WHERE pallet_id = ? AND pallet.partner_id = partners.partner_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, palletId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				lblPartnerName.setText("Partner: " + rs.getString(1));
				lblPartnerAddress.setText("Rendeltetési hely: " + rs.getString(2));
				lblPartnerEmail.setText("E-mail: " + rs.getString(3));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}
	}

	@FXML
	private void finalizeDelivery() throws FileNotFoundException, DocumentException {

		lblLicenseStatus.setText("");
		String license1 = txtLicense1.getText();
		String license2 = txtLicense2.getText();

		if (license1.length() > 20 || license2.length() > 20 || license1.isEmpty()) {

			lblLicenseStatus.setText("Hibás adatmegadás!");
			lblLicenseStatus.setTextFill(Color.web("#ff2d00"));
			return;
		}

		int partnerId = getPartnerId();
		int userId = getUserId();
		String deliveryNoteNumber = generateDeliveryNoteNumber();
		String dateTime = LocalDateTime.now().toString();

		ObservableList<Pallet> data = tblContents.getItems();

		HashMap<String, Double> partNoAndQty = new HashMap<>();

		for (Pallet p : data) {

			double qty = p.getPalletQuantity();
			String partNo = p.getPartNumber();

			if (!partNoAndQty.containsKey(partNo)) {

				partNoAndQty.put(partNo, qty);
			} else {

				double qtyTemp = partNoAndQty.get(partNo);
				qty = qty + qtyTemp;

				partNoAndQty.put(partNo, qty);
			}

		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "INSERT INTO deliverynote (deliverynote_number, part_number, pallet_quantity, user_id, creation_date, partner_id, truck_licenseplate1, truck_licenseplate2) VALUES (?,?,?,?,?,?,?,?)";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			for (String s : partNoAndQty.keySet()) {

				ps.setString(1, deliveryNoteNumber);
				ps.setString(2, s);

				double quantity = partNoAndQty.get(s);

				ps.setDouble(3, quantity);
				ps.setInt(4, userId);
				ps.setString(5, dateTime);
				ps.setInt(6, partnerId);
				ps.setString(7, license1);
				ps.setString(8, license2);

				ps.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			myConnection.disconnect();
		}

		CreateDeliverynotePdf.createDeliverynotePdf(deliveryNoteNumber, license1, license2);

		String time = LocalDateTime.now().toString();

		Event event = new Event(time, LoginController.getUsername(), "Szállítólevél",
				"Új szállítólevelet hozott létre, szállítólevélszám: " + deliveryNoteNumber);

		event.createEvent();

		Alert alert = new Alert(AlertType.INFORMATION, "A " + deliveryNoteNumber + " számú szállítólevél elkészült!",
				ButtonType.OK);
		alert.showAndWait();

		archivePallets();
		archiveOffloadList();

		txtLicense1.setDisable(true);
		txtLicense2.setDisable(true);

		txtLicense1.setText("");
		txtLicense2.setText("");

		btnCreateDeliverynote.setDisable(true);
		btnDeleteOffloadList.setDisable(true);

		fillOffloadListTable();
		tblContents.getItems().clear();
	}

	private void archiveOffloadList() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "UPDATE offload_list SET archived = 1 WHERE offload_list_task_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, selectedOffloadListTaskNumber);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	private void archivePallets() {
		ObservableList<Pallet> data = tblContents.getItems();

		MySqlConnection myConnection = new MySqlConnection();

		String dateTime = LocalDateTime.now().toString();

		String sql = "UPDATE pallet SET archived = 1, delivery_date = ? WHERE pallet_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			for (Pallet p : data) {

				String palletId = p.getPalletId();

				ps.setString(1, dateTime);
				ps.setString(2, palletId);

				ps.executeUpdate();

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	private String generateDeliveryNoteNumber() {

		int year = Calendar.getInstance().get(Calendar.YEAR);

		MySqlConnection myConnection = new MySqlConnection();

		String latestDeliverynoteNumber = "";
		int num = 0;

		String sql = "SELECT deliverynote_number FROM deliverynote ORDER BY deliverynote_number DESC LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				latestDeliverynoteNumber = rs.getString(1);
			}

			if (!latestDeliverynoteNumber.isEmpty()) {
				String[] splitArray = latestDeliverynoteNumber.split("/");

				num = Integer.parseInt(splitArray[1]);

			}
			num++;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		String result = year + "/" + num;

		return result;

	}

	private int getUserId() {

		int userid = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT user_id from offload_list WHERE offload_list_task_number = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, selectedOffloadListTaskNumber);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				userid = rs.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return userid;

	}

	private int getPartnerId() {

		int partnerid = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_id from partners WHERE partner_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, lblPartnerName.getText().substring(9));

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				partnerid = rs.getInt(1);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return partnerid;
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
