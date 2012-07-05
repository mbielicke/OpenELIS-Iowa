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
  xmlns:meta="xalan://org.openelis.meta.OrderMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AuxDataTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="SendoutOrder" name="{resource:getString($constants,'sendoutOrder')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <VerticalPanel height="100%" padding="0" spacing="0" style="AtoZ">
                <xsl:call-template name="aToZButton">
                  <xsl:with-param name="keyParam">#</xsl:with-param>
                  <xsl:with-param name="queryParam">&gt;0</xsl:with-param>
                </xsl:call-template>
              </VerticalPanel>
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" maxRows="23" style="atozTable" width="auto">
                <col header="{resource:getString($constants,'orderNum')}" width="75">
                  <label field="Integer" />
                </col>
                <col header="{resource:getString($constants,'requestedBy')}" width="150">
                  <label field="String" />
                </col>
              </table>
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
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">
<!--button panel code-->
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="previousButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="nextButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="addButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="updateButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="abortButton">
                <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
              </xsl:call-template>
              <xsl:call-template name="buttonPanelDivider" />
              <menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
                <menuItem>
                  <menuDisplay>
                    <appButton action="option" style="ButtonPanelButton">
                      <HorizontalPanel>
                        <text>
                          <xsl:value-of select='resource:getString($constants,"options")' />
                        </text>
                        <AbsolutePanel height="20" style="OptionsButtonImage" width="20" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <html>&lt;hr/&gt;</html>
                    <menuItem description="" enable="false" icon="shippingIcon" key="shippingInfo" label="{resource:getString($constants,'shippingInfo')}" />
                    <menuItem description="" enable="false" icon="" key="orderRequestForm" label="{resource:getString($constants,'orderRequestForm')}" />
                    <html>&lt;hr/&gt;</html>
                    <menuItem description="" enable="false" icon="historyIcon" key="orderHistory" label="{resource:getString($constants,'orderHistory')}" />
                    <menuItem description="" enable="false" icon="historyIcon" key="organizationHistory" label="{resource:getString($constants,'orderOrganizationHistory')}" />
                    <menuItem description="" enable="false" icon="historyIcon" key="itemHistory" label="{resource:getString($constants,'orderItemHistory')}" />
                    <menuItem description="" enable="false" icon="historyIcon" key="testHistory" label="{resource:getString($constants,'orderTestHistory')}" />
                    <menuItem description="" enable="false" icon="historyIcon" key="containerHistory" label="{resource:getString($constants,'orderContainerHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderNum")' />:
                </text>
                <textbox field="Integer" key="{meta:getId()}" tab="{meta:getNeededInDays()},{meta:getCostCenterId()}" width="90" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"neededDays")' />:
                </text>
                <widget colspan="2">
                  <textbox field="Integer" key="{meta:getNeededInDays()}" tab="{meta:getNumberOfForms()},{meta:getId()}" width="35" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"numForms")' />:
                </text>
                <widget colspan="2">
                  <textbox field="Integer" key="{meta:getNumberOfForms()}" tab="{meta:getShipFromId()},{meta:getNeededInDays()}" width="35" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"shipFrom")' />:
                </text>
                <dropdown case="MIXED" field="Integer" key="{meta:getShipFromId()}" required = "true" tab="{meta:getOrganizationName()},{meta:getNumberOfForms()}" width="203" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"shipTo")' />:
                </text>
                <widget colspan="5">
                  <autoComplete case="UPPER" field="Integer" key="{meta:getOrganizationName()}" required="true" tab="{meta:getStatusId()},{meta:getShipFromId()}" width="188">
                    <col header="{resource:getString($constants,'name')}" width="180" />
                    <col header="{resource:getString($constants,'street')}" width="110" />
                    <col header="{resource:getString($constants,'city')}" width="100" />
                    <col header="{resource:getString($constants,'st')}" width="20" />
                  </autoComplete>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"status")' />:
                </text>
                <dropdown case="MIXED" field="Integer" key="{meta:getStatusId()}" popWidth="auto" tab="{meta:getOrganizationAttention()},{meta:getOrganizationName()}" width="90" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"attention")' />:
                </text>
                <widget colspan="5">
                  <textbox case="UPPER" field="String" key="{meta:getOrganizationAttention()}" max="30" tab="{meta:getOrderedDate()},{meta:getStatusId()}" width="188" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderDate")' />:
                </text>
                <calendar begin="0" end="2" key="{meta:getOrderedDate()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getOrganizationAddressMultipleUnit()},{meta:getOrganizationAttention()}" width="90" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                </text>
                <widget colspan="5">
                  <textbox case="UPPER" field="String" key="{meta:getOrganizationAddressMultipleUnit()}" tab="{meta:getRequestedBy()},{meta:getOrderedDate()}" max="30" width="188" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"requestedBy")' />:
                </text>
                <textbox case="LOWER" field="String" key="{meta:getRequestedBy()}" tab="{meta:getOrganizationAddressStreetAddress()},{meta:getOrganizationAddressMultipleUnit()}" width="203" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"address")' />:
                </text>
                <widget colspan="5">
                  <textbox case="UPPER" field="String" key="{meta:getOrganizationAddressStreetAddress()}" tab="{meta:getCostCenterId()},{meta:getRequestedBy()}" max="30" width="188" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                </text>
                <dropdown field="Integer" key="{meta:getCostCenterId()}" popWidth="auto" required = "true" tab="{meta:getOrganizationAddressCity()},{meta:getOrganizationAddressStreetAddress()}" width="203"/>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"city")' />:
                </text>
                <widget colspan="5">
                  <textbox case="UPPER" field="String" key="{meta:getOrganizationAddressCity()}" tab="{meta:getDescription()},{meta:getCostCenterId()}" max="30" width="188" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget>
                  <autoComplete case="LOWER" field="String" key="{meta:getDescription()}" tab="{meta:getOrganizationAddressState()},{meta:getOrganizationAddressCity()}" width="203">
                    <col header="{resource:getString($constants,'description')}" width="310" />
                  </autoComplete>
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"state")' />:
                </text>
                <widget>
                  <dropdown case="UPPER" field="String" key="{meta:getOrganizationAddressState()}" tab = "{meta:getOrganizationAddressZipCode()},{meta:getDescription()}" width="35" />
                </widget>
                <widget>
                  <HorizontalPanel width="10" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                </text>
                <widget>
                  <textbox field="String" key="{meta:getOrganizationAddressZipCode()}" tab = "tabPanel,{meta:getOrganizationAddressState()}" max="30" width="72" />
                </widget>
              </row>
            </TablePanel>
