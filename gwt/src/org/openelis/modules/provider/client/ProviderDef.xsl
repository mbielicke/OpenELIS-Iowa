

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
  xmlns:addr="xalan://org.openelis.meta.AddressMeta"
  xmlns:location="xalan://org.openelis.metamap.ProviderAddressMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.ProviderMetaMap">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <xsl:variable name="pro" select="meta:new()" />
    <xsl:variable name="loc" select="meta:getProviderAddress($pro)" />
    <xsl:variable name="locAddr" select="location:getAddress($loc)" />

<!-- main screen -->

    <screen id="Provider" name="{resource:getString($constants,'provider')}">
      <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="18" style="atozTable">
                <col width="88" header="{resource:getString($constants,'lastName')}">
                  <label />
                </col>
                <col width="87" header="{resource:getString($constants,'firstName')}">
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
            </HorizontalPanel>
          </AbsolutePanel>

<!--end button panel-->

          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <TablePanel style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'id')" />:
                </text>
                <textbox key="{meta:getId($pro)}" width="50" tab="{meta:getLastName($pro)},{meta:getNpi($pro)}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'lastName')" />:
                </text>
                <textbox key="{meta:getLastName($pro)}" width="215" case="UPPER" max="30" tab="{meta:getFirstName($pro)},{meta:getId($pro)}" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'type')" />:
                </text>
                <dropdown key="{meta:getTypeId($pro)}" width="80" tab="{meta:getNpi($pro)},{meta:getMiddleName($pro)}" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'firstName')" />:
                </text>
                <textbox key="{meta:getFirstName($pro)}" width="145" case="UPPER" max="20" tab="{meta:getMiddleName($pro)},{meta:getLastName($pro)}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'npi')" />:
                </text>
                <textbox key="{meta:getNpi($pro)}" width="145" max="20" tab="{meta:getId($pro)},{meta:getTypeId($pro)}" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'middleName')" />:
                </text>
                <textbox key="{meta:getMiddleName($pro)}" width="145" case="UPPER" max="20" tab="{meta:getTypeId($pro)},{meta:getFirstName($pro)}" />
              </row>
            </TablePanel>

<!-- TAB PANEL -->

            <TabPanel key="provTabPanel" width="605">

<!-- TAB 1 -->

              <tab key="addressesTab" text="{resource:getString($constants,'locations')}">
                <VerticalPanel width="605">
                  <widget valign="top">
                    <table key="providerAddressTable" width="587" maxRows="10" showScroll="ALWAYS">
                      <col key="{location:getLocation($loc)}" width="115" header="{resource:getString($constants,'location')}">
                        <textbox max="50" required="true" />
                      </col>
                      <col key="{location:getExternalId($loc)}" width="130" header="{resource:getString($constants,'externalId')}">
                        <textbox max="10" />
                      </col>
                      <col key="{addr:getMultipleUnit($locAddr)}" width="130" header="{resource:getString($constants,'aptSuite')}">
                        <textbox max="30" />
                      </col>
                      <col key="{addr:getStreetAddress($locAddr)}" width="130" header="{resource:getString($constants,'address')}">
                        <textbox max="30" />
                      </col>
                      <col key="{addr:getCity($locAddr)}" width="130" header="{resource:getString($constants,'city')}">
                        <textbox max="30" />
                      </col>
                      <col key="{addr:getState($locAddr)}" width="60" header="{resource:getString($constants,'state')}">
                        <dropdown width="60" case="UPPER" field="String" />
                      </col>
                      <col key="{addr:getZipCode($locAddr)}" width="100" header="{resource:getString($constants,'zipcode')}">
                        <textbox max="10" />
                      </col>
                      <col key="{addr:getCountry($locAddr)}" width="130" header="{resource:getString($constants,'country')}">
                        <dropdown width="130" field="String" />
                      </col>
                      <col key="{addr:getWorkPhone($locAddr)}" width="90" header="{resource:getString($constants,'workNumber')}">
                        <textbox max="21" />
                      </col>
                      <col key="{addr:getHomePhone($locAddr)}" width="90" header="{resource:getString($constants,'homeNumber')}">
                        <textbox max="16" />
                      </col>
                      <col key="{addr:getCellPhone($locAddr)}" width="90" header="{resource:getString($constants,'cellNumber')}">
                        <textbox max="16" />
                      </col>
                      <col key="{addr:getFaxPhone($locAddr)}" width="150" header="{resource:getString($constants,'faxNumber')}">
                        <textbox max="16" />
                      </col>
                      <col key="{addr:getEmail($locAddr)}" width="145" header="{resource:getString($constants,'email')}">
                        <textbox max="80" />
                      </col>
                    </table>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addAddressButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <widget>
                            <text>
                              <xsl:value-of select="resource:getString($constants,'addRow')" />
                            </text>
                          </widget>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeAddressButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage" />
                          <widget>
                            <text>
                              <xsl:value-of select="resource:getString($constants,'removeRow')" />
                            </text>
                          </widget>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>

<!-- TAB 2 -->

              <tab key="notesTab" text="{resource:getString($constants,'note')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes key="notesPanel" width="605" height="247" />
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
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
