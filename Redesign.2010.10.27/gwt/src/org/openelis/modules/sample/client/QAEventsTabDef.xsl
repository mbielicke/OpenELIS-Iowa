
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

  <xsl:template name="QAEventsTab">
    <VerticalPanel padding="0" spacing="0">
      <TablePanel height="100%" padding="0" spacing="0" style="TabSubForm">
        <row>
          <table key="sampleQATable" width="auto" maxRows="8" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
            <col key="{meta:getSampleSubQaName()}" width="172" header="{resource:getString($constants,'sampleQAEvent')}">
              <textbox field="String" />
            </col>
            <col key="{meta:getSampleQaTypeId()}" width="90" header="{resource:getString($constants,'type')}">
              <dropdown width="90" field="Integer" />
            </col>
            <col key="{meta:getSampleQaIsBillable()}" width="61" header="{resource:getString($constants,'billable')}">
              <check />
            </col>
          </table>
          <widget rowspan="3">
            <AbsolutePanel style="Divider" />
          </widget>
          <table key="analysisQATable" width="auto" maxRows="8" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
            <col key="{meta:getAnalysisSubQaName()}" width="172" header="{resource:getString($constants,'analysisQAEvent')}">
              <textbox field="String" />
            </col>
            <col key="{meta:getAnalysisQaTypeId()}" width="90" header="{resource:getString($constants,'type')}">
              <dropdown width="90" field="Integer" />
            </col>
            <col key="{meta:getAnalysisQaIsBillable()}" width="60" header="{resource:getString($constants,'billable')}">
              <check />
            </col>
          </table>
        </row>
        <row>
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
        </row>
      </TablePanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
