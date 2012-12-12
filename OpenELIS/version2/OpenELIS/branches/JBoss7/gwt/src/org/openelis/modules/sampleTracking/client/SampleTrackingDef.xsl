

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
    <screen id="SampleTracking" name="SampleTracking">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <appButton action="expand" key="expand" style="ButtonPanelButton">
              <HorizontalPanel>
                <AbsolutePanel height="20" style="expandButtonImage" width="20" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"expand")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton action="collapse" key="collapse" style="ButtonPanelButton">
              <HorizontalPanel>
                <AbsolutePanel height="20" style="collapseButtonImage" width="20" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"collapse")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton action="similar" key="similar" style="ButtonPanelButton">
              <HorizontalPanel>
                <AbsolutePanel height="20" style="similarButtonImage" width="20" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"similar")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <xsl:call-template name="buttonPanelDivider" />
            <appButton action="query" key="query" shortcut="ctrl+q" style="ButtonPanelButton" toggle="true">
                <HorizontalPanel>
                  <AbsolutePanel height="20" style="QueryButtonImage" width="20" />
                  <text>
                    <xsl:value-of select='resource:getString($constants,"query")' />
                  </text>
                </HorizontalPanel>
             </appButton>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="updateButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <appButton action="addTest" key="addTest" style="ButtonPanelButton">
              <HorizontalPanel>
                <AbsolutePanel height="20" style="addTestButtonImage" width="20" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"addTest")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton action="cancelTest" key="cancelTest" style="ButtonPanelButton">
              <HorizontalPanel>
                <AbsolutePanel height="20" style="cancelTestButtonImage" width="20" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"cancelTest")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton" />
            <xsl:call-template name="abortButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
              <menuItem>
                <menuDisplay>
                  <appButton style="ButtonPanelButton" action="option">
                    <HorizontalPanel>
                      <text>
                        <xsl:value-of select='resource:getString($constants,"options")' />
                      </text>
                      <AbsolutePanel width="20px" height="20px" style="OptionsButtonImage" />
                    </HorizontalPanel>
                  </appButton>
                </menuDisplay>
                 <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem key="unreleaseSample" description="" enable="false" icon="unreleaseIcon" label="Unrelease Sample" />
                  <menuItem key="previewFinalReport" description="" enable="false" icon="unreleaseIcon" label = "{resource:getString($constants,'viewFinalReport')}" />  					
                  <menuItem key="changeDomain" description="" enable="false" icon="unreleaseIcon" label = "{resource:getString($constants,'changeDomain')}" />
                  <html>&lt;hr/&gt;</html>
                  <menuItem key="historySample" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySample')}" />
                  <menuItem key="historySampleSpec" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleSpec')}" />
                  <menuItem key="historySampleProject" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleProject')}" />
                  <menuItem key="historySampleOrganization" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleOrganization')}" />
                  <menuItem key="historySampleItem" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleItem')}" />
                  <menuItem key="historyAnalysis" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historyAnalysis')}" />
                  <menuItem key="historyCurrentResult" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historyCurrentResult')}" />
			      <menuItem key="historyStorage" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historyStorage')}" />
                  <menuItem key="historySampleQA" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleQA')}" />
                  <menuItem key="historyAnalysisQA" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historyAnalysisQA')}" />
                  <menuItem key="historyAuxData" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historyAuxData')}" />
                </menuPanel>
              </menuItem>
            </menuPanel>
          </HorizontalPanel>
        </AbsolutePanel>
        <HorizontalPanel>
          <AbsolutePanel key="collapsePanel" style="LeftSidePanel">
            <HorizontalPanel width="225">
              <VerticalPanel>
                <tree key="trackingTree" maxRows="14" width="auto">
                  <header>
                    <col header="Sample" width="200" />
                    <col header="Type/Status" width="100" />
                  </header>
                  <leaf key="sample">
                    <col>
                      <label field="Integer"/>
                    </col>
                    <col>
                       <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="sampleItem">
                    <col>
                      <label field="String"/>
                    </col>
                    <col>
                       <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="analysis">
                    <col>
                      <label field="String"/>
                    </col>
                    <col>
                       <dropdown width="110" case="LOWER" popWidth="110" field="Integer" />
                    </col>
                  </leaf>
                  <leaf key="storage">
                    <col>
                      <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="qaevent">
                    <col>
                      <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="note">
                    <col>
                      <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="auxdata">
                    <col>
                      <label field="String"/>
                    </col>
                  </leaf>
                  <leaf key="result">
                    <col>
                      <label field="String"/>
                    </col>
                  </leaf>
                </tree>
                <widget>
                  <HorizontalPanel>
                    <HorizontalPanel width = "115"/>
                    <appButton enable="false" key="prevPage" style="Button">
                      <AbsolutePanel style="prevNavIndex" />
                    </appButton>
                    <appButton enable="false" key="nextPage" style="Button">
                      <AbsolutePanel style="nextNavIndex" />
                    </appButton>
                    <HorizontalPanel width = "70"/>
                    <appButton key="popoutTree" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="popoutButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'popout')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
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
              <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="90" maxValue="0" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},{meta:getOrderId()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'time')" />:
              </text>
              <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60" max="5" mask="{resource:getString($constants,'timeMask')}" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="125" maxValue="0" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="110" popWidth="110" tab="{meta:getClientReference()},{meta:getReceivedDate()}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'clntRef')" />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getClientReference()}" width="196" case="LOWER" max="20" tab="tabPanel,{meta:getStatusId()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
              </TablePanel>
              <TabPanel height="244" key="tabPanel" width="715">
<!-- Blank Default deck -->
                <tab text="" visible="false">
                  <AbsolutePanel />
                </tab>
<!-- Environmental deck -->
                <tab text="{resource:getString($constants,'environmental')}" visible="false" tab="{meta:getEnvIsHazardous()},{meta:getBillTo()}">
                  <AbsolutePanel key="envDomainPanel"/>
                </tab>
<!-- Private Well Deck -->
                <tab text="{resource:getString($constants,'privateWell')}" visible="false" tab="{meta:getWellOrganizationName()},{meta:getBillTo()}">
                  <AbsolutePanel key="privateWellDomainPanel"/>
                </tab>
<!--     SDWISS Tab -->
                <tab text="{resource:getString($constants,'sdwis')}" visible="false" tab="{meta:getSDWISPwsNumber0()},{meta:getBillTo()}">
                  <AbsolutePanel key="sdwisDomainPanel"/>
                </tab> 
<!--   Quick Entry Tab -->
                <tab text="{resource:getString($constants,'quickEntry')}" visible="false">
                  <AbsolutePanel key="quickEntryDomainPanel"/>
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
