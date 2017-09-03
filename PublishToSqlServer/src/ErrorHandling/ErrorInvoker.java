package ErrorHandling;
import com.intellij.openapi.ui.Messages;

public class ErrorInvoker {
    private String publishFailedTitle = "Create Publish Script Failed";

    public void ShowSQLFileNotReadableError(String fileEntry) {
        Messages.showErrorDialog("Could not read sql file: " + fileEntry, publishFailedTitle);
    }

    public void ShowSQLFileNotParsableError(String fileEntry) {
        Messages.showErrorDialog("Could not parse sql file: " + fileEntry, publishFailedTitle);
    }

    public void ShowPublishSciptSaveError() {
        Messages.showErrorDialog("Could not save publish script", publishFailedTitle);
    }
}
