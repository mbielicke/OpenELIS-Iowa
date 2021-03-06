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
              <table key="atozTable" width="auto" maxRows="19" style="atozTable">
                <col width="85" header="{resource:getString($constants,'adjustmentNum')}">
                  <label field="Integer" />
                </col>
                <col width="90" header="{resource:getString($constants,'adjDate')}">
                  <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
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
                    <menuItem key="inventoryAdjustmentHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'inventoryAdjustmentHistory')}" />
                    <menuItem key="inventoryAdjustmentLocationHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'inventoryAdjustmentLocationHistory')}" />
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
                <xsl:value-of select='resource:getString($constants,"adjustmentNum")' />:
              </text>
              <textbox key="{meta:getId()}" width="50" tab="{meta:getDescription()},{meta:getInventoryLocationInventoryItemStoreId()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"description")' />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getDescription()}" width="414" max="60" tab="{meta:getAdjustmentDate()},{meta:getId()}" field="String" required  = "true"/>
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"adjDate")' />:
              </text>
              <calendar key="{meta:getAdjustmentDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getSystemUserId()},{meta:getDescription()}" required  = "true"/>
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
                  <autoComplete width="55" case="LOWER" field="Integer" required  = "true" >
                     <col width="55" header="{resource:getString($constants,'locationNum')}" />
                     <col width="130" header="{resource:getString($constants,'name')}" />
                     <col width="110" header="{resource:getString($constants,'store')}" />
                     <col width="160" header="{resource:getString($constants,'location')}" />
                     <col width="70" header="{resource:getString($constants,'lotNum')}" />
                     <col width="70" header="{resource:getString($constants,'expDate')}" />
                     <col width="30" header="{resource:getString($constants,'qty')}" />                    
                  </autoComplete>
                </col>
                <col key="{meta:getInventoryLocationInventoryItemName()}" width="205" header="{resource:getString($constants,'inventoryItem')}">
                  <autoComplete width="205" case="LOWER" field="Integer" required  = "true" >
                    <col width="130" header="{resource:getString($constants,'name')}" />
                    <col width="110" header="{resource:getString($constants,'store')}" />
                    <col width="160" header="{resource:getString($constants,'location')}" />
                    <col width="70" header="{resource:getString($constants,'lotNum')}" />
                    <col width="70" header="{resource:getString($constants,'expDate')}" />
                    <col width="30" header="{resource:getString($constants,'qty')}" />
                  </autoComplete>
                </col>
                <col width="225" header="{resource:getString($constants,'storageLocation')}">
                  <label field="String" />
                </col>
                <col key="{meta:getInventoryLocationQuantityOnhand()}" width="65" header="{resource:getString($constants,'onHand')}">
                  <label field="Integer" />
                </col>
                <col key="{meta:getInventoryXAdjustPhysicalCount()}" width="65" header="{resource:getString($constants,'physCount')}">
                  <textbox field="Integer" required  = "true"/>
                </col>
                <col key="{meta:getInventoryXAdjustQuantity()}" width="65" header="{resource:getString($constants,'adjQuan')}">
                  <label field="Integer" />
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
     </HorizontalPanel>	
    </screen>
  </xsl:template>
</xsl:stylesheet>
