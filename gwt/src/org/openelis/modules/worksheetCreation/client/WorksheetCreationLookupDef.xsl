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
<xsl:stylesheet extension-element-prefixes="resource" 
				version="1.0" 
				xmlns:analysisMetaMap="xalan://org.openelis.metamap.AnalysisMetaMap" 
				xmlns:locale="xalan://java.util.Locale" 
				xmlns:methodMeta="xalan://org.openelis.metamap.MethodMeta" 
				xmlns:resource="xalan://org.openelis.util.UTFResource" 
				xmlns:sampleMetaMap="xalan://org.openelis.metamap.SampleMetaMap" 
				xmlns:sampleItemMetaMap="xalan://org.openelis.metamap.SampleItemMetaMap" 
				xmlns:testMetaMap="xalan://org.openelis.metamap.TestMetaMap" 
				xmlns:xalan="http://xml.apache.org/xalan" 
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
				xsi:noNamespaceSchemaLocation="../../../../../../../../OpenELIS-Lib/src/org/openelis/gwt/public/ScreenSchema.xsd"
				xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform ../../../../../../../../OpenELIS-Lib/src/org/openelis/gwt/public/XSLTSchema.xsd"> 
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
  <xsl:template match="doc">
    <xsl:variable name="sample" select="sampleMetaMap:new()" />
    <xsl:variable name="sampleItem" select="sampleMetaMap:getSampleItem($sample)" />
    <xsl:variable name="analysis" select="sampleItemMetaMap:getAnalysis($sampleItem)" />
    <xsl:variable name="test" select="analysisMetaMap:getTest($analysis)" />
    <xsl:variable name="method" select="testMetaMap:getMethod($test)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'worksheetCreationLookup')}" id="WorksheetCreationLookup">
      <VerticalPanel padding="0" style="WhiteContentPanel" spacing="0">
        <TablePanel style="Form">
          <row>
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'test')" />:</text>
            <autoComplete width="150px" case="LOWER" key="{testMetaMap:getName($test)}" tab="{sampleMetaMap:getAccessionNumber($sample)},selectAll" popWidth="auto" field="Integer">
              <col width="150" header="Test" />
              <col width="150" header="Method" />
              <col width="200" header="Description" />
              <col width="200" header="Active Begin" />
              <col width="200" header="Active End" />
            </autoComplete>
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'method')" />:</text>
            <autoComplete width="150px" case="LOWER" key="{methodMeta:getName($method)}" popWidth="auto" field="Integer">
              <col width="150" header="Method" />
            </autoComplete>
          </row>
          <row>
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'accessionNum')" />:</text>
            <textbox width="75px" key="{sampleMetaMap:getAccessionNumber($sample)}" required="true" tab="{analysisMetaMap:getStatusId($analysis)},{testMetaMap:getName($test)}" field="Integer" />
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')" />:</text>
            <dropdown width="110px" key="{analysisMetaMap:getStatusId($analysis)}" required="true" tab="{sampleMetaMap:getReceivedDate($sample)},{sampleMetaMap:getAccessionNumber($sample)}" popWidth="110px" field="Integer" />
            <appButton style="Button" key="findButton" action="find" tab="analysesTable,{sampleMetaMap:getEnteredDate($sample)}">
              <HorizontalPanel>
                <AbsolutePanel style="FindButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'find')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </row>
          <row>
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'received')" />:</text>
            <calendar width="110px" key="{sampleMetaMap:getReceivedDate($sample)}" pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="2" tab="{sampleMetaMap:getEnteredDate($sample)},{analysisMetaMap:getStatusId($analysis)}" />
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'entered')" />:</text>
            <calendar width="110px" key="{sampleMetaMap:getEnteredDate($sample)}" pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="2" tab="findButton,{sampleMetaMap:getReceivedDate($sample)}" />
          </row>
        </TablePanel>
        <table width="auto" key="analysesTable" tab="addButton,findButton" title="" maxRows="9" showScroll="ALWAYS">
          <col width="106" header="{resource:getString($constants,'accessionNum')}">
            <label />
          </col>
          <col width="130" header="{resource:getString($constants,'test')}">
            <label />
          </col>
          <col width="130" header="{resource:getString($constants,'method')}">
            <label />
          </col>
          <col width="130" header="{resource:getString($constants,'section')}">
            <label />
          </col>
          <col width="130" header="{resource:getString($constants,'status')}">
            <label />
          </col>
          <col width="130" header="{resource:getString($constants,'received')}">
            <label />
          </col>
        </table>
        <HorizontalPanel style="WhiteContentPanel">
          <widget halign="center" style="WhiteContentPanel">
            <appButton style="Button" key="addButton" action="add" tab="selectAllButton,analysesTable">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'add')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="center" style="WhiteContentPanel">
            <appButton style="Button" key="selectAllButton" action="selectAll" tab="{testMetaMap:getName($test)},addButton">
              <text>
                <xsl:value-of select="resource:getString($constants,'selectAll')" />
              </text>
            </appButton>
          </widget>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>