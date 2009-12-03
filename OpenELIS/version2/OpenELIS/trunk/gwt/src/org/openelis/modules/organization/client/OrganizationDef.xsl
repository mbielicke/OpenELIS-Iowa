

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
  xmlns:contact="xalan://org.openelis.metamap.OrganizationContactMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.OrganizationMetaMap"
  xmlns:parameter="xalan://org.openelis.metamap.OrganizationParameterMeta"
  xmlns:parent="xalan://org.openelis.meta.OrganizationMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <xsl:variable name="org" select="meta:new()" />
    <xsl:variable name="addr" select="meta:getAddress($org)" />
    <xsl:variable name="cont" select="meta:getOrganizationContact($org)" />
    <xsl:variable name="param" select="meta:getOrganizationParameter($org)" />
    <xsl:variable name="parent" select="meta:getParentOrganization($org)" />
    <xsl:variable name="contAddr" select="contact:getAddress($cont)" />

<!-- main screen -->

    <screen id="Organization" name="{resource:getString($constants,'organization')}">
      <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="20" style="atozTable">
                <col width="175" header="{resource:getString($constants,'name')}">
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
                <textbox key="{meta:getId($org)}" width="75" tab="{meta:getName($org)},{meta:getIsActive($org)}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
                </text>
                <textbox key="{meta:getName($org)}" width="225" case="UPPER" max="40" tab="{addr:getMultipleUnit($addr)},{meta:getId($org)}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'city')" />:
                </text>
                <widget colspan="3">
                  <textbox key="{addr:getCity($addr)}" width="212" case="UPPER" max="30" tab="{addr:getState($addr)},{addr:getStreetAddress($addr)}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'aptSuite')" />:
                </text>
                <textbox key="{addr:getMultipleUnit($addr)}" width="212" case="UPPER" max="30" tab="{addr:getStreetAddress($addr)},{meta:getName($org)}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'state')" />:
                </text>
                <dropdown key="{addr:getState($addr)}" width="40" case="UPPER" popWidth="40" tab="{addr:getZipCode($addr)},{addr:getCity($addr)}" field="String" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'zipcode')" />:
                </text>
                <textbox key="{addr:getZipCode($addr)}" width="70" mask="99999-9999" tab="{addr:getCountry($addr)},{addr:getState($addr)}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'address')" />:
                </text>
                <textbox key="{addr:getStreetAddress($addr)}" width="212" case="UPPER" max="30" tab="{addr:getCity($addr)},{addr:getMultipleUnit($addr)}" field="String" required="true" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'country')" />:
                </text>
                <widget colspan="3">
                  <dropdown key="{addr:getCountry($addr)}" width="175" popWidth="175" tab="{parent:getName($parent)},{addr:getZipCode($addr)}" field="String" required="true" />
                </widget>
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'parentOrganization')" />:
                </text>
                <autoComplete key="{parent:getName($parent)}" width="241" case="UPPER" popWidth="auto" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}" field="Integer">
                  <col width="180" header="Name" />
                  <col width="110" header="Street" />
                  <col width="100" header="City" />
                  <col width="20" header="St" />
                </autoComplete>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'active')" />:
                </text>
                <widget colspan="3">
                  <check key="{meta:getIsActive($org)}" tab="contactTable,{parent:getName($parent)}" />
                </widget>
              </row>
            </TablePanel>

<!-- TAB PANEL -->

            <TabPanel key="tabPanel" width="605" height="285">

<!-- TAB 1 -->

              <tab key="contactTab" text="{resource:getString($constants,'contact')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="contactTable" width="587" maxRows="10" showScroll="ALWAYS" tab="{meta:getId($org)},{meta:getIsActive($org)}">
                    <col key="{contact:getContactTypeId($cont)}" width="106" header="{resource:getString($constants,'type')}">
                      <dropdown width="90" field="Integer" required="true" />
                    </col>
                    <col key="{contact:getName($cont)}" width="130" header="{resource:getString($constants,'contactName')}">
                      <textbox max="40" field="String" required="true" />
                    </col>
                    <col key="{addr:getMultipleUnit($contAddr)}" width="130" header="{resource:getString($constants,'aptSuite')}">
                      <textbox case="UPPER" max="30" field="String" />
                    </col>
                    <col key="{addr:getStreetAddress($contAddr)}" width="130" header="{resource:getString($constants,'address')}">
                      <textbox case="UPPER" max="40" field="String" />
                    </col>
                    <col key="{addr:getCity($contAddr)}" width="130" header="{resource:getString($constants,'city')}" sort="true">
                      <textbox case="UPPER" max="30" field="String" />
                    </col>
                    <col key="{addr:getState($contAddr)}" width="56" header="{resource:getString($constants,'state')}">
                      <dropdown width="40" case="UPPER" field="String" />
                    </col>
                    <col key="{addr:getZipCode($contAddr)}" width="68" header="{resource:getString($constants,'zipcode')}">
                      <textbox mask="99999-9999" field="String" />
                    </col>
                    <col key="{addr:getCountry($contAddr)}" width="126" header="{resource:getString($constants,'country')}">
                      <dropdown width="110" field="String" />
                    </col>
                    <col key="{addr:getWorkPhone($contAddr)}" width="100" header="{resource:getString($constants,'workNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{addr:getHomePhone($contAddr)}" width="90" header="{resource:getString($constants,'homeNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{addr:getCellPhone($contAddr)}" width="90" header="{resource:getString($constants,'cellNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{addr:getFaxPhone($contAddr)}" width="90" header="{resource:getString($constants,'faxNumber')}">
                      <textbox field="String" />
                    </col>
                    <col key="{addr:getEmail($contAddr)}" width="150" header="{resource:getString($constants,'email')}">
                      <textbox field="String" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addContactButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeContactButton" style="Button">
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

              <tab key="parameterTab" text="{resource:getString($constants,'parameter')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="parameterTable" width="587" maxRows="10" showScroll="ALWAYS" tab="{meta:getId($org)},{meta:getIsActive($org)}">
                    <col key="{parameter:getTypeId($param)}" width="300" align="left" header="{resource:getString($constants,'type')}">
                      <dropdown width="300" field="Integer" required="true" />
                    </col>
                    <col key="{parameter:getValue($param)}" width="287" align="left" header="{resource:getString($constants,'value')}">
                      <textbox max="80" field="String" required="true" />
                    </col>
                  </table>
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="addParameterButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'addRow')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="removeParameterButton" style="Button">
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

<!-- TAB 3 -->

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
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
