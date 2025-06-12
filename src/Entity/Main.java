package Entity;

import java.time.LocalDate;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("URL = " + Entity.Consts.CONN_STR);
        Map<Long,Integer> res =
            new Control.ItemDao().countUsedBetween(
                  LocalDate.parse("2025-01-01"),
                  LocalDate.parse("2025-06-01"));
        System.out.println(res);
    }
}
