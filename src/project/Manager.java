package project;

import java.io.*;
import java.util.*;

import asciiTable.*;

/**
 */
public class Manager extends User {

  /**
   *
   */
  private static final long serialVersionUID = 3719209285051635074L;

  /**
   * @param firstName
   * @param lastName
   * @param middleName
   * @param login
   * @param password
   * @param role
   */
  Manager(String firstName, String lastName, String middleName, String login, String password, Role role) {
    super(firstName, lastName, middleName, login, password, role);
  }

  public Vector<Teacher> getTeachers() {
    return this.getTeachers((o1, o2) -> o1.getId().compareTo(o2.getId()));
  }

  public Vector<Student> getStudents() {
    return this.getStudents((o1, o2) -> o1.getId().compareTo(o2.getId()));
  }

  /**
   * @param comparator
   * @return
   */
  public Vector<Teacher> getTeachers(Comparator<Teacher> comparator) {
    Vector<Teacher> result = new Vector<Teacher>();
    for (User u : Intranet.INSTANCE.getAllUsers()) {
      if (u.role.equals(Role.TEACHER) && !u.isBlocked()) result.add((Teacher) u);
    }
    Collections.sort(result, comparator);
    return result;
  }

  /**
   * @param comparator
   * @return
   */
  public Vector<Student> getStudents(Comparator<Student> comparator) {
    Vector<Student> result = new Vector<Student>();
    for (User u : Intranet.INSTANCE.getAllUsers()) {
      if (u.role.equals(Role.STUDENT) && !u.isBlocked()) result.add((Student) u);
    }
    Collections.sort(result, comparator);
    return result;
  }

  /**
   * @return
   */
  public Vector<Order> getManagersOrders() {
    return Intranet.INSTANCE.getOROrders();
  }

