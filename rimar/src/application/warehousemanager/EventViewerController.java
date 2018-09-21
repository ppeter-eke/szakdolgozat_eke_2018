package application.warehousemanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import application.Event;
import application.MySqlConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EventViewerController {

	@FXML
	private Button btnBack;

	@FXML
	private Button btnRefresh;

	@FXML
	private TableView<Event> tblEventViewer;

	@FXML
	private TableColumn<Event, String> colTime;

	@FXML
	private TableColumn<Event, String> colName;

	@FXML
	private TableColumn<Event, String> colEventType;

	@FXML
	private TableColumn<Event, String> colDescription;
	
	@FXML
	private Label lblTotal;

	

	@FXML
	private void initialize() {

		fillEventViewer();
	}

	private void fillEventViewer() {
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "SELECT activity_timestamp, user_name, activity_name, activity_description FROM event_table";
			
		ObservableList<Event> data = FXCollections.observableArrayList();
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				
				String time = rs.getString(1);
				String userName = rs.getString(2);
				String activityName = rs.getString(3);
				String activityDescription = rs.getString(4);
				
				Event event = new Event(time, userName, activityName, activityDescription);
				
				data.add(event);
				
			}
			
			
			colTime.setCellValueFactory(new PropertyValueFactory<Event, String>("time"));
			colName.setCellValueFactory(new PropertyValueFactory<Event, String>("userName"));
			colEventType.setCellValueFactory(new PropertyValueFactory<Event, String>("activityName"));
			colDescription.setCellValueFactory(new PropertyValueFactory<Event, String>("activityDescription"));

			tblEventViewer.setItems(data);
			
			int size = tblEventViewer.getItems().size();
			
			lblTotal.setText("Összesen: " + size + " db esemény");
			
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

			Parent root = FXMLLoader
					.load(getClass().getResource("/application/warehousemanager/WarehouseManagerMain.fxml"));

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
