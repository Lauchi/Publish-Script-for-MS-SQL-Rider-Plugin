package Procedures;

import Domain.SQLFile;
import FileIO.BomPomReader;
import FileIO.DatabaseFileManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;

import java.util.ArrayList;
import java.util.List;

public class ProcedureRepository {

    private BomPomReader bomPomReader;
    private DatabaseFileManager databaseFileManager;

    public ProcedureRepository(BomPomReader bomPomReader, DatabaseFileManager databaseFileManager) {
        this.bomPomReader = bomPomReader;
        this.databaseFileManager = databaseFileManager;
    }

    public ArrayList<SQLFile> getDatabaseProcedures(final VirtualDirectoryImpl folder) {
        ArrayList<VirtualFile> sqlFiles = databaseFileManager.getSqlFiles(folder);
        ArrayList<SQLFile> procedures = new ArrayList<>();
        for (VirtualFile file : sqlFiles) {
            List<String> sqlContent = bomPomReader.readLines(file);
            for (String sqlLine : sqlContent) {
                if (sqlLine.toUpperCase().contains("CREATE PROCEDURE")) {
                    SQLFile procedure = new SQLFile(sqlContent);
                    procedures.add(procedure);
                    break;
                }
            }
        }

        return procedures;
    }
}
