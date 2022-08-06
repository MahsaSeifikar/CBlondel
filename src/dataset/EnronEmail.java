/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

/**
 *
 * @author Mahsa Seifikar
 */
public class EnronEmail {

    private String sender;
    private String reciever;
    private String senderEmail;
    private String reciverEmail;
    private String messageID;
    private String subject;
    private String content;
    private Date date;

    public EnronEmail() {
        date = new Date();
    }

    
    public EnronEmail(String date, String senderEmail, String reciverEmail,
            String id, String subject, String content, String sender, String reciever) {
        this.date = new Date();
        
        setDate(date);
        setSenderEmail(senderEmail);
        setReciverEmail(reciverEmail);
        setMessageID(id);
        setSubject(subject);
        setContent(content);
        this.sender = sender;
        this.reciever = reciever;
    }

    /**
     * @param date the date senderEmail set
     */
    public void setDate(String date) {

        this.getDate().setYear(Integer.parseInt(date.split("-")[0]));
        this.getDate().setMonth(Integer.parseInt(date.split("-")[1]));
        this.getDate().setDate(Integer.parseInt(date.split("-")[2]));

    }

    /**
     * @return the senderEmail
     */
    public String getSenderEmail() {
        return senderEmail;
    }

    /**
     * @param senderEmail the senderEmail senderEmail set
     */
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    /**
     * @return the From
     */
    public String getReciverEmail() {
        return reciverEmail;
    }

    /**
     * @param From the From senderEmail set
     */
    public void setReciverEmail(String reciverEmail) {
        this.reciverEmail = reciverEmail;
    }

    public Date getDate() {
        return date;
    }

    /**
     * @param date the date senderEmail set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * @return the messageID
     */
    public String getMessageID() {
        return messageID;
    }

    /**
     * @param messageID the messageID senderEmail set
     */
    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject the subject senderEmail set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content senderEmail set
     */
    public void setContent(String content) {
        this.content = content;
    }

    
    /**
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender the sender to set
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the reciever
     */
    public String getReciever() {
        return reciever;
    }

    /**
     * @param reciever the reciever to set
     */
    public void setReciever(String reciever) {
        this.reciever = reciever;
    }
    
    @Override
    public String toString() {
        return getSender() + "          " + getReciever();
//        return getSenderEmail() + "            " + getReciverEmail();
//        return "ID: " + getMessageID() + " Date:" + getDate().toString()
//                + " From: " + getReciverEmail() + " To: " + getSenderEmail();
//                + " Subject: " + getSubject() + "\n" + getContent() + "\n";
    }

    /**
     *
     * @param email
     * @return
     */
    public boolean isGreater(EnronEmail email) {
        if (email.getDate().isGreater(getDate())) {
            return true;
        }
        return false;
    }

}
