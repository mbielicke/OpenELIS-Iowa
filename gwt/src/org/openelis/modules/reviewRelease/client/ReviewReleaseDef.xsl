
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
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="ReviewRelase" name="{resource:getString($constants,'reviewAndRelease')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
<!--button panel code-->
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton" />
            <xsl:call-template name="previousButton" />
            <xsl:call-template name="nextButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="updateButton" />
            <xsl:call-template name="buttonPanelDivider" />
            <appButton key="complete" style="ButtonPanelButton" action="complete">
              <HorizontalPanel>
                <AbsolutePanel width="20" height="20" style="completeIcon" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"complete")' />
                </text>
              </HorizontalPanel>
            </appButton>
            <appButton key="release" style="ButtonPanelButton" action="release">
              <HorizontalPanel>
                <AbsolutePanel width="20" height="20" style="reviewAndReleaseIcon" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"release")' />
                </text>
              </HorizontalPanel>
            </appButton>
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
                  <menuItem key="historySamplePrivateWell" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySamplePrivateWell')}" />
                  <menuItem key="historySampleSDWIS" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'historySampleSDWIS')}" />
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
        <table key="atozTable" width="auto" maxRows="10" multiSelect="true" showScroll="ALWAYS">
          <col key="{meta:getAccessionNumber()}" width="115" header="Accession #">
            <textbox field="Integer" />
          </col>
          <col key="{meta:getAnalysisTestName()}" width="192" header="Test">
            <textbox field="String" />
          </col>
          <col key="{meta:getAnalysisMethodName()}" width="192" header="Method">
            <textbox field="String" />
          </col>
          <col key="{meta:getAnalysisStatusId()}" width="100" header="Analysis Status">
            <dropdown width="100" field="Integer" />
          </col>
          <col key="{meta:getStatusId()}" width="100" header="Sample Status">
            <dropdown width="100" field="Integer" />
          </col>
        </table>
        <TabPanel key="SampleContent" width="715" height="274">
<!-- Blank default deck -->
          <tab visible="false" text="">
            <AbsolutePanel />
          </tab>
<!-- Sample deck -->
          <tab tab="{meta:getAccessionNumber()},{meta:getClientReference()}" visible="false" text="Sample">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
                </text>
                <textbox key="{meta:getAccessionNumber()}" width="75" tab="orderNumber,SampleContent" field="Integer" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'orderNum')" />:
                </text>
                <textbox key="orderNumber" width="75" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collected')" />:
                </text>
                <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="80" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},orderNumber" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'time')" />:
                </text>
                <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'received')" />:
                </text>
                <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="110" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="110" popWidth="110px" tab="{meta:getClientReference()},{meta:getReceivedDate()}" field="Integer" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'clntRef')" />:
                </text>
                <widget colspan="3">
                  <textbox key="{meta:getClientReference()}" width="175" tab="SampleContent,{meta:getStatusId()}" field="String" />
                </widget>
              </row>
            </TablePanel>
          </tab>
<!-- Environment Deck -->
          <tab tab="{meta:getEnvIsHazardous()},{meta:getBillTo()}" visible="false" text="Environment">
            <VerticalPanel width="98%">
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
                  <textbox key="{meta:getEnvCollector()}" width="235" tab="{meta:getEnvCollectorPhone()},{meta:getEnvDescription()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'phone')" />:
                  </text>
                  <textbox key="{meta:getEnvCollectorPhone()}" width="120" tab="{meta:getEnvLocation()},{meta:getEnvCollector()}" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'location')" />:
                  </text>
                  <HorizontalPanel>
                    <textbox key="{meta:getEnvLocation()}" width="175" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                    <appButton key="locButton" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'desc')" />:
                  </text>
                  <textbox key="{meta:getEnvDescription()}" width="315" tab="{meta:getProjectName()},{meta:getEnvLocation()}" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="175" case="UPPER" popWidth="auto" tab="{meta:getOrgName()},{meta:getEnvDescription()}" field="Integer">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
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
                    <autoComplete key="{meta:getBillTo()}" width="175" case="UPPER" popWidth="auto" tab="{meta:getAccessionNumber()},{meta:getOrgName()}" field="Integer">
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
          </tab>
