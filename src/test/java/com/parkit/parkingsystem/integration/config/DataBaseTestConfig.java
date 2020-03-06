package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ResourceBundle;

public class DataBaseTestConfig extends DataBaseConfig {

	private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");

	public Connection getConnection() throws ClassNotFoundException, SQLException {
		logger.info("Create DB connection");
		Class.forName("com.mysql.cj.jdbc.Driver");
		// return DriverManager.getConnection(
		// "jdbc:mysql://localhost:3306/test","root","rootroot");
		
		// Modify by E2lre for database acces
		ResourceBundle bundle = ResourceBundle.getBundle("com.parkit.parkingsystem.integration.config.config");
		String mysqlUrl = bundle.getString("mysql.urltest");
		String mysqlLogin = bundle.getString("mysql.login");
		String mysqlPassword = bundle.getString("mysql.password");
		return DriverManager.getConnection(mysqlUrl, mysqlLogin, mysqlPassword);

		// return DriverManager.getConnection(
		// "jdbc:mysql://localhost:3306/test?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=Europe/Paris","root","rootroot");
	}

	public void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
				logger.info("Closing DB connection");
			} catch (SQLException e) {
				logger.error("Error while closing connection", e);
			}
		}
	}

	public void closePreparedStatement(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				logger.info("Closing Prepared Statement");
			} catch (SQLException e) {
				logger.error("Error while closing prepared statement", e);
			}
		}
	}

	public void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				logger.info("Closing Result Set");
			} catch (SQLException e) {
				logger.error("Error while closing result set", e);
			}
		}
	}
}
