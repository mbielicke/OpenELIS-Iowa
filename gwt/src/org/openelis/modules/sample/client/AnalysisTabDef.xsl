

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

  <xsl:template name="AnalysisTab">
    <VerticalPanel padding="0" spacing="0">
      <TablePanel style="Form">
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'test')" />:
          </text>
          <autoComplete case="LOWER" field="Integer" key="{meta:getAnalysisTestName()}" popWidth="auto" width="150px">
            <col header="{resource:getString($constants,'test')}" width="150" />
            <col header="{resource:getString($constants,'method')}" width="150" />
            <col header="{resource:getString($constants,'description')}" width="200" />
          </autoComplete>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'method')" />:
          </text>
          <autoComplete case="LOWER" field="Integer" key="{meta:getAnalysisMethodName()}" popWidth="auto" width="150px">
            <col header="{resource:getString($constants,'method')}" width="150" />
          </autoComplete>
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'status')" />:
          </text>
          <dropdown field="Integer" key="{meta:getAnalysisStatusId()}" popWidth="150px" width="150px" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'revision')" />:
          </text>
          <textbox field="Integer" key="{meta:getAnalysisRevision()}" width="60px" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'testReportable')" />:
          </text>
          <check key="{meta:getAnalysisIsReportable()}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'section')" />:
          </text>
          <dropdown case="LOWER" field="Integer" key="{meta:getAnalysisSectionName()}" popWidth="150px" width="150px" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'unit')" />:
          </text>
          <dropdown field="Integer" key="{meta:getAnalysisUnitOfMeasureId()}" popWidth="150px" width="150px" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'samplePrep')" />:
          </text>
          <dropdown field="Integer" key="{meta:getAnalysisSamplePrep()}" popWidth="350px" width="350px" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'started')" />:
          </text>
          <calendar begin="0" end="2" key="{meta:getAnalysisStartedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'completed')" />:
          </text>
          <calendar begin="0" end="2" key="{meta:getAnalysisCompletedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
        </row>
        <row>
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'released')" />:
          </text>
          <calendar begin="0" end="2" key="{meta:getAnalysisReleasedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'printed')" />:
          </text>
          <calendar begin="0" end="2" key="{meta:getAnalysisPrintedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" />
        </row>
      </TablePanel>
    </VerticalPanel>
  </xsl:template>
</xsl:stylesheet>
