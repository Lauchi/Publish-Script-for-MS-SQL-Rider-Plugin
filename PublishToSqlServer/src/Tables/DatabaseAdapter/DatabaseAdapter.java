package Tables.DatabaseAdapter;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    public CreateTable getTable(String tableName, Connection connection) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + connection.getCatalog()
                    +  ".INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            String tableNameClean = tableName.replace("[", "").replace("]", "");
            statement.setString(1, tableNameClean);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            resultSet.beforeFirst();
            List<ColumnDefinition> columnList = new ArrayList<>();
            while (resultSet.next()) {
                String columnName = resultSet.getString("COLUMN_NAME");
                String dataType = resultSet.getString("DATA_TYPE");
                int maxLength = resultSet.getInt("CHARACTER_MAXIMUM_LENGTH");
                boolean isNullable = resultSet.getBoolean("IS_NULLABLE");
                ColumnDefinition column = new ColumnDefinition();
                column.setColumnName(columnName);
                List<String> specList = new ArrayList<>();
                specList.add("(" + maxLength + ")");
                specList.add(parseIsNull(isNullable));
                column.setColumnSpecStrings(specList);
                ColDataType type = new ColDataType();
                type.setDataType(dataType);
                column.setColDataType(type);
                columnList.add(column);
            }
            CreateTable createTable = new CreateTable();
            createTable.setColumnDefinitions(columnList);
            Table table = new Table();
            table.setName(tableName);
            // TODO präfix dran table.setSchemaName(connection.getCatalog());
            createTable.setTable(table);
            return createTable;
        } catch (SQLException e) {
            return null;
        }
    }

    private String parseIsNull(boolean isNullable) {
        if (isNullable) return "NULL";
        return "NOT NULL";
    }
}
