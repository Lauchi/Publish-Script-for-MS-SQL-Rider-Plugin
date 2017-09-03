package Procedures;

import Domain.SQLFile;
import FileIO.BomPomReader;
import FileIO.DatabaseFileManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
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


    public List<SQLFile> getDatabaseProcedures(VirtualDirectoryImpl databaseFolder) {
        List<VirtualFile> procedureFiles = databaseFileManager.getProcedureFiles(databaseFolder);
        List<SQLFile> procedures = new ArrayList<>();
        for (VirtualFile file : procedureFiles) {
            List<String> sqlContent = bomPomReader.readLines(file);
            procedures.add(new SQLFile(sqlContent));
        }
        return procedures;
    }
}
