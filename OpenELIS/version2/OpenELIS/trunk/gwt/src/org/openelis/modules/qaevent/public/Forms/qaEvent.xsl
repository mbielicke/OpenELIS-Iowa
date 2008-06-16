<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:qaEventMeta="xalan://org.openelis.meta.QaEventMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="qaEventMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.QaEventMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
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
      <textbox key = "{qaEventMeta:getName()}" max = "20" width= "145px" case = "lower" tab="{qaEventMeta:getDescription()},{qaEventMeta:getReportingText()}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed" max = "60"  key= "{qaEventMeta:getDescription()}" width= "425px" tab="{qaEventMeta:getTypeId()},{qaEventMeta:getName()}"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>
     <widget>  		
		<autoDropdown key="{qaEventMeta:getTypeId()}" width = "120px" case="mixed" tab="{qaEventMeta:getTestId()},{qaEventMeta:getDescription()}"/>
	</widget>  									        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
     </widget>
     <widget>
		<autoDropdown key="{qaEventMeta:getTestId()}" width = "140px"  case="mixed" tab="{qaEventMeta:getIsBillable()},{qaEventMeta:getTypeId()}"/>
	 </widget>
	  </row>		
	  						
      <row>           
      <widget>
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"billable")'/>:</text>
        </widget>
        <widget>
          <check key= "{qaEventMeta:getIsBillable()}" tab="{qaEventMeta:getReportingSequence()},{qaEventMeta:getTestId()}"/>
       </widget>
      </row> 
      <row>
       
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>
     </widget>
     <widget>
      <textbox key= "{qaEventMeta:getReportingSequence()}"  width= "50px" tab="{qaEventMeta:getReportingText()},{qaEventMeta:getIsBillable()}"/>
     </widget>    
     </row>
			
	   <row>
	    <widget valign = "top"> 
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="400px" height="200px" case="mixed" key="{qaEventMeta:getReportingText()}" tab="{qaEventMeta:getName()},{qaEventMeta:getReportingSequence()}"/>
	    </widget> 
	   </row>								          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{qaEventMeta:getId()}" type="integer" required = "false" />
 <string key="{qaEventMeta:getName()}" max = "20" required = "true"/>
 <number key="{qaEventMeta:getReportingSequence()}"  type="integer" required = "false" />
 <string key="{qaEventMeta:getDescription()}" max = "60" required = "false" /> 	 
 <check key= "{qaEventMeta:getIsBillable()}" required = "false" />
 <string key="{qaEventMeta:getReportingText()}" required = "true"/>
 <dropdown key="{qaEventMeta:getTestId()}" type="integer" required = "false" />
 <dropdown key="{qaEventMeta:getTypeId()}" type="integer" required = "true"/>
</rpc>
					   
<rpc key= "query">     
 <queryString key="{qaEventMeta:getName()}" />
 <queryNumber key="{qaEventMeta:getReportingSequence()}" type="integer" />
 <queryString key="{qaEventMeta:getDescription()}"  /> 	
 <dropdown key="{qaEventMeta:getTypeId()}" type="integer"/> 
 <dropdown key="{qaEventMeta:testId()}" type="integer"/>
 <queryCheck key="{qaEventMeta:getIsBillable()}"/>
 <queryString key="{qaEventMeta:getReportingText()}"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{qaEventMeta:getName()}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 