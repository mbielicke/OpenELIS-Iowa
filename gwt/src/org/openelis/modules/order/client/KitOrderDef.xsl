
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
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="KitOrder" name="{resource:getString($constants,'kitOrder')}">
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
              <table key="atozTable" width="auto" maxRows="22" style="atozTable">
                <col width="75" header="{resource:getString($constants,'orderNum')}">
                  <label field="Integer" />
                </col>
                <col width="150" header="{resource:getString($constants,'requestedBy')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton key="atozPrev" style="Button" enable="false">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton key="atozNext" style="Button" enable="false">
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
                    <menuItem key="orderHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'orderHistory')}" />
                    <menuItem key="itemHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'orderItemHistory')}" />
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
                  <autoComplete key="{meta:getOrganizationName()}" width="188" case="UPPER" tab="{meta:getStatusId()},{meta:getShipFromId()}" field="Integer">
                    <col width="180" header="Name" />
                    <col width="110" header="Street" />
                    <col width="100" header="City" />
                    <col width="20" header="St" />
                  </autoComplete>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"status")' />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="90" case="MIXED" popWidth="auto" tab="{meta:getOrderedDate()},{meta:getOrganizationName()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressMultipleUnit()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="Integer" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderDate")' />:
                </text>
                <calendar key="{meta:getOrderedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getRequestedBy()},{meta:getOrganizationName()}" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"address")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressStreetAddress()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"requestedBy")' />:
                </text>
                <textbox key="{meta:getRequestedBy()}" width="203" tab="{meta:getCostCenterId()},{meta:getOrderedDate()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"city")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressCity()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                </text>
                <dropdown key="{meta:getCostCenterId()}" width="203" case="MIXED" popWidth="auto" tab="{meta:getDescription()},{meta:getRequestedBy()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"state")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressState()}" width="35" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <HorizontalPanel width="17" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressZipCode()}" width="65" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />:
                </text>
                <widget colspan="3">
                  <autoComplete key="{meta:getDescription()}" width="300" case="MIXED" tab="{meta:getId()},{meta:getCostCenterId()}" field="String">
                    <col width="310" header="Description" />
                  </autoComplete>
                </widget>
              </row>
            </TablePanel>

<!-- TAB PANEL -->

            <TabPanel key="tabPanel" width="605" height="285">

<!-- TAB 1 (items) -->

              <tab key="itemTab" text="{resource:getString($constants,'items')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="itemTable" width="auto" maxRows="10" showScroll="ALWAYS">
                    <col key="{meta:getOrderItemQuantity()}" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <textbox field="Integer" required="true" />
                    </col>
                    <col key="{meta:getOrderItemInventoryItemName()}" width="275" header="{resource:getString($constants,'inventoryItem')}">
                      <autoComplete width="auto" case="LOWER" field="Integer" required="true">
                        <col width="155" header="{resource:getString($constants,'name')}" />
                        <col width="110" header="{resource:getString($constants,'store')}">
                          <dropdown width="110" popWidth="auto" field="Integer" />
                        </col>
                        <col width="110" header="{resource:getString($constants,'dispensedUnits')}">
                          <dropdown width="110" popWidth="auto" field="Integer" />
                        </col>
                      </autoComplete>
                    </col>
                    <col key="{meta:getOrderItemInventoryItemStoreId()}" width="238" header="{resource:getString($constants,'store')}">
                      <dropdown width="235" field="Integer" />
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

<!-- TAB 2 (receipts) -->

              <tab key="fillTab" text="{resource:getString($constants,'filled')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="fillTable" width="auto" maxRows="10" showScroll="ALWAYS">
                    <col key="" width="150" header="{resource:getString($constants,'inventoryItem')}">
                      <label field="String" />
                    </col>
                    <col key="" width="180" header="{resource:getString($constants,'location')}">
                      <label field="String" />
                    </col>
                    <col key="" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <label field="String" />
                    </col>
                    <col key="" width="85" header="{resource:getString($constants,'lotNum')}">
                      <label field="String" />
                    </col>
                    <col key="" width="92" header="{resource:getString($constants,'expDate')}">
                      <label field="String" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>

<!-- TAB 4 (order notes) -->

              <tab key="noteTab" text="{resource:getString($constants,'orderShippingNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="604" height="247" />
                  <appButton key="standardNoteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="StandardNoteButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addNote')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </VerticalPanel>
              </tab>

<!-- TAB 3 (customer notes)-->

              <tab key="customerNote" text="{resource:getString($constants,'customerNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="customerNotesPanel" width="604" height="247" />
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

<!-- TAB 5 (report to/bill to)-->

              <tab key="reportToBillToTab" text="{resource:getString($constants,'reportToBillTo')}">
                <HorizontalPanel height="247" padding="0" spacing="0">
                  <VerticalPanel>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"reportTo")' />:
                        </text>
                        <widget colspan="5">
                          <autoComplete key="{meta:getReportToName()}" width="188" case="UPPER" tab="{meta:getBillToName()},{meta:getBillToName()}" field="Integer">
                            <col width="180" header="Name" />
                            <col width="110" header="Street" />
                            <col width="100" header="City" />
                            <col width="20" header="St" />
                          </autoComplete>
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
                          <HorizontalPanel width="17" />
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getReportToAddressZipCode()}" width="65" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
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
                          <autoComplete key="{meta:getBillToName()}" width="188" case="UPPER" tab="{meta:getReportToName()},{meta:getReportToName()}" field="Integer">
                            <col width="180" header="Name" />
                            <col width="110" header="Street" />
                            <col width="100" header="City" />
                            <col width="20" header="St" />
                          </autoComplete>
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
                          <HorizontalPanel width="17" />
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getBillToAddressZipCode()}" width="65" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                </HorizontalPanel>
              </tab>
              <tab key="tab6" text="Tab 6">
                <VerticalPanel height="247" />
              </tab>
              <tab key="tab7" text="Tab 7">
                <VerticalPanel height="247" />
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
