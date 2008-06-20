<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.newmeta.QaEventMetaMap"
                xmlns:testMeta="xalan://org.openelis.newmeta.QaEventTestMetaMap"
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
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.QaEventMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="testMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.newmeta.QaEventTestMetaMap"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="qae" select="meta:new()"/>
    <xsl:variable name="test" select="meta:getTest($qae)"/>
    <xsl:variable name="method" select="testMeta:getMethod($test)"/>
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id= "QAEvents" name="{resource:getString($constants,'QAEvent')}" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height="425px" width="100%" key="hideablePanel" maxRows="19" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')},{resource:getString($constants,'test')},{resource:getString($constants,'method')}" colwidths ="100,65,65">   
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
    			<xsl:call-template name="buttonPanelDivider"/>
    			<xsl:call-template name="commitButton"/>
    			<xsl:call-template name="abortButton"/>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
   
  <panel layout= "vertical" height = "5px" xsi:type= "Panel"/> 
  <panel key = "qafields" layout= "table" style="Form" xsi:type= "Table">
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
     </widget>
     <widget> 
      <textbox key = "{meta:getName($qae)}" max = "20" width= "145px" case = "lower" tab="{meta:getDescription($qae)},{meta:getReportingText($qae)}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed" max = "60"  key= "{meta:getDescription($qae)}" width= "425px" tab="{meta:getTypeId($qae)},{meta:getName($qae)}"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>
     <widget>  		
		<autoDropdown key="{meta:getTypeId($qae)}" width = "120px" case="mixed" tab="{meta:getTestId($qae)},{meta:getDescription($qae)}"/>
	</widget>  									        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
     </widget>
     <widget>
		<autoDropdown key="{meta:getTestId($qae)}" width = "140px"  case="mixed" tab="{meta:getIsBillable($qae)},{meta:getTypeId($qae)}"/>
	 </widget>
	  </row>		
	  						
      <row>           
      <widget>
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"billable")'/>:</text>
        </widget>
        <widget>
          <check key= "{meta:getIsBillable($qae)}" tab="{meta:getReportingSequence($qae)},{meta:getTestId($qae)}"/>
       </widget>
      </row> 
      <row>
       
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>
     </widget>
     <widget>
      <textbox key= "{meta:getReportingSequence($qae)}"  width= "50px" tab="{meta:getReportingText($qae)},{meta:getIsBillable($qae)}"/>
     </widget>    
     </row>
			
	   <row>
	    <widget valign = "top"> 
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="400px" height="200px" case="mixed" key="{meta:getReportingText($qae)}" tab="{meta:getName($qae)},{meta:getReportingSequence($qae)}"/>
	    </widget> 
	   </row>								          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{meta:getId($qae)}" type="integer" required = "false" />
 <string key="{meta:getName($qae)}" max = "20" required = "true"/>
 <number key="{meta:getReportingSequence($qae)}"  type="integer" required = "false" />
 <string key="{meta:getDescription($qae)}" max = "60" required = "false" /> 	 
 <check key= "{meta:getIsBillable($qae)}" required = "false" />
 <string key="{meta:getReportingText($qae)}" required = "true"/>
 <dropdown key="{meta:getTestId($qae)}" type="integer" required = "false" />
 <dropdown key="{meta:getTypeId($qae)}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{meta:getName($qae)}" />
 <queryNumber key="{meta:getReportingSequence($qae)}" type="integer" />
 <queryString key="{meta:getDescription($qae)}"  /> 	
 <dropdown key="{meta:getTypeId($qae)}" type="integer"/> 
 <dropdown key="{meta:getTestId($qae)}" type="integer"/>
 <queryCheck key="{meta:getIsBillable($qae)}"/>
 <queryString key="{meta:getReportingText($qae)}"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{meta:getName($qae)}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 