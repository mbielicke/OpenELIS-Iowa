

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
  xmlns:componentMeta="xalan://org.openelis.metamap.InventoryComponentMetaMap"
  xmlns:invItemMeta="xalan://org.openelis.meta.InventoryItemMeta"
  xmlns:locationMeta="xalan://org.openelis.metamap.InventoryLocationMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.InventoryItemMetaMap"
  xmlns:noteMeta="xalan://org.openelis.meta.NoteMeta"
  xmlns:storageLocationMeta="xalan://org.openelis.meta.StorageLocationMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryItemMetaMap" />
  </xalan:component>
  <xalan:component prefix="noteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta" />
  </xalan:component>
  <xalan:component prefix="locationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryLocationMetaMap" />
  </xalan:component>
  <xalan:component prefix="componentMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryComponentMetaMap" />
  </xalan:component>
  <xalan:component prefix="storageLocationMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StorageLocationMeta" />
  </xalan:component>
  <xalan:component prefix="invItemMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.InventoryItemMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="invItem" select="meta:new()" />
    <xsl:variable name="parentInvItem" select="meta:getParentInventoryItem($invItem)" />
    <xsl:variable name="component" select="meta:getInventoryComponent($invItem)" />
    <xsl:variable name="location" select="meta:getInventoryLocation($invItem)" />
    <xsl:variable name="note" select="meta:getNote($invItem)" />
    <xsl:variable name="compInvItem" select="componentMeta:getInventoryItem($component)" />
    <xsl:variable name="locStorageLoc" select="locationMeta:getStorageLocation($location)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
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
              <table key="atozTable" width="auto" maxRows="20" style="atozTable">
                <col width="110" header="{resource:getString($constants,'name')}">
                  <label />
                </col>
                <col width="105" header="{resource:getString($constants,'store')}">
                  <label />
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
                        <AbsolutePanel width="20px" height="20px" style="OptionsButtonImage" />
                      </HorizontalPanel>
                    </appButton>
                  </menuDisplay>
                  <menuPanel layout="vertical" position="below" style="topMenuContainer">
                    <xsl:call-template name="duplicateRecordMenuItem" />
                    <xsl:call-template name="historyMenuItem" />
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
                    <xsl:value-of select='resource:getString($constants,"name")' />:
                  </text>
                  <textbox key="{meta:getName($invItem)}" width="150" case="LOWER" max="20" tab="{meta:getDescription($invItem)},{meta:getIsNoInventory($invItem)}" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"id")' />:
                  </text>
                  <textbox key="{meta:getId($invItem)}" width="75" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"description")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getDescription($invItem)}" width="340" max="60" tab="{meta:getStoreId($invItem)},{meta:getName($invItem)}" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"store")' />:
                  </text>
                  <widget colspan="3">
                    <dropdown key="{meta:getStoreId($invItem)}" width="225" tab="{meta:getCategoryId($invItem)},{meta:getDescription($invItem)}" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"category")' />:
                  </text>
                  <widget colspan="3">
                    <dropdown key="{meta:getCategoryId($invItem)}" width="180" tab="{meta:getQuantityMinLevel($invItem)},{meta:getStoreId($invItem)}" />
                  </widget>
                </row>
                <row>
                  <AbsolutePanel style="VerticalSpacer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"minOrderLevel")' />:
                  </text>
                  <textbox key="{meta:getQuantityMinLevel($invItem)}" width="55" tab="{meta:getQuantityToReorder($invItem)},{meta:getCategoryId($invItem)}" />
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"reorderLevel")' />:
                  </text>
                  <textbox key="{meta:getQuantityToReorder($invItem)}" width="55" tab="{meta:getQuantityMaxLevel($invItem)},{meta:getQuantityMinLevel($invItem)}" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"maxOrderLevel")' />:
                  </text>
                  <widget colspan="3">
                    <textbox key="{meta:getQuantityMaxLevel($invItem)}" width="55" tab="{meta:getDispensedUnitsId($invItem)},{meta:getQuantityToReorder($invItem)}" />
                  </widget>
                </row>
                <row>
                  <AbsolutePanel style="VerticalSpacer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"dispensedUnits")' />:
                  </text>
                  <dropdown key="{meta:getDispensedUnitsId($invItem)}" width="90" tab="{meta:getIsActive($invItem)},{meta:getQuantityMaxLevel($invItem)}" />
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
                    <check key="{meta:getIsActive($invItem)}" tab="{meta:getIsReorderAuto($invItem)},{meta:getDispensedUnitsId($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"autoReorder")' />:
                    </text>
                    <check key="{meta:getIsReorderAuto($invItem)}" tab="{meta:getIsLotMaintained($invItem)},{meta:getIsActive($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"maintainLot")' />:
                    </text>
                    <check key="{meta:getIsLotMaintained($invItem)}" tab="{meta:getIsSerialMaintained($invItem)},{meta:getIsReorderAuto($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"serialRequired")' />:
                    </text>
                    <check key="{meta:getIsSerialMaintained($invItem)}" tab="{meta:getIsBulk($invItem)},{meta:getIsLotMaintained($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"bulk")' />:
                    </text>
                    <check key="{meta:getIsBulk($invItem)}" tab="{meta:getIsNotForSale($invItem)},{meta:getIsSerialMaintained($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"notForSale")' />:
                    </text>
                    <check key="{meta:getIsNotForSale($invItem)}" tab="{meta:getIsSubAssembly($invItem)},{meta:getIsBulk($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"subAssembly")' />:
                    </text>
                    <check key="{meta:getIsSubAssembly($invItem)}" tab="{meta:getIsLabor($invItem)},{meta:getIsNotForSale($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"labor")' />:
                    </text>
                    <check key="{meta:getIsLabor($invItem)}" tab="{meta:getIsNoInventory($invItem)},{meta:getIsSubAssembly($invItem)}" />
                  </row>
                  <row>
                    <text style="CondensedPrompt">
                      <xsl:value-of select='resource:getString($constants,"doNotInventory")' />:
                    </text>
                    <check key="{meta:getIsNoInventory($invItem)}" tab="{meta:getName($invItem)},{meta:getIsLabor($invItem)}" />
                  </row>
                </TablePanel>
              </VerticalPanel>
            </HorizontalPanel>
            <VerticalPanel>

