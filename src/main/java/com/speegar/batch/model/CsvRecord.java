
// 2. Data Model
package com.speegar.batch.model;

public class CsvRecord {
	private String id;
	private String name;
	private String description;
	private String country;
	private String phoneNumber;

	public CsvRecord() {
	}

	public CsvRecord(String id, String name, String description, String country, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.country = country;
		this.phoneNumber = phoneNumber;

	}

	// Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "CsvRecord{id='" + id + "', name='" + name + "', description='" + description + "'}";
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
}