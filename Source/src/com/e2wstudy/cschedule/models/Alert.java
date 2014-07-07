package com.e2wstudy.cschedule.models;

/**
 * @author khoahuyen
 * */
public class Alert {
	private int id = -1;
	private String aname = "";

	public Alert(int id, String aname) {
		super();
		this.id = id;
		this.aname = aname;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAname() {
		return aname;
	}

	public void setAname(String aname) {
		this.aname = aname;
	}

}
