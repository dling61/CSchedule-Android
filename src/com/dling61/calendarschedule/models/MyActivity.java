package com.dling61.calendarschedule.models;

/**
 * @author khoahuyen
 * 
 */
public class MyActivity {

	private String activity_ID = "";
	private int owner_ID = -1;
	private int alert = -1;
	private int repeat = -1;
	private String activity_name = "";
	private String starttime = "";
	private String endtime = "";
	private String desp = "";
	private int otc_offset = -1;
	private int role = -1;

	public MyActivity(String id, int ownid, int alrt, int rep, String name,
			String start, String end, String des, int otc, int r) {
		activity_ID = id;
		owner_ID = ownid;
		alert = alrt;
		repeat = rep;
		activity_name = name;
		starttime = start;
		endtime = end;
		desp = des;
		otc_offset = otc;
		role = r;
	}

	public String getActivity_ID() {
		return activity_ID;
	}

	public void setActivity_ID(String activity_ID) {
		this.activity_ID = activity_ID;
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
	 * @return the alert
	 */
	public int getAlert() {
		return alert;
	}

	/**
	 * @param alert
	 *            the alert to set
	 */
	public void setAlert(int alert) {
		this.alert = alert;
	}

	/**
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the activity_name
	 */
	public String getActivity_name() {
		return activity_name;
	}

	/**
	 * @param activity_name
	 *            the activity_name to set
	 */
	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
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

	/**
	 * @return the otc_offset
	 */
	public int getOtc_offset() {
		return otc_offset;
	}

	/**
	 * @param otc_offset
	 *            the otc_offset to set
	 */
	public void setOtc_offset(int otc_offset) {
		this.otc_offset = otc_offset;
	}

	/**
	 * @return the role
	 */
	public int getRole() {
		return role;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(int role) {
		this.role = role;
	}
}
