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
                xmlns:meta="xalan://org.openelis.metamap.SystemVariableMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.SystemVariableMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="sv" select="meta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "SystemVariable" name="{resource:getString($constants,'systemVariable')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <HorizontalPanel spacing= "0" padding= "0" style="WhiteContentPanel">  
  <aToZ height="235px" width="100%" key="hideablePanel"  maxRows="10" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')}" colwidths ="175">     
     <buttonPanel key="atozButtons">
	   <xsl:call-template name="aToZLeftPanelButtons"/>		
	</buttonPanel>		     
  </aToZ>
  
  <VerticalPanel>
   <!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
			<widget>
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
								<xsl:call-template name="deleteButton">
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
 			</widget>
		</AbsolutePanel>
		<!--end button panel-->
   
  <VerticalPanel height = "5px"/> 
  <TablePanel key = "svfields" layout= "table" style="Form" xsi:type= "Table">
    <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
      <textbox key ="{meta:getName($sv)}" max = "30" width= "215px" case = "lower" tab="{meta:getValue($sv)},{meta:getValue($sv)}"/>
     </row>     
     <row>     
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"value")'/>:</text>
      <textbox case= "mixed" key= "{meta:getValue($sv)}" width= "425px" tab="{meta:getName($sv)},{meta:getName($sv)}"/>   
    </row>     						                                           					                         
   </TablePanel>                                        						
  </VerticalPanel>
 </HorizontalPanel>
</display>
							  
<rpc key= "display">
 <number key="{meta:getId($sv)}" type="integer" required = "false" />
 <string key="{meta:getName($sv)}" required="true"/>
 <string key="{meta:getValue($sv)}" required="true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{meta:getName($sv)}" />
 <queryString key="{meta:getValue($sv)}"  /> 	
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{meta:getName($sv)}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 