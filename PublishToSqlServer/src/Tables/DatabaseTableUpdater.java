package Tables;

import Domain.SQLFile;
import Tables.DatabaseAdapter.DatabaseAdapter;
import net.sf.jsqlparser.schema.Database;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.sql.Connection;
import java.util.List;

public class DatabaseTableUpdater {
    private DatabaseAdapter databaseAdapter;

    public DatabaseTableUpdater(DatabaseAdapter databaseAdapter){

        this.databaseAdapter = databaseAdapter;
    }
    public List<SQLFile> getTableFilesUpdated(List<Statement> databaseTableFiles, Connection connectionString) {
        for (Statement statement : databaseTableFiles) {
            if (statement instanceof CreateTable) {
                CreateTable table = (CreateTable) statement;
                List<ColumnDefinition> columnDefinitions = table.getColumnDefinitions();
                String name = table.getTable().getName();
                Table tableUpdater = databaseAdapter.getTable(name, connectionString);
            }
        }
        return null;
    }
}
