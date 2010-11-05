
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
<!-- main screen -->
    <screen name="{resource:getString($constants,'internalOrder')}">
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
              <table key="atozTable" rows="20" style="atozTable">
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
                    <AbsolutePanel style="prevNavIndex" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="nextNavIndex" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
<!--button panel code-->
        <VerticalPanel padding="0" spacing="0">
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
                          <cell text="{resource:getString($constants,'options')}" />
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
                <textbox key="{meta:getId()}" width="50" tab="{meta:getNeededInDays()},{meta:getCostCenterId()}" field="Integer" />
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"neededDays")' />:
                </text>
                <widget colspan="3">
                  <textbox key="{meta:getNeededInDays()}" width="50" tab="{meta:getStatusId()},{meta:getId()}" field="Integer" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'status')" />:
                </text>
                <dropdown key="{meta:getStatusId()}" width="90" tab="{meta:getRequestedBy()},{meta:getNeededInDays()}" field="Integer" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'requestedBy')" />:
                </text>
                <textbox key="{meta:getRequestedBy()}" width="175" tab="{meta:getOrderedDate()},{meta:getStatusId()}" field="String" case = "LOWER" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"orderDate")' />:
                </text>
                <calendar key="{meta:getOrderedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCostCenterId()},{meta:getRequestedBy()}" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                </text>
                <dropdown key="{meta:getCostCenterId()}" width="187" tab="tabPanel,{meta:getOrderedDate()}" field="Integer" />
              </row>
            </TablePanel>
<!-- TAB PANEL -->
            <TabPanel key="tabPanel" width="605" height="305">
<!-- TAB 1 (Items) -->
              <tab key="itemTab" text="{resource:getString($constants,'items')}" tab="itemTable, itemTable">
                <VerticalPanel padding="0" spacing="0">
                  <table key="itemTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getOrderItemQuantity()}" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <textbox field="Integer" required="true" />
                    </col>
                    <col key="{meta:getOrderItemInventoryItemName()}" width="275" header="{resource:getString($constants,'inventoryItem')}">
                      <autoComplete width="auto" case="LOWER" field="Integer" required="true">
                        <col width="155" header="{resource:getString($constants,'name')}" />
                        <col width="110" header="{resource:getString($constants,'store')}">
                          <dropdown width="110" field="Integer" />
                        </col>
                        <col width="110" header="{resource:getString($constants,'dispensedUnits')}">
                          <dropdown width="110" field="Integer" />
                        </col>
                      </autoComplete>
                    </col>
                    <col key="{meta:getOrderItemInventoryItemStoreId()}" width="238" header="{resource:getString($constants,'store')}">
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
              <tab key="fillTab" text="{resource:getString($constants,'filled')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="fillTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getInventoryReceiptOrderItemId()}" width="150" header="{resource:getString($constants,'inventoryItem')}">
                      <label field="String" />
                    </col>
                    <col key="location" width="180" header="{resource:getString($constants,'location')}">
                      <label field="String" />
                    </col>
                    <col key="{meta:getInventoryReceiptQuantityReceived()}" width="65" align="right" header="{resource:getString($constants,'quantity')}">
                      <label field="String" />
                    </col>
                    <col key="" width="85" header="{resource:getString($constants,'lotNum')}">
                      <label field="String" />
                    </col>
                    <col key="" width="92" header="{resource:getString($constants,'expDate')}">
                      <calendar pattern="{resource:getString($constants,'datePattern')}" begin = "0" end = "2" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>
<!-- TAB 3 (order notes) -->
              <tab key="noteTab" text="{resource:getString($constants,'orderShippingNotes')}" tab="notesPanel, notesPanel">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="604" height="267" />
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
