<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "QAEvents" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height= "425px" width = "100%" key= "hideablePanel" visible= "false">
   <panel layout= "horizontal" style="ScreenLeftPanel" xsi:type= "Panel" spacing= "0">
    <xsl:if test="string($language)='en'">
     	<xsl:call-template name="aToZLeftPanelButtons"/>
		</xsl:if>
		
      <table maxRows = "20" rows = "0" width= "auto" key = "qaEventsTable" manager = "QAEventsNamesTable" title="" showError="false">
       <headers><xsl:value-of select='resource:getString($constants,"name")'/>,<xsl:value-of select='resource:getString($constants,"test")'/>,<xsl:value-of select='resource:getString($constants,"method")'/></headers>
							<widths>100,65,65</widths>
							<editors>
								<label/>
								<label/>	
								<label/>									
							</editors>
							<fields>
								<string/>
								<string/>
								<string/>								
							</fields>
							<sorts>false,false,false</sorts>
							<filters>false,false,false</filters>
     </table>
   </panel>
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
      <textbox key = "name" case = "lower" tab="description,reportingText"/>
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "description" width= "200px" tab="qaEventType,name"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>
     <widget>
										<autoDropdown cat="qaEventType" key="qaEventType" serviceUrl="OpenELISServlet?service=org.openelis.modules.analysis.server.QAEventServlet" case="mixed"  width="100px" multiSelect="false" fromModel="true"  type="integer" tab="test,description">
													<autoWidths>80</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>													 													
										</autoDropdown>
												<query>
												  <autoDropdown cat="qaEventType" serviceUrl="OpenELISServlet?service=org.openelis.modules.analysis.server.QAEventServlet" case="mixed"  width="80px" fromModel="true" multiSelect="true"  type="integer" tab="test,description">
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>													 
										          </autoDropdown>
												 </query>
										</widget>  
										        
     </row>
     <row>
     <widget>
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
     </widget>
     <widget>
										<autoDropdown cat="test" key="test" serviceUrl="OpenELISServlet?service=org.openelis.modules.analysis.server.QAEventServlet" case="mixed" multiSelect="false" fromModel="true" width="150px"   type="integer" tab="billable,qaEventType">
													<autoWidths>130</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													 <autoItems>													       
												     </autoItems>													
										</autoDropdown>
												<query>
												  <autoDropdown cat="test" serviceUrl="OpenELISServlet?service=org.openelis.modules.analysis.server.QAEventServlet" case="mixed" fromModel="true" width="150px"  multiSelect="true"  type="integer" tab="billable,qaEventType">
													<autoWidths>130</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>													       
												    </autoItems> 
										          </autoDropdown>
												 </query>
										</widget>
	  </row>		
	  						
      <row>           
      <widget>
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"billable")'/>:</text>
        </widget>
        <widget>
         <check key= "billable" tab="sequence,test"/>
       </widget>
      </row> 
      <row>
       
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>
     </widget>
     <widget>
      <textbox key= "sequence"  width= "50px" tab="reportingText,billable"/>
     </widget>    
     </row>
			
	   <row>
	    <widget>
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="300px" height="200px" case="mixed" key="reportingText" tab="name,sequence"/>
	    </widget> 
	   </row>								          
                                 					                         
   </panel>            
                            
						
  </panel>
 </panel>
</display>
							  
<rpc key= "display">
 <number key="qaeId" type="integer" required = "false" />
 <string key="name" required = "true" />
 <number key="sequence" type="integer" required = "false" />
 <string key="description" required = "false" /> 	 
 <string key= "billable" required = "false" />
 <string key="reportingText" required = "true" />
 <dropdown key="test" required = "false" />
 <dropdown key="qaEventType" required = "true" />
</rpc>
					   
<rpc key= "query">     
 <queryString key="name" />
 <queryNumber key="sequence" type="integer" />
 <queryString key="description"  /> 	
 <dropdown key="qaEventType"/> 
 <dropdown key="test"/>
 <queryString key="billable"/>
 <queryString key="reportingText"  />
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="name"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 