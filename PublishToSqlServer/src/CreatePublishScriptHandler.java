import FileIO.DatabaseFileManager;
import Procedures.ProcedureUpdater;
import Procedures.ProcedureRepository;
import Domain.SQLFile;
import Repository.TableRepository;
import Utils.UiEditorActionHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import ErrorHandling.ErrorInvoker;
import FileIO.BomPomReader;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JSqlParser;
import net.sf.jsqlparser.statement.Statement;

import java.util.ArrayList;
import java.util.List;

public class CreatePublishScriptHandler extends AnAction {
    private JSqlParser jSqlParser;
    private TableRepository tableRepository;
    private BomPomReader bomPomReader;
    private ErrorInvoker errorInvoker;
    private ProcedureRepository procedureRepository;
    private ProcedureUpdater procedureUpdater;
    private DatabaseFileManager databaseFileManager;
    private UiEditorActionHandler uiEditorHandler;

    public CreatePublishScriptHandler() {
        super("Create _Publish _Script _Handler");
    }

    public void actionPerformed(AnActionEvent event) {
        createDIYContainer();

        final VirtualDirectoryImpl databaseFolder = databaseFileManager.getDatabaseFolder(event);

        String CreateTables = null;
        ArrayList<Statement> sqlCreateTableFiles = tableRepository.getDatabaseTables(databaseFolder);
        for(Statement statement : sqlCreateTableFiles) {
            CreateTables += statement.toString();
        }
        //TODO use tables

        ArrayList<SQLFile> procedureFiles = procedureRepository.getDatabaseProcedures(databaseFolder);
        List<SQLFile> modifiedSQLFiles = procedureUpdater.getSqlFilesUpdated(procedureFiles);

        SQLFile publishScript = databaseFileManager.createPublishScript(modifiedSQLFiles);

        String publishScriptLocation = databaseFileManager.getPublishScriptLocation(event).getAbsolutePath() + "\\publishScript.sql";
        databaseFileManager.saveSqlFile(publishScript, publishScriptLocation);
        uiEditorHandler.openSqlFileInEditor(event, databaseFolder, publishScriptLocation);
    }

    private void createDIYContainer() {
        errorInvoker = new ErrorInvoker();
        bomPomReader = new BomPomReader(errorInvoker);
        jSqlParser = new CCJSqlParserManager();
        tableRepository = new TableRepository(jSqlParser, bomPomReader);
        procedureRepository = new ProcedureRepository(bomPomReader);
        procedureUpdater = new ProcedureUpdater();
        databaseFileManager = new DatabaseFileManager(errorInvoker);
        uiEditorHandler = new UiEditorActionHandler();
    }

    @Override
    public void update(AnActionEvent event) {
        uiEditorHandler.showOptionInDialogIfUserClickedOnDatabaseProject(event);
    }
}