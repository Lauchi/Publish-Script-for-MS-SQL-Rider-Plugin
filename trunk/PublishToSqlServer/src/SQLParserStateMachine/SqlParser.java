package SQLParserStateMachine;

import Domain.SQLCommand;
import FileIO.BomPomReader;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.List;

import static com.intellij.icons.AllIcons.FileTypes.AS;

public class SqlParser {
    private BomPomReader fileReader;

    public SqlParser(BomPomReader fileReader) {
        this.fileReader = fileReader;
    }

    public SQLCommand parseFileToSql(VirtualFile file) {
        List<String> strings = fileReader.readLines(file);
        SQLParseState state = new ParseStart();
        for (String line : strings) {

        }
        return null;
    }
}
