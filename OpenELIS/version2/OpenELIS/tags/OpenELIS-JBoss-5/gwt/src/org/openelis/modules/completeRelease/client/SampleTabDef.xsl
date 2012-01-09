<!--
Exhibit A - UIRF Open-source Based Public Software License.
  
The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu
  
Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.
  
The Original Code is OpenELIS code.
  
The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.
  
Contributor(s): ______________________________________.
  
Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
  -->
<xsl:stylesheet
  version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:meta="xalan://org.openelis.meta.SampleMeta">

  <xsl:template name="SampleTab">
      <TablePanel style="Form">
        <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getAccessionNumber()}" width="75" tab="{meta:getOrderId()},SampleContent" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'orderNum')" />:
              </text>
              <textbox key="{meta:getOrderId()}" width="75" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'collected')" />:
              </text>
              <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="90" maxValue="0" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},{meta:getOrderId()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'time')" />:
              </text>
              <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="125" maxValue="0" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="110" popWidth="110" tab="{meta:getClientReference()},{meta:getReceivedDate()}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'clntRef')" />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getClientReference()}" width="196" case="LOWER" max="20" tab="SampleContent,{meta:getStatusId()}" field="String" />
              </widget>
            </row>
      </TablePanel>
  </xsl:template>
</xsl:stylesheet>