<!-- TAB PANEL -->
            <TabPanel height="297" key="tabPanel" width="715">
              <tab key="organizationTab" tab="organizationTable,organizationTable" text="{resource:getString($constants,'organization')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="organizationTable" maxRows="11" showScroll="ALWAYS" tab="organizationTable,organizationTable" title="" width="697">
                    <col key="{meta:getOrderOrganizationTypeId()}" header="{resource:getString($constants,'type')}" width="125">
                      <dropdown field="Integer" required="true" width="125" />
                    </col>
                    <col key="{meta:getOrderOrganizationAttention()}" header="{resource:getString($constants,'attention')}" width="180">
                      <textbox case="UPPER" max = "30" field="String" />
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationName()}" header="{resource:getString($constants,'name')}" width="220">
                      <autoComplete case="UPPER" field="Integer" required="true" width="130">
                        <col header="{resource:getString($constants,'name')}" width="200" />
                        <col header="{resource:getString($constants,'street')}" width="130" />
                        <col header="{resource:getString($constants,'city')}" width="120" />
                        <col header="{resource:getString($constants,'st')}" width="20" />
                      </autoComplete>
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationAddressMultipleUnit()}" width="130" header="{resource:getString($constants,'aptSuite')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationAddressStreetAddress()}" width="130" header="{resource:getString($constants,'address')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationAddressCity()}" header="{resource:getString($constants,'city')}" width="110">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationAddressState()}" header="{resource:getString($constants,'state')}" width="56">
                      <dropdown case="UPPER" width="40" field="String" />
                    </col>
                    <col key="{meta:getOrganizationOrganizationAddressZipCode()}" width="70" header="{resource:getString($constants,'zipcode')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getOrderOrganizationOrganizationAddressCountry()}" width="126" header="{resource:getString($constants,'country')}">
                      <dropdown width="110" field="String" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="organizationAddButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="organizationRemoveButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
              <tab key="tab7" text="{resource:getString($constants,'auxData')}">
                <xsl:call-template name="AuxDataTab">
                  <xsl:with-param name="maxRows">9</xsl:with-param>
                  <xsl:with-param name="col2Width">301</xsl:with-param>
                  <xsl:with-param name="col3Width">302</xsl:with-param>
                  <xsl:with-param name="showTwoInfoRows">false</xsl:with-param>
                </xsl:call-template>
              </tab>
              <tab key="testTab" tab="orderTestTree,orderTestTree" text="{resource:getString($constants,'test')}">
                <VerticalPanel padding="0" spacing="0">
                  <widget valign="top">
                    <tree key="orderTestTree" maxRows="11" showScroll="ALWAYS" width="auto">
                      <header>
                        <col header="{resource:getString($constants,'itemNum')}" width="55" />
                        <col header="{resource:getString($constants,'testMethodDescription')}" width="636" />
                      </header>
                      <leaf key="test">
                        <col align="left" key ="{meta:getTestItemSequence()}">
                          <textbox field="Integer" required="true"/>
                        </col>
                        <col align="left" key="{meta:getTestName()}">
                          <autoComplete case="LOWER" field="Integer" popWidth="560" required="true" width="160">
                            <col header="{resource:getString($constants,'testMethodDescription')}" width="555" />
                          </autoComplete>
                        </col>                        
                      </leaf>
                      <leaf key="analyte">
                        <col>
                          <check />
                        </col>
                        <col align="left" key="analyte">
                          <label field="String" />
                        </col>
                      </leaf>
                    </tree>
                  </widget>
                  <HorizontalPanel>
                    <widget style="TableButtonFooter">
                      <HorizontalPanel>
                        <appButton key="addTestButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="AddRowButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'addRow')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                        <appButton key="removeTestButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="RemoveRowButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'removeRow')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                        <appButton key="testPopoutButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="popoutButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'popout')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                      </HorizontalPanel>
                    </widget>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>
              <tab key="containerTab" tab="orderContainerTable, orderContainerTable" text="{resource:getString($constants,'container')}">
                <VerticalPanel padding="0" spacing="0">
                  <widget valign="top">
                    <table key="orderContainerTable" maxRows="11" showScroll="ALWAYS" title="" width="auto">
                      <col align="left" header="{resource:getString($constants,'itemNum')}" key="{meta:getContainerItemSequence()}" width="45">
                          <textbox field="Integer" />
                        </col>
                      <col align="left" header="{resource:getString($constants,'container')}" key="{meta:getContainerContainerId()}" width="412">
                        <dropdown field="Integer" popWidth="340" required="true" width="450" />
                      </col>
                      <col align="left" header="{resource:getString($constants,'sampleType')}" key="{meta:getContainerTypeOfSampleId()}" width="231">
                        <dropdown field="Integer" popWidth="250" width="241" />
                      </col>
                    </table>
                  </widget>
                  <HorizontalPanel>
                    <HorizontalPanel>
                      <appButton key="addContainerButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeContainerButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'removeRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="duplicateContainerButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="DuplicateRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'duplicateRecord')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="containerPopoutButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="popoutButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'popout')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                    </HorizontalPanel>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>
              <tab key="itemTab" tab="itemTable, itemTable" text="{resource:getString($constants,'items')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="itemTable" maxRows="11" showScroll="ALWAYS" width="auto">
                    <col align="right" header="{resource:getString($constants,'quantity')}" key="{meta:getOrderItemQuantity()}" width="65">
                      <textbox field="Integer" required="true" />
                    </col>
                    <col header="{resource:getString($constants,'inventoryItem')}" key="{meta:getOrderItemInventoryItemName()}" width="385">
                      <autoComplete case="LOWER" field="Integer" required="true" width="275">
                        <col header="{resource:getString($constants,'name')}" width="155" />
                        <col header="{resource:getString($constants,'description')}" width="200" />
                        <col header="{resource:getString($constants,'store')}" width="110">
                          <dropdown field="Integer" popWidth="110" width="110" />
                        </col>
                        <col header="{resource:getString($constants,'dispensedUnits')}" width="110">
                          <dropdown field="Integer" popWidth="110" width="110" />
                        </col>
                      </autoComplete>
                    </col>
                    <col header="{resource:getString($constants,'store')}" key="{meta:getOrderItemInventoryItemStoreId()}" width="238">
                      <dropdown field="Integer" width="235" />
                    </col>
                  </table>
                  <HorizontalPanel>
                    <appButton key="addItemButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="AddRowButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'addRow')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                    <appButton key="removeItemButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="RemoveRowButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'removeRow')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>
              <tab key="noteTab" tab="notesPanel, notesPanel" text="{resource:getString($constants,'shipping')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes height="257" key="notesPanel" width="714" />
                  <appButton key="standardNoteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="StandardNoteButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'editNote')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </VerticalPanel>
              </tab>
              <tab key="customerNote" tab="customerNotesPanel, customerNotesPanel" text="{resource:getString($constants,'customer')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes height="257" key="customerNotesPanel" width="714" />
                  <appButton key="editNoteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="StandardNoteButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'editNote')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </VerticalPanel>
              </tab>
              <tab key="internalNote" tab="internalNotesPanel, internalNotesPanel" text="{resource:getString($constants,'internal')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes height="257" key="internalNotesPanel" width="714" />
                  <appButton key="addNoteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="StandardNoteButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addNote')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </VerticalPanel>
              </tab>
              <tab key="sampleNote" tab="sampleNotesPanel, sampleNotesPanel," text="{resource:getString($constants,'sample')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes height="257" key="sampleNotesPanel" width="714" />
                  <appButton key="sampleEditNoteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="StandardNoteButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'editNote')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </VerticalPanel>
              </tab>
              <tab key="recurringTab" text="{resource:getString($constants,'recur')}">
                <HorizontalPanel>
                  <VerticalPanel>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'active')" />:
                        </text>
                        <check key="{meta:getRecurrenceIsActive()}" tab="{meta:getRecurrenceActiveBegin()},{meta:getParentOrderId()}" />
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'beginDate')" />:
                        </text>
                        <widget colspan="3">
                          <calendar begin="0" end="2" key="{meta:getRecurrenceActiveBegin()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getRecurrenceActiveEnd()},{meta:getRecurrenceIsActive()}" width="90" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'endDate')" />:
                        </text>
                        <widget colspan="3">
                          <calendar begin="0" end="2" key="{meta:getRecurrenceActiveEnd()}" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getRecurrenceFrequency()},{meta:getRecurrenceActiveBegin()}" width="90" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'frequency')" />:
                        </text>
                        <textbox field="Integer" key="{meta:getRecurrenceFrequency()}" tab="{meta:getRecurrenceUnitId()},{meta:getRecurrenceActiveEnd()}" width="25" />
                        <text style="Prompt">
                          <xsl:value-of select="resource:getString($constants,'unit')" />:
                        </text>
                        <dropdown field="Integer" key="{meta:getRecurrenceUnitId()}" popWidth="auto" tab="{meta:getParentOrderId()},{meta:getRecurrenceFrequency()}" width="50" />
                      </row>
                    </TablePanel>
                    <VerticalPanel width="472">
                      <html>&lt;hr/&gt;</html>
                      <VerticalPanel height="5" />
                      <TablePanel style="Form">
                        <row>
                          <text style="Prompt">
                            <xsl:value-of select="resource:getString($constants,'parentOrderNum')" />:
                          </text>
                          <textbox field="Integer" key="{meta:getParentOrderId()}" tab="{meta:getRecurrenceIsActive()},{meta:getRecurrenceUnitId()}" width="90" />
                        </row>
                      </TablePanel>
                    </VerticalPanel>
                  </VerticalPanel>
                  <VerticalPanel>
                    <widget valign="top">
                      <table key="dateTable" maxRows="11" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
                        <col align="left" header="{resource:getString($constants,'orderRecurOn')}" key="date" width="220">
                          <label begin="0" end="2" field="Date" pattern="{resource:getString($constants,'dayInYearPattern')}" />
                        </col>
                      </table>
                    </widget>
                    <widget style="TableButtonFooter">
                      <HorizontalPanel>
                        <appButton key="showDateButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'showDates')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                      </HorizontalPanel>
                    </widget>
                  </VerticalPanel>
                </HorizontalPanel>
              </tab>
              <tab key="fillTab" tab="fillTable, fillTable" text="{resource:getString($constants,'filled')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="fillTable" maxRows="11" showScroll="ALWAYS" width="auto">
                    <col header="{resource:getString($constants,'inventoryItem')}" key="{meta:getInventoryReceiptOrderItemId()}" width="205">
                      <label field="String" />
                    </col>
                    <col header="{resource:getString($constants,'location')}" key="location" width="235">
                      <label field="String" />
                    </col>
                    <col align="right" header="{resource:getString($constants,'quantity')}" key="{meta:getInventoryReceiptQuantityReceived()}" width="65">
                      <label field="String" />
                    </col>
                    <col header="{resource:getString($constants,'lotNum')}" key="" width="85">
                      <label field="String" />
                    </col>
                    <col header="{resource:getString($constants,'expDate')}" key="" width="92">
                      <label field="String" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>