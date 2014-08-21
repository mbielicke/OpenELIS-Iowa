<!--
Exhibit A - UIRF Open-source Based Public Software License.

The contents of this file are subject to the UIRF Open-source Based
Public Software License(the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at
openelis.uhl.uiowa.edu

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
License for the specific language governing rights and limitations
under the License.

The Original Code is OpenELIS code.

The Initial Developer of the Original Code is The University of Iowa.
Portions created by The University of Iowa are Copyright 2006-2008. All
Rights Reserved.

Contributor(s): ______________________________________.

Alternatively, the contents of this file marked
"Separately-Licensed" may be used under the terms of a UIRF Software
license ("UIRF Software License"), in which case the provisions of a
UIRF Software License are applicable instead of those above. 
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"                
                xmlns:locale="xalan://java.util.Locale"
                xmlns:meta="xalan://org.openelis.metamap.PanelMetaMap"  
                xmlns:panelItem="xalan://org.openelis.metamap.PanelItemMetaMap"              
                extension-element-prefixes="resource"
                version="1.0">
	<xsl:import href="aToZTwoColumns.xsl"/>
	<xalan:component prefix="resource">
		<xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
	</xalan:component>
	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
	</xalan:component>
	<xalan:component prefix="meta">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.PanelMetaMap"/>
	</xalan:component>
	<xalan:component prefix="panelItem">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.PanelItemMetaMap"/>
	</xalan:component>	
	
	
	<xsl:template match="doc">	
	   <xsl:variable name="panel" select="meta:new()"/>
	   <xsl:variable name="pi" select="meta:getPanelItem($panel)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Panel" name="{resource:getString($constants,'panel')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="225px" style="LeftSidePanel">
					  <VerticalPanel>	
						<azTable colwidths="175"  key="azTable" maxRows="10" tablewidth="auto" headers="{resource:getString($constants,'panel')}" width="100%">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
						<VerticalPanel height = "80px"/>
					  </VerticalPanel>	
					</CollapsePanel>					
					<VerticalPanel spacing="0" padding="0">
						<!--button panel code-->
						<AbsolutePanel spacing="0" style="ButtonPanelContainer">
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
								<xsl:call-template name="deleteButton">
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
							
						</AbsolutePanel>
						<!--end button panel-->						
						<VerticalPanel spacing="0" padding="0" style="WhiteContentPanel">				
							<TablePanel style="Form">									
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>								  	
								  <textbox key="{meta:getName($panel)}" case = "mixed" tab="{meta:getDescription($panel)},{meta:getDescription($panel)}" max="20" width="145px"/>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>									
									<textbox  key="{meta:getDescription($panel)}" tab="{meta:getName($panel)},{meta:getName($panel)}" case = "mixed" max="60" width="425px"/>									
								</row>														
						     </TablePanel>	
						  <VerticalPanel height = "10px"/>   
						  <HorizontalPanel>
						      						                                    
                              <VerticalPanel style="Form">
                                <!--<text ><xsl:value-of select="resource:getString($constants,'testsThisPanel')"/></text>-->
                               <widget valign="top"> 
                                <table key="addedTestTable" manager="this" maxRows="8" showError="false" showScroll="ALWAYS" title="" width="auto">												
												<headers> 
												 <xsl:value-of select="resource:getString($constants,'test')"/>,
												 <xsl:value-of select="resource:getString($constants,'method')"/>												 
												</headers>
												<widths>120,120</widths>
												<editors>
													<label/>
													<label/>																								
												</editors>
												<fields>
													<string key="{panelItem:getTestName($pi)}" required="true"/>
													<string key="{panelItem:getMethodName($pi)}" required="true"/>													
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
							    </table>
							    <query>
								 <queryTable maxRows="8" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>
												 <xsl:value-of select="resource:getString($constants,'test')"/>,
												 <xsl:value-of select="resource:getString($constants,'method')"/>														 																							
												</headers>
												<widths>120,120</widths>
												<editors>
													<textbox case="mixed"/>
													<textbox case="mixed"/>																									
												</editors>
												<fields>
												    <xsl:value-of select='panelItem:getTestName($pi)'/>,<xsl:value-of select='panelItem:getMethodName($pi)'/>																																																	
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
								 </queryTable>
								</query>
							   </widget>
							 
                             </VerticalPanel>
                             <HorizontalPanel width = "10px"/>                             						         
						         <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="removeRow" onclick="this" style="Button" key="addTestButton">									        
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"moveTestLeft")'/> </text>
							                 </widget>							               
						                 </appButton>
						        </widget>  
						       <HorizontalPanel width = "10px"/> 
						      <VerticalPanel style="Form" >						      
						      <!--<text ><xsl:value-of select="resource:getString($constants,'allTests')"/></text>-->
                               <table key="allTestsTable" manager="this" maxRows="8" showError="false" showScroll="ALWAYS" title="" width="auto">												
												<headers> 
												 <xsl:value-of select="resource:getString($constants,'test')"/>,
												 <xsl:value-of select="resource:getString($constants,'method')"/>,
												 <xsl:value-of select="resource:getString($constants,'section')"/>
												</headers>
												<widths>78,78,78</widths>
												<editors>												
													<label/>
													<label/>	
													<label/>
												</editors>
												<fields>
													<string key="test"/>
													<string key="method"/>
													<string key="section"/>													
												</fields>
												<sorts>true,true,true</sorts>
												<filters>false,false,false</filters>
												<colAligns>left,left,left</colAligns>
								</table>
                              </VerticalPanel> 
						   </HorizontalPanel>	
						   <HorizontalPanel>	
						   <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="removeTest" onclick="this" style="Button" key="removeTestButton">									        
						                    <HorizontalPanel>
											 <AbsolutePanel style="RemoveRowButtonImage"/>						                      
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </HorizontalPanel> 						               
						                 </appButton>
						         </widget>
						       <HorizontalPanel width = "2px"/>
						       <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="moveUp" onclick="this" style="Button" key="moveUpButton">									       
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"moveUp")'/>   </text>
							                 </widget>							              
						                 </appButton>
						         </widget>
						         <HorizontalPanel width = "3px"/>						         
						         <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="moveDown" onclick="this" style="Button" key="moveDownButton">									        
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"moveDown")'/> </text>
							                 </widget>							               
						                 </appButton>
						        </widget>       						        						         
						     </HorizontalPanel>					   
						</VerticalPanel>
					</VerticalPanel>					
				</HorizontalPanel>				
			</display>
			<rpc key="display">		
			 <integer key="{meta:getId($panel)}" type = "integer" required="false" />	 
			 <string key="{meta:getName($panel)}" max = "20" required="true" />			 
			 <string key="{meta:getDescription($panel)}" max="60" required="false"/>		 			 					
		     <table key = "addedTestTable"/>
		   </rpc>
		   <rpc key="query">			 
			 <queryString key="{meta:getName($panel)}"/>			 
			 <queryString key="{meta:getDescription($panel)}"/>	
			 <queryString key="{panelItem:getTestName($pi)}"/>		 			 							
			 <queryString key="{panelItem:getMethodName($pi)}"/>
			 <model key = "addedTestTable"/>
		    </rpc>
			<rpc key="queryByLetter">
			 <queryString key="{meta:getName($panel)}"/>				
			</rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>
