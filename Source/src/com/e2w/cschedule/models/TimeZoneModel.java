package com.e2w.cschedule.models;
/**
 * @author khoahuyen
 * */
public class TimeZoneModel {
	private int id = -1;
	private String tzname ="";
	private String displayname = "";
	private String displayorder = "";
	private String abbrtzname = "";

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getDisplayname() {
		return displayname;
	}

	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}

	public String getDisplayorder() {
		return displayorder;
	}

	public void setDisplayorder(String displayorder) {
		this.displayorder = displayorder;
	}

	public String getAbbrtzname() {
		return abbrtzname;
	}

	public void setAbbrtzname(String abbrtzname) {
		this.abbrtzname = abbrtzname;
	}

	public String getTzname() {
		return tzname;
	}

	public void setTzname(String tzname) {
		this.tzname = tzname;
	}

	public TimeZoneModel(int id, String tzname, String displayname,
			String displayorder, String abbrtzname) {
		super();
		this.id = id;
		this.tzname = tzname;
		this.displayname = displayname;
		this.displayorder = displayorder;
		this.abbrtzname = abbrtzname;
	}

	

}
