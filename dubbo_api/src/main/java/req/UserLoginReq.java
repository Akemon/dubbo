package req;

import java.io.Serializable;

/**
 * @Author khe
 * @Date 2019/1/29
 */
public class UserLoginReq implements Serializable {

	private static final long serialVersionUID = -8470006673487597376L;

	private String name;

	private String password;

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

	public UserLoginReq() {
	}

	public UserLoginReq(String name, String password) {

		this.name = name;
		this.password = password;
	}

	@Override
	public String toString() {
		return "UserLoginReq{" +
			"name='" + name + '\'' +
			", password='" + password + '\'' +
			'}';
	}
}
