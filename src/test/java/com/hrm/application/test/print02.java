package com.hrm.application.test;

import org.junit.Test;

public class print02 {

    @Test
    public void Test0001(){
        String gameId = "205";
        String turnNum = "2025274";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            sb.append("DELETE FROM user_bet_")
                    .append(i)
                    .append(" WHERE game_id = '")
                    .append(gameId)
                    .append("' AND turn_num = '")
                    .append(turnNum)
                    .append("'");
            if (i < 255) { // 最後一個不用加 UNION ALL
                sb.append("\nUNION ALL\n");
            }
        }

        System.out.println(sb.toString());
    }

    @Test
    public void Test0002(){
        String addTime = "2025-08-01";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 256; i++) {
            sb.append("DELETE FROM user_bill_")
                    .append(i)
                    .append(" WHERE add_time < '")
                    .append(addTime)
                    .append("'")
                    .append(";\n");
//            if (i < 255) { // 最後一個不用加 UNION ALL
//                sb.append("\nUNION ALL\n");
//            }
        }

        System.out.println(sb.toString());
    }

    @Test
    public void Test0003(){
        String s = "1519\n" +
                "1agm\n" +
                "1ayl\n" +
                "1gyh\n" +
                "1jlc\n" +
                "1mpj\n" +
                "1myh\n" +
                "1yfc\n" +
                "2cyh\n" +
                "2eyh\n" +
                "2gyh\n" +
                "2hgf\n" +
                "2hgh\n" +
                "2lpj\n" +
                "33cp\n" +
                "3eyh\n" +
                "3gyh\n" +
                "3kws\n" +
                "3mgm\n" +
                "5000\n" +
                "5bjs\n" +
                "5fjs\n" +
                "5jpj\n" +
                "5lpj\n" +
                "63cp\n" +
                "66cp\n" +
                "6ajs\n" +
                "6dpj\n" +
                "6hgc\n" +
                "6lpj\n" +
                "6mpj\n" +
                "7cpj\n" +
                "7djs\n" +
                "7hgg\n" +
                "7lpj\n" +
                "7mpj\n" +
                "888c\n" +
                "8bjs\n" +
                "8cyh\n" +
                "8dws\n" +
                "8gws\n" +
                "8hpj\n" +
                "8lws\n" +
                "9hgc\n" +
                "9jws\n" +
                "9lpj\n" +
                "aofa\n" +
                "awan\n" +
                "betdc\n" +
                "bwzz\n" +
                "cai7\n" +
                "cai8\n" +
                "cjs\n" +
                "cmpj\n" +
                "cpj\n" +
                "dmpj\n" +
                "hgwa\n" +
                "hgwb\n" +
                "hhws\n" +
                "jinc\n" +
                "msmh\n" +
                "vtyc\n" +
                "ymgm\n" +
                "zwnx\n";

        String s1 = s.replace(" ","\":\"").replace("\n","\",\"");
        //  String s2 = s1.replace("\n","\'\n");
        System.out.printf(s1);
    }
}
