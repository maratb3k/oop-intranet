package project;


public class UserFactory {
	
	/**
	 * @param firstName
	 * @param lastName
	 * @param middleName
	 * @param login
	 * @param password
	 * @param role
	 */
	public static User createUser(String firstName, String lastName, String middleName, String login, String password, Role role) {
		
		User newUser = null;
		if (role == null) {
			return null;
		}
		for (User u : Intranet.INSTANCE.getAllUsers()) {
			if (u.login.equals(login)) return null;
		}
		
		if (role.equals(Role.ADMIN)) {
			newUser = new Admin(firstName, lastName, middleName, login, password, role);
		} else if (role.equals(Role.MANAGER)) {
			newUser = new Manager(firstName, lastName, middleName, login, password, role);
		} else if (role.equals(Role.STUDENT)) {
			newUser = new Student(firstName, lastName, middleName, login, password, role);
		} else if (role.equals(Role.TEACHER)) {
			newUser = new Teacher(firstName, lastName, middleName, login, password, role);
		} else if (role.equals(Role.TECH_SUPPORT)) {
			newUser = new TechSupport(firstName, lastName, middleName, login, password, role);
		}
		
		Intranet.INSTANCE.addUser(newUser);

		return newUser;
		
	}
}
