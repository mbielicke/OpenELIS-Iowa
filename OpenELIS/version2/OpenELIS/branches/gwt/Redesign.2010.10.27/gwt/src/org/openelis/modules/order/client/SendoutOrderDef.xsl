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
  xmlns:meta="xalan://org.openelis.meta.OrderMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AuxDataTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen name="{resource:getString($constants,'kitOrder')}">
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
              <table key="atozTable" rows="20">
                <col width="75" header="{resource:getString($constants,'orderNum')}">
                  <label field="Integer" />
                </col>
                <col width="150" header="{resource:getString($constants,'requestedBy')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="PreviousButtonImage" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="NextButtonImage" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
        <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton"/>
              <xsl:call-template name="previousButton"/>
              <xsl:call-template name="nextButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="addButton"/>
              <xsl:call-template name="updateButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
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
                  <xsl:call-template name="duplicateRecordMenuItem" />
                  <menuItem key="orderHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orderHistory')}" />
                  <menuItem key="itemHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orderItemHistory')}" />
                  <menuItem key="testHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orderTestHistory')}" />
                  <menuItem key="containerHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orderContainerHistory')}" />
              </menu>
            </HorizontalPanel>
          </AbsolutePanel>

<!--end button panel-->

          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderNum")' />:
                </text>
                <textbox key="{meta:getId()}" width="90" tab="{meta:getNeededInDays()},{meta:getCostCenterId()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"neededDays")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getNeededInDays()}" width="75" tab="{meta:getShipFromId()},{meta:getId()}" field="Integer" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"shipFrom")' />:
                </text>
                <dropdown key="{meta:getShipFromId()}" width="203" case="MIXED" tab="{meta:getOrganizationName()},{meta:getNeededInDays()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"shipTo")' />:
                </text>
                <widget colspan="5">
                  <autoComplete key="{meta:getOrganizationName()}" width="188" case="UPPER" tab="{meta:getStatusId()},{meta:getShipFromId()}" required = "true">
                    <col width="180" header="{resource:getString($constants,'name')}" />
                    <col width="110" header="{resource:getString($constants,'street')}" />
                    <col width="100" header="{resource:getString($constants,'city')}" />
                    <col width="20" header="{resource:getString($constants,'st')}" />
                  </autoComplete>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"status")' />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="90" case="MIXED" tab="{meta:getOrganizationAttention()},{meta:getOrganizationName()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"attention")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAttention()}" tab="{meta:getOrderedDate()},{meta:getStatusId()}" width="188" max="30" case = "UPPER" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderDate")' />:
                </text>
                <calendar key="{meta:getOrderedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getRequestedBy()},{meta:getOrganizationAttention()}" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressMultipleUnit()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"requestedBy")' />:
                </text>
                <textbox key="{meta:getRequestedBy()}" width="203" tab="{meta:getCostCenterId()},{meta:getOrderedDate()}" case = "LOWER" field="String" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"address")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressStreetAddress()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                </text>
                <dropdown key="{meta:getCostCenterId()}" width="203" case="MIXED" tab="{meta:getDescription()},{meta:getRequestedBy()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"city")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressCity()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget>
                  <autoComplete key="{meta:getDescription()}" width="203" case="LOWER" tab="tabPanel,{meta:getCostCenterId()}" field="String">
                    <col width="310" header="{resource:getString($constants,'description')}" />
                  </autoComplete>
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"state")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressState()}" width="35" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <HorizontalPanel width="10" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressZipCode()}" width="72" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
            </TablePanel>

<!-- TAB PANEL -->

            <TabPanel key="tabPanel" width="605" height="315">

<!-- TAB 1 (items) -->

              <tab key="itemTab" tab="itemTable, itemTable" text="{resource:getString($constants,'items')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="itemTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getOrderItemQuantity()}" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <textbox field="Integer" required="true" />
                    </col>
                    <col key="{meta:getOrderItemInventoryItemName()}" width="275" header="{resource:getString($constants,'inventoryItem')}">
                      <autoComplete case="LOWER" required="true">
                        <col width="155" header="{resource:getString($constants,'name')}" />
                        <col width="110" header="{resource:getString($constants,'store')}">
                          <dropdown width="110" field="Integer" />
                        </col>
                        <col width="110" header="{resource:getString($constants,'dispensedUnits')}">
                          <dropdown width="110" field="Integer" />
                        </col>
                      </autoComplete>
                    </col>
                    <col key="{meta:getOrderItemInventoryItemStoreId()}" width="245" header="{resource:getString($constants,'store')}">
                      <dropdown width="235" field="Integer" />
                    </col>
                  </table>
                  <HorizontalPanel>
                    <button key="addItemButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                    <button key="removeItemButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>

<!-- TAB 2 (receipts) -->

              <tab key="fillTab" tab="fillTable, fillTable" text="{resource:getString($constants,'filled')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="fillTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getInventoryReceiptOrderItemId()}" width="150" header="{resource:getString($constants,'inventoryItem')}">
                      <label field="String" />
                    </col>
                    <col key="location" width="180" header="{resource:getString($constants,'location')}">
                      <label field="String" />
                    </col>
                    <col key="{meta:getInventoryReceiptQuantityReceived()}" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <label field="Integer" />
                    </col>
                    <col key="" width="85" header="{resource:getString($constants,'lotNum')}">
                      <label field="String" />
                    </col>
                    <col key="" width="105" header="{resource:getString($constants,'expDate')}">
                      <label field="Date" begin="0" end="2" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>

<!-- TAB 4 (order notes) -->

              <tab key="noteTab" tab="notesPanel, notesPanel" text="{resource:getString($constants,'orderShippingNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="604" height="257" />
                  <button key="standardNoteButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'addNote')}" style="Button"/>
                </VerticalPanel>
              </tab>

