package me.chanjar.weixin.mp.demo.jizhang;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015-3-15.
 */
public class JZHelper {
    private static String nameFile = "jz_people.txt";
    private static String summaryFile = "jz_summary.txt";

    private static Map<Integer /*ID*/, String /*name*/> idToNameMap = new HashMap<Integer, String>();
    private static Map<Integer /*ID*/, Integer /*Money*/> idToMoneyMap = new HashMap<Integer, Integer>();

    public static String getPeopleIdList() {
        StringBuilder sb = new StringBuilder();

        File file = new File(nameFile);

        int i = 1;
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                try {
                    //循环，每次读一行
                    while ((line = reader.readLine()) != null) {
                        idToNameMap.put(i, line);

                        sb.append("ID: " + i);
                        sb.append(line);
                        sb.append("\n");
                        i++;
                    }
                    reader.close();
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getHistory() {
        StringBuilder sb = new StringBuilder();
        String se = "\\|";

        //read file and write into wordsList
        File file = new File(summaryFile);

        int i = 1;
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line, lastLine =null;
                try {
                    //循环，每次读一行
                    while ((line = reader.readLine()) != null) {
                        lastLine = line;
                    }

                    if (null !=lastLine) {
                        String[] lastLines = lastLine.split(se);
                        for (String s : lastLines) {
                            String[] idAndMonry = s.split(" ");
                            idToMoneyMap.put(Integer.valueOf(idAndMonry[0]),
                                    Integer.valueOf(idAndMonry[1]));
                        }
                    }

                    for (Map.Entry<Integer, Integer> entry : idToMoneyMap.entrySet()) {
                        int id = entry.getKey();
                        int money = entry.getValue();
                        sb.append(idToNameMap.get(id));
                        sb.append(": " );
                        sb.append(money).append("\n");
                    }

                    reader.close();
                    read.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public void writeToHistory() {

    }
}
