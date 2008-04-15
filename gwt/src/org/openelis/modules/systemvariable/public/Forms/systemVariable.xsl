<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:systemVariableMeta="xalan://org.openelis.meta.SystemVariableMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZTwoColumns.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "SystemVariable" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height="200px" width="100%" key="hideablePanel" maxRows="10" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')}" colwidths ="100">   
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
      <textbox key ="{systemVariableMeta:name()}" max = "30" width= "150px" case = "lower" tab="{systemVariableMeta:value()}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"value")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed"  key= "{systemVariableMeta:value()}" width= "300px" tab="{systemVariableMeta:name()}"/>
     </widget>     
    </row>     						          
                                 					                         
   </panel>            
                            						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{systemVariableMeta:id()}" type="integer" required = "false" />
 <string key="{systemVariableMeta:name()}" required = "true" />
 <string key="{systemVariableMeta:value()}" required = "true" /> 	 
</rpc>
					   
<rpc key= "query">     
 <queryString key="{systemVariableMeta:name()}" />
 <queryString key="{systemVariableMeta:value()}"  /> 	
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{systemVariableMeta:name()}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 