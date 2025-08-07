package Control;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/** DAO לפעולות על טבלת Item */
public class ItemDao {

    /** מחזיר מפה: itemTypeId → כמות פריטים שנצרכו בטווח התאריכים */
    public Map<Long, Integer> countUsedBetween(LocalDate from, LocalDate to) throws SQLException {
        final String sql = """
                SELECT itemTypeId, COUNT(*) AS usedQty
                FROM Items
                WHERE status = 'USED'
                  AND usageDate BETWEEN ? AND ?
                GROUP BY itemTypeId
                """;

        Map<Long, Integer> result = new HashMap<>();

        try (Connection c = Db.get();
             PreparedStatement pst = c.prepareStatement(sql)) {

            pst.setDate(1, Date.valueOf(from));
            pst.setDate(2, Date.valueOf(to));

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getLong("itemTypeId"), rs.getInt("usedQty"));
                }
            }
        }
        return result;
    }
}