/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset;

/**
 *
 * @author Mahsa Seifikar
 */
public class Date {

    private int year;
    private int month;
    private int date;

    public Date() {
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the date
     */
    public int getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(int date) {
        this.date = date;
    }

    @Override
    public String toString() {

        return year + "-" + month + "-" + date;
    }

    /**
     * is Greater or equal
     *
     * @param sample is a Date
     * @return
     */
    public boolean isGreater(Date sample) {
        
        if (sample.year < this.year) {
            return true;
        }
        if (sample.year == this.year && sample.month < this.month) {
            return true;
        }

        if (sample.year == this.year
                && sample.month == this.month
                && sample.date <= this.date) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param baseDate
     * @param durationType
     * @param countDuration
     * @return
     */
    public boolean isBelongDuration(Date baseDate, String durationType, int countDuration) {

        switch (durationType) {
            case "year":
                if (baseDate.getYear() <= this.year
                        && this.year < baseDate.getYear() + countDuration) {
                    return true;
                }
                break;
            case "month":
                if (baseDate.getYear() == this.getYear()
                        && baseDate.getMonth() <= this.month
                        && this.month < baseDate.getMonth() + countDuration) {
                    return true;
                }
                break;
            case "day":
//                System.out.println("this:  "+this);
//                System.out.println("base:  "+ baseDate);
//                System.out.println("cn:  "+ createNewDate(baseDate, countDuration));
//                System.out.println(this.isGreater(baseDate));
//                System.out.println(createNewDate(baseDate, countDuration).isGreater(this));
                
                if (this.isGreater(baseDate) && createNewDate(baseDate, countDuration).isGreater(this)) {
                    return true;
                }
                break;
        }
        return false;
    }

    /**
     *
     * @param baseDate
     * @param countDuration
     * @return
     */
    private Date createNewDate(Date baseDate, int countDuration) {
        Date sample = new Date();
        countDuration--;
        if (baseDate.getMonth() < 7) {
            sample.setDate((baseDate.getDate() + countDuration) % 31);
            sample.setMonth(((baseDate.getDate() + countDuration) / 31) + baseDate.getMonth());
            sample.setYear(baseDate.getYear());
            if (sample.getMonth() > 12) {
                sample.setMonth(sample.getMonth() % 12);
                sample.setYear(baseDate.getYear() + 1);
            }
        } else {
            sample.setDate((baseDate.getDate() + countDuration) % 30);
            sample.setMonth(((baseDate.getDate() + countDuration) / 30) + baseDate.getMonth());
            sample.setYear(baseDate.getYear());

            if (sample.getMonth() > 12) {
                sample.setMonth(sample.getMonth() % 12);
                sample.setYear(baseDate.getYear() + 1);
            }
        }
//        System.out.println("sample: " + sample);
        return sample;
    }
}
