package project;

import java.io.*;
import java.util.*;

import asciiTable.AsciiTable;

/**
 */
public class Course extends Discipline {
	/**
	 * @param code
	 * @param title
	 * @param description
	 * @param prerequisites
	 * @param capacity
	 * @param teacherId
	 * @param schedule
	 */
	Course(String code, String title, String description, String[] prerequisites, int credits, int capacity,
			String teacherId, Faculty[] faculties) {
		super(code, title, description, credits, prerequisites, faculties);
		this.courseId = Intranet.INSTANCE.generateCourseId();
		this.teacherID = teacherId;
		this.capacity = capacity;
		this.students = new HashMap<String, Mark>();
		this.courseFiles = new Vector<CourseFile>();
		this.schedule = new Schedule();
	}

	private static final long serialVersionUID = 4754774493911751398L;
	public static final int numOfWeeks = 15;
	private String courseId;
	private String teacherID;
	private HashMap<String, Mark> students;
	private int capacity;
	private boolean ended;
	private Vector<CourseFile> courseFiles;
	private Schedule schedule;

	/**
	 * @return
	 */
	public void end() {
		this.ended = true;
		this.setOpenedForRegistration(false);
		for (String id : getStudentsIDs()) {
			Student st = (Student) Intranet.INSTANCE.getUserByID(id);
			st.setSchedule(st.getSchedule().removeFromSchedule(this.schedule));
		}
		Teacher t = getTeacher();
		t.setSchedule(t.getSchedule().removeFromSchedule(this.schedule));
	}

	public boolean isEnded() {
		return ended;
	}

	public String getId() {
		return this.courseId;
	}

	public boolean isNeedSchedule() {
		return this.schedule.getLessonsCount() != this.getCredits();
	}

	public int getCapacity() {
		return this.capacity;
	}

	public String toString() {
		return "Course name: " + this.getTitle();
	}

	public Teacher getTeacher() {
		return (Teacher) Intranet.INSTANCE.getUserByID(this.teacherID);
	}

	public String getCourseId() {
		return this.courseId;
	}

	public Vector<String> getStudentsIDs() {
		return new Vector<String>(this.students.keySet());
	}

	public void removeStudent(Student student) {
		this.students.remove(student.id);
		student.getCourses().remove(this.courseId);
		student.setSchedule(student.getSchedule().removeFromSchedule(this.schedule));
	}

	/**
	 * @param student
	 * @return
	 */
	public boolean canJoin(Student student) {
		Vector<String> filteredCourseIds = new Vector<String>();
		for (String p : this.getPrerequisites()) {
			if (student.getCourses().containsKey(p)) {
				filteredCourseIds.add(p);
			} else return false;
		}

		for (String id : filteredCourseIds) {
			Course course = Intranet.INSTANCE.getCourseByID(id);
			Mark mark = course.getStudentMark(student);
			if (mark.isFinalHeld()) {
				try {
					if (mark.isRetake(this)) return false;
				} catch (IOException e) {}
			} else return false;
		}

		return super.canJoin(filteredCourseIds, student.getFaculty())
				&& this.capacity > this.getStudentsCount() && !this.students.keySet().contains(student.getId());
	}

	/**
	 * @param studentID
	 * @return
	 */
	public Mark getStudentMark(String studentID) {
		return this.students.get(studentID);
	}

	public Mark getStudentMark(Student student) {
		return this.students.get(student.id);
	}

	/**
	 * @return
	 */
	public int getStudentsCount() {
		return this.students.size();
	}

	/**
	 * @return
	 */
	public Vector<CourseFile> getCourseFiles() {
		return this.courseFiles;
	}

	/**
	 * @param title
	 * @param link
	 */
	public void addFile(String title, String link) {
		CourseFile newCourseFile = new CourseFile(title, link); 
		this.courseFiles.add(newCourseFile);
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public void addStudent(Student student) throws IOException {
		Mark mark = new Mark();
		student.getCourses().put(this.courseId, student.getCurrentSemester());
		this.students.put(student.id, mark);
		student.setSchedule(student.getSchedule().addToSchedule(this.schedule));
	}

	public void addStudent(String studentID) throws NoSuchElementException, IOException {
		this.addStudent((Student) Intranet.INSTANCE.getUserByID(studentID));
	}

	public void viewCourse(User user, BufferedReader br) {
		System.out.println("Code: " + this.getCode());
		System.out.println("Title: " + this.getTitle());

		if (!user.role.equals(Role.TEACHER))
			System.out.println("Teacher: " + getTeacher().getFullName());
		if (!user.role.equals(Role.TEACHER))
			System.out.println("Teacher login: " + getTeacher().login);
		if (!user.role.equals(Role.TEACHER))
			System.out.println("Teacher qualification: " + getTeacher().getQualification());
		
		System.out.println("Description: " + this.getDescription());
		System.out.println("Prerequisites: [ " + String.join(", ", this.getPrerequisites()) + " ]");
		System.out.println("Course files count: " + this.getCourseFiles().size());
		
		
		// Draw table with files
		String[] headerFiles = { "#", "Title", "DownloadLink" };
		String[][] dataFiles = new String[this.courseFiles.size()][3];
		for (int i = 0; i < this.courseFiles.size(); i++) {
			dataFiles[i][0] = "" + (i + 1);
			dataFiles[i][1] = this.courseFiles.get(i).getFileTitle();
			dataFiles[i][2] = this.courseFiles.get(i).getLink();
		}
		System.out.println(AsciiTable.getTable(headerFiles, dataFiles));
		
		if (user.role.equals(Role.TEACHER)) {
			Teacher teacher = (Teacher) user;
			teacher.viewCourse(this, br);
		}
		if (user.role.equals(Role.STUDENT)) {
			Student student = (Student) user;
			student.viewCourseMarks(this, br);
		}
		if (user.role.equals(Role.MANAGER)) {
			Manager manager = (Manager) user;
			manager.viewCourse(this, br);
		}
		
	}

}
