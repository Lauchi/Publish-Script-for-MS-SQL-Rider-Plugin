package ErrorHandling;
import com.intellij.openapi.ui.Messages;

public class ErrorInvoker {
    private String publishFailedTitle = "Create Publish Script Failed";

    public void ShowSQLFileNotReadableError(String fileEntry) {

        Messages.showErrorDialog("Could not read sql file: " + fileEntry, publishFailedTitle);
    }
}
