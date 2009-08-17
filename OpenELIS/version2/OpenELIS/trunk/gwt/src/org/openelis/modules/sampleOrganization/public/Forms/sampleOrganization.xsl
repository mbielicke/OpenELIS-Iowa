

<!--
		Exhibit A - UIRF Open-source Based Public Software License. The
		contents of this file are subject to the UIRF Open-source Based Public
		Software License(the "License"); you may not use this file except in
		compliance with the License. You may obtain a copy of the License at
		openelis.uhl.uiowa.edu Software distributed under the License is
		distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
		express or implied. See the License for the specific language
		governing rights and limitations under the License. The Original Code
		is OpenELIS code. The Initial Developer of the Original Code is The
		University of Iowa. Portions created by The University of Iowa are
		Copyright 2006-2008. All Rights Reserved. Contributor(s):
		______________________________________. Alternatively, the contents of
		this file marked "Separately-Licensed" may be used under the terms of
		a UIRF Software license ("UIRF Software License"), in which case the
		provisions of a UIRF Software License are applicable instead of those
		above.
  -->

<xsl:stylesheet extension-element-prefixes="resource" version="1.0" xmlns:locale="xalan://java.util.Locale" xmlns:resource="xalan://org.openelis.util.UTFResource" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:import href="button.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="SampleOrganizationPicker" name="{resource:getString($constants,'sampleOrganization')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <VerticalPanel padding="0" spacing="0">
        <VerticalPanel width="300px" padding="0" spacing="0" style="WhiteContentPanel">
          <table key="sampleOrganizationTable" width="auto" maxRows="10" showScroll="ALWAYS" tab="sampleOrganizationTable,sampleOrganizationTable" title="">
            <col width="120" header="Type">
              <dropdown width="110px" case="mixed" />
            </col>
            <col width="35" header="Id">
              <label/>
            </col>
            <col width="160" header="Name">
              <autoComplete width="130px" case="upper">
                <col width="180" header="Name" />
                <col width="110" header="Street" />
                <col width="100" header="City" />
                <col width="20" header="St" />
              </autoComplete>
            </col>
            <col width="110" header="City">
              <label/>
            </col>
            <col width="50" header="State">
              <label/>
            </col>
          </table>
          <HorizontalPanel style="WhiteContentPanel">
          	<widget halign="center" style="WhiteContentPanel">
            	<appButton action="organizationAdd" key="organizationAddButton" onclick="this" style="Button">
                	<HorizontalPanel>
                    	<AbsolutePanel style="AddRowButtonImage"/>
                        <text><xsl:value-of select="resource:getString($constants,'addRow')"/></text>
                   	</HorizontalPanel>
            	</appButton>
          	</widget>
            <widget halign="center" style="WhiteContentPanel">
            	<appButton action="organizationRemove" key="organizationRemoveButton" onclick="this" style="Button">
                	<HorizontalPanel>
                    	<AbsolutePanel style="RemoveRowButtonImage"/>
                        <text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
                  	</HorizontalPanel>
            	</appButton>
        	</widget>
       	</HorizontalPanel>
        </VerticalPanel>

<!--button panel code-->

        <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="commitButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
