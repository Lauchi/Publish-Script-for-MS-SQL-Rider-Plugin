import Domain.SQLFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.jetbrains.rider.projectView.solutionExplorer.SolutionExplorerNodeRider;
import ErrorHandling.ErrorInvoker;
import FileIO.BomPomReader;
import net.pempek.unicode.UnicodeBOMInputStream;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CreatePublishScriptHandler extends AnAction {
    private JSqlParser jSqlParser;
    private BomPomReader bomPomReader;
    private ErrorInvoker errorInvoker;
    private String publishFailedTitle = "Create Publish Script Failed";

    public CreatePublishScriptHandler() {
        super("Create _Publish _Script _Handler");
    }

    public void actionPerformed(AnActionEvent event) {
        errorInvoker = new ErrorInvoker();
        bomPomReader = new BomPomReader(errorInvoker);
        jSqlParser = new CCJSqlParserManager();
        final VirtualDirectoryImpl databaseFolder = getDatabaseFolder(event);

        String CreateTAbles = null;
        try {
            ArrayList<Statement> sqlCreateTableFiles = getSqlCreateTableFiles(databaseFolder);
            for(Statement statement : sqlCreateTableFiles) {
                CreateTAbles += statement.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<SQLFile> procedureFiles = getCreateProcedureFiles(databaseFolder);
        List<SQLFile> modifiedSQLFiles = getSqlFilesUpdated(procedureFiles);
        SQLFile publishScript = createPublishScript(modifiedSQLFiles);

        String publishScriptLocation = getPublishScriptLocation(event).getAbsolutePath() + "\\publishScript.sql";
        saveSqlFile(publishScript, publishScriptLocation);
        openSqlFileInEditor(event, databaseFolder, publishScriptLocation);
    }

    private void openSqlFileInEditor(AnActionEvent event, VirtualDirectoryImpl folder, String publishScriptLocation) {
        folder.refresh(false,true);
        FileEditorManager manager;
        manager = FileEditorManager.getInstance(event.getProject());
        VirtualFile refreshedFile = LocalFileSystem.getInstance().findFileByPath(publishScriptLocation);
        manager.openFile(refreshedFile, true);
    }

    private List<SQLFile> getSqlFilesUpdated(List<SQLFile> sqlFiles) {
        try {
            return ParseProcedureFilesToUpdate(sqlFiles);
        } catch (ParseException e) {
            Messages.showErrorDialog("Could not parse sql file", publishFailedTitle);
            return null;
        }
    }

    private void saveSqlFile(SQLFile publishScript, String location) {
        try {
            PrintWriter writer = new PrintWriter(location, "Unicode");
            for (String line : publishScript.getSqlContent()) {
                writer.println(line);
            }
            writer.close();
        } catch (IOException e) {
            Messages.showErrorDialog("Could not save publish script", publishFailedTitle);
        }
    }

    private SQLFile createPublishScript(List<SQLFile> modifiedSQLFiles) {
        List<String> sqlStatements = new ArrayList<>();

        for (SQLFile sqlFile : modifiedSQLFiles) {
            sqlStatements.addAll(sqlFile.getSqlContent());
        }
        return new SQLFile(sqlStatements);
    }

    private List<SQLFile> ParseProcedureFilesToUpdate(List<SQLFile> sqlFiles) throws ParseException {
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

    @NotNull
    private VirtualDirectoryImpl getDatabaseFolder(AnActionEvent event) {
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

    @Override
    public void update(AnActionEvent event) {
        Presentation presentation = event.getPresentation();

        Object project = event.getData(PlatformDataKeys.SELECTED_ITEM);
        if (project instanceof SolutionExplorerNodeRider) {
            SolutionExplorerNodeRider node = (SolutionExplorerNodeRider) project;
            VirtualFile virtualFile = node.getVirtualFile();
            String extension = virtualFile.getExtension();
            if (extension != null && extension.equals("sqlproj"))
            {
                show(presentation);
                return;
            }
        }
        hide(presentation);
    }

    private static void show(Presentation presentation) {
        presentation.setEnabled(true);
        presentation.setVisible(true);
    }

    private static void hide(Presentation presentation) {
        presentation.setEnabled(false);
        presentation.setVisible(false);
    }

    private File getPublishScriptLocation(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        String projectFilePath = project.getBasePath();
        String dataBaseProject = projectFilePath + "/Database";

        return new File(dataBaseProject);
    }

    public ArrayList<Statement> getSqlCreateTableFiles(final VirtualDirectoryImpl folder) throws IOException {
        ArrayList<Statement> sqlFiles = new ArrayList<>();
        for (final VirtualFile fileEntry : folder.getChildren()) {
            if (fileEntry instanceof VirtualDirectoryImpl) {
                sqlFiles.addAll(getSqlCreateTableFiles((VirtualDirectoryImpl) fileEntry));
            } else {
                String extension = getFileExtension(fileEntry);
                if (extension.equals("sql")) {
                    UnicodeBOMInputStream unicodeBOMInputStream = new UnicodeBOMInputStream(fileEntry.getInputStream());
                    unicodeBOMInputStream.skipBOM();
                    InputStreamReader fisWithoutBoms = new InputStreamReader(unicodeBOMInputStream);
                    try {
                        Statement parse = jSqlParser.parse(fisWithoutBoms);
                        sqlFiles.add(parse);
                    } catch (JSQLParserException e) {
                        return sqlFiles;
                    }
                }
            }
        }
        return sqlFiles;
    }

    public ArrayList<SQLFile> getCreateProcedureFiles(final VirtualDirectoryImpl folder) {
        ArrayList<SQLFile> sqlFiles = new ArrayList<>();
        for (final VirtualFile fileEntry : folder.getChildren()) {
            if (fileEntry instanceof VirtualDirectoryImpl) {
                sqlFiles.addAll(getCreateProcedureFiles((VirtualDirectoryImpl) fileEntry));
            } else {
                String extension = getFileExtension(fileEntry);
                if (extension.equals(SQLFile.EXTENSION)) {
                    List<String> sqlContent = bomPomReader.readLines(fileEntry);
                    for(String sqlLine : sqlContent) {
                        if (sqlLine.toUpperCase().contains("CREATE PROCEDURE")) {
                            SQLFile sqlFile = new SQLFile(sqlContent);
                            sqlFiles.add(sqlFile);
                            break;
                        }
                    }

                }
            }
        }
        return sqlFiles;
    }

    private String getFileExtension(VirtualFile fileEntry) {
        String[] splits = fileEntry.getName().split("\\.");

        String extension = "";

        if (splits.length >= 2) {
            extension = splits[splits.length - 1];
        }
        return extension;
    }
}