<!-- TAB 3 (customer notes)-->

              <tab key="customerNote" tab="customerNotesPanel, customerNotesPanel" text="{resource:getString($constants,'customerNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="customerNotesPanel" width="604" height="257" />
                  <button key="editNoteButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'editNote')}" style="Button"/>
                </VerticalPanel>
              </tab>

<!-- TAB 5 (report to/bill to)-->

              <tab key="reportToBillToTab" tab="{meta:getReportToName()}, {meta:getBillToName()}" text="{resource:getString($constants,'reportToBillTo')}">
                <HorizontalPanel height="247" padding="0" spacing="0">
                  <VerticalPanel>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"reportTo")' />:
                        </text>
                        <widget colspan="5">
                          <autoComplete key="{meta:getReportToName()}" width="188" case="UPPER" tab="{meta:getReportToAttention()},{meta:getBillToAttention()}">
                            <col width="180" header="{resource:getString($constants,'name')}" />
                            <col width="110" header="{resource:getString($constants,'street')}" />
                            <col width="100" header="{resource:getString($constants,'city')}" />
                            <col width="20" header="{resource:getString($constants,'st')}" />
                          </autoComplete>
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"attention")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getReportToAttention()}" tab="{meta:getBillToName()},{meta:getReportToName()}" case="UPPER" width="188" max = "30" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getReportToAddressMultipleUnit()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"address")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getReportToAddressStreetAddress()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"city")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getReportToAddressCity()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"state")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getReportToAddressState()}" width="35" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                        <widget>
                          <HorizontalPanel width="10" />
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getReportToAddressZipCode()}" width="72" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                  <VerticalPanel>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"billTo")' />:
                        </text>
                        <widget colspan="5">
                          <autoComplete key="{meta:getBillToName()}" width="188" case="UPPER" tab="{meta:getBillToAttention()},{meta:getReportToAttention()}">
                            <col width="180" header="{resource:getString($constants,'name')}" />
                            <col width="110" header="{resource:getString($constants,'street')}" />
                            <col width="100" header="{resource:getString($constants,'city')}" />
                            <col width="20" header="{resource:getString($constants,'st')}" />
                          </autoComplete>
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"attention")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getBillToAttention()}" tab="{meta:getReportToName()},{meta:getBillToName()}" case="UPPER" width="188" max = "30" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getBillToAddressMultipleUnit()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"address")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getBillToAddressStreetAddress()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"city")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getBillToAddressCity()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"state")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getBillToAddressState()}" width="35" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                        <widget>
                          <HorizontalPanel width="10" />
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getBillToAddressZipCode()}" width="72" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                </HorizontalPanel>
              </tab>
              <tab key="containerTab" tab="orderTestTable, orderContainerTable" text="{resource:getString($constants,'container')}">
                <VerticalPanel>
                  <VerticalPanel padding="0" spacing="0">
                    <widget valign="top">
                      <table key="orderTestTable" rows="4" vscroll="ALWAYS" hscroll="ALWAYS" tab="orderContainerTable, orderContainerTable" >
                        <col key="{meta:getTestName()}" width="160" align="left" header="{resource:getString($constants,'test')}">
                          <autoComplete width="160" case="LOWER" required="true">
                            <col width="150" header="{resource:getString($constants,'test')}" />
                            <col width="150" header="{resource:getString($constants,'method')}" />
                            <col width="250" header="{resource:getString($constants,'description')}" />
                          </autoComplete>
                        </col>
                        <col key="method" width="160" align="left" header="{resource:getString($constants,'method')}">
                          <label field="String" />
                        </col>
                        <col key="description" width="258" align="left" header="{resource:getString($constants,'description')}">
                          <label field="String" />
                        </col>
                      </table>
                    </widget>
                    <HorizontalPanel>
                      <widget style="TableButtonFooter">
                        <HorizontalPanel>
                          <button key="addTestButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                          <button key="removeTestButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                        </HorizontalPanel>
                      </widget>
                    </HorizontalPanel>
                  </VerticalPanel>
                  <HorizontalPanel width="10" />
                  <VerticalPanel padding="0" spacing="0">
                    <widget valign="top">
                      <table key="orderContainerTable" rows="4" vscroll="ALWAYS" hscroll="ALWAYS" tab="orderTestTable, orderTestTable">
                        <col key="{meta:getContainerContainerId()}" width="300" align="left" header="{resource:getString($constants,'container')}">
                          <dropdown width="300" field="Integer" required="true" />
                        </col>
                        <col key="{meta:getContainerNumberOfContainers()}" width="78" align="left" header="{resource:getString($constants,'quantity')}">
                          <textbox field="Integer" required="true" />
                        </col>
                        <col key="{meta:getContainerTypeOfSampleId()}" width="200" align="left" header="{resource:getString($constants,'sampleType')}">
                          <dropdown width="200" field="Integer" />
                        </col>
                      </table>
                    </widget>
                    <HorizontalPanel>
                      <HorizontalPanel>
                        <button key="addContainerButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                        <button key="removeContainerButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                      </HorizontalPanel>
                    </HorizontalPanel>
                  </VerticalPanel>
                </VerticalPanel>
              </tab>
              <tab key="tab7" text="{resource:getString($constants,'auxData')}">
                <xsl:call-template name="AuxDataTab">
                  <xsl:with-param name="maxRows">8</xsl:with-param>
                  <xsl:with-param name="col2Width">246</xsl:with-param>
                  <xsl:with-param name="col3Width">247</xsl:with-param>
                  <xsl:with-param name="showTwoInfoRows">true</xsl:with-param>
                </xsl:call-template>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>