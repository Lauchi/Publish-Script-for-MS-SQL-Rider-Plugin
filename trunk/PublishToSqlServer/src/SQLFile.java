import java.util.ArrayList;
import java.util.List;

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
}