  public void viewDisciplines(BufferedReader br) {
    while (true) {
      Vector<Discipline> disciplines = Intranet.INSTANCE.getDisciplines();
      String header[] = {"#", "Code", "Name", "Faculties"};
      String data[][] = new String[disciplines.size()][4];
      for (int i = 0; i < disciplines.size(); i++) {
        Discipline curDiscipline = disciplines.get(i);
        data[i][0] = "" + (i+1) + (curDiscipline.isOpenedForRegistration() ? "*":"");
        data[i][1] = curDiscipline.getCode();
        data[i][2] = curDiscipline.getTitle();
        data[i][3] = curDiscipline.facultiesToString();
      }
      System.out.println(AsciiTable.getTable(header, data));

      System.out.println("Please select one of the option");
      System.out.println("1) Add discipline");
      System.out.println("2) Open/close discipline for registration");
      System.out.println("3) Delete discipline");
      System.out.println("0) Back");

			// System.out.println("Please enter number of discipline");
			
			try {
        String input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
          break;
        
        if (input.equals("1")) {
          System.out.println("Enter code of the discipline");
          String code = br.readLine().toUpperCase();
          System.out.println("Enter Name of the discipline");
          String title = br.readLine();
          System.out.println("Enter the description");
          String description = br.readLine();
          System.out.println("Enter the number of credits");
          int credits = Integer.parseInt(br.readLine());
          System.out.println("Enter the prerequisites codes separated by a space");
          String[] prerequisitesInput = br.readLine().toUpperCase().trim().split(" ");
          Vector<String> filteredDisciplines = new Vector<String>();
          // Check if manager try add discipline with prerequisite, which doesn't exists
          // Just delete this values
          for (String p : prerequisitesInput) {
            for (Discipline d : disciplines) {
              if (d.getCode().equals(p)) {
                filteredDisciplines.add(p);
                break;
              }
            }
          }

          System.out.println("Enter the allowed faculties separated by a space");
          String[] facultiesInput = br.readLine().toUpperCase().trim().split(" ");
          Vector<Faculty> filteredFaculties = new Vector<Faculty>();
          Vector<String> facultyStrings = new Vector<String>();

          // Check if manager try add faculty which doesn't exists
          // Just delete this values
          for (String fInput : facultiesInput) {
            try {
              filteredFaculties.add(Faculty.valueOf(fInput));
              facultyStrings.add(fInput);
            } catch (IllegalArgumentException e) {
              continue;
            }
          }
          
          while (true) {
            System.out.println("Add this discipline?");
            System.out.println("Code: " + code);
            System.out.println("Name: " + title);
            System.out.println("Description: " + description);
            System.out.println("Credits: " + credits);
            System.out.println("Prerequisites: [" + String.join(", ", filteredDisciplines) +"]");
            System.out.println("Allowed faculties: [" + String.join(", ", facultyStrings) +"]");
            System.out.println("1) Yes");
            System.out.println("2) No");
            String option = br.readLine();
            if (option.equals("1")) {
              String[] newDisciplines = filteredDisciplines.toArray(new String[filteredDisciplines.size()]);
              Faculty[] newFaculties = filteredFaculties.toArray(new Faculty[filteredFaculties.size()]);
              Discipline newDiscipline = new Discipline(code, title, description, credits, newDisciplines, newFaculties);
              newDiscipline.setOpenedForRegistration(true);
              Intranet.INSTANCE.getDisciplines().add(newDiscipline);
              Intranet.INSTANCE.addLog("CREATE DISCIPLINE", this, "DISCIPLINE " + newDiscipline.getCode() +" " + newDiscipline.getTitle());
              break;
            }
            if (option.equals("2")) {
              break;
            }
          }

        } 
        if (input.equals("2")) {
          System.out.println("Please enter the discipline's #");
          int disciplineIndex = Integer.parseInt(br.readLine())-1;
          Discipline chosenDiscipline = disciplines.get(disciplineIndex);
          chosenDiscipline.setOpenedForRegistration(!chosenDiscipline.isOpenedForRegistration());
          Intranet.INSTANCE.addLog("OPENED/CLOSED DISCIPLINE", this, "DISCIPLINE " + chosenDiscipline.getCode() + " "+ chosenDiscipline.getTitle());
          System.out.println("Successfully changed!");
        }
        if (input.equals("3")) {
          System.out.println("Please enter the discipline's #");
          int disciplineIndex = Integer.parseInt(br.readLine())-1;
          Discipline chosenDiscipline = disciplines.get(disciplineIndex);
          Intranet.INSTANCE.addLog("DELETE DISCIPLINE", this, "DISCIPLINE " + chosenDiscipline.getCode() + " "+ chosenDiscipline.getTitle());
          disciplines.remove(disciplineIndex);
          System.out.println("Successfully deleted");
                  }
        if (input.equals("0")) {
          break;
        }
			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Discipline not found");
				continue;
			}

		}

  }

  public void viewCourses(BufferedReader br) {
    while (true) {
      Vector<Course> courses = Intranet.INSTANCE.getCourses();
      String header[] = {"#", "Code", "Name", "Teacher", "Students count", "Faculties", "Need schedule"};
      String data[][] = new String[courses.size()][7];
      for (int i = 0; i < courses.size(); i++) {
        Course curCourse = courses.get(i);
        data[i][0] = "" + (i+1) + (curCourse.isEnded() ? "":"*");
        data[i][1] = curCourse.getCode();
        data[i][2] = curCourse.getTitle();
        data[i][3] = curCourse.getTeacher().getFullName();
        data[i][4] = "" + curCourse.getStudentsCount() + "/" + curCourse.getCapacity();
        data[i][5] = curCourse.facultiesToString();
        data[i][6] = curCourse.isNeedSchedule() ? "Yes" : "No";
      }
      System.out.println(AsciiTable.getTable(header, data));
      System.out.println("Please select one of the option");
      System.out.println("1) Edit course");
      System.out.println("2) End course");
      System.out.println("0) Back");
			
			try {
        String input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
          break;
        
        if (input.equals("1")) {
          System.out.println("Enter number of the course");
          int courseIndex = Integer.parseInt(br.readLine())-1;
          courses.get(courseIndex).viewCourse(this, br);

        } 
        if (input.equals("2")) {
          int courseIndex = Integer.parseInt(br.readLine())-1;
          Course chosenCourse = courses.get(courseIndex);
          chosenCourse.end();
          Intranet.INSTANCE.addLog("END COURSE", this, "COURSE ID" + chosenCourse.getId() + " "+ chosenCourse.getTitle());
        }
			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Discipline not found");
				continue;
			}

		}
  }

