package project;

import java.io.Serializable;
import java.util.Vector;

/** */
public class Discipline implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532687561811062405L;

	private String code;
	private String title;
	private String description;
	private Faculty[] allowedFaculties;
	private String[] prerequisites;
	private boolean openedForRegistration;
	private int credits;

	/**
	 * 
	 * @param code
	 * @param title
	 * @param description
	 * @param prerequisites
	 */
	public Discipline (String code, String title, String description, int credits, String[] prerequisites, Faculty[] faculties) {
		this.code = code;
		this.title = title;
		this.description = description;
		this.prerequisites = prerequisites;
		this.openedForRegistration = false;
		this.allowedFaculties = faculties;
		this.credits = credits;
	}

	public String getCode() {
		return code;
	}

	public int getCredits() {
		return this.credits;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Faculty[] getAllowedFaculties() {
		return this.allowedFaculties;
	}

	public String facultiesToString() {
		String result = "[ ";
		for (Faculty f : this.allowedFaculties) result += f.toString() + ", ";
		result += "]";
		result = result.replace(", ]", " ]");
		return result;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getPrerequisites() {
		return prerequisites;
	}

	public void setPrerequisites(String[] prerequisites) {
		this.prerequisites = prerequisites;
	}

	public boolean isOpenedForRegistration() {
		return openedForRegistration;
	}

	public void setOpenedForRegistration(boolean openedForRegistration) {
		this.openedForRegistration = openedForRegistration;
	}

	/**
	 * @param courses
	 * @return
	 */
	public boolean checkPrerequisites(Vector<String> courses) {
		for (String p : prerequisites) {
			boolean founded = false;
			for (String c : courses) {
				Course course = Intranet.INSTANCE.getCourseByID(c);
				if (course.getCode().equals(p)) {
					founded = true;
					break;
				}
			}
			if (!founded)
				return false;
		}
		return true;
	}

	public boolean checkFaculties(Faculty faculty) {
		for (Faculty f : this.allowedFaculties) 
			if (f.equals(faculty)) return true;
		return false;
	}

	public boolean canJoin(Vector<String> courses, Faculty faculty) {
		return checkFaculties(faculty) && checkPrerequisites(courses);
	}

	/**
	 * @return
	 */

}
