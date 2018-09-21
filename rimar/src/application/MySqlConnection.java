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

//https://stackoverflow.com/questions/2839321/connect-java-to-a-mysql-database//



package application;

import java.sql.*;
import java.util.Properties;

public class MySqlConnection {

	private final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
	private final String DATABASE_URL = "jdbc:mysql://localhost:3306/rimardb?autoReconnect=true&useSSL=false";
	private final String USERNAME = "root";
	private final String PASSWORD = "barplate";
	private final String MAX_POOL = "250";

	private Connection connection;

	private Properties properties;

	public MySqlConnection() {

	}

	private Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			properties.setProperty("user", USERNAME);
			properties.setProperty("password", PASSWORD);
			properties.setProperty("MaxPooledStatements", MAX_POOL);
		}
		return properties;
	}

	public Connection connect() {
		if (connection == null) {
			try {
				Class.forName(DATABASE_DRIVER);
				connection = DriverManager.getConnection(DATABASE_URL, getProperties());
			} catch (ClassNotFoundException | SQLException e) {

				e.printStackTrace();

			}
		}
		return connection;
	}

	public void disconnect() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
