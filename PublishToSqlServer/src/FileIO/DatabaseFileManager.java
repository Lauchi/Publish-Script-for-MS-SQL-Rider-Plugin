package FileIO;

import Domain.SQLFile;
import ErrorHandling.ErrorInvoker;
import Utils.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.jetbrains.rider.projectView.solutionExplorer.SolutionExplorerNodeRider;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFileManager {
    private ErrorInvoker errorInvoker;
    private BomPomReader bomPomReader;

    public DatabaseFileManager(ErrorInvoker errorInvoker, BomPomReader bomPomReader){
        this.errorInvoker = errorInvoker;
        this.bomPomReader = bomPomReader;
    }

    public void saveSqlFile(SQLFile publishScript, String location) {
        try {
            PrintWriter writer = new PrintWriter(location, "Unicode");
            for (String line : publishScript.getSqlContent()) {
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            errorInvoker.ShowPublishScriptSaveError();
        }
    }

    public List<VirtualFile> getSqlFiles(VirtualDirectoryImpl folder) {
        ArrayList<VirtualFile> sqlFiles = new ArrayList<>();
        for (final VirtualFile fileEntry : folder.getChildren()) {
            if (fileEntry instanceof VirtualDirectoryImpl) {
                sqlFiles.addAll(getSqlFiles((VirtualDirectoryImpl) fileEntry));
            } else {
                String extension = Utils.getFileExtension(fileEntry);
                if (extension.equals(SQLFile.EXTENSION)) {
                    sqlFiles.add(fileEntry);
                }
            }
        }
        return sqlFiles;
    }

    public SQLFile createPublishScript(List<SQLFile> modifiedSQLFiles) {
        List<String> sqlStatements = new ArrayList<>();

        for (SQLFile sqlFile : modifiedSQLFiles) {
            sqlStatements.addAll(sqlFile.getSqlContent());
        }
        return new SQLFile(sqlStatements);
    }

    public VirtualDirectoryImpl getDatabaseFolder(AnActionEvent event) {
        Object project = event.getData(PlatformDataKeys.SELECTED_ITEM);
        if (project instanceof SolutionExplorerNodeRider) {
            SolutionExplorerNodeRider node = (SolutionExplorerNodeRider) project;
            VirtualFile virtualFile = node.getVirtualFile().getParent();
            if (virtualFile instanceof VirtualDirectoryImpl) {
                return (VirtualDirectoryImpl) virtualFile;
            }
        }
        return null;
    }

    public VirtualFile getPublishScriptFolder(AnActionEvent event) {
        Object project = event.getData(PlatformDataKeys.SELECTED_ITEM);
        if (project instanceof SolutionExplorerNodeRider) {
            SolutionExplorerNodeRider node = (SolutionExplorerNodeRider) project;
            VirtualFile path = node.getVirtualFile().getParent();
            return path;
        }
        return null;
    }

    public List<VirtualFile> getProcedureFiles(final VirtualDirectoryImpl folder) {
        List<VirtualFile> sqlFiles = getSqlFiles(folder);
        List<VirtualFile> procedures = new ArrayList<>();
        for (VirtualFile file : sqlFiles) {
            List<String> sqlContent = bomPomReader.readLines(file);
            for (String sqlLine : sqlContent) {
                if (sqlLine.toUpperCase().contains("CREATE PROCEDURE")) {
                    procedures.add(file);
                    break;
                }
            }
        }

        return procedures;
    }
}
