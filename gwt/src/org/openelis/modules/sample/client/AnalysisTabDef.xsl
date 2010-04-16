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

  <xsl:template name="AnalysisTab">
    <VerticalPanel padding="0" spacing="0">
      <TablePanel style="Form">
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'test')" />:
          </text>
          <autoComplete key="{meta:getAnalysisTestName()}" width="150" case="LOWER" popWidth="auto" field="Integer">
            <col width="170" header="{resource:getString($constants,'test')}" />
            <col width="150" header="{resource:getString($constants,'method')}" />
            <col width="290" header="{resource:getString($constants,'description')}" />
          </autoComplete>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'method')" />:
          </text>
          <widget colspan="3">
          <autoComplete key="{meta:getAnalysisMethodName()}" width="125" case="LOWER" popWidth="auto" field="Integer">
            <col width="150" header="{resource:getString($constants,'method')}" />
          </autoComplete>
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'started')" />:
          </text>
          <calendar key="{meta:getAnalysisStartedDate()}" width="125" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
        <row>
        <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'status')" />:
          </text>
          <dropdown key="{meta:getAnalysisStatusId()}" width="150" popWidth="150" field="Integer" />
        <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'section')" />:
          </text>
          <widget colspan="3">
          <dropdown key="{meta:getAnalysisSectionId()}" width="125" case="LOWER" popWidth="150" field="Integer" />
          </widget>
           <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'completed')" />:
          </text>
          <calendar key="{meta:getAnalysisCompletedDate()}" width="125" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
          <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'unit')" />:
          </text>
          <dropdown key="{meta:getAnalysisUnitOfMeasureId()}" width="150" popWidth="150" field="Integer" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'revision')" />:
          </text>
          <textbox key="{meta:getAnalysisRevision()}" width="60" field="Integer" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'testReportable')" />:
          </text>
          <check key="{meta:getAnalysisIsReportable()}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'released')" />:
          </text>
          <calendar key="{meta:getAnalysisReleasedDate()}" width="125" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
       </row>
       <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'samplePrep')" />:
          </text>
          <widget colspan="5">
          <autoComplete key="{meta:getAnalysisSamplePrep()}" width="350" popWidth="auto" field="Integer">
            <col width="350" header="Name" />
          </autoComplete>
          </widget>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'printed')" />:
          </text>
          <calendar key="{meta:getAnalysisPrintedDate()}" width="125" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
          </row>
      </TablePanel>
      <HorizontalPanel spacing="0" padding="0">
                   <VerticalPanel spacing="0" padding="0">
            <table key="worksheetTable" style="ScreenTableWithSides" width="auto" maxRows="3" showScroll="ALWAYS" title="">
            	<col width="75" header="Worksheet">
              		<label field="String"/>
            	</col>
            	<col width="115" header="Created">
              		<label field="String"/>
            	</col>
            	<col width="115" header="Status">
              		<label field="String"/>
            	</col>
            	<col width="115" header="User">
              		<label field="String"/>
            	</col>
            </table>
	            <HorizontalPanel style="TableFooterPanel">
                 <appButton key="selectWkshtButton" style="Button">
                   <HorizontalPanel>
                     <AbsolutePanel style="CommitButtonImage" />
                     <text>
                       <xsl:value-of select='resource:getString($constants,"select")' />
                     </text>
                   </HorizontalPanel>
                 </appButton>
                  </HorizontalPanel>
                  </VerticalPanel>
            <AbsolutePanel style="Divider" />
                  <VerticalPanel spacing="0" padding="0">
      		<table key="analysisUserTable" style="ScreenTableWithSides" width="auto" maxRows="3" showScroll="ALWAYS" title="">
            	<col width="115" header="User">
              		<label field="String"/>
            	</col>
            	<col width="110" header="Action">
              		<label field="String"/>
            	</col>
            </table>
            <HorizontalPanel style="TableFooterPanel">
               <appButton key="addActionButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeActionButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                  </HorizontalPanel>
                  </VerticalPanel>
      </HorizontalPanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
