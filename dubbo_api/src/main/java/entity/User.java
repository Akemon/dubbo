package entity;

import java.io.Serializable;

/**
 * @Author khe
 * @Date 2019/1/28
 */
public class User implements Serializable {

	private static final long serialVersionUID = -6772117257504457541L;
	private String name;
	private String password;

	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	@Override
	public String toString() {
		return "User{" +
			"name='" + name + '\'' +
			", password='" + password + '\'' +
			'}';
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User() {
	}
}
