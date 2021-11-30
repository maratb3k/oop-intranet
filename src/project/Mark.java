package project;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

/**
 */
public class Mark implements Serializable {

	private static final long serialVersionUID = -5688064113814295498L;
	private Vector<Double> firstAtt;
	private Vector<Double> secondAtt;
	private double pointsForFinal;
	private int absenceCount;
	private boolean finalHeld = false;
	private static HashMap<String, Double> GPATable = new HashMap<String, Double>() {
		private static final long serialVersionUID = 1L;
		{
			put("A", 4.0);
			put("A-", 3.67);
			put("B+", 3.33);
			put("B", 3.0);
			put("B-", 2.67);
			put("C+", 2.33);
			put("C", 2.0);
			put("D+", 1.67);
			put("D", 1.0);
			put("F", 0.0);
		}
	};
	
	public Mark() {
		this.finalHeld = false;
		this.firstAtt = new Vector<Double>();
		this.secondAtt = new Vector<Double>();
		this.absenceCount = 0;
		this.pointsForFinal = 0;
	}

	/**
     * to get number of absence of student for the course
     * @return number of absences of students
     */
	public int getAbsenceCount() {
		return this.absenceCount;
	}

	/**
     * let to put student's absences to teacher for the course
     */
	public void putAbsence() {
		this.absenceCount++;
	}

	/**
     * let to get student's points for the course for the first attestation
     * @return vector of points for the first attestation
     */
	public Vector<Double> getFirstAtt() {
		return this.firstAtt;
	}

	/**
	 * checks whether the student passed the final
     * @return true if student passed final, false if not
     */
	public boolean isFinalHeld() {
		return finalHeld;
	}

	/**
     * let to get student's points for the course for the second attestation
     * @return vector of points for the courses for the second attestation
     */
	public Vector<Double> getSecondAtt() {
		return this.secondAtt;
	}

	/**
     * let to get student's points for the final
     * @return point for the final
     */
	public double getFinal() {
		return Math.min(this.pointsForFinal, 40);
	}

	/**
     * to put point for the final exam
     * @param points 
     */
	public void putFinal(Double points) {
		this.finalHeld = true;
		this.pointsForFinal = Math.min(points, 40);
	}

	/**
     * let to get student's total points for the course for the first attestation
     * @return total point for the first attestation
     */
	public double getSumFirstAtt() {
		double result = 0.0;
		for (Double cur : this.firstAtt)
			result += cur;
		return result;
	}

	/**
     * let to get student's total points for the course for the second attestation
     * @return total point for the second attestation
     */
	public double getSumSecondAtt() {
		double result = 0.0;
		for (Double cur : this.secondAtt)
			result += cur;
		return result;
	}

	/**
     * let to get student's total points for the course for the both attestations
     * @return total point for both attestation
     */
	public double getSumBothAtt() {
		return Math.min(getSumFirstAtt() + getSumSecondAtt(), 60);
	}

	/**
     * let to get student's total points for the course
     * @return total point for the course
     */
	public double getTotalPoints() {
		return getSumBothAtt() + getFinal();
	}

	/**
     * to get student's grade for the course
     * @return grade for the course
     */
	public String getGrade() throws IOException {
		if (!this.finalHeld)
			throw new IOException("Final not held yet");
		double total = getTotalPoints();
		if (total >= 95)
			return "A";
		if (total >= 90)
			return "A-";
		if (total >= 85)
			return "B+";
		if (total >= 80)
			return "B";
		if (total >= 75)
			return "B-";
		if (total >= 70)
			return "C+";
		if (total >= 65)
			return "C";
		if (total >= 60)
			return "C-";
		if (total >= 55)
			return "D+";
		if (total >= 50)
			return "D";
		return "F";
	}

	/**
     * to get student's gpa for the course
     * @return gpa for the course
     */
	public double getGPA() throws IOException {
		return Mark.GPATable.get(getGrade());
	}

	/**
     * checks student's absences
     * @param course 
     * @return true if student's absence is ok for the course, false if not
     */
	public boolean isRetake(Course course) throws IOException {
		if (!((course.getCredits() * Course.numOfWeeks * 0.2) > this.absenceCount)) {
			if (this.finalHeld) {
				return getGrade().equals("F");
			} else
				throw new IOException("Final not held yet");
		}
		return false;
	}

}
