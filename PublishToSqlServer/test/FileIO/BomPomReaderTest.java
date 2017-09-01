package FileIO;

import com.intellij.openapi.vfs.VirtualFile;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class BomPomReaderTest {
    @Test
    public void readLines() throws Exception {
        BomPomReader bomPomReader = new BomPomReader(null);
        VirtualFile fileMock = mock(VirtualFile.class);
        when(fileMock.getInputStream()).thenReturn(new FileInputStream("testRessources/FileIO/SqlReadLinesFile.sql"));
        List<String> s = bomPomReader.readLines(fileMock);
        for (int i = 0; i < s.size(); i++) {
            Assert.assertEquals(s.get(i), getExpectedString().get(i));
        }
    }

    private List<String> getExpectedString() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("CREATE PROCEDURE [dbo].[GetTaskRedirectionByAutomaticTaskId]");

        strings.add("\t@AutomaticTaskId nvarchar(100)");
        strings.add("AS");
        strings.add("\tSELECT Id, AutomaticTaskId, WindowsAccount, OriginalEmployeeWindowsAccount, IsDeleted, RowVersion");
        strings.add("\tFROM [dbo].[AutogeneratedTaskRedirections]");
        strings.add("RETURN 0");
        return strings;
    }

    @Test
    public void readContent() throws Exception {
        BomPomReader bomPomReader = new BomPomReader(null);
        VirtualFile fileMock = mock(VirtualFile.class);
        when(fileMock.getInputStream()).thenReturn(new FileInputStream("testRessources/FileIO/SqlReadStringsFile.sql"));
        String s = bomPomReader.readContent(fileMock);
        Assert.assertEquals("CREATE PROCEDURE [dbo].[GetTaskRedirectionByAutomaticTaskId]\r\n" +
                "\t@AutomaticTaskId nvarchar(100)\r\n" +
                "AS\r\n" +
                "\tSELECT Id\r\n" +
                "\tFROM [dbo].[AutogeneratedTaskRedirections]\r\n" +
                "\tWHERE AutomaticTaskId = @AutomaticTaskId\r\n" +
                "\tAND ISNULL([IsDeleted], 1) = 0\r\n" +
                "RETURN 0\r\n", s);

    }

}