package project;

import java.io.Serializable;

/**
 */
public class CourseFile implements Serializable {

	private static final long serialVersionUID = -3627116885342424580L;
	private String link;
	private String fileTitle;


	CourseFile(String title, String link) {
		this.fileTitle = title;
		this.link = link;
	}

	/**
	 * @return
	 */
	public String getLink() {
		return this.link;
	}

	/**
	 * @param link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @return
	 */
	public String getFileTitle() {
		return this.fileTitle;
	}

	/**
	 * @param fileTitle
	 */
	public void setFileTitle(String fileTitle) {
		this.fileTitle = fileTitle;
	}

	/**
	 */
	public void download() {
		// TODO:
		// https://stackoverflow.com/questions/10967451/open-a-link-in-browser-with-java-button
		// https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java/5226244
	}
}
