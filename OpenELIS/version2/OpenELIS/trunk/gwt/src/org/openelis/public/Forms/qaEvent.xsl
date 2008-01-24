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
<screen id= "QAEvent" serviceUrl= "OpenElisService"
xsi:noNamespaceSchemaLocation= "file:///home/tschmidt/workspace/libraries/metadata/FormSchema.xsd"
xmlns:locale = "xalan:/java.util.Locale" xmlns:xalan= "http://xml.apache.org/xalan" xmlns:xsi= "http://www.w3.org/2001/XMLSchema-instance">
<display>
 <panel layout= "horizontal" width = "400px" spacing= "0" padding= "0" xsi:type= "Panel">  
  
  <panel layout= "vertical" xsi:type = "Panel">
   <panel layout= "vertical" spacing= "2" xsi:type= "Panel">
    <widget halign= "center">
      <buttonPanel key="buttons">
            <appButton action="query" toggle="true">
              <widget>
                <text>Query</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="previous">
              <widget>
                <text>Previous</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="next">
              <widget>
                <text>Next</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="add" toggle="true">
              <widget>
                <text>Add</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="update" toggle="true">
              <widget>
                <text>Update</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="commit" >
              <widget>
                <text>Commit</text>
              </widget>
            </appButton>
            <html>&lt;div style="width:1px;height:20px;background:grey"/&gt;</html>
            <appButton action="abort" >
              <widget>
                <text>Abort</text>
              </widget>
            </appButton>            
          </buttonPanel>
    </widget>
    <widget halign= "center">
     <appMessage key= "message"/>
    </widget>
   </panel>
   
   <panel key= "secMod" layout= "table" width= "450px" style= "Form" xsi:type= "Table">
    <row>
     <widget>
      <text style= "Prompt">Name</text>
     </widget>
     <widget width= "210px"> 
      <textbox key = "name" case = "upper" />
     </widget>
     <widget>
      <text style= "Prompt">Sequence</text>
     </widget>
     <widget>
      <textbox key= "sequence" case = "upper"   width= "50px"/>
     </widget>
     </row>
     <row>     
     <widget>
      <text style= "Prompt">Description</text>
     </widget>
     <widget>
      <textbox case= "mixed"   key= "description" width= "200px"/>
     </widget>     
    </row>
     <row>
     <widget>
      <text style= "Prompt">Type</text>
     </widget>
     <widget>
										<autoDropdown cat="qaEventType" key="qaEventType" case="mixed"  width="80px"   type="integer" >
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>	
												       <item value= "0"> </item>
									                   <item value= "1">Type1</item>														
									                   <item value= "2">Type2</item>														 																																			
												    </autoItems> 													
										</autoDropdown>
												<query>
												  <autoDropdown cat="qaEventType" case="mixed" serviceUrl="ProviderServlet" width="80px"  multiSelect="true"  type="integer" >
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>	
												       <item value= "0"> </item>
									                   <item value= "1">Type1</item>														
									                   <item value= "2">Type2</item>														 																																			
												    </autoItems> 
										          </autoDropdown>
												 </query>
										</widget>          

     <widget>
       <text style= "Prompt">Test</text>
     </widget>
     <widget>
										<autoDropdown cat="test" key="test" case="mixed"  width="80px"   type="integer" >
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													
										</autoDropdown>
												<query>
												  <autoDropdown cat="test" case="mixed"  width="80px"  multiSelect="true"  type="integer" >
													<autoWidths>60</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
													<autoItems>	
												       <item value= "0"> </item>
									                   <item value= "1">Test1</item>														
									                   <item value= "2">Test2</item>														 																																			
												    </autoItems> 
										          </autoDropdown>
												 </query>
										</widget>
										
            
      <widget>
        <text style= "Prompt">Billable</text>
        </widget>
        <widget>
         <check key= "billable"/>
       </widget>
									          
    </row>
                                   <row>
										<widget>
											<text style="Prompt">Reporting Text</text>
										</widget>										
                                  </row>										
                         <row>	
                             <widget>							
                    	       <textarea width="400px" height="50px" case="mixed" key="reportText"/>
							 </widget>
						</row>
    
    
   </panel>
  </panel>
 </panel>
</display>
							  
<rpc key= "display">	
 
</rpc>
					   
<rpc key= "query">     
 
</rpc>

 
</screen>
</xsl:template>
</xsl:stylesheet> 