package com.e2w.cschedule.models;

public class Participant {
	private int id=-1;
	private String name="";
	private String email="";
	private String mobile="";
	private int owner_id=-1;
	public boolean isChecked=false;//mark checkbox uncheck
	public Participant(int p_id,String p_name, String p_email, String p_mobile, int o_id)
	{
		id = p_id;
		name = p_name;
		email = p_email;
		mobile = p_mobile;
		owner_id = o_id;
	}
	
	public int getID()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getMobile()
	{
		return mobile;
	}
	
	public int getOwnerID()
	{
		return owner_id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}
	
	public void setOwnerId(int ownerid)
	{
		this.owner_id = ownerid;
	}
	public Participant()
	{
		
	}
}
