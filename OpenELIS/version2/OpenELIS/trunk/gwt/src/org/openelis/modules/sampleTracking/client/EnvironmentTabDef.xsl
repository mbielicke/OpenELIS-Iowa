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
  
    <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  
    <screen id="envDomain" name="Environment Info">
           <VerticalPanel width="98%">
            <TablePanel width="100%" style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                </text>
                <check key="{meta:getEnvIsHazardous()}" tab="{meta:getEnvPriority()},{meta:getClientReference()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'priority')" />:
                </text>
                <textbox key="{meta:getEnvPriority()}" width="90px" tab="{meta:getEnvCollector()},{meta:getEnvIsHazardous()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collector')" />:
                </text>
                <textbox key="{meta:getEnvCollector()}" width="235px" tab="{meta:getEnvCollectorPhone()},{meta:getEnvDescription()}" field="String"/>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'phone')" />:
                </text>
                <textbox key="{meta:getEnvCollectorPhone()}" width="120px" tab="{meta:getEnvSamplingLocation()},{meta:getEnvCollector()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'location')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getEnvSamplingLocation()}" width="175px" field="String" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" />
                  <appButton key="locButton" style="LookupButton">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'desc')" />:
                </text>
                <textbox key="{meta:getEnvDescription()}" width="315px" tab="itemsTestsTree,{meta:getEnvSamplingLocation()}" field="String" />
              </row>
              <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="{meta:getOrgName()},itemsTestsTree">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getOrgName()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="{meta:getBillTo()},{meta:getProjectName()}">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="reportToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'billTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getBillTo()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="sampleItemTabPanel,{meta:getOrgName()}">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="billToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
            </TablePanel>
          </VerticalPanel>
       </screen>
</xsl:template>
</xsl:stylesheet>