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
<screen id="Dictionary" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" spacing="5" xsi:type="Panel">
			<!--left table goes here -->
																
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
      <table maxRows = "20" rows = "0" width= "auto" key = "categoryTable" serviceUrl= "DictionaryServlet" manager = "CategorySystemNamesTable" title="{resource:getString($constants,'dic_Categories')}">
       <headers><xsl:value-of select='resource:getString($constants,"dic_SystemName")'/></headers>
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
  
  
				<!-- end left table -->
				<panel layout="vertical" spacing="5" width="500px" xsi:type="Panel">
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
					<panel layout="horizontal" spacing="5" width="200px" xsi:type="Panel">
						<panel layout="vertical" spacing="5" width="200px" xsi:type="Panel">
							<!-- first vertical panel for org fields -->
							<panel key="secMod" layout="table" style="Form" width="200px" xsi:type="Table">																							
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"name")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="name"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="desc" width="300px"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									 </widget>
									 
									 <!--<panel layout="horizontal" spacing="5" width="150px" xsi:type="Panel">-->
									  <!--<widget>
									  	<textbox case="mixed" key="secName"/>
									  	<option key="secName" multi= "false" required= "false" onChange= "this"/>
									  </widget>-->
									  <widget>
										<autoDropdown cat="section" key="secName" case="lower" serviceUrl="DictionaryServlet" width="100px"   fromModel="true" type="integer" >
													<autoWidths>80</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
										</autoDropdown>
													<query>
													<!--<option/>-->
												  <autoDropdown cat="section" case="lower" serviceUrl="DictionaryServlet" width="100px"   fromModel="true" type="integer" >
													<autoWidths>80</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
										</autoDropdown>
												</query>
										</widget>
								      <!--<widget>
								 			<html key="lookupParentOrganizationHtml" onclick="this">&lt;img src=&quot;Images/lookupButtonIcon.png&quot;&gt;</html>
									  </widget>	-->									  
                                <!-- </panel> -->								   						
								</row>	
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"dic_SystemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="systemName"/>
									</widget>
								</row>						  
							</panel>
						</panel>
					</panel>
					<panel height="25px" layout="vertical"  xsi:type="Panel"/>
					<panel layout="vertical" spacing="5" xsi:type="Panel">
						<!-- start TAB 1 data table -->
						<widget>
							<table maxRows = "6" rows = "0" width = "auto" manager = "DictionaryEntriesTable" key="dictEntTable"  title="{resource:getString($constants,'dic_Entries')}">
								<headers><xsl:value-of select='resource:getString($constants,"dic_Active")'/>,<xsl:value-of select='resource:getString($constants,"dic_SystemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_Abbr")'/>, <xsl:value-of select='resource:getString($constants,"dic_Entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_RelEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>
									<check/>									
									<textbox/>									
									<textbox/>									
									<textbox/>	
									<!--<textbox/>-->
									<auto cat="relatedEntry" key="relatedEntry" case="mixed" serviceUrl="DictionaryServlet" width="100px" popupHeight="50px"  type="integer">
												<autoWidths>100</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>												
									</auto>																										
								</editors>
								<fields>
									<check/>																		
									<string/>									
									<string/>
									<string/>
									<number type="integer">0</number>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  <query>
						   	<table width = "auto" maxRows = "5" rows="1" title="">
								<headers><xsl:value-of select='resource:getString($constants,"dic_Active")'/>,<xsl:value-of select='resource:getString($constants,"dic_SystemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_Abbr")'/>, <xsl:value-of select='resource:getString($constants,"dic_Entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"dic_RelEntry")'/></headers>
								<widths>65,95,75,110,100</widths>
								<editors>									
									<autoDropdown cat="isActive" key="isActive" case="upper" serviceUrl="DictionaryServlet" width="40px" popupHeight="80px" dropdown="true"  multiSelect="true" type="string">
												<autoWidths>40</autoWidths>
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
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<textbox case = "mixed"/>									
								</editors>
								<fields>									
									<collection/>										
									<queryString/>									
									<queryString/>
									<queryString/>
									<queryString/>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  </query>						  
						</widget>
						<panel layout= "horizontal" xsi:type= "Panel" height= "5px"/>						
		                <!--<button halign= "right" action = "this" onclick = "this" key= "removeEntryButton" style= "ScreenButtonPanel" html= "&lt;img src=&quot;Images/deleteButtonIcon.png&quot;&gt;{resource:getString($constants,'removeRow')}"/>	-->
		                <widget halign = "right">
                            <appButton  action="remove" key = "removeEntryButton">
                              <widget>
                                  <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
                               </widget> 
                             </appButton>
                           </widget>	                
					</panel>					
				</panel>
		</panel>
	</display>
	<rpc key = "display">
	 <number key="categoryId" type="integer" required="false"/>	
	 <string key="systemName" max="30" required="true"/>
	 <string key="name" max="50" required="true"/>
	 <string key="desc" max="60" required="false"/>
     <table key="dictEntTable"/>	 
     <number key="secNameId" type="integer" required="false"/>
	</rpc>
	<rpc key = "query">	 
	 <table key="dictEntTable"/>	
	 <queryString key="systemName"/>
	 <queryString key="name"/>
	 <queryString key="desc"/>
	 <collection key="secName" type="integer"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="systemName"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 