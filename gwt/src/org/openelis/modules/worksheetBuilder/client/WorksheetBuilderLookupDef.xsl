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
  xmlns:meta="xalan://org.openelis.meta.AnalysisViewMeta">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetBuilderLookup" name="{resource:getString($constants,'worksheetBuilderLookup')}">
      <HorizontalPanel>
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <VerticalPanel>
            <table key="analyteTable" width="auto" maxRows="14" showScroll="ALWAYS" style="atozTable">
              <col width="175" header="{resource:getString($constants,'analytes')}">
                <label field="String" />
              </col>
            </table>
          </VerticalPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <HorizontalPanel>
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'test')" />:
                </text>
                <autoComplete key="{meta:getTestId()}" width="150" case="LOWER" popWidth="auto" tab="{meta:getSectionId()},analysisTable" field="Integer">
                  <col width="150" header="Test" />
                  <col width="150" header="Method" />
                  <col width="200" header="Description" />
                  <col width="75" header="Begin Date" />
                  <col width="75" header="End Date" />
                </autoComplete>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'method')" />:
                </text>
                <textbox key="{meta:getMethodName()}" width="150" case="LOWER" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'section')" />:
                </text>
                <dropdown key="{meta:getSectionId()}" width="130" popWidth="130px" tab="{meta:getAccessionNumber()},meta:getTestId()" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
                </text>
                <textbox key="{meta:getAccessionNumber()}" width="80" tab="{meta:getAnalysisStatusId()},{meta:getSectionId()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown key="{meta:getAnalysisStatusId()}" width="100" popWidth="100" tab="{meta:getTypeOfSampleId()},{meta:getAccessionNumber()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'sampleType')" />:
                </text>
                <dropdown key="{meta:getTypeOfSampleId()}" width="175" popWidth="175" tab="{meta:getReceivedDate()},meta:getAnalysisStatusId()" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'received')" />:
                </text>
                <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getEnteredDate()},{meta:getTypeOfSampleId()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'entered')" />:
                </text>
                <calendar key="{meta:getEnteredDate()}" begin="0" end="4" width="130" pattern="{resource:getString($constants,'dateTimePattern')}" tab="searchButton,{meta:getReceivedDate()}" />
              </row>
            </TablePanel>
            <widget halign="center" valign="middle">
              <appButton key="searchButton" style="Button" tab="analysisTable,{meta:getEnteredDate()}" action="search">
                <HorizontalPanel>
                  <AbsolutePanel style="FindButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'search')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
          </HorizontalPanel>
          <table key="analysesTable" width="800" maxRows="9" showScroll="ALWAYS" style="ScreenTableWithSides" tab="addButton,searchButton" title="">
            <col key="{meta:getAccessionNumber()}" width="90" sort="true" header="{resource:getString($constants,'accessionNum')}">
              <label field="Integer" />
            </col>
            <col key="{meta:getWorksheetDescription()}" width="150" sort="true" header="{resource:getString($constants,'description')}">
              <label field="String" />
            </col>
            <col key="{meta:getTestName()}" width="100" sort="true" header="{resource:getString($constants,'test')}">
              <label field="String" />
            </col>
            <col key="{meta:getMethodName()}" width="100" sort="true" header="{resource:getString($constants,'method')}">
              <label field="String" />
            </col>
            <col key="{meta:getSectionId()}" width="100" sort="true" header="{resource:getString($constants,'section')}">
              <dropdown width="100" field="Integer" />
            </col>
            <col key="{meta:getUnitOfMeasureId}" width="100" header="{resource:getString($constants,'unit')}">
              <dropdown width="100" field="Integer" />
            </col>
            <col key="{meta:getAnalysisStatusId()}" width="75" sort="true" header="{resource:getString($constants,'status')}">
              <dropdown width="55" field="Integer" />
            </col>
            <col key="{meta:getCollectionDate}" width="75" sort="true" header="{resource:getString($constants,'collected')}">
              <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
            </col>
            <col key="{meta:getReceivedDate}" width="110" sort="true" header="{resource:getString($constants,'received')}">
              <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
            </col>
            <col key="_analysis.dueDays" width="50" sort="true" header="{resource:getString($constants,'due')}">
              <label field="Integer" />
            </col>
            <col key="_analysis.expireDate" width="110" sort="true" header="{resource:getString($constants,'expire')}">
              <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
            </col>
            <col key="{meta:getPriority()}" width="65" sort="true" header="{resource:getString($constants,'priority')}">
              <label field="Integer" />
            </col>
          </table>
          <widget style="TableFooterPanel">
            <HorizontalPanel>
              <appButton key="addButton" style="Button" tab="selectAllButton,analysesTable" action="add">
                <HorizontalPanel>
                  <AbsolutePanel style="AddRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'add')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <appButton key="selectAllButton" style="Button" tab="{meta:getTestId()},addButton" action="selectAll">
                <HorizontalPanel>
                  <AbsolutePanel style="SelectAllButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'selectAll')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </HorizontalPanel>
          </widget>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