<!-- Private Well Deck -->
          <tab visible="false" text="Private Well">
                  <VerticalPanel width="98%">
                  <VerticalPanel>
                    <HorizontalPanel width="100%"> 
              			<TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <widget colspan="3">
                    <autoComplete key="{meta:getWellOrganizationName()}" width="180" case="UPPER" max="30" popWidth="auto" tab="{meta:getWellOrganizationId()},{meta:getClientReference()}" field="String">
                      <col width="180" header="Name" />
                      <col width="110" header="Street" />
                      <col width="100" header="City" />
                      <col width="20" header="St" />
                    </autoComplete>
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'orgId')" />:
                  </text>
                  <textbox key="{meta:getWellOrganizationId()}" width="60" tab="{meta:getWellReportToAddressMultipleUnit()},{meta:getWellOrganizationName()}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellReportToAddressMultipleUnit()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAddressStreetAddress()},{meta:getWellOrganizationId()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"address")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellReportToAddressStreetAddress()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAddressCity()},{meta:getWellReportToAddressMultipleUnit()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"city")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellReportToAddressCity()}" width="180" case="UPPER" max="30" tab="{meta:getWellReportToAddressState()},{meta:getWellReportToAddressStreetAddress()}" field="String" />
                  </widget>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'phone')" />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressWorkPhone()}" width="100" max="21" tab="{meta:getWellReportToAddressFaxPhone()},{meta:getWellReportToAddressZipCode()}" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"state")' />:
                  </text>
                  <dropdown key="{meta:getWellReportToAddressState()}" width="40" case="UPPER" tab="{meta:getWellReportToAddressZipCode()},{meta:getWellReportToAddressCity()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressZipCode()}" width="73" case="UPPER" max="10" tab="{meta:getWellReportToAddressWorkPhone()},{meta:getWellReportToAddressState()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'faxNumber')" />:
                  </text>
                  <textbox key="{meta:getWellReportToAddressFaxPhone()}" width="100" max="16" tab="{meta:getWellLocation()},{meta:getWellReportToAddressWorkPhone()}" field="String" />
                </row>
                <row></row>
                </TablePanel>
                <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'location')" />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocation()}" width="180" max="40" tab="{meta:getWellLocationAddrMultipleUnit()},{meta:getAddressFaxPhone()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrMultipleUnit()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrStreetAddress()},{meta:getWellLocation()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"address")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrStreetAddress()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrCity()},{meta:getWellLocationAddrMultipleUnit()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"city")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getWellLocationAddrCity()}" width="180" case="UPPER" max="30" tab="{meta:getWellLocationAddrState()},{meta:getWellLocationAddrStreetAddress()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"state")' />:
                  </text>
                  <dropdown key="{meta:getWellLocationAddrState()}" width="40" case="UPPER" tab="{meta:getWellLocationAddrZipCode()},{meta:getWellLocationAddrCity()}" field="String" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                  </text>
                  <textbox key="{meta:getWellLocationAddrZipCode()}" width="73" case="UPPER" max="10" tab="itemsTestsTree,{meta:getWellLocationAddrState()}" field="String" />
                </row>
                </TablePanel>
                </HorizontalPanel>
                <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'owner')" />:
                  </text>
                  <textbox key="{meta:getWellOwner()}" width="200" max="30" tab="{meta:getWellCollector()},itemsTestsTree" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'collector')" />:
                  </text>
                  <textbox key="{meta:getWellCollector()}" width="200" max="30" tab="{meta:getWellWellNumber()},{meta:getWellOwner()}" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'wellNum')" />:
                  </text>
                  <textbox key="{meta:getWellWellNumber()}" width="80" tab="{meta:getProjectName()},{meta:getWellCollector()}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="182" case="UPPER" popWidth="auto" tab="sampleItemTabPanel,{meta:getWellWellNumber()}" field="Integer">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
              </TablePanel>
             </VerticalPanel>
                  </VerticalPanel>
          </tab>
<!--     SDWISS Tab -->
                <tab text="SDWIS" visible="false">
                  <VerticalPanel width="98%">
                   	<TablePanel style="Form">
              		  <row>
                		<text style="Prompt">
                  			<xsl:value-of select="resource:getString($constants,'pwsId')" />:
                		</text>
                	    <HorizontalPanel>
                  		  <textbox key="{meta:getSDWISPwsId()}" width="75" max="9" case="UPPER" tab="pwsName,{meta:getClientReference()}" field="String" required="true"/>
                  			<appButton key="pwsButton" style="LookupButton">
                    		  <AbsolutePanel style="LookupButtonImage" />
                            </appButton>
                       </HorizontalPanel>
                       <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'pwsName')" />:
                       </text>
                       <textbox key="pwsName" width="250" tab="{meta:getSDWISStateLabId()},{meta:getSDWISPwsId()}" field="String" />
                     </row>
                     <row>
                        <text style="Prompt">
                           <xsl:value-of select="resource:getString($constants,'stateLabNo')" />:
                        </text>
                        <textbox key="{meta:getSDWISStateLabId()}" width="102" tab="{meta:getSDWISFacilityId()},pwsName" field="Integer" required="true"/>
                        <text style="Prompt">
                           <xsl:value-of select="resource:getString($constants,'facilityId')" />:
                        </text>
                        <textbox key="{meta:getSDWISFacilityId()}" width="75" max="12" tab="{meta:getSDWISSampleTypeId()},{meta:getSDWISStateLabId()}" field="String" />
                     </row>
                     <row>
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
                     <row>
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'collector')" />:
                       </text>
                       <textbox key="{meta:getSDWISCollector()}" width="162" max="20" tab="{meta:getOrgName()},itemsTestsTree" field="String" />
                       <text style="Prompt">
                         <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                       </text>
                       <HorizontalPanel>
                         <autoComplete key="{meta:getOrgName()}" width="179" case="UPPER" popWidth="auto" tab="{meta:getBillTo()},{meta:getSDWISCollector()}" field="Integer">
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
                        <autoComplete key="{meta:getBillTo()}" width="179" case="UPPER" popWidth="auto" tab="sampleItemTabPanel,{meta:getOrgName()}" field="Integer">
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
    </screen>
  </xsl:template>
</xsl:stylesheet>
