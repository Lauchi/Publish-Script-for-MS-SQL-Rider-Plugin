package Repository;

import ErrorHandling.ErrorInvoker;
import FileIO.BomPomReader;
import FileIO.DatabaseFileManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TableRepository {
    private JSqlParser jSqlParser;
    private BomPomReader bomPomReader;
    private ErrorInvoker errorInvoker;
    private DatabaseFileManager databaseFileManager;
    private List<String> filesThatCouldNotBeParsed;

    public TableRepository(JSqlParser jSqlParser, BomPomReader bomPomReader, ErrorInvoker errorInvoker, DatabaseFileManager databaseFileManager) {
        this.jSqlParser = jSqlParser;
        this.bomPomReader = bomPomReader;
        this.errorInvoker = errorInvoker;
        this.databaseFileManager = databaseFileManager;
        filesThatCouldNotBeParsed = new ArrayList<>();
    }

    public ArrayList<Statement> getDatabaseTables(final VirtualDirectoryImpl folder, ArrayList<VirtualFile> ignoredFiles) {
        ArrayList<VirtualFile> sqlFiles = databaseFileManager.getSqlFiles(folder);
        sqlFiles.removeAll(ignoredFiles);
        ArrayList<Statement> databaseTables = new ArrayList<>();
        for (VirtualFile databaseTableFile : sqlFiles) {
            InputStreamReader fisWithoutBoms = bomPomReader.getInputStream(databaseTableFile);
            Statement table = parseFileToTable(fisWithoutBoms, databaseTableFile.getName());
            if (table != null) databaseTables.add(table);
        }
        if (filesThatCouldNotBeParsed.size() > 0) errorInvoker.ShowParseErrorForFiles(filesThatCouldNotBeParsed);
        return databaseTables;
    }

    private Statement parseFileToTable(InputStreamReader fisWithoutBoms, String name)  {
        try {
            return jSqlParser.parse(fisWithoutBoms);
        } catch (JSQLParserException e) {
            filesThatCouldNotBeParsed.add(name);
            return null;
        }
    }
}
