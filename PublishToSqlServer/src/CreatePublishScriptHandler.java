import FileIO.DatabaseFileManager;
import Procedures.ProcedureUpdater;
import Procedures.ProcedureRepository;
import Domain.SQLFile;
import Repository.TableRepository;
import Utils.UiEditorActionHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import ErrorHandling.ErrorInvoker;
import FileIO.BomPomReader;
import com.intellij.openapi.vfs.newvfs.impl.VirtualFileImpl;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CreatePublishScriptHandler extends AnAction {
    private TableRepository tableRepository;
    private ProcedureRepository procedureRepository;
    private ProcedureUpdater procedureUpdater;
    private DatabaseFileManager databaseFileManager;
    private UiEditorActionHandler uiEditorHandler;
    private String publishScriptLocation;

    public CreatePublishScriptHandler() {
        super("Create _Publish _Script _Handler");
    }

    public void actionPerformed(AnActionEvent event) {
        createDIYContainer(event);

        final VirtualDirectoryImpl databaseFolder = databaseFileManager.getDatabaseFolder(event);

        // Procedures
        List<SQLFile> procedureFiles = procedureRepository.getDatabaseProcedures(databaseFolder);
        List<SQLFile> modifiedProcedures = procedureUpdater.getSqlFilesUpdated(procedureFiles);

        //Tables
        List<VirtualFile> ignoredFiles = new ArrayList<>();
        ignoredFiles.add(LocalFileSystem.getInstance().findFileByPath(publishScriptLocation));
        ignoredFiles.addAll(databaseFileManager.getProcedureFiles(databaseFolder));
        List<Statement> databaseTableFiles = tableRepository.getDatabaseTables(databaseFolder, ignoredFiles);

        for(Statement statement : databaseTableFiles) {
            statement.toString();
            //TODO update sql tables here somehow
        }

        //Save Publish Script
        SQLFile publishScript = databaseFileManager.createPublishScript(modifiedProcedures);
        databaseFileManager.saveSqlFile(publishScript, publishScriptLocation);
        uiEditorHandler.openSqlFileInEditor(event, databaseFolder, publishScriptLocation);
    }

    private void createDIYContainer(AnActionEvent event) {
        ErrorInvoker errorInvoker = new ErrorInvoker();
        BomPomReader bomPomReader = new BomPomReader(errorInvoker);
        JSqlParser jSqlParser = new CCJSqlParserManager();
        procedureUpdater = new ProcedureUpdater();
        databaseFileManager = new DatabaseFileManager(errorInvoker, bomPomReader);
        procedureRepository = new ProcedureRepository(bomPomReader, databaseFileManager);
        tableRepository = new TableRepository(jSqlParser, bomPomReader, errorInvoker, databaseFileManager);
        uiEditorHandler = new UiEditorActionHandler();
        publishScriptLocation = databaseFileManager.getPublishScriptLocation(event) + "\\publishScript.sql";
    }

    @Override
    public void update(AnActionEvent event) {
        UiEditorActionHandler uiEditorHandler = new UiEditorActionHandler();
        uiEditorHandler.showOptionInDialogIfUserClickedOnDatabaseProject(event);
    }
}