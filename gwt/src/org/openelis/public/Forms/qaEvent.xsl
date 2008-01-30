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
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.client.main.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id= "QAEvents" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" width = "400px" spacing= "0" padding= "0" xsi:type= "Panel">  
  <aToZ height= "425px" width = "auto" key= "hideablePanel" visible= "false" onclick= "this">
   <panel layout= "horizontal" xsi:type= "Panel" spacing= "0">
    <xsl:if test="string($language)='en'">
     <panel layout= "vertical" xsi:type= "Panel" spacing = "0"> 
      <widget> 
       <html key= "a" onclick= "this">&lt;a class='navIndex'&gt;A&lt;/a&gt;</html> 
      </widget>
      <widget>
       <html key= "b" onclick= "this">&lt;a class='navIndex'&gt;B&lt;/a&gt;</html> 
      </widget>
      <widget>
        <html key= "c" onclick= "this">&lt;a class='navIndex'&gt;C&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "d" onclick= "this">&lt;a class='navIndex'&gt;D&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "e" onclick= "this">&lt;a class='navIndex'&gt;E&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key = "f" onclick = "this">&lt;a class='navIndex'&gt;F&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "g" onclick = "this">&lt;a class='navIndex'&gt;G&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "h" onclick = "this">&lt;a class='navIndex'&gt;H&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "i" onclick = "this">&lt;a class='navIndex'&gt;I&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "j" onclick = "this">&lt;a  class='navIndex'&gt;J&lt;/a&gt;</html>
      </widget>
      <widget>
        <html key= "k" onclick = "this">&lt;a class='navIndex'&gt;K&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "l" onclick= "this">&lt;a class='navIndex'&gt;L&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "m" onclick= "this">&lt;a class='navIndex'&gt;M&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "n" onclick= "this">&lt;a class='navIndex'&gt;N&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "o" onclick= "this">&lt;a class='navIndex'&gt;O&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "p" onclick= "this">&lt;a class='navIndex'&gt;P&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "q" onclick= "this">&lt;a class='navIndex'&gt;Q&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "r" onclick= "this">&lt;a class='navIndex'&gt;R&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "s" onclick= "this">&lt;a class='navIndex'&gt;S&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "t" onclick= "this">&lt;a class='navIndex'&gt;T&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "u" onclick= "this">&lt;a class='navIndex'&gt;U&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "v" onclick= "this">&lt;a class='navIndex'&gt;V&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "w" onclick= "this">&lt;a class='navIndex'&gt;W&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "x" onclick= "this">&lt;a class='navIndex'&gt;X&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "y" onclick= "this">&lt;a class='navIndex'&gt;Y&lt;/a&gt;</html>
      </widget>
      <widget>
       <html key= "z" onclick= "this">&lt;a class='navIndex'&gt;Z&lt;/a&gt;</html>
      </widget>
    </panel>
    </xsl:if>
    
    <panel layout= "vertical" width = "175px" xsi:type= "Panel" >
      <table maxRows = "20" rows = "0" width= "auto" key = "qaEventsTable" manager = "QAEventsNamesTable"  serviceUrl = "QAEventServlet"  title="{resource:getString($constants,'qaEvents')}">
       <headers><xsl:value-of select='resource:getString($constants,"name")'/></headers>
							<widths>150</widths>
							<editors>
								<label/>								
							</editors>
							<fields>
								<string/>								
							</fields>
							<sorts>false</sorts>
							<filters>false</filters>
     </table>
    </panel>
   </panel>
  </aToZ>
  
  <panel layout= "vertical" xsi:type = "Panel">
   <panel layout= "vertical" spacing= "2" xsi:type= "Panel">
    <widget halign="center">						
		<buttonPanel key="buttons">
            <appButton action="query" toggle="true">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"query")'/></text>
              </widget>
            </appButton>            
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="previous">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"previous")'/></text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="next">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"next")'/></text>
              </widget>
            </appButton>
            <appButton action="add" toggle="true">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"add")'/></text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="update" toggle="true">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"update")'/></text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="commit">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"commit")'/></text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="abort">
              <widget>
                <text><xsl:value-of select='resource:getString($constants,"abort")'/></text>
              </widget>
            </appButton>
            
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
     <widget>
      <text style= "Prompt"><xsl:value-of select='resource:getString($constants,"sequence")'/>:</text>
     </widget>
     <widget>
      <textbox key= "sequence"  width= "50px"/>
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
   </panel>
       <panel layout= "vertical" height = "20px" xsi:type = "Panel"/>            
       <widget halign = "center">
		  <textarea width="500px" height="200px" case="mixed" key="reportingText"/>
	   </widget>                      
						
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