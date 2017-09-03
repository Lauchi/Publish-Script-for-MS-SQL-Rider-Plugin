package Procedures;

import Domain.SQLFile;

import java.util.ArrayList;
import java.util.List;

public class ProcedureUpdater {

    public List<SQLFile> getSqlFilesUpdated(List<SQLFile> sqlFiles) {
        return ParseProcedureFilesToUpdate(sqlFiles);
    }

    private List<SQLFile> ParseProcedureFilesToUpdate(List<SQLFile> sqlFiles) {
        List<SQLFile> edditedtSqlFiles = new ArrayList<>();

        for (SQLFile sqlFile : sqlFiles) {
            SQLFile edditedSqlFile = replaceProcedureUpdate(sqlFile);
            edditedtSqlFiles.add(edditedSqlFile);
        }
        return edditedtSqlFiles;
    }

    private SQLFile replaceProcedureUpdate(SQLFile sqlFile) {
        List<String> sqlContentOld = sqlFile.getSqlContent();
        String procedureName = sqlFile.getProcedureName();
        String sqlReplaced = AlterIFExistsRoutine(procedureName);
        sqlContentOld.add(0, sqlReplaced);
        sqlContentOld.add(sqlContentOld.size(), "GO");
        return new SQLFile(sqlContentOld);
    }

    private String AlterIFExistsRoutine(String procedureName) {
        return "IF EXISTS ( SELECT * \n" +
                "            FROM   sysobjects \n" +
                "            WHERE  id = object_id(N'" + procedureName + "') \n" +
                "                   and OBJECTPROPERTY(id, N'IsProcedure') = 1 )\n" +
                "BEGIN\n" +
                "    DROP PROCEDURE " + procedureName + "\n" +
                "END\n" +
                "GO";
    }
}
