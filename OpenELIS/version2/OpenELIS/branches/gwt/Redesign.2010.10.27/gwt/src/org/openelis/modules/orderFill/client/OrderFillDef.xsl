

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
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'fillOrder')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton"/>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="updateButton"/>
            <xsl:call-template name="processButton"/>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton"/>
            <xsl:call-template name="abortButton"/>
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
                <menuItem key="shippingInfo" enabled="false" icon="shippingIcon" display="{resource:getString($constants,'shippingInfo')}" />
            </menu>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

        <VerticalPanel padding="0" spacing="0">
          <widget valign="top">
            <table key="orderTable" width="870" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
              <col key="process" width="20" header="">
                <check />
              </col>
              <col key="{meta:getId()}" width="45" sort="true" header="{resource:getString($constants,'ordNum')}">
                <textbox field="Integer" />
              </col>
              <col key="{meta:getStatusId()}" width="80" header="{resource:getString($constants,'status')}">
                <dropdown width="80" field="Integer" />
              </col>
              <col key="{meta:getOrderedDate()}" width="70" sort="true" header="{resource:getString($constants,'orderDate')}">
                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
              </col>
              <col key="{meta:getShipFromId()}" width="65" header="{resource:getString($constants,'shipFrom')}">
                <dropdown width="100" field="Integer" />
              </col>
              <col key="{meta:getRequestedBy()}" width="85" header="{resource:getString($constants,'requestedBy')}" filter="true">
                <textbox width="120" field="String" />
              </col>
              <col key="{meta:getOrganizationName()}" width="160" header="{resource:getString($constants,'shipTo')}" filter="true">
                <textbox width="130" case="UPPER" field="String" />
              </col>
              <col key="{meta:getDescription()}" width="155" header="{resource:getString($constants,'description')}" filter="true">
                <textbox field="String" />
              </col>
              <col key="{meta:getNeededInDays()}" width="45" sort="true" header="{resource:getString($constants,'neededNumDays')}">
                <textbox field="Integer" />
              </col>
              <col key="daysLeft" width="60" sort="true" header="{resource:getString($constants,'numDaysLeft')}">
                <textbox field="Integer" />
              </col>
              <col key="{meta:getType()}" width="75" header="{resource:getString($constants,'type')}" filter="true">
                <dropdown width="75" field="String" />
              </col>
            </table>
          </widget>
          <VerticalPanel style="WhiteContentPanel">
            <TabPanel key="tabPanel" width="880" height="200">
              <tab key="itemTab" text="{resource:getString($constants,'items')}">
                <HorizontalPanel>
                  <VerticalPanel style="subform">
                    <text style="FormTitle">
                      <xsl:value-of select='resource:getString($constants,"shipToAddress")' />
                    </text>
                    <TablePanel style="Form">
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"costCenter")' />:
                        </text>
                        <widget colspan="5">
                          <dropdown key="{meta:getCostCenterId()}" width="186" field="Integer" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"attention")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getOrganizationAttention()}" width="186" max="30" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getOrganizationAddressMultipleUnit()}" width="186" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"address")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getOrganizationAddressStreetAddress()}" width="186" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"city")' />:
                        </text>
                        <widget colspan="5">
                          <textbox key="{meta:getOrganizationAddressCity()}" width="186" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                      <row>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"state")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getOrganizationAddressState()}" width="33" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                        <widget>
                          <HorizontalPanel width="11" />
                        </widget>
                        <text style="Prompt">
                          <xsl:value-of select='resource:getString($constants,"zipcode")' />:
                        </text>
                        <widget>
                          <textbox key="{meta:getOrganizationAddressZipCode()}" width="72" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                  <VerticalPanel style="subform">
                    <text style="FormTitle">
                      <xsl:value-of select='resource:getString($constants,"itemsOrdered")' />
                    </text>
                    <widget>
                      <tree key="itemsTree" rows="6" vscroll="ALWAYS" hscroll="ALWAYS">
                        <columns>
                          <col width="50" header="{resource:getString($constants,'ordNum')}" />
                          <col width="30" header="{resource:getString($constants,'qty')}" />
                          <col width="170" header="{resource:getString($constants,'item')}" />
                          <col width="160" header="{resource:getString($constants,'location')}" />
                          <col width="60" header="{resource:getString($constants,'lotNum')}" />
                          <col width="70" header="{resource:getString($constants,'expDate')}" />
                        </columns>
                        <node key="top">
                          <col>
                            <label field="Integer" required="true" />
                          </col>
                          <col>
                            <textbox field="Integer" required="true" />
                          </col>
                          <col>
                            <autoComplete width="137" case="LOWER" field="Integer" required="true">
                              <col width="137" />
                            </autoComplete>
                          </col>
                        </node>
                        <node key="orderItem">
                          <col>
                            <label field="Integer" />
                          </col>
                          <col>
                            <textbox field="Integer" required="true" />
                          </col>
                          <col>
                            <autoComplete width="130" case="LOWER" field="Integer">
                              <col width="118" />
                            </autoComplete>
                          </col>
                          <col>
                            <autoComplete width="125" case="LOWER" field="Integer" required="true">
                              <col width="300" header="{resource:getString($constants,'description')}" />
                              <col width="65" header="{resource:getString($constants,'lotNum')}" />
                              <col width="55" header="{resource:getString($constants,'qty')}" />
                              <col width="65" header="{resource:getString($constants,'expDate')}">
                                <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                              </col>
                            </autoComplete>
                          </col>
                          <col>
                            <label field="String" />
                          </col>
                          <col>
                            <calendar begin="0" end="2" pattern="{resource:getString($constants,'datePattern')}" />
                          </col>
                        </node>
                      </tree>
                    </widget>
                    <HorizontalPanel>
                      <button key="removeItemButton" icon="RemoveRowButtonImage" text="resource:getString($constants,'removeRow')" style="Button"/>
                      <button key="addItemButton" icon="AddRowButtonImage" text="resource:getString($constants,'addLocation')" style="Button"/>
                    </HorizontalPanel>
                  </VerticalPanel>
                </HorizontalPanel>
              </tab>
              <tab key="noteTab" tab="notesPanel, notesPanel" text="{resource:getString($constants,'orderShippingNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="880" height="170" />
                  <button key="standardNoteButton" icon="StandardNoteButtonImage" text="resource:getString($constants,'addNote')" style="Button"/>
                </VerticalPanel>
              </tab>
              <tab key="customerNote" tab="customerNotesPanel, customerNotesPanel" text="{resource:getString($constants,'customerNotes')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="customerNotesPanel" width="880" height="170" />
                  <button key="editNoteButton" icon="StandardNoteButtonImage" text="resource:getString($constants,'editNote')" style="Button"/>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
