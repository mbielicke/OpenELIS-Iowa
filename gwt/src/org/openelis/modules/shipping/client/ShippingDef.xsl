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
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="Shipping" name="{resource:getString($constants,'shipping')}">
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
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="90" popWidth="auto" tab="{meta:getShippedDate()},{meta:getShippedMethodId()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedDate')" />:
              </text>
              <widget colspan="5">
                <calendar key="{meta:getShippedDate()}" begin="0" end="2" width="80" tab="{meta:getNumberOfPackages()},{meta:getStatusId()}" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'numPackages')" />:
              </text>
              <textbox key="{meta:getNumberOfPackages()}" width="60" tab="{meta:getCost()},{meta:getShippedDate()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'cost')" />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getCost()}" width="60" tab="{meta:getShippedFromId()},{meta:getNumberOfPackages()}" field="Double" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedFrom')" />:
              </text>
              <dropdown key="{meta:getShippedFromId()}" width="172" popWidth="auto" tab="{meta:getShippedToName()},{meta:getCost()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedTo')" />:
              </text>
              <widget colspan="5">
                <autoComplete key="{meta:getShippedToName()}" width="199" case="UPPER" tab="{meta:getProcessedDate()},{meta:getShippedFromId()}" field="Integer">
                  <col width="180" header="Name" />
                  <col width="110" header="Street" />
                  <col width="100" header="City" />
                  <col width="20" header="St" />
                </autoComplete>
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'processedDate')" />:
              </text>
              <calendar key="{meta:getProcessedDate()}" begin="0" end="2" width="80" tab="{meta:getProcessedById()},{meta:getShippedToName()}" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"aptSuite")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressMultipleUnit()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'processedBy')" />:
              </text>
              <textbox key="{meta:getProcessedById()}" width="203" tab="{meta:getShippedMethodId()},{meta:getProcessedDate()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"address")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressStreetAddress()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'shippedMethod')" />:
              </text>
              <dropdown key="{meta:getShippedMethodId()}" width="140" popWidth="auto" tab="{meta:getStatusId()},{meta:getProcessedById()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"city")' />:
              </text>
              <widget colspan="5">
                <textbox key="{meta:getShippedToAddressCity()}" width="199" case="UPPER" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
            </row>
            <row>
              <widget colspan="2">
                <HorizontalPanel />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"state")' />:
              </text>
              <widget>
                <textbox key="{meta:getShippedToAddressState()}" width="35" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
              <widget>
              	<HorizontalPanel width="28" />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"zipcode")' />:
              </text>
              <widget>
                <textbox key="{meta:getShippedToAddressZipCode()}" width="65" max="30" style="ScreenTextboxDisplayOnly" field="String" />
              </widget>
            </row>
          </TablePanel>
          <TabPanel key="shippingTabPanel" width="625" height="220">
            <tab key="itemsTab" text="{resource:getString($constants,'items')}">
              <HorizontalPanel width="625" height="200">
                <widget>
                  <table key="itemsTable" width="auto" maxRows="8" showScroll="ALWAYS" title="">
                    <col width="65" header="{resource:getString($constants,'qty')}">
                      <label field="Integer" />
                    </col>
                    <col width="325" header="{resource:getString($constants,'item')}">
                      <label field="String" />
                    </col>
                  </table>
                </widget>
                <HorizontalPanel width="9" />
                <VerticalPanel>
                  <widget>
                    <table key="trackingNumbersTable" width="auto" maxRows="7" showScroll="ALWAYS" title="">
                      <col key="{meta:getTrackingTrackingNumber()}" width="180" header="{resource:getString($constants,'trackingNums')}">
                        <textbox max="30" field="String" />
                      </col>
                    </table>
                  </widget>
                  <widget halign="center" style="WhiteContentPanel">
                    <appButton key="removeRowButton" style="Button" action="removeRow">
                      <HorizontalPanel>
                        <AbsolutePanel style="RemoveRowButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'removeRow')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </widget>
                </VerticalPanel>
              </HorizontalPanel>
            </tab>
            <tab key="orderNotesTab" text="{resource:getString($constants,'orderShippingNotes')}">
              <VerticalPanel width="625" height="200" padding="0" spacing="0">
                <TablePanel key="noteFormPanel" padding="0" spacing="0" style="Form">
                  <row>
                    <HorizontalPanel height="15" />
                  </row>
                  <row>
                    <HorizontalPanel width="14" />
                    <widget>
                      <textarea width="576" height="155" /><!-- key="{noteMeta:getText()}" -->
                    </widget>
                  </row>
                </TablePanel>
              </VerticalPanel>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
