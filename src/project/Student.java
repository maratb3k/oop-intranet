package project;

import java.io.*;
import java.util.*;

import asciiTable.AsciiTable;

/**
 */
public class Student extends User {

	/**
	 *
	 */
	private static final long serialVersionUID = 6291512968016553475L;
	private Faculty faculty;
	private int yearOfStudy;
	private HashMap<String, Integer> courses;
	private int currSemester;
	private Schedule schedule;
	private Degree degree;

	/**
	 * 
	 * @param firstName
	 * @param lastName
	 * @param middleName
	 * @param login
	 * @param password
	 * @param role
	 */
	public Student(String firstName, String lastName, String middleName, String login, String password, Role role) {
		super(firstName, lastName, middleName, login, password, role);
		this.schedule = new Schedule();
		this.currSemester = 1;
		this.courses = new HashMap<String, Integer>();
		this.faculty = Faculty.FIT;
		this.degree = Degree.UNDERGRADUATE;
	}

	/**
     * to show the year of study of student
     * @return year of study of student
     */
	public int getYearOfStudy() {
		return this.yearOfStudy;
	}

	/**
     * Add year of study of student
     */
	public void addYearOfStudy() {
		this.yearOfStudy++;
	}

	/**
     * to get an information about student
     * @return information about student
     */
	public String toString() {
		return super.toString() + "\nGPA: " + calculateGPA() + "\nDegree: " + degree + "\nYear of study: " + yearOfStudy
				+ "\nFaculty: " + faculty.toString();
	}

	/**
     * to get number of current semester
     * @return current semester
     */
	public int getCurrentSemester() {
		return this.currSemester;
	}

	/**
     * Add semester of study of student
     */
	public void addCurrentSemester() {
		this.currSemester++;
	}

	/**
     * to get an information about student's degree
     * @return degree of student
     */
	public Degree getDegree() {
		return this.degree;
	}

	/**
     * to change degree of student
     * @param degree 
     */
	public void setDegree(Degree degree) {
		this.degree = degree;
	}

