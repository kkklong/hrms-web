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
}
