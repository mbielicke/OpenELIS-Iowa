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
  xmlns:analysisMetaMap="xalan://org.openelis.metamap.AnalysisMetaMap"
  xmlns:methodMeta="xalan://org.openelis.meta.MethodMeta"
  xmlns:sampleItemMetaMap="xalan://org.openelis.metamap.SampleItemMetaMap"
  xmlns:sampleMetaMap="xalan://org.openelis.metamap.SampleMetaMap"
  xmlns:testMetaMap="xalan://org.openelis.metamap.TestMetaMap"
  xmlns:worksheetMetaMap="xalan://org.openelis.metamap.WorksheetMetaMap">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="analysisMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AnalysisMetaMap" />
  </xalan:component>
  <xalan:component prefix="methodMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.MethodMeta" />
  </xalan:component>
  <xalan:component prefix="sampleMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleMetaMap" />
  </xalan:component>
  <xalan:component prefix="sampleItemMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleItemMetaMap" />
  </xalan:component>
  <xalan:component prefix="testMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestMetaMap" />
  </xalan:component>
  <xalan:component prefix="worksheetMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.WorksheetMetaMap" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="sample" select="sampleMetaMap:new()" />
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)" />
    <xsl:variable name="analysis" select="sampleItemMetaMap:getAnalysis($sampleItem)" />
    <xsl:variable name="test" select="analysisMetaMap:getTest($analysis)" />
    <xsl:variable name="method" select="testMetaMap:getMethod($test)" />
    <xsl:variable name="worksheet" select="worksheetMetaMap:new()" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCreation" name="{resource:getString($constants,'worksheetCreation')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <TablePanel style="Form">
          <row>
	        <text style="Prompt">
	          <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
	        </text>
	        <textbox key="{worksheetMetaMap:getId($worksheet)}" width="100px" case="LOWER" field="String" tab="worksheetItemTable,worksheetItemTable" />
	        <appButton key="saveButton" style="Button" action="save" >
	          <HorizontalPanel>
	            <AbsolutePanel style="SaveButtonImage" />
	            <text>
	              <xsl:value-of select="resource:getString($constants,'save')" />
	            </text>
	          </HorizontalPanel>
	        </appButton>
	        <appButton key="exitButton" style="Button" action="exit">
	          <HorizontalPanel>
	            <AbsolutePanel style="ExitButtonImage" />
	            <text>
	              <xsl:value-of select="resource:getString($constants,'exit')" />
	            </text>
	          </HorizontalPanel>
	        </appButton>
          </row>
        </TablePanel>
        <table key="worksheetItemTable" width="auto" maxRows="9" showScroll="ALWAYS" tab="{worksheetMetaMap:getId($worksheet)},{worksheetMetaMap:getId($worksheet)}" title="" style="atozTable">
          <col width="50" header="{resource:getString($constants,'wellNumber')}" sort="false">
            <label />
          </col>
          <col width="50" header="{resource:getString($constants,'QC')}" sort="false">
            <label />
          </col>
          <col width="90" header="{resource:getString($constants,'accessionNum')}" sort="true">
            <label />
          </col>
          <col width="150" header="{resource:getString($constants,'description')}" sort="true">
            <label />
          </col>
          <col width="100" header="{resource:getString($constants,'test')}" sort="true">
            <label />
          </col>
          <col width="100" header="{resource:getString($constants,'method')}" sort="true">
            <label />
          </col>
          <col width="75" header="{resource:getString($constants,'status')}" sort="true">
            <label />
          </col>
          <col width="75" header="{resource:getString($constants,'collected')}" sort="true">
            <calendar pattern="{resource:getString($constants,'datePattern')}" begin="0" end="2"/>
          </col>
          <col width="100" header="{resource:getString($constants,'received')}" sort="true">
            <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
          </col>
        </table>
        <widget style="TableFooterPanel">
          <HorizontalPanel>
            <appButton key="insertQCButton" style="Button" action="insertQC">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'insertQC')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="removeRowButton" style="Button" action="removeRow">
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
    </screen>
  </xsl:template>
</xsl:stylesheet>
