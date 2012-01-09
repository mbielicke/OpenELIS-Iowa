
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
  xmlns:meta="xalan://org.openelis.meta.ProviderMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/note/client/InternalNoteTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
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
                  <label field="String" />
                </col>
                <col width="87" header="{resource:getString($constants,'firstName')}">
                  <label field="String" />
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
                    <menuItem key="providerHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'providerHistory')}" />
                    <menuItem key="providerLocationHistory" description="" enable="false" icon="historyIcon" label="{resource:getString($constants,'providerLocationHistory')}" />
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
                  <xsl:value-of select="resource:getString($constants,'id')" />:
                </text>
                <textbox key="{meta:getId()}" width="50" tab="{meta:getLastName()},{meta:getNpi()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'lastName')" />:
                </text>
                <textbox key="{meta:getLastName()}" width="215" case="UPPER" max="30" tab="{meta:getFirstName()},{meta:getId()}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'type')" />:
                </text>
                <dropdown key="{meta:getTypeId()}" width="80" tab="{meta:getNpi()},{meta:getMiddleName()}" field="Integer" required="true" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'firstName')" />:
                </text>
                <textbox key="{meta:getFirstName()}" width="145" case="UPPER" max="20" tab="{meta:getMiddleName()},{meta:getLastName()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'npi')" />:
                </text>
                <textbox key="{meta:getNpi()}" width="145" max="20" tab="{meta:getId()},{meta:getTypeId()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'middleName')" />:
                </text>
                <textbox key="{meta:getMiddleName()}" width="145" case="UPPER" max="20" tab="{meta:getTypeId()},{meta:getFirstName()}" field="String" />
              </row>
            </TablePanel>
<!-- TAB PANEL -->
            <TabPanel key="tabPanel" width="605">
<!-- TAB 1 -->
              <tab key="locationTab" text="{resource:getString($constants,'locations')}">
                <VerticalPanel width="605">
                  <widget valign="top">
                    <table key="locationTable" width="587" maxRows="10" showScroll="ALWAYS">
                      <col key="{meta:getProviderLocationLocation()}" width="115" header="{resource:getString($constants,'location')}">
                        <textbox max="50" field="String" required="true" />
                      </col>
                      <col key="{meta:getProviderLocationExternalId()}" width="130" header="{resource:getString($constants,'externalId')}">
                        <textbox max="10" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressMultipleUnit()}" width="130" header="{resource:getString($constants,'aptSuite')}">
                        <textbox case="UPPER" max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressStreetAddress()}" width="130" header="{resource:getString($constants,'address')}">
                        <textbox case="UPPER" max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCity()}" width="130" header="{resource:getString($constants,'city')}">
                        <textbox case="UPPER" max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressState()}" width="60" header="{resource:getString($constants,'state')}">
                        <dropdown width="60" case="UPPER" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressZipCode()}" width="100" header="{resource:getString($constants,'zipcode')}">
                        <textbox mask="99999-9999" max="10" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCountry()}" width="130" header="{resource:getString($constants,'country')}">
                        <dropdown case="MIXED" width="130" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressWorkPhone()}" width="90" header="{resource:getString($constants,'workNumber')}">
                        <textbox max="21" mask="{resource:getString($constants,'phoneWithExtensionPattern')}" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressHomePhone()}" width="90" header="{resource:getString($constants,'homeNumber')}">
                        <textbox max="16" mask="{resource:getString($constants,'phonePattern')}" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCellPhone()}" width="90" header="{resource:getString($constants,'cellNumber')}">
                        <textbox max="16" mask="{resource:getString($constants,'phonePattern')}" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressFaxPhone()}" width="150" header="{resource:getString($constants,'faxNumber')}">
                        <textbox max="16" mask="{resource:getString($constants,'phonePattern')}" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressEmail()}" width="145" header="{resource:getString($constants,'email')}">
                        <textbox max="80" field="String" />
                      </col>
                    </table>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addLocationButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <widget>
                            <text>
                              <xsl:value-of select="resource:getString($constants,'addRow')" />
                            </text>
                          </widget>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeLocationButton" style="Button">
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
              	<xsl:call-template name="InternalNoteTab">
          			<xsl:with-param name="width">605</xsl:with-param>
          			<xsl:with-param name="height">247</xsl:with-param>
        		</xsl:call-template>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
