
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
            <col width="150" header="{resource:getString($constants,'test')}" />
            <col width="150" header="{resource:getString($constants,'method')}" />
            <col width="200" header="{resource:getString($constants,'description')}" />
          </autoComplete>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'method')" />:
          </text>
          <autoComplete key="{meta:getAnalysisMethodName()}" width="150" case="LOWER" popWidth="auto" field="Integer">
            <col width="150" header="{resource:getString($constants,'method')}" />
          </autoComplete>
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'status')" />:
          </text>
          <dropdown key="{meta:getAnalysisStatusId()}" width="150" popWidth="150" field="Integer" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'revision')" />:
          </text>
          <textbox key="{meta:getAnalysisRevision()}" width="60" field="Integer" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'testReportable')" />:
          </text>
          <check key="{meta:getAnalysisIsReportable()}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'section')" />:
          </text>
          <dropdown key="{meta:getAnalysisSectionName()}" width="150" case="LOWER" popWidth="150" field="Integer" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'unit')" />:
          </text>
          <dropdown key="{meta:getAnalysisUnitOfMeasureId()}" width="150" popWidth="150" field="Integer" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'samplePrep')" />:
          </text>
          <autoComplete key="{meta:getAnalysisSamplePrep()}" width="350" popWidth="auto" field="Integer">
            <col width="350" header="Name" />
          </autoComplete>
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'started')" />:
          </text>
          <calendar key="{meta:getAnalysisStartedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'completed')" />:
          </text>
          <calendar key="{meta:getAnalysisCompletedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'released')" />:
          </text>
          <calendar key="{meta:getAnalysisReleasedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'printed')" />:
          </text>
          <calendar key="{meta:getAnalysisPrintedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
        </row>
      </TablePanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
