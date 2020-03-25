package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	//E2lre  public DataBaseConfig dataBaseConfig = new DataBaseConfig();
	public DataBaseConfig dataBaseConfig = null;
	
	//E2lre
	public TicketDAO() {
		dataBaseConfig = new DataBaseConfig();
	}
	public TicketDAO(DataBaseConfig dataBaseConfig) {
		this.dataBaseConfig = dataBaseConfig;
	}
	
	public boolean saveTicket(Ticket ticket) {
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseConfig.getConnection();
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
				// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	//		try {
				ps.setInt(1, ticket.getParkingSpot().getId());
				ps.setString(2, ticket.getVehicleRegNumber());
				ps.setDouble(3, ticket.getPrice());
				ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
				ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
				ps.execute();
				return true;
//			} catch (Exception ex) {
//				logger.error("Error fetching next available slot on ps execute", ex);
//				return false;//E2lre to avoid return on finally
//			} finally {
//				ps.close();
//			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
			return false;//E2lre to avoid return on finally
		} finally {
			dataBaseConfig.closePreparedStatement(ps);
			dataBaseConfig.closeConnection(con);
			//return false;//E2lre to avoid return on finally
		}
	}
 

//TODO try catch close
	// E2lre add for testing
	public Ticket getLastTicket(String vehicleRegNumber) {
		Connection con = null;
		Ticket ticket = null;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_LAST_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			try {
				ps.setString(1, vehicleRegNumber);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					ticket = new Ticket();
					ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
					ticket.setParkingSpot(parkingSpot);
					ticket.setId(rs.getInt(2));
					ticket.setVehicleRegNumber(vehicleRegNumber);
					ticket.setPrice(rs.getDouble(3));
					ticket.setInTime(rs.getTimestamp(4));
					ticket.setOutTime(rs.getTimestamp(5));
				}
				if(rs.last()){
					ticket.setNumberEntry(rs.getRow()); 
				}
				dataBaseConfig.closeResultSet(rs);
				dataBaseConfig.closePreparedStatement(ps);
				return ticket; // E2lre to avoid return on finally
			} catch (Exception ex) {
				logger.error("Error fetching next available slot on ps execute", ex);
				return null;
			} finally {
				ps.close();
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
			return null;// E2lre to avoid return on finally
		} finally {
			dataBaseConfig.closeConnection(con);
			// return ticket;//E2lre to avoid return on finally
		}
	}


	// E2lre add for fee reduction management
	public int numberOfTicket(String vehicleRegNumber) {
		Connection con = null;
		
		int numberOfTicket = 0;
		try {
			con = dataBaseConfig.getConnection();
			PreparedStatement ps = con.prepareStatement(DBConstants.GET_COUNT_TICKET);
			// Count on VEHICLE_REG_NUMBER
			try {
				ps.setString(1, vehicleRegNumber);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					numberOfTicket = rs.getInt(1);
				}
				dataBaseConfig.closeResultSet(rs);
				dataBaseConfig.closePreparedStatement(ps);
				return numberOfTicket; // E2lre to avoid return on finally
			} catch (Exception ex) {
				logger.error("Error fetching next available slot on ps execute", ex);
				return -1;
			} finally {
				ps.close();
			}
		} catch (Exception ex) {
			logger.error("Error fetching next available slot", ex);
			return -1;// E2lre to avoid return on finally
		} finally {
			dataBaseConfig.closeConnection(con);
			// return ticket;//E2lre to avoid return on finally
		}
	}
	public boolean updateTicket(Ticket ticket) {
		Connection con = null;
		try {
			con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
				try {
				ps.setDouble(1, ticket.getPrice());
				ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
				ps.setInt(3, ticket.getId());
				ps.execute();
				ps.close(); //E2lre add to close ps
				return true;
			} catch (Exception ex) {
				logger.error("Error fetching next available slot on ps execute", ex);
				return false;
			} finally {
				ps.close();
			}
		} catch (Exception ex) {
			logger.error("Error saving ticket info", ex);
			return false;
		} finally {
			dataBaseConfig.closeConnection(con);
		}
	}
}
