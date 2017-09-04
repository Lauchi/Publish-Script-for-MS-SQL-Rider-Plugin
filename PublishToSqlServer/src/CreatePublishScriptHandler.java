import FileIO.DatabaseFileManager;
import Procedures.ProcedureUpdater;
import Procedures.ProcedureRepository;
import Domain.SQLFile;
import Repository.TableRepository;
import Tables.DatabaseAdapter.DatabaseAdapter;
import Tables.DatabaseTableUpdater;
import Utils.UiEditorActionHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import ErrorHandling.ErrorInvoker;
import FileIO.BomPomReader;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreatePublishScriptHandler extends AnAction {
    private TableRepository tableRepository;
    private ProcedureRepository procedureRepository;
    private ProcedureUpdater procedureUpdater;
    private DatabaseFileManager databaseFileManager;
    private UiEditorActionHandler uiEditorHandler;
    private String publishScriptLocation;
    private DatabaseTableUpdater tableUpdater;
    private Connection connection;
    private ErrorInvoker errorInvoker;

    public CreatePublishScriptHandler() {
        super("Create _Publish _Script _Handler");
    }

    public void actionPerformed(AnActionEvent event) {
        try {
            createDIYContainer(event);
        } catch (SQLException e) {
            errorInvoker.ShowConnectionStringError();
            return;
        } catch (ClassNotFoundException e) {
            errorInvoker.ShowConnectionStringError();
            return;
        }

        final VirtualDirectoryImpl databaseFolder = databaseFileManager.getDatabaseFolder(event);

        // Procedures
        List<SQLFile> procedureFiles = procedureRepository.getDatabaseProcedures(databaseFolder);
        List<SQLFile> modifiedProcedures = procedureUpdater.getSqlFilesUpdated(procedureFiles);

        //Tables
        List<VirtualFile> ignoredFiles = new ArrayList<>();
        ignoredFiles.add(LocalFileSystem.getInstance().findFileByPath(publishScriptLocation));
        ignoredFiles.addAll(databaseFileManager.getProcedureFiles(databaseFolder));
        List<Statement> databaseTableFiles = tableRepository.getDatabaseTables(databaseFolder, ignoredFiles);
        List<SQLFile> modifiedTables = tableUpdater.getTableFilesUpdated(databaseTableFiles, connection);

        //Save Publish Script
        List<SQLFile> modifiedSQLFiles = new ArrayList<>();
        modifiedSQLFiles.addAll(modifiedProcedures);
        modifiedSQLFiles.addAll(modifiedTables);

        SQLFile publishScript = databaseFileManager.createPublishScript(modifiedSQLFiles);
        databaseFileManager.saveSqlFile(publishScript, publishScriptLocation);
        uiEditorHandler.openSqlFileInEditor(event, databaseFolder, publishScriptLocation);
    }

    private void createDIYContainer(AnActionEvent event) throws SQLException, ClassNotFoundException {
        errorInvoker = new ErrorInvoker();
        BomPomReader bomPomReader = new BomPomReader(errorInvoker);
        JSqlParser jSqlParser = new CCJSqlParserManager();
        //Todo: get from publishscript.xml
        String url ="jdbc:sqlserver://LOCALHOST\\NEXUS;databaseName=test_sales_employeetaskautomationservice;integratedSecurity=true;";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connection = DriverManager.getConnection(url);
        procedureUpdater = new ProcedureUpdater();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter();
        tableUpdater = new DatabaseTableUpdater(databaseAdapter);
        databaseFileManager = new DatabaseFileManager(errorInvoker, bomPomReader);
        procedureRepository = new ProcedureRepository(bomPomReader, databaseFileManager);
        tableRepository = new TableRepository(jSqlParser, bomPomReader, errorInvoker, databaseFileManager);
        uiEditorHandler = new UiEditorActionHandler();
        publishScriptLocation = databaseFileManager.getPublishScriptFolder(event).getPath() + "\\publishScript.sql";
    }

    @Override
    public void update(AnActionEvent event) {
        UiEditorActionHandler uiEditorHandler = new UiEditorActionHandler();
        uiEditorHandler.showOptionInDialogIfUserClickedOnDatabaseProject(event);
    }
}