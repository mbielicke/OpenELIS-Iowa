<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:categoryMeta="xalan://org.openelis.meta.CategoryMeta"
                xmlns:dictionaryMeta="xalan://org.openelis.meta.DictionaryMeta"
                xmlns:dictRelEntryMeta="xalan://org.openelis.meta.DictionaryRelatedEntryMeta"
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
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
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Dictionary" name="{resource:getString($constants,'dictionary')}" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="horizontal" style="WhiteContentPanel" spacing="0" xsi:type="Panel" >
			<!--left table goes here -->
																
  <aToZ height="425px" width="100%" key="hideablePanel" maxRows="19" title="{resource:getString($constants,'catName')}" tablewidth="auto"  colwidths ="175">   
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
										<textbox case="mixed" max="50" width="355px" key="{categoryMeta:getName()}" tab="{categoryMeta:getDescription()},{categoryMeta:getSystemName()}"/>
									</widget>
								</row>
								<row>
									<widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" max="60" key="{categoryMeta:getDescription()}" width="425px" tab="{categoryMeta:getSectionId()},{categoryMeta:getName()}"/>
									</widget>
								</row>
								<row>								
									 <widget>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									 </widget>
									 									 									  
									           <widget>
												   <autoDropdown key="{categoryMeta:getSectionId()}" case="lower" width="100px" tab="{categoryMeta:getSystemName()},{categoryMeta:getDescription()}"/>
												</widget>
								     							   						
								</row>	
								<row>
									<widget halign="right">
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"systemName")'/></text>
									</widget>
									<widget>
										<textbox case="mixed" max="30" width="215px" key="{categoryMeta:SystemName()}" tab="{categoryMeta:getName()},{categoryMeta:getSection()}"/>
									</widget>
								</row>						  							
						</panel>
					</panel>
					<panel height="10px" layout="vertical"  xsi:type="Panel"/>
					
					<panel layout="vertical"  spacing="3" xsi:type="Panel">
						<widget>
							<table maxRows = "11" width = "auto" manager = "DictionaryEntriesTable" key="dictEntTable"  title="" showError="false" showScroll="true">
								<headers><xsl:value-of select='resource:getString($constants,"active")'/>,<xsl:value-of select='resource:getString($constants,"systemName")'/>,
								         <xsl:value-of select='resource:getString($constants,"abbr")'/>, <xsl:value-of select='resource:getString($constants,"entry")'/>,
								         <xsl:value-of select='resource:getString($constants,"relEntry")'/></headers>
								<widths>45,105,85,120,130</widths>
								<editors>
									<check/>									
									<textbox max = "30"/>									
									<textbox max = "10"/>									
									<textbox/>																			
									<autoDropdown cat="relatedEntry" case="mixed" serviceUrl="OpenELISServlet?service=org.openelis.modules.dictionary.server.DictionaryService" width="100px">												
												<widths>123</widths>
											</autoDropdown>
								</editors>
								<fields>																											
									<check key="{dictionaryMeta:getIsActive()}">Y</check>
									<string key="{dictionaryMeta:getSystemName()}"/>									
									<string key="{dictionaryMeta:getLocalAbbrev()}"/>
									<string key="{dictionaryMeta:getEntry()}" required = "true"/>
									<dropdown key="{dictRelEntryMeta:getEntry()}"/>									
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
								<widths>45,105,85,120,148</widths>
								<editors>									
								    <check threeState="true"/>											
									<textbox/>									
									<textbox/>									
									<textbox/>										
									<textbox case = "mixed"/>									
								</editors>
								<fields>																		
									<xsl:value-of select='dictionaryMeta:isActive()'/>,<xsl:value-of select='dictionaryMeta:getSystemName()'/>,
		                            <xsl:value-of select='dictionaryMeta:localAbbrev()'/>,<xsl:value-of select='dictionaryMeta:getEntry()'/>,
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
	 <number key="{categoryMeta:getId()}" type="integer" required="false"/>	
	 <string key="{categoryMeta:getSystemName()}" max="30" required = "true"/>
	 <string key="{categoryMeta:getName()}" max="50" required = "true"/>
	 <string key="{categoryMeta:getDescription()}" max="60" required="false"/>
     <table key="dictEntTable"/>	 
     <dropdown key="{categoryMeta:getSectionId()}" type="integer" required="false"/>    
	</rpc>
	<rpc key = "query">	 		
     <table key="dictEntTable"/>	
	 <queryString key="{categoryMeta:getSystemName()}"/>
	 <queryString key="{categoryMeta:getName()}"/>
	 <queryString key="{categoryMeta:getDescription()}"/>
	 <dropdown key="{categoryMeta:getSectionId()}" type="integer" required="false"/> 
	 <queryCheck key="{dictionaryMeta:isActive()}" required="false"/>
	  <queryString key="{dictionaryMeta:getSystemName()}" required="false"/>
	  <queryString key="{dictionaryMeta:getLocalAbbrev()}" required="false"/>
	  <queryString key="{dictionaryMeta:getEntry()}" required="false"/>
	  <queryString key="{dictRelEntryMeta:getEntry()}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="{categoryMeta:getName()}"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 