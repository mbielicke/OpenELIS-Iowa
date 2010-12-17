
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
    <screen name="{resource:getString($constants,'provider')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="18">
                <col width="88" header="{resource:getString($constants,'lastName')}">
                  <label field="String" />
                </col>
                <col width="87" header="{resource:getString($constants,'firstName')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="PreviousButtonImage" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="NextButtonImage" />
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
                      <Grid cols="2">
                        <row>
                          <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                  <menuItem key="providerHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'providerHistory')}" />
                  <menuItem key="providerLocationHistory" enabled="false" icon="historyIcon" display="{resource:getString($constants,'providerLocationHistory')}" />
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
            <TabPanel key="tabPanel" width="609" height="290">
<!-- TAB 1 -->
              <tab key="locationTab" text="{resource:getString($constants,'locations')}">
                <VerticalPanel width="605">
                  <widget valign="top">
                    <table key="locationTable" width="605" rows="10" vscroll="ALWAYS" hscroll="ALWAYS">
                      <col key="{meta:getProviderLocationLocation()}" width="115" header="{resource:getString($constants,'location')}">
                        <textbox max="50" field="String" required="true" />
                      </col>
                      <col key="{meta:getProviderLocationExternalId()}" width="130" header="{resource:getString($constants,'externalId')}">
                        <textbox max="10" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressMultipleUnit()}" width="130" header="{resource:getString($constants,'aptSuite')}">
                        <textbox max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressStreetAddress()}" width="130" header="{resource:getString($constants,'address')}">
                        <textbox max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCity()}" width="130" header="{resource:getString($constants,'city')}">
                        <textbox max="30" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressState()}" width="60" header="{resource:getString($constants,'state')}">
                        <dropdown width="60" case="UPPER" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressZipCode()}" width="100" header="{resource:getString($constants,'zipcode')}">
                        <textbox max="10" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCountry()}" width="130" header="{resource:getString($constants,'country')}">
                        <dropdown width="130" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressWorkPhone()}" width="90" header="{resource:getString($constants,'workNumber')}">
                        <textbox max="21" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressHomePhone()}" width="90" header="{resource:getString($constants,'homeNumber')}">
                        <textbox max="16" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressCellPhone()}" width="90" header="{resource:getString($constants,'cellNumber')}">
                        <textbox max="16" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressFaxPhone()}" width="150" header="{resource:getString($constants,'faxNumber')}">
                        <textbox max="16" field="String" />
                      </col>
                      <col key="{meta:getProviderLocationAddressEmail()}" width="145" header="{resource:getString($constants,'email')}">
                        <textbox max="80" field="String" />
                      </col>
                    </table>
                  </widget>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="addLocationButton" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}" style="Button"/>
                      <button key="removeLocationButton" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}" style="Button"/>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
<!-- TAB 2 -->
              <tab key="notesTab" text="{resource:getString($constants,'note')}">
              	<xsl:call-template name="InternalNoteTab">
          			<xsl:with-param name="width">607</xsl:with-param>
          			<xsl:with-param name="height">237</xsl:with-param>
        		</xsl:call-template>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
