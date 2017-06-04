package com.it_project.fg15a.timetable_app.helpers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class dataModifier {

    // This method returns the HTML of the specified URL as string that needs to be parsed
    public String getWebContent(String p_sUrl) {
        // declare and initialize necessary variables
        String sWebContent = "", sLine;
        InputStream isWebsite = null;

        try {
            // open connection to URL
            URLConnection uconWebsite = new URL(p_sUrl).openConnection();
            // connect to the URL
            uconWebsite.connect();
            // get the content of the connection
            isWebsite = uconWebsite.getInputStream();
            // create reader to read content
            BufferedReader brWebsite = new BufferedReader(
                    new InputStreamReader(isWebsite, "ISO-8859-1"));

            // as long as there is something that can be red
            while ((sLine = brWebsite.readLine()) != null) {
                // add the new line to the return value
                sWebContent += sLine;
            }

            // close the reader
            brWebsite.close();
        }
        catch (IOException e) {
            // throw the catched exception
            e.printStackTrace();
        } finally {
            // when the input stream is not null
            if (isWebsite != null) {
                try {
                    // try to close the connection to the website
                    isWebsite.close();
                } catch (IOException e) {
                    // throw the catched exception
                    e.printStackTrace();
                }
            }
        }

        // return the unparsed HTML of the given URL
        return sWebContent;
    }

    // This method returns the preprocessed data of a timetable html file
    public Map<String, String[]> preModifyContent(String p_sWebsiteContent) {
        // map to store all processed data
        // Structure: {Column Index_Row Index, [(0), (1), (2), (3), (4)]}
        // Example -> Time: {0_1, [(0)Time from, (1)Time to]}
        // Example -> Hour: {1_1, [(2)Subject, (3)Teacher, (4)Room]}
        Map<String, String[]> mData = new LinkedHashMap<>();

        // list to store all column indexes at which all columns of the following rows need to be
        // moved to a new index
        List<Integer> lMoveColumns = new ArrayList<>();

        // int to check the hour
        String sHour = "";

        // turn p_sWebsiteContent into a HTML document
        Document docPage = Jsoup.parse(p_sWebsiteContent);

        // select all rows which are direct children of tbody of the table, max count: 21
        Elements eRows = docPage.select("table[border='3'] > tbody > tr:has(tr)");

        // iterate through each row (= HTML tr tag) of the table
        for (Element eRow : eRows) {
            // index of tr tag in tbody
            int iRowIndex = eRow.elementSiblingIndex();

            // if row index is not zero, then it's real content
            if (iRowIndex != 0) {
                // select all td tags of tr tag, max count: 7
                Elements eColumns = eRow.children();

                // iterate through each definition (= HTML td tag) of the actual row
                for (Element eColumn : eColumns) {
                    // index of td tag in tr tag
                    int iColumnIndex = eColumn.elementSiblingIndex();
                    // index of previous row, used to correct column index of 2nd part of blocks
                    int iPreviousRow = iRowIndex - 2;

                    // if there's at least one complete day entry and the hour value is odd
                    if (lMoveColumns.size() > 0 && Integer.valueOf(sHour) % 2 != 0) {
                        // as long as the column index is part of the list with the complete day
                        // entry indexes or the actual key is part of the data map
                        while (lMoveColumns.contains(iColumnIndex) ||
                                mData.containsKey(iColumnIndex + "_" + iRowIndex)) {
                            // increment the column index by 1
                            iColumnIndex += 1;
                        }
                    }

                    // initialize string array for data
                    // sData[0/1] => time from/to
                    // sData[2-4] => subject, teacher, room
                    // sData[5] => 1 - complete day, 2 - half block,
                    //             3 - replacement lessons/omission, 0 - default entry, t - time
                    // sData[6] => value of hour
                    String[] sData = new String[7];

                    // if column index is zero then there are time values
                    if (iColumnIndex == 0) {
                        // set time from value
                        sData[0] = eColumn.select("> table > tbody > tr > td + td").text();
                        // set time to value
                        sData[1] = eColumn.select("> table > tbody > tr + tr > td").text();
                        // set entry type value
                        sData[5] = "t";
                        // set number of hour twice to set sHour which is used in the else part
                        sData[6] = eColumn.select("> table > tbody > tr > td[rowspan='2']").text();
                        sHour = sData[6];

                        // add data with unique index to map
                        mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex),
                                sData);
                    }
                    // TODO: Add check for empty values
                    // TODO: Add check for course blocks (= tds with 20% width)
                    // if no time or day values then subject, teacher and room values
                    else {
                        // select all tr tags of the table of the td tag
                        Elements eRemainingStrings = eColumn.select("> table > tbody > tr");

                        // complete day, when attribute rowspan = 20
                        if (eColumn.attr("rowspan").equals("20")) {
                            // set entry type to value for complete day entry
                            sData[5] = "1";
                            // add column index to list with indexes which are unavailable in rows
                            // greater than 1
                            lMoveColumns.add(iColumnIndex);
                        }
                        // half block, when attribute rowspan = 2
                        else if (eColumn.attr("rowspan").equals("2")) {
                            // replacement lesson/omission, when font color is red
                            if (eColumn.getElementsByAttributeValue("color", "#FF0000")
                                    .size() != 0) {
                                sData[5] = "3";
                            }
                            // default half block, when font color is not red
                            else {
                                sData[5] = "2";
                            }
                        }
                        // default branch, could also be an if with condition: rowspan = 4
                        else {
                            sData[5] = "0";
                        }

                        // iterate through each row to get the left information
                        for (Element eRemainingString : eRemainingStrings) {
                            // index of tr tag of table in td tag
                            int iRemainingStringIndex = eRemainingString.elementSiblingIndex();

                            // switch on base of the index of the row to get left information
                            switch (iRemainingStringIndex) {
                                case 0:
                                    // set subject
                                    sData[2] = eRemainingString.text();
                                    break;
                                case 1:
                                    // set teacher
                                    sData[3] = eRemainingString.text();
                                    break;
                                case 2:
                                    // set room
                                    sData[4] = eRemainingString.text();
                                    break;
                                default:
                                    break;
                            }
                        }

                        // if hour value is even, it's the second part of a block
                        if (Integer.valueOf(sHour) % 2 == 0) {
                            // set hour value to hour value - 1 to get value of previous row
                            sData[6] = String.valueOf(Integer.valueOf(sHour) - 1);

                            // iterate from monday to saturday
                            for (int i = 1; i <= 6; i++) {
                                // if the value of the map with the specified key equals sData,
                                // it's the previous row, but only when it not already exists
                                if (Arrays.equals(mData.get(i + "_" + iPreviousRow), sData)) {
                                    // set column index to fit the previous one
                                    iColumnIndex = i;
                                    // when the data map doesn't already contain the actual key
                                    if (!mData.containsKey(iColumnIndex + "_" + iRowIndex)) {
                                        // stop the loop iteration
                                        break;
                                    }
                                }
                            }
                        }

                        // set the hour value to the correct value
                        sData[6] = sHour;

                        // add data with unique index to map
                        mData.put(String.valueOf(iColumnIndex) + "_" + String.valueOf(iRowIndex),
                                sData);
                    }
                }
            }
        }

        // return map with processed data
        return postModifyContent(mData);
    }

    // This method returns the final post processed data of a timetable html file
    private Map<String, String[]> postModifyContent (Map<String, String[]> p_mData) {
        // map to store post processed data
        // Structure: {Column Index_Row Index, [(0), (1), (2), (3), (4)]}
        // Example -> Time: {0_1, [(0)Time from, (1)Time to]}
        // Example -> Hour: {1_1, [(2)Subject, (3)Teacher, (4)Room]}
        Map<String, String[]> mFinalData = new LinkedHashMap<>();

        // iterate through each entry in data map
        for (Map.Entry<String, String[]> meData : p_mData.entrySet()) {
            // get column index, represents day index and x-axis value of table
            int iColumnIndex = Integer.valueOf(meData.getKey().substring(0, 1));
            // get row index, represents hour index and y-axis value of table
            int iRowIndex = Integer.valueOf(meData.getKey().substring(2));

            // get values of the actual hour
            String[] sData = meData.getValue();

            // if key starts with column index zero, then it's the time column
            if (meData.getValue()[5].equals("t")) {
                // put time value in final map
                mFinalData.put(meData.getKey(), meData.getValue());
            }
            // when type value stands for complete day and row index is 1
            else if (meData.getValue()[5].equals("1") && meData.getKey().substring(2).equals("1")) {
                // iterate from hour 1 to hour 10
                for (int i = 1; i <= 19; i = i + 2) {
                    // add values to all 10 hours
                    mFinalData.put(iColumnIndex + "_" + i, meData.getValue());
                }
            }
            // half block or replacement lesson/omission, both types consist of 2 parts
            else if (meData.getValue()[5].equals("2") || meData.getValue()[5].equals("3")) {
                // if odd hour value
                if (Integer.valueOf(meData.getValue()[6]) % 2 != 0) {
                    // add 1 to actual hour for check if second part of this one exists
                    sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) + 1);

                    // if the original map contains the following entry and the final map
                    // doesn't contain the actual one
                    if (p_mData.containsValue(sData) &&
                            !mFinalData.containsKey(meData.getKey())) {
                        // add actual and following hour to final map
                        mFinalData.put(meData.getKey(), meData.getValue());
                        mFinalData.put(iColumnIndex + "_" + (iRowIndex + 2), sData);
                    }
                    // if the final map doesn't contain the actual hour
                    else if (!mFinalData.containsKey(meData.getKey())) {
                        // add actual hour to final map
                        mFinalData.put(meData.getKey(), meData.getValue());
                    }
                }
                // if it's not an odd hour value it's even
                else {
                    // subtract 1 from actual hour to check if first part of this one exists
                    sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) - 1);

                    // if the original map contains the previous entry and the final map
                    // doesn't contain the actual one
                    if (p_mData.containsValue(sData) &&
                            !mFinalData.containsKey(meData.getKey())) {
                        // add previous and actual hour to final map
                        mFinalData.put(iColumnIndex + "_" + (iRowIndex - 2), sData);
                        mFinalData.put(meData.getKey(), meData.getValue());
                    }
                    // if the final map doesn't contain the actual hour
                    else if (!mFinalData.containsKey(meData.getKey())) {
                        // add actual hour to final map
                        mFinalData.put(meData.getKey(), meData.getValue());
                    }
                }
            }
            // default entry, consists only of one part
            else {
                // add 1 to actual hour for creating the second part
                sData[6] = String.valueOf(Integer.valueOf(meData.getValue()[6]) + 1);

                // add first and second part of actual hour to final map
                mFinalData.put(meData.getKey(), meData.getValue());
                mFinalData.put(iColumnIndex + "_" + (iRowIndex + 2), sData);
            }
        }

        // return the final processed map
        return mFinalData;
    }
}
