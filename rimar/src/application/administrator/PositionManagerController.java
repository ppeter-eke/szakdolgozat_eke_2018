package application.administrator;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.SQLException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import application.MySqlConnection;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class PositionManagerController {

	@FXML
	private Button btnCreateBarcode;

	@FXML
	private Button btnBack;

	@FXML
	private Label lblStatus;

	@FXML
	private Button btnAdd;

	@FXML
	private TextField txtPosition;

	@FXML
	private ImageView imgView;

	@FXML
	private TableView<Position> tblPositions;

	@FXML
	private TableColumn<Position, Integer> positionId;
	@FXML
	private TableColumn<Position, String> positionName;
	
	@FXML
	private void initialize() {

		fillTable();
		//generatePositions();
	}

	private void fillTable() {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT * FROM positions";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			ObservableList<Position> data = FXCollections.observableArrayList();

			while (rs.next()) {

				int id = rs.getInt("position_id");
				String name = rs.getString("position_name");

				Position pos = new Position(id, name);

				data.add(pos);
			}

			positionId.setCellValueFactory(new PropertyValueFactory<Position, Integer>("positionId"));
			positionName.setCellValueFactory(new PropertyValueFactory<Position, String>("positionName"));
			tblPositions.setItems(data);
			tblPositions.setVisible(true);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

	}

	@SuppressWarnings("unchecked")
	@FXML
	private void selectCell() {

		TablePosition<Position, String> pos = tblPositions.getSelectionModel().getSelectedCells().get(0);
		int row = pos.getRow();

		Position position = tblPositions.getItems().get(row);

		TableColumn<Position, String> col = pos.getTableColumn();

		String data = "";
		try {
			data = (String) col.getCellObservableValue(position).getValue();
		} catch (Exception e) {

		}

		txtPosition.setText(data);

	}
	
	@FXML
	private void addPosition() {

		lblStatus.setText("");

		String input = txtPosition.getText();
		input = input.toUpperCase();

		if (input.isEmpty()) {
			lblStatus.setTextFill(Color.web("#ff2d00"));
			lblStatus.setText("Kötelező mező!");
			return;
		}

		if (input.length() > 20) {

			lblStatus.setTextFill(Color.web("#ff2d00"));
			lblStatus.setText("Tárhelynév túl hosszú!");

			return;
		}

		if (!isUniquePosition(input)) {

			lblStatus.setTextFill(Color.web("#ff2d00"));
			lblStatus.setText("Tárhelynév foglalt!");
			return;
		}

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "INSERT INTO positions (position_name) VALUES (?)";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);

			st.setString(1, input);

			st.executeUpdate();

			lblStatus.setTextFill(Color.web("#1bff00"));
			lblStatus.setText("Tárhely sikeresen hozzáadva!");

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		fillTable();

	}

	private boolean isUniquePosition(String input) {

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT position_name FROM positions WHERE position_name = ?";

		try {

			PreparedStatement st = myConnection.connect().prepareStatement(sql);
			st.setString(1, input);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				myConnection.disconnect();
				return false;
			}

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		return true;
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
	
	@FXML
	private void generateBarcode() throws Exception {

		lblStatus.setText("");
		String input = txtPosition.getText();
		input = input.toUpperCase();
		if (isUniquePosition(input)) {

			lblStatus.setTextFill(Color.web("#ff2d00"));
			lblStatus.setText("Ez a tárhely nem létezik!");
			return;
		}

		Barcode barcode = new Barcode();

		barcode.positionCode(input);

		File file = new File("barcode_" + input + "_.png");

		Image image = new Image(file.toURI().toString());

		imgView.setImage(image);

	}

	@SuppressWarnings("unused")
	private void generatePositions() {
		MySqlConnection myConnection = new MySqlConnection();

		String sql = "INSERT INTO positions (position_name) VALUES (?)";

		int pack = 1;
		int stage = 0;
		int position = 1;

		for (int i = 1; i <= 14; i++) {

			for (int j = 0; j <= 5; j++) {

				for (int k = 1; k <= 4; k++) {

					pack = i;
					stage = j;
					position = k;

					String result = "";

					if (pack < 10) {

						result = "A-0" + pack + "-" + stage + "-" + position;

					} else {

						result = "A-" + pack + "-" + stage + "-" + position;
					}
					try {

						PreparedStatement st = myConnection.connect().prepareStatement(sql);

						st.setString(1, result);

						st.executeUpdate();

					} catch (SQLException e) {

						e.printStackTrace();
					} finally {
						myConnection.disconnect();
					}

				}

			}
		}

	}

}
