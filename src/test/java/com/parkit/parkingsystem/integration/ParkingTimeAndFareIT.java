package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Date;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/*
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
*/

@ExtendWith(MockitoExtension.class)
public class ParkingTimeAndFareIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    
   //private static final Logger logger = LogManager.getLogger("ParkingDataBaseIT");

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
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

  
    public void testParkingACar10MinAgo(){

    	//Calculate the date 10 minutes ago : it will be the date of entry car
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  10 * 60 * 1000) );

    	ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,true);

    	Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF"); 
        ticket.setOutTime(null);
        ticket.setPrice(0);
        ticketDAO.saveTicket(ticket);
    
    }
  public void testParkingACar1HourAgo(){

    	//Calculate the date 10 minutes ago : it will be the date of entry car
    	Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );

    	ParkingSpot parkingSpot = new ParkingSpot(1,ParkingType.CAR,true);

    	Ticket ticket = new Ticket();
        ticket.setId(1);
        ticket.setInTime(inTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF"); 
        ticket.setOutTime(null);
        ticket.setPrice(0);
        ticketDAO.saveTicket(ticket);
         
    }
    @Test
    public void testParkingLotExitLessThan30Min(){
  
    	System.out.println("start testParkingLotExitLessThan30Min");
     
        testParkingACar10MinAgo();
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        
        Date outTime = new Date();
        
        //todo done: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket("ABCDEF");
        System.out.println("Test exit date " +ticket.getOutTime() + " price " + ticket.getPrice());
       //out time are populated correctly in the database for 10 minutes
       //TODO E2lre : est on certain que le outime est correct si on déclenche le test à 59 secondes?
        assertThat(ticket.getOutTime()).isEqualToIgnoringSeconds(outTime);
        //check that the fare generated. it must be 0 
        assertThat(ticket.getPrice()).isEqualTo(0);
    }
  
    @Test
    public void testParkingLotExitLessMore30Min(){
 
    	System.out.println("start testParkingLotExitLessMore30Min");

        double minPrice = 1.49;
        double maxPrice = 1.51;
        
        testParkingACar1HourAgo();
        
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();
        
        Date outTime = new Date();
        
        //todo done: check that the fare generated and out time are populated correctly in the database
        Ticket ticket = new Ticket();
        ticket = ticketDAO.getTicket("ABCDEF");
        System.out.println("Test exit date " +ticket.getOutTime() + " price " + ticket.getPrice());
       //out time are populated correctly in the database for 1 hour
       //TODO E2lre : est on certain que le outime est correct si on déclenche le test à 59 minutes?
        assertThat(ticket.getOutTime()).isEqualToIgnoringSeconds(outTime);
        //check that the fare generated
        assertThat(ticket.getPrice()).isBetween(minPrice, maxPrice);
    }
}
