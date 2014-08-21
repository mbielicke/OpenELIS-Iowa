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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCreationMeta">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCreation1" name="{resource:getString($constants,'worksheetCreation1')}">
      <HorizontalPanel>
        <VerticalPanel padding="0" spacing="0">
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <menuPanel key="loadTemplateMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="loadTemplate">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"loadTemplate")' />
                        </text>
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                    <menuItem key="template1" description="" enable="false" icon="historyIcon" label="Template 1" />
                    <menuItem key="template2" description="" enable="false" icon="historyIcon" label="Template 2" />
                    <menuItem key="template3" description="" enable="false" icon="historyIcon" label="Template 3" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
              <menuPanel key="deleteQcsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton style="ButtonPanelButton" action="deleteQcs">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"undoQc")' />
                        </text>
                        <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                    <menuItem key="deleteAll" description="" enable="false" icon="" label="All" />
                    <menuItem key="deleteTemplate" description="" enable="false" icon="" label="Template" />
                    <menuItem key="deleteManual" description="" enable="false" icon="" label="Manual" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
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
                <textbox key="{meta:getWorksheetId()}" width="100" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'worksheetFormat')" />:
                </text>
                <dropdown key="{meta:getWorksheetFormatId()}" width="130" popWidth="130px" tab="lookupWorksheetButton,removeRowButton" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getWorksheetRelatedWorksheetId()}" width="100" field="Integer" />
                  <appButton key="lookupWorksheetButton" style="LookupButton" tab="instrumentId,{meta:getWorksheetFormatId()}" action="lookupWorksheet">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'instrumentName')" />:
                </text>
                <widget colspan="5">
                  <autoComplete key="instrumentId" width="150" case="LOWER" popWidth="auto" tab="worksheetItemTable,lookupWorksheetButton" field="Integer">
                    <col width="150" header="Name" />
                    <col width="200" header="Description" />
                    <col width="200" header="Type"/>
                    <col width="200" header="Location" />
                  </autoComplete>
                </widget>
              </row>
            </TablePanel>
            <table key="worksheetItemTable" width="800" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" tab="insertAnalysisWorksheetButton,instrumentId" title="">
              <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}">
                <label field="Integer" />
              </col>
              <col key="{meta:getSampleAccessionNumber()}" width="90" sort="true" header="{resource:getString($constants,'accessionNum')}">
                <label field="String" />
              </col>
              <col key="{meta:getSampleDescription()}" width="110" sort="true" header="{resource:getString($constants,'description')}">
                <label field="String" />
              </col>
              <col key="{meta:getWorksheetAnalysisWorksheetAnalysisId}" width="90" header="{resource:getString($constants,'qcLink')}">
                <dropdown width="70" field="Integer" />
              </col>
              <col key="{meta:getAnalysisTestName()}" width="100" sort="true" header="{resource:getString($constants,'test')}">
                <label field="String" />
              </col>
              <col key="{meta:getAnalysisTestMethodName()}" width="100" sort="true" header="{resource:getString($constants,'method')}">
                <label field="String" />
              </col>
              <col key="{meta:getAnalysisUnitOfMeasureId()}" width="100" header="{resource:getString($constants,'unit')}">
                <autoComplete width="100" case="MIXED" field="Integer">
                  <col width="100" />
                </autoComplete>
              </col>
              <col key="{meta:getAnalysisStatusId()}" width="75" sort="true" header="{resource:getString($constants,'status')}">
                <dropdown width="55" field="Integer" />
              </col>
              <col key="{meta:getSampleCollectionDate()}" width="75" sort="true" header="{resource:getString($constants,'collected')}">
                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
              </col>
              <col key="{meta:getSampleReceivedDate()}" width="110" sort="true" header="{resource:getString($constants,'received')}">
                <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
              </col>
              <col key="{meta:getAnalysisDueDays()}" width="50" sort="true" header="{resource:getString($constants,'due')}">
                <label field="Integer" />
              </col>
              <col key="{meta:getAnalysisExpireDate()}" width="110" sort="true" header="{resource:getString($constants,'expire')}">
                <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
              </col>
            </table>
            <widget style="TableFooterPanel">
              <HorizontalPanel>
                <menuPanel key="addRowMenu" layout="vertical" style="topBarItemHolder">
                  <menuItem>
                    <menuDisplay>
                      <appButton style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select='resource:getString($constants,"addRow")' />
                          </text>
                          <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                        </HorizontalPanel>
                      </appButton>
                    </menuDisplay>
                    <menuPanel width="200" layout="vertical" position="below" style="buttonMenuContainer">
                      <menuItem key="insertFromWorksheetAbove" description="" enable="false" icon="historyIcon" label="From Another Worksheet (Above)" />
                      <menuItem key="insertFromWorksheetBelow" description="" enable="false" icon="historyIcon" label="From Another Worksheet (Below)" />
                      <menuItem key="insertFromQcTableAbove" description="" enable="false" icon="historyIcon" label="From QC Table (Above)" />
                      <menuItem key="insertFromQcTableBelow" description="" enable="false" icon="historyIcon" label="From QC Table (Below)" />
                    </menuPanel>
                  </menuItem>
                </menuPanel>
                <appButton key="removeRowButton" style="Button" tab="{meta:getWorksheetFormatId()},addRowMenu" action="removeRow">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'removeRow')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </HorizontalPanel>
            </widget>
          </VerticalPanel>
        </VerticalPanel>
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <VerticalPanel>
            <table key="analyteTable" width="auto" maxRows="14" showScroll="ALWAYS" style="atozTable">
              <col key="analyteReportable" width="75" header="{resource:getString($constants,'reportable')}">
                <check />
              </col>
              <col width="175" header="{resource:getString($constants,'analytes')}">
                <label field="String" />
              </col>
            </table>
          </VerticalPanel>
        </CollapsePanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
