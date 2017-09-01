package FileIO;

import com.intellij.openapi.vfs.VirtualFile;
import ErrorHandling.ErrorInvoker;
import net.pempek.unicode.UnicodeBOMInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BomPomReader {
    private ErrorInvoker errorInvoker;

    public BomPomReader(ErrorInvoker errorInvoker) {
        this.errorInvoker = errorInvoker;
    }

    public List<String> readLines(VirtualFile fileEntry) {
        try {
            InputStream fileInputStream = fileEntry.getInputStream();
            UnicodeBOMInputStream unicodeBOMInputStream = new UnicodeBOMInputStream(fileInputStream);
            unicodeBOMInputStream.skipBOM();
            InputStreamReader fisWithpoutBoms = new InputStreamReader(unicodeBOMInputStream);
            BufferedReader br = new BufferedReader(fisWithpoutBoms);

            String line;
            List<String> lines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            return lines;
        } catch (IOException e) {
            errorInvoker.ShowSQLFileNotReadableError(fileEntry.getName());
            return null;
        }
    }

    public String readContent(VirtualFile fileEntry) {
        try {
            InputStream fileInputStream = fileEntry.getInputStream();
            UnicodeBOMInputStream unicodeBOMInputStream = new UnicodeBOMInputStream(fileInputStream);
            unicodeBOMInputStream.skipBOM();
            InputStreamReader fisWithpoutBoms = new InputStreamReader(unicodeBOMInputStream);
            BufferedReader br = new BufferedReader(fisWithpoutBoms);

            String         line = null;
            StringBuilder  stringBuilder = new StringBuilder();
            String         ls = System.getProperty("line.separator");

            try {
                while((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(ls);
                }

                return stringBuilder.toString();
            } finally {
                br.close();
            }
        } catch (IOException e) {
            errorInvoker.ShowSQLFileNotReadableError(fileEntry.getName());
            return null;
        }
    }
}
