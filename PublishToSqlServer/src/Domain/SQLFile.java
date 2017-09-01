package Domain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sheiss on 16/07/2017.
 */
public class SQLFile {
    public static final String EXTENSION = "sql";
    private List<String> sqlContent;

    public SQLFile(List<String> sqlContent) {
        List<String> strings = new ArrayList<>();
        strings.addAll(sqlContent);
        this.sqlContent = strings;
    }

    public List<String> getSqlContent() {
        List<String> strings = new ArrayList<>();
        strings.addAll(sqlContent);
        return strings;
    }

    public String getProcedureName() {
        for (String line : getSqlContent()) {
            if (line.toUpperCase().contains("CREATE PROCEDURE")) {
                String splitterValue = "PROCEDURE";
                String modified = Pattern.compile(splitterValue, Pattern.CASE_INSENSITIVE).matcher(line).replaceAll(splitterValue);
                return modified.split(splitterValue)[1].trim();
            }
        }
        return null;
    }
}
