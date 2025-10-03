package connectDB;/*
 * @ (#) ConnectDB.java   1.0     25/09/2025
package connectDB;


/**
 * @description :
 * @author : Vy, Pham Kha Vy
 * @version 1.0
 * @created : 25/09/2025
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectDB {
	private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=HeThongQuanLyBanVeTauGaSaiGon_V5;encrypt=false;";
	private static final String USER = "sa";
	private static final String PASSWORD = "sapassword";

	private static ConnectDB instance;
	private Connection connection;

	private ConnectDB() {
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Đảm bảo rằng mỗi lần gọi getInstance() sẽ trả về cùng một đối tượng ConnectionDB đã tồn tại.
	public static ConnectDB getInstance() {
		if (instance == null) {
			synchronized (ConnectDB.class) {
				if (instance == null) {
					instance = new ConnectDB();
				}
			}
		}
		return instance;
	}

	public Connection getConnection() {
		try {
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}


	public Connection connect() {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

	public void disconnect() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void close(PreparedStatement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
