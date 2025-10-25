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
        String s = "bwzz\n" +
                "3mgm\n" +
                "9hgc\n" +
                "aofa\n" +
                "7cpj\n" +
                "8lws\n" +
                "vtyc\n" +
                "5000\n" +
                "dmpj\n" +
                "hhws\n" +
                "cjs\n" +
                "1yfc\n" +
                "6hgc\n" +
                "cai8\n" +
                "1jlc\n" +
                "2hgf\n" +
                "7lpj\n" +
                "8bjs\n" +
                "8516\n" +
                "2hgh\n" +
                "6lpj\n" +
                "9lpj\n" +
                "1gyh\n" +
                "2cyh\n" +
                "hgwb\n";

        String s1 = s.replace(" ","\":\"").replace("\n","\",\"");
        //  String s2 = s1.replace("\n","\'\n");
        System.out.printf(s1);
    }
}
