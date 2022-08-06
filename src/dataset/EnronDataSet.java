/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mahsa Seifikar
 */
public class EnronDataSet {

    static ArrayList<EnronEmail> emails;

//    public static void main(String[] arg) {
//        String csvFile = "emails.csv";
//        preprocessData(csvFile);
//
//    }
    /**
     *
     * @param csvFile
     */
    private static void preprocessData(String csvFile) {
        try {
            readBaseCSVFile(csvFile);
        } catch (IOException ex) {
            System.out.println("cant red file");
        }

        System.out.println(emails.size() + "  Data extract");
        MergeSort mergeSort = new MergeSort(emails);
        mergeSort.mergeSort();
//        QuickSort qs = new QuickSort(emails);
//        qs.startQuickStart(0, emails.size() - 1);
        System.out.println("Data Sort");
        writeToPreprocessedFile();
        System.out.println(emails.size() + "  data write");
    }

    /**
     *
     * @param csvFile
     * @throws IOException
     */
    private static void readBaseCSVFile(String csvFile) throws IOException {

        CsvReader reader;
        try {
            reader = new CsvReader(new FileReader(csvFile), ',');
            reader.setSafetySwitch(false);

            String[] record = null;
            String[] data;
            String content, subject, date, reciverEmails, senderEmails, messageID;
            emails = new ArrayList<>();
            String headr;
            reader.readHeaders();
//            int i = 0;
            while (reader.readRecord()) {

//                if (i >100) {
//                    break;
//                }
                headr = reader.get("file");
                if (!headr.contains("sent")) {
                    continue;
                }

                data = reader.get("message").split("\n");

                if (illigalEmail(data)) {

                    date = prepareDate(data[1]);
                    reciverEmails = prepareReciverEmails(data);
                    subject = prepareSubject(data);
                    content = prepareContent(data);
                    messageID = prepareMessageID(data[0]);
                    senderEmails = prepareSenderEmail(data[2]);

                    if (reciverEmails.contains(",")) {
                        for (String recie : reciverEmails.split(",")) {
                            emails.add(new EnronEmail(date, senderEmails, recie,
                                    messageID, subject, content, " ", " "));
//                                    dataStorage.getEmployeeName(senderEmails),
//                                    dataStorage.getEmployeeName(recie)));
                        }
                    } else {
                        emails.add(new EnronEmail(date, senderEmails, reciverEmails,
                                messageID, subject, content, " ", " "));
//                                dataStorage.getEmployeeName(senderEmails),
//                                dataStorage.getEmployeeName(reciverEmails)));

                    }
//                    i++;
                }
            }

            reader.close();
        } catch (IOException exception) {
            System.out.println("Can't Read" + csvFile + " file");
        }
    }

