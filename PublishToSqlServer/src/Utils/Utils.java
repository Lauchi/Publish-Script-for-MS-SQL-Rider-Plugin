package Utils;

import com.intellij.openapi.vfs.VirtualFile;

public class Utils {
    private Utils() {
        //static class
    }

    public static String getFileExtension(VirtualFile fileEntry) {
        String[] splits = fileEntry.getName().split("\\.");

        String extension = "";

        if (splits.length >= 2) {
            extension = splits[splits.length - 1];
        }
        return extension;
    }
}
