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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCompletionMeta">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetLookup" name="{resource:getString($constants,'worksheetLookup')}">
      <VerticalPanel padding="0" spacing="0">
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <HorizontalPanel>
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
                </text>
                <textbox key="{meta:getId()}" width="100" tab="{meta:getSystemUserId()},cancelButton" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetUser')" />
                </text>
                <autoComplete key="{meta:getSystemUserId()}" width="100" case="LOWER" popWidth="auto" tab="{meta:getCreatedDate()},{meta:getId()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'created')" />:
                </text>
                <calendar key="{meta:getCreatedDate()}" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getSystemUserId()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="100" popWidth="100" tab="searchButton,{meta:getCreatedDate()}" field="Integer" />
              </row>
            </TablePanel>
            <widget halign="center" valign="middle">
              <appButton key="searchButton" style="Button" tab="worksheetTable,{meta:getStatusId()}" action="search">
                <HorizontalPanel>
                  <AbsolutePanel style="FindButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'search')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
          </HorizontalPanel>
          <table key="worksheetTable" width="auto" maxRows="9" multiSelect="false" showScroll="ALWAYS" style="ScreenTableWithSides" tab="okButton,searchButton" title="">
            <col key="{meta:getId()}" width="100" sort="true" header="{resource:getString($constants,'worksheetNumber')}">
              <label field="Integer" />
            </col>
            <col key="{meta:getSystemUserId()}" width="100" sort="true" header="{resource:getString($constants,'worksheetUser')}">
              <label field="String" />
            </col>
            <col key="{meta:getCreatedDate()}" width="130" sort="true" header="{resource:getString($constants,'created')}">
              <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
            </col>
            <col key="{meta:getStatusId()}" width="100" sort="true" header="{resource:getString($constants,'status')}">
              <dropdown width="80" field="Integer" />
            </col>
            <col key="{meta:getAnalysisTestName()}" width="100" header="{resource:getString($constants,'test')}" sort="true">
              <label field="String"/>
            </col>
            <col key="{meta:getAnalysisTestMethodName()}" width="75" header="{resource:getString($constants,'method')}" sort="true">
              <label field="String"/>
            </col>
          </table>
        </VerticalPanel>

<!--button panel code-->

        <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="selectButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="cancelButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
