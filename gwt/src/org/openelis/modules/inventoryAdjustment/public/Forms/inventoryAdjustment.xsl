<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) OpenELIS.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale" 
                xmlns:meta="xalan://org.openelis.meta.OrganizationMeta" 
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.OrganizationMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
  <xsl:variable name="org" select="meta:new()"/>
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Organization" name="Inventory Adjustment" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel style="WhiteContentPanel" spacing="0" padding="0">
			<VerticalPanel spacing="0">
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
			<VerticalPanel>
				<TablePanel style="Form">
					<row>
						<text style="Prompt">Adjustment #:</text>
						<textbox case="lower" key="aa" width="75px" max="20" tab="??,??"/>
						<text style="Prompt">Description:</text>
						<textbox case="lower" key="ss" width="350px" max="20" tab="??,??"/>
					</row>
					<row>
						<text style="Prompt">Adj Date:</text>
						<textbox case="lower" key="aa" width="85px" max="20" tab="??,??"/>
						<text style="Prompt">User:</text>
						<textbox case="lower" key="aa" width="125px" max="20" tab="??,??"/>
					</row>
				</TablePanel>
				<VerticalPanel spacing="0" padding="0" overflow="hidden">
					<widget valign="top">
						<table width="auto" key="contactsTable" manager="OrganizationContactsTable" maxRows="10" title="" showError="false" showScroll="true">
							<headers>Inventory Item, Store, Storage Location, On Hand, Phys Count, Adj Quan</headers>
							<widths>170,170,170,70,70,70</widths>										<editors>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
								<textbox case="upper"/>
							</editors>
							<fields>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
								<string key="{meta:getId($org)}" required="false"/>
							</fields>
							<sorts>true,true,true,true,true,true</sorts>
							<filters>false,false,false,false,false,false</filters>
							<colAligns>left,left,left,left,left,left</colAligns>
						</table>
						<query>
							<queryTable width="592px" title="" maxRows="8" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"type")'/>,<xsl:value-of select='resource:getString($constants,"contactName")'/>,
								<xsl:value-of select='resource:getString($constants,"aptSuite")'/>,<xsl:value-of select='resource:getString($constants,"address")'/>,
								<xsl:value-of select='resource:getString($constants,"city")'/>,<xsl:value-of select='resource:getString($constants,"state")'/>,
								<xsl:value-of select='resource:getString($constants,"zipcode")'/>,<xsl:value-of select='resource:getString($constants,"country")'/>,
								<xsl:value-of select='resource:getString($constants,"workNumber")'/>,<xsl:value-of select='resource:getString($constants,"homeNumber")'/>,
								<xsl:value-of select='resource:getString($constants,"cellNumber")'/>,<xsl:value-of select='resource:getString($constants,"faxNumber")'/>,
								<xsl:value-of select='resource:getString($constants,"email")'/></headers>
								<widths>106,130,130,130,130,56,68,126,100,90,90,90,150</widths>
								<editors>
									<autoDropdown case="mixed" width="90px" multiSelect="true"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<textbox case="upper"/>
									<autoDropdown case="upper" width="40px" multiSelect="true"/>
								 	<textbox case="mixed"/>
								 	<autoDropdown case="mixed" width="110px" multiSelect="true"/>
								 	<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>
									<textbox case="mixed"/>		 	
								</editors>
								<fields><xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/>,
								<xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/>,
								<xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/></fields>,<xsl:value-of select='meta:getId($org)'/>,
								<xsl:value-of select='meta:getId($org)'/>,	<xsl:value-of select='meta:getId($org)'/>,<xsl:value-of select='meta:getId($org)'/>,
								<xsl:value-of select='meta:getId($org)'/>										
							</queryTable>
							</query>
						</widget>
						<widget style="WhiteContentPanel" halign="center">									
							<appButton action="removeRow" onclick="this" style="Button" key="removeContactButton">
								<HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                						<text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
					              </HorizontalPanel>
						            </appButton>
					            </widget>
							</VerticalPanel>
				</VerticalPanel>								
				</VerticalPanel>				
		</HorizontalPanel>
	</display>
	<rpc key="display">
  	  <number key="{meta:getId($org)}" type="integer" required="false"/>
      <number key="{meta:getId($org)}" required="false" type="integer"/>
      <string key="{meta:getId($org)}" max="40" required="true"/>
      <string key="{meta:getId($org)}" max="30" required="true"/>
      <string key="{meta:getId($org)}" max="30" required="false"/>
      <string key="{meta:getId($org)}" max="30" required="true"/>
      <string key="{meta:getId($org)}" max="10" required="true"/>
      <check key="{meta:getId($org)}" required="false"/>
      <string key="{meta:getId($org)}" max="60" required="false"/>
      <string key="{meta:getId($org)}" required="false"/>
      <dropdown key="{meta:getId($org)}" type="integer" required="false"/> 
      <dropdown key="{meta:getId($org)}" required="false"/>
      <dropdown key="{meta:getId($org)}" required="true"/>
      <table key="contactsTable"/>
	</rpc>
	<rpc key="query">
      <queryNumber key="{meta:getId($org)}" type="integer"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}" value="query"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}"/>
      <queryString key="{meta:getId($org)}"/>
      <dropdown key="{meta:getId($org)}" required="false"/>
      <dropdown key="{meta:getId($org)}" required="false"/>
      <queryCheck key="{meta:getId($org)}" required="false"/>
      <table key="contactsTable"/>
      <dropdown key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
	  <dropdown key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
      <queryString key="{meta:getId($org)}" required="false"/>
      <queryString key="{meta:getId($org)}" required="false"/>
      <queryString key="{meta:getId($org)}" required="false"/>
	  <queryString key="{meta:getId($org)}" required="false"/>
      <queryString key="{meta:getId($org)}" required="false"/>
	  <dropdown key="{meta:getId($org)}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
		<queryString key="{meta:getId($org)}"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>