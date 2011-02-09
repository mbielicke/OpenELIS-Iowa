
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

 <xsl:import href="IMPORT/button.xsl" />
  <xsl:variable name="language" select="doc/locale" />
  <xsl:variable name="props" select="doc/props" />
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
  
  <screen name="Environmental">
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                        </text>
                        <check key="{meta:getEnvIsHazardous()}" tab="{meta:getEnvPriority()},{meta:getBillTo()}" />
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'priority')" />:
                        </text>
                        <textbox field="Integer" key="{meta:getEnvPriority()}" tab="{meta:getEnvCollector()},{meta:getEnvIsHazardous()}" width="90px" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'collector')" />:
                        </text>
                        <textbox field="String" key="{meta:getEnvCollector()}" tab="{meta:getEnvCollectorPhone()},{meta:getEnvDescription()}" width="235px" />
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'phone')" />:
                        </text>
                        <textbox field="String" key="{meta:getEnvCollectorPhone()}" tab="{meta:getEnvLocation()},{meta:getEnvCollector()}" width="120px" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'location')" />:
                        </text>
                        <HorizontalPanel>
                          <textbox field="String" key="{meta:getEnvLocation()}" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" width="175px" />
                          <button key="locButton" style="LookupButton">
                            <AbsolutePanel style="LookupButtonImage" />
                          </button>
                        </HorizontalPanel>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'desc')" />:
                        </text>
                        <textbox field="String" key="{meta:getEnvDescription()}" tab="{meta:getProjectName()},{meta:getEnvLocation()}" width="315px" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'project')" />:
                        </text>
                        <HorizontalPanel>
                          <autoComplete case="UPPER" key="{meta:getProjectName()}" tab="{meta:getOrgName()},{meta:getEnvDescription()}" width="175px">
                            <col header="{resource:getString($constants,'name')}" width="115" />
                            <col header="{resource:getString($constants,'desc')}" width="190" />
                          </autoComplete>
                          <button key="projectLookup" style="LookupButton">
                            <AbsolutePanel style="LookupButtonImage" />
                          </button>
                        </HorizontalPanel>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                        </text>
                        <HorizontalPanel>
                          <autoComplete case="UPPER" key="{meta:getOrgName()}" tab="{meta:getBillTo()},{meta:getProjectName()}" width="175px">
                            <col header="{resource:getString($constants,'name')}" width="180" />
                            <col header="{resource:getString($constants,'street')}" width="110" />
                            <col header="{resource:getString($constants,'city')}" width="100" />
                            <col header="{resource:getString($constants,'st')}" width="20" />
                          </autoComplete>
                          <button key="reportToLookup" style="LookupButton">
                            <AbsolutePanel style="LookupButtonImage" />
                          </button>
                        </HorizontalPanel>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'billTo')" />:
                        </text>
                        <HorizontalPanel>
                          <autoComplete case="UPPER" key="{meta:getBillTo()}" tab="{meta:getEnvIsHazardous()},{meta:getOrgName()}" width="175px">
                            <col header="{resource:getString($constants,'name')}" width="180" />
                            <col header="{resource:getString($constants,'street')}" width="110" />
                            <col header="{resource:getString($constants,'city')}" width="100" />
                            <col header="{resource:getString($constants,'st')}" width="20" />
                          </autoComplete>
                          <button key="billToLookup" style="LookupButton">
                            <AbsolutePanel style="LookupButtonImage" />
                          </button>
                        </HorizontalPanel>
                      </row>
                    </TablePanel>
  </screen>
    </xsl:template>
</xsl:stylesheet>