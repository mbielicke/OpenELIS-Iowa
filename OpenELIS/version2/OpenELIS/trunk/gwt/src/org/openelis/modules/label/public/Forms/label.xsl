<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:labelMeta="xalan://org.openelis.meta.LabelMeta"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.LabelMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "Label" name = "{resource:getString($constants,'label')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" height="225px" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height="225px" width="100%" key="hideablePanel" maxRows="9" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')}" colwidths ="175">   
     <buttonPanel key="atozButtons">
	   <xsl:call-template name="aToZLeftPanelButtons"/>		
	</buttonPanel>		     
  </aToZ>
  
  <panel layout= "vertical" xsi:type = "Panel">
   <!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="queryButton"/>
    			<xsl:call-template name="previousButton"/>
    			<xsl:call-template name="nextButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="addButton"/>
    			<xsl:call-template name="updateButton"/>
    			<xsl:call-template name="deleteButton"/>
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton"/>
    			<xsl:call-template name="abortButton"/>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
   
  <panel layout= "vertical" height = "5px" xsi:type= "Panel"/> 
  <panel key = "svfields" layout= "table" style="Form" xsi:type= "Table">
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
     </widget>
     <widget> 
      <textbox key = "{labelMeta:getName()}" max = "30" width= "215px" case = "lower" tab="{labelMeta:getDescription()},{labelMeta:getScriptletId()}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed" max = "60"  key= "{labelMeta:getDescription()}" width= "425px" tab="{labelMeta:getPrinterTypeId()},{labelMeta:getName()}"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"printerType")'/>:</text>
     </widget>
     <widget>
		<autoDropdown key="{labelMeta:getPrinterTypeId()}" width = "90px" case="mixed" tab="{labelMeta:getScriptletId()},{labelMeta:getDescription()}"/>
	</widget>  
										        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"scriptlet")'/>:</text>
     </widget>
     <widget>
     	<autoDropdown key="{labelMeta:getScriptletId()}" width = "180px" case="mixed" tab="{labelMeta:getName()},{labelMeta:getPrinterTypeId()}"/>
	</widget>
	  </row>			  												          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{labelMeta:getId()}" type="integer" required = "false" />
 <string key="{labelMeta:getName()}" max = "20" required = "true"/> 
 <string key="{labelMeta:getDescription()}" max = "60" required = "false" /> 	 
 <dropdown key="{labelMeta:getPrinterTypeId()}" type="integer" required = "true"/>
 <dropdown key="{labelMeta:getScriptletId()}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{labelMeta:getName()}"/>
 <queryString key="{labelMeta:getDescription()}"  /> 	
 <dropdown key="{labelMeta:getPrinterTypeId()}" type="integer"/> 
 <dropdown key="{labelMeta:getScriptletId()}" type="integer"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{labelMeta:getName()}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 