

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
            <menuItem>
              <menuDisplay>
                <appButton action="query" key="query" shortcut="ctrl+q" style="ButtonPanelButton" toggle="true">
                  <HorizontalPanel>
                    <AbsolutePanel height="20" style="QueryButtonImage" width="20" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"query")' />
                    </text>
                    <AbsolutePanel height="20px" style="OptionsButtonImage" width="20px" />
                  </HorizontalPanel>
                </appButton>
              </menuDisplay>
              <menuPanel layout="vertical" position="below" style="buttonMenuContainer">
                <menuItem description="" icon="environmentalSampleLoginIcon" key="environmentalSample" label="{resource:getString($constants,'environmentalSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="false" icon="clinicalSampleLoginIcon" key="clinicalSample" label="{resource:getString($constants,'clinicalSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="false" icon="animalSampleLoginIcon" key="animalSample" label="{resource:getString($constants,'animalSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="false" icon="newbornScreeningSampleLoginIcon" key="newbornScreeningSample" label="{resource:getString($constants,'newbornScreeningSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="false" icon="ptSampleLoginIcon" key="ptSample" label="{resource:getString($constants,'ptSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="false" icon="sdwisSampleLoginIcon" key="sdwisSample" label="{resource:getString($constants,'sdwisSample')}" style="TopMenuRowContainer" />
                <menuItem description="" enable="true" icon="privateWellWaterSampleLoginIcon" key="privateWellWaterSample" label="{resource:getString($constants,'privateWellWaterSample')}" style="TopMenuRowContainer" />
              </menuPanel>
            </menuItem>
            <xsl:call-template name="previousButton" />
            <xsl:call-template name="nextButton" />
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
                  <menuItem key="historySample" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySample')}" />
                  <menuItem key="historySampleEnvironmental" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleEnvironmental')}" />
                  <menuItem key="historySamplePrivateWell" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySamplePrivateWell')}" />
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
                <tree key="atozTable" maxRows="15" width="auto">
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
              <TabPanel height="266" key="SampleContent" width="724">
<!-- Blank Default deck -->
                <tab text="" visible="false">
                  <AbsolutePanel />
                </tab>
<!-- Environmental deck -->
                <tab text="{resource:getString($constants,'envInfo')}" visible="false" tab="{meta:getEnvIsHazardous()},{meta:getBillTo()}">
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
                        <textbox field="String" key="{meta:getEnvCollectorPhone()}" tab="{meta:getEnvLocation()},{meta:getEnvCollector()}" width="120px" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'location')" />:
                        </text>
                        <HorizontalPanel>
                          <textbox field="String" key="{meta:getEnvLocation()}" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" width="175px" />
                          <appButton key="locButton" style="LookupButton">
                            <AbsolutePanel style="LookupButtonImage" />
                          </appButton>
                        </HorizontalPanel>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'desc')" />:
                        </text>
                        <textbox field="String" key="{meta:getEnvDescription()}" tab="{meta:getProjectName()},{meta:getEnvLocation()}" width="315px" />
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
                </tab>
<!-- Private Well Deck -->
                <tab text="Private Well" visible="false">
                  <VerticalPanel width="98%">
                    <HorizontalPanel width="100%"> 
                      <TablePanel style="Form">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                          </text>
                          <widget colspan="3">
                            <autoComplete case="UPPER" field="Integer" key="{meta:getOrgName()}" popWidth="auto" tab="{meta:getWellOrganizationId()},{meta:getClientReference()}" width="180">
                              <col header="Name" width="180" />
                              <col header="Street" width="110" />
                              <col header="City" width="100" />
                              <col header="St" width="20" />
                            </autoComplete>
                          </widget>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'orgId')" />:
                          </text>
                          <textbox field="Integer" key="{meta:getWellOrganizationId()}" tab="{meta:getAddressMultipleUnit()},{meta:getOrgName()}" width="60px" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getAddressMultipleUnit()}" max="30" tab="{meta:getAddressStreetAddress()},{meta:getWellOrganizationId()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"address")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getAddressStreetAddress()}" max="30" tab="{meta:getAddressCity()},{meta:getAddressMultipleUnit()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"city")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getAddressCity()}" max="30" tab="{meta:getAddressState()},{meta:getAddressStreetAddress()}" width="180px" />
                          </widget>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'phone')" />:
                          </text>
                          <textbox field="String" key="{meta:getAddressWorkPhone()}" tab="{meta:getAddressFaxPhone()},{meta:getAddressZipCode()}" width="100px" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"state")' />:
                          </text>
                          <dropdown case="UPPER" field="String" key="{meta:getAddressState()}" tab="{meta:getAddressZipCode()},{meta:getAddressCity()}" width="40px" />
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                          </text>
                          <textbox case="UPPER" field="String" key="{meta:getAddressZipCode()}" max="30" tab="{meta:getAddressWorkPhone()},{meta:getAddressState()}" width="73px" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'faxNumber')" />:
                          </text>
                          <textbox field="String" key="{meta:getAddressFaxPhone()}" tab="{meta:getWellLocation()},{meta:getAddressWorkPhone()}" width="100px" />
                        </row>
                        <row></row>
                        </TablePanel>
                        <TablePanel style="Form">          
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'location')" />:
                          </text>
                          <widget colspan="3">
                            <textbox field="String" key="{meta:getWellLocation()}" tab="{meta:getLocationAddrMultipleUnit()},{meta:getAddressFaxPhone()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getLocationAddrMultipleUnit()}" max="30" tab="{meta:getLocationAddrStreetAddress()},{meta:getWellLocation()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"address")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getLocationAddrStreetAddress()}" max="30" tab="{meta:getLocationAddrCity()},{meta:getLocationAddrMultipleUnit()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"city")' />:
                          </text>
                          <widget colspan="3">
                            <textbox case="UPPER" field="String" key="{meta:getLocationAddrCity()}" max="30" tab="{meta:getLocationAddrState()},{meta:getLocationAddrStreetAddress()}" width="180px" />
                          </widget>
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"state")' />:
                          </text>
                          <dropdown case="UPPER" field="String" key="{meta:getLocationAddrState()}" tab="{meta:getLocationAddrZipCode()},{meta:getLocationAddrCity()}" width="40px" />
                          <text style="Prompt">
                            <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                          </text>
                          <textbox case="UPPER" field="String" key="{meta:getLocationAddrZipCode()}" max="30" tab="itemsTestsTree,{meta:getLocationAddrState()}" width="73px" />
                        </row>
                      </TablePanel>
                    </HorizontalPanel>
                    <TablePanel style="form">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'owner')" />:
                          </text>
                          <textbox field="String" key="{meta:getWellOwner()}" tab="{meta:getWellCollector()},itemsTestsTree" width="200px" />
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'collector')" />:
                          </text>
                          <textbox field="String" key="{meta:getWellCollector()}" tab="{meta:getWellWellNumber()},{meta:getWellOwner()}" width="200px" />
                        </row>
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'project')" />:
                          </text>
                          <widget>
                            <HorizontalPanel>
                              <autoComplete case="UPPER" field="Integer" key="{meta:getProjectName()}" popWidth="auto" tab="sampleItemTabPanel,{meta:getWellWellNumber()}" width="182px">
                                <col header="{resource:getString($constants,'name')}" width="115" />
                                <col header="{resource:getString($constants,'desc')}" width="190" />
                              </autoComplete>
                              <appButton key="projectLookup" style="LookupButton">
                                <AbsolutePanel style="LookupButtonImage" />
                              </appButton>
                            </HorizontalPanel>
                          </widget>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'wellNum')" />:
                          </text>
                          <textbox field="Integer" key="{meta:getWellWellNumber()}" tab="{meta:getProjectName()},{meta:getWellCollector()}" width="80px" />
                        </row>
                      </TablePanel>
                  </VerticalPanel>
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
