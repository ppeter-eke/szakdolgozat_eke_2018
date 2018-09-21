package application.warehousemanager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import application.MySqlConnection;
import application.SelfInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class PerformanceController {

	private SelfInfo self = new SelfInfo();
	private int numOfTotalPositions = self.getNumOfStoragePositions();

	@FXML
	private Button btnBack;

	@FXML
	private PieChart pieUsage;

	@FXML
	private BarChart<String, Integer> barUsage;

	@FXML
	private CategoryAxis xBarAxis;

	@FXML
	private NumberAxis yBarAxis;
	
	@FXML
	private CategoryAxis xLineAxis;
	
	@FXML
	private NumberAxis yLineAxis;
	
	@FXML
	private LineChart<String, Integer> lineChart;

	@FXML
	private void initialize() throws ParseException {

		fillPieChart();
		fillBarChart();
		fillLineChart();

	}

	private void fillPieChart() {

		int occupiedPositions = 0;

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(DISTINCT pallet_position) FROM pallet WHERE offloaded = 0 and archived = 0";

		try {

			PreparedStatement ps = myConnection.connect().prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				occupiedPositions = rs.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
		
		int freePositions = numOfTotalPositions - occupiedPositions;
		
		pieChartData.add(new Data("Szabad tárhely", freePositions));
		pieChartData.add(new Data("Foglalt tárhely", occupiedPositions));

		pieUsage.setData(pieChartData);

		double percentage = (occupiedPositions * 1.0) / (numOfTotalPositions * 1.0);

		pieUsage.setTitle("Raktárkihasználtság: " + percentage + "%" + "\n" + "Összes tárhely: " + numOfTotalPositions
				+ "\n" + "Foglalt tárhely: " + occupiedPositions + "\n" + "Szabad tárhely: " + freePositions);

		

	}

	@SuppressWarnings("unchecked")
	private void fillBarChart() {

		barUsage.setTitle("Tárhelyhasználat partnerenként");
		

		XYChart.Series<String, Integer> series1 = new XYChart.Series<>();

		MySqlConnection myConnection = new MySqlConnection();

		String sql = "SELECT COUNT(pallet_id), partner_name FROM pallet, partners WHERE archived = 0 AND offloaded = 0 AND pallet.partner_id = partners.partner_id GROUP BY pallet.partner_id";

		try {

			PreparedStatement ps = myConnection.connect().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				series1.getData().add(new XYChart.Data<String, Integer>(rs.getString(2), rs.getInt(1)));

			}
			
			barUsage.getData().addAll(series1);
			barUsage.setLegendVisible(false);

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			myConnection.disconnect();
		}
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillLineChart() throws ParseException {
		
		MySqlConnection myConnection = new MySqlConnection();
		
		String sql = "SELECT COUNT(pallet_id), arrival_date FROM pallet WHERE archived = 0 AND offloaded = 0 GROUP BY arrival_date";
		String sql2 = "SELECT COUNT(pallet_id), delivery_date FROM pallet WHERE archived = 1 AND offloaded = 1 GROUP BY delivery_date";
			
		XYChart.Series<String, Integer> series1 = new XYChart.Series();
		XYChart.Series<String, Integer> series2 = new XYChart.Series();
		
		
		try {
			
			PreparedStatement ps = myConnection.connect().prepareStatement(sql);
			PreparedStatement ps2 = myConnection.connect().prepareStatement(sql2);			
			ResultSet rs = ps.executeQuery();
			ResultSet rs2 = ps2.executeQuery();
			
			while(rs.next()) {
				
				
				series1.getData().add(new XYChart.Data<String, Integer>(rs.getString(2), rs.getInt(1)));
		}
			
			while(rs2.next()) {
				
				series2.getData().add(new XYChart.Data<String, Integer>(rs2.getString(2), rs2.getInt(1)));

			}
			
			lineChart.getData().addAll(series1, series2);
			lineChart.setTitle("Kitárolt és betárolt paletták száma");
			series1.setName("Betárolt paletta");
			series2.setName("Kitárolt paletta");
		
			
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
