
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
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'inventoryTransfer')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="addButton"/>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton"/>
            <xsl:call-template name="abortButton"/>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <widget valign="top">
            <table key="receiptTable" rows="10" vscroll="ALWAYS" hscroll="ALWAYS">
              <col key="fromItemName" width="135" header="{resource:getString($constants,'fromItem')}">
                <autoComplete required="true">
                  <col width="100" header="{resource:getString($constants,'name')}" />
                  <col width="110" header="{resource:getString($constants,'store')}" />
                  <col width="160" header="{resource:getString($constants,'location')}" />
                  <col width="30" header="{resource:getString($constants,'qty')}" />
                </autoComplete>
              </col>
              <col key="fromLoc" width="160" header="{resource:getString($constants,'fromLoc')}">
                <label field="String" />
              </col>
              <col key="qtyOnHand" width="55" header="{resource:getString($constants,'onHand')}">
                <label field="Integer" />
              </col>
              <col key="qtyReceived" width="40" header="{resource:getString($constants,'qty')}">
                <textbox key="inventoryReceiptgetQuantityReceived" required = "true" field="Integer" />
              </col>
              <col key="toItemName" width="135"  header="{resource:getString($constants,'toItem')}">
                <autoComplete width="400" tableWidth="400" case="LOWER" required = "true">
                  <col width="100" header="{resource:getString($constants,'name')}" />
                  <col width="150" header="{resource:getString($constants,'store')}" />
                  <col width="150" header="{resource:getString($constants,'dispensedUnits')}" />
                </autoComplete>
              </col>
              <col key="addToExisting" width="75" header="{resource:getString($constants,'ext')}">
                <check />
              </col>
              <col key="toLoc" width="160" header="{resource:getString($constants,'toLoc')}">
                <autoComplete width="137" required="true">
                  <col width="300" header="{resource:getString($constants,'description')}" />
                  <col width="65" header="{resource:getString($constants,'lotNum')}" />
                  <col width="55" header="{resource:getString($constants,'qty')}" />
                  <col width="65" header="{resource:getString($constants,'expDate')}">
                    <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                  </col>                                   
                </autoComplete>
              </col>
            </table>
          </widget>
          <widget style="TableButtonFooter">
            <HorizontalPanel>
              <button key="addReceiptButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
              <button key="removeReceiptButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
            </HorizontalPanel>
          </widget>
          <VerticalPanel style="subform">
            <text style="FormTitle">
              <xsl:value-of select='resource:getString($constants,"itemInformation")' />
            </text>
            <TablePanel style="Form">
              <row>
                <HorizontalPanel />
                <text style="TopPrompt">
                  <xsl:value-of select='resource:getString($constants,"description")' />
                </text>
                <text style="TopPrompt">
                  <xsl:value-of select='resource:getString($constants,"store")' />
                </text>
                <text style="TopPrompt">
                  <xsl:value-of select='resource:getString($constants,"dispensedUnits")' />
                </text>
                <text style="TopPrompt">
                  <xsl:value-of select='resource:getString($constants,"lotNum")' />
                </text>
                <text style="TopPrompt">
                  <xsl:value-of select='resource:getString($constants,"expDate")' />
                </text>
              </row>
              <row>
                <text style="Prompt"><xsl:value-of select="resource:getString($constants,'from')" />:</text>
                <widget>
                  <textbox key="inventoryItemDescription" width="225" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="inventoryItemStoreId" width="165" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="inventoryItemDispensedUnitsId" width="90" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="inventoryLocationLotNumber" width="130" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="inventoryLocationExpirationDate" width="122" case="MIXED" style="ScreenTextboxDisplayOnly" pattern="{resource:getString($constants,'datePattern')}" field="String" />
                </widget>
              </row>
              <row>
                <text style="Prompt"><xsl:value-of select="resource:getString($constants,'to')" />:</text>
                <widget>
                  <textbox key="toDescription" width="225" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="toStoreId" width="165" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="toDispensedUnits" width="90" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="toLotNumber" width="130" case="MIXED" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                </widget>
                <widget>
                  <textbox key="toExpDate" width="122" case="MIXED" style="ScreenTextboxDisplayOnly" pattern="{resource:getString($constants,'datePattern')}" field="String" />
                </widget>
              </row>
            </TablePanel>
          </VerticalPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>