	@Override
	/**
     * to get the hash code of the object
     * @return the hash code of the object
     */
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(courses, currSemester, degree, faculty, yearOfStudy);
		return result;
	}

	@Override
	/**
     * check if the objects is the same type
     * @param obj 
     * @return true if the object is not null and is an object of the same type, false if not
     */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Student)) {
			return false;
		}
		Student other = (Student) obj;
		return Objects.equals(courses, other.courses) && currSemester == other.currSemester && degree == other.degree
				&& faculty == other.faculty && yearOfStudy == other.yearOfStudy;
	}

	/**
     * to calculate GPA of student
     * @return GPA of student
     */
	public double calculateGPA() {
		double result = 0.0;
		int credits = 0;
		for (String id : courses.keySet()) {
			Course course = Intranet.INSTANCE.getCourseByID(id);
			Mark mark = course.getStudentMark(this);
			if (mark.isFinalHeld()) {
				try {
					result += mark.getGPA() * course.getCredits();
					credits += course.getCredits();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (credits == 0) return 0;
		return result / credits;
	}

	/**
     * to get student's courses and number of semester in which courses were taken
     * @return name of courses and points for the courses
     */
	public HashMap<String, Integer> getCourses() {
		return this.courses;
	}

	/**
     * to get a faculty of student
     * @return faculty of student
     */
	public Faculty getFaculty() {
		return this.faculty;
	}

	/**
     * to change faculty of student
     * @param faculty 
     */
	public void setFaculty(Faculty faculty) {
		this.faculty = faculty;
	}

    /**
     * to view list of disciplines passed for the corresponding period of study, indicating credits and grades
     * @param br 
     */
	public void viewTranscript(BufferedReader br) {
		if (this.courses.size() == 0) { 
		      System.out.println("You have no courses");
		      return;
		    }
		Vector<Vector<Course>> data = new Vector<Vector<Course>>();
		for (int i = 0; i < currSemester; i++) data.add(new Vector<Course>());
		for (String courseId : this.courses.keySet()) {
			Course course = Intranet.INSTANCE.getCourseByID(courseId);
			data.get(this.courses.get(courseId) - 1).add(course);
		}

		for (Vector<Course> v : data) {
			if (v.size() > 0) {
				int totalCredits = 0;
				double totalGPA = 0.0;
				int semester = this.courses.get(v.get(0).getCourseId());
				System.out.println("Semester #" + semester);

				String[] header = { "Code", "Name", "Credits", "Total Points", "Grade", "GPA" };
				String[][] information = new String[v.size()][6];

				for (int i = 0; i < v.size(); i++) {
					information[i][0] = v.get(i).getCode();
					information[i][1] = v.get(i).getTitle();
					information[i][2] = "" + v.get(i).getCredits();
					Mark mark = v.get(i).getStudentMark(this.id);
					information[i][3] = "" + mark.getTotalPoints();

					totalCredits += v.get(i).getCredits();

					try {
						totalGPA += mark.getGPA() * v.get(i).getCredits();
						information[i][4] = mark.isFinalHeld() ? mark.getGrade() : "x";
						information[i][5] = mark.isFinalHeld() ? "" + mark.getGPA() : "x";
					} catch (IOException e) {
						// e.printStackTrace();
					}

				}

				System.out.println(AsciiTable.getTable(header, information));
				System.out.printf("Total credits: " + totalCredits + ". GPA: %.2f\n\n", (double) totalGPA / totalCredits);
			}
		}
		try {
			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * to show the name of courses that the student is studying in the current semester
     * @param br 
     */
	public void viewCourses(BufferedReader br) {
		if (this.courses.size() == 0) { 
		      System.out.println("You have no courses");
		      return;
		    }
		while (true) {
			Vector<Vector<Course>> data = new Vector<Vector<Course>>();
			for (int i = 0; i < currSemester; i++) data.add(new Vector<Course>());
			for (String courseId : this.courses.keySet()) {
				Course course = Intranet.INSTANCE.getCourseByID(courseId);
				data.get(this.courses.get(courseId) - 1).add(course);
			}

			for (Vector<Course> v : data) {
				if (v.size() > 0) {
					int semester = this.courses.get(v.get(0).getCourseId());
					System.out.println("Semester #" + semester);

					String[] header = { "#", "Code", "Name", "Teacher", "Course Files Count" };
					String[][] information = new String[v.size()][5];

					for (int i = 0; i < v.size(); i++) {
						information[i][0] = "" + (i + 1);
						information[i][1] = v.get(i).getCode();
						information[i][2] = "" + v.get(i).getTitle();
						Teacher teacher = v.get(i).getTeacher();
						information[i][3] = "" + teacher.getFullName();
						information[i][4] = "" + v.get(i).getCourseFiles().size();
					}
					System.out.println(AsciiTable.getTable(header, information));
				}
			}

			System.out.println("Please enter number of semester");
			System.out.println("type 0 or exit to back");
			int semesterInput = 0;
			int courseInput = 0;
			try {
				String input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
					break;
				semesterInput = Integer.parseInt(input) - 1;
				data.get(semesterInput);

				System.out.println("Please enter number of course");
				input = br.readLine();
				if (input.equalsIgnoreCase("exit") || input.equals("0"))
					break;
				courseInput = Integer.parseInt(input) - 1;
				Course selectedCourse = data.get(semesterInput).get(courseInput);
				selectedCourse.viewCourse(this, br);
			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("You haven't any courses on this semester");
				continue;
			}

		}

	}

	/**
     * to show student's marks
     * @param br 
     * @param c 
     */
	public void viewCourseMarks(Course c, BufferedReader br) {
		Mark mark = c.getStudentMark(this.id);
		String[][] data = new String[4][2];
		data[0][0] = "Attestation 1";
		data[1][0] = "Attestation 2";
		data[2][0] = "Final";
		data[2][1] = "" + mark.getFinal();
		data[3][0] = "Absences count";
		data[3][1] = "" + mark.getAbsenceCount();
		String att1 = "" + mark.getSumFirstAtt() + " [ ";
		String att2 = "" + mark.getSumSecondAtt() + " [ ";
		for (double p : mark.getFirstAtt()) {
			att1 += p + ", ";
		}
		for (double p : mark.getSecondAtt()) {
			att2 += p + ", ";
		}
		att1 += "]";
		att2 += "]";
		att1 = att1.replace(", ]", " ]");
		att2 = att2.replace(", ]", " ]");
		data[0][1] = att1;
		data[1][1] = att2;
		System.out.println(AsciiTable.getTable(data));
		System.out.println("Total: " + mark.getTotalPoints());
		if (mark.isFinalHeld()) {
			try {
				System.out.println("GPA: " + mark.getGPA());
				System.out.println("Grade: " + mark.getGrade());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
     * to show student's schedule of courses
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
     * to get schedule of student
     * @return Schedule
     */
	public Schedule getSchedule() {
		return this.schedule;
	}

	/**
     * to set the schedule of student
     * @param schedule 
     */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

    /**
     * to register a student for courses that he will study
     * @param br 
     */
	public void registerToCourses(BufferedReader br) {
		while (true) {
			Vector<Course> courses = new Vector<Course>();
			// Filter only opened for registration courses
			for (Course c : Intranet.INSTANCE.getCourses()) {
				if (c.isOpenedForRegistration() && !c.isEnded())
					courses.add(c);
			}
			String[] header = { "#", "Code", "Title", "Credits", 
			"Teacher", "Faculties", "Prerequisites", "Students", "Can Join" };

			String[][] data = new String[courses.size()][9];
			for (int i = 0; i < courses.size(); i++) {
				Course c = courses.get(i);
				data[i][0] = "" + (i + 1);
				data[i][1] = c.getCode();
				data[i][2] = c.getTitle();
				data[i][3] = "" + c.getCredits();
				data[i][4] = c.getTeacher().getFullName();
				data[i][5] = c.facultiesToString();
				data[i][6] =  "[" + String.join(", ", c.getPrerequisites()) + "]";
				data[i][7] = "" + c.getStudentsCount() + "/" + c.getCapacity();
				data[i][8] = c.canJoin(this) ? "Yes" : "No";
			}
			System.out.println(AsciiTable.getTable(header, data));
			System.out.println("Please enter the # of course, that you want to join");
			System.out.println("or '0' to exit");
			try {
				String input = br.readLine();
				if (input.equals("0") || input.equalsIgnoreCase("exit")) {
					break;
				}
				int courseIndex = Integer.parseInt(input) -1;
				Course course = courses.get(courseIndex);
				if (course.canJoin(this)) {
					course.addStudent(this);
					Intranet.INSTANCE.addLog("STUDENT JOINED COURSE", this, "COURSE ID " + course.getId());
				} else {
					System.err.println("You can't join this course");
				}

			} catch (IOException e) {
				System.err.println("Please enter the number!");
				continue;
			} catch (NumberFormatException e1) {
				System.err.println("Please enter the NUMBER!");
				continue;
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println("Can't find course with this #");
				continue;
			}

		}

	}

	@Override
	/**
     * to show to student options that student can do
     * @param br 
     */
	public void mainMenu(BufferedReader br) {
		try {
			while (true) {
				System.out.println("Please choose one of the options below:");
				System.out.println("1) Make request to Office Register"); // +
				System.out.println("2) Make request for Tech Support"); // +
				System.out.println("3) Send mail"); // +
				System.out.println("4) View schedule"); // +
				System.out.println("5) View transcript"); // +
				System.out.println("6) View my courses"); // +
				System.out.println("7) Register to courses"); // +
				System.out.println("8) View mailbox"); // +
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
				if (option.equals("4")) {
					this.viewSchedule(br);
				}
				if (option.equals("5")) {
					this.viewTranscript(br);
				}
				if (option.equals("6")) {
					this.viewCourses(br);
				}
				if (option.equals("7")) {
					this.registerToCourses(br);
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
