package Tables.DatabaseAdapter;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseAdapter {
    public Table getTable(String tableName, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM ?.INFORMATION_SCHEMA.COLUMNS  WHERE TABLE_NAME = N'?'");
            statement.setString(0, connection.getCatalog());
            statement.setString(1, tableName);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("DATA_TYPE");
                int maxLength = resultSet.getInt("CHARACTER_MAXIMUM_LENGTH");
                boolean isNullable = resultSet.getBoolean("IS_NULLABLE");
                Column column = new Column();
                column.setColumnName(columnName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
