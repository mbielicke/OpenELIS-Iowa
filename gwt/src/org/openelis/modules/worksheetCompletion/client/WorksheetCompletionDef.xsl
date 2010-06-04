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
  <xsl:import href="OPENELIS/org/openelis/modules/note/client/InternalNoteTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCompletion" name="{resource:getString($constants,'worksheetCompletion')}">
      <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="printButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="updateButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="abortButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
          </HorizontalPanel>
        </AbsolutePanel>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
              </text>
              <textbox key="{meta:getId()}" width="100" case="LOWER" field="String"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'instrumentName')" />:
              </text>
              <autoComplete key="instrumentId" width="150" case="LOWER" popWidth="auto" tab="loadFilePopupButton,lookupWorksheetButton" field="Integer">
                <col width="150" header="Name" />
                <col width="200" header="Description" />
                <col width="75" header="Type">
                  <dropdown width="75" popWidth="auto" field="Integer"/>
                </col>
                <col width="200" header="Location" />
              </autoComplete>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'initials')" />:
              </text>
              <textbox key="defaultInitials" width="50" case="LOWER" tab="defaultStartedDate,loadFilePopupButton" field="String"/>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="100" popWidth="100" tab="lookupWorksheetButton,tabPanel" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'loadFrom')" />:
              </text>
              <appButton key="loadFilePopupButton" style="Button" action="load" tab="defaultInitials,browseButton">
                <HorizontalPanel>
                  <AbsolutePanel style="LoadButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'load')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'started')" />:
              </text>
              <calendar key="defaultStartedDate" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="defaultCompletedDate,defaultInitials" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
              </text>
              <HorizontalPanel>
                <textbox key="{meta:getRelatedWorksheetId()}" width="100" case="LOWER" field="String" />
                <appButton key="lookupWorksheetButton" style="LookupButton" tab="instrumentId,{meta:getStatusId()}" action="lookupWorksheet">
                  <AbsolutePanel style="LookupButtonImage" />
                </appButton>
              </HorizontalPanel>
	          <HorizontalPanel/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'completed')" />:
              </text>
              <calendar key="defaultCompletedDate" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="tabPanel,defaultStartedDate" />
            </row>
          </TablePanel>
<!-- TAB PANEL -->
          <TabPanel key="tabPanel" width="850" height="285">
<!-- TAB 1 -->
            <tab key="worksheetItemTab" tab="worksheetItemTable,worksheetItemTable" text="{resource:getString($constants,'worksheet')}">
              <VerticalPanel padding="0" spacing="0">
                <table key="worksheetItemTable" width="832" maxRows="10" showScroll="ALWAYS" tab="{meta:getId()},{meta:getId()}">
                  <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}">
                    <label field="String" />
                  </col>
                  <col key="{meta:getWorksheetAnalysisAccessionNumber()}" width="90" sort="true" header="{resource:getString($constants,'accessionNum')}">
                    <label field="String" />
                  </col>
                  <col key="{meta:getSampleDescription()}" width="110" header="{resource:getString($constants,'description')}" sort="true">
                    <label field="String" />
                  </col>
                  <col key="{meta:getWorksheetAnalysisWorksheetAnalysisId}" width="90" header="{resource:getString($constants,'qcLink')}">
                    <label field="String" />
                  </col>
                  <col key="{meta:getAnalysisTestName()}" width="100" header="{resource:getString($constants,'test')}" sort="true">
                    <label field="String" />
                  </col>
                  <col key="{meta:getAnalysisTestMethodName()}" width="100" header="{resource:getString($constants,'method')}" sort="true">
                    <label field="String" />
                  </col>
                  <col key="{meta:getAnalysisStatusId()}" width="75" header="{resource:getString($constants,'status')}" sort="true">
                    <dropdown width="55" field="Integer" />
                  </col>
                  <col key="analyteName" width="100" header="Analyte">
                    <label field="String" />
                  </col>
                  <col key="rawValue" width="100" header="Raw Value">
                    <label field="String" />
                  </col>
                  <col key="dilutionFactor" width="100" header="Dilution Factor">
                    <label field="String" />
                  </col>
                  <col key="finalValue" width="100" header="Final Value">
                    <label field="String" />
                  </col>
                  <col key="expectedValue" width="100" header="Expected Value">
                    <label field="String" />
                  </col>
                  <col key="quantLimit" width="100" header="Quant. Limit">
                    <label field="String" />
                  </col>
                </table>
                <widget style="TableButtonFooter">
                  <HorizontalPanel>
                    <appButton key="editMultipleButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="EditMultipleButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'editMultiple')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </HorizontalPanel>
                </widget>
              </VerticalPanel>
            </tab>
<!-- TAB 2 -->
            <tab key="notesTab" tab="standardNoteButton,standardNoteButton" text="{resource:getString($constants,'note')}">
              <xsl:call-template name="InternalNoteTab">
          	    <xsl:with-param name="width">850</xsl:with-param>
          		  <xsl:with-param name="height">247</xsl:with-param>
              </xsl:call-template>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
