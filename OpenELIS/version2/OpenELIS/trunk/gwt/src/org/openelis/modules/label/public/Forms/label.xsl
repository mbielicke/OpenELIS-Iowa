<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:labelMeta="xalan://org.openelis.newmeta.LabelMetaMap"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.LabelMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="lbl" select="labelMeta:new()"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "Label" name = "{resource:getString($constants,'label')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <HorizontalPanel spacing= "0" padding= "0" height="225px" style="WhiteContentPanel">  
  <aToZ height="225px" width="100%" key="hideablePanel" maxRows="9" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')}" colwidths ="175">   
     <buttonPanel key="atozButtons">
	   <xsl:call-template name="aToZLeftPanelButtons"/>		
	</buttonPanel>		     
  </aToZ>
  
  <VerticalPanel>
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
   
  <VerticalPanel/> 
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
	  <autoDropdown key="{labelMeta:getPrinterTypeId($lbl)}" width = "90px" case="mixed" tab="{labelMeta:getScriptletId($lbl)},{labelMeta:getDescription($lbl)}"/>
    </row>
    <row>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"scriptlet")'/>:</text>
       <autoDropdown key="{labelMeta:getScriptletId($lbl)}" width = "180px" case="mixed" tab="{labelMeta:getName($lbl)},{labelMeta:getPrinterTypeId($lbl)}"/>
	  </row>			  												                                           					                         
   </TablePanel>            
               
                            
						
  </VerticalPanel>
 </HorizontalPanel>
</display>
							  
<rpc key= "display">
 <number key="{labelMeta:getId($lbl)}" type="integer" required = "false" />
 <string key="{labelMeta:getName($lbl)}" max = "20" required = "true"/> 
 <string key="{labelMeta:getDescription($lbl)}" max = "60" required = "false" /> 	 
 <dropdown key="{labelMeta:getPrinterTypeId($lbl)}" type="integer" required = "true"/>
 <dropdown key="{labelMeta:getScriptletId($lbl)}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{labelMeta:getName($lbl)}"/>
 <queryString key="{labelMeta:getDescription($lbl)}"  /> 	
 <dropdown key="{labelMeta:getPrinterTypeId($lbl)}" type="integer"/> 
 <dropdown key="{labelMeta:getScriptletId($lbl)}" type="integer"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{labelMeta:getName($lbl)}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 