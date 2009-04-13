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
                xmlns:labelMeta="xalan://org.openelis.metamap.LabelMetaMap"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="labelMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.LabelMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="lbl" select="labelMeta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "Label" name = "{resource:getString($constants,'label')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <HorizontalPanel spacing= "0" padding= "0">  
  					<CollapsePanel key="collapsePanel" height="235px" style="LeftSidePanel">
  					   <!--
						<azTable colwidths ="175" height="225px" key="azTable" maxRows="9" tablewidth="auto" title="" width="100%" headers = "{resource:getString($constants,'name')}">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
						-->
					   <resultsTable height="225px" width="100%" key="azTable">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="9" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'name')"/></headers>
						     <widths>175</widths>
						     <editors>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						     </fields>
						   </table>
					   </resultsTable>
					</CollapsePanel>  
  <VerticalPanel padding="0" spacing="0">
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
		</AbsolutePanel>
		<!--end button panel-->
   
	<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel" height="235px" width="620px">
  <TablePanel key = "svfields" style="Form">
    <row>     
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text> 
      <textbox key = "{labelMeta:getName($lbl)}" max = "30" width= "215px" case = "lower" tab="{labelMeta:getDescription($lbl)},{labelMeta:getScriptletId($lbl)}"/>
     </row>     
     <row>     
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
      <textbox case= "mixed" max = "60"  key= "{labelMeta:getDescription($lbl)}" width= "425px" tab="{labelMeta:getPrinterTypeId($lbl)},{labelMeta:getName($lbl)}"/>    
    </row>
     <row>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"printerType")'/>:</text>
	  <dropdown key="{labelMeta:getPrinterTypeId($lbl)}" width = "90px" case="mixed" tab="{labelMeta:getScriptletId($lbl)},{labelMeta:getDescription($lbl)}"/>
    </row>
    <row>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"scriptlet")'/>:</text>
       <dropdown key="{labelMeta:getScriptletId($lbl)}" width = "180px" case="mixed" tab="{labelMeta:getName($lbl)},{labelMeta:getPrinterTypeId($lbl)}"/>
	  </row>			  												                                           					                         
   </TablePanel>            
               
                            
						
  </VerticalPanel>
  </VerticalPanel>
 </HorizontalPanel>
</display>
							  
<rpc key= "display">
 <integer key="{labelMeta:getId($lbl)}" type="integer" required = "false" />
 <string key="{labelMeta:getName($lbl)}" max = "20" required = "true"/> 
 <string key="{labelMeta:getDescription($lbl)}" max = "60" required = "false" /> 	 
 <dropdown key="{labelMeta:getPrinterTypeId($lbl)}" type="integer" required = "true"/>
 <dropdown key="{labelMeta:getScriptletId($lbl)}" type="integer" required = "true"/>
</rpc>
<!--					   
<rpc key= "query">     
 <queryString key="{labelMeta:getName($lbl)}"/>
 <queryString key="{labelMeta:getDescription($lbl)}"  /> 	
 <dropdown key="{labelMeta:getPrinterTypeId($lbl)}" type="integer"/> 
 <dropdown key="{labelMeta:getScriptletId($lbl)}" type="integer"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{labelMeta:getName($lbl)}"/>
</rpc>
 -->
</screen>
</xsl:template>
</xsl:stylesheet> 
