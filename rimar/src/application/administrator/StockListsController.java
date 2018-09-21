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

import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.itextpdf.text.DocumentException;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class StockListsController {

	@FXML
	private Button btnBack;

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
	private TableColumn<Pallet, String> arrivalDate;

	@FXML
	private TableColumn<Pallet, Integer> quarantine;

	@FXML
	private ComboBox<String> cbPartners;

	@FXML
	private CheckBox chbQuarantine;

	@FXML
	private Button btnCreateStockList;

	@FXML
	private Button btnCreateBlindList;

	private ObservableList<Pallet> data = FXCollections.observableArrayList();

	private String selectedPartNumber;
	private String selectedPartner;

	@FXML
	private void initialize() {

		tblPallet.setPlaceholder(new Label("Válasszon partnert a paletták megjelenítéséhez!"));

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

	@FXML
	private void fillTable() {

		selectedPartNumber = "";
		chbQuarantine.setDisable(false);
		selectedPartner = cbPartners.getValue();
		tblPallet.getItems().clear();

		MySqlConnection myConnection = new MySqlConnection();

		int partnerId = getPartnerId();

		String sql = "";

		if (!chbQuarantine.isSelected()) {

			sql = "SELECT pallet_id, position_name, part_number, pallet_quantity, arrival_date, quarantine FROM pallet, positions WHERE positions.position_id = pallet.pallet_position AND partner_id = ? AND quarantine = 0 AND offloaded = 0";
		} else {

			sql = "SELECT pallet_id, position_name, part_number, pallet_quantity, arrival_date, quarantine FROM pallet, positions WHERE positions.position_id = pallet_position AND pallet.partner_id = ? AND offloaded = 0";
		}
		try {

			data = FXCollections.observableArrayList();

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ps.setInt(1, partnerId);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				String palletId = rs.getString(1);
				String positionName = rs.getString(2);
				String partNumber = rs.getString(3);
				double quantity = rs.getDouble(4);
				String arrivalDate = rs.getString(5);
				int quarantine = rs.getInt(6);

				Pallet pallet = new Pallet(palletId, partNumber, quantity, positionName, "", "", "", arrivalDate,
						quarantine);

				data.add(pallet);

			}

			palletId.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletId"));
			positionName.setCellValueFactory(new PropertyValueFactory<Pallet, String>("palletPosition"));
			partNo.setCellValueFactory(new PropertyValueFactory<Pallet, String>("partNumber"));
			palletQuantity.setCellValueFactory(new PropertyValueFactory<Pallet, Double>("palletQuantity"));
			arrivalDate.setCellValueFactory(new PropertyValueFactory<Pallet, String>("arrivalDate"));
			quarantine.setCellValueFactory(new PropertyValueFactory<Pallet, Integer>("quarantine"));

			tblPallet.setItems(data);
			tblPallet.setVisible(true);
			colorRow();

			btnCreateBlindList.setDisable(false);

			if (tblPallet.getItems().isEmpty()) {

				btnCreateStockList.setDisable(true);
				btnCreateBlindList.setDisable(true);

			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
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

	@FXML
	private void selectRow() {

		if (tblPallet.getSelectionModel().getSelectedItem() != null) {

			Pallet selectedPallet = tblPallet.getSelectionModel().getSelectedItem();

			selectedPartNumber = selectedPallet.getPartNumber();

			btnCreateStockList.setDisable(false);

		}
	}

	@FXML
	private void createStockList() throws FileNotFoundException, DocumentException {

		CreateStockListPdf.createStockListPdf(selectedPartNumber);

		Alert alert = new Alert(AlertType.INFORMATION,
				"A " + selectedPartNumber + " cikkszámú termék készlet listája elkészült!", ButtonType.OK);
		alert.showAndWait();

	}

	@FXML
	private void createBlindList() throws FileNotFoundException, DocumentException {

		CreateBlindListPdf.createBlindListPdf(selectedPartner);

		Alert alert = new Alert(AlertType.INFORMATION,
				"A " + selectedPartner + " nevű partner leltári vaklistája elkészült", ButtonType.OK);
		alert.showAndWait();

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
