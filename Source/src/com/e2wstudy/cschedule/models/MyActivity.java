package com.e2wstudy.cschedule.models;

/**
 * @author khoahuyen
 * 
 */
public class MyActivity {
	private String activity_ID = "";
	private int owner_ID = -1;
	private String activity_name = "";
	private String desp = "";
	private int alertId=-1;
	private int timezoneId=-1;
	private int role = -1;

	public int getAlertId() {
		return alertId;
	}

	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}

	public int getTimezoneId() {
		return timezoneId;
	}

	public void setTimezoneId(int timezoneId) {
		this.timezoneId = timezoneId;
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
	 * @return the role
	 */
	public int getRole() {
		return role;
	}

	public MyActivity(String activity_ID, int owner_ID, String activity_name,
			String desp, int role, int alertId, int timezoneId) {
		super();
		this.activity_ID = activity_ID;
		this.owner_ID = owner_ID;
		this.activity_name = activity_name;
		this.desp = desp;
		this.role = role;
		this.alertId=alertId;
		this.timezoneId=timezoneId;
	}

	/**
	 * @param role
	 *            the role to set
	 */
	public void setRole(int role) {
		this.role = role;
	}
}
