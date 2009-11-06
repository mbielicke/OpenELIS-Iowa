
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
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:addr="xalan://org.openelis.meta.AddressMeta"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:location="xalan://org.openelis.metamap.ProviderAddressMetaMap"
  xmlns:meta="xalan://org.openelis.metamap.ProviderMetaMap"
  xmlns:note="xalan://org.openelis.meta.NotetMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProviderMetaMap" />
  </xalan:component>
  <xalan:component prefix="note">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NotetMeta" />
  </xalan:component>
  <xalan:component prefix="location">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.ProviderAddressMetaMap" />
  </xalan:component>
  <xalan:component prefix="addr">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="pro" select="meta:new()" />
    <xsl:variable name="loc" select="meta:getProviderAddress($pro)" />
    <xsl:variable name="note" select="meta:getNote($pro)" />
    <xsl:variable name="locAddr" select="location:getAddress($loc)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="Provider" name="{resource:getString($constants,'provider')}">
      <HorizontalPanel padding="0" spacing="0">
        <CollapsePanel height="440px" key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225px">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" maxRows="18" width="auto">
                <col header="{resource:getString($constants,'lastName')}" width="88">
                  <label />
                </col>
                <col header="{resource:getString($constants,'firstName')}" width="87">
                  <label />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton enable="false" key="atozPrev" style="Button">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton enable="false" key="atozNext" style="Button">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
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
                  <xsl:value-of select="resource:getString($constants,'id')" />:</text>
                <textbox field="Integer" key="{meta:getId($pro)}" tab="{meta:getLastName($pro)},{meta:getNpi($pro)}" width="50px" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'lastName')" />:</text>
                <textbox case="UPPER" key="{meta:getLastName($pro)}" max="30" required="true" tab="{meta:getFirstName($pro)},{meta:getId($pro)}" width="215px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'type')" />:</text>
                <dropdown key="{meta:getTypeId($pro)}" required="true" tab="{meta:getNpi($pro)},{meta:getMiddleName($pro)}" width="80px" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'firstName')" />:</text>
                <textbox case="UPPER" key="{meta:getFirstName($pro)}" max="20" tab="{meta:getMiddleName($pro)},{meta:getLastName($pro)}" width="145px" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'npi')" />:</text>
                <textbox key="{meta:getNpi($pro)}" max="20" tab="{meta:getId($pro)},{meta:getTypeId($pro)}" width="145px" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'middleName')" />:</text>
                <textbox case="UPPER" key="{meta:getMiddleName($pro)}" max="20" tab="{meta:getTypeId($pro)},{meta:getFirstName($pro)}" width="145px" />
              </row>
            </TablePanel>
            <TabPanel key="provTabPanel" width="648px">
              <tab key="addressesTab" text="{resource:getString($constants,'locations')}">
                <VerticalPanel width="645px">
                  <widget valign="top">
                    <table key="providerAddressTable" maxRows="10" showScroll="ALWAYS" title="" width="615px">
                      <col header="{resource:getString($constants,'location')}" key="{location:getLocation($loc)}" sort="true" width="115">
                        <textbox max="50" required="true" />
                      </col>
                      <col header="{resource:getString($constants,'externalId')}" key="{location:getExternalId($loc)}" sort="true" width="130">
                        <textbox max="10" />
                      </col>
                      <col header="{resource:getString($constants,'aptSuite')}" key="{addr:getMultipleUnit($locAddr)}" sort="true" width="130">
                        <textbox max="30" />
                      </col>
                      <col header="{resource:getString($constants,'address')}" key="{addr:getStreetAddress($locAddr)}" sort="true" width="130">
                        <textbox max="30" />
                      </col>
                      <col header="{resource:getString($constants,'city')}" key="{addr:getCity($locAddr)}" sort="true" width="130">
                        <textbox max="30" />
                      </col>
                      <col header="{resource:getString($constants,'state')}" key="{addr:getState($locAddr)}" sort="true" width="60">
                        <dropdown case="UPPER" field="String" width="60px" />
                      </col>
                      <col header="{resource:getString($constants,'zipcode')}" key="{addr:getZipCode($locAddr)}" sort="true" width="100">
                        <textbox max="10" />
                      </col>
                      <col header="{resource:getString($constants,'country')}" key="{addr:getCountry($locAddr)}" sort="true" width="130">
                        <dropdown field="String" width="130px" />
                      </col>
                      <col header="{resource:getString($constants,'workNumber')}" key="{addr:getWorkPhone($locAddr)}" sort="true" width="90">
                        <textbox max="21" />
                      </col>
                      <col header="{resource:getString($constants,'homeNumber')}" key="{addr:getHomePhone($locAddr)}" sort="true" width="90">
                        <textbox max="16" />
                      </col>
                      <col header="{resource:getString($constants,'cellNumber')}" key="{addr:getCellPhone($locAddr)}" sort="true" width="90">
                        <textbox max="16" />
                      </col>
                      <col header="{resource:getString($constants,'faxNumber')}" key="{addr:getFaxPhone($locAddr)}" sort="true" width="150">
                        <textbox max="16" />
                      </col>
                      <col header="{resource:getString($constants,'email')}" key="{addr:getEmail($locAddr)}" sort="true" width="145">
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
              <tab key="notesTab" text="{resource:getString($constants,'note')}">
                <VerticalPanel padding="0" spacing="0">
                  <notes height="247" key="notesPanel" width="604" />
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
          <HorizontalPanel width="10px" />
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
