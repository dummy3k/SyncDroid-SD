package de.syncdroid.db.model;

import java.util.Date;

public class Profile implements Model{
	private Long id;
	private String name;
	private Date lastSync;
	private Boolean onlyIfWifi = false;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getLastSync() {
		return lastSync;
	}
	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
	public Boolean getOnlyIfWifi() {
		return onlyIfWifi;
	}
	public void setOnlyIfWifi(Boolean onlyIfWifi) {
		this.onlyIfWifi = onlyIfWifi;
	}
	
	
}
