package com.e2wstudy.cschedule.models;

public class Sharedmember {
	private int id=-1;
	private String name="";
	private String email="";
	private String mobile="";
	private int role=-1;
	private int service_id=-1;
	public boolean isChecked=false;//mark checkbox uncheck
	public Sharedmember(int p_id,String p_name, String p_email, String p_mobile, int p_role, int s_id)
	{
		id = p_id;
		name = p_name;
		email = p_email;
		mobile = p_mobile;
		role = p_role;
		service_id = s_id;
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
	
	public int getRole()
	{
		return role;
	}
	
	public int getService ()
	{
		return service_id;
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
	
	public void setRole (int r)
	{
		this.role = r;
	}
	
	public void setServiceId(int s_id)
	{
		this.service_id = s_id;
	}
}
