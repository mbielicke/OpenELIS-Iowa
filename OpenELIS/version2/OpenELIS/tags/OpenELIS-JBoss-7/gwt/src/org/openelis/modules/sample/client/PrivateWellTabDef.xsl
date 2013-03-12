
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
  <screen id="PrivateWell" name="PrivateWell">
  
  	          <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <widget colspan="3">
                    <autoComplete key="{meta:getWellOrganizationName()}" width="180" case="UPPER" popWidth="auto" tab="{meta:getWellOrganizationId()},{meta:getBillTo()}" field="String">                      
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'orgId')" />:
                  </text>
                  <textbox key="{meta:getWellOrganizationId()}" width="60" tab="{meta:getWellReportToAddressMultipleUnit()},{meta:getWellOrganizationName()}" field="Integer" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'location')" />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocation()}" width="180" case="LOWER" max="28" tab="{meta:getWellLocationAddrMultipleUnit()},{meta:getAddressFaxPhone()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellReportToAddressMultipleUnit()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAttention()},{meta:getWellOrganizationId()}" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"attn")' />:
                  </text>
                  <textbox key="{meta:getWellReportToAttention()}" width="100" case="UPPER" max="30" tab="{meta:getWellReportToAddressStreetAddress()},{meta:getWellReportToAddressMultipleUnit()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrMultipleUnit()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrStreetAddress()},{meta:getWellLocation()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"address")' />:
                  </text>
                  <widget colspan="5">
                    <textbox key="{meta:getWellReportToAddressStreetAddress()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAddressCity()},{meta:getWellReportToAddressMultipleUnit()}" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"address")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrStreetAddress()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrCity()},{meta:getWellLocationAddrMultipleUnit()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"city")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellReportToAddressCity()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAddressState()},{meta:getWellReportToAddressStreetAddress()}" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'phone')" />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressWorkPhone()}" width="100" max="17" tab="{meta:getWellReportToAddressFaxPhone()},{meta:getWellReportToAddressZipCode()}" mask="{resource:getString($constants,'phoneWithExtensionPattern')}" field="String" />
                <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"city")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrCity()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrState()},{meta:getWellLocationAddrStreetAddress()}" field="String" />
                  </widget>
                  </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"state")' />:
                  </text>
                  <dropdown key="{meta:getWellReportToAddressState()}" width="40" case="UPPER" tab="{meta:getWellReportToAddressZipCode()},{meta:getWellReportToAddressCity()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressZipCode()}" width="73" mask="{resource:getString($constants,'zipcodePattern')}" max="10" tab="{meta:getWellReportToAddressWorkPhone()},{meta:getWellReportToAddressState()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'faxNumber')" />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressFaxPhone()}" width="100" max="12" tab="{meta:getWellLocation()},{meta:getWellReportToAddressWorkPhone()}" mask="{resource:getString($constants,'phonePattern')}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"state")' />:
                  </text>
                  <dropdown key="{meta:getWellLocationAddrState()}" width="40" case="UPPER" tab="{meta:getWellLocationAddrZipCode()},{meta:getWellLocationAddrCity()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                  </text>
                  <textbox key="{meta:getWellLocationAddrZipCode()}" width="73" case="UPPER" mask="{resource:getString($constants,'zipcodePattern')}" max="10" tab="itemsTestsTree,{meta:getWellLocationAddrState()}" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'owner')" />:
                  </text>
                  <widget colspan="3">
                  <textbox key="{meta:getWellOwner()}" width="200" case="LOWER" max="30" tab="{meta:getWellCollector()},itemsTestsTree" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <widget colspan="5">
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="182" case="LOWER" popWidth="auto" tab="{meta:getBillTo()},{meta:getWellWellNumber()}" field="Integer">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'collector')" />:
                  </text>
                  <widget colspan="3">
                  <textbox key="{meta:getWellCollector()}" width="200" case="LOWER" max="30" tab="{meta:getWellWellNumber()},{meta:getWellOwner()}" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'billTo')" />:
                  </text>
                  <widget colspan="5">
                  <HorizontalPanel>
                    <autoComplete key="{meta:getBillTo()}" width="182" case="UPPER" popWidth="auto" tab="{meta:getWellOrganizationName()},{meta:getProjectName()}" field="Integer">
                      <col width="200" header="{resource:getString($constants,'name')}" />
                      <col width="130" header="{resource:getString($constants,'street')}" />
                      <col width="120" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="billToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'wellNum')" />:
                  </text>
                  <widget colspan="3">
                  <textbox key="{meta:getWellWellNumber()}" width="80" tab="{meta:getProjectName()},{meta:getWellCollector()}" field="Integer" />
                  </widget>
                </row>
              </TablePanel>
              
  </screen>
  </xsl:template>
</xsl:stylesheet>