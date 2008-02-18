<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">
                
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.client.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "QAEvents" serviceUrl= "OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" spacing= "0" padding= "0" style="WhiteContentPanel" xsi:type= "Panel">  
  <aToZ height= "425px" width = "100%" key= "hideablePanel" visible= "false" onclick= "this">
   <panel layout= "horizontal" style="ScreenLeftPanel" xsi:type= "Panel" spacing= "0">
    <xsl:if test="string($language)='en'">
    <panel layout="horizontal" xsi:type="Panel" spacing="0" padding="0" style="AtoZ">
     <panel layout="vertical" xsi:type="Panel" spacing="0">
      <widget>
            <appButton key="a" action="a" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>A</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="b" action="b" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>B</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="c" action="c" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>C</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="d" action="d" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>D</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="e" action="e" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>E</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="f" action="f" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>F</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="g" action="g" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>G</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="h" action="h" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>H</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="i" action="i" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>I</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="j" action="j" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>J</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="k" action="k" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>K</text>
              </widget>
            </appButton>            
          </widget>
          <widget>
            <appButton key="l" action="l" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>L</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="m" action="m" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>M</text>
              </widget>
            </appButton>
            </widget>
          </panel>
         <panel layout="vertical" xsi:type="Panel" spacing="0">
          <widget>
            <appButton key="n" action="n" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>N</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="o" action="o" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>O</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="p" action="p" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>P</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="q" action="q" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>Q</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="r" action="r" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>R</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="s" action="s" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>S</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="t" action="t" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>T</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="u" action="u" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>U</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="v" action="v" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>V</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="w" action="w" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>W</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="x" action="x" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>X</text>
              </widget>
            </appButton>		  
          </widget>
          <widget>
            <appButton key="y" action="y" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>Y</text>
              </widget>
            </appButton>
          </widget>
          <widget>
            <appButton key="z" action="z" toggle="true" alwaysEnabled="true" onclick="this">
              <widget>
                <text>Z</text>
              </widget>
            </appButton>
          </widget>
          </panel>
</panel>
		</xsl:if>
		<xsl:if test="string($language)='cn'">

		</xsl:if>	
		<xsl:if test="string($language)='fa'">

		</xsl:if>
		<xsl:if test="string($language)='sp'">
		
		</xsl:if>
      <table maxRows = "20" rows = "0" width= "auto" key = "qaEventsTable" manager = "QAEventsNamesTable"  serviceUrl = "QAEventServlet"  title="">
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
   	<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer">
    <widget>						
		<buttonPanel key="buttons">
      <appButton action="query" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="QueryButtonImage"/>
              <widget>
                <text>Query</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="prev" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="PreviousButtonImage"/>
              <widget>
                <text>Previous</text>
              </widget>
              </panel>
            </appButton>
 <appButton action="next" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="NextButtonImage"/>
              <widget>
                <text>Next</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="add" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AddButtonImage"/>
              <widget>
                <text>Add</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="update" toggle="true">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="UpdateButtonImage"/>
              <widget>
                <text>Update</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonDivider"/>
            <appButton action="commit">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="CommitButtonImage"/>
              <widget>
                <text>Commit</text>
              </widget>
              </panel>
            </appButton>
            <appButton action="abort">
              <panel xsi:type="Panel" layout="horizontal">
              <panel xsi:type="Absolute" layout="absolute" style="AbortButtonImage"/>
              <widget>
                <text>Abort</text>
              </widget>
              </panel>
            </appButton>
            <panel xsi:type="Absolute" layout="absolute" style="ButtonSpacer"/>
          </buttonPanel>						
	</widget>
					
   </panel>
   
  <panel key = "qafields" layout= "table" width= "450px"   style="FormBorderless" xsi:type= "Table">
    <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"name")'/>:</text>
     </widget>
     <widget width= "210px"> 
      <textbox key = "name" case = "lower" />
     </widget>
     </row>     
     <row>     
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"description")'/>:</text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "description" width= "200px"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
     </widget>
     <widget>
										<autoDropdown cat="qaEventType" key="qaEventType" serviceUrl= "QAEventServlet" case="mixed"  width="100px" fromModel="true"  type="integer" >
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
												  <autoDropdown cat="qaEventType" serviceUrl= "QAEventServlet" case="mixed"  width="80px" fromModel="true" multiSelect="true"  type="integer" >
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
										<autoDropdown cat="test" key="test" serviceUrl= "QAEventServlet" case="mixed" fromModel="true" width="150px"   type="integer" >
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
												  <autoDropdown cat="test" serviceUrl= "QAEventServlet" case="mixed" fromModel="true" width="150px"  multiSelect="true"  type="integer" >
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
        <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"qae_Billable")'/>:</text>
        </widget>
        <widget>
         <check key= "billable"/>
         <query>
         <autoDropdown cat="billable" serviceUrl= "QAEventServlet" case="upper"  width="20px"  multiSelect="true"  type="string" >
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
      <textbox key= "sequence"  width= "50px"/>
     </widget>    
     </row>
			
	   <row>
	    <widget>
		   <text style="Prompt"><xsl:value-of select='resource:getString($constants,"text")'/>:</text>
		</widget>
	    <widget halign = "center">
		  <textarea width="300px" height="200px" case="mixed" key="reportingText"/>
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
 <number key="qaEventTypeId" type="integer" required = "false" />
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