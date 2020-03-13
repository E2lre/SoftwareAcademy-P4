package com.parkit.parkingsystem.model;

import java.util.Date;

public class Ticket {
	private int id;
	private ParkingSpot parkingSpot;
	private String vehicleRegNumber;
	private double price;
	private Date inTime;
	private Date outTime;
	private int numberEntry;//E2lre add for fee reduction management

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParkingSpot getParkingSpot() {
		return parkingSpot;
	}

	public void setParkingSpot(ParkingSpot parkingSpot) {
		this.parkingSpot = parkingSpot;
	}

	public String getVehicleRegNumber() {
		return vehicleRegNumber;
	}

	public void setVehicleRegNumber(String vehicleRegNumber) {
		this.vehicleRegNumber = vehicleRegNumber;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getInTime() {
		Date localDate = inTime;
		return localDate;
	}

	public void setInTime(Date inTime) {
		//E2lre : protection if intime is null
		if (inTime == null) {
			this.inTime = null;
		}
		else {
			this.inTime = new Date(inTime.getTime());
		}

		//this.inTime = new Date(inTime.getTime());
	}

	public Date getOutTime() {
		Date localDate = outTime;
		return localDate;
//		return outTime;
	}

	public void setOutTime(Date outTime) {
		//E2lre : the outime is null when a vehicule enter
		if (outTime == null) {
			this.outTime = null;
		}
		else {
			this.outTime = new Date(outTime.getTime());
		}
	}
	
	//E2lre add for fee reduction management	
	public int getNumberEntry() {
		return numberEntry;
	}

	public void setNumberEntry(int numberEntry) {
		this.numberEntry = numberEntry;
	}
}
