package Procedures;

import Domain.SQLFile;
import FileIO.BomPomReader;
import Utils.Utils;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;

import java.util.ArrayList;
import java.util.List;

public class ProcedureRepository {

    private BomPomReader bomPomReader;

    public ProcedureRepository(BomPomReader bomPomReader) {

        this.bomPomReader = bomPomReader;
    }

    public ArrayList<SQLFile> getDatabaseProcedures(final VirtualDirectoryImpl folder) {
        ArrayList<SQLFile> procedures = new ArrayList<>();
        for (final VirtualFile fileEntry : folder.getChildren()) {
            if (fileEntry instanceof VirtualDirectoryImpl) {
                procedures.addAll(getDatabaseProcedures((VirtualDirectoryImpl) fileEntry));
            } else {
                String extension = Utils.getFileExtension(fileEntry);
                if (extension.equals(SQLFile.EXTENSION)) {
                    List<String> sqlContent = bomPomReader.readLines(fileEntry);
                    for(String sqlLine : sqlContent) {
                        if (sqlLine.toUpperCase().contains("CREATE PROCEDURE")) {
                            SQLFile procedure = new SQLFile(sqlContent);
                            procedures.add(procedure);
                            break;
                        }
                    }

                }
            }
        }
        return procedures;
    }
}
