package project;

import java.io.*;
import java.util.*;

public class Tester {

	/**
	 * @deprecated
	 * @param args
	 */
	public static void main(String[] args) {
		UserFactory.createUser("Alen", "Admin", "", "alen", "admin", Role.ADMIN);
		
		System.out.println(
				" _    _      _                            _          _____      _                        _   \r\n"
						+ "| |  | |    | |                          | |        |_   _|    | |                      | |  \r\n"
						+ "| |  | | ___| | ___ ___  _ __ ___   ___  | |_ ___     | | _ __ | |_ _ __ __ _ _ __   ___| |_ \r\n"
						+ "| |/\\| |/ _ \\ |/ __/ _ \\| '_ ` _ \\ / _ \\ | __/ _ \\    | || '_ \\| __| '__/ _` | '_ \\ / _ \\ __|\r\n"
						+ "\\  /\\  /  __/ | (_| (_) | | | | | |  __/ | || (_) |  _| || | | | |_| | | (_| | | | |  __/ |_ \r\n"
						+ " \\/  \\/ \\___|_|\\___\\___/|_| |_| |_|\\___|  \\__\\___/   \\___/_| |_|\\__|_|  \\__,_|_| |_|\\___|\\__|\r\n"
						+ "                                                                                             \r\n"
						+ "                                                                                             ");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true) {

				System.out.println("Please enter your login\nor type exit");
				String login = br.readLine(); // input()
				if (login.equalsIgnoreCase("exit"))
					break;
				System.out.println("Please enter your password");
				String password = br.readLine(); // input()

				try {
					User user = Intranet.INSTANCE.logIn(login, password);
					user.mainMenu(br);
				} catch (NoSuchElementException e) {
					 e.printStackTrace();
					System.err.println("Wrong credentials, please try again");
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Intranet.INSTANCE.serializeSystem();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("______              _ \r\n"
				+ "| ___ \\            | |\r\n"
				+ "| |_/ /_   _  ___  | |\r\n"
				+ "| ___ \\ | | |/ _ \\ | |\r\n"
				+ "| |_/ / |_| |  __/ |_|\r\n"
				+ "\\____/ \\__, |\\___| (_)\r\n"
				+ "        __/ |         \r\n"
				+ "       |___/          ");

	}

}
