package net.eduard.api.setup.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnector {

	private String userName;
	private String password;
	private String host = "locahost";
	private int port = 3306;
	private String database;
	private String databasePath;

	private Connection connection;

	public DBConnector(String pathName) {
		this.databasePath = pathName;
	}

	public DBConnector(String userName, String password, String database) {
		this.userName = userName;
		this.password = password;
		this.database = database;
	}

	public Connection newMySQLConnection() {
		try {
			return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, userName,
					password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Connection newSQLiteConnection() {
		try {
			return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void openMySQLConnection() {
		closeConnection();
		this.connection = newMySQLConnection();
	}

	public void openSQLiteConnection() {
		closeConnection();
		this.connection = newSQLiteConnection();
	}

	public void closeConnection() {
		try {

			if (connection != null) {
				if (!connection.isClosed()) {
					connection.close();
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public boolean hasConnection() {

		if (connection == null)
			return false;
		try {
			return !connection.isClosed();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return false;

	}

	public PreparedStatement state(String sintax) {
		PreparedStatement state = null;
		if (hasConnection()) {
			try {
				if (!sintax.endsWith(";")) {
					sintax += ";";
				}
				state = connection.prepareStatement(sintax);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}

	public boolean update(String sintax) {
		try {
			state(sintax).executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public ResultSet query(String sintax) {
		ResultSet rs = null;
		if (hasConnection()) {
			PreparedStatement state = state(sintax);
			try {
				rs = state.executeQuery();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return rs;
	}

}