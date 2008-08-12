<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) The University of Iowa.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.metamap.CategoryMetaMap"
                xmlns:dictionary="xalan://org.openelis.metamap.DictionaryMetaMap"
                xmlns:relentry="xalan://org.openelis.meta.DictionaryMeta"                
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="aToZOneColumn.xsl"/>

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>
  
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.CategoryMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="dictionary">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMetaMap"/>
  </xalan:component>
  
  <xalan:component prefix="relentry">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.DictionaryMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
    <xsl:variable name="cat" select="meta:new()"/>    
    <xsl:variable name="dictNew" select="meta:getDictionary($cat)"/>
    <!--<xsl:variable name="dictNew" select="dictionary:new()"/>-->
    <xsl:variable name="rel" select="dictionary:getRelatedEntry($dictNew)"/>    
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="Dictionary" name="{resource:getString($constants,'dictionary')}" serviceUrl="OpenElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<HorizontalPanel style="WhiteContentPanel" spacing="0" xsi:type="Panel" >
			<!--left table goes here -->
			 		<CollapsePanel key="collapsePanel">
						<azTable colwidths ="175" height="425px" key="azTable" maxRows="19" tablewidth="auto" title="{resource:getString($constants,'catName')}" width="100%">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
					</CollapsePanel>
	<VerticalPanel spacing="0">
				<!--button panel code-->
		<AbsolutePanel spacing="0" style="ButtonPanelContainer">
			<widget>
    			<buttonPanel key="buttons">
								<xsl:call-template name="queryButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="previousButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="nextButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="buttonPanelDivider"/>
								<xsl:call-template name="addButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="updateButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="buttonPanelDivider"/>
								<xsl:call-template name="commitButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="abortButton">
									<xsl:with-param name="language">
										<xsl:value-of select="language"/>
									</xsl:with-param>
								</xsl:call-template>
							</buttonPanel>
 			</widget>
		</AbsolutePanel>
		<!--end button panel-->
			          		
						<VerticalPanel spacing="0"  xsi:type="Panel">							
							<TablePanel key="secMod" layout="table" style="Form"  xsi:type="Table">																							
								<row>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"catName")'/></text>
										<textbox case="mixed" max="50" width="355px" key="{meta:getName($cat)}" tab="{meta:getDescription($cat)},{meta:getSystemName($cat)}"/>
								</row>
								<row>
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"description")'/></text>
										<textbox case="mixed" max="60" key="{meta:getDescription($cat)}" width="425px" tab="{meta:getSectionId($cat)},{meta:getName($cat)}"/>
								</row>
								<row>								
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"section")'/></text>
									    <autoDropdown key="{meta:getSectionId($cat)}" case="lower" width="100px" tab="{meta:getSystemName($cat)},{meta:getDescription($cat)}"/>
								     							   						
								</row>	
								<row>									
										<text style="Prompt"><xsl:value-of select='resource:getString($constants,"systemName")'/></text>
										<textbox case="mixed" max="30" width="215px" key="{meta:getSystemName($cat)}" tab="{meta:getName($cat)},{meta:getSectionId($cat)}"/>									
								</row>						  							
						</TablePanel>
					</VerticalPanel>
					<VerticalPanel height="10px"/>
					
					<VerticalPanel  spacing="3" xsi:type="Panel">
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
									<check key="{dictionary:getIsActive($dictNew)}">Y</check>
									<string key="{dictionary:getSystemName($dictNew)}"/>									
									<string key="{dictionary:getLocalAbbrev($dictNew)}"/>
									<string key="{dictionary:getEntry($dictNew)}" required = "true"/>																		
									<dropdown key="{relentry:getEntry($rel)}"/>
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
									<xsl:value-of select='dictionary:getIsActive($dictNew)'/>,<xsl:value-of select='dictionary:getSystemName($dictNew)'/>,
		                            <xsl:value-of select='dictionary:getLocalAbbrev($dictNew)'/>,<xsl:value-of select='dictionary:getEntry($dictNew)'/>,		                            
									<xsl:value-of select='relentry:getEntry($rel)'/>									
								</fields>
								<sorts>true,true,true,true,true</sorts>
								<filters>false,false,false,false,false</filters>
								<colAligns>center,left,center,right,left</colAligns>
							</queryTable>
						  </query>						  
						</widget>								                
		                <widget halign = "center">
                            <appButton  action="removeEntry" onclick="this" key = "removeEntryButton" style="Button">
                            <HorizontalPanel>
              						<AbsolutePanel style="RemoveRowButtonImage"/>
                              <widget>
                                  <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
                               </widget> 
                               </HorizontalPanel>
                             </appButton>
                           </widget>	                
					</VerticalPanel>					
				</VerticalPanel>
		</HorizontalPanel>
	</display>
	<rpc key = "display">
	 <number key="{meta:getId($cat)}" type="integer" required="false"/>	
	 <string key="{meta:getSystemName($cat)}" max="30" required = "true"/>
	 <string key="{meta:getName($cat)}" max="50" required = "true"/>
	 <string key="{meta:getDescription($cat)}" max="60" required="false"/>
     <table key="dictEntTable"/>	 
     <dropdown key="{meta:getSectionId($cat)}" required="false"/>    
	</rpc>
	<rpc key = "query">	 		    	
	 <queryString key="{meta:getSystemName($cat)}"/>
	 <queryString key="{meta:getName($cat)}"/>
	 <queryString key="{meta:getDescription($cat)}"/>
	 <dropdown key="{meta:getSectionId($cat)}" required="false"/> 
	 <queryCheck key="{dictionary:getIsActive($dictNew)}" required="false"/>
	  <queryString key="{dictionary:getSystemName($dictNew)}" required="false"/>
	  <queryString key="{dictionary:getLocalAbbrev($dictNew)}" required="false"/>
	  <queryString key="{dictionary:getEntry($dictNew)}" required="false"/>
	  <dropdown key="{relentry:getEntry($rel)}" required="false"/>
	</rpc>
	<rpc key="queryByLetter">
      <queryString key="{meta:getName($cat)}"/>
    </rpc>
</screen>
</xsl:template>
</xsl:stylesheet> 
