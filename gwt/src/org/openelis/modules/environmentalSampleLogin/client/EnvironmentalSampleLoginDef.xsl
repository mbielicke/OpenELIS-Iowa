
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
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisNotesTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AuxDataTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/QAEventsTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleItemTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleNotesTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/StorageTabDef.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/ResultTabDef.xsl" />
  <xsl:variable name="language" select="doc/locale" />
  <xsl:variable name="props" select="doc/props" />
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="EnvironmentalSampleLogin" name="{resource:getString($constants,'environmentalSampleLogin')}">
      <VerticalPanel padding="0" spacing="0">
<!--button panel code-->
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton" />
            <xsl:call-template name="previousButton" />
            <xsl:call-template name="nextButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="addButton" />
            <xsl:call-template name="updateButton" />
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
                      <AbsolutePanel width="20" height="20" style="OptionsButtonImage" />
                    </HorizontalPanel>
                  </appButton>
                </menuDisplay>
                <menuPanel layout="vertical" position="below" style="topMenuContainer">
                  <menuItem key="historySample" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySample')}" />
                  <menuItem key="historySampleEnvironmental" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleEnvironmental')}" />
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
<!--end button panel code-->
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getAccessionNumber()}" width="75" tab="{meta:getOrderId()},billTo" field="Integer" required="true" />
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
              <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
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
                <textbox key="{meta:getClientReference()}" width="196" max="20" tab="{meta:getEnvIsHazardous()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
          </TablePanel>
          <VerticalPanel width="98%" style="subform">
            <text style="FormTitle">
              <xsl:value-of select="resource:getString($constants,'envInfo')" />
            </text>
            <TablePanel width="100%" style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                </text>
                <check key="{meta:getEnvIsHazardous()}" tab="{meta:getEnvPriority()},{meta:getClientReference()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'priority')" />:
                </text>
                <textbox key="{meta:getEnvPriority()}" width="90" tab="{meta:getEnvCollector()},{meta:getEnvIsHazardous()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collector')" />:
                </text>
                <textbox key="{meta:getEnvCollector()}" width="235" max="40" tab="{meta:getEnvCollectorPhone()},{meta:getEnvPriority()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'phone')" />:
                </text>
                <textbox key="{meta:getEnvCollectorPhone()}" width="120" max="21" tab="{meta:getEnvLocation()},{meta:getEnvCollector()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'location')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getEnvLocation()}" width="175" max="40" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                  <appButton key="locButton" style="LookupButton">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'desc')" />:
                </text>
                <textbox key="{meta:getEnvDescription()}" width="315" max="40" tab="itemsTestsTree,{meta:getEnvLocation()}" field="String" />
              </row>
            </TablePanel>
          </VerticalPanel>
          <HorizontalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')" />
              </text>
              <TablePanel padding="0" spacing="0">
                <row>
                  <tree key="itemsTestsTree" width="auto" maxRows="4" showScroll="ALWAYS" tab="{meta:getProjectName()},{meta:getEnvDescription()}">
                    <header>
                      <col width="280" header="{resource:getString($constants,'itemAnalyses')}" />
                      <col width="130" header="{resource:getString($constants,'typeStatus')}" />
                    </header>
                    <leaf key="sampleItem">
                      <col>
                        <label field="String" />
                      </col>
                      <col>
                        <label field="String" />
                      </col>
                    </leaf>
                    <leaf key="analysis">
                      <col>
                        <label field="String" />
                      </col>
                      <col>
                        <dropdown width="110" case="LOWER" popWidth="110" field="String" />
                      </col>
                    </leaf>
                  </tree>
                </row>
                <row>
                  <widget style="TreeButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addItemButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addItem')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="addAnalysisButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addAnalysis')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeRowButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
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
                </row>
              </TablePanel>
            </VerticalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'organizationInfo')" />
              </text>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="175" case="UPPER" popWidth="auto" tab="{meta:getOrgName()},itemsTestsTree" field="Integer">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getOrgName()}" width="175" case="UPPER" popWidth="auto" tab="{meta:getBillTo()},{meta:getProjectName()}" field="Integer">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
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
                    <autoComplete key="{meta:getBillTo()}" width="175" case="UPPER" popWidth="auto" tab="sampleItemTabPanel,{meta:getOrgName()}" field="Integer">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="billToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
          <TabPanel key="sampleItemTabPanel" width="715" height="236">
            <tab key="tab0" tab="{meta:getItemTypeOfSampleId()},{meta:getItemUnitOfMeasureId()}" text="{resource:getString($constants,'sampleItem')}">
              <xsl:call-template name="SampleItemTab" />
            </tab>
            <tab key="tab1" tab="{meta:getAnalysisTestName()},{meta:getAnalysisPrintedDate()}" text="{resource:getString($constants,'analysis')}">
              <xsl:call-template name="AnalysisTab" />
            </tab>
            <tab key="tab2" tab="testResultsTable,testResultsTable" text="{resource:getString($constants,'testResults')}">
              <xsl:call-template name="ResultTab" />
            </tab>
            <tab key="tab3" tab="anExNoteButton,anIntNoteButton" text="{resource:getString($constants,'analysisNotes')}">
              <xsl:call-template name="AnalysisNotesTab" />
            </tab>
            <tab key="tab4" tab="sampleExtNoteButton,sampleIntNoteButton" text="{resource:getString($constants,'sampleNotes')}">
              <xsl:call-template name="SampleNotesTab" />
            </tab>
            <tab key="tab5" tab="storageTable,storageTable" text="{resource:getString($constants,'storage')}">
              <xsl:call-template name="StorageTab" />
            </tab>
            <tab key="tab6" tab="sampleQATable,analysisQATable" text="{resource:getString($constants,'qaEvents')}">
              <xsl:call-template name="QAEventsTab" />
            </tab>
            <tab key="tab7" tab="auxValsTable,auxValsTable" text="{resource:getString($constants,'auxData')}">
              <xsl:call-template name="AuxDataTab" />
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
