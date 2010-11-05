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
  xmlns:meta="xalan://org.openelis.meta.ShippingMeta">

  <xsl:import href="IMPORT/aToZTwoColumns.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" name="{resource:getString($constants,'shipping')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

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
                  <menuItem key="shippingHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'shippingHistory')}" />
                  <menuItem key="itemHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'shippingItemHistory')}" />
                  <menuItem key="trackingHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'shippingTrackingHistory')}" />
              </menu>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'id')" />:
              </text>
              <textbox key="{meta:getId()}" width="50" tab="{meta:getCost()},{meta:getShippedDate()}" field="Integer"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedDate')" />:
              </text>
              <widget colspan="5">
                <calendar key="{meta:getShippedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getNumberOfPackages()},{meta:getStatusId()}" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="90" tab="{meta:getShippedDate()},{meta:getShippedMethodId()}" field="Integer" required = "true"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'cost')" />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getCost()}" width="60" tab="{meta:getShippedFromId()},{meta:getNumberOfPackages()}" field="Double" />
              </widget>              
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'numPackages')" />:
              </text>
              <textbox key="{meta:getNumberOfPackages()}" width="60" tab="{meta:getCost()},{meta:getShippedDate()}" field="Integer" required = "true"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedTo')" />:
              </text>
              <widget colspan="5">
                <autoComplete key="{meta:getShippedToName()}" width="199" case="UPPER" tab="{meta:getProcessedDate()},{meta:getShippedFromId()}" required = "true">
                  <col width="180" header="{resource:getString($constants,'name')}" />
                  <col width="110" header="{resource:getString($constants,'street')}" />
                  <col width="100" header="{resource:getString($constants,'city')}" />
                  <col width="20" header="{resource:getString($constants,'st')}" />
                </autoComplete>
              </widget>              
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedFrom')" />:
              </text>
              <dropdown key="{meta:getShippedFromId()}" width="172" tab="{meta:getShippedToName()},{meta:getCost()}" field="Integer" required = "true"/>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressMultipleUnit()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>              
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'processedDate')" />:
              </text>
              <calendar key="{meta:getProcessedDate()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getProcessedBy()},{meta:getShippedToName()}" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"address")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressStreetAddress()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>              
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'processedBy')" />:
              </text>
              <textbox key="{meta:getProcessedBy()}" width="203" tab="{meta:getShippedMethodId()},{meta:getProcessedDate()}" field="String" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"city")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressCity()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>              
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedMethod')" />:
              </text>
              <dropdown key="{meta:getShippedMethodId()}" width="140" tab="{meta:getStatusId()},{meta:getProcessedBy()}" field="Integer" />
              <!-- <widget colspan="2">
                <HorizontalPanel />
              </widget> -->
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"state")' />:
              </text>
              <widget>
                <textbox key="{meta:getShippedToAddressState()}" width="35" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
              <widget>
                <HorizontalPanel width="21" />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"zipcode")' />:
              </text>
              <widget>
                <textbox key="{meta:getShippedToAddressZipCode()}" width="72" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>              
            </row>
          </TablePanel>
          <TabPanel key="tabPanel" width="635" height="233">
            <tab key="itemTab" text="{resource:getString($constants,'items')}">
              <HorizontalPanel>
                <VerticalPanel>
                  <widget>
                    <table key="itemTable" rows="8" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
                      <col width="65" key="{meta:getItemQuantity()}" header="{resource:getString($constants,'qty')}">
                        <textbox field="Integer" required = "true"/>
                      </col>
                      <col width="325" key="{meta:getItemDescription()}" header="{resource:getString($constants,'item')}">
                        <textbox field="String" required = "true" />
                      </col>
                    </table>
                  </widget>
                  <HorizontalPanel>
                    <button key="addItemButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                    <button key="removeItemButton" icon="RemoveRowButtonImage" text="resource:getString($constants,'removeRow')}" style="Button"/>
                    <button key="lookupItemButton" text="{resource:getString($constants,'lookupItem')}" style="Button"/>
                  </HorizontalPanel>
                </VerticalPanel>
                <HorizontalPanel width="16" style="Divider" />
                <VerticalPanel>
                  <widget>
                    <table key="trackingTable" rows="8" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
                      <col key="{meta:getTrackingTrackingNumber()}" width="180" header="{resource:getString($constants,'trackingNums')}">
                        <textbox max="30" field="String" required = "true" />
                      </col>
                    </table>
                  </widget>
                  <HorizontalPanel>
                    <button key="addTrackingButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                    <button key="removeTrackingButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                  </HorizontalPanel>
                </VerticalPanel>
              </HorizontalPanel>
            </tab>
            <tab key="noteTab" text="{resource:getString($constants,'shippingNotes')}">
              <VerticalPanel padding="0" spacing="0">
                <notes key="notesPanel" width="635" height="205" />
                <button key="standardNoteButton" icon="StandardNoteButtonImage" text="{resource:getString($constants,'addNote')}" style="Button"/>
              </VerticalPanel>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