    /**
     *
     * @return
     */
    public static boolean writeToPreprocessedFile() {

        CsvWriter sb = null;
        try {
            sb = new CsvWriter(new FileWriter("FinalEnronEmails.csv", true), ',');
            EnronEmail enronEmail = null;
            sb.write("ID");
            sb.write("Date");
            sb.write("From");
            sb.write("To");
            sb.write("Subject");
            sb.write("Content");
            sb.write("Sender");
            sb.write("Reciever");
            sb.endRecord();

            for (int i = emails.size() - 1; i >= 0; i--) {
                enronEmail = emails.get(i);
                if (enronEmail.getDate().getYear() < 1998 || enronEmail.getDate().getYear() > 2004) {
                    continue;
                }

                sb.write(enronEmail.getMessageID());
                sb.write(enronEmail.getDate().toString());
                sb.write(enronEmail.getReciverEmail());
                sb.write(enronEmail.getSenderEmail());
                sb.write(enronEmail.getSubject());
                sb.write(enronEmail.getContent());
                sb.write(enronEmail.getSender());
                sb.write(enronEmail.getReciever());
                sb.endRecord();
            }
            sb.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param data
     * @return
     */
    private static boolean illigalEmail(String[] data) {
        if (data[1].contains("Date:")
                && data[2].contains("From:")
                && data[3].contains("To:")
                && !data[2].contains("'")
                && !data[3].contains("'")
                && data[2].contains("@")
                && data[3].contains("@")) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param data
     * @return
     */
    private static String prepareSubject(String[] data) {
        String subject = "";
        boolean flag = false;
        for (int i = 3; i < data.length; i++) {

            if (data[i].contains("Cc") || data[i].contains("Version")) {
                break;
            }
            if (data[i].contains("Subject")) {
                flag = true;
            }
            if (flag) {
                subject += data[i];
            }
        }
        subject = subject.replace("Subject: ", "");

        return subject;
    }

    /**
     *
     * @param data
     * @return
     */
    private static String prepareContent(String[] data) {
        String content = "";
        boolean flag = true;
        int i = 0;
        while (flag) {
            if (!data[i].contains("X-FileName:")) {
                i++;
                continue;
            } else {
                i++;
                break;
            }
        }
        for (; i < data.length; i++) {
            content += data[i];
        }

        return content;
    }

    /**
     *
     * @param data
     * @return
     */
    private static String prepareSenderEmail(String data) {
        return data.split(" ")[1];
    }

    /**
     *
     * @param data
     * @return
     */
    private static String prepareMessageID(String data) {
        return data.split(" ")[1].replace("<", "").replace(".JavaMail.evans@thyme>", "");
    }

    /**
     * to, bcc, cc
     *
     * @param data
     * @return
     */
    private static String prepareReciverEmails(String[] data) {
        String recieverEmails = "";
        boolean flag = true;
        for (int i = 3; i < data.length; i++) {
            if (data[i].contains("Origin")) {
                break;
            }
            if (data[i].contains("Subject") || data[i].contains("Version")
                    || data[i].contains("X-")) {
                flag = false;
            }
            if ((data[i].contains("Cc:") || data[i].contains("Bcc:"))
                    && !(data[i].contains("X-cc:") || data[i].contains("X-bcc:"))) {
                flag = true;
                recieverEmails += ",";
            }
            if (flag) {
                recieverEmails += data[i];
            }
        }
        recieverEmails = recieverEmails.replace("To: ", "").replace("Cc:", "")
                .replace("Bcc:", "").replace(" ", "").replace("\t", "").replace("\n", "");

        return recieverEmails;
    }

//    /**
//     *
//     * @param data
//     * @return
//     */
//    private static String prepareSenderName(String[] data) {
//        String senderName = "";
//        for (String s : data) {
//            if (s.contains("X-From")) {
//                senderName = s;
//                break;
//            }
//        }
//        System.out.println(senderName);
//
//        senderName = senderName.replace("X-From: ", "");
//        String temp = "";
//        for (String s : senderName.split(" ")) {
//            if (s.contains("<")) {
//                continue;
//            }
//            temp += s.replace(",", "");
//        }
//        senderName = temp;
//        return senderName;
//    }
//    /**
//     *
//     * @param data
//     * @return
//     */
//    private static String prepareReciverNames(String data) {
//
//        String recieverNames = "";
//        for (String s : data) {
//            if (s.contains("X-To:")) {
//                recieverNames = s;
//                break;
//            }
//        }
//        System.out.println(recieverNames);
//
//        recieverNames = recieverNames.replace("X-To: ", "");
//        String temp = "";
//        boolean flag = true;
//        for (String s : recieverNames.split(" ")) {
//            if (s.contains("<")) {
//                temp += "-";
//                flag = false;
//                continue;
//            } else if (s.contains(">")) {
//                flag = true;
//                continue;
//            }
//            if (flag) {
////                if (s.contains(".com")) {
////                    temp += s.replace(",", "") + "-";
////                    continue;
////                }
////                temp += s.replace(",", "-").replace("\"", "");
//                temp += s;
//            }
//        }
//        recieverNames = temp.replace("--", "-");
//        return recieverNames;
//    }
    /**
     *
     * @param data
     * @return year-month-day
     */
    private static String prepareDate(String data) {
        String date = "";
        date += Integer.parseInt(data.split(" ")[4]) + "-";
        switch (data.split(" ")[3]) {
            case "Jan":
                date += "1";
                break;
            case "Feb":
                date += "2";
                break;
            case "Mar":
                date += "3";
                break;
            case "Apr":
                date += "4";
                break;
            case "May":
                date += "5";
                break;
            case "Jun":
                date += "6";
                break;
            case "Jul":
                date += "7";
                break;
            case "Aug":
                date += "8";
                break;
            case "Sep":
                date += "9";
                break;
            case "Oct":
                date += "10";
                break;
            case "Nov":
                date += "11";
                break;
            case "Dec":
                date += "12";
                break;

        }

        date += "-" + Integer.parseInt(data.split(" ")[2]);
        return date;
    }

    /**
     *
     * @param type
     * @param duration
     * @return
     * @throws IOException
     */
    public int builsSnapshotLists(String type, int duration) throws IOException {
        ArrayList<List<EnronEmail>> extractEmails = new ArrayList<>();

        ArrayList<EnronEmail> sampleEmails = readPreprocessedData();

        Date baseDate = sampleEmails.get(0).getDate();
        ArrayList<EnronEmail> subEmails = new ArrayList<>();
        System.out.println("base: " + baseDate);
        
        for (EnronEmail enronEmail : sampleEmails) {

            if (enronEmail.getDate().isBelongDuration(baseDate, type, duration)) {
                subEmails.add(enronEmail);
            } else {

                extractEmails.add(subEmails);
                subEmails = new ArrayList<>();
                subEmails.add(enronEmail);
                baseDate = enronEmail.getDate();
                System.out.println("----------------------------------------------------------------");
                System.out.println("base: " + baseDate);
                
            }
        }
        extractEmails.add(subEmails);

        String fn = "Snapshot";
        for (int i = 1; i <= extractEmails.size(); i++) {
            writeGephiFormData(extractEmails.get(i - 1), fn + i + ".csv");
        }
        return extractEmails.size();
    }

    /**
     *
     * @return @throws IOException
     */
    public static ArrayList<EnronEmail> readPreprocessedData() throws IOException {
        ArrayList<EnronEmail> preprocessedemails = new ArrayList();

        CsvReader reader = new CsvReader("FinalEnronEmails.csv");
        reader.setSafetySwitch(false);

        reader.readHeaders();

        // TODO
        while (reader.readRecord()) {
            if (reader.get("Date").equals("Date")) {
                break;
            }

            preprocessedemails.add(new EnronEmail(reader.get("Date"), reader.get("From"),
                    reader.get("To"), reader.get("ID"),
                    reader.get("Subject"), reader.get("Content"),
                    reader.get("Sender"), reader.get("Reciever")));

        }
        reader.close();
        return preprocessedemails;
    }

    /**
     *
     * @param emails
     * @param fn
     * @return
     * @throws IOException
     */
    public static boolean writeGephiFormData(List<EnronEmail> emails, String fn) throws IOException {

        CsvWriter sb = null;
        try {
            sb = new CsvWriter(new FileWriter(fn, true), ',');
            EnronEmail enronEmail = null;
            
            for (int i = 0; i < emails.size(); i++) {
                enronEmail = emails.get(i);
                sb.write(enronEmail.getSenderEmail());
                sb.write(enronEmail.getReciverEmail());
                sb.endRecord();
//                System.out.println(i);
            }
            sb.close();
            return true;
        } catch (IOException e) {
            System.out.println("Error in CsvFileWriter !!!");
            return false;
        }
    }

}
