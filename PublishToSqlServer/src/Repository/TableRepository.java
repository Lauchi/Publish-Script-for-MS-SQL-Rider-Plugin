package Repository;

import FileIO.BomPomReader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.io.InputStreamReader;
import java.util.ArrayList;

import Utils.Utils;

public class TableRepository {
    private JSqlParser jSqlParser;
    private BomPomReader bomPomReader;

    public TableRepository(JSqlParser jSqlParser, BomPomReader bomPomReader) {
        this.jSqlParser = jSqlParser;
        this.bomPomReader = bomPomReader;
    }

    public ArrayList<Statement> getDatabaseTables(final VirtualDirectoryImpl folder) {
        ArrayList<Statement> databaseTables = new ArrayList<>();
        for (final VirtualFile fileEntry : folder.getChildren()) {
            if (fileEntry instanceof VirtualDirectoryImpl) {
                databaseTables.addAll(getDatabaseTables((VirtualDirectoryImpl) fileEntry));
            } else {
                String extension = Utils.getFileExtension(fileEntry);
                if (extension.equals("sql")) {
                    InputStreamReader fisWithoutBoms = bomPomReader.getInputStream(fileEntry);
                    Statement table = parseFileToTable(fisWithoutBoms);
                    if (table != null) databaseTables.add(table);
                }
            }
        }
        return databaseTables;
    }

    private Statement parseFileToTable(InputStreamReader fisWithoutBoms)  {
        try {
            return jSqlParser.parse(fisWithoutBoms);
        } catch (JSQLParserException e) {
            // Todo: log error for not parsed sql files and exclude procedures from list (used libray can not parse procedures)
            return null;
        }
    }
}
