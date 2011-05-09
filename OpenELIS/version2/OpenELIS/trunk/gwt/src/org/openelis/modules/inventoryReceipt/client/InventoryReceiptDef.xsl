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
  xmlns:meta="xalan://org.openelis.meta.InventoryReceiptMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="InventoryReceipt" name="{resource:getString($constants,'inventoryReceipt')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton">
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
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <widget valign="top">
            <table key="receiptTable" width="auto" maxRows="10" showScroll="ALWAYS" title="">
              <col key="{meta:getOrderItemOrderId()}" width="50" header="{resource:getString($constants,'ordNum')}">
                <textbox field="Integer" />
              </col>
              <col key="{meta:getOrderItemOrderExternalOrderNumber()}" width="107" header="{resource:getString($constants,'extOrderNum')}">
                <textbox field="String" />
              </col>
              <col key="{meta:getReceivedDate()}" width="75" header="{resource:getString($constants,'dateRec')}">
                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
              </col>
              <col key="{meta:getUpc()}" width="80" header="{resource:getString($constants,'upc')}">
                <autoComplete width="120" case="LOWER" field="Integer">
                  <col width="100" header="{resource:getString($constants,'upc')}" />
                  <col width="150" header="{resource:getString($constants,'inventoryItem')}" />
                </autoComplete>
              </col>
              <col key="{meta:getInventoryItemName()}" width="140" header="{resource:getString($constants,'inventoryItem')}">
                <autoComplete width="120" case="LOWER" field="Integer" >
                  <col width="100" header="{resource:getString($constants,'name')}" />
                  <col width="150" header="{resource:getString($constants,'store')}" />
                  <col width="150" header="{resource:getString($constants,'dispensedUnits')}" />
                </autoComplete>
              </col>
              <col key="{meta:getOrganizationName()}" width="160" header="{resource:getString($constants,'vendor')}">
                <autoComplete width="140" case="UPPER" field="Integer" >
                  <col width="180" header="{resource:getString($constants,'name')}" />
                  <col width="110" header="{resource:getString($constants,'street')}" />
                  <col width="100" header="{resource:getString($constants,'city')}" />
                  <col width="20" header="{resource:getString($constants,'st')}" />
                </autoComplete>
              </col>
              <col key="{meta:getOrderItemQuantity()}" width="50" header="{resource:getString($constants,'numReq')}" align = "right">
                <label field="Integer" />
              </col>
              <col key="{meta:getQuantityReceived()}" width="50" header="{resource:getString($constants,'numRec')}" align = "right">
                <textbox field="Integer" />
              </col>
              <col key="{meta:getUnitCost()}" width="55" header="{resource:getString($constants,'cost')}" align = "right">
                <textbox pattern = "{resource:getString($constants,'displayCurrencyFormat')}" field="Double" />
              </col>
            </table>
          </widget>
          <widget style="TableButtonFooter">
            <HorizontalPanel>
              <appButton key="addReceiptButton" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="AddRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'addRow')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <appButton key="removeReceiptButton" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="RemoveRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'removeRow')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </HorizontalPanel>
          </widget>
          <VerticalPanel style="WhiteContentPanel">
            <TabPanel key="tabPanel" width="802" height="110">
              <tab key="itemTab" text="{resource:getString($constants,'items')}">
                <VerticalPanel>
                  <HorizontalPanel>
                    <HorizontalPanel width="355" />
                    <TablePanel padding="0" spacing="0" style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"addToExisting")' />:
                        </text>
                        <check key="addToExisting" tab = "{meta:getInventoryLocationStorageLocationId()},{meta:getInventoryLocationExpirationDate()}"/>
                      </row>
                    </TablePanel>
                  </HorizontalPanel>
                  <HorizontalPanel>
                    <TablePanel padding="0" style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"description")' />:
                        </text>
                        <widget colspan="2">
                          <textbox key="{meta:getInventoryItemDescription()}" width="275" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"store")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getInventoryItemStoreId()}" width="115" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"dispensedUnits")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getInventoryItemDispensedUnitsId()}" width="115" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                    </TablePanel>
                    <TablePanel padding="0" style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"location")' />:
                        </text>
                        <widget colspan="7">
                          <autoComplete key="{meta:getInventoryLocationStorageLocationId()}"  width="300" field="Integer" tab = "{meta:getInventoryLocationLotNumber()},addToExisting">
                            <col width="300" header="{resource:getString($constants,'description')}" />
                            <col width="65" header="{resource:getString($constants,'lotNum')}" />
                            <col width="55" header="{resource:getString($constants,'qty')}" />
                            <col width="65" header="{resource:getString($constants,'expDate')}">
                              <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                            </col>
                          </autoComplete>
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"lotNum")' />:
                        </text>
                        <widget colspan="2">
                          <textbox key="{meta:getInventoryLocationLotNumber()}" width="100" max="30" field="String" case = "UPPER" tab = "{meta:getQcReference()},{meta:getInventoryLocationStorageLocationId()}"/>
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"extQC")' />:
                        </text>
                        <widget colspan="2">
                          <textbox key="{meta:getQcReference()}" width="100" field="String" tab = "{meta:getInventoryLocationExpirationDate()},{meta:getInventoryLocationLotNumber()}"/>
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"expDate")' />:
                        </text>
                        <widget colspan="2">
                          <calendar key="{meta:getInventoryLocationExpirationDate()}" begin="0" end="2" width="100" tab = "addToExisting,{meta:getQcReference()}"/>
                        </widget>
                      </row>
                    </TablePanel>
                  </HorizontalPanel>
                </VerticalPanel>
              </tab>
              <tab key="vendorTab" text="{resource:getString($constants,'vendorAddress')}">
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                    </text>
                    <widget colspan="5">
                      <textbox key="{meta:getOrganizationAddressMultipleUnit()}" width="180" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"address")' />:
                    </text>
                    <widget colspan="5">
                      <textbox key="{meta:getOrganizationAddressStreetAddress()}" width="180" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"city")' />:
                    </text>
                    <widget colspan="5">
                      <textbox key="{meta:getOrganizationAddressCity()}" width="180" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"state")' />:
                    </text>
                    <widget>
                      <textbox key="{meta:getOrganizationAddressState()}" width="30" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                    </widget>
                    <widget>
                      <HorizontalPanel width="5" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                    </text>
                    <widget>
                      <textbox key="{meta:getOrganizationAddressZipCode()}" width="72" mask="99999-9999" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                    </widget>
                  </row>
                </TablePanel>
              </tab>
              <tab key="noteTab" text="{resource:getString($constants,'orderShippingNotes')}" tab="notesPanel, notesPanel">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="802" height="75" />
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
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