<!-- TAB PANEL -->

              <TabPanel key="itemTabPanel" height="200">

<!-- TAB 1  -->

                <tab key="componentsTab" text="{resource:getString($constants,'components')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="componentsTable" width="587" maxRows="9" showScroll="ALWAYS" tab="{meta:getIsNoInventory($invItem)},{meta:getIsLabor($invItem)}">
                      <col key="{componentMeta:getComponentId($component)}" width="129" header="{resource:getString($constants,'component')}">
                        <autoComplete key="{componentMeta:getComponentId($component)}" width="100" case="LOWER" popWidth="auto" field="Integer" required="true">
                          <col width="100" />
                        </autoComplete>
                      </col>
                      <col key="{invItemMeta:getDescription($compInvItem)}" width="335" header="{resource:getString($constants,'description')}">
                        <label />
                      </col>
                      <col key="{componentMeta:getQuantity($component)}" width="104" header="{resource:getString($constants,'quantity')}">
                        <textbox max="10" field="Double" required="true" />
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

                <tab key="locationsTab" text="{resource:getString($constants,'locationQuantity')}">
                  <VerticalPanel padding="0" spacing="0">
                    <table key="locQuantitiesTable" width="587" maxRows="10" showScroll="ALWAYS">
                      <col key="{locationMeta:getStorageLocationId($location)}" width="166" header="{resource:getString($constants,'location')}">
                        <label />
                      </col>
                      <col key="{locationMeta:getLotNumber($location)}" width="70" header="{resource:getString($constants,'lotNum')}">
                        <label />
                      </col>
                      <col key="{locationMeta:getId($location)}" width="70" header="{resource:getString($constants,'locationNum')}">
                        <label />
                      </col>
                      <col key="{locationMeta:getExpirationDate($location)}" width="133" header="{resource:getString($constants,'expirationDate')}">
                        <label />
                      </col>
                      <col key="{locationMeta:getQuantityOnhand($location)}" width="123" header="{resource:getString($constants,'quantityOnHand')}">
                        <label />
                      </col>
                    </table>
                  </VerticalPanel>
                </tab>

<!-- start TAB 3 -->

                <tab key="additionalInfoTab" text="{resource:getString($constants,'additionalInfo')}">
                  <TablePanel padding="0" spacing="0" style="Form">
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"productURI")' />:
                      </text>
                      <widget colspan="5">
                        <textbox key="{meta:getProductUri($invItem)}" width="490" max="80" tab="{invItemMeta:getName($parentInvItem)},{meta:getParentRatio($invItem)}" />
                      </widget>
                    </row>
                    <row>
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageLeadTime")' />:
                      </text>
                      <textbox key="{meta:getAverageLeadTime($invItem)}" width="55" max="30" style="ScreenTextboxDisplayOnly" />
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageCost")' />:
                      </text>
                      <textbox key="{meta:getAverageCost($invItem)}" width="55" max="30" style="ScreenTextboxDisplayOnly" />
                      <text style="Prompt">
                        <xsl:value-of select='resource:getString($constants,"averageDailyUse")' />:
                      </text>
                      <textbox key="{meta:getAverageDailyUse($invItem)}" width="55" max="30" style="ScreenTextboxDisplayOnly" />
                    </row>
                    <row>
                      <text style="Prompt">Parent Item:</text>
                      <widget colspan="3">
                        <autoComplete key="{invItemMeta:getName($parentInvItem)}" width="210">
                          <col width="135" header="{resource:getString($constants,'name')}" />
                          <col width="130" header="{resource:getString($constants,'store')}" />
                        </autoComplete>
                      </widget>
                      <text style="Prompt">Parent Ratio:</text>
                      <textbox key="{meta:getParentRatio($invItem)}" width="55" max="30" tab="{meta:getProductUri($invItem)},{invItemMeta:getName($parentInvItem)}" />
                    </row>
                  </TablePanel>
                </tab>

<!-- start TAB 4 -->

                <tab key="manufacturingTab" text="{resource:getString($constants,'manufacturing')}">
                  <TablePanel key="manFormPanel" padding="0" spacing="0" style="Form">
                    <row>
                      <html key="manufacturingText" width="625" height="200" />
                    </row>
                    <row>
                      <appButton key="editManufacturingButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="StandardNoteButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'edit')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </row>
                  </TablePanel>
                </tab>

<!-- start TAB 5 -->

                <tab key="notesTab" text="{resource:getString($constants,'note')}">
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
              </TabPanel>
            </VerticalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
