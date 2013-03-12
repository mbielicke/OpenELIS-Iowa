
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
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="ToDo" name="{resource:getString($constants,'toDo')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <HorizontalPanel style="Form">
          <HorizontalPanel width="10" />
          <check key="mySection" />
          <text style="Prompt">
            <xsl:value-of select="resource:getString($constants,'showMySectOnly')" />
          </text>
          <HorizontalPanel width="200" />
          <HorizontalPanel>
            <appButton key="refreshButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="refreshButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'refresh')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
          <appButton key="trackingButton" style="Button">
            <HorizontalPanel>
              <AbsolutePanel style="trackingIcon" />
              <text>
                <xsl:value-of select="resource:getString($constants,'details')" />
              </text>
            </HorizontalPanel>
          </appButton>
          <appButton key="exportToExcelButton" style="Button">
            <HorizontalPanel>
              <text>
                <xsl:value-of select="resource:getString($constants,'exportToXl')" />
              </text>
            </HorizontalPanel>
          </appButton>
        </HorizontalPanel>
        <VerticalPanel height="5" />
        <TabPanel height="550" key="tabPanel" width="625">
          <tab text="{resource:getString($constants,'loggedIn')}">
            <VerticalPanel>
              <widget valign="top">
                <table key="loggedInTable" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                  <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                    <label field="Integer" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                    <dropdown field="String" width="50" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="70">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="60">
                    <label field="String" />
                  </col>
                  <col header="{resource:getString($constants,'collected')}" key="datec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col header="{resource:getString($constants,'received')}" key="daterec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'override')}" key="qaResOver" width="55">
                    <check />
                  </col>
                  <col header="{resource:getString($constants,'holding')}" key="holdingTime" sort="true" width="100">
                    <percentBar barWidth = "50">
                      <range color="green" threshold="25" />
                      <range color="yellow" threshold="50" />
                      <range color="orange" threshold="75" />
                      <range color="red" threshold="100" />
                    </percentBar>
                  </col>
                  <col header="{resource:getString($constants,'expCompletion')}" key="avgTATime" sort="true" width="105">
                    <percentBar barWidth = "50">
                      <range color="green" threshold="25" />
                      <range color="yellow" threshold="50" />
                      <range color="orange" threshold="75" />
                      <range color="red" threshold="100" />
                    </percentBar>
                  </col>
                  <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                    <label field="String" />
                  </col>
                </table>
              </widget>
              <VerticalPanel height="200" key="loggedInPanel" width="623" />
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'initiated')}">
            <VerticalPanel>
              <table key="initiatedTable" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                  <label field="Integer" />
                </col>
                <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                  <dropdown field="String" width="50" />
                </col>
                <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="70">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="60">
                  <label field="String" />
                </col>
                <col header="{resource:getString($constants,'holding')}" key="holdingTime" sort="true" width="100">
                  <percentBar barWidth = "50">
                    <range color="green" threshold="25" />
                    <range color="yellow" threshold="50" />
                    <range color="orange" threshold="75" />
                    <range color="red" threshold="100" />
                  </percentBar>
                </col>
                <col header="{resource:getString($constants,'expCompletion')}" key="avgTATime" sort="true" width="105">
                  <percentBar barWidth = "50">
                    <range color="green" threshold="25" />
                    <range color="yellow" threshold="50" />
                    <range color="orange" threshold="75" />
                    <range color="red" threshold="100" />
                  </percentBar>
                </col>
                <col header="{resource:getString($constants,'daysInInitiated')}" align = "center" key="daysInInitiated" sort="true" width="100">
                  <label field="Integer" />
                </col>
                <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                  <label field="String" />
                </col>
              </table>
              <VerticalPanel height="200" key="initiatedPanel" width="605" />
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'worksheet')}">
            <VerticalPanel>
              <table key="worksheetTable" maxRows="24" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                <col header="{resource:getString($constants,'worksheetNumber')}" key="worksheetNum" sort="true" width="80">
                  <label field="Integer" />
                </col>
                <col filter="true" header="{resource:getString($constants,'worksheetUser')}" key="user" sort="true" width="122">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="80">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="80">
                  <label field="String" />
                </col>
                <col header="{resource:getString($constants,'created')}" key="createdDate" sort="true" width="105">
                  <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
              </table>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'completed')}">
            <VerticalPanel>
              <table key="completedTable" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                  <label field="Integer" />
                </col>
                <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                  <dropdown field="String" width="50" />
                </col>
                <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="70">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="60">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'override')}" key="qaResOver" width="55">
                  <check />
                </col>
                <col header="{resource:getString($constants,'completed')}" key="datec" sort="true" width="105">
                  <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                  <label field="String" />
                </col>
              </table>
              <VerticalPanel height="200" key="completedPanel" width="605" />
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'released')}">
            <VerticalPanel>
              <widget valign="top">
                <table key="releasedTable" maxRows="24" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                  <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                    <label field="Integer" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                    <dropdown field="String" width="50" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="70">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="60">
                    <label field="String" />
                  </col>
                  <col header="{resource:getString($constants,'collected')}" key="datec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col header="{resource:getString($constants,'released')}" key="daterec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'override')}" key="qaResOver" width="55">
                    <check />
                  </col>
                  <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                    <label field="String" />
                  </col>
                </table>
              </widget>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'toBeVerified')}">
            <VerticalPanel>
              <widget valign="top">
                <table key="toBeVerifiedTable" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                  <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                    <label field="Integer" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                    <dropdown field="String" width="50" />
                  </col>
                  <col header="{resource:getString($constants,'collected')}" key="datec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col header="{resource:getString($constants,'received')}" key="daterec" sort="true" width="105">
                    <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'override')}" key="qaResOver" width="55">
                    <check />
                  </col>
                  <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                    <label field="String" />
                  </col>
                  <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                    <label field="String" />
                  </col>
                </table>
              </widget>
              <VerticalPanel height="200" key="toBeVerifiedPanel" width="623" />
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'other')}">
            <VerticalPanel>
              <table key="otherTable" maxRows="24" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="605">
                <col header="{resource:getString($constants,'accNum')}" key="accessionNumber" sort="true" width="50">
                  <label field="Integer" />
                </col>
                <col filter="true" header="{resource:getString($constants,'domain')}" key="domain" width="50">
                  <dropdown field="String" width="50" />
                </col>
                <col filter="true" header="{resource:getString($constants,'section')}" key="section" sort="true" width="100">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'status')}" key="status" sort="true" width="100">
                  <dropdown field="Integer" width="100" />
                </col>
                <col filter="true" header="{resource:getString($constants,'test')}" key="test" sort="true" width="70">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'method')}" key="method" sort="true" width="60">
                  <label field="String" />
                </col>
                <col header="{resource:getString($constants,'collected')}" key="datec" sort="true" width="105">
                  <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col header="{resource:getString($constants,'received')}" key="daterec" sort="true" width="105">
                  <label field="Date" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col filter="true" header="{resource:getString($constants,'override')}" key="qaResOver" width="55">
                  <check />
                </col>
                <col header="{resource:getString($constants,'domainSpecField')}" key="mush" width="150">
                  <label field="String" />
                </col>
                <col filter="true" header="{resource:getString($constants,'reportTo')}" key="reportTo" sort="true" width="200">
                  <label field="String" />
                </col>
              </table>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'instrument')}">
            <text />
          </tab>
        </TabPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>