

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
    <screen name="{resource:getString($constants,'sdwisSampleLogin')}">
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
                <menuItem key="historySample" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySample')}" />
                <menuItem key="historySampleSDWIS" enabled="false" icon="historyIcon" display="{resource:getString($constants,'historySampleSDWIS')}" />
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
              <HorizontalPanel>
                <textbox key="{meta:getOrderId()}" width="75" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />
                <button key="orderButton" style="LookupButton">
                  <AbsolutePanel style="LookupButtonImage" />
                </button>
              </HorizontalPanel>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'collected')" />:
              </text>
              <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},{meta:getOrderId()}"/>
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
                <textbox key="{meta:getClientReference()}" width="196" max="20" tab="{meta:getSDWISPwsId()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
          </TablePanel>
          <VerticalPanel style="subform">
            <text style="FormTitle">
              <xsl:value-of select="resource:getString($constants,'sdwisInfo')" />
            </text>
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'pwsId')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getSDWISPwsId()}" width="75" max="9" case="UPPER" tab="pwsName,{meta:getClientReference()}" field="String" required="true"/>
                  <button key="pwsButton" style="LookupButton">
                    <AbsolutePanel style="LookupButtonImage" />
                  </button>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'pwsName')" />:
                </text>
                <textbox key="pwsName" width="250" tab="{meta:getSDWISStateLabId()},{meta:getSDWISPwsId()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'stateLabNo')" />:
                </text>
                <textbox key="{meta:getSDWISStateLabId()}" width="102" tab="{meta:getSDWISFacilityId()},pwsName" field="Integer" required="true"/>
              </row>
              <row>
              
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'facilityId')" />:
                </text>
                <textbox key="{meta:getSDWISFacilityId()}" width="75" max="12" tab="{meta:getSDWISSampleTypeId()},{meta:getSDWISStateLabId()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'sampleType')" />:
                </text>
                <dropdown key="{meta:getSDWISSampleTypeId()}" width="120" tab="{meta:getSDWISSampleCategoryId()},{meta:getSDWISFacilityId()}" field="Integer" required="true"/>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'sampleCat')" />:
                </text>
                <dropdown key="{meta:getSDWISSampleCategoryId()}" width="108" tab="{meta:getSDWISSamplePointId()},{meta:getSDWISSampleTypeId()}" field="Integer" required="true"/>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'samplePtId')" />:
                </text>
                <textbox key="{meta:getSDWISSamplePointId()}" width="75" max="11" tab="{meta:getSDWISLocation()},{meta:getSDWISSampleCategoryId()}" field="String" required="true"/>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'pointDesc')" />:
                </text>
                <textbox key="{meta:getSDWISLocation()}" width="250" max="20" tab="itemsTestsTree,{meta:getSDWISSamplePointId()}" field="String" />
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
                  <tree key="itemsTestsTree" rows="4" vscroll="ALWAYS" hscroll="ALWAYS" tab="{meta:getSDWISCollector()},{meta:getSDWISLocation()}">
                    <columns>
                      <col width="280" header="{resource:getString($constants,'itemAnalyses')}" />
                      <col width="130" header="{resource:getString($constants,'typeStatus')}" />
                    </columns>
                    <node key="sampleItem">
                      <col>
                        <label field="String" />
                      </col>
                      <col>
                        <label field="String" />
                      </col>
                    </node>
                    <node key="analysis">
                      <col>
                        <label field="String" />
                      </col>
                      <col>
                        <dropdown width="110" case="LOWER" field="String" />
                      </col>
                    </node>
                  </tree>
                </row>
                <row>
                  <widget style="TreeButtonFooter">
                    <HorizontalPanel>
                      <button key="addItemButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addItem')}" style="Button"/>
                      <button key="addAnalysisButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addAnalysis')}" style="Button"/>
                      <button key="removeRowButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                      <button key="popoutTree" icon="popoutButtonImage" text="{resource:getString($constants,'popout')}" style="Button"/>
                    </HorizontalPanel>
                  </widget>
                </row>
              </TablePanel>
            </VerticalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'collectorOrgInfo')" />
              </text>
              <TablePanel style="Form">
                <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collector')" />:
                </text>
                <textbox key="{meta:getSDWISCollector()}" width="162" max="20" tab="{meta:getOrgName()},itemsTestsTree" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <widget colspan="3">
                    <HorizontalPanel>
                      <autoComplete key="{meta:getOrgName()}" width="179" case="UPPER" tab="{meta:getBillTo()},{meta:getSDWISCollector()}" >
                        <col width="200" header="{resource:getString($constants,'name')}" />
                        <col width="130" header="{resource:getString($constants,'street')}" />
                        <col width="120" header="{resource:getString($constants,'city')}" />
                        <col width="20" header="{resource:getString($constants,'st')}" />
                      </autoComplete>
                      <button key="reportToLookup" style="LookupButton">
                        <AbsolutePanel style="LookupButtonImage" />
                      </button>
                    </HorizontalPanel>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'billTo')" />:
                  </text>
                  <widget colspan="3">
                    <HorizontalPanel>
                      <autoComplete key="{meta:getBillTo()}" width="179" case="UPPER" tab="sampleItemTabPanel,{meta:getOrgName()}" >
                        <col width="200" header="{resource:getString($constants,'name')}" />
                        <col width="130" header="{resource:getString($constants,'street')}" />
                        <col width="120" header="{resource:getString($constants,'city')}" />
                        <col width="20" header="{resource:getString($constants,'st')}" />
                      </autoComplete>
                      <button key="billToLookup" style="LookupButton">
                        <AbsolutePanel style="LookupButtonImage" />
                      </button>
                    </HorizontalPanel>
                  </widget>
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
