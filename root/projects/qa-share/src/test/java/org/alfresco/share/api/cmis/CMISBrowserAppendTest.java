package org.alfresco.share.api.cmis;

import java.io.File;
import java.io.RandomAccessFile;

import org.alfresco.share.enums.CMISBinding;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Class to include: Tests for CMIS Append for Browser binding
 * 
 * @author Abhijeet Bharade
 * 
 */
@Listeners(FailedTestListener.class)
@Test(groups = { "AlfrescoOne", "MyAlfresco" })
public class CMISBrowserAppendTest extends CMISAppendTest
{

    private static Log logger = LogFactory.getLog(CMISBrowserAppendTest.class);

    @BeforeClass(alwaysRun = true)
    public void beforeClass() throws Exception
    {
        try
        {
            super.beforeClass();
            binding = CMISBinding.BROWSER11;

            testName = this.getClass().getSimpleName();

            createTestData(testName);
        }
        catch (Throwable e)
        {
            reportError(drone, testName, e);
        }
    }

    @Test
    public void AONE_14489() throws Exception
    {
        String thisTestName = getTestName();
        createDocTest(thisTestName);
    }

    @Test
    public void AONE_14490() throws Exception
    {
        appendTest(drone, getTestName(), false, fileName);

    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14491() throws Exception
    {
        appendTest(drone, getTestName(), true, fileName);
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14492() throws Exception
    {
        appendSeveralChunksTest(drone, getFileName(getTestName()));
    }

    @Test(groups = {"IntermittentBugs"})
    public void AONE_14493() throws Exception
    {
        String file150mbName = "150MB_file"; // 150+150+150 = 450 MB;
        try
        {
            try
            {
                RandomAccessFile f = new RandomAccessFile(DATA_FOLDER + file150mbName, "rw");
                f.setLength(150000000);
            }
            catch (Exception e)
            {
                logger.error("Test File don't created.");
            }
            appendLargeChunksTest(drone, file150mbName);
        }
        finally
        {
            File f = new File(DATA_FOLDER + file150mbName);
            f.delete();
        }
    }
}
