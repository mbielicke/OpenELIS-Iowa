
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
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/TestResultsTabDef.xsl" />
  
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="SampleTracking" name="SampleTracking">
      <HorizontalPanel padding="0" spacing="0">
        <AbsolutePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <VerticalPanel>
              <tree key="atozTable" maxRows="15" style="atozTable" width="auto">
                <header>
                  <col header="Sample" width="200" />
                  <col header="Type/Status" width="100" />
                </header>
                <leaf key="sample">
                  <col>
                    <label />
                  </col>
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="item">
                  <col>
                    <label />
                  </col>
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="analysis">
                  <col>
                    <label />
                  </col>
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="storage">
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="qaevent">
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="note">
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="auxdata">
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="result">
                  <col>
                    <label />
                  </col>
                </leaf>
              </tree>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton enable="false" key="atozPrev" style="Button">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton enable="false" key="atozNext" style="Button">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </AbsolutePanel>
        <AbsolutePanel style="Divider" width="2"></AbsolutePanel>
        <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <menuItem>
                <menuDisplay>
                  <xsl:call-template name="queryButton" />
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem description="" icon="environmentalSampleLoginIcon" key="environmentalSample" label="{resource:getString($constants,'environmentalSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="false" icon="clinicalSampleLoginIcon" key="clinicalSample" label="{resource:getString($constants,'clinicalSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="false" icon="animalSampleLoginIcon" key="animalSample" label="{resource:getString($constants,'animalSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="false" icon="newbornScreeningSampleLoginIcon" key="newbornScreeningSample" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="false" icon="ptSampleLoginIcon" key="ptSample" label="{resource:getString($constants,'ptSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="false" icon="sdwisSampleLoginIcon" key="sdwisSample" label="{resource:getString($constants,'sdwisSampleLogin')}" style="TopMenuRowContainer" />
                  <menuItem description="" enable="true" icon="privateWellWaterSampleLoginIcon" key="privateWellWaterSample" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" style="TopMenuRowContainer" />
                </menuPanel>
              </menuItem>
              <xsl:call-template name="previousButton" />
              <xsl:call-template name="nextButton" />
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="updateButton" />
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton" />
              <xsl:call-template name="abortButton" />
            </HorizontalPanel>
          </AbsolutePanel>
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
                </text>
                <textbox field="Integer" key="{meta:getAccessionNumber()}" required="true" tab="orderNumber,SampleContent" width="75px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'orderNum')" />:
                </text>
                <textbox field="Integer" key="orderNumber" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" width="75px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collected')" />:
                </text>
                <calendar begin="0" end="2" key="{meta:getCollectionDate()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},orderNumber" width="80px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'time')" />:
                </text>
                <textbox begin="3" end="5" field="Date" key="{meta:getCollectionTime()}" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" width="60px" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'received')" />:
                </text>
                <calendar begin="0" end="4" key="{meta:getReceivedDate()}" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" width="110px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown field="Integer" key="{meta:getStatusId()}" popWidth="110px" required="true" tab="{meta:getClientReference()},{meta:getReceivedDate()}" width="110px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'clntRef')" />:
                </text>
                <widget colspan="3">
                  <textbox field="String" key="{meta:getClientReference()}" tab="SampleContent,{meta:getStatusId()}" width="175px" />
                </widget>
              </row>
            </TablePanel>
            <TabBar key="SampleBar" style="None" width="724"></TabBar>
            <DeckPanel height="254" key="SampleContent" width="724">
              <deck>
                <AbsolutePanel />
              </deck>
              <deck tab="{meta:getEnvIsHazardous()},{meta:getBillTo()}">
                <VerticalPanel width="98%">
                  <TablePanel style="Form" width="100%">
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                      </text>
                      <check key="{meta:getEnvIsHazardous()}" tab="{meta:getEnvPriority()},{meta:getClientReference()}" />
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'priority')" />:
                      </text>
                      <textbox field="Integer" key="{meta:getEnvPriority()}" tab="{meta:getEnvCollector()},{meta:getEnvIsHazardous()}" width="90px" />
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'collector')" />:
                      </text>
                      <textbox field="String" key="{meta:getEnvCollector()}" tab="{meta:getEnvCollectorPhone()},{meta:getEnvDescription()}" width="235px" />
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'phone')" />:
                      </text>
                      <textbox field="String" key="{meta:getEnvCollectorPhone()}" tab="{meta:getEnvSamplingLocation()},{meta:getEnvCollector()}" width="120px" />
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'location')" />:
                      </text>
                      <HorizontalPanel>
                        <textbox field="String" key="{meta:getEnvSamplingLocation()}" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" width="175px" />
                        <appButton key="locButton" style="LookupButton">
                          <AbsolutePanel style="LookupButtonImage" />
                        </appButton>
                      </HorizontalPanel>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'desc')" />:
                      </text>
                      <textbox field="String" key="{meta:getEnvDescription()}" tab="{meta:getProjectName()},{meta:getEnvSamplingLocation()}" width="315px" />
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'project')" />:
                      </text>
                      <HorizontalPanel>
                        <autoComplete case="UPPER" field="Integer" key="{meta:getProjectName()}" popWidth="auto" tab="{meta:getOrgName()},{meta:getEnvDescription()}" width="175px">
                          <col header="{resource:getString($constants,'name')}" width="115" />
                          <col header="{resource:getString($constants,'desc')}" width="190" />
                        </autoComplete>
                        <appButton key="projectLookup" style="LookupButton">
                          <AbsolutePanel style="LookupButtonImage" />
                        </appButton>
                      </HorizontalPanel>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                      </text>
                      <HorizontalPanel>
                        <autoComplete case="UPPER" field="Integer" key="{meta:getOrgName()}" popWidth="auto" tab="{meta:getBillTo()},{meta:getProjectName()}" width="175px">
                          <col header="{resource:getString($constants,'name')}" width="180" />
                          <col header="{resource:getString($constants,'street')}" width="110" />
                          <col header="{resource:getString($constants,'city')}" width="100" />
                          <col header="{resource:getString($constants,'st')}" width="20" />
                        </autoComplete>
                        <appButton key="reportToLookup" style="LookupButton">
                          <AbsolutePanel style="LookupButtonImage" />
                        </appButton>
                      </HorizontalPanel>
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'billTo')" />:
                      </text>
                      <HorizontalPanel>
                        <autoComplete case="UPPER" field="Integer" key="{meta:getBillTo()}" popWidth="auto" tab="{meta:getAccessionNumber()},{meta:getOrgName()}" width="175px">
                          <col header="{resource:getString($constants,'name')}" width="180" />
                          <col header="{resource:getString($constants,'street')}" width="110" />
                          <col header="{resource:getString($constants,'city')}" width="100" />
                          <col header="{resource:getString($constants,'st')}" width="20" />
                        </autoComplete>
                        <appButton key="billToLookup" style="LookupButton">
                          <AbsolutePanel style="LookupButtonImage" />
                        </appButton>
                      </HorizontalPanel>
                    </row>
                  </TablePanel>
                </VerticalPanel>
              </deck>
              <deck>
                <VerticalPanel width="98%">
                  <TablePanel style="Form" width="100%">
                    <row>
                      <text style="Prompt">Sampling Point:</text>
                      <textbox field="String" key="samplingpoint" width="120px" />
                      <text style="Prompt">Collector:</text>
                      <textbox field="String" key="collector" width="200px" />
                    </row>
                    <row>
                      <text style="Prompt">Owner:</text>
                      <textbox field="String" key="owner" tab="??,??" width="200px" />
                      <text style="Prompt">Depth:</text>
                      <textbox field="String" key="depth" width="80px" />
                    </row>
                  </TablePanel>
                  <VerticalPanel style="subform">
                    <text style="FormTitle">Results Reported To</text>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">Name:</text>
                        <widget colspan="3">
                          <textbox key="name" tab="??,??" width="180px" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                        </text>
                        <widget colspan="3">
                          <textbox case="UPPER" field="String" key="1" max="30" width="180px" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"address")' />:
                        </text>
                        <widget colspan="3">
                          <textbox case="UPPER" field="String" key="2" max="30" width="180px" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"city")' />:
                        </text>
                        <widget colspan="3">
                          <textbox case="UPPER" field="String" key="3" max="30" width="180px" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"state")' />:
                        </text>
                        <dropdown case="UPPER" field="String" key="4" tab="??,??" width="40px" />
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <textbox case="UPPER" field="String" key="5" max="30" width="73px" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"country")' />:
                        </text>
                        <widget colspan="3">
                          <dropdown field="String" key="6" tab="??,??" width="180px" />
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                </VerticalPanel>
              </deck>
              <deck tab="{meta:getItemTypeOfSampleId()},{meta:getItemUnitOfMeasureId()}">
                <xsl:call-template name="SampleItemTab" />
              </deck>
              <deck tab="{meta:getAnalysisTestName()},{meta:getAnalysisPrintedDate()}">
                <xsl:call-template name="AnalysisTab" />
              </deck>
              <deck tab="testResultsTable,testResultsTable">
                <xsl:call-template name="TestResultsTab" />
              </deck>
              <deck tab="anExNoteButton,anIntNoteButton">
                <xsl:call-template name="AnalysisNotesTab" />
              </deck>
              <deck tab="sampleExtNoteButton,sampleIntNoteButton">
                <xsl:call-template name="SampleNotesTab" />
              </deck>
              <deck tab="storageTable,storageTable">
                <xsl:call-template name="StorageTab" />
              </deck>
              <deck tab="sampleQATable,analysisQATable">
                <xsl:call-template name="QAEventsTab" />
              </deck>
              <deck tab="auxValsTable,auxValsTable">
                <xsl:call-template name="AuxDataTab" />
              </deck>
            </DeckPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
