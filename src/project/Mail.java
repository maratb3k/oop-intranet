package project;

import java.io.Serializable;
import java.util.*;
import asciiTable.AsciiTable;

/**
 */
public class Mail implements Serializable {


  /**
   *
   */
  private static final long serialVersionUID = 1455438730736114657L;
  protected String title;
  protected String text;
  protected Date sendTime;
  protected boolean isRead;
  protected String senderId;

  public Mail(String title, String text, String senderId) {
	  this.title = title;
	  this.text = text;
	  this.senderId = senderId;
	  this.isRead = false;
	  this.sendTime = new Date();
  }
  
  public void read() {
	  this.isRead = true;
  }
  
  public User getSender() {
	  return  Intranet.INSTANCE.getUserByID(this.senderId);
  }

  public String getTitle() {
    return this.title;
  }

  /**
   * @return
   */
  public String toString() {
    User sender = getSender();
    String[][] data = { 
      { "Title:", title }, 
      { "Sender:", sender.getFullName() },
      { "Sended time:", this.sendTime.toLocaleString() } 
    };
    return AsciiTable.getTable(data) + "\n" + this.text;
  }
}
