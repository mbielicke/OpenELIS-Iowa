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
					<panel layout="horizontal" spacing="0"  xsi:type="Panel">
						<panel layout="vertical" spacing="0"  xsi:type="Panel">							
							<panel key="secMod" layout="table" style="Form"  xsi:type="Table">																							
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"catName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="category.name" tab="category.description,category.systemName"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="category.description" width="300px" tab="category.section,category.name"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									 </widget>
									 
									 
									  <!--<widget>
										<autoDropdown cat="section" key="category.section" case="lower" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px" multiSelect="false"  fromModel="false" type="integer" tab="category.systemName,category.description">
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
										</widget> -->
										<widget>
												   <autoDropdown cat="section" key="category.section" case="lower" width="100px" popWidth="auto" tab="category.systemName,category.description">
													 <widths>40</widths>
												   </autoDropdown>
												</widget>
								     							   						
								</row>	
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"systemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" key="category.systemName" tab="category.name,category.section"/>
									</widget>
								</row>						  
							</panel>
						</panel>
					</panel>
					<panel height="25px" layout="vertical"  xsi:type="Panel"/>
					<panel layout="vertical"  spacing="5" xsi:type="Panel">
						<!-- start TAB 1 data table -->
						<widget>
							<table maxRows = "6" cellHeight = "20" rows = "0" width = "480px" manager = "DictionaryEntriesTable" key="dictEntTable"  title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>
									<check/>									
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<auto cat="relatedEntry" key="relatedEntry" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" multiSelect="false" width="100px" popupHeight="50px"  type="integer">
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
									<string>Y</string>
									<string/>									
									<string/>
									<string required = "true"/>
									<number type="integer">0</number>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  <query>
						   	<table width = "480px" cellHeight = "20" maxRows = "5" rows="1" title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>65,95,75,110,100</widths>
								<editors>									
								    <check threeState="true"/>											
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<textbox case = "mixed"/>									
								</editors>
								<fields>									
									<queryString/>										
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
	 <number key="category.id" type="integer" required="false"/>	
	 <string key="category.systemName" max="30" required="true"/>
	 <string key="category.name" max="50" required="true"/>
	 <string key="category.description" max="60" required="false"/>
     <table key="dictEntTable"/>	 
     <dropdown key="category.section" type="integer" required="false"/>     
	</rpc>
	<rpc key = "query">	 
	 <table key="dictEntTable"/>	
	 <queryString key="category.systemName"/>
	 <queryString key="category.name"/>
	 <queryString key="category.description"/>
	 <dropdown key="category.section" type="integer" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="category.name"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 