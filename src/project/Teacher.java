package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import asciiTable.AsciiTable;

/**
 */
public class Teacher extends User {

	private static final long serialVersionUID = -2531834496876434528L;
	private Qualification qualification;
	private Vector<String> courses;
	private Schedule schedule;
	private static HashMap<Qualification, Integer> qualificationTable = new HashMap<Qualification, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(Qualification.LECTOR, 1);
			put(Qualification.SENIOR_LECTOR, 2);
			put(Qualification.PROFESSOR, 5);
		}
	};
	/**
	 *
	 * @param firstName
	 * @param lastName
	 * @param middleName
	 * @param login
	 * @param password
	 * @param role
	 */
	public Teacher(String firstName, String lastName, String middleName, String login, String password, Role role) {
		super(firstName, lastName, middleName, login, password, role);
		this.courses = new Vector<String>();
		this.schedule = new Schedule();
		this.qualification = Qualification.PROFESSOR;
	}

	/**
	 * to get String representation of the teacher
	 * @return string with information about teacher
	 */
	public String toString() {
		return super.toString()+"\nQualification: " + getQualification().toString();
	}

	/**
	 * to get the id of the courses that the teacher has
	 * @return vector of the id of the courses
	*/
	public Vector<String> getCourses() {
		return this.courses;
	}


	/**
	 * to compare teachers by qualification
	 * @param t1 
	 * @param t2 
	 * @return positive number if greater, negative if less, and 0 if the same
	*/
	public static int compareByQualification(Teacher t1, Teacher t2) {
		return Integer.compare(qualificationTable.get(t1.qualification), qualificationTable.get(t2.qualification));
	}

	/**
	 * to set teacher's qualification
	 * @param qualification 
	*/
	public void setQualification(Qualification qualification) {
		this.qualification = qualification;
	}

	/**
	 * to get teacher's qualification
	 * @return qualification of teacher
	*/
	public Qualification getQualification () {
		return this.qualification;
	}

	/**
	 * to get courses that teacher has, if id of teacher of course is equal to the id of teacher then it will be added to the vector of courses
	 * @return vector of courses of teacher
	*/
	public Vector<Course> getOwnCourses() {
		Vector<Course> result = new Vector<Course>();
		for (Course c : Intranet.INSTANCE.getCourses()) {
			if (c.getTeacher().equals(this) && !c.isEnded())
				result.add(c);
		}
		return result;
	}

	/**
	 * to view teacher's courses, students, student's marks, number of absences 
	 * @param br 
	 * @param course 
	*/
	public void viewCourse(Course course, BufferedReader br) {
		while (true) {
		Vector<Student> students = new Vector<Student>();
		for (String id : course.getStudentsIDs()) {
			students.add((Student) Intranet.INSTANCE.getUserByID(id));
		}
		String[] header = {"#", "Student ID", "Name", "Login", "Points", "Absences count"};
		String[][] data = new String[students.size()][6];
		
		for (int i = 0; i < students.size(); i++) {
			Student st = students.get(i);
			Mark m = course.getStudentMark(st);
			data[i][0] = "" + (i+1);
			data[i][1] = st.id;
			data[i][2] = st.getFullName();
			data[i][3] = st.login;
			data[i][4] = "" + m.getTotalPoints();
			data[i][5] = "" + m.getAbsenceCount();
		}
		System.out.println(AsciiTable.getTable(header, data));
		String option = "";
		
		
		System.out.println("1) Choose student");
		System.out.println("2) Edit Course Files");
		System.out.println("0) Exit");
		try {
			option = br.readLine();
			if (option.equalsIgnoreCase("exit") || option.equals("0"))
				break;
			
			if (option.equals("1")) {
				this.chooseStudent(course, br, students);
			}
			
			if (option.equals("2")) {
				this.editCourseFiles(course, br);
			}
			
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		}
	}
		
	/**
	 * let to teacher put student's marks and absences
	 * @param course 
	*/
	public void chooseStudent(Course course, BufferedReader br, Vector<Student> students) {
		while (true) {
			String[] header = {"#", "Student ID", "Name", "Login", "Points", "Absences count"};
			String[][] data = new String[students.size()][6];

			for (int i = 0; i < students.size(); i++) {
				Student st = students.get(i);
				Mark m = course.getStudentMark(st);
				data[i][0] = "" + (i+1);
				data[i][1] = st.id;
				data[i][2] = st.getFullName();
				data[i][3] = st.login;
				data[i][4] = "" + m.getTotalPoints();
				data[i][5] = "" + m.getAbsenceCount();
			}
			System.out.println(AsciiTable.getTable(header, data));
			String input = "";
			String option = "";
			Double points = 0.0;
			int studentNum = 0;

			System.out.println("Enter the number of student");
			System.out.println("type 0 or exit to back");
			try {
				input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
					break;
				
				studentNum = Integer.parseInt(input) - 1;
				Student student = students.get(studentNum);
				Mark mark = course.getStudentMark(student);
				System.out.println("1) Put points into 1-st attestation");
				System.out.println("2) Put points into 2-st attestation");
				System.out.println("3) Put final points");
				System.out.println("4) Put absence");
				System.out.println("0) Back");
				option = br.readLine();
				if (option.equals("1")) {
					System.out.println("Enter points");
					mark.getFirstAtt().add(Double.parseDouble(br.readLine()));
					Intranet.INSTANCE.addLog("PUT MARK", this, "COURSE ID " + course.getId() + "; STUDENT ID: " + student.getId());
				}
				if (option.equals("2")) {
					System.out.println("Enter points");
					mark.getSecondAtt().add(Double.parseDouble(br.readLine()));
					Intranet.INSTANCE.addLog("PUT MARK", this, "COURSE ID " + course.getId() + "; STUDENT ID: " + student.getId());
				}
				if (option.equals("3")) {
					System.out.println("Enter points");
					points = Double.parseDouble(br.readLine());
					if (points < 0) {
						System.err.println("Points for final can't be negative");
					} else {
						mark.putFinal(points);
						Intranet.INSTANCE.addLog("PUT FINAL", this, "COURSE ID " + course.getId() + "; STUDENT ID: " + student.getId());
					}
				}
				if (option.equals("4")) {
					mark.putAbsence();
					Intranet.INSTANCE.addLog("PUT ABSENCE", this, "COURSE ID " + course.getId() + "; STUDENT ID: " + student.getId());
				}
				if (option.equals("0")) {
					break;
				}
				
				
			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Please enter the CORRECT number!");
				continue;
			}
		}

	}
	
	/**
	 * let to teacher to add or remove course files
	 * @param br 
	 * @param course 
	*/
	public void editCourseFiles(Course course, BufferedReader br) {
		while (true) {
			String[] header = {"#", "Title", "Link to download"};
			String[][] data = new String[course.getCourseFiles().size()][3];
			for (int i = 0; i < course.getCourseFiles().size(); i++) {
				CourseFile courseFile = course.getCourseFiles().get(i);
				data[i][0] = "" + (i + 1);
				data[i][1] = courseFile.getFileTitle();
				data[i][2] = courseFile.getLink();
			}
			System.out.println(AsciiTable.getTable(header, data));
			
			String input = "";
			String option = "";
			try {
				if (option.equalsIgnoreCase("exit") || option.equals("0"))
					break;

				System.out.println("Please choose one of the options below:");
				System.out.println("1) Add new course file");
				System.out.println("2) Delete existent course file");
				System.out.println("0) Back");
				option = br.readLine();
				if (option.equals("1")) {
					System.out.println("Please enter the title");
					String courseTitle = br.readLine();
					if (courseTitle.equals("")) {
						System.err.println("You need to specify title");
						continue;
					}
					System.out.println("Please enter link to resource");
					String courseDownloadLink = br.readLine();
					if (!courseDownloadLink.matches("https?://.+\\..+")) {
						System.err.println("You need to specify correct link to resource");
						continue;
					}
					course.addFile(courseTitle, courseDownloadLink);
					Intranet.INSTANCE.addLog("COURSE ADD FILE", this, "COURSE ID " + course.getId());
				}
				if (option.equals("2")) {
					input = br.readLine();
					int fileId = Integer.parseInt(input) - 1;
					course.getCourseFiles().remove(fileId);
					Intranet.INSTANCE.addLog("COURSE REMOVE FILE", this, "COURSE ID " + course.getId());
				}
				if (option.equals("0")) {
					break;
				}
				
				
			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Please enter the CORRECT number!");
				continue;
			}
		}

	}

	/**
	 * let to add new courses to teacher
	 * @param discipline 
	*/
	public void createCourse(Discipline discipline) {
	}

	/**
	 * let to get the schedule of teacher
	 * @return teacher's schedule
	*/
	public Schedule getSchedule() {
		return this.schedule;
	}

	/**
	 * let to set teacher's schedule
	 * @param schedule 
	*/
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * show schedule of teacher
	 * @param br 
	*/
	public void viewSchedule(BufferedReader br) {
		System.out.println(this.schedule.toString());
		try {
			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * view courses that teacher has(id, name, student's count, files of courses)
	 * @param br 
	*/
	public void viewCourses(BufferedReader br) {
		while (true) {

			Vector<Course> data = this.getOwnCourses();
			String[] header = { "#", "Course ID", "Code", "Name", "Students count", "Course Files Count" };
			String[][] information = new String[data.size()][6];
			
			for (int i = 0; i < courses.size(); i++) {
				Course course = data.get(i);
				information[i][0] = "" + (i + 1);
				information[i][1] = course.getCourseId();
				information[i][2] = course.getCode();
				information[i][3] = course.getTitle();
				information[i][4] = "" + course.getStudentsCount() + "/" + course.getCapacity();
				information[i][5] = "" + course.getCourseFiles().size();
			}
			System.out.println(AsciiTable.getTable(header, information));

			System.out.println("Please enter number of course");
			System.out.println("type 0 or exit to back");
			String input = "";
			int courseNum = -1;
			try {
				input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
					break;
				courseNum = Integer.parseInt(input)-1;
				Course course = data.get(courseNum);
				course.viewCourse(this, br);
			} catch (IOException e) {
				System.err.println("Please enter the number of course!");
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the NUMBER of course!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Please enter the CORRECT number of course");
				continue;
			}
			
		}

	}

	/**
	 * to view teacher's disciplines and to add to the next semester disciplines
	 * @param br 
	*/
	public void viewDisciplines(BufferedReader br) {
		Vector<Discipline> disciplines = new Vector<Discipline>();
		for (Discipline d : Intranet.INSTANCE.getDisciplines()) {
			if (d.isOpenedForRegistration()) disciplines.add(d);
		}
		String[] header = {"#", "Code", "Title", "Credits", "Allowed Faculties"};
		try {
			while (true) {
				String[][] data = new String[disciplines.size()][5];
				for (int i = 0; i < disciplines.size(); i++) {
					Discipline curDiscipline = disciplines.get(i);
					data[i][0] = "" + (i + 1);
					data[i][1] = curDiscipline.getCode();
					data[i][2] = curDiscipline.getTitle();
					data[i][3] = "" + curDiscipline.getCredits();
					data[i][4] = curDiscipline.facultiesToString();
				}
				System.out.println(AsciiTable.getTable(header, data));

				System.out.println("Please choose # of the course, which you want to add");
				System.out.println("or '0' to exit");
				
				try {
					String input = br.readLine();
					if (input.equals("0") || input.equals("exit")) break;
					int disciplineIndex = Integer.parseInt(input) - 1;
					Discipline discipline = disciplines.get(disciplineIndex);
					System.out.println("Enter capacity of student for this course");
					int capacity = Integer.parseInt(br.readLine());
					if (capacity <= 0) {
						System.err.println("Capacity must be positive...");
						continue;
					}
					Course course = new Course(discipline.getCode(), discipline.getTitle(), discipline.getDescription(),
							discipline.getPrerequisites(), discipline.getCredits(), capacity, this.id,
							discipline.getAllowedFaculties());
					Intranet.INSTANCE.getCourses().add(course);
					this.courses.add(course.getId());
					Intranet.INSTANCE.addLog("ADD COURSE", this, "COURSE ID " + course.getId());
				} catch (NumberFormatException e) {
					System.err.println("Please enter the NUMBER!");
					continue;
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println("You haven't any courses on this semester");
					continue;
				}


			}
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	@Override
	/**
	 * shows to teacher options that teacher can do
	 * @param br 
	*/
	public void mainMenu(BufferedReader br) {
		try {
			while (true) {
				System.out.println("Please choose one of the options below:");
				System.out.println("1) Make request to Office Register"); // +
				System.out.println("2) Make request for Tech Support"); // +
				System.out.println("3) Send mail"); // +
				System.out.println("4) Add course"); // +
				System.out.println("5) View my courses"); // +
				System.out.println("6) View schedule"); // +
				System.out.println("7) View mailbox"); // +
				System.out.println("0) Exit"); // +

				String option = br.readLine();
				if (option.equals("1")) {
					super.writeOrderMenu(br, OrderType.OR);
				}
				if (option.equals("2")) {
					super.writeOrderMenu(br, OrderType.IT);
				}
				if (option.equals("3")) {
					super.writeMailMenu(br);
				}
				if (option.equals("6")) {
					this.viewSchedule(br);
				}
				if (option.equals("5")) {
					this.viewCourses(br);
				}
				if (option.equals("4")) {
					this.viewDisciplines(br);
				}
				if (option.equals("7")) {
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
