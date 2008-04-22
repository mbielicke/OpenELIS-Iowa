<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:labelMeta="xalan://org.openelis.meta.LabelMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumnsNum.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="labelMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.LabelMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "Label" name = "Label" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height="200px" width="100%" key="hideablePanel" maxRows="9" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')}" colwidths ="175">   
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
      <textbox key = "{labelMeta:name()}" max = "30" width= "215px" case = "lower" tab="{labelMeta:description()},{labelMeta:scriptlet()}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed" max = "60"  key= "{labelMeta:description()}" width= "425px" tab="{labelMeta:printerType()},{labelMeta:name()}"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"printerType")'/>:</text>
     </widget>
     <widget>
										<autoDropdown key="{labelMeta:printerType()}" width = "90px" case="mixed" popWidth="auto"  multiSelect="false" fromModel="true"  type="integer" tab="{labelMeta:scriptlet()},{labelMeta:description()}">													
													 <widths>100</widths>													 													
										</autoDropdown>												
										</widget>  
										        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"scriptlet")'/>:</text>
     </widget>
     <widget>
										<autoDropdown key="{labelMeta:scriptlet()}" width = "180px"  popWidth="auto" case="mixed" multiSelect="false" fromModel="true"    type="integer" tab="{labelMeta:name()},{labelMeta:printerType()}">													
													<widths>190</widths>													 													
										</autoDropdown>												
										</widget>
	  </row>			  												          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{labelMeta:id()}" type="integer" required = "false" />
 <string key="{labelMeta:name()}" max = "20" required="false"/> <!--required = "true"--> 
 <string key="{labelMeta:description()}" max = "60" required = "false" /> 	 
 <dropdown key="{labelMeta:printerType()}" type="integer" required="false"/> <!--required = "true"-->
 <dropdown key="{labelMeta:scriptlet()}" type="integer" required="false"/> <!--required = "true"-->
</rpc>
					   
<rpc key= "query">     
 <queryString key="{labelMeta:name()}"/>
 <queryString key="{labelMeta:description()}"  /> 	
 <dropdown key="{labelMeta:printerType()}" type="integer"/> 
 <dropdown key="{labelMeta:scriptlet()}" type="integer"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{labelMeta:name()}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 