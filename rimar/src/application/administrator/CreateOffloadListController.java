package application.administrator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Calendar;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CreateOffloadListController {

	@FXML
	private ComboBox<String> cbPartners;

	@FXML
	private Button btnAddPallet;

	@FXML
	private Button btnCreateOffloadList;

	@FXML
	private Button btnBack;

	@FXML
	private Button btnRemovePallet;

	@FXML
	private Button btnRefreshStockList;

	@FXML
	private TableView<Pallet> tblStock;

	@FXML
	private TableView<Pallet> tblOffloadList;

	@FXML
	private TableColumn<Pallet, String> stckPalletId;

	@FXML
	private TableColumn<Pallet, String> stckPalletPosition;

	@FXML
	private TableColumn<Pallet, String> stckPartNo;

	@FXML
	private TableColumn<Pallet, Double> stckPalletQuantity;

	@FXML
	private TableColumn<Pallet, String> offldPalletId;

	@FXML
	private TableColumn<Pallet, String> offldPalletPosition;

	@FXML
	private TableColumn<Pallet, String> offldPartNo;

	@FXML
	private TableColumn<Pallet, Double> offldPalletQuantity;

	@FXML
	private Label lblNumOfPallets;

	@FXML
	private TextField txtComments;

	

	private Pallet selectedStockPallet;

	private Pallet selectedOffloadListPallet;

	private int offLoadlistTaskNumber;

	private String partnerName = "";
	
	@FXML
	private void initialize() {

		offLoadlistTaskNumber = 0;

		tblStock.setPlaceholder(new Label("Válasszon partnert a paletták megjelenítéséhez!"));
		tblOffloadList.setPlaceholder(new Label("Adjon palettát a listához!"));
		txtComments.setDisable(true);

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

			cbPartners.setItems(data);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}
	}

	private int getPartnerId() {

		int partnerId = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT partner_id FROM partners WHERE partner_name = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setString(1, cbPartners.getValue());

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
	private void fillStockTable() {

		btnAddPallet.setDisable(true);

		if (partnerName != null && !partnerName.equals(cbPartners.getValue())) {

			tblOffloadList.getItems().clear();
			lblNumOfPallets.setText("");
		}

		partnerName = cbPartners.getValue();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT pallet_id, position_name, part_number, pallet_quantity FROM pallet, positions WHERE pallet.pallet_position = positions.position_id AND pallet.partner_id = ? AND offloaded = 0 AND quarantine = 0 AND archived = 0";
		int partnerId = getPartnerId();

		try {
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, partnerId);

			ResultSet rs = ps.executeQuery();

			ObservableList<Pallet> data = FXCollections.observableArrayList();

			while (rs.next()) {

				String palletId = rs.getString(1);
				String palletPosition = rs.getString(2);
				String partNumber = rs.getString(3);
				double palletQuantity = rs.getDouble(4);

				Pallet pallet = new Pallet(palletId, palletPosition, partNumber, palletQuantity);

				data.add(pallet);

				if (!data.isEmpty()) {
					btnAddPallet.setDisable(false);
				}

			}

			stckPalletId.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletId"));
			stckPalletPosition.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletPosition"));
			stckPartNo.setCellValueFactory(new PropertyValueFactory<Pallet, String>("partNumber"));
			stckPalletQuantity.setCellValueFactory(new PropertyValueFactory<Pallet, Double>("palletQuantity"));

			tblStock.setItems(data);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}
	
	@FXML
	private void selectStockRow() {

		if (tblStock.getSelectionModel().getSelectedItem() != null) {

			selectedStockPallet = tblStock.getSelectionModel().getSelectedItem();

			

		}
	}
	
	@FXML
	private void addPallet() {

		if (selectedStockPallet == null) {
			return;
		}

		offldPalletId.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletId"));
		offldPalletPosition.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletPosition"));
		offldPartNo.setCellValueFactory(new PropertyValueFactory<Pallet, String>("partNumber"));
		offldPalletQuantity.setCellValueFactory(new PropertyValueFactory<Pallet, Double>("palletQuantity"));

		if (!tblOffloadList.getItems().isEmpty()) {

			cbPartners.setDisable(true);
		}

		if (!tblOffloadList.getItems().contains(selectedStockPallet)) {

			tblOffloadList.getItems().add(selectedStockPallet);

		}
		selectedStockPallet = null;
		

		btnRemovePallet.setDisable(false);
		btnCreateOffloadList.setDisable(false);
		txtComments.setDisable(false);

		selectedStockPallet = null;
		

		int size = tblOffloadList.getItems().size();

		lblNumOfPallets.setText(size + " db raklap");

	}
	
	@FXML
	private void selectOffloadRow() {

		if (tblOffloadList.getSelectionModel().getSelectedItem() != null) {

			selectedOffloadListPallet = tblOffloadList.getSelectionModel().getSelectedItem();

			

		}

	}
	
	@FXML
	private void removePallet() {

		if (selectedOffloadListPallet == null) {
			return;
		}
		
		tblOffloadList.getItems().remove(selectedOffloadListPallet);
		

		if (tblOffloadList.getItems().isEmpty()) {

			btnRemovePallet.setDisable(true);
			btnCreateOffloadList.setDisable(true);
			cbPartners.setDisable(false);
			txtComments.setDisable(true);
			lblNumOfPallets.setText("");
		}

		selectedOffloadListPallet = null;
		

		int size = tblOffloadList.getItems().size();

		if (size != 0) {
			lblNumOfPallets.setText(size + " db raklap");

		}

	}
	
	@FXML
	private void createOffloadList() throws Exception {

		ObservableList<Pallet> data = tblOffloadList.getItems();

		if (!checkOffloadList(data)) {

			Alert alert = new Alert(AlertType.ERROR,
					"Hiba történt, a listán szereplő paletták között van(nak) már előzőleg kitárolt rakat(ok), ellenőrizze a tételeket!",
					ButtonType.OK);
			alert.showAndWait();

			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "INSERT INTO offload_list (offload_list_number, offload_list_task_number, pallet_id, user_id, creation_date, active, started, complete, archived, comments) VALUES (?,?,?,?,?,?,?,?,?,?)";

		String offloadListNumber = generateOffloadListNumber();
		int offloadListTaskNumber = generateOffloadListTaskNumber();
		int userId = LoginController.getUserid();
		String dateTime = LocalDateTime.now().toString();
		int active = 0;
		int started = 0;
		int complete = 0;
		int archived = 0;
		String comments = txtComments.getText();

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			for (Pallet p : data) {

				String palletId = p.getPalletId();

				ps.setString(1, offloadListNumber);
				ps.setInt(2, offloadListTaskNumber);
				ps.setString(3, palletId);
				ps.setInt(4, userId);
				ps.setString(5, dateTime);
				ps.setInt(6, active);
				ps.setInt(7, started);
				ps.setInt(8, complete);
				ps.setInt(9, archived);
				ps.setString(10, comments);

				ps.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		try {

			String sql2 = "UPDATE pallet SET offloaded = ? WHERE pallet_id = ?";
			PreparedStatement ps2 = myConnection.connect().prepareStatement(sql2);

			for (Pallet p : data) {

				String palletId = p.getPalletId();

				ps2.setInt(1, 1);
				ps2.setString(2, palletId);

				ps2.executeUpdate();
			}

		} catch (SQLException e) {

			myConnection.disconnect();

		}

		CreateOffLoadListPdf.createPdf(offloadListTaskNumber);
		
		String time = LocalDateTime.now().toString();
		
		Event event = new Event(time, LoginController.getUsername(), "Kitárolási lista", "Új kitárolási listát hozott létre, feladatszám: " + offloadListTaskNumber);
		
		event.createEvent();
		
		Alert alert = new Alert(AlertType.INFORMATION,
				"A kitárolási lista elkészült, feladatszám: " + offloadListTaskNumber, ButtonType.OK);
		alert.showAndWait();

		tblOffloadList.getItems().clear();
		btnRemovePallet.setDisable(true);
		btnCreateOffloadList.setDisable(true);
		cbPartners.setDisable(false);
		txtComments.setText("");
		txtComments.setDisable(true);

		fillStockTable();

	}

	private boolean checkOffloadList(ObservableList<Pallet> input) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT offloaded from pallet WHERE pallet_id = ?";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			for (Pallet p : input) {

				ps.setString(1, p.getPalletId());

				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					int offloaded = rs.getInt(1);

					if (offloaded == 1) {
						return false;
					}
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {

			myConnection.disconnect();
		}

		return true;
	}

	private String generateOffloadListNumber() {

		int year = Calendar.getInstance().get(Calendar.YEAR);

		MySqlConnection myConnection = new MySqlConnection();

		String latestOffloadListNumber = "";
		int num = 0;

		String sql = "SELECT offload_list_number FROM offload_list ORDER BY offload_list_task_number ASC LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				latestOffloadListNumber = rs.getString(1);
			}

			if (!latestOffloadListNumber.isEmpty()) {
				String[] splitArray = latestOffloadListNumber.split("/");

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

	private int generateOffloadListTaskNumber() {

		MySqlConnection myConnection = new MySqlConnection();

		int num = 0;

		String sql = "SELECT offload_list_task_number FROM offload_list ORDER BY offload_list_task_number ASC LIMIT 1";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				num = rs.getInt(1);
			}

			num--;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		offLoadlistTaskNumber = num;

		return num;
	}
	
	@FXML
	private void back() {

		if (!tblOffloadList.getItems().isEmpty()) {

			Alert alert = new Alert(AlertType.CONFIRMATION,
					"A függőben lévő kitárolási lista törlődik ha kilép, folytatja?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait();

			if (alert.getResult() == ButtonType.NO) {
				return;
			}
		}

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

	public int getOffLoadlistTaskNumber() {
		return offLoadlistTaskNumber;
	}

}
