package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		// todo done: Some tests are failing here. Need to check if this logic is
		// E2lre : corrected
		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		float duration = (float) (outHour - inHour) / 3600000;

		// E2lre : add control : if duration is less than 30 minutes, park is free
		if (duration < Fare.PARK_TIME_FREE) {
			ticket.setPrice(0);
		} else {
			switch (ticket.getParkingSpot().getParkingType()) {
			case CAR: {
				ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		}
		if (ticket.getNumberEntry()>1) {
			ticket.setPrice(ticket.getPrice()*(1-(Fare.RECURRING_FEE_BENEFIT)/100));
			System.out.println("As a recurring user of our parking lot, you benefit from a "+Fare.RECURRING_FEE_BENEFIT+"% discount");
			
		}

	}
}