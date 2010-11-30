
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

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/completeRelease/client/SampleTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisNotesTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AuxDataTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/QAEventsTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleItemTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleNotesTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/StorageTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/ResultTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'reviewAndRelease')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
<!--button panel code-->
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="updateButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <button key="complete" icon="completeIcon" text="{resource:getString($constants,'complete')}" style="ButtonPanelButton" action="complete"/>
            <button key="release" icon="reviewAndReleaseIcon" text="{resource:getString($constants,'release')}" style="ButtonPanelButton" action="release"/>

<!-- 
  
<appButton action="report" key="report" style="ButtonPanelButton">
<HorizontalPanel>
<AbsolutePanel height="20" style="finalReportButton" width="20" />
<text>
<xsl:value-of select='resource:getString($constants,"finalReport")' />
</text>
</HorizontalPanel>
</appButton>
  -->
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton" />
            <xsl:call-template name="abortButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <menu key="optionsMenu" selfShow="true" showBelow="true">
                <menuDisplay>
                  <button style="ButtonPanelButton" action="option">
                    <Grid cols="2">
                      <row>
                        <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'options')}" />
                        <cell style="OptionsButtonImage" />
                      </row>
                    </Grid>
                  </button>
                </menuDisplay>
                <menuItem key="unreleaseAnalysis" enabled="false" icon="unreleaseIcon" display="Unrelease Analysis" />
                <menu>
  					<menuDisplay>
  				    	<Grid cols="2">
  				    	  <row>
     				    	<check key="previewReport"/>
     						<text style="Prompt">Preview Final Report</text>
     					  </row>
     					</Grid>
 					</menuDisplay>
 				</menu>
                <separator/>
                <menuItem key="historySample" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySample')}" />
                <menuItem key="historySampleSpec" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleSpec')}" />
                <menuItem key="historySampleProject" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleProject')}" />
                <menuItem key="historySampleOrganization" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleOrganization')}" />
                <menuItem key="historySampleItem" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleItem')}" />
                <menuItem key="historyAnalysis" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historyAnalysis')}" />
                <menuItem key="historyCurrentResult" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historyCurrentResult')}" />
                <menuItem key="historyStorage" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historyStorage')}" />
                <menuItem key="historySampleQA" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleQA')}" />
                <menuItem key="historyAnalysisQA" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historyAnalysisQA')}" />
                <menuItem key="historyAuxData" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historyAuxData')}" />
            </menu>
          </HorizontalPanel>
        </AbsolutePanel>
        <table key="completeReleaseTable" rows="10" multiSelect="true" vscroll="ALWAYS" hscroll="ALWAYS">
          <col key="{meta:getAccessionNumber()}" width="115" header="Accession #" sort="true">
            <textbox field="Integer" />
          </col>
          <col key="{meta:getAnalysisTestName()}" width="192" header="Test" sort="true">
            <textbox field="String" />
          </col>
          <col key="{meta:getAnalysisMethodName()}" width="210" header="Method" sort="true">
            <textbox field="String" />
          </col>
          <col key="{meta:getAnalysisStatusId()}" width="101" header="Analysis Status" sort="true">
            <dropdown width="100" field="Integer" />
          </col>
          <col key="{meta:getStatusId()}" width="100" header="Sample Status" sort="true">
            <dropdown width="100" field="Integer" />
          </col>
        </table>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <TabPanel key="SampleContent" width="715" height="236">
<!-- Blank default deck -->
          <tab visible="false" text="">
            <AbsolutePanel />
          </tab>
<!-- Sample deck -->
          <tab tab="{meta:getAccessionNumber()},{meta:getClientReference()}" visible="false" text="Sample">
            <xsl:call-template name="SampleTab" />
          </tab>
<!-- Environmental deck -->
                <tab text="{resource:getString($constants,'environmental')}" visible="false" tab="{meta:getEnvIsHazardous()},{meta:getBillTo()}">
                  <AbsolutePanel key="envDomainPanel"/>
                </tab>
<!-- Private Well Deck -->
                <tab text="{resource:getString($constants,'privateWell')}" visible="false">
                  <AbsolutePanel key="privateWellDomainPanel"/>
                </tab>
<!--     SDWISS Tab -->
                <tab text="{resource:getString($constants,'sdwis')}" visible="false">
                  <AbsolutePanel key="sdwisDomainPanel"/>
                </tab> 
<!-- Sample Item deck -->
          <tab tab="{meta:getItemTypeOfSampleId()},{meta:getItemUnitOfMeasureId()}" visible="false" text="{resource:getString($constants,'sampleItem')}">
            <xsl:call-template name="SampleItemTab" />
          </tab>
<!-- Analysis deck -->
          <tab tab="{meta:getAnalysisTestName()},{meta:getAnalysisPrintedDate()}" visible="false" text="{resource:getString($constants,'analysis')}">
            <xsl:call-template name="AnalysisTab" />
          </tab>
<!-- Results deck -->
          <tab tab="testResultsTable,testResultsTable" visible="false" text="{resource:getString($constants,'testResults')}">
            <xsl:call-template name="ResultTab" />
          </tab>
<!-- Analysis Notes Deck -->
          <tab tab="anExNoteButton,anIntNoteButton" visible="false" text="{resource:getString($constants,'analysisNotes')}">
            <xsl:call-template name="AnalysisNotesTab" />
          </tab>
<!-- Sample Notes Deck -->
          <tab tab="sampleExtNoteButton,sampleIntNoteButton" visible="false" text="{resource:getString($constants,'sampleNotes')}">
            <xsl:call-template name="SampleNotesTab" />
          </tab>
<!-- Storage Deck -->
          <tab tab="storageTable,storageTable" visible="false" text="{resource:getString($constants,'storage')}">
            <xsl:call-template name="StorageTab" />
          </tab>
<!-- QA Events deck -->
          <tab tab="sampleQATable,analysisQATable" visible="false" text="{resource:getString($constants,'qaEvents')}">
            <xsl:call-template name="QAEventsTab" />
          </tab>
<!-- Aux Data deck -->
          <tab tab="auxValsTable,auxValsTable" visible="false" text="{resource:getString($constants,'auxData')}">
            <xsl:call-template name="AuxDataTab" />
          </tab>
        </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
