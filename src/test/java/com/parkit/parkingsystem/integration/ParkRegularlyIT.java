package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@ExtendWith(MockitoExtension.class)
public class ParkRegularlyIT {
	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	// private static final Logger logger =
	// LogManager.getLogger("ParkingDataBaseIT");

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
		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {

	}

	public void testCarEnter1hourAgo() {
		System.out.println("testCarEnter1hourAgo start");
		// A Car enter one hour ago
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (60 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		Ticket ticket = new Ticket();
		ticket.setId(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setOutTime(null);
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
		ticketDAO.saveTicket(ticket);

	}

	public void testCarEnterAndExitYesterday() {
		System.out.println("testCarEnterAndExitYesterday start");
		// A Car enter and exit parking yesterday
		Date inTime = new Date();
		inTime.setTime(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
		Date outTime = new Date();
		outTime.setTime(System.currentTimeMillis() - (23 * 60 * 60 * 1000));

		ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, true);

		Ticket ticket = new Ticket();
		ticket.setId(1);
		ticket.setInTime(inTime);
		ticket.setParkingSpot(parkingSpot);
		ticket.setVehicleRegNumber("ABCDEF");
		ticket.setOutTime(outTime);
		ticket.setPrice(Fare.CAR_RATE_PER_HOUR);
		ticketDAO.saveTicket(ticket);

	}

	@Test
	public void processExitingVehicle_aCarEnterAndExistYesterdayAndEnterToday_returnAFeeCalculateOnTheSecondEntryWithFeeDiscount() {//testCarEnterPreviously() {
		// if a car is already enter, the fee must be calculate since the second entry, not the first and a discount fee's is present

		double minPrice = 1.49 * 0.95;  
		double maxPrice = 1.51 * 0.95;  
		//WHEN
		testCarEnterAndExitYesterday();
		testCarEnter1hourAgo();
		
		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		Ticket ticket = new Ticket();
		ticket = ticketDAO.getLastTicket("ABCDEF");
		System.out.println("Test exit date " + ticket.getOutTime() + " price " + ticket.getPrice());
		
		//THEN
		// check that the fare is correctly generated with discount
		assertThat(ticket.getPrice()).isBetween(minPrice, maxPrice);

	}

	@Test
	public void processExitingVehicle_aCarEnterOneHourAgoForTheFirstTime_returnFeeWithoutDiscount() {//testFarReductionForRecuringCar() {
		// // A car enter in the park one hour ago for the first time and exit now. No discount on the fee


		double minPrice = 1.49;
		double maxPrice = 1.51;
		
		//WHEN
		testCarEnter1hourAgo();

		//GIVEN
		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		parkingService.processExitingVehicle();

		Ticket ticket = new Ticket();
		ticket = ticketDAO.getLastTicket("ABCDEF");
		System.out.println("Test exit date " + ticket.getOutTime() + " price " + ticket.getPrice());
		
		//THEN
		// check that the fare is correctly generated. it must be 1.5
		assertThat(ticket.getPrice()).isBetween(minPrice, maxPrice);

	}

}
