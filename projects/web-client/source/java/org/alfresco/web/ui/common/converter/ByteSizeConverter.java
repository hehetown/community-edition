/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.common.converter;

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter class to convert the size of an item in bytes into a readable KB/MB form.
 * 
 * @author Kevin Roast
 */
public class ByteSizeConverter implements Converter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "org.alfresco.faces.ByteSizeConverter";

   private static final String POSTFIX_KB = " KB";
   private static final String POSTFIX_MB = " MB";
   private static final String POSTFIX_GB = " GB";
   
   private static final String NUMBER_PATTERN = "###,###.##";
   
   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      return Long.parseLong(value);
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      String result = "";
      if (value != null)
      {
         result = value.toString();
      }
      
      long size;
      if (value instanceof Long)
      {
         size = (Long)value;
      }
      else if (value instanceof String)
      {
         try
         {
            size = Long.parseLong((String)value);
         }
         catch (NumberFormatException ne)
         {
            return result;
         }
      }
      else
      {
         return result;
      }
      
      // get formatter
      // TODO: can we cache this instance...? DecimalFormat is not threadsafe! Need threadlocal instance.
      DecimalFormat formatter = new DecimalFormat(NUMBER_PATTERN);
      
      StringBuilder buf = new StringBuilder();
      
      if (size < 999999)
      {
         double val = ((double)size) / 1024.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_KB);
      }
      else if (size < 999999999)
      {
         double val = ((double)size) / 1048576.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_MB);
      }
      else
      {
         double val = ((double)size) / 1073741824.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_GB);
      }
      
      return buf.toString();
   }
}
