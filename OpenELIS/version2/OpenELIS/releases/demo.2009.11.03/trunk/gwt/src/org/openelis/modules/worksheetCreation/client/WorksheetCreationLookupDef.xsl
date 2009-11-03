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
  xmlns:sectionMeta="xalan://org.openelis.meta.SectionMeta"
  xmlns:testMetaMap="xalan://org.openelis.metamap.TestMetaMap">

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
  <xalan:component prefix="sectionMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SectionMeta" />
  </xalan:component>
  <xalan:component prefix="testMetaMap">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestMetaMap" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="sample" select="sampleMetaMap:new()" />
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)" />
    <xsl:variable name="analysis" select="sampleItemMetaMap:getAnalysis($sampleItem)" />
    <xsl:variable name="test" select="analysisMetaMap:getTest($analysis)" />
    <xsl:variable name="method" select="testMetaMap:getMethod($test)" />
    <xsl:variable name="section" select="analysisMetaMap:getSection($analysis)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCreationLookup" name="{resource:getString($constants,'worksheetCreationLookup')}">
<!--
      <VerticalPanel padding="0" spacing="0">
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <appButton key="findButton" style="ButtonPanelButton" action="find">
              <HorizontalPanel>
                <AbsolutePanel style="FindButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'find')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
        </AbsolutePanel>
-->        
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <TablePanel style="Form">
          <row>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'test')" />:
            </text>
            <autoComplete key="{testMetaMap:getId($test)}" width="150px" case="LOWER" popWidth="auto" tab="{sectionMeta:getId($section)},analysisTable" field="Integer">
              <col width="150" header="Test" />
              <col width="150" header="Method" />
              <col width="200" header="Description" />
              <col width="75" header="Begin Date" />
              <col width="75" header="End Date" />
            </autoComplete>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'method')" />:
            </text>
            <textbox key="{methodMeta:getName($method)}" width="150px" case="LOWER" field="String" />
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'section')" />:
            </text>
            <dropdown key="{sectionMeta:getId($section)}" width="150px" popWidth="150px" tab="{sampleMetaMap:getAccessionNumber($sample)},testMetaMap:getId($test)" field="Integer" />
          </row>
          <row>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
            </text>
            <textbox key="{sampleMetaMap:getAccessionNumber($sample)}" width="75px" tab="{analysisMetaMap:getStatusId($analysis)},{sectionMeta:getId($section)}" field="Integer" />
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'status')" />:
            </text>
            <dropdown key="{analysisMetaMap:getStatusId($analysis)}" width="110px" popWidth="110px" tab="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)},{sampleMetaMap:getAccessionNumber($sample)}" field="Integer" />
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'sampleType')" />:
            </text>
            <dropdown key="{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}" width="150px" popWidth="150px" tab="{sampleMetaMap:getReceivedDate($sample)},analysisMetaMap:getStatusId($analysis)" field="Integer" />
          </row>
          <row>
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'received')" />:
            </text>
            <calendar key="{sampleMetaMap:getReceivedDate($sample)}" begin="0" end="4" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{sampleMetaMap:getEnteredDate($sample)},{sampleItemMetaMap:getTypeOfSampleId($sampleItem)}" />
            <text style="Prompt">
              <xsl:value-of select="resource:getString($constants,'entered')" />:
            </text>
            <calendar key="{sampleMetaMap:getEnteredDate($sample)}" begin="0" end="4" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="analysisTable,{sampleMetaMap:getReceivedDate($sample)}" />
            <appButton key="findButton" style="Button" action="find">
              <HorizontalPanel>
                <AbsolutePanel style="FindButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'find')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </row>
        </TablePanel>
        <table key="analysesTable" width="800px" maxRows="9" showScroll="ALWAYS" tab="{testMetaMap:getId($test)},{sampleMetaMap:getEnteredDate($sample)}" title="" style="atozTable">
          <col width="90" header="{resource:getString($constants,'accessionNum')}">
            <label />
          </col>
          <col width="150" header="{resource:getString($constants,'description')}">
            <label />
          </col>
<!-- 
          <col width="150" header="{resource:getString($constants,'project')}">
            <label />
          </col>
-->
          <col width="150" header="{resource:getString($constants,'test')}">
            <label />
          </col>
          <col width="150" header="{resource:getString($constants,'method')}">
            <label />
          </col>
          <col width="150" header="{resource:getString($constants,'section')}">
            <label />
          </col>
          <col width="75" header="{resource:getString($constants,'status')}">
            <label />
          </col>
          <col width="75" header="{resource:getString($constants,'collected')}">
            <calendar pattern="{resource:getString($constants,'datePattern')}" begin="0" end="2"/>
          </col>
          <col width="100" header="{resource:getString($constants,'received')}">
            <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
          </col>
        </table>
        <widget style="TableFooterPanel">
          <HorizontalPanel>
            <appButton key="addButton" style="Button" action="add">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'add')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="selectAllButton" style="Button" action="selectAll">
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
<!--      </VerticalPanel>   -->
    </screen>
  </xsl:template>
</xsl:stylesheet>
