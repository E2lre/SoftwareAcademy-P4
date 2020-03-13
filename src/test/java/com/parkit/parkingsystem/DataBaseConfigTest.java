package com.parkit.parkingsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;


import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;

public class DataBaseConfigTest {
	@Test
	public void  getConnection_processOpenCloseConnection_catalogIsProd() {
		DataBaseConfig dataBaseConfig = new DataBaseConfig();
		Connection con = null;
		PreparedStatement ps = null;
		String catalog = null;
		//GIVEN
		
		//WHEN
		try{
			con = dataBaseConfig.getConnection();
			catalog = con.getCatalog();
			System.out.println( "Catalog "+catalog);
			 ps = con.prepareStatement(DBConstants.GET_TICKET);
			
		} catch (Exception ex) {
			System.out.println("Test processOpenCloseConnectionTest KO ");
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
		}
		//THEN
		assertThat(catalog).contains("prod");
	}

}
