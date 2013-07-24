

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
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'vendorOrder')}">
      <HorizontalPanel padding="0" spacing="0">
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
                <textbox key="{meta:getId()}" width="90" case="LOWER" max="20" tab="{meta:getNeededInDays()},{meta:getExternalOrderNumber()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"neededDays")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getNeededInDays()}" width="75" tab="{meta:getStatusId()},{meta:getId()}" field="Integer" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"status")' />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="90" case="MIXED" tab="{meta:getOrganizationName()},{meta:getNeededInDays()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"vendor")' />:
                </text>
                <widget colspan="5">
                  <autoComplete key="{meta:getOrganizationName()}" width="188" case="UPPER" tab="{meta:getOrderedDate()},{meta:getStatusId()}" required="true" >
                    <col width="180" header="{resource:getString($constants,'name')}" />
                    <col width="110" header="{resource:getString($constants,'street')}" />
                    <col width="100" header="{resource:getString($constants,'city')}" />
                    <col width="20" header="{resource:getString($constants,'st')}" />
                  </autoComplete>
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderDate")' />:
                </text>
                <calendar key="{meta:getOrderedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getOrganizationAttention()},{meta:getOrganizationName()}" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"attention")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAttention()}" tab="{meta:getRequestedBy()},{meta:getOrderedDate()}" case = "UPPER" width="188" max = "30" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"requestedBy")' />:
                </text>
                <textbox key="{meta:getRequestedBy()}" width="203" tab="{meta:getCostCenterId()},{meta:getOrganizationAttention()}" case = "LOWER" field="String" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressMultipleUnit()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>                
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                </text>
                <dropdown key="{meta:getCostCenterId()}" width="203" case="MIXED" tab="{meta:getExternalOrderNumber()},{meta:getRequestedBy()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"address")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressStreetAddress()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"extOrderNum")' />:
                </text>
                <textbox key="{meta:getExternalOrderNumber()}" width="203" case="MIXED" max="20" tab="tabPanel,{meta:getCostCenterId()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"city")' />:
                </text>
                <widget colspan="5">
                  <textbox key="{meta:getOrganizationAddressCity()}" width="188" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>              
              <row>
                <widget colspan = "2">
                  <HorizontalPanel width="188" />
                </widget>
               	<text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"state")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressState()}" width="35" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <HorizontalPanel width="10" />
                </widget>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                </text>
                <widget>
                  <textbox key="{meta:getOrganizationAddressZipCode()}" width="72" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
              </row>
            </TablePanel>

<!-- TAB PANEL -->

            <TabPanel key="tabPanel" width="600" height="300">

<!-- TAB 1 (items) -->

              <tab key="itemTab" text="{resource:getString($constants,'items')}" tab="itemTable, itemTable">
                <VerticalPanel padding="0" spacing="0">
                  <widget>
                    <table key="itemTable" rows="10" width="600" vscroll="ALWAYS" hscroll="ALWAYS">
                      <col key="{meta:getOrderItemQuantity()}" width="60" align="right" header="{resource:getString($constants,'quantity')}">
                        <textbox field="Integer" required="true" />
                      </col>
                      <col key="{meta:getOrderItemInventoryItemName()}" width="178" header="{resource:getString($constants,'inventoryItem')}">
                        <autoComplete width="210" case="LOWER" field="Integer">
                          <col width="155" header="{resource:getString($constants,'name')}" />
                          <col width="110" header="{resource:getString($constants,'store')}">
                            <dropdown width="110" field="Integer" />
                          </col>
                          <col width="110" header="{resource:getString($constants,'dispensedUnits')}">
                            <dropdown width="110" field="Integer" />
                          </col>
                        </autoComplete>
                      </col>
                      <col key="{meta:getOrderItemInventoryItemStoreId()}" width="163" header="{resource:getString($constants,'store')}">
                        <dropdown width="163" field="Integer" />
                      </col>
                      <col key="{meta:getOrderItemUnitCost()}" width="70" header="{resource:getString($constants,'unitCost')}" align = "right">
                        <textbox field="Double" />
                      </col>
                      <col key="{meta:getOrderItemCatalogNumber()}" width="87" header="{resource:getString($constants,'catalogNum')}">
                        <textbox field="String" />
                      </col>
                    </table>
                  </widget>
                  <HorizontalPanel>
                    <button key="addItemButton" icon="AddRowButtonImage" text="resource:getString($constants,'addRow')" style="Button"/>
                    <button key="removeItemButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>

<!-- TAB 2 (receipts) -->

              <tab key="fillTab" text="{resource:getString($constants,'filled')}">
                <VerticalPanel padding="0" spacing="0" >
                <table key="fillTable" width="600" rows="10" vscroll="ALWAYS" hscroll="ALWAYS">
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
                  <col key="" width="92" header="{resource:getString($constants,'expDate')}">
                    <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                  </col>
                  <col key="{meta:getInventoryReceiptReceivedDate()}" width="80" header="{resource:getString($constants,'dateRec')}">
                    <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                  </col>
                  <col key="{meta:getInventoryReceiptUnitCost()}" width="55" header="{resource:getString($constants,'cost')}" align = "right">
                    <textbox field="Double" />
                  </col>
                  <col key="{meta:getInventoryReceiptExternalReference()}" width="130" header="{resource:getString($constants,'extReference')}">
                    <label field="String" />
                  </col>
                </table>
                </VerticalPanel>
              </tab>

<!-- TAB 3 (order notes) -->

              <tab key="noteTab" text="{resource:getString($constants,'orderShippingNotes')}" tab="notesPanel, notesPanel">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="602" height="236" />
                  <button key="standardNoteButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'addNote')}" style="Button"/>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>