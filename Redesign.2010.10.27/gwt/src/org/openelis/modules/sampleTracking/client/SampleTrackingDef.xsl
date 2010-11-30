

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

  <xsl:import href="IMPORT/button.xsl" />
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
    <screen name="SampleTracking">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <button action="expand" icon="expandButtonImage" text="{resource:getString($constants,'expand')}" key="expand" style="ButtonPanelButton"/>
            <button action="collapse" icon="collapseButtonImage" text="{resource:getString($constants,'collapse')}" key="collapse" style="ButtonPanelButton"/>
            <button action="similar" icon="similarButtonImage" text="{resource:getString($constants,'similar')}" key="similar" style="ButtonPanelButton"/>
            <xsl:call-template name="buttonPanelDivider" />
            <menu selfShow="true" showBelow="true">
              <menuDisplay>
                <button action="query" key="query" shortcut="ctrl+q" style="ButtonPanelButton" toggle="true">
                  <Grid cols="3">
                    <row>
                      <cell style="QueryButtonImage" />
                      <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'query')}" />
                      <cell style="OptionsButtonImage"/>
                    </row>
                  </Grid>
                </button>
              </menuDisplay>
              <menuItem icon="environmentalSampleLoginIcon" key="environmentalSample" display="{resource:getString($constants,'environmentalSample')}" style="TopMenuRowContainer" />
              <menuItem icon="privateWellWaterSampleLoginIcon" key="privateWellWaterSample" display="{resource:getString($constants,'privateWellWaterSample')}" style="TopMenuRowContainer" />
              <menuItem icon="sdwisSampleLoginIcon" key="sdwisSample" display="{resource:getString($constants,'sdwisSample')}" style="TopMenuRowContainer" />
              <menuItem enabled="false" icon="clinicalSampleLoginIcon" key="clinicalSample" display="{resource:getString($constants,'clinicalSample')}" style="TopMenuRowContainer" />
              <menuItem enabled="false" icon="newbornScreeningSampleLoginIcon" key="newbornScreeningSample" display="{resource:getString($constants,'newbornScreeningSample')}" style="TopMenuRowContainer" />
              <menuItem enabled="false" icon="animalSampleLoginIcon" key="animalSample" display="{resource:getString($constants,'animalSample')}" style="TopMenuRowContainer" />
              <menuItem enabled="false" icon="ptSampleLoginIcon" key="ptSample" display="{resource:getString($constants,'ptSample')}" style="TopMenuRowContainer" />
            </menu>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="updateButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <button action="addTest" icon="addTestButtonImage" text="{resource:getString($constants,'addTest')}" key="addTest" style="ButtonPanelButton"/>
            <button action="cancelTest" icon="cancelTestButtonImage" text="{resource:getString($constants,'cancelTest')}" key="cancelTest" style="ButtonPanelButton"/>
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
                <menuItem key="unreleaseSample" enabled="false" icon="unreleaseIcon" display="Unrelease Sample" />
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
        <HorizontalPanel>
          <AbsolutePanel key="collapsePanel" style="LeftSidePanel">
            <HorizontalPanel width="225">
              <VerticalPanel>
                <tree key="trackingTree" rows="14" vscroll="ALWAYS" hscroll="ALWAYS">
                  <columns>
                    <col header="Sample" width="200" />
                    <col header="Type/Status" width="100" />
                  </columns>
                  <node key="sample">
                    <col>
                      <label />
                    </col>
                    <col>
                       <label />
                    </col>
                  </node>
                  <node key="sampleItem">
                    <col>
                      <label />
                    </col>
                    <col>
                       <label/>
                    </col>
                  </node>
                  <node key="analysis">
                    <col>
                      <label />
                    </col>
                    <col>
                       <dropdown width="110" case="LOWER" field="Integer" />
                    </col>
                  </node>
                  <node key="storage">
                    <col>
                      <label />
                    </col>
                  </node>
                  <node key="qaevent">
                    <col>
                      <label />
                    </col>
                  </node>
                  <node key="note">
                    <col>
                      <label />
                    </col>
                  </node>
                  <node key="auxdata">
                    <col>
                      <label />
                    </col>
                  </node>
                  <node key="result">
                    <col>
                      <label />
                    </col>
                  </node>
                </tree>
                <widget halign="center">
                  <HorizontalPanel>
                    <button enabled="false" key="prevPage" style="Button">
                      <AbsolutePanel style="PreviousButtonImage" />
                    </button>
                    <button enabled="false" key="nextPage" style="Button">
                      <AbsolutePanel style="NextButtonImage" />
                    </button>
                  </HorizontalPanel>
                </widget>
              </VerticalPanel>
            </HorizontalPanel>
          </AbsolutePanel>
          <AbsolutePanel style="Divider" width="2"></AbsolutePanel>
          <VerticalPanel padding="0" spacing="0">
            <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
              <TablePanel style="Form">
               <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getAccessionNumber()}" width="75" tab="{meta:getOrderId()},tabPanel" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'orderNum')" />:
              </text>
              <textbox key="{meta:getOrderId()}" width="75" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'collected')" />:
              </text>
              <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},{meta:getOrderId()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'time')" />:
              </text>
              <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="125" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="110" tab="{meta:getClientReference()},{meta:getReceivedDate()}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'clntRef')" />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getClientReference()}" width="196" max="20" tab="tabPanel,{meta:getStatusId()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
              </TablePanel>
              <TabPanel height="247" key="tabPanel" width="715">
<!-- Blank Default deck -->
                <tab text="" visible="false">
                  <AbsolutePanel />
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
<!-- Sample Item Deck -->
                <tab text="{resource:getString($constants,'sampleItem')}" visible="false" tab="{meta:getItemTypeOfSampleId()},{meta:getItemUnitOfMeasureId()}">
                  <xsl:call-template name="SampleItemTab" />
                </tab>
<!-- Analysis deck -->
                <tab text="{resource:getString($constants,'analysis')}" visible="false" tab="{meta:getAnalysisTestName()},{meta:getAnalysisPrintedDate()}">
                  <xsl:call-template name="AnalysisTab" />
                </tab>
<!-- Results deck -->
                <tab text="{resource:getString($constants,'testResults')}" visible="false" tab="testResultsTable,testResultsTable">
                  <xsl:call-template name="ResultTab" />
                </tab>
<!-- Analysis Notes Deck -->
                <tab text="{resource:getString($constants,'analysisNotes')}" visible="false" tab="anExNoteButton,anIntNoteButton">
                  <xsl:call-template name="AnalysisNotesTab" />
                </tab>
<!-- Sample Notes Deck  -->
                <tab text="{resource:getString($constants,'sampleNotes')}" visible="false" tab="sampleExtNoteButton,sampleIntNoteButton">
                  <xsl:call-template name="SampleNotesTab" />
                </tab>
<!-- Storage deck -->
                <tab text="{resource:getString($constants,'storage')}" visible="false" tab="storageTable,storageTable">
                  <xsl:call-template name="StorageTab" />
                </tab>
<!-- QA Events Deck -->
                <tab text="{resource:getString($constants,'qaEvents')}" visible="false" tab="sampleQATable,analysisQATable">
                  <xsl:call-template name="QAEventsTab" />
                </tab>
<!-- Aux Data Deck -->
                <tab text="{resource:getString($constants,'auxData')}" visible="false" tab="auxValsTable,auxValsTable">
                  <xsl:call-template name="AuxDataTab" />
                </tab>
              </TabPanel>
            </VerticalPanel>
          </VerticalPanel>
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
