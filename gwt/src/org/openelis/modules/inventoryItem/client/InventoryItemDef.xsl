
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
  xmlns:meta="xalan://org.openelis.meta.InventoryItemMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen id="Inventory" name="{resource:getString($constants,'inventoryItem')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="23" style="atozTable">
                <col width="110" header="{resource:getString($constants,'name')}">
                  <label field = "String"/>
                </col>
                <col width="105" header="{resource:getString($constants,'store')}">
                  <dropdown width="105" field="Integer" />
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
<!--button panel code-->
        <VerticalPanel padding="0" spacing="0">
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
                  <menuPanel layout="vertical" position="below" style="buttonMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <menuItem key="invItemHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'invItemHistory')}" />
                    <menuItem key="invComponentHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'invComponentHistory')}" />
                    <menuItem key="invLocationHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'invLocationHistory')}" />
                  </menuPanel>
                </menuItem>
              </menuPanel>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"id")' />:
                  </text>
                  <textbox key="{meta:getId()}" width="75" tab="{meta:getName()},{meta:getId()}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"name")' />:
                  </text>
                  <textbox key="{meta:getName()}" width="215" case="LOWER" max="30" tab="{meta:getDescription()},{meta:getId()}" required = "true" field="String" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"description")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getDescription()}" width="340" max="60" tab="{meta:getCategoryId()},{meta:getName()}" field="String" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"category")' />:
                  </text>
                  <widget colspan="3">
                    <dropdown key="{meta:getCategoryId()}" width="180" tab="{meta:getStoreId()},{meta:getDescription()}" field="Integer" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"store")' />:
                  </text>
                  <widget colspan="3">
                    <dropdown key="{meta:getStoreId()}" width="225" tab="{meta:getQuantityMinLevel()},{meta:getCategoryId()}" required = "true" field="Integer" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"minOrderLevel")' />:
                  </text>
                  <textbox key="{meta:getQuantityMinLevel()}" width="55" tab="{meta:getQuantityToReorder()},{meta:getStoreId()}" required = "true" field="Integer" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"reorderLevel")' />:
                  </text>
                  <textbox key="{meta:getQuantityToReorder()}" width="55" tab="{meta:getQuantityMaxLevel()},{meta:getQuantityMinLevel()}" required = "true" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"maxOrderLevel")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getQuantityMaxLevel()}" width="55" tab="{meta:getDispensedUnitsId()},{meta:getQuantityToReorder()}" field="Integer" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"dispensedUnits")' />:
                  </text>
                  <dropdown key="{meta:getDispensedUnitsId()}" width="150" tab="{meta:getIsActive()},{meta:getQuantityMaxLevel()}" required = "true" field="Integer" />
                </row>
              </TablePanel>
              <VerticalPanel style="subform">
                <text style="FormTitle">
                  <xsl:value-of select='resource:getString($constants,"controlsParameters")' />
                </text>
                <TablePanel style="Form">
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"active")' />:
                    </text>
                    <check key="{meta:getIsActive()}" tab="{meta:getIsReorderAuto()},{meta:getDispensedUnitsId()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"autoReorder")' />:
                    </text>
                    <check key="{meta:getIsReorderAuto()}" tab="{meta:getIsLotMaintained()},{meta:getIsActive()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"maintainLot")' />:
                    </text>
                    <check key="{meta:getIsLotMaintained()}" tab="{meta:getIsSerialMaintained()},{meta:getIsReorderAuto()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"serialRequired")' />:
                    </text>
                    <check key="{meta:getIsSerialMaintained()}" tab="{meta:getIsBulk()},{meta:getIsLotMaintained()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"bulk")' />:
                    </text>
                    <check key="{meta:getIsBulk()}" tab="{meta:getIsNotForSale()},{meta:getIsSerialMaintained()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"notForSale")' />:
                    </text>
                    <check key="{meta:getIsNotForSale()}" tab="{meta:getIsSubAssembly()},{meta:getIsBulk()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"subAssembly")' />:
                    </text>
                    <check key="{meta:getIsSubAssembly()}" tab="{meta:getIsLabor()},{meta:getIsNotForSale()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"labor")' />:
                    </text>
                    <check key="{meta:getIsLabor()}" tab="{meta:getIsNotInventoried()},{meta:getIsSubAssembly()}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"doNotInventory")' />:
                    </text>
                    <check key="{meta:getIsNotInventoried()}" tab="{meta:getName()},componentTable" />
                  </row>
                </TablePanel>
              </VerticalPanel>
            </HorizontalPanel>
            <VerticalPanel>
<!-- TAB PANEL -->
              <TabPanel key="tabPanel" width="623" height="285">
<!-- TAB 1  -->
                <tab key="componentTab" text="{resource:getString($constants,'components')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="componentTable" width="auto" maxRows="10" showScroll="ALWAYS" tab="{meta:getName()},{meta:getIsNotInventoried()}">
                      <col key="{meta:getComponentName()}" width="150" header="{resource:getString($constants,'component')}">
                        <autoComplete width="137" case="LOWER" field="Integer" required="true">
                          <col width="135" header="{resource:getString($constants,'name')}" />
                          <col width="300" header="{resource:getString($constants,'description')}" />
                        </autoComplete>
                      </col>
                      <col key="{meta:getComponentDescription()}" width="376" header="{resource:getString($constants,'description')}">
                        <label field = "String"/>
                      </col>
                      <col key="{meta:getComponentQuantity()}" width="70" header="{resource:getString($constants,'quantity')}">
                        <textbox max="10" field="Integer" required="true" />
                      </col>
                    </table>
                    <widget style="TableButtonFooter">
                      <HorizontalPanel>
                        <appButton key="addComponentButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="AddRowButtonImage" />
                            <text>
                              <xsl:value-of select="resource:getString($constants,'addRow')" />
                            </text>
                          </HorizontalPanel>
                        </appButton>
                        <appButton key="removeComponentButton" style="Button">
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
<!-- TAB 2 -->
                <tab key="locationTab" text="{resource:getString($constants,'locationQuantity')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="locationTable" width="auto" maxRows="11" showScroll="ALWAYS">
                      <col key="{meta:getLocationId()}" width="52" header="{resource:getString($constants,'locationNum')}">
                        <textbox field="Integer" />
                      </col>
                      <col key="{meta:getLocationStorageLocationName()}" width="203" sort="true" header="{resource:getString($constants,'location')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getLocationLotNumber()}" width="110" sort="true" header="{resource:getString($constants,'lotNum')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getLocationId()}" width="52" header="{resource:getString($constants,'serialNum')}">
                        <textbox field="Integer" />
                      </col>
                      <col key="{meta:getLocationExpirationDate()}" width="105" sort="true" header="{resource:getString($constants,'expirationDate')}">
                        <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                      </col>
                      <col key="{meta:getLocationQuantityOnhand()}" width="65" sort="true" header="{resource:getString($constants,'qty')}">
                        <textbox field="Integer" required="true" />
                      </col>
                    </table>
                  </VerticalPanel>
                </tab>
<!-- start TAB 3 -->
                <tab key="additionalTab" text="{resource:getString($constants,'additionalInfo')}">
                  <TablePanel style="Form">
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"productURI")' />:
                      </text>
                      <widget colspan="5">
                        <textbox key="{meta:getProductUri()}" width="490" max="80" tab="{meta:getParentInventoryItemName()},{meta:getParentRatio()}" field="String" />
                      </widget>
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"parentItem")' />:
                      </text>
                      <widget colspan="3">
                        <autoComplete key="{meta:getParentInventoryItemName()}" width="210" tab="{meta:getParentRatio()},{meta:getProductUri()}" field="Integer">
                          <col width="135" header="{resource:getString($constants,'name')}" />
                          <col width="130" header="{resource:getString($constants,'store')}" />
                        </autoComplete>
                      </widget>
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"parentRatio")' />:
                      </text>
                      <textbox key="{meta:getParentRatio()}" width="55" max="30" tab="{meta:getProductUri()},{meta:getParentInventoryItemName()}" field="Integer" />
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageLeadTime")' />:
                      </text>
                      <textbox key="{meta:getAverageLeadTime()}" width="55" max="30" style="ScreenTextboxDisplayOnly" field="Integer" />
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageCost")' />:
                      </text>
                      <textbox key="{meta:getAverageCost()}" width="55" max="30" style="ScreenTextboxDisplayOnly" field="Double" />
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageDailyUse")' />:
                      </text>
                      <textbox key="{meta:getAverageDailyUse()}" width="55" max="30" style="ScreenTextboxDisplayOnly" field="Integer" />
                    </row>
                  </TablePanel>
                </tab>
<!-- start TAB 4 -->
                <tab key="manufacturingTab" text="{resource:getString($constants,'manufacturing')}">
                  <VerticalPanel padding="0" spacing="0">
                    <ScrollPanel width="622" height="247" style="ScreenTable">
                      <html key="manufacturingPanel" width="100%" height="100%" />
                    </ScrollPanel>
                    <appButton key="editManufacturingButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="StandardNoteButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'edit')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </VerticalPanel>
                </tab>
<!-- start TAB 5 -->
                <tab key="noteTab" text="{resource:getString($constants,'note')}">
                  <VerticalPanel padding="0" spacing="0">
                    <notes key="notesPanel" width="622" height="247" />
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
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
