package org.alfresco.repo.content.metadata;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.apache.tika.metadata.Metadata;


/**
 * @see DWGMetadataExtracter
 * 
 * @author Nick Burch
 */
public class DWGMetadataExtracterTest extends AbstractMetadataExtracterTest
{
    private DWGMetadataExtracter extracter;
    private static final QName TIKA_LAST_AUTHOR_TEST_PROPERTY =
       QName.createQName("TikaLastAuthorTestProp");
    private static final QName TIKA_CUSTOM_TEST_PROPERTY =
            QName.createQName("TikaCustomTestProp");
    private static final String TIKA_CUSTOM_KEY = "customprop1";

    @SuppressWarnings("deprecation")
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        extracter = new DWGMetadataExtracter();
        extracter.setDictionaryService(dictionaryService);
        extracter.register();
        
        // Attach some extra mappings, using the Tika
        //  metadata keys namespace
        // These will be tested later
        HashMap<String, Set<QName>> newMap = new HashMap<String, Set<QName>>(
              extracter.getMapping()
        );
        
        Set<QName> tlaSet = new HashSet<QName>();
        tlaSet.add(TIKA_LAST_AUTHOR_TEST_PROPERTY);
        Set<QName> custSet = new HashSet<QName>();
        custSet.add(TIKA_CUSTOM_TEST_PROPERTY);
        newMap.put( Metadata.LAST_AUTHOR, tlaSet );
        newMap.put( TIKA_CUSTOM_KEY, custSet );
        
        extracter.setMapping(newMap);
    }

    /**
     * @return Returns the same transformer regardless - it is allowed
     */
    protected MetadataExtracter getExtracter()
    {
        return extracter;
    }

    public void testSupports() throws Exception
    {
        for (String mimetype : DWGMetadataExtracter.SUPPORTED_MIMETYPES)
        {
            boolean supports = extracter.isSupported(mimetype);
            assertTrue("Mimetype should be supported: " + mimetype, supports);
        }
    }

    /**
     * Test all the supported files.
     * Note - doesn't use extractFromMimetype
     */
    public void testSupportedMimetypes() throws Exception
    {
        String mimetype = MimetypeMap.MIMETYPE_APP_DWG; 
           
        for (String version : new String[] {"2004","2007","2010"})
        {
           String filename = "quick" + version + ".dwg";
           URL url = AbstractContentTransformerTest.class.getClassLoader().getResource("quick/" + filename);
           File file = new File(url.getFile());

           Map<QName, Serializable> properties = extractFromFile(file, mimetype);
           
           // check we got something
           assertFalse("extractFromMimetype should return at least some properties, none found for " + mimetype,
              properties.isEmpty());
           
           // check common metadata
           testCommonMetadata(mimetype, properties);
           // check file-type specific metadata
           testFileSpecificMetadata(mimetype, properties);
        }
    }
    
    @Override
    protected boolean skipAuthorCheck(String mimetype) { return true; }

   /**
    * We also provide the creation date - check that
    */
   protected void testFileSpecificMetadata(String mimetype,
         Map<QName, Serializable> properties) 
   {
      // Check for extra fields
      assertEquals(
            "Property " + ContentModel.PROP_AUTHOR + " not found for mimetype " + mimetype,
            "Nevin Nollop",
            DefaultTypeConverter.INSTANCE.convert(String.class, properties.get(ContentModel.PROP_AUTHOR)));
      
      // Ensure that we can also get things which are standard
      //  Tika metadata properties, if we so choose to
      assertTrue( 
            "Test Property " + TIKA_LAST_AUTHOR_TEST_PROPERTY + " not found for mimetype " + mimetype,
            properties.containsKey(TIKA_LAST_AUTHOR_TEST_PROPERTY)
      );
      assertEquals(
            "Test Property " + TIKA_LAST_AUTHOR_TEST_PROPERTY + " incorrect for mimetype " + mimetype,
            "paolon",
            DefaultTypeConverter.INSTANCE.convert(String.class, properties.get(TIKA_LAST_AUTHOR_TEST_PROPERTY)));
   }
   
   /**
    * Test 2010 custom properties (ALF-16628)
    */
   public void test2010CustomProperties() throws Exception
   {
       String mimetype = MimetypeMap.MIMETYPE_APP_DWG; 
          
       String filename = "quick2010CustomProps.dwg";
       URL url = AbstractContentTransformerTest.class.getClassLoader().getResource("quick/" + filename);
       File file = new File(url.getFile());

       Map<QName, Serializable> properties = extractFromFile(file, mimetype);
      
       // check we got something
       assertFalse("extractFromMimetype should return at least some properties, none found for " + mimetype,
               properties.isEmpty());
      
       // check common metadata
       testCommonMetadata(mimetype, properties);
      
       assertEquals("Custom DWG property not found", "valueforcustomprop1", properties.get(TIKA_CUSTOM_TEST_PROPERTY));
   }
    
}
