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
  xmlns:meta="xalan://org.openelis.meta.InventoryAdjustmentMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="InventoryAdjustment" name="{resource:getString($constants,'inventoryAdjustment')}">
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
          </HorizontalPanel>
        </AbsolutePanel>
<!--end button panel-->
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"adjustmentNum")' />:
              </text>
              <textbox key="{meta:getId()}" width="75" max="20" tab="{meta:getDescription()},{meta:getInventoryLocationInventoryItemStoreId()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"description")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getDescription()}" width="414" max="60" tab="{meta:getAdjustmentDate()},{meta:getId()}" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"adjDate")' />:
              </text>
              <calendar key="{meta:getAdjustmentDate()}" begin="0" end="2" width="75" tab="{meta:getSystemUserId()},{meta:getDescription()}" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"user")' />:
              </text>
              <textbox key="{meta:getSystemUserId()}" width="125" tab="{meta:getInventoryLocationInventoryItemStoreId()},{meta:getAdjustmentDate()}" field="String" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"store")' />:
              </text>
              <dropdown key="{meta:getInventoryLocationInventoryItemStoreId()}" width="243" tab="{meta:getId()},{meta:getSystemUserId()}" field="Integer" />
            </row>
          </TablePanel>
          <VerticalPanel padding="0" spacing="0">
            <widget valign="top">
              <table key="adjustmentTable" width="auto" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides">
                <col key="{meta:getInventoryLocationId()}" width="55" header="{resource:getString($constants,'locationNum')}">
                  <textbox field="Integer" required="true" />
                </col>
                <col key="{meta:getInventoryLocationInventoryItemName()}" width="205" header="{resource:getString($constants,'inventoryItem')}">
                  <autoComplete width="auto" case="LOWER" field="Integer" required="true">
                    <col width="130" header="{resource:getString($constants,'name')}" />
                    <col width="110" header="{resource:getString($constants,'store')}" />
                    <col width="160" header="{resource:getString($constants,'location')}" />
                    <col width="70" header="{resource:getString($constants,'lotNum')}" />
                    <col width="70" header="{resource:getString($constants,'expDate')}" />
                    <col width="30" header="{resource:getString($constants,'qty')}" />
                  </autoComplete>
                </col>
                <col width="225" header="{resource:getString($constants,'storageLocation')}">
                  <label field="Integer" />
                </col>
                <col key="{meta:getInventoryLocationQuantityOnhand()}" width="65" header="{resource:getString($constants,'onHand')}">
                  <label field="Integer" />
                </col>
                <col key="{meta:getInventoryXAdjustPhysicalCount()}" width="65" header="{resource:getString($constants,'physCount')}">
                  <label field="Integer" />
                </col>
                <col key="{meta:getInventoryXAdjustQuantity()}" width="65" header="{resource:getString($constants,'adjQuan')}">
                  <label field="String" />
                </col>
              </table>
            </widget>
            <HorizontalPanel style="TableFooterPanel">
                <widget halign="center">
                  <appButton key="addRowButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"addRow")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
                <widget halign="center">
                  <appButton key="removeRowButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"removeRow")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
              </HorizontalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
