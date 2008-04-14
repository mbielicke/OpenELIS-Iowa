<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:categoryMeta="xalan://org.openelis.meta.CategoryMeta"
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta"
                xmlns:dictRelEntryMeta="xalan://org.openelis.meta.DictionaryRelatedEntryMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="categoryMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.CategoryMeta"/>
  </xalan:component>
  
  <xalan:component prefix="dictionaryMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="Dictionary" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" xsi:type="Panel" >
			<!--left table goes here -->
																
  <aToZ height="425px" width="100%" key="hideablePanel" visible="false" maxRows="19" title="{resource:getString($constants,'catName')}" tablewidth="auto"  colwidths ="150">   
     	 <buttonPanel key="atozButtons">
	    	  <xsl:call-template name="aToZLeftPanelButtons"/>		
		 </buttonPanel>        
  </aToZ>
  
  
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
			          		
						<panel layout="vertical" spacing="0"  xsi:type="Panel">							
							<panel key="secMod" layout="table" style="Form"  xsi:type="Table">																							
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"catName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" max="50" key="{categoryMeta:name()}" tab="{categoryMeta:description()},{categoryMeta:systemName()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" max="60" key="{categoryMeta:description()}" width="300px" tab="{categoryMeta:section()},{categoryMeta:name()}"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									 </widget>
									 									 									  
									           <widget>
												   <autoDropdown key="{categoryMeta:section()}" case="lower" width="100px" popWidth="auto" tab="{categoryMeta:systemName()},{categoryMeta:description()}">
													 <widths>92</widths>
												   </autoDropdown>
												</widget>
								     							   						
								</row>	
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"systemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" max="30" key="{categoryMeta:systemName()}" tab="{categoryMeta:name()},{categoryMeta:section()}"/>
									</widget>
								</row>						  							
						</panel>
					</panel>
					<panel height="10px" layout="vertical"  xsi:type="Panel"/>
					
					<panel layout="vertical"  spacing="5" xsi:type="Panel">
						<widget>
							<table maxRows = "11" width = "auto" manager = "DictionaryEntriesTable" key="dictEntTable"  title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>
									<check/>									
									<textbox max = "30"/>									
									<textbox max = "10"/>									
									<textbox/>																			
									<autoDropdown cat="relatedEntry" case="upper" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px" popWidth="118px">												
												<widths>113</widths>
											</autoDropdown>
								</editors>
								<fields>																											
									<string>Y</string>
									<string/>									
									<string/>
									<string required = "true"/>
									<dropdown/>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</table>
						  <query>
						   	<queryTable width = "auto" maxRows = "11"  title="" showError="false">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>45,95,75,110,120</widths>
								<editors>									
								    <check threeState="true"/>											
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<textbox case = "mixed"/>									
								</editors>
								<fields>																		
									<xsl:value-of select='dictionaryMeta:isActive()'/>,<xsl:value-of select='dictionaryMeta:systemName()'/>,
		                            <xsl:value-of select='dictionaryMeta:localAbbrev()'/>,<xsl:value-of select='dictionaryMeta:entry()'/>,
		                            <xsl:value-of select='dictRelEntryMeta:entry()'/>
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</queryTable>
						  </query>						  
						</widget>								                
		                <widget halign = "center">
                            <appButton  action="removeEntry" onclick="this" key = "removeEntryButton" style="Button">
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
	 <number key="{categoryMeta:id()}" type="integer" required="false"/>	
	 <string key="{categoryMeta:systemName()}" max="30" required="true"/>
	 <string key="{categoryMeta:name()}" max="50" required="true"/>
	 <string key="{categoryMeta:description()}" max="60" required="false"/>
     <table key="dictEntTable"/>	 
     <dropdown key="{categoryMeta:section()}" type="integer" required="false"/>    
	</rpc>
	<rpc key = "query">	 		
	 <queryString key="{categoryMeta:systemName()}"/>
	 <queryString key="{categoryMeta:name()}"/>
	 <queryString key="{categoryMeta:description()}"/>
	 <dropdown key="{categoryMeta:section()}" type="integer" required="false"/> 
	 <queryString key="{dictionaryMeta:isActive()}" required="false"/>
	  <queryString key="{dictionaryMeta:systemName()}" required="false"/>
	  <queryString key="{dictionaryMeta:localAbbrev()}" required="false"/>
	  <queryString key="{dictionaryMeta:entry()}" required="false"/>
	  <queryString key="{dictRelEntryMeta:entry()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="{categoryMeta:name()}"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 