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

  <xsl:template name="ResultTab">
                <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
                  </text>
                  <textbox field="Integer" key="{meta:getAccessionNumber()}" required="true" tab="orderNumber,SampleContent" width="75px" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'orderNum')" />:
                  </text>
                  <textbox field="Integer" key="orderNumber" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" width="75px" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'collected')" />:
                  </text>
                  <calendar begin="0" end="2" key="{meta:getCollectionDate()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},orderNumber" width="80px" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'time')" />:
                  </text>
                  <textbox begin="3" end="5" field="Date" key="{meta:getCollectionTime()}" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" width="60px" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'received')" />:
                  </text>
                  <calendar begin="0" end="4" key="{meta:getReceivedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" width="110px" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'status')" />:
                  </text>
                  <dropdown field="Integer" key="{meta:getStatusId()}" popWidth="110px" required="true" tab="{meta:getClientReference()},{meta:getReceivedDate()}" width="110px" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'clntRef')" />:
                  </text>
                  <widget colspan="3">
                    <textbox field="String" key="{meta:getClientReference()}" tab="SampleContent,{meta:getStatusId()}" width="175px" />
                  </widget>
                </row>
              </TablePanel>
            </VerticalPanel>
  </xsl:template>

</xsl:stylesheet>
