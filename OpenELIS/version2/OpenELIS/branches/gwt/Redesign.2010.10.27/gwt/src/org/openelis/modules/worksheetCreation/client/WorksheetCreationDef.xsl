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

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'worksheetCreation')}">
      <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <button key="saveButton" icon="SaveButtonImage" text="{resource:getString($constants,'save')}" style="ButtonPanelButton" action="save"/>
            <button key="exitButton" icon="ExitButtonImage" text="{resource:getString($constants,'exit')}" style="ButtonPanelButton" action="exit"/>
          </HorizontalPanel>
        </AbsolutePanel>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
              </text>
              <textbox key="{meta:getWorksheetId()}" width="100" case="LOWER" field="String" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
              </text>
              <HorizontalPanel>
                <textbox key="{meta:getWorksheetRelatedWorksheetId()}" width="100" case="LOWER" field="String" />
                <button key="lookupWorksheetButton" style="LookupButton" tab="worksheetItemTable,removeRowButton" action="lookupWorksheet">
                  <AbsolutePanel style="LookupButtonImage" />
                </button>
              </HorizontalPanel>
            </row>
          </TablePanel>
          <table key="worksheetItemTable" width="800" rows="9" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides" tab="insertQCWorksheetButton,lookupWorksheetButton">
            <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}">
              <label field="String" />
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
              <button key="insertQCWorksheetButton" icon="AddRowButtonImage" text="{resource:getString($constants,'insertQCWorksheet')}" style="Button" tab="insertQCLookupButton,worksheetItemTable" action="insertQCWorksheet"/>
              <button key="insertQCLookupButton" icon="AddRowButtonImage" text="{resource:getString($constants,'insertQCLookup')}" style="Button" tab="removeRowButton,insertQCWorksheetButton" action="insertQCLookup"/>
              <button key="removeRowButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button" tab="{meta:getWorksheetRelatedWorksheetId()},insertQCLookupButton" action="removeRow"/>
            </HorizontalPanel>
          </widget>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
