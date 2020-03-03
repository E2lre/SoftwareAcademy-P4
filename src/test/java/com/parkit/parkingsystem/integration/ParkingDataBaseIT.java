package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
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

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

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
    private static void setUp() throws Exception{
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
    private static void tearDown(){

    }

    @Test
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actualy saved in DB and Parking table is updated with availability
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket("ABCDEF");
        System.out.println("ticket "+ticket.getId());
        assertEquals(ticket.getId(), 1);
        
    }

    @Test
    public void testParkingLotExit(){
        System.out.println("start testParkingLotExit");
    	testParkingACar();
    	System.out.println("start wait");
        try {
        Thread.sleep(60000);
        }
        catch (InterruptedException e){
        	logger.error(e.getMessage());
        }
        System.out.println("End wait");
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        //TODO: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket("ABCDEF");
        System.out.println("Test exit date " +ticket.getOutTime() + " price " + ticket.getPrice());
        assertNotEquals(ticket.getPrice(), 0); //TODO E2lre A changer et compléter
     
    }

}
