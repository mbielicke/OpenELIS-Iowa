
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
    <screen id="EnvironmentalSampleLogin" name="{resource:getString($constants,'newbornScreeningSampleLogin')}">
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
                  <xsl:call-template name="duplicateRecordMenuItem" />
                    <html>&lt;hr/&gt;</html>
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
              <textbox key="{meta:getAccessionNumber()}" width="75" tab="{meta:getOrderId()},sampleItemTabPanel" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'orderNum')" />:
              </text>
              <HorizontalPanel>
                <textbox key="{meta:getOrderId()}" width="75" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />              
                <appButton key="orderButton" style="LookupButton">
                  <AbsolutePanel style="LookupButtonImage" />
                </appButton>
              </HorizontalPanel>
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
                <textbox key="{meta:getClientReference()}" width="196" case="LOWER" max="20" tab="{meta:getEnvIsHazardous()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
          </TablePanel>
          <VerticalPanel>
           
            <HorizontalPanel padding = "0">
              <VerticalPanel style="subform">
                <text style="FormTitle"><xsl:value-of select="resource:getString($constants,'patient')" />:</text>  
                <TablePanel style="Form">
                  <row>                
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.last')" />:</text>
                    <widget colspan="3">
                      <textbox key="{meta:getNeoPatientLastName()}" width="222" case="LOWER" max="30" field="String" />
                    </widget>                    
                  </row>
                  <row>                
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.first')" />:</text>
                    <widget colspan="3">
                      <textbox key="{meta:getNeoPatientFirstName()}" width="222" max="20" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'gender')" />:</text>
                    <dropdown key="{meta:getNeoPatientGenderId()}" width="60" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />                  
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'race')" />:</text>
                    <dropdown key="{meta:getNeoPatientRaceId()}" width="65" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.ethnicity')" />:</text>
                    <dropdown key="{meta:getNeoPatientEthnicityId()}" width="80" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.birthDT')" />:</text>
                    <calendar begin="0" end="2" key="{meta:getNeoPatientBirthDate()}" width="90"/>    
                    <widget colspan="3">
                      <textbox field = "Date" key="{meta:getNeoPatientBirthTime()}" begin="3" end="5" width="60" mask="{resource:getString($constants,'timeMask')}" pattern="{resource:getString($constants,'timePattern')}"/>
                    </widget>                
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'aptSuite')" />:</text>
                    <widget colspan="5">
                      <textbox field = "String" key="{meta:getNeoPatientAddrMultipleUnit()}" width="222"/>      
                    </widget>          
                  </row>
                  <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'address')" />:</text>
                     <widget colspan="5">
                         <textbox field = "String" key="{meta:getNeoPatientAddrStreetAddress()}" width="222"/>
                     </widget>
                 </row>
                 <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'city')" />:</text>         
                     <widget colspan="3">            
                       <textbox key="{meta:getNeoPatientAddrCity()}" field = "String" width="100"/>
                      </widget>
                 </row>
                 <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'state')" />:</text>                     
                     <dropdown key="{meta:getNeoPatientAddrState()}" field = "String" width="40"/>
                 </row>
                 <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'zipcode')" />:</text>                     
                     <textbox key="{meta:getNeoPatientAddrZipCode()}" field = "String" width="70"/>
                 </row>
                </TablePanel>
              </VerticalPanel>
              <VerticalPanel style="subform">
                <text style="FormTitle"><xsl:value-of select="resource:getString($constants,'sample.nextOfKin')" /></text>
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.last')" />:</text>
                    <widget colspan="5">
                      <textbox key="{meta:getNeoNextOfKinLastName()}" width="222" case="LOWER" max="30" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.maiden')" />:</text>
                    <widget colspan="5">
                      <textbox key="{meta:getNeoNextOfKinMiddleName()}" width="222" case="LOWER" max="30" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.first')" />:</text>
                    <widget colspan="5">
                      <textbox key="{meta:getNeoNextOfKinFirstName()}" width="222" case="LOWER" max="30" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.relation')" />:</text>
                    <widget colspan="5">
                     <dropdown key="{meta:getNeoNextOfKinRelationId()}" width="80" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                    </widget>  
                  </row>
                  <row>               
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'gender')" />:</text>
                    <dropdown key="{meta:getNeoNextOfKinGenderId()}" width="60" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />                  
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'race')" />:</text>
                    <widget colspan="2">
                      <dropdown key="{meta:getNeoNextOfKinRaceId()}" width="65" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                    </widget>                    
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.ethnicity')" />:</text>
                    <dropdown key="{meta:getNeoNextOfKinEthnicityId()}" width="65" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" field="String" />
                    <text style="Prompt">Birth:</text>
                    <widget colspan="5">
                      <calendar begin="0" end="2" key="{meta:getNeoNextOfKinBirthDate()}" width="90"/>    
                    </widget>                                           
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'phone')" />:</text>
                    <widget colspan="3">
                      <textbox field = "Date" key="{meta:getNeoNextOfKinAddrHomePhone()}" begin="3" end="5" width="120" mask="{resource:getString($constants,'timeMask')}" pattern="{resource:getString($constants,'timePattern')}"/>
                    </widget>   
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'aptSuite')" />:</text>
                    <widget colspan="5">
                      <textbox field = "String" key="{meta:getNeoNextOfKinAddrMultipleUnit()}" width="222"/>      
                    </widget>          
                  </row>
                  <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'address')" />:</text>
                     <widget colspan="5">
                         <textbox field = "String" key="{meta:getNeoNextOfKinAddrtreetAddress()}" width="222"/>
                     </widget>
                 </row>
                 <row>
                     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.cityStateZip')" />:</text>  
                     <widget colspan="2">
                       <textbox key="{meta:getNeoNextOfKinAddrCity()}" field = "String" width="100"/>
                     </widget>
                     <dropdown key="{meta:getNeoNextOfKinAddrState()}" field = "String" width="40"/>
                     <textbox key="{meta:getNeoNextOfKinAddrZipCode()}" field = "String" width="70"/>
                 </row>
                </TablePanel>
              </VerticalPanel>        
              <VerticalPanel style="subform">
                <text style="FormTitle"><xsl:value-of select="resource:getString($constants,'other')" /></text>
                <TablePanel style="Form">
                    <row>  
                       <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.nicu')" />:</text>
                       <check key="{meta:getNeoIsNicu()}"/>
                    </row> 
                    <row>   
                       <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.birthOrder')" />:</text>
                       <widget colspan="4">
                         <dropdown field = "Integer" key="{meta:getNeoBirthOrderId()}" width="35"/>
                       </widget>
                    </row> 
                    <row>                       
                        <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.gestAge')" />:</text>
                        <textbox field = "Integer" key="{meta:getNeoGestationalAge()}" width="25"/>                                        
                    </row>
                    <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.feeding')" />:</text>
                      <widget colspan="3">
                        <dropdown field = "Integer" key="{meta:getNeoFeedingId()}" width="75"/>
                      </widget>                  
                    </row> 
                    <row>
                       <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.weight')" />:</text>
                       <widget colspan="5">
                         <textbox field = "Integer" key="{meta:getNeoWeight()}" width="50"/>
                       </widget>  
                    </row>
                    <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.transfused')" />:</text>
                      <check key="{meta:getNeoIsTransfused()}"/>
                    </row>
                    <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.transDate')" />:</text>
                      <widget colspan="3">
                         <calendar begin="0" end="2" key="{meta:getNeoTransfusionDate()}" width="90"/>  
                      </widget>
                    </row>
                    <row>                                         
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.transAge')" />:</text>
                      <widget colspan="5">
                        <textbox field = "Integer" key="{meta:getNeoTransfusionAge()}" width="50"/>
                      </widget> 
                    </row>         
                    <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'repeat')" />:</text>
                      <check key="{meta:getNeoIsRepeat()}"/>
                    </row>       
                    <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.collectAge')" />:</text>
                      <widget colspan="5">
                        <textbox field = "Integer" key="{meta:getNeoCollectionAge()}" width="50"/>
                      </widget>
                    </row>
                    <row>  
                        <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sample.collectValid')" />:</text>
                        <check key="{meta:getNeoIsCollectionValid()}"/>                        
                    </row>                      
                </TablePanel>
                <VerticalPanel height = "3"/>
              </VerticalPanel>    
            </HorizontalPanel>
          </VerticalPanel>  
          <HorizontalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')" />
              </text>
              <TablePanel padding="0" spacing="0">
                <row>
                  <tree key="itemsTestsTree" width="auto" maxRows="4" showScroll="ALWAYS" tab="sampleItemTabPanel,{meta:getBillTo()}">
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
                <xsl:value-of select="resource:getString($constants,'collectorOrgInfo')" />
              </text>
              <TablePanel style="Form">
                <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'sample.providerLF')" />:
                    </text>
                    <textbox key="{meta:getNeoProviderLastName()}" width="150" case="LOWER" max="30" field="String" />
                    <textbox key="{meta:getNeoProviderFirstName()}" width="130" max="20" field="String" />
                  </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <widget colspan="3">
                    <HorizontalPanel>
                      <autoComplete key="{meta:getProjectName()}" width="265" case="LOWER" popWidth="auto" tab="{meta:getOrgName()},{meta:getEnvDescription()}" field="Integer">
                        <col width="150" header="{resource:getString($constants,'name')}" />
                        <col width="275" header="{resource:getString($constants,'description')}" />
                      </autoComplete>
                      <appButton key="projectLookup" style="LookupButton">
                        <AbsolutePanel style="LookupButtonImage" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <widget colspan="3">
                    <HorizontalPanel>
                      <autoComplete key="{meta:getOrgName()}" width="265" case="UPPER" popWidth="auto" tab="{meta:getBillTo()},{meta:getProjectName()}" field="Integer">
                        <col width="200" header="{resource:getString($constants,'name')}" />
                        <col width="130" header="{resource:getString($constants,'street')}" />
                        <col width="120" header="{resource:getString($constants,'city')}" />
                        <col width="20" header="{resource:getString($constants,'st')}" />
                      </autoComplete>
                      <appButton key="reportToLookup" style="LookupButton">
                        <AbsolutePanel style="LookupButtonImage" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'sample.birthHospital')" />:
                  </text>
                  <widget colspan="3">
                    <HorizontalPanel>
                      <autoComplete key="{meta:getBillTo()}" width="265" case="UPPER" popWidth="auto" tab="itemsTestsTree,{meta:getOrgName()}" field="Integer">
                        <col width="200" header="{resource:getString($constants,'name')}" />
                        <col width="130" header="{resource:getString($constants,'street')}" />
                        <col width="120" header="{resource:getString($constants,'city')}" />
                        <col width="20" header="{resource:getString($constants,'st')}" />
                      </autoComplete>
                      <appButton key="billToLookup" style="LookupButton">
                        <AbsolutePanel style="LookupButtonImage" />
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </row>
                <row>
                      <text style="Prompt"><xsl:value-of select="resource:getString($constants,'barcode')" />:</text>
                      <widget colspan="3">
                        <textbox field = "Integer" key="{meta:getNeoBarcodeNumber()}" width="150"/>
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