
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
    <screen name="{resource:getString($constants,'inventoryItem')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="23" style="atozTable">
                <col width="110" header="{resource:getString($constants,'name')}">
                  <label field = "String"/>
                </col>
                <col width="105" header="{resource:getString($constants,'store')}">
                  <dropdown width="105" field="Integer" />
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
                      <Grid>
                        <row>
                          <cell text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                  <xsl:call-template name="duplicateRecordMenuItem" />
                  <menuItem key="invItemHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'invItemHistory')}" />
                  <menuItem key="invComponentHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'invComponentHistory')}" />
                  <menuItem key="invLocationHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'invLocationHistory')}" />
              </menu>
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
              <TabPanel key="tabPanel" width="605" height="285">
<!-- TAB 1  -->
                <tab key="componentTab" text="{resource:getString($constants,'components')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="componentTable" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" tab="{meta:getName()},{meta:getIsNotInventoried()}">
                      <col key="{meta:getComponentName()}" width="138" header="{resource:getString($constants,'component')}">
                        <autoComplete width="137" case="LOWER" required="true">
                          <col width="135" header="{resource:getString($constants,'name')}" />
                          <col width="300" header="{resource:getString($constants,'description')}" />
                        </autoComplete>
                      </col>
                      <col key="{meta:getComponentDescription()}" width="370" header="{resource:getString($constants,'description')}">
                        <label field = "String"/>
                      </col>
                      <col key="{meta:getComponentQuantity()}" width="70" header="{resource:getString($constants,'quantity')}">
                        <textbox max="10" field="Integer" required="true" />
                      </col>
                    </table>
                    <widget style="TableButtonFooter">
                      <HorizontalPanel>
                        <button key="addComponentButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                        <button key="removeComponentButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                      </HorizontalPanel>
                    </widget>
                  </VerticalPanel>
                </tab>
<!-- TAB 2 -->
                <tab key="locationTab" text="{resource:getString($constants,'locationQuantity')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="locationTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                      <col key="{meta:getLocationStorageLocationName()}" width="172" sort="true" header="{resource:getString($constants,'location')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getLocationLotNumber()}" width="110" sort="true" header="{resource:getString($constants,'lotNum')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getLocationId()}" width="70" header="{resource:getString($constants,'serialNum')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getLocationExpirationDate()}" width="105" sort="true" header="{resource:getString($constants,'expirationDate')}">
                        <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                      </col>
                      <col key="{meta:getLocationQuantityOnhand()}" width="115" sort="true" header="{resource:getString($constants,'quantityOnHand')}">
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
                        <autoComplete key="{meta:getParentInventoryItemName()}" width="210" tab="{meta:getParentRatio()},{meta:getProductUri()}">
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
                    <ScrollPanel width="604" height="247" style="ScreenTable">
                      <html key="manufacturingPanel" width="100%" height="100%" />
                    </ScrollPanel>
                    <button key="editManufacturingButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'edit')}" style="Button"/>
                  </VerticalPanel>
                </tab>
<!-- start TAB 5 -->
                <tab key="noteTab" text="{resource:getString($constants,'note')}">
                  <VerticalPanel padding="0" spacing="0">
                    <notes key="notesPanel" width="604" height="247" />
                    <button key="standardNoteButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'addNote')}" style="Button"/>
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
