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
<screen id="Dictionary" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" xsi:type="Panel">
			<!--left table goes here -->
																
  <aToZ height= "425px" width="100%" key= "hideablePanel" visible= "false">
   <panel layout= "horizontal" xsi:type= "Panel" style="ScreenLeftPanel" spacing= "0">
    <xsl:if test="string($language)='en'">
     	<xsl:call-template name="aToZLeftPanelButtons"/>
    </xsl:if>
   
      <table maxRows = "19" rows = "0" width= "auto" key = "categoryTable" manager = "CategorySystemNamesTable" title="{resource:getString($constants,'catName')}" showError="false">
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
  </aToZ>
  
  
				<!-- end left table -->
				<panel layout="vertical" spacing="0" xsi:type="Panel">
				<!--button panel code-->
				<xsl:call-template name="buttonPanelTemplate">
						<xsl:with-param name="buttonsParam">qpn|au|cb</xsl:with-param>
				</xsl:call-template>
		
		          <panel layout= "vertical" height = "5px" xsi:type= "Panel"/>
					<panel layout="horizontal" spacing="0"  xsi:type="Panel">
						<panel layout="vertical" spacing="0"  xsi:type="Panel">
							<!-- first vertical panel for org fields -->
							<panel key="secMod" layout="table" style="Form"  xsi:type="Table">																							
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"catName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="name" tab="desc,systemName"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="desc" width="300px" tab="section,name"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									 </widget>
									 
									 
									  <widget>
										<autoDropdown cat="section" key="section" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px"   fromModel="true" type="integer" tab="systemName,desc">
													<autoWidths>80</autoWidths>
													<autoEditors>
														<label/>
													</autoEditors>
													<autoFields>
														<string/>
													</autoFields>
										</autoDropdown>
													<query>
													
												  <autoDropdown cat="section" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px" multiSelect="true"  fromModel="true" type="integer" >
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
								     							   						
								</row>	
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"systemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="systemName" tab="name,section"/>
									</widget>
								</row>						  
							</panel>
						</panel>
					</panel>
					<panel height="25px" layout="vertical"  xsi:type="Panel"/>
					<panel layout="vertical"  spacing="5" xsi:type="Panel">
						<!-- start TAB 1 data table -->
						<widget>
							<table maxRows = "6" rows = "0" width = "480px" manager = "DictionaryEntriesTable" key="dictEntTable"  title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>
									<check/>									
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<auto cat="relatedEntry" key="relatedEntry" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px" popupHeight="50px"  type="integer">
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
									<string />									
									<string/>
									<string required = "true"/>
									<number type="integer">0</number>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  <query>
						   	<table width = "480px" maxRows = "5" rows="1" title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>65,95,75,110,100</widths>
								<editors>									
									<autoDropdown cat="isActive" key="isActive" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="40px" fromModel = "false" popupHeight="80px" dropdown="true"  multiSelect="true" type="string">
												<autoWidths>40</autoWidths>
												<autoEditors>
													<label/>
												</autoEditors>
												<autoFields>
													<string/>
												</autoFields>
												<!--<autoItems>
												 <item value= ""> </item>
									             <item value= "Y">Y</item>														
									             <item value= "N">N</item>													 																																			
												</autoItems>-->
										</autoDropdown>																
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<textbox case = "mixed"/>									
								</editors>
								<fields>									
									<collection type = "string"/>										
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
		                <widget halign = "left">
                            <appButton  action="removeEntry" onclick="this" key = "removeEntryButton">
                            <panel xsi:type="Panel" layout="horizontal">
              						<panel xsi:type="Absolute" layout="absolute" style="RemoveRowButtonImage"/>
                              <widget>
                                  <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
                               </widget> 
                               </panel>
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
     <number key="sectionId" type="integer" required="false"/>     
	</rpc>
	<rpc key = "query">	 
	 <table key="dictEntTable"/>	
	 <queryString key="systemName"/>
	 <queryString key="name"/>
	 <queryString key="desc"/>
	 <collection key="section" type="integer" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="name"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 