package com.example.mychat.Notifications;

public class Data {
	private String user;
	private int icon;
	private String user;
	private String body;
	private String sented;

	public Data(String user, int icon, String user1, String body, String sented) {
		this.user = user;
		this.icon = icon;
		this.user = user1;
		this.body = body;
		this.sented = sented;
	}
	public Data(){

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSented() {
		return sented;
	}

	public void setSented(String sented) {
		this.sented = sented;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}
}
