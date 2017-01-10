package com.jpodder.util;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.net.URL;
import java.text.DateFormat;
import java.text.*;
import java.util.Date;
import java.util.Locale;

public class Util {
    static DateFormat formatter;
    static {
        formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, Locale.US);
    }

    /**
     * Converts a file size into a playtime based on the sampling bitrate of the
     * file.
     * 
     * @param bitrate
     *            The sampling bitrate.
     * @param size
     *            The file size in bytes.
     * @return The time in miliseconds;
     */
    public static long convertToTime(int bitrate, long size) {
        long bits = size * 8;
        float seconds = bits / bitrate;
        return new Float(seconds).longValue() * 1000;
    }

    /**
     * Format a milisecond number.
     * 
     * @param time
     * @return String
     */
    public static String formatTime(long time) {

        String elapsedTime;

        long seconds = time / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        long sec_remainder = seconds % 60;
        long min_remainder = minutes % 60;
        long hours_remainder = hours % 24;

        if (minutes < 1) {
            elapsedTime = (seconds <= 9 ? "0" : "")
                    + new Long(seconds).toString() + " sec";
        } else {
            if (hours < 1) {
                elapsedTime = (minutes <= 9 ? "0" : "")
                        + new Long(minutes).toString() + ":"
                        + (sec_remainder <= 9 ? "0" : "")
                        + new Long(sec_remainder).toString() + " min";
            } else {
                elapsedTime = (hours <= 9 ? "0" : "")
                        + new Long(hours).toString() + ":"
                        + (min_remainder <= 9 ? "0" : "")
                        + new Long(min_remainder).toString() + ":"
                        + (sec_remainder <= 9 ? "0" : "")
                        + new Long(sec_remainder).toString();
            }
        }
        return elapsedTime;
    }

    public static String formatSize(Object size) {
        if (size instanceof Integer) {
            return formatSize(((Integer) size).intValue());
        }
        if (size instanceof Long) {
            return formatSize(((Long) size).intValue());
        }
        return null;
    }

    /**
     * @param size
     * @return String
     */
    public static String formatSize(long size) {

        NumberFormat formatter = NumberFormat.getInstance();
        formatter.setMaximumFractionDigits(1);
        formatter.setMinimumFractionDigits(1);

        float mbValue = size;
        String format;
        if (mbValue < 1024) {
            format = formatter.format(mbValue) + " KB";
        } else {
            if (mbValue < 1048576) {
                mbValue /= 1024;
                format = formatter.format(mbValue) + " KB";
            } else {
                if (mbValue < 109970456576L) {
                    mbValue /= 1048576;
                    format = formatter.format(mbValue) + " MB";
                } else {
                    formatter.setMaximumFractionDigits(1);
                    formatter.setMinimumFractionDigits(1);
                    mbValue /= (float) 1048576;
                    format = formatter.format(mbValue) + " KB";
                }
            }
        }
        return format;
    }

    /**
     * Format a floating point to a String with a 1 digit fraction.
     * 
     * @param speed
     * @return String A formated speed string with one digit fraction looking
     *         like 0.1
     */
    public static String formatSpeed(float speed) {
        NumberFormat formatter = NumberFormat.getInstance();

        if (speed < 1) {
            formatter.setMaximumFractionDigits(2);
            formatter.setMinimumFractionDigits(2);
        } else {
            formatter.setMaximumFractionDigits(1);
            formatter.setMinimumFractionDigits(1);
        }
        String format = formatter.format(speed);
        return format;
    }

    /**
     * Get the name of a file which is extracted from a file path.
     * 
     * @param path
     *            String
     * @return String
     */
    public static String getName(String path) {
        String result;
        int index = path.lastIndexOf(java.io.File.separator);
        result = path.substring(index + 1, path.length());
        return result;
    }

    /**
     * Get the name of a file which is extracted from a URL path.
     * 
     * @param url
     * @return String
     */
    public static String getName(URL url) {
        String path = url.getPath();
        int index = path.lastIndexOf("/");
        String result = path.substring(index + 1, path.length());
        return result;
    }

    /**
     * Get the URL path stripping of the file name.
     * 
     * @param url
     * @return String
     */
    public static String getNoFilePath(URL url) {
        String path = url.getPath();
        int index = path.lastIndexOf("/");
        String result = path.substring(0, index + 1);
        return result;
    }

    /**
     * Get the name of a file which is extracted from a file path.
     * 
     * @param name
     * 
     * @return String
     */
    public static String stripExtension(String name) {
        String result = name;

        int index = name.lastIndexOf(java.io.File.separator);
        if (index != -1)
            result = name.substring(index + 1, name.length());
        index = result.lastIndexOf('.');
        if (index != -1)
            result = result.substring(0, index);
        return result;
    }

    /**
     * Get the extension of a file which is extracted from a file path.
     * 
     * @param name
     * 
     * @return String
     */
    public static String stripName(String name) {
        String result = name;

        int index = name.lastIndexOf(java.io.File.separator);
        if (index != -1)
            result = name.substring(index + 1, name.length());
        index = result.lastIndexOf('.');
        if (index != -1)
            result = result.substring(index, result.length());
        return result;
    }

    /**
     * Format a date as returned by the Apache HTTP Client date formatter into
     * an application specific format.
     * 
     * @param date
     *            The date to be formated.
     * 
     * @return String The formatted date, with pattern: EEE, d MMM yyyy kk:mm:ss
     *         z
     * 
     */
    public static String formatDate(Date date) {
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
        try {
            SimpleDateFormat simpleFormatter = (SimpleDateFormat) formatter;
            simpleFormatter.applyPattern("EEE, d MMM yyyy kk:mm:ss z");
            String result = simpleFormatter.format(date);
            return result;
        } catch (Exception e) {
            // Simple formatter not supported.
        }
        return null;

    }

    public static Date resolvedDateRFC822(String pString) throws ParseException {
        Date lDate = null;
        String[] lDateFormats = { 
                "EEE, dd MMM yyyy kk:mm:ss z",
                "EEE, MMM dd yyyy kk:mm:ss z" };

        if (pString != null && !"".equals(pString)) {
            SimpleDateFormat simpleFormatter = (SimpleDateFormat) formatter;            
            for (int i = 0; i < lDateFormats.length; i++) {
                String lFormat = lDateFormats[i];
                simpleFormatter.applyPattern(lFormat);    
                try{
                    lDate = simpleFormatter.parse(pString);  
                    break;
                }catch(ParseException pe){
                    // keep trying.
                }
            }
            if(lDate == null){
                throw new ParseException("Not RFC 822 parsebale date format", 0);
            }
        } else {
            throw new IllegalArgumentException();
        }
        return lDate;
    }

    /**
     * Format a date as returned for producing a portable player readable date.
     * (9 characters).
     * 
     * @param date
     *            The date to be formated.
     * 
     * @return String The formatted date, "yy-MM-dd"
     * 
     */
    public static String formatSmartDate(Date date) {
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
        try {
            SimpleDateFormat simpleFormatter = (SimpleDateFormat) formatter;
            simpleFormatter.applyPattern("yy-MM-dd");
            String result = simpleFormatter.format(date);
            return result;
        } catch (Exception e) {
            // Simple formatter not supported.
        }
        return "";
    }

    /**
     * Format a date as returned for producing a portable player readable date.
     * (9 characters).
     * 
     * @param date
     *            The date to be formated.
     * @param pPatern
     *            The patern of the date
     * @return String The formatted date/
     * 
     */
    public static String formatDate(Date date, String pPatern) {
        DateFormat formatter = DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.US);
        try {
            SimpleDateFormat simpleFormatter = (SimpleDateFormat) formatter;
            simpleFormatter.applyPattern(pPatern);
            String result = simpleFormatter.format(date);
            return result;
        } catch (Exception e) {
            // Simple formatter not supported.
        }
        return "";
    }

    /**
     * Get the center of the screen.
     * 
     * @return Point The screen center expressed as x and y.
     */
    public static Point getScreenCenter() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point(screenSize.width / 2, screenSize.height / 2);
    }

    /**
     * Get the left upper corner location of a component, if it should be
     * centered in the screen. Calculates the location from the default screen
     * size and the provided dimension.
     * 
     * @param dim
     * @return Point
     */
    public static Point centerLocations(Dimension dim) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Point((screenSize.width - dim.width) / 2,
                (screenSize.height - dim.height) / 2);

    }
}