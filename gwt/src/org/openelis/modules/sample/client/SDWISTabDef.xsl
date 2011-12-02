
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
  
  <screen id="SDWIS" name="SDWIS">
                   	<TablePanel style="Form">
                     <row>
                		<text style="Prompt">
                  			<xsl:value-of select="resource:getString($constants,'pwsId')" />:
                		</text>
                	    <HorizontalPanel>
                  		  <textbox key="{meta:getSDWISPwsNumber0()}" width="75" max="9" case="UPPER" tab="pwsName,{meta:getClientReference()}" field="String"/>
                  			<appButton key="pwsButton" style="LookupButton">
                    		  <AbsolutePanel style="LookupButtonImage" />
                            </appButton>
                       </HorizontalPanel>
                       <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'pwsName')" />:
                       </text>
                       <textbox key="pwsName" width="250" tab="{meta:getSDWISStateLabId()},{meta:getSDWISPwsNumber0()}" field="String" />
                     </row>
                     <row>
                        <text style="Prompt">
                           <xsl:value-of select="resource:getString($constants,'stateLabNo')" />:
                        </text>
                        <textbox key="{meta:getSDWISStateLabId()}" width="102" tab="{meta:getSDWISFacilityId()},pwsName" field="Integer"/>
                        <text style="Prompt">
                           <xsl:value-of select="resource:getString($constants,'facilityId')" />:
                        </text>
                        <textbox key="{meta:getSDWISFacilityId()}" width="75" max="12" case="UPPER" tab="{meta:getSDWISSampleTypeId()},{meta:getSDWISStateLabId()}" field="String" />
                     </row>
                     <row>
                        <text style="Prompt">
                           <xsl:value-of select="resource:getString($constants,'sampleType')" />:
                        </text>
                        <dropdown key="{meta:getSDWISSampleTypeId()}" width="120" tab="{meta:getSDWISSampleCategoryId()},{meta:getSDWISFacilityId()}" field="Integer" />
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'sampleCat')" />:
                        </text>
                        <dropdown key="{meta:getSDWISSampleCategoryId()}" width="108" tab="{meta:getSDWISSamplePointId()},{meta:getSDWISSampleTypeId()}" field="Integer"/>
                     </row>
                     <row>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'samplePtId')" />:
                       </text>
                       <textbox key="{meta:getSDWISSamplePointId()}" width="75" max="11" case="UPPER" tab="{meta:getSDWISLocation()},{meta:getSDWISSampleCategoryId()}" field="String"/>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'location')" />:
                       </text>
                       <textbox key="{meta:getSDWISLocation()}" width="250" max="20" tab="{meta:getSDWISCollector()},{meta:getSDWISSamplePointId()}" field="String" />
                     </row>
                     <row>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'collector')" />:
                       </text>
                       <textbox key="{meta:getSDWISCollector()}" width="162" case="LOWER" max="20" tab="{meta:getOrgName()},{meta:getSDWISLocation()}" field="String" />
                     </row>
                     <row>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                       </text>
                       <HorizontalPanel>
                         <autoComplete key="{meta:getOrgName()}" width="179" case="UPPER" popWidth="auto" tab="{meta:getBillTo()},{meta:getSDWISCollector()}" field="Integer">
                           <col width="180" header="{resource:getString($constants,'name')}" />
                           <col width="110" header="{resource:getString($constants,'street')}" />
                           <col width="100" header="{resource:getString($constants,'city')}" />
                           <col width="20" header="{resource:getString($constants,'st')}" />
                         </autoComplete>
                         <appButton key="reportToLookup" style="LookupButton">
                           <AbsolutePanel style="LookupButtonImage" />
                         </appButton>
                       </HorizontalPanel>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'billTo')" />:
                       </text>
                       <HorizontalPanel>
                        <autoComplete key="{meta:getBillTo()}" width="179" case="UPPER" popWidth="auto" tab="sampleItemTabPanel,{meta:getOrgName()}" field="Integer">
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
  </screen>
  </xsl:template>
</xsl:stylesheet>