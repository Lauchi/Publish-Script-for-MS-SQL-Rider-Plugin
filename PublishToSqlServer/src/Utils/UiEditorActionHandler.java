package Utils;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.VirtualDirectoryImpl;
import com.jetbrains.rider.projectView.solutionExplorer.SolutionExplorerNodeRider;

public class UiEditorActionHandler {

    public void openSqlFileInEditor(AnActionEvent event, VirtualDirectoryImpl folder, String publishScriptLocation) {
        folder.refresh(false,true);
        FileEditorManager manager;
        manager = FileEditorManager.getInstance(event.getProject());
        VirtualFile refreshedFile = LocalFileSystem.getInstance().findFileByPath(publishScriptLocation);
        manager.openFile(refreshedFile, true);
    }

    public void showOptionInDialogIfUserClickedOnDatabaseProject(AnActionEvent event) {
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

    private void show(Presentation presentation) {
        presentation.setEnabled(true);
        presentation.setVisible(true);
    }

    private void hide(Presentation presentation) {
        presentation.setEnabled(false);
        presentation.setVisible(false);
    }
}
