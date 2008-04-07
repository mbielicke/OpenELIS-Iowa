<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:qaEventMeta="xalan://org.openelis.meta.QaEventMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="qaEventMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.QaEventMeta"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "QAEvents" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height="425px" width="100%" key="hideablePanel" visible="false" maxRows="19" title = "" tablewidth="auto" headers = "{resource:getString($constants,'name')},{resource:getString($constants,'test')},{resource:getString($constants,'method')}" colwidths ="100,65,65">   
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
      <textbox key = "{qaEventMeta:name()}" max = "20" width= "150px" case = "lower" tab="{qaEventMeta:description()},{qaEventMeta:reportingText()}"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed" max = "60"  key= "{qaEventMeta:description()}" width= "300px" tab="{qaEventMeta:type()},{qaEventMeta:name()}"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>
     <widget>
										<autoDropdown key="{qaEventMeta:type()}" width = "90px" case="mixed" popWidth="auto"  multiSelect="false" fromModel="true"  type="integer" tab="{qaEventMeta:test()},{qaEventMeta:description()}">
													
													 <widths>100</widths>													 													
										</autoDropdown>
												<query>
												  <autoDropdown case="mixed" popWidth="auto"  fromModel="true" multiSelect="true"  type="integer" tab="{qaEventMeta:test()},{qaEventMeta:description()}">
													
													<widths>100</widths>													 
										          </autoDropdown>
												 </query>
										</widget>  
										        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
     </widget>
     <widget>
										<autoDropdown key="{qaEventMeta:test()}" width = "140px"  popWidth="auto" case="mixed" multiSelect="false" fromModel="true"    type="integer" tab="{qaEventMeta:isBillable()},{qaEventMeta:type()}">													
													<widths>135</widths>													 													
										</autoDropdown>
												<query>
												  <autoDropdown  case="mixed" popWidth="auto" fromModel="true"   multiSelect="true"  type="integer" tab="{qaEventMeta:isBillable()},{qaEventMeta:type()}">													
													<widths>135</widths>												
										          </autoDropdown>
												 </query>
										</widget>
	  </row>		
	  						
      <row>           
      <widget>
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"billable")'/>:</text>
        </widget>
        <widget>
          <check key= "{qaEventMeta:isBillable()}" tab="{qaEventMeta:reportingSequence()},{qaEventMeta:test()}"/>
       </widget>
      </row> 
      <row>
       
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>
     </widget>
     <widget>
      <textbox key= "{qaEventMeta:reportingSequence()}"  width= "50px" tab="{qaEventMeta:reportingText()},{qaEventMeta:isBillable()}"/>
     </widget>    
     </row>
			
	   <row>
	    <widget valign = "top"> 
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="400px" height="200px" case="mixed" key="{qaEventMeta:reportingText()}" tab="{qaEventMeta:name()},{qaEventMeta:reportingSequence()}"/>
	    </widget> 
	   </row>								          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="{qaEventMeta:id()}" type="integer" required = "false" />
 <string key="{qaEventMeta:name()}" max = "20" required = "true" />
 <number key="{qaEventMeta:reportingSequence()}"  type="integer" required = "false" />
 <string key="{qaEventMeta:description()}" max = "60" required = "false" /> 	 
 <string key= "{qaEventMeta:isBillable()}" required = "false" />
 <string key="{qaEventMeta:reportingText()}" required = "true" />
 <dropdown key="{qaEventMeta:test()}" type="integer" required = "false" />
 <dropdown key="{qaEventMeta:type()}" type="integer" required = "true" />
</rpc>
					   
<rpc key= "query">     
 <queryString key="{qaEventMeta:name()}" />
 <queryNumber key="{qaEventMeta:reportingSequence()}" type="integer" />
 <queryString key="{qaEventMeta:description()}"  /> 	
 <dropdown key="{qaEventMeta:type()}" type="integer"/> 
 <dropdown key="{qaEventMeta:test()}" type="integer"/>
 <queryString key="{qaEventMeta:isBillable()}"/>
 <queryString key="{qaEventMeta:reportingText()}"/>
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="{qaEventMeta:name()}"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 