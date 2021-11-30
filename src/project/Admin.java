package project;

import java.nio.file.*;
import java.io.*;
import java.util.*;
import asciiTable.*;

/**
 */
public class Admin extends User {

  /**
   *
   */
  private static final long serialVersionUID = 164207315673648679L;

  /**
   * @param firstName
   * @param lastName
   * @param middleName
   * @param login
   * @param password
   * @param role
   */
  public Admin(String firstName, String lastName, String middleName, String login, String password, Role role) {
    super(firstName, lastName, middleName, login, password, role);
  }

  private String getLogs() {
	    String result = "";
	    try {
	      result = String.join("\n", Files.readAllLines(Paths.get(Intranet.databasePath+"logs.log")));
	    } catch (Exception e) {}
	    return result;
	  }

  /**
   * @param br
   */
  public void viewUsers(BufferedReader br) {
    Vector<User> users = Intranet.INSTANCE.getAllUsers();
    String[] header = { "#", "ID", "Name", "Login", "Role", "Blocked" };
    while (true) {
      String[][] data = new String[users.size()][6];
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        data[i][0] = "" + (i + 1);
        data[i][1] = user.getId();
        data[i][2] = user.getFullName();
        data[i][3] = user.login;
        data[i][4] = user.getRole().toString();
        data[i][5] = user.isBlocked() ? "Yes" : "No";
      }
      System.out.println(AsciiTable.getTable(header, data));
      try {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Create user");
        System.out.println("2) Change user");
        System.out.println("3) Block/Unblock user");
        System.out.println("4) Sort by name");
        System.out.println("5) Sort by role");
        System.out.println("6) Sort by login");
        System.out.println("7) Reverse list");
        System.out.println("0) Exit");
        String option = br.readLine();
        if (option.equals("1")) {
          System.out.println("Enter user's first name");
          String firstName = br.readLine();
          System.out.println("Enter user's last name");
          String lastName = br.readLine();
          System.out.println("Enter user's middle name or skip this field");
          String middleName = br.readLine();
          System.out.println("Enter user's login");
          String login = br.readLine();
          System.out.println("Enter user's password");
          String password = br.readLine();
          String roleInput = "";
          Role role = null;
          while (true) {
            System.out.println("Choose user's role");
            System.out.println("1) Student");
            System.out.println("2) Teacher");
            System.out.println("3) Manager");
            System.out.println("4) TechSupport");
            System.out.println("5) Admin");
            roleInput = br.readLine();
            if (roleInput.equals("1")) {
              role = Role.STUDENT;
              break;
            }
            if (roleInput.equals("2")) {
              role = Role.TEACHER;
              break;
            }
            if (roleInput.equals("3")) {
              role = Role.MANAGER;
              break;
            }
            if (roleInput.equals("4")) {
              role = Role.TECH_SUPPORT;
              break;
            }
            if (roleInput.equals("5")) {
              role = Role.ADMIN;
              break;
            }
          }
          User user = UserFactory.createUser(firstName, lastName, middleName, login, password, role);
          if (user == null) {
            System.err.println("Some of the user's already has such login, canceling...");
            continue;
          }
          users.add(user);
          Intranet.INSTANCE.addLog("USER CREATE", this, "USER ID: " + user.getId() + " LOGIN: " + user.login + " USER ROLE: " + user.getRole().toString());
          if (role.equals(Role.STUDENT)) {
            Student student = (Student) user;
            String degreeInput = "";
            Degree degree = null;
            while (true) {
              System.out.println("Choose student's degree");
              System.out.println("1) UNDERGRADUATE");
              System.out.println("2) MASTER");
              System.out.println("3) PHD");
              degreeInput = br.readLine();
              if (degreeInput.equals("1")) {
                degree = Degree.UNDERGRADUATE;
                break;
              }
              if (degreeInput.equals("2")) {
                degree = Degree.MASTER;
                break;
              }
              if (degreeInput.equals("3")) {
                degree = Degree.PHD;
                break;
              }
            }
            Faculty faculty = null;

            while (true) {
              try {
                System.out.println("Enter one of the faculty FIT, MCM, BS, ISE, KMA, FEOGI, SCE");
                faculty = Faculty.valueOf(br.readLine().trim().toUpperCase());
                break;
              } catch (Exception e) {
                System.err.println("Specify one of the faculties, that listed here");
              }
            }
            student.setDegree(degree);
            student.setFaculty(faculty);
          }
          if (role.equals(Role.TEACHER)) {
            Teacher teacher = (Teacher) user;
            String qualificationInput = "";
            Qualification qualification = null;
            while (true) {
              System.out.println("Choose teacher's qualification");
              System.out.println("1) LECTOR");
              System.out.println("2) SENIOR_LECTOR");
              System.out.println("3) PROFESSOR");
              qualificationInput = br.readLine();
              if (qualificationInput.equals("1")) {
                qualification = Qualification.LECTOR;
                break;
              }
              if (qualificationInput.equals("2")) {
                qualification = Qualification.SENIOR_LECTOR;
                break;
              }
              if (qualificationInput.equals("3")) {
                qualification = Qualification.PROFESSOR;
                break;
              }
            }

            teacher.setQualification(qualification);
          }
          Intranet.INSTANCE.serializeSystem();

        }
        if (option.equals("2")) {
        	System.out.println("Please enter the # of user");
        	int userIndex = Integer.parseInt(br.readLine()) - 1;
        	User user = users.get(userIndex);
        	while (true) {
            if (user.getRole().equals(Role.STUDENT)) {
              System.out.println(((Student) user).toString());
            } else if (user.getRole().equals(Role.TEACHER)) {
              System.out.println(((Teacher) user).toString());
            } else
              System.out.println(user.toString());
            System.out.println("Please choose one of the options below:");
            System.out.println("1) Change name");
            System.out.println("2) Change login");
            System.out.println("3) Change password");
            if (user.getRole().equals(Role.STUDENT)) {
              System.out.println("4) Change faculty");
              System.out.println("5) Change degree");
            } else if (user.getRole().equals(Role.TEACHER)) {
              System.out.println("4) Change qualification");
            }
            System.out.println("0) Back");
            String input = br.readLine();
            if (input.equals("0"))
              break;
            if (input.equals("1")) {
              System.out.println("Enter the first name");
              String firstName = br.readLine().trim();
              System.out.println("Enter the last name");
              String lastName = br.readLine().trim();
              System.out.println("Enter the middle name or just skip this field");
              String middleName = br.readLine();
              user.firstName = firstName;
              user.lastName = lastName;
              user.middleName = middleName;
              System.out.println("Successfully changed");
              Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
              continue;
            }
            if (input.equals("2")) {
              System.out.println("Enter new login");
              user.login = br.readLine().trim();
              System.out.println("Successfully changed");
              Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
            }
            if (input.equals("3")) {
              System.out.println("Enter new password");
              user.password = br.readLine().trim().hashCode();
              System.out.println("Successfully changed");
              Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
            }
            if (user.getRole().equals(Role.STUDENT)) {
              if (input.equals("4")) {
                Faculty faculty = null;
                while (true) {
                  try {
                    System.out.println("Enter one of the faculty FIT, MCM, BS, ISE, KMA, FEOGI, SCE");
                    faculty = Faculty.valueOf(br.readLine().trim().toUpperCase());
                    ((Student) user).setFaculty(faculty);
                    break;
                  } catch (Exception e) {
                    System.err.println("Specify one of the faculties, that listed here");
                  }
                }
                System.out.println("Successfully changed");
                Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
                continue;
              }
              if (input.equals("5")) {
                Student student = (Student) user;
                String degreeInput = "";
                Degree degree = null;
                while (true) {
                  System.out.println("Choose student's degree");
                  System.out.println("1) UNDERGRADUATE");
                  System.out.println("2) MASTER");
                  System.out.println("3) PHD");
                  degreeInput = br.readLine();
                  if (degreeInput.equals("1")) {
                    degree = Degree.UNDERGRADUATE;
                    break;
                  }
                  if (degreeInput.equals("2")) {
                    degree = Degree.MASTER;
                    break;
                  }
                  if (degreeInput.equals("3")) {
                    degree = Degree.PHD;
                    break;
                  }
                }
                student.setDegree(degree);
                System.out.println("Successfully changed");
                Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
              }
            }
            if (user.getRole().equals(Role.TEACHER)) {
              if (input.equals("4")) {
                Teacher teacher = (Teacher) user;
                String qualificationInput = "";
                Qualification qualification = null;
                while (true) {
                  System.out.println("Choose teacher's qualification");
                  System.out.println("1) LECTOR");
                  System.out.println("2) SENIOR_LECTOR");
                  System.out.println("3) PROFESSOR");
                  qualificationInput = br.readLine();
                  if (qualificationInput.equals("1")) {
                    qualification = Qualification.LECTOR;
                    break;
                  }
                  if (qualificationInput.equals("2")) {
                    qualification = Qualification.SENIOR_LECTOR;
                    break;
                  }
                  if (qualificationInput.equals("3")) {
                    qualification = Qualification.PROFESSOR;
                    break;
                  }
                }
                teacher.setQualification(qualification);
                System.out.println("Successfully changed");
                Intranet.INSTANCE.addLog("USER CHANGE", this, "JUST CHANGED USER WITH ID " + user.getId());
              }

            }
          }
        }
        if (option.equals("3")) {
          System.out.println("Enter user's #");
          int userIndex = Integer.parseInt(br.readLine()) - 1;
          User user = users.get(userIndex);
          if (user.isBlocked()) {
            user.unBlock();
            Intranet.INSTANCE.addLog("USER UNBLOCK", this, "JUST UNBLOCKED USER WITH ID " + user.getId());
          } else {
            user.block();
            Intranet.INSTANCE.addLog("USER BLOCK", this, "JUST BLOCKED USER WITH ID " + user.getId());
          }
          System.out.println("Successfully changed");
        }
        if (option.equals("4")) {
          Collections.sort(users, (o1, o2) -> o1.getFullName().compareTo(o2.getFullName()));
        }
        if (option.equals("5")) {
          Collections.sort(users, User::compareByRole);
        }
        if (option.equals("6")) {
          Collections.sort(users, (o1, o2) -> o1.login.compareTo(o2.login));
        }
        if (option.equals("7")) {
          Collections.reverse(users);
        }
        if (option.equals("0") || option.equalsIgnoreCase("exit")) {
          break;
        }

      } catch (IOException e) {
        System.err.println("Please enter the number!");
        continue;
      } catch (NumberFormatException e) {
        System.err.println("Please enter the NUMBER!");
        continue;
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("Please enter CORRECT number!");
        continue;
      }
    }
  }

  @Override
  public void mainMenu(BufferedReader br) {
    try {
      while (true) {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Send mail"); // +
        System.out.println("2) Edit users");
        System.out.println("3) Check logs"); // +
        System.out.println("0) Exit"); // +

        String option = br.readLine();
        if (option.equals("1")) {
          super.writeMailMenu(br);
        }
        if (option.equals("2")) {
          this.viewUsers(br);
        }
        if (option.equals("3")) {
          System.out.println(this.getLogs());
          br.readLine();
        }
        if (option.equals("0") || option.equalsIgnoreCase("exit")) {
          break;
        }

      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
