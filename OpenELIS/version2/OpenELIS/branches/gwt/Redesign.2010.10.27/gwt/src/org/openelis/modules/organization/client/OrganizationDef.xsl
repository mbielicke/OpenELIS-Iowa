
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
  xmlns:meta="xalan://org.openelis.meta.OrganizationMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:import href="OPENELIS/org/openelis/modules/note/client/InternalNoteTabDef.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen name="{resource:getString($constants,'organization')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" vscroll="NEVER" hscroll="NEVER" rows="20">
                <col width="175" header="{resource:getString($constants,'name')}">
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
             
                <menu selfShow="true" showBelow="true">
                  <menuDisplay>
                    <button style="ButtonPanelButton">
                      <Grid cols="2">
                        <row>
                          <cell style="ScreenLabel,ButtonAdj" text="{resource:getString($constants,'options')}" />
                          <cell style="OptionsButtonImage" />
                        </row>
                      </Grid>
                    </button>
                  </menuDisplay>
                    <menuItem key="orgHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orgHistory')}" />
                    <menuItem key="orgContactHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orgContactHistory')}" />
                    <menuItem key="orgParameterHistory" description="" enabled="false" icon="historyIcon" display="{resource:getString($constants,'orgParameterHistory')}" />
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
                <textbox key="{meta:getId()}" width="75" tab="{meta:getName()},tabPanel" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <textbox key="{meta:getName()}" width="225" case="UPPER" max="40" tab="{meta:getAddressMultipleUnit()},{meta:getId()}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'city')" />:
                </text>
                <widget colspan="3">
                  <textbox key="{meta:getAddressCity()}" width="212" case="UPPER" max="30" tab="{meta:getAddressState()},{meta:getAddressStreetAddress()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'aptSuite')" />:
                </text>
                <textbox key="{meta:getAddressMultipleUnit()}" width="212" case="UPPER" max="30" tab="{meta:getAddressStreetAddress()},{meta:getName()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'state')" />:
                </text>
                <dropdown key="{meta:getAddressState()}" width="40" case="UPPER" tab="{meta:getAddressZipCode()},{meta:getAddressCity()}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'zipcode')" />:
                </text>
                <textbox key="{meta:getAddressZipCode()}" width="70" mask="99999-9999" tab="{meta:getAddressCountry()},{meta:getAddressState()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'address')" />:
                </text>
                <textbox key="{meta:getAddressStreetAddress()}" width="212" case="UPPER" max="30" tab="{meta:getAddressCity()},{meta:getAddressMultipleUnit()}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'country')" />:
                </text>
                <widget colspan="3">
                  <dropdown key="{meta:getAddressCountry()}" width="175" tab="{meta:getParentOrganizationName()},{meta:getAddressZipCode()}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'parentOrganization')" />:
                </text>
                <autoComplete key="{meta:getParentOrganizationName()}" width="241" case="UPPER" tab="{meta:getIsActive()},{meta:getAddressCountry()}" field="Integer">
                  <col width="180" header="{resource:getString($constants,'name')}" />
                  <col width="110" header="{resource:getString($constants,'street')}" />
                  <col width="100" header="{resource:getString($constants,'city')}" />
                  <col width="20" header="{resource:getString($constants,'st')}" />
                </autoComplete>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'active')" />:
                </text>
                <widget colspan="3">
                  <check key="{meta:getIsActive()}" tab="tabPanel,{meta:getParentOrganizationName()}" />
                </widget>
              </row>
            </TablePanel>
<!-- TAB PANEL -->
            <TabPanel key="tabPanel" width="605" height="290">
<!-- TAB 1 -->
              <tab tab="contactTable,contactTable" text="{resource:getString($constants,'contact')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="contactTable" width="605" rows="10" vscroll="ALWAYS" tab="{meta:getId()},{meta:getIsActive()}">
                    <col key="{meta:getContactContactTypeId()}" width="106" header="{resource:getString($constants,'type')}">
                      <dropdown width="90" field="Integer" required="true" />
                    </col>
                    <col key="{meta:getContactName()}" width="130" header="{resource:getString($constants,'contactName')}">
                      <textbox max="40" field="String" required="true" />
                    </col>
                    <col key="{meta:getContactAddressMultipleUnit()}" width="130" header="{resource:getString($constants,'aptSuite')}">
                      <textbox case="UPPER" max="30" field="String" />
                    </col>
                    <col key="{meta:getContactAddressStreetAddress()}" width="130" header="{resource:getString($constants,'address')}">
                      <textbox case="UPPER" max="40" field="String" />
                    </col>
                    <col key="{meta:getContactAddressCity()}" width="130" sort="true" header="{resource:getString($constants,'city')}">
                      <textbox case="UPPER" max="30" field="String" />
                    </col>
                    <col key="{meta:getContactAddressState()}" width="56" header="{resource:getString($constants,'state')}">
                      <dropdown width="40" case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getContactAddressZipCode()}" width="68" header="{resource:getString($constants,'zipcode')}">
                      <textbox mask="99999-9999" field="String" />
                    </col>
                    <col key="{meta:getContactAddressCountry()}" width="126" header="{resource:getString($constants,'country')}">
                      <dropdown width="110" field="String" />
                    </col>
                    <col key="{meta:getContactAddressWorkPhone()}" width="100" header="{resource:getString($constants,'workNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getContactAddressHomePhone()}" width="90" header="{resource:getString($constants,'homeNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getContactAddressCellPhone()}" width="90" header="{resource:getString($constants,'cellNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getContactAddressFaxPhone()}" width="90" header="{resource:getString($constants,'faxNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getContactAddressEmail()}" width="150" header="{resource:getString($constants,'email')}">
                      <textbox field="String" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="addContactButton" style="Button" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}"/>
                      <button key="removeContactButton" style="Button" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}"/>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
<!-- TAB 2 -->
              <tab tab="parameterTable,parameterTable" text="{resource:getString($constants,'parameter')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="parameterTable" width="605" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" tab="{meta:getId()},{meta:getIsActive()}">
                    <col key="{meta:getOrganizationParameterTypeId()}" width="300" align="left" header="{resource:getString($constants,'type')}">
                      <dropdown width="300" field="Integer" required="true" />
                    </col>
                    <col key="{meta:getOrganizationParameterValue()}" width="287" align="left" header="{resource:getString($constants,'value')}">
                      <textbox max="80" field="String" required="true" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <button key="addParameterButton" style="Button" icon="AddRowButtonImage" text="{resource:getString($constants,'addRow')}"/>
                      <button key="removeParameterButton" style="Button" icon="RemoveRowButtonImage" text="{resource:getString($constants,'removeRow')}"/>
                    </HorizontalPanel>
                  </widget>
                </VerticalPanel>
              </tab>
<!-- TAB 3 -->
              <tab tab="standardNoteButton,standardNoteButton" text="{resource:getString($constants,'note')}">
              	<xsl:call-template name="InternalNoteTab">
          			<xsl:with-param name="width">604</xsl:with-param>
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
