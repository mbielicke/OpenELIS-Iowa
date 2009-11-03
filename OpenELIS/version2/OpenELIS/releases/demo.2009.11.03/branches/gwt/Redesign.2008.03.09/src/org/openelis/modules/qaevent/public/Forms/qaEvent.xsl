<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>
<xsl:import href="buttonPanel.xsl"/>

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
	<xsl:call-template name="buttonPanelTemplate">
		<xsl:with-param name="buttonsParam">qpn|au|cb</xsl:with-param>
	</xsl:call-template>
   
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
										<autoDropdown cat="qaEventType" key="qaEventType" serviceUrl="OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService" case="mixed"  width="100px" fromModel="true"  type="integer" tab="test,description">
													<autoWidths>80</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>	
												       <!--<item value= "0"> </item>
									                   <item value= "1">Type1</item>														
									                   <item value= "2">Type2</item>-->														 																																			
												    </autoItems> 													
										</autoDropdown>
												<query>
												  <autoDropdown cat="qaEventType" serviceUrl="OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService" case="mixed"  width="80px" fromModel="true" multiSelect="true"  type="integer" tab="test,description">
													<autoWidths>60</autoWidths>
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
       <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"test")'/>:</text>
     </widget>
     <widget>
										<autoDropdown cat="test" key="test" serviceUrl="OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService" case="mixed" fromModel="true" width="150px"   type="integer" tab="billable,qaEventType">
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
												  <autoDropdown cat="test" serviceUrl="OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService" case="mixed" fromModel="true" width="150px"  multiSelect="true"  type="integer" tab="billable,qaEventType">
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
         <query>
         <autoDropdown cat="billable" serviceUrl="OpenELISServlet?service=org.openelis.modules.qaevent.server.QAEventService" case="upper"  width="20px"  multiSelect="true"  type="string" tab="sequence,test">
													<autoWidths>10</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>	
													   <item value= " "> </item>
									                   <item value= "Y">Y</item>														
									                   <item value= "N">N</item>											       
												    </autoItems> 
										          </autoDropdown>
         </query>
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
 <check key= "billable" required = "false" />
 <string key="reportingText" required = "true" />
 <number key="testId" type="integer" required = "false" />
 <number key="qaEventTypeId" type="integer" required = "true" />
</rpc>
					   
<rpc key= "query">     
 <queryString key="name" />
 <queryNumber key="sequence" type="integer" />
 <queryString key="description"  /> 	
 <collection key="qaEventType" type="integer"/> 
 <collection key="test" type="integer"/>
 <collection key="billable" type="string"/>
 <queryString key="reportingText"  />
</rpc>

<rpc key= "queryByLetter">     
 <queryString key="name"/>
</rpc>
 
</screen>
</xsl:template>
</xsl:stylesheet> 