
<!--
		Exhibit A - UIRF Open-source Based Public Software License. The
		contents of this file are subject to the UIRF Open-source Based Public
		Software License(the "License"); you may not use this file except in
		compliance with the License. You may obtain a copy of the License at
		openelis.uhl.uiowa.edu Software distributed under the License is
		distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
		express or implied. See the License for the specific language
		governing rights and limitations under the License. The Original Code
		is OpenELIS code. The Initial Developer of the Original Code is The
		University of Iowa. Portions created by The University of Iowa are
		Copyright 2006-2008. All Rights Reserved. Contributor(s):
		______________________________________. Alternatively, the contents of
		this file marked "Separately-Licensed" may be used under the terms of
		a UIRF Software license ("UIRF Software License"), in which case the
		provisions of a UIRF Software License are applicable instead of those
		above.
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
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'sampleLocation')}">
      <VerticalPanel padding="0" spacing="0">
        <VerticalPanel width="300" padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"location")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getEnvLocation()}" width="214" max="40" tab="{meta:getLocationAddrMultipleUnit()},{meta:getLocationAddrCountry()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getLocationAddrMultipleUnit()}" width="214" case="UPPER" max="30" tab="{meta:getLocationAddrStreetAddress()},{meta:getEnvLocation()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"address")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getLocationAddrStreetAddress()}" width="214" case="UPPER" max="30" tab="{meta:getLocationAddrCity()},{meta:getLocationAddrMultipleUnit()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"city")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getLocationAddrCity()}" width="214" case="UPPER" max="30" tab="{meta:getLocationAddrState()},{meta:getLocationAddrStreetAddress()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"state")' />:
              </text>
              <widget>
                <dropdown key="{meta:getLocationAddrState()}" width="55" case="UPPER" tab="{meta:getLocationAddrZipCode()},{meta:getLocationAddrCity()}" field="String" />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"zipcode")' />:
              </text>
              <widget>
                <textbox key="{meta:getLocationAddrZipCode()}" width="91" case="UPPER" max="10" tab="{meta:getLocationAddrCountry()},{meta:getLocationAddrState()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'country')" />:
              </text>
              <widget colspan="3">
                <dropdown key="{meta:getLocationAddrCountry()}" width="214" tab="{meta:getEnvLocation()},{meta:getLocationAddrZipCode()}" field="String" />
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>
<!--button panel code-->
        <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="okButton"/>
          </HorizontalPanel>
        </AbsolutePanel>
<!--end button panel-->
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