  public void viewCourse(Course course, BufferedReader br) {
    if (course.getSchedule().getLessonsCount() == 0) {
      System.out.println("No schedule");
    } else {
      System.out.println(course.getSchedule().toString());
    }
    Vector<Student> students = new Vector<Student>();
		for (String id : course.getStudentsIDs()) {
			students.add((Student) Intranet.INSTANCE.getUserByID(id));
    }
    
		String[] header = {"#", "Student ID", "Name", "Login", "Faculty", "Degree"};
		String[][] data = new String[students.size()][6];
		for (int i = 0; i < students.size(); i++) {
			Student st = students.get(i);
			data[i][0] = "" + (i+1);
			data[i][1] = st.id;
			data[i][2] = st.getFullName();
			data[i][3] = st.login;
			data[i][4] = st.getFaculty().toString();
			data[i][5] = st.getDegree().toString();
    }
    
		System.out.println(AsciiTable.getTable(header, data));
    while (true) {
      System.out.println("Please choose one of the options below:");
      System.out.println("1) Add student to this course");
      System.out.println("2) Remove student from the course");
      System.out.println("3) Edit schedule");
      System.out.println("4) Open course for registration");
      System.out.println("5) Close course for registration");
      System.out.println("0) Back");

      try {
        String input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
          break;
        
        if (input.equals("1")) {
          System.out.println("Enter student's login");
          Student student = (Student) Intranet.INSTANCE.getUserByLogin(br.readLine().trim());
          course.addStudent(student);
          Intranet.INSTANCE.addLog("ADD STUDENT TO COURSE", this, "STUDENT ID"+student.getId()  +" COURSE ID" + course.getId() + " "+ course.getTitle());
          continue;
        } 
        if (input.equals("2")) {
          System.out.println("Enter number of the student");
          int studentIndex = Integer.parseInt(br.readLine())-1;
          course.removeStudent(students.get(studentIndex));
          Intranet.INSTANCE.addLog("ADD STUDENT TO COURSE", this, "STUDENT ID"+students.get(studentIndex).getId()  +" COURSE ID" + course.getId() + " "+ course.getTitle());        }
        if (input.equals("3")) {
          while (true) {
            System.out.println(course.getSchedule().toString());
            System.out.println("1) Add lesson");
            System.out.println("2) Remove lesson");
            System.out.println("0) Back");
            String option = br.readLine();
            if (option.equals("1")) {
              if (course.getSchedule().getLessonsCount() >= course.getCredits()) {
                System.err.println("This course already have " + course.getSchedule().getLessonsCount() + " lessons at week!");
                break;
              }
              System.out.println("Choose day:");
              System.out.println("1) Monday");
              System.out.println("2) Tuesday");
              System.out.println("3) Wednesday");
              System.out.println("4) Thursday");
              System.out.println("5) Friday");
              System.out.println("6) Saturday");
              System.out.println("7) Sunday");
              int dayIndex = Integer.parseInt(br.readLine())-1;
              if (dayIndex < 0 || dayIndex >= Schedule.daysInWeek) {
                System.err.println("Enter correct day");
                continue;
              }

              System.err.println("Enter hours");
              int hoursIndex = Integer.parseInt(br.readLine());
              if (hoursIndex < (Schedule.startFromHour-1) || hoursIndex >= Schedule.endToHour) {
                System.out.println("Wrong hours format");
                continue;
              }
              System.out.println("Please enter lesson room");
              String text = course.getTitle() + " " + course.getTeacher().getFullName() + " " + br.readLine();
              

              //! I TRIED SO HARD AND GOT SO FAR
              Schedule tempSchedule = new Schedule(); 
              tempSchedule.addLesson(dayIndex, hoursIndex, text);

              if (tempSchedule.isIntersect(course.getTeacher().getSchedule())) {
                System.err.println("There is intersection in teacher's schedule, canceling operation...");
                continue;
              }

              boolean isIntersect = false;
              String errorMsg = "This students have intersection in schedule:\n";

              for (Student st : students) {
                if (st.getSchedule().isIntersect(tempSchedule)) {
                  errorMsg += st.id + " | " + st.getFullName() + " | " +  st.login + "\n";
                  isIntersect = true;
                }
              }
              if (isIntersect) {
                System.err.println(errorMsg + "canceling operation...");
                continue;
              } else {
                course.getTeacher().getSchedule().addLesson(dayIndex, hoursIndex, text);
                for (Student st : students) {
                  st.getSchedule().addLesson(dayIndex, hoursIndex, text);
                }
                course.getSchedule().addLesson(dayIndex, hoursIndex, text);
                Intranet.INSTANCE.addLog("EDIT COURSE SCHEDULE", this, "COURSE ID " + course.getId());
                continue;
              }

            }
            if (option.equals("2")) {
              System.out.println("Choose day:");
              System.out.println("1) Monday");
              System.out.println("2) Tuesday");
              System.out.println("3) Wednesday");
              System.out.println("4) Thursday");
              System.out.println("5) Friday");
              System.out.println("6) Saturday");
              System.out.println("7) Sunday");
              int dayIndex = Integer.parseInt(br.readLine())-1;
              if (dayIndex < 0 || dayIndex >= Schedule.daysInWeek) {
                System.err.println("Enter correct day");
                continue;
              }
              System.err.println("Enter hours");
              int hoursIndex = Integer.parseInt(br.readLine());
              if (hoursIndex < (Schedule.startFromHour-1) || hoursIndex >= Schedule.endToHour) {
                System.err.println("Wrong hours format");
                continue;
              }
              course.getSchedule().removeLesson(dayIndex, hoursIndex);
              course.getTeacher().getSchedule().removeLesson(dayIndex, hoursIndex);
              for (Student st : students) {
                st.getSchedule().removeLesson(dayIndex, hoursIndex);
              }
              Intranet.INSTANCE.addLog("EDIT COURSE SCHEDULE", this, "COURSE ID " + course.getId());

            }
            if (option.equals("0")) {
              break;
            }
          }
        }
        if (input.equals("4")) {
          course.setOpenedForRegistration(true);
          System.out.println("Successfully opened course!");
          Intranet.INSTANCE.addLog("OPENED/CLOSED COURSE", this, "COURSE ID " + course.getId());
        }
        if (input.equals("5")) {
          course.setOpenedForRegistration(false);
          System.out.println("Successfully closed course!");
          Intranet.INSTANCE.addLog("OPENED/CLOSED COURSE", this, "COURSE ID " + course.getId());
        }


        if (input.equals("0")) {
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
      } catch (NoSuchElementException e) {
        System.err.println("Can't find this student!");
        continue;
      } catch (ClassCastException e) {
        System.err.println("That user is not student!");
      }
      
    }
  }

  public void viewStudents(BufferedReader br) {
    Vector<Student> students = this.getStudents();
    while (true) {
      String header[] = {"#", "ID", "Login", "Full Name", "GPA", "Year of Study"};
      String data[][] = new String[students.size()][6];
      for (int i = 0; i < students.size(); i++) {
        Student st = students.get(i);
        data[i][0] = "" + (i+1);
        data[i][1] = st.getId();
        data[i][2] = st.login;
        data[i][3] = st.getFullName();
        data[i][4] = "" + st.calculateGPA();
        data[i][5] = "" + st.getYearOfStudy();
      }

      System.out.println(AsciiTable.getTable(header, data));

      try {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Choose student by login");
        System.out.println("2) Choose student by #");
        System.out.println("3) Sort by Student id");
        System.out.println("4) Sort by name");
        System.out.println("5) Sort by GPA");
        System.out.println("6) Sort by year of study");
        System.out.println("7) Reverse students list");
        System.out.println("0) Back");
        String option = br.readLine();
        if (option.equals("1")) {
          Student student = (Student) Intranet.INSTANCE.getUserByLogin(br.readLine());
          System.out.println(student.toString());
          System.out.println(student.getSchedule().toString());
          br.readLine();
        }
        if (option.equals("2")) {
          Student student = students.get(Integer.parseInt(br.readLine()) -1);
          System.out.println(student.toString());
          System.out.println(student.getSchedule().toString());
          br.readLine();
        }
        if (option.equals("3")) {
          Collections.sort(students, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        }
        if (option.equals("4")) {
          Collections.sort(students, (o1, o2) -> o1.getFullName().compareTo(o2.getFullName()));
        }
        if (option.equals("5")) {
          Collections.sort(students, (o1, o2) -> Double.compare(o1.calculateGPA(), o2.calculateGPA()));
        }
        if (option.equals("6")) {
          Collections.sort(students, (o1, o2) -> Integer.compare(o1.getYearOfStudy(), o2.getYearOfStudy()));
        }
        if (option.equals("7")) {
          Collections.reverse(students);
        }
        if (option.equals("0")) {
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
      } catch (NoSuchElementException e) {
        System.err.println("Can't find this student!");
        continue;
      } catch (ClassCastException e) {
        System.err.println("That user is not student!");
      }
    }
  }

  public void viewTeachers(BufferedReader br) {
    Vector<Teacher> teachers = this.getTeachers();
    while (true) {
      String header[] = {"#", "ID", "Login", "Full Name", "Qualification"};
      String data[][] = new String[teachers.size()][5];
      for (int i = 0; i < teachers.size(); i++) {
        Teacher tch = teachers.get(i);
        data[i][0] = "" + (i+1);
        data[i][1] = tch.getId();
        data[i][2] = tch.login;
        data[i][3] = tch.getFullName();
        data[i][4] = tch.getQualification().toString();
      }

      System.out.println(AsciiTable.getTable(header, data));

      try {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Choose teacher by login");
        System.out.println("2) Choose teacher by #");
        System.out.println("3) Sort by teacher id");
        System.out.println("4) Sort by name");
        System.out.println("5) Sort by qualification");
        System.out.println("6) Reverse teachers list");
        System.out.println("0) Back");
        String option = br.readLine();
        if (option.equals("1")) {
          Teacher teacher = (Teacher) Intranet.INSTANCE.getUserByLogin(br.readLine());
          System.out.println(teacher.toString());
          System.out.println(teacher.getSchedule().toString());
          br.readLine();
        }
        if (option.equals("2")) {
          Teacher teacher = teachers.get(Integer.parseInt(br.readLine()) -1);
          System.out.println(teacher.toString());
          System.out.println(teacher.getSchedule().toString());
          br.readLine();
        }
        if (option.equals("3")) {
          Collections.sort(teachers, (o1, o2) -> o1.getId().compareTo(o2.getId()));
        }
        if (option.equals("4")) {
          Collections.sort(teachers, (o1, o2) -> o1.getFullName().compareTo(o2.getFullName()));
        }
        if (option.equals("5")) {
          Collections.sort(teachers, Teacher::compareByQualification);
        }
        if (option.equals("6")) {
          Collections.reverse(teachers);
        }
        if (option.equals("0")) {
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
      } catch (NoSuchElementException e) {
        System.err.println("Can't find this teacher!");
        continue;
      } catch (ClassCastException e) {
        System.err.println("That user is not teacher!");
      }
    }
  }

  /**
   */
  @Override
  public void mainMenu(BufferedReader br) {
    try {
      while (true) {
        System.out.println("Please choose one of the options below:");
        System.out.println("1) Make request for Tech Support"); // +
        System.out.println("2) Send mail"); // +
        System.out.println("3) Edit disciplines"); // +
        System.out.println("4) View courses"); // +
        System.out.println("5) View students"); // +
        System.out.println("6) View teachers"); // +
        System.out.println("7) View OR orders"); // 8
        System.out.println("8) View mailbox"); // +
        System.out.println("0) Exit"); // +

        String option = br.readLine();
        if (option.equals("1")) {
          super.writeOrderMenu(br, OrderType.IT);
        }
        if (option.equals("2")) {
          super.writeMailMenu(br);
        }
        if (option.equals("3")) {
          this.viewDisciplines(br);
        }
        if (option.equals("4")) {
          this.viewCourses(br);
        }
        if (option.equals("5")) {
          this.viewStudents(br);
        }
        if (option.equals("6")) {
          this.viewTeachers(br);
        }
        if (option.equals("7")) {
          Order.viewOrders(br, OrderType.OR, this.getId());
        }
        if (option.equals("8")) {
          super.viewMailBox(br);
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
