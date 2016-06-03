
package org.alfresco.rest.api.tests.util;


import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <i><b>multipart/form-data</b></i> builder.
 *
 * @author Jamal Kaabi-Mofrad
 */
public class MultiPartBuilder
{
    private FileData fileData;
    private String relativePath;
    private String updateNodeRef;
    private String description;
    private String contentTypeQNameStr;
    private List<String> aspects = Collections.emptyList();
    private Boolean majorVersion;
    private Boolean overwrite;
    private Boolean autoRename;
    private String nodeType;
    private List<String> renditionIds = Collections.emptyList(); // initially single rendition name/id (in the future we may support multiple)
    private Map<String, String> properties = Collections.emptyMap();

    private MultiPartBuilder()
    {
    }

    private MultiPartBuilder(MultiPartBuilder that)
    {
        this.fileData = that.fileData;
        this.relativePath = that.relativePath;
        this.updateNodeRef = that.updateNodeRef;
        this.description = that.description;
        this.contentTypeQNameStr = that.contentTypeQNameStr;
        this.aspects = new ArrayList<>(that.aspects);
        this.majorVersion = that.majorVersion;
        this.overwrite = that.overwrite;
        this.autoRename = that.autoRename;
        this.nodeType = that.nodeType;
        this.renditionIds = that.renditionIds;
        this.properties = new HashMap<>(that.properties);
    }

    public static MultiPartBuilder create()
    {
        return new MultiPartBuilder();
    }

    public static MultiPartBuilder copy(MultiPartBuilder copy)
    {
        return new MultiPartBuilder(copy);
    }

    public MultiPartBuilder setFileData(FileData fileData)
    {
        this.fileData = fileData;
        return this;
    }

    public MultiPartBuilder setRelativePath(String relativePath)
    {
        this.relativePath = relativePath;
        return this;
    }

    public MultiPartBuilder setUpdateNoderef(String updateNodeRef)
    {
        this.updateNodeRef = updateNodeRef;
        return this;
    }

    public MultiPartBuilder setDescription(String description)
    {
        this.description = description;
        return this;
    }

    public MultiPartBuilder setContentTypeQNameStr(String contentTypeQNameStr)
    {
        this.contentTypeQNameStr = contentTypeQNameStr;
        return this;
    }

    public MultiPartBuilder setAspects(List<String> aspects)
    {
        this.aspects = aspects;
        return this;
    }

    public MultiPartBuilder setMajorVersion(boolean majorVersion)
    {
        this.majorVersion = Boolean.valueOf(majorVersion);
        return this;
    }

    public MultiPartBuilder setOverwrite(boolean overwrite)
    {
        this.overwrite = Boolean.valueOf(overwrite);
        return this;
    }

    public MultiPartBuilder setAutoRename(boolean autoRename)
    {
        this.autoRename = Boolean.valueOf(autoRename);
        return this;
    }

    public MultiPartBuilder setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
        return this;
    }

    public MultiPartBuilder setProperties(Map<String, String> properties)
    {
        this.properties = properties;
        return this;
    }

    public MultiPartBuilder setRenditions(List<String> renditionIds)
    {
        this.renditionIds = renditionIds;
        return this;
    }

    private String getCommaSeparated(List<String> names)
    {
        if (! names.isEmpty())
        {
            StringBuilder sb = new StringBuilder(names.size() * 2);
            for (String str : names)
            {
                sb.append(str).append(',');
            }

            sb.deleteCharAt(sb.length() - 1); // remove leading separator

            return sb.toString();

        }
        return null;
    }

    public static class FileData
    {
        private final String fileName;
        private final File file;
        private final String mimetype;
        private final String encoding;

        public FileData(String fileName, File file, String mimetype)
        {
            this(fileName, file, mimetype, null);
        }

        public FileData(String fileName, File file, String mimetype, String encoding)
        {
            this.fileName = fileName;
            this.file = file;
            this.mimetype = mimetype;
            this.encoding = encoding;
        }

        public String getFileName()
        {
            return fileName;
        }

        public File getFile()
        {
            return file;
        }

        public String getMimetype()
        {
            return mimetype;
        }

        public String getEncoding()
        {
            return encoding;
        }
    }

    public static class MultiPartRequest
    {
        private final byte[] body;
        private final String contentType;
        private final long contentLength;

        public MultiPartRequest(byte[] body, String contentType, long contentLength)
        {
            this.body = body;
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        public byte[] getBody()
        {
            return body;
        }

        public String getContentType()
        {
            return contentType;
        }

        public long getContentLength()
        {
            return contentLength;
        }
    }

    public MultiPartRequest build() throws IOException
    {
        List<Part> parts = new ArrayList<>();

        if (fileData != null)
        {
            FilePart fp = new FilePart("filedata", fileData.getFileName(), fileData.getFile(), fileData.getMimetype(), null);
            // Get rid of the default values added upon FilePart instantiation
            fp.setCharSet(fileData.getEncoding());
            fp.setContentType(fileData.getMimetype());
            parts.add(fp);
            addPartIfNotNull(parts, "name", fileData.getFileName());
        }
        addPartIfNotNull(parts, "relativepath", relativePath);
        addPartIfNotNull(parts, "updatenoderef", updateNodeRef);
        addPartIfNotNull(parts, "description", description);
        addPartIfNotNull(parts, "contenttype", contentTypeQNameStr);
        addPartIfNotNull(parts, "aspects", getCommaSeparated(aspects));
        addPartIfNotNull(parts, "majorversion", majorVersion);
        addPartIfNotNull(parts, "overwrite", overwrite);
        addPartIfNotNull(parts, "autorename", autoRename);
        addPartIfNotNull(parts, "nodetype", nodeType);
        addPartIfNotNull(parts, "renditions", getCommaSeparated(renditionIds));

        if (!properties.isEmpty())
        {
            for (Entry<String, String> prop : properties.entrySet())
            {
                parts.add(new StringPart(prop.getKey(), prop.getValue()));
            }
        }

        MultipartRequestEntity req = new MultipartRequestEntity(parts.toArray(new Part[parts.size()]), new HttpMethodParams());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        req.writeRequest(os);

        return new MultiPartRequest(os.toByteArray(), req.getContentType(), req.getContentLength());
    }

    private void addPartIfNotNull(List<Part> list, String partName, Object partValue)
    {
        if (partValue != null)
        {
            list.add(new StringPart(partName, partValue.toString()));
        }
    }
}
