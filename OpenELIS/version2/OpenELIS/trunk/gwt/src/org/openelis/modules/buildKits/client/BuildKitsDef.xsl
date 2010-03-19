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
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="BuildKits" name="{resource:getString($constants,'buildKits')}">
      <VerticalPanel padding="0" spacing="0">
<!--button panel code-->
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="addButton">
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
                <xsl:value-of select='resource:getString($constants,"kit")' />:
              </text>
              <autoComplete key="{meta:getName()}" width="190" case="LOWER" tab="numRequested,{meta:getLocationExpirationDate()}" field="Integer" required="true">
                <col width="135" header="{resource:getString($constants,'name')}" />
                <col width="130" header="{resource:getString($constants,'store')}" />
                <col width="110" header="{resource:getString($constants,'dispensedUnits')}" />
              </autoComplete>
              <widget colspan="2">
                <HorizontalPanel>
                  <check key="addToExisting" tab="{meta:getLocationStorageLocationName()},numRequested"></check>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"addToExisting")' />
                  </text>
                </HorizontalPanel>
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"numRequested")' />:
              </text>
              <textbox key="numRequested" width="50" max="20" tab="addToExisting,{meta:getName()}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"location")' />:
              </text>
              <autoComplete key="{meta:getLocationStorageLocationName()}" width="160" case="LOWER" tab="{meta:getLocationLotNumber()},addToExisting" field="Integer" required="true">
                <col width="300" header="{resource:getString($constants,'description')}" />
              </autoComplete>
            </row>
            <row>
              <widget colspan="2">
                <HorizontalPanel />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"lotNum")' />:
              </text>
              <textbox key="{meta:getLocationLotNumber()}" width="100" max="30" tab="{meta:getLocationExpirationDate()},{meta:getLocationStorageLocationName()}" field="String" />
            </row>
            <row>
              <widget colspan="2">
                <HorizontalPanel />
              </widget>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"expDate")' />:
              </text>
              <calendar key="{meta:getLocationExpirationDate()}" begin="0" end="2" tab="{meta:getName()},{meta:getLocationLotNumber()}" />
            </row>
          </TablePanel>
          <TablePanel>
            <row>
              <widget colspan="4">
                <table key="subItemsTable" width="auto" maxRows="10" showScroll="ALWAYS" style="ScreenTableWithSides" tab="{meta:getName()},{meta:getIsNotInventoried()}" title="">
                  <col key="{meta:getComponentComponentId()}" width="160" header="{resource:getString($constants,'kitComponentName')}">
                    <label field="Integer" required="true" />
                  </col>
                  <col key="{meta:getLocationStorageLocationName()}" width="177" header="{resource:getString($constants,'location')}">
                    <autoComplete width="137" case="LOWER" field="Integer" required="true">
                      <col width="300" header="{resource:getString($constants,'description')}" />
                      <col width="65" header="{resource:getString($constants,'lotNum')}" />
                      <col width="30" header="{resource:getString($constants,'qty')}" />
                    </autoComplete>
                  </col>
                  <col key="{meta:getLocationLotNumber()}" width="80" header="{resource:getString($constants,'lotNum')}">
                    <label field="String" />
                  </col>
                  <col key="{meta:getComponentQuantity()}" width="60" header="{resource:getString($constants,'unit')}">
                    <label field="String" />
                  </col>
                  <col key="total" width="60" header="{resource:getString($constants,'total')}">
                    <textbox max="10" field="Double" required="true" />
                  </col>
                  <col key="{meta:getLocationQuantityOnhand()}" width="60" header="{resource:getString($constants,'onHand')}">
                    <label field="Integer" required="true" />
                  </col>
                </table>
              </widget>
            </row>
            <row>
              <widget colspan="4">
                <appButton key="transferButton" style="Button" action="transfer">
                  <HorizontalPanel>
                    <AbsolutePanel style="RemoveRowButtonImage" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"transfer")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
