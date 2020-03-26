package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {
		lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void processIncomingVehicle_aCarPark_idAndParkingTypeArePopulated() { 
		
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// todo done: check that a ticket is actualy saved in DB and Parking table is
		// updated with availability

		//WHEN
		// check that a ticket is actualy saved in DB : get the car position. It must be 1
		Ticket ticket = new Ticket();
		//ticket = ticketDAO.getTicket("ABCDEF"); // E2lre : getTicket change in getLastTicket
		ticket = ticketDAO.getLastTicket("ABCDEF");
		
		//THEN		
		assertThat(ticket.getId()).isEqualTo(1);
		// Parking table is updated with availability : check if place 2 is free
		assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).isEqualTo(2);

	}

	@Test
	public void processExitingVehicle_aCarGetOut_returnAFeeAndOutTimeIsPopulated() {
		System.out.println("start testParkingLotExit");
		long waitingTime = 5000; // Waiting time beteewn entry car and car out in miliseconds

		//GIVEN : a car enter now
		Date inTime = new Date();
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		
		Ticket ticket = new Ticket();


		// waiting, the car ins park
		System.out.println("start wait");
		try {
			Thread.sleep(waitingTime); // Waiting for 30 seconds
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		System.out.println("End wait");
		
		//WHEN
		parkingService.processExitingVehicle();
		
		//THEN
		// todo done: check that the fare generated and out time are populated correctly in the database
		ticket = ticketDAO.getLastTicket("ABCDEF");
		// out time are populated correctly in the database
		assertThat(ticket.getOutTime()).isNotNull();
		assertThat(ticket.getOutTime()).isAfter(inTime);
		// check that the fare generated with story1 the result will be 0 because 30 minutes free
		assertThat(ticket.getPrice()).isEqualTo(0);
		
	}
	
	@Test
	public void processExitingVehicle_aCarGetOut_returnAFeeAndOutTimeIsPopulatedByMock() {

		//GIVEN 
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (10 * 60 * 1000)); //Car entry 10 minutes ago
		
		processIncomingVehicle_aCarPark_idAndParkingTypeArePopulatedByMock(inTime);
	
		
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		
		//WHEN
		parkingService.processExitingVehicle();
		
		//THEN
		// todo done: check that the fare generated and out time are populated correctly in the database
		Ticket ticket = new Ticket();
		ticket = ticketDAO.getLastTicket("ABCDEF");
		// out time are populated correctly in the database
		assertThat(ticket.getOutTime()).isNotNull();
		assertThat(ticket.getOutTime()).isAfter(inTime);
		// check that the fare generated with story1 the reult will be 0 because 30 minutes free
		assertThat(ticket.getPrice()).isEqualTo(0);
		
	}
	public void processIncomingVehicle_aCarPark_idAndParkingTypeArePopulatedByMock(Date inTime) { 
		
		//To avoid dependancies with other fonctions, Data injection in database
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataBaseTestConfig.getConnection();
			// ParkingSpot injection
			ps = con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
			
			boolean parkingSpotIsAvaliable = false;
			int parkingSpotId = 1;
			ps.setBoolean(1, parkingSpotIsAvaliable);
			ps.setInt(2, parkingSpotId);
			ps.executeUpdate();
			dataBaseTestConfig.closePreparedStatement(ps);
			
			//Ticket Injection
			ps = con.prepareStatement(DBConstants.SAVE_TICKET);
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			String ticketVehicleRegNumber = "ABCDEF";
			double ticketPrice = 0;
			ps.setInt(1, parkingSpotId);
			ps.setString(2, ticketVehicleRegNumber);
			ps.setDouble(3, ticketPrice);
			ps.setTimestamp(4,new Timestamp(inTime.getTime()));
			ps.setTimestamp(5,  null);
			ps.execute();
			
	
		} catch (Exception ex) {
			logger.error("Error updating parking info during test", ex);
					} finally {
			dataBaseTestConfig.closePreparedStatement(ps);
			dataBaseTestConfig.closeConnection(con);
		}
		
		

	}

}
