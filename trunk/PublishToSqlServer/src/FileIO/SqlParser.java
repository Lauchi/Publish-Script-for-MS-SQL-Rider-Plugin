package FileIO;

import Domain.SQLCommand;
import com.intellij.openapi.vfs.VirtualFile;

public class SqlParser {
    private BomPomReader fileReader;

    public SqlParser(BomPomReader fileReader) {
        this.fileReader = fileReader;
    }

    public SQLCommand parseFileToSql(VirtualFile file) {
        String strings = fileReader.readContent(file);
        return null;
    }
}
