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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:meta="xalan://org.openelis.metamap.OrganizationMetaMap" 
                xmlns:addr="xalan://org.openelis.meta.AddressMeta"
                xmlns:contact="xalan://org.openelis.metamap.OrganizationContactMetaMap"
                xmlns:note="xalan://org.openelis.meta.NoteMeta"
                xmlns:parent="xalan://org.openelis.meta.OrganizationMeta"
                extension-element-prefixes="resource"
                version="1.0">
  <xsl:import href="aToZOneColumn.xsl"/>
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  <xalan:component prefix="meta">

    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrganizationMetaMap"/>
  </xalan:component>
  <xalan:component prefix="addr">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  </xalan:component>
  <xalan:component prefix="contact">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.OrganizationContactMetaMap"/>
  </xalan:component>
  <xalan:component prefix="note">

    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.NoteMeta"/>
  </xalan:component>
  <xalan:component prefix="parent">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="org" select="meta:new()"/>
    <xsl:variable name="addr" select="meta:getAddress($org)"/>
    <xsl:variable name="cont" select="meta:getOrganizationContact($org)"/>

    <xsl:variable name="parent" select="meta:getParentOrganization($org)"/>
    <xsl:variable name="note" select="meta:getNote($org)"/>
    <xsl:variable name="contAddr" select="contact:getAddress($cont)"/>
    <xsl:variable name="language">
      <xsl:value-of select="locale"/>
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props"/>
    </xsl:variable>

    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
    <screen id="Organization" name="{resource:getString($constants,'organization')}" serviceUrl="TestService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <display>
        <HorizontalPanel padding="0" spacing="0">
          <!--left table goes here -->
          <CollapsePanel key="collapsePanel" height="440px" style="LeftSidePanel">
              <resultsTable height="425px" width="100%" key="azTable" showError="false">
                 <buttonPanel key="atozButtons">
                <xsl:call-template name="aToZLeftPanelButtons"/>
               </buttonPanel>
               <table maxRows="18" width="auto">
                 <col header="{resource:getString($constants,'name')}" width="175"> 
                   <label/>
                 </col>
               </table>
             </resultsTable>
          </CollapsePanel>
          <VerticalPanel spacing="0" padding="0">
            <!--button panel code-->
            <AbsolutePanel spacing="0" style="ButtonPanelContainer">
              <buttonPanel key="buttons">
                <xsl:call-template name="queryButton">
                  <xsl:with-param name="language">

                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="previousButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="nextButton">

                  <xsl:with-param name="language">
                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="buttonPanelDivider"/>
                <xsl:call-template name="addButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language"/>
                  </xsl:with-param>

                </xsl:call-template>
                <xsl:call-template name="updateButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="buttonPanelDivider"/>
                <xsl:call-template name="commitButton">
                  <xsl:with-param name="language">

                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
                <xsl:call-template name="abortButton">
                  <xsl:with-param name="language">
                    <xsl:value-of select="language"/>
                  </xsl:with-param>
                </xsl:call-template>
              </buttonPanel>

            </AbsolutePanel>
            <!--end button panel-->
            <VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
                  <textbox key="{meta:getId($org)}" tab="{meta:getName($org)},{meta:getIsActive($org)}" width="75px" field="Integer"/>
                </row>

                <row>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
                  <textbox case="upper" key="{meta:getName($org)}" max="40" tab="{addr:getMultipleUnit($addr)},{meta:getId($org)}" width="225px" field="String" required="true"/>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'city')"/>:</text>
                  <widget colspan="3">
                    <textbox case="upper" key="{addr:getCity($addr)}" max="30" tab="{addr:getState($addr)},{addr:getStreetAddress($addr)}" width="212px" field="String" />
                  </widget>
                </row>

                <row>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'aptSuite')"/>:</text>
                  <textbox case="upper" key="{addr:getMultipleUnit($addr)}" max="30" tab="{addr:getStreetAddress($addr)},{meta:getName($org)}" width="212px" field="String"/>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'state')"/>:</text>
                  <dropdown case="upper" key="{addr:getState($addr)}" tab="{addr:getZipCode($addr)},{addr:getCity($addr)}" width="40px" field="String" popWidth="40px"/>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'zipcode')"/>:</text>
                  <textbox key="{addr:getZipCode($addr)}" mask="99999-9999" tab="{addr:getCountry($addr)},{addr:getState($addr)}" width="70" field="String"/>

                </row>
                <row>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'address')"/>:</text>
                  <textbox case="upper" key="{addr:getStreetAddress($addr)}" max="30" tab="{addr:getCity($addr)},{addr:getMultipleUnit($addr)}" width="212px" field="String"/>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'country')"/>:</text>
                  <widget colspan="3">
                    <dropdown case="mixed" key="{addr:getCountry($addr)}" tab="{parent:getName($parent)},{addr:getZipCode($addr)}" width="175px" popWidth="175px" field="String"/>
                  </widget>

                </row>
                <row>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'parentOrganization')"/>:</text>
                  <autoComplete case="upper" cat="parentOrg" key="{parent:getName($parent)}" field="Integer" tab="{meta:getIsActive($org)},{addr:getCountry($addr)}" width="241px" popWidth="auto">
                      <col header="Name" width="180"/>
                      <col header="Street" width="110"/>
                      <col header="City" width="100"/>
                      <col header="St" width="20"/>
                  </autoComplete>
                  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'active')"/>:</text>
                  <widget colspan="3">
                    <check key="{meta:getIsActive($org)}" tab="contactsTable,{parent:getName($parent)}"/>
                  </widget>
                </row>
              </TablePanel>
              <!-- TAB PANEL -->
              <TabPanel key="orgTabPanel">
                <!-- TAB 1 -->
                <tab key="contactsTab" text="{resource:getString($constants,'contact')}">
                  <VerticalPanel  width="610px" padding="0" spacing="0">
                    <widget valign="top">
                      <table key="contactsTable" maxRows="9" showScroll="ALWAYS" title="" width="574px" tab="{meta:getId($org)},{meta:getIsActive($org)}">
                          <col key="{contact:getContactTypeId($cont)}" header="{resource:getString($constants,'type')}" width="106" sort="true">
                              <dropdown case="mixed" width="90" field="Integer" required="true"/>
                          </col>
                          <col key="{contact:getName($cont)}" header="{resource:getString($constants,'contactName')}" width="130" sort="true">   
                            <textbox  case="upper" field="String" max="40" required="true"/>
                          </col>
                          <col key="{addr:getMultipleUnit($contAddr)}" header="{resource:getString($constants,'aptSuite')}" width="130" sort="true">
                              <textbox  case="upper" field="String" max="30"/>
                          </col>
                          <col key="{addr:getStreetAddress($contAddr)}" header="{resource:getString($constants,'address')}" width="130" sort="true">
                            <textbox  case="upper" field="String" max="40"/>
                          </col>
                          <col key="{addr:getCity($contAddr)}" header="{resource:getString($constants,'city')}" width="130" sort="true">
                              <textbox  case="upper" field="String" max="30"/>
                          </col>
                          <col key="{addr:getState($contAddr)}" header="{resource:getString($constants,'state')}" width="56" sort="true">
                             <dropdown case="upper" width="40px" field="String"/>
                          </col>
                          <col key="{addr:getZipCode($contAddr)}" header="{resource:getString($constants,'zipcode')}" width="68" sort="true">
                              <textbox mask="99999-9999"  case="mixed" field="String"/>
                          </col>
                          <col key="{addr:getCountry($contAddr)}" header="{resource:getString($constants,'country')}" width="126" sort="true">
                              <dropdown  case="mixed" width="110px" field="String"/>
                          </col>
                          <col key="{addr:getWorkPhone($contAddr)}" header="{resource:getString($constants,'workNumber')}" width="100" sort="true">
                              <textbox case="mixed" field="String"/>
                          </col>
                          <col key="{addr:getHomePhone($contAddr)}" header="{resource:getString($constants,'homeNumber')}" width="90" sort="true">
                              <textbox case="mixed" field="String"/>
                          </col>
                          <col key="{addr:getCellPhone($contAddr)}" header="{resource:getString($constants,'cellNumber')}" width="90" sort="true">
                            <textbox  case="mixed" field="String"/>
                          </col>
                          <col key="{addr:getFaxPhone($contAddr)}" header="{resource:getString($constants,'faxNumber')}" width="90" sort="true">
                            <textbox case="mixed" field="String"/>
                          </col>
                          <col key="{addr:getEmail($contAddr)}" header="{resource:getString($constants,'email')}" width="150" sort="true">
                            <textbox case="mixed" field="String"/>
                          </col>
                      </table>
                    </widget>
                    <widget halign="center" style="WhiteContentPanel">
                      <appButton action="removeRow" key="removeContactButton" onclick="this" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage"/>
                          <text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
                        </HorizontalPanel>

                      </appButton>
                    </widget>
                  </VerticalPanel>
                  <!-- END TAB 1 -->
                </tab>
                <!-- START TAB 2 -->
                <tab key="identifierTab" text="{resource:getString($constants,'identifier')}">
                  <VerticalPanel padding="0" spacing="0">
                  <widget valign="top">
                    <table key="identifierstsTable" maxRows="9" showError="false" showScroll="ALWAYS" title="" width="auto">
                        <col key="{contact:getName($cont)}" header="{resource:getString($constants,'identifier')}" width="267" sort="true" filter="false" align="left">
                          <textbox case="mixed" required="true" field="String"/>
                        </col>
                        <col key="{addr:getMultipleUnit($contAddr)}" header="{resource:getString($constants,'value')}" width="298" sort="true" filter="false" align="left"> 
                           <textbox case="mixed" required="true" field="String"/>
                        </col>
                     </table>
                    </widget>
                    <widget halign="center" style="WhiteContentPanel">
                      <appButton action="removeIdentifierRow" key="removeIdentifierButton" onclick="this" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage"/>
                          <text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
                        </HorizontalPanel>
                      </appButton>
                    </widget>
                    <HorizontalPanel height="18px"/>
                  </VerticalPanel>
                </tab>
                <!-- END TAB 2 -->
                <!-- start TAB 3 -->
                <tab key="notesTab" text="{resource:getString($constants,'note')}">
                  <VerticalPanel height="164px" key="secMod3" padding="0" spacing="0" width="100%">

                    <TablePanel key="noteFormPanel" padding="0" spacing="0" style="Form">
                      <row>
                        <text style="Prompt"><xsl:value-of select="resource:getString($constants,'subject')"/>:</text>
                        <textbox case="mixed" key="{note:getSubject($note)}" max="60" showError="false" enabledStates="" tab="{note:getText($note)},{note:getText($note)}" width="429px" field="String"/>
                        <appButton action="standardNote" key="standardNoteButton" style="Button">
                          <HorizontalPanel>
                            <AbsolutePanel style="StandardNoteButtonImage"/>
                            <text><xsl:value-of select="resource:getString($constants,'standardNote')"/></text>
                          </HorizontalPanel>
                        </appButton>
                      </row>
                      <row>
                        <text style="Prompt"><xsl:value-of select="resource:getString($constants,'note')"/>:</text>
                        <widget colspan="2">
                          <textarea case="mixed" height="48px" key="{note:getText($note)}" showError="false" enabledStates="" tab="{note:getSubject($note)},{note:getSubject($note)}" width="545px"/>
                        </widget>
                      </row>
                      <row>
                          <widget>
                            <html key="spacer" xml:space="preserve"> </html>
                          </widget>
                        <widget colspan="2">
                          <HorizontalPanel style="notesPanelContainer">
                            <ScrollPanel height="178px" key="notesPanel" overflowX="auto" overflowY="scroll" style="NotesPanel" valign="top" width="545px"/>
                          </HorizontalPanel>
                        </widget>
                      </row>
                    </TablePanel>
                  </VerticalPanel>
                </tab>
              </TabPanel>
            </VerticalPanel>
          </VerticalPanel>
        </HorizontalPanel>
      </display>
    </screen>
  </xsl:template>
</xsl:stylesheet>