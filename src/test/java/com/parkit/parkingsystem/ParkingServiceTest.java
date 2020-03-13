package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

	private static ParkingService parkingService;

	@Mock
	private static InputReaderUtil inputReaderUtil;
	@Mock
	private static ParkingSpotDAO parkingSpotDAO;
	@Mock
	private static TicketDAO ticketDAO;

	@BeforeEach
	private void setUpPerTest() {
		try {
			lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");// E2lre : lenient is used to avoid duplicate code in two test method

			ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
			Ticket ticket = new Ticket();
			ticket.setInTime(new Date(System.currentTimeMillis() - (60 * 60 * 1000)));
			ticket.setParkingSpot(parkingSpot);
			ticket.setVehicleRegNumber("ABCDEF");
			
			lenient().when(ticketDAO.getLastTicket(anyString())).thenReturn(ticket); // E2lre : lenient is used to avoid duplicate code on ticket Class on test method
			
			lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);// E2lre : lenient is used to avoid duplicate code in two test method

			parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to set up test mock objects");
		}
	}

	@Test
	public void  processExitingVehicle_vehiculeExiting_updateIsCallOneTime() {
		//GIVEN
		when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

		//WHEN
		parkingService.processExitingVehicle();
		
		//THEN
		verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
	}

	//add test El2re
	@Test
	public void IncomingVehicle_aBikeAlreadyEnter_messageSended () {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		
	    //GIVEN
	    when(ticketDAO.numberOfTicket(any(String.class))).thenReturn(2); //E2lre add for incoming 2 times for a car
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); //E2lre add for incoming 2 times for a car
		when(inputReaderUtil.readSelection()).thenReturn(2); //E2lre add for incoming 2 times for a bike

		//WHEN		
		parkingService.processIncomingVehicle();
		
		//THEN 
		assertThat(outContent.toString()).contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a "+Fare.RECURRING_FEE_BENEFIT+"% discount.");
	}
	
	//add test El2re
	@Test
	public void IncomingVehicle_aCarAlreadyEnter_messageSended () {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		
	    //GIVEN
	    when(ticketDAO.numberOfTicket(any(String.class))).thenReturn(2); //E2lre add for incoming 2 times for a car
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(1); //E2lre add for incoming 2 times for a car
		when(inputReaderUtil.readSelection()).thenReturn(1); //E2lre add for incoming 2 times for a car

		//WHEN		
		parkingService.processIncomingVehicle();
		
		//THEN 
		assertThat(outContent.toString()).contains("Welcome back! As a recurring user of our parking lot, you'll benefit from a "+Fare.RECURRING_FEE_BENEFIT+"% discount.");
	}
	
	//add test El2re
	@Test
	public void getNextParkingNumberIfAvailable_parkingFull_errorMessageSended () {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		
	    //GIVEN
		when(parkingSpotDAO.getNextAvailableSlot(any(ParkingType.class))).thenReturn(0); //E2lre add for incoming 2 times for a car
		when(inputReaderUtil.readSelection()).thenReturn(2); //E2lre add for incoming 2 times for a bike //return a bike

		//WHEN		
		parkingService.getNextParkingNumberIfAvailable();
		//THEN
		assertThat(outContent.toString()).contains("Parking slots might be full");
	}
	
	//add test El2re
	@Test
	public void getNextParkingNumberIfAvailable_unknownVehiculeTypeIndicated_errorMessageSended () {
		ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	    System.setOut(new PrintStream(outContent));
		
	    //GIVEN
	    when(inputReaderUtil.readSelection()).thenReturn(3); //E2lre unknown vehicule type is send

		//WHEN		
		parkingService.getNextParkingNumberIfAvailable();
		//THEN
		assertThat(outContent.toString()).contains("Incorrect input provided");
	}
	

}
