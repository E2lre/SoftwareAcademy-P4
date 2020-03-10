package com.parkit.parkingsystem.integration;

//import com.parkit.parkingsystem.constants.Fare;
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

//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertThat;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
		when(inputReaderUtil.readSelection()).thenReturn(1);
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	@Test
	public void processIncomingVehicle_aCarPark_idAndParkingTypeArePopulated() { //testParkingACar() {
		
		//WHEN
		
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processIncomingVehicle();
		// todo done: check that a ticket is actualy saved in DB and Parking table is
		// updated with availability

		//THEN
		// check that a ticket is actualy saved in DB : get the car position. It must be 1
		Ticket ticket = new Ticket();
		ticket = ticketDAO.getTicket("ABCDEF");
		// System.out.println("ticket "+ticket.getId());
		assertThat(ticket.getId()).isEqualTo(1);

		// Parking table is updated with availability : check if place 2 is free

		assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).isEqualTo(2);

	}

	@Test
	public void processExitingVehicle_aCarGetOut_returnAFeeAndOutTimeIsPopulated() {//testParkingLotExit() {
		System.out.println("start testParkingLotExit");
		long waitingTime = 5000; // Waiting time beteewn entry car and car out in miliseconds

		//WHEN
		Date inTime = new Date();
		// inTime.setTime( System.currentTimeMillis() + 2*waitingTime );

		processIncomingVehicle_aCarPark_idAndParkingTypeArePopulated();
		System.out.println("start wait");

		try {
			Thread.sleep(waitingTime); // Waiting for 30 seconds
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
		}
		System.out.println("End wait");
		
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();
		
		//THEN
		// todo done: check that the fare generated and out time are populated correctly in the database
		Ticket ticket = new Ticket();
		//ticket = ticketDAO.getTicket("ABCDEF");
		ticket = ticketDAO.getLastTicket("ABCDEF");
		System.out.println("Test exit date " + ticket.getOutTime() + " price " + ticket.getPrice());
		// out time are populated correctly in the database
		assertThat(ticket.getOutTime()).isAfter(inTime);
		// check that the fare generated
		// with story1 the reult will be 0 because 30 minutes free
		assertThat(ticket.getPrice()).isEqualTo(0);
	}

}
