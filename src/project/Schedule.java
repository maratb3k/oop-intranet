package project;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import asciiTable.AsciiTable;

/**
 */
public class Schedule implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3948504256033409449L;
	public static final int hoursInDay = 24;
	public static final int daysInWeek = 7;
	public static final String[] tableHeader = { "Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday",
			"Saturday", "Sunday" };
	public static final int startFromHour = 8;
	public static final int endToHour = 20;
	/**
	 */
	private String[][] timeTable;

	public Schedule() {
		this.timeTable = new String[Schedule.hoursInDay][Schedule.daysInWeek];
	}


	public String[][] getTimeTable() {
		return this.timeTable;
	}

	public int getLessonsCount() {
		int result = 0;
		for (int i = 0; i < Schedule.hoursInDay; i++) {
			for (int j = 0; j < Schedule.daysInWeek; j++) {
				if (this.timeTable[i][j] != null) {
					result++;
				}
			}
		}
		return result;
	}

	/**
	 * @return
	 */
	public static boolean isIntersect(Schedule schedule1, Schedule schedule2) {

		for (int i = 0; i < Schedule.hoursInDay; i++) {
			for (int j = 0; j < Schedule.daysInWeek; j++) {
				if (schedule1.timeTable[i][j] != null && schedule2.timeTable[i][j] != null) {
					return true;
				}
			}
		}

		return false;
	}

	public void addLesson(int dayIndex, int hoursIndex, String text) {
		this.timeTable[hoursIndex][dayIndex] = text;
	}

	public void removeLesson(int dayIndex, int hoursIndex) {
		this.timeTable[hoursIndex][dayIndex] = null;
	}

	public boolean isIntersect(Schedule schedule) {
		return Schedule.isIntersect(this, schedule);
	}

	/**
	 * @param schedule2
	 * @param schedule1
	 * @return
	 */
	public static Schedule addToSchedule(Schedule schedule1, Schedule schedule2) throws IOException {
		if (Schedule.isIntersect(schedule1, schedule2))
			throw new IOException("Schedules are intersecting");
		Schedule result = new Schedule();
		for (int i = 0; i < Schedule.hoursInDay; i++) {
			for (int j = 0; j < Schedule.daysInWeek; j++) {
				if (schedule1.timeTable[i][j] != null) {
					result.timeTable[i][j] = schedule1.timeTable[i][j];
				}
				if (schedule2.timeTable[i][j] != null) {
					result.timeTable[i][j] = schedule2.timeTable[i][j];
				}
			}
		}
		return result;
	}

	public Schedule addToSchedule(Schedule schedule) throws IOException {
		return Schedule.addToSchedule(this, schedule);
	}

	/**
	 * @param schedule2
	 * @param schedule1
	 * @return
	 */
	public static Schedule removeFromSchedule(Schedule schedule1, Schedule schedule2) {
		Schedule result = new Schedule();

		for (int i = 0; i < Schedule.hoursInDay; i++) {
			for (int j = 0; j < Schedule.daysInWeek; j++) {
				if (schedule1.timeTable[i][j] != null && schedule1.timeTable[i][j].equals(schedule2.timeTable[i][j])) {
					result.timeTable[i][j] = null;
				} else {
					result.timeTable[i][j] = schedule1.timeTable[i][j];
				}
			}
		}
		return result;
	}

	public Schedule removeFromSchedule(Schedule schedule) {
		return Schedule.removeFromSchedule(this, schedule);
	}

	/**
	 * @return
	 */
	public String hoursPretty(int hours) {
		if (hours > 9) {
			return "" + hours + ":00";
		} else {
			return "0" + hours + ":00";
		}
	}

	public String toString() {
		String[][] data = new String[endToHour - startFromHour + 1][daysInWeek + 1];
		for (int i = startFromHour; i <= endToHour; i++) {
			data[i - startFromHour][0] = hoursPretty(i);
			for (int j = 1; j < daysInWeek + 1; j++) {
				data[i - startFromHour][j] = this.timeTable[i][j - 1];
			}
		}

		return AsciiTable.getTable(tableHeader, data);
	}

	public Schedule clone() {
		if (timeTable == null) {
			return null;
		}
		Schedule clone = new Schedule();
		String[][] cloneTimeTable = new String[Schedule.hoursInDay][Schedule.daysInWeek];
		for (int i = 0; i < timeTable.length; i++) {
			cloneTimeTable[i] = Arrays.copyOf(timeTable[i], timeTable[i].length);
		}
		clone.timeTable = cloneTimeTable;
		return clone;
	}
}
