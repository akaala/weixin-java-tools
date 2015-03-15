package me.chanjar.weixin.mp.demo;

import java.io.*;
import java.util.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class GameInfoCache {
    private static Map<Integer /*Room Number*/, GameInfo> gameMap = Maps.newHashMap();

    private static Map</*words for one game*/List<String>, /*words number*/Integer> wordsList = new
            HashMap<List<String>, Integer>();

    static {
        readWordsFile();
    }


    public static void main(String[] args) {
        readWordsFile();
        for (Map.Entry<List<String>, Integer> entry : wordsList.entrySet()) {
            System.out.println(entry.getValue() + "words:");
            for(String word: entry.getKey()) {
                System.out.println("\t"+word);
            }
        }
    }


    public static void readWordsFile() {
        String se = "\\|";
        //read file and write into wordsList
        File file = new File("words.txt");

        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;
                try {
                    //循环，每次读一行
                    while ((line = reader.readLine()) != null) {
                        String[] words = line.split(se);
                        wordsList.put(Arrays.asList(words), words.length);
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
    }

    /**
     *
     * @param playerNum
     * @return room number
     */
    public static int buildGame(int playerNum) {
        int randomRoomNum = new Random().nextInt(10000);

        GameInfo gameInfo = buildRandomGameInfo(playerNum);
        // todo: 更好的处理null的情况
        if (null !=gameInfo) {
            gameMap.put(randomRoomNum, buildRandomGameInfo(playerNum));
            return randomRoomNum;
        } else {
            return -1;
        }
    }

    private static GameInfo buildRandomGameInfo(int playerNum) {
        TreeMap<Integer, String> wordMap = new TreeMap<Integer, String>();
        ArrayList<String> words = new ArrayList<String>(filterWordsMap(playerNum));

        if (null != words && words.size()>1) {
            String scenario = words.remove(0);

            Collections.shuffle(words);
            for (int i = 1; i <= playerNum; i++) {
                wordMap.put(i, words.get(i - 1));
            }
            return new GameInfo(playerNum, scenario, wordMap);
        } else {
            return null;
        }
    }

    private static List<String> filterWordsMap(final int playerNum) {
        Map<List<String>, Integer> newWordList = Maps.filterValues(wordsList, new Predicate<Integer>() {
            @Override
            public boolean apply(Integer integer) {
                return integer>=playerNum + 1;
            }
        });

        List<List<String>> words = new ArrayList<List<String>>(newWordList.keySet());
        if (words.size() >0) {
            Collections.shuffle(words);
            return words.get(0);
        } else {
            return null;
        }
    }

    public static String getGameInfo(Integer roomNumber, Integer index) {
        if (gameMap.containsKey(roomNumber)) {
            String info = gameMap.get(roomNumber).getGameInfo(index);
            if (null != info) {
                return info;
            } else {
                return null;
            }
        } else {
           return null;
        }
    }

    public static Integer nextAvailableIndex(Integer roomNum) {
        if ( gameMap.containsKey(roomNum)) {
            GameInfo gameInfo = gameMap.get(roomNum);
            return gameInfo.getAvailableIndex();
        } else {
            return null;
        }
    }

    public static boolean validateRoomNum(Integer roomNum) {
        return gameMap.containsKey(roomNum);
    }

    private static class GameInfo {
        int playerNum;
        Map<Integer, String> wordMap;
        String scenario;

        protected GameInfo(int playerNum, String scenario, TreeMap<Integer/*player index*/, String/*word*/> map) {
            this.playerNum = playerNum;
            this.wordMap = map;
            this.scenario = scenario;
            for(int i = 2; i <= playerNum; i++) {
                availableIndexes.add(i);
            }
        }

        List<Integer> availableIndexes = new ArrayList<Integer>();

        public String getGameInfo(int index) {
            // 1 你是%d号
            // 2 场景信息
            // 3 其他player信息
            if (wordMap.size() > 0 && wordMap.containsKey(index)) {
                StringBuilder sb= new StringBuilder();
                sb.append("============\n");
                sb.append("你是").append(index).append("号，");
                sb.append("模拟的场景为：").append(scenario).append("\n");
                sb.append("其他人的词语是：\n");
                for (Integer i: wordMap.keySet()) {
                    if (i != index) {
                        sb.append(String.format("============\n" +
                                "%d号： %s\n", i, wordMap.get(i)));
                    }
                }
                return sb.toString();
            } else {
                return null;
            }
        }

        public Integer getAvailableIndex() {
            if (availableIndexes.size() >0) {
                int indexToPull = new Random().nextInt(availableIndexes.size());
                int returnIndex = availableIndexes.get(indexToPull);
                availableIndexes.remove(indexToPull);
                return returnIndex;
            } else {
                return null;
            }
        }
    }
}
