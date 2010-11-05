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
  xmlns:meta="xalan://org.openelis.meta.StorageMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'storage')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="19" style="atozTable">
                <col width="175" header="{resource:getString($constants,'storageLocationName')}">
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
              <xsl:call-template name="updateButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel width="620" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"storageLocationName")' />:
                </text>
                <textbox key="{meta:getStorageLocationName()}" width="150" case="LOWER" tab="{meta:getStorageLocationLocation()},{meta:getStorageLocationIsAvailable()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"location")' />:
                </text>
                <textbox key="{meta:getStorageLocationLocation()}" width="395" tab="{meta:getStorageLocationStorageUnitDescription()},{meta:getStorageLocationName()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"storageUnit")' />:
                </text>
                <textbox key="{meta:getStorageLocationStorageUnitDescription()}" tab="{meta:getStorageLocationIsAvailable()},{meta:getStorageLocationLocation()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select='resource:getString($constants,"available")' />:
                </text>
                <check key="{meta:getStorageLocationIsAvailable()}" tab="{meta:getStorageLocationName()},{meta:getStorageLocationStorageUnitDescription()}" />
              </row>
            </TablePanel>
            <TabPanel key="tabPanel" width="630" height="295">
              <tab key="currentTab" text="{resource:getString($constants,'current')}">
                <VerticalPanel>
                  <widget>
                    <tree key="storageCurrentTree" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                      <columns>
                        <col width="200" header="{resource:getString($constants,'storageLocationItem')}" />
                        <col width="100" header="{resource:getString($constants,'user')}" />
                        <col width="150" header="{resource:getString($constants,'checkIn')}" />
                        <col width="150" header="{resource:getString($constants,'checkOut')}" />
                      </columns>
                      <node key="locationName">
                        <col>
                          <label field="String" />
                        </col>
                      </node>
                      <node key="storage">
                        <col>
                          <label field="String" />
                        </col>
                        <col>
                          <label field="String" />
                        </col>
                        <col>
                          <textbox pattern="{resource:getString($constants,'dateTimePattern')}" field="Date" />
                        </col>
                        <col>
                          <textbox pattern="{resource:getString($constants,'dateTimePattern')}" field="Date" />
                        </col>
                      </node>
                    </tree>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="moveItemsButton" icon="MoveStorageButtonImage" text="resource:getString($constants,'move')" style="Button"/>
                      <button key="discardItemsButton" icon="DiscardButtonImage" text="resource:getString($constants,'discard')" style="Button"/>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
              <tab key="historyTab" text="{resource:getString($constants,'history')}">
                <VerticalPanel>
                  <widget>
                    <table key="storageHistoryTable" rows="11" vscroll="ALWAYS" hscroll="ALWAYS" tab="{meta:getStorageLocationName()},{meta:getStorageLocationIsAvailable()}">
                      <col key="{meta:getReferenceId()}" width="100" header="{resource:getString($constants,'storageLocation')}">
                        <textbox field="Integer" />
                      </col>
                      <col key="{meta:getReferenceId()}" width="100" header="{resource:getString($constants,'item')}">
                        <textbox field="Integer" />
                      </col>
                      <col key="{meta:getSystemUserId()}" width="100" header="{resource:getString($constants,'user')}">
                        <textbox field="String" />
                      </col>
                      <col key="{meta:getCheckin()}" width="147" header="{resource:getString($constants,'checkIn')}">
                        <textbox pattern="{resource:getString($constants,'dateTimePattern')}" field="Date" />
                      </col>
                      <col key="{meta:getCheckout()}" width="150" header="{resource:getString($constants,'checkOut')}">
                        <textbox pattern="{resource:getString($constants,'dateTimePattern')}" field="Date" />
                      </col>
                    </table>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="historyPrevButton" style="Button">
                        <AbsolutePanel style="prevNavIndex" />
                      </button>
                      <button key="historyNextButton" style="Button">
                        <AbsolutePanel style="nextNavIndex" />
                      </button>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
