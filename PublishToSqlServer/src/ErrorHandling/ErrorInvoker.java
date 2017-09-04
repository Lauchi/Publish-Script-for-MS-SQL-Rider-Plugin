package ErrorHandling;
import com.intellij.openapi.ui.Messages;

import java.util.List;

public class ErrorInvoker {
    private String publishFailedTitle = "Create Publish Script Failed";
    private String warning = "Warning";

    public void ShowSQLFileNotReadableError(String fileEntry) {
        Messages.showErrorDialog("Could not read sql file: " + fileEntry, publishFailedTitle);
    }

    public void ShowPublishScriptSaveError() {
        Messages.showErrorDialog("Could not save publish script", publishFailedTitle);
    }

    public void ShowParseErrorForFiles(List<String> notParsedSqlFiles) {
        String sqlParsWarnings = "Could not parse SQL-Files: " + System.lineSeparator() + System.lineSeparator();
        for (String file : notParsedSqlFiles) {
            sqlParsWarnings += file + System.lineSeparator();
        }
        Messages.showWarningDialog(sqlParsWarnings, warning);
    }

    public void ShowConnectionStringError() {
        Messages.showErrorDialog("Could not connect to database, check connectionstring in publish.xml", publishFailedTitle);
    }
}
