package com.e2wstudy.cschedule.models;

public class Schedule {
	private int owner_ID = -1;
	private int schedule_ID = -1;
	private String service_ID = "";
	private String starttime = "";
	private String endtime = "";
	private String desp = "";
//	private String abbrtzname = "";// timezone
	private int alert = 0;
	public Schedule(int owner_ID, int schedule_ID, String service_ID,
			String starttime, String endtime, String desp, int alert, int tzid) {
		super();
		this.owner_ID = owner_ID;
		this.schedule_ID = schedule_ID;
		this.service_ID = service_ID;
		this.starttime = starttime;
		this.endtime = endtime;
		this.desp = desp;
		this.alert = alert;
		this.tzid = tzid;
	}

	private int tzid=0;
//
//	public Schedule(int o_id, int sche_id, String serv_id, String start,
//			String end, String des, String timezone, int alert) {
//		owner_ID = o_id;
//		schedule_ID = sche_id;
//		service_ID = serv_id;
//		starttime = start;
//		endtime = end;
//		desp = des;
//		this.abbrtzname = timezone;
//		this.alert = alert;
//	}
//
//	public String getAbbrtzname() {
//		return abbrtzname;
//	}
//
//	public void setAbbrtzname(String abbrtzname) {
//		this.abbrtzname = abbrtzname;
//	}

	public int getTzid() {
		return tzid;
	}

	public void setTzid(int tzid) {
		this.tzid = tzid;
	}

	public int getAlert() {
		return alert;
	}

	public void setAlert(int alert) {
		this.alert = alert;
	}

	/**
	 * @return the owner_ID
	 */
	public int getOwner_ID() {
		return owner_ID;
	}

	/**
	 * @param owner_ID
	 *            the owner_ID to set
	 */
	public void setOwner_ID(int owner_ID) {
		this.owner_ID = owner_ID;
	}

	/**
	 * @return the schedule_ID
	 */
	public int getSchedule_ID() {
		return schedule_ID;
	}

	/**
	 * @param schedule_ID
	 *            the schedule_ID to set
	 */
	public void setSchedule_ID(int schedule_ID) {
		this.schedule_ID = schedule_ID;
	}

	public String getService_ID() {
		return service_ID;
	}

	public void setService_ID(String service_ID) {
		this.service_ID = service_ID;
	}

	/**
	 * @return the starttime
	 */
	public String getStarttime() {
		return starttime;
	}

	/**
	 * @param starttime
	 *            the starttime to set
	 */
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	/**
	 * @return the endtime
	 */
	public String getEndtime() {
		return endtime;
	}

	/**
	 * @param endtime
	 *            the endtime to set
	 */
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	/**
	 * @return the desp
	 */
	public String getDesp() {
		return desp;
	}

	/**
	 * @param desp
	 *            the desp to set
	 */
	public void setDesp(String desp) {
		this.desp = desp;
	}
}
