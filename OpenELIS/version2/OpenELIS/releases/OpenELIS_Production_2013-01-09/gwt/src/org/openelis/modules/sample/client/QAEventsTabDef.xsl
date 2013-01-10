
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

  <xsl:template name="QAEventsTab">
    <HorizontalPanel padding="0" spacing="0">
      <VerticalPanel>
        <table key="sampleQATable" maxRows="6" showScroll="ALWAYS" style="ScreenTableWithSides" tab="analysisQATable,analysisQATable" title="" width="auto">
          <col header="{resource:getString($constants,'sampleQAEvent')}" key="{meta:getSampleSubQaName()}" width="172">
            <label field="String" />
          </col>
          <col header="{resource:getString($constants,'type')}" key="{meta:getSampleQaTypeId()}" width="90">
            <dropdown field="Integer" width="90" />
          </col>
          <col key="{meta:getSampleQaIsBillable()}" width="61" header="{resource:getString($constants,'billable')}">
            <check />
          </col>
        </table>        
        <widget style="TableButtonFooter">
          <HorizontalPanel>
            <appButton key="removeSampleQAButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="RemoveRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'removeRow')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="sampleQAPicker" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="PickerButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'qaEvents')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
        </widget>
        <VerticalPanel height = "5"/>
        <TablePanel style="TabSubForm">
          <row>
            <text key = "sampleBillableLabel" style="WarningLabel" width = "320" wordwrap = "true"/>
          </row>
        </TablePanel>
      </VerticalPanel>
      <HorizontalPanel width="10" />
      <VerticalPanel>
        <table key="analysisQATable" maxRows="6" showScroll="ALWAYS" style="ScreenTableWithSides" tab="sampleQATable,sampleQATable" title="" width="auto">
          <col header="{resource:getString($constants,'analysisQAEvent')}" key="{meta:getAnalysisSubQaName()}" width="172">
            <label field="String" />
          </col>
          <col header="{resource:getString($constants,'type')}" key="{meta:getAnalysisQaTypeId()}" width="90">
            <dropdown field="Integer" width="90" />
          </col>
          <col key="{meta:getAnalysisQaIsBillable()}" width="60" header="{resource:getString($constants,'billable')}">
            <check />
          </col>
        </table>
        <widget style="TableButtonFooter">
          <HorizontalPanel>
            <appButton key="removeAnalysisQAButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="RemoveRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'removeRow')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="analysisQAPicker" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="PickerButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'qaEvents')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
        </widget>
        <VerticalPanel height = "5"/>
        <TablePanel style="TabSubForm">
          <row>
            <text key = "analysisBillableLabel" style="WarningLabel" width = "330" wordwrap = "true"/>
          </row>
        </TablePanel>
      </VerticalPanel>
    </HorizontalPanel>
  </xsl:template>
</xsl:stylesheet>