package FileIO;

import Domain.SQLFile;
import ErrorHandling.ErrorInvoker;
import Utils.Utils;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.jetbrains.rider.projectView.solutionExplorer.SolutionExplorerNodeRider;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFileManager {
    private ErrorInvoker errorInvoker;

    public DatabaseFileManager(ErrorInvoker errorInvoker){
        this.errorInvoker = errorInvoker;
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

    public ArrayList<VirtualFile> getSqlFiles(VirtualDirectoryImpl folder) {
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

    public File getPublishScriptLocation(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String projectFilePath = project.getBasePath();
        String dataBaseProject = projectFilePath + "/Database";

        return new File(dataBaseProject);
    }
}
