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
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="100" popWidth="100" tab="lookupWorksheetButton,tabPanel" field="Integer" />
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
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'instrumentName')" />:
              </text>
              <widget colspan="5">
                <autoComplete key="instrumentId" width="150" case="LOWER" popWidth="auto" tab="defaultUser,lookupWorksheetButton" field="Integer">
                  <col width="150" header="Name" />
                  <col width="200" header="Description" />
                  <col width="75" header="Type">
                    <dropdown width="75" popWidth="auto" field="Integer"/>
                  </col>
                  <col width="200" header="Location" />
                </autoComplete>
              </widget>
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
                  <col key="reportable" width="100" header="Reportable">
                    <label field="String" />
                  </col>
                  <col key="value1" width="100" header="Value 1">
                    <label field="String" />
                  </col>
                  <col key="value2" width="100" header="Value 2">
                    <label field="String" />
                  </col>
                  <col key="value3" width="100" header="Value 3">
                    <label field="String" />
                  </col>
                  <col key="value4" width="100" header="Value 4">
                    <label field="String" />
                  </col>
                  <col key="value5" width="100" header="Value 5">
                    <label field="String" />
                  </col>
                  <col key="value6" width="100" header="Value 6">
                    <label field="String" />
                  </col>
                  <col key="value7" width="100" header="Value 7">
                    <label field="String" />
                  </col>
                  <col key="value8" width="100" header="Value 8">
                    <label field="String" />
                  </col>
                  <col key="value9" width="100" header="Value 9">
                    <label field="String" />
                  </col>
                  <col key="value10" width="100" header="Value 10">
                    <label field="String" />
                  </col>
                  <col key="value11" width="100" header="Value 11">
                    <label field="String" />
                  </col>
                  <col key="value12" width="100" header="Value 12">
                    <label field="String" />
                  </col>
                  <col key="value13" width="100" header="Value 13">
                    <label field="String" />
                  </col>
                  <col key="value14" width="100" header="Value 14">
                    <label field="String" />
                  </col>
                  <col key="value15" width="100" header="Value 15">
                    <label field="String" />
                  </col>
                  <col key="value16" width="100" header="Value 16">
                    <label field="String" />
                  </col>
                  <col key="value17" width="100" header="Value 17">
                    <label field="String" />
                  </col>
                  <col key="value18" width="100" header="Value 18">
                    <label field="String" />
                  </col>
                  <col key="value19" width="100" header="Value 19">
                    <label field="String" />
                  </col>
                  <col key="value20" width="100" header="Value 20">
                    <label field="String" />
                  </col>
                  <col key="value21" width="100" header="Value 21">
                    <label field="String" />
                  </col>
                  <col key="value22" width="100" header="Value 22">
                    <label field="String" />
                  </col>
                  <col key="value23" width="100" header="Value 23">
                    <label field="String" />
                  </col>
                  <col key="value24" width="100" header="Value 24">
                    <label field="String" />
                  </col>
                  <col key="value25" width="100" header="Value 25">
                    <label field="String" />
                  </col>
                  <col key="value26" width="100" header="Value 26">
                    <label field="String" />
                  </col>
                  <col key="value27" width="100" header="Value 27">
                    <label field="String" />
                  </col>
                  <col key="value28" width="100" header="Value 28">
                    <label field="String" />
                  </col>
                  <col key="value29" width="100" header="Value 29">
                    <label field="String" />
                  </col>
                  <col key="value30" width="100" header="Value 30">
                    <label field="String" />
                  </col>
                </table>
                <widget style="TableButtonFooter">
                  <HorizontalPanel>
                    <appButton key="editWorksheetButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="EditMultipleButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'editWorksheet')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                    <appButton key="loadFromEditButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="LoadButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'loadFromEditFile')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                    <appButton key="loadFilePopupButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="LoadButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'loadFromFile')" />
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
