package com.dling61.calendarschedule.models;

public class Schedule {
	private int owner_ID=-1;
	private int schedule_ID=-1;
	private int service_ID=-1;
	private String starttime="";
	private String endtime="";
	private String desp="";
	
	
	public Schedule(int o_id, int sche_id, int serv_id, String start, String end,String des)
	{
		owner_ID = o_id;
		schedule_ID = sche_id;
		service_ID = serv_id;
		starttime = start;
		endtime = end;
		desp = des;
	}


	/**
	 * @return the owner_ID
	 */
	public int getOwner_ID() {
		return owner_ID;
	}


	/**
	 * @param owner_ID the owner_ID to set
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
	 * @param schedule_ID the schedule_ID to set
	 */
	public void setSchedule_ID(int schedule_ID) {
		this.schedule_ID = schedule_ID;
	}


	/**
	 * @return the service_ID
	 */
	public int getService_ID() {
		return service_ID;
	}


	/**
	 * @param service_ID the service_ID to set
	 */
	public void setService_ID(int service_ID) {
		this.service_ID = service_ID;
	}


	/**
	 * @return the starttime
	 */
	public String getStarttime() {
		return starttime;
	}


	/**
	 * @param starttime the starttime to set
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
	 * @param endtime the endtime to set
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
	 * @param desp the desp to set
	 */
	public void setDesp(String desp) {
		this.desp = desp;
	}
}


