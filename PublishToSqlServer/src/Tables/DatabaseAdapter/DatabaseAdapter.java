package Tables.DatabaseAdapter;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    public Table getTable(String tableName, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + connection.getCatalog()
                    +  ".INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?");
            String tableNameClean = tableName.replace("[", "").replace("]", "");
            statement.setString(1, tableNameClean);
            ResultSet resultSet = statement.executeQuery();
            List<Column> columnList = new ArrayList<>();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("DATA_TYPE");
                int maxLength = resultSet.getInt("CHARACTER_MAXIMUM_LENGTH");
                boolean isNullable = resultSet.getBoolean("IS_NULLABLE");
                Column column = new Column();
                column.setColumnName(columnName);
                columnList.add(column);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
