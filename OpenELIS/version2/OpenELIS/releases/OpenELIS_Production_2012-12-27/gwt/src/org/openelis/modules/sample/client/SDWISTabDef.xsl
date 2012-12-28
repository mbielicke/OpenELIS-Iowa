
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
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.meta.SampleMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:variable name="language" select="doc/locale" />
  <xsl:variable name="props" select="doc/props" />
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="SDWIS" name="SDWIS">
      <TablePanel style="Form">
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'pwsId')" />:
          </text>
          <HorizontalPanel>
            <textbox case="UPPER" field="String" key="{meta:getSDWISPwsNumber0()}" max="9" tab="pwsName,{meta:getClientReference()}" width="75" />
            <appButton key="pwsButton" style="LookupButton">
              <AbsolutePanel style="LookupButtonImage" />
            </appButton>
          </HorizontalPanel>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'pwsName')" />:
          </text>
          <textbox field="String" key="pwsName" tab="{meta:getSDWISStateLabId()},{meta:getSDWISPwsNumber0()}" width="250" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'stateLabNo')" />:
          </text>
          <textbox field="Integer" key="{meta:getSDWISStateLabId()}" tab="{meta:getSDWISFacilityId()},pwsName" width="102" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'facilityId')" />:
          </text>
          <textbox case="UPPER" field="String" key="{meta:getSDWISFacilityId()}" max="12" tab="{meta:getSDWISSampleTypeId()},{meta:getSDWISStateLabId()}" width="75" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'sampleType')" />:
          </text>
          <dropdown field="Integer" key="{meta:getSDWISSampleTypeId()}" tab="{meta:getSDWISSampleCategoryId()},{meta:getSDWISFacilityId()}" width="120" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'sampleCat')" />:
          </text>
          <dropdown field="Integer" key="{meta:getSDWISSampleCategoryId()}" tab="{meta:getSDWISSamplePointId()},{meta:getSDWISSampleTypeId()}" width="108" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'samplePtId')" />:
          </text>
          <textbox case="UPPER" field="String" key="{meta:getSDWISSamplePointId()}" max="11" tab="{meta:getSDWISLocation()},{meta:getSDWISSampleCategoryId()}" width="75" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'location')" />:
          </text>
          <textbox field="String" key="{meta:getSDWISLocation()}" max="20" tab="{meta:getSDWISCollector()},{meta:getSDWISSamplePointId()}" width="250" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'collector')" />:
          </text>
          <textbox case="LOWER" field="String" key="{meta:getSDWISCollector()}" max="20" tab="{meta:getProjectName()},{meta:getSDWISLocation()}" width="162" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'project')" />:
          </text>
          <widget colspan="5">
            <HorizontalPanel>
              <autoComplete case="LOWER" field="Integer" key="{meta:getProjectName()}" popWidth="auto" tab="{meta:getOrgName()},{meta:getSDWISCollector()}" width="179">
                <col header="{resource:getString($constants,'name')}" width="115" />
                <col header="{resource:getString($constants,'desc')}" width="190" />
              </autoComplete>
              <appButton key="projectLookup" style="LookupButton">
                <AbsolutePanel style="LookupButtonImage" />
              </appButton>
            </HorizontalPanel>
          </widget>
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'reportTo')" />:
          </text>
          <HorizontalPanel>
            <autoComplete case="UPPER" field="Integer" key="{meta:getOrgName()}" popWidth="auto" tab="{meta:getBillTo()},{meta:getProjectName()}" width="179">
              <col header="{resource:getString($constants,'name')}" width="180" />
              <col header="{resource:getString($constants,'street')}" width="110" />
              <col header="{resource:getString($constants,'city')}" width="100" />
              <col header="{resource:getString($constants,'st')}" width="20" />
            </autoComplete>
            <appButton key="reportToLookup" style="LookupButton">
              <AbsolutePanel style="LookupButtonImage" />
            </appButton>
          </HorizontalPanel>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'billTo')" />:
          </text>
          <HorizontalPanel>
            <autoComplete case="UPPER" field="Integer" key="{meta:getBillTo()}" popWidth="auto" tab="sampleItemTabPanel,{meta:getOrgName()}" width="179">
              <col header="{resource:getString($constants,'name')}" width="180" />
              <col header="{resource:getString($constants,'street')}" width="110" />
              <col header="{resource:getString($constants,'city')}" width="100" />
              <col header="{resource:getString($constants,'st')}" width="20" />
            </autoComplete>
            <appButton key="billToLookup" style="LookupButton">
              <AbsolutePanel style="LookupButtonImage" />
            </appButton>
          </HorizontalPanel>
        </row>
      </TablePanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>