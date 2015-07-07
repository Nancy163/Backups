package net.USky.entity;

public class UserInfo {
	private String username;
	private String telphone;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTelphone() {
		return telphone;
	}

	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}

	@Override
	public String toString() {
		return "UserInfo [username=" + username + ", telphone=" + telphone
				+ "]";
	}

}
