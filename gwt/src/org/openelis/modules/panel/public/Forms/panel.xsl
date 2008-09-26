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
                xmlns:meta="xalan://org.openelis.metamap.PanelMetaMap"  
                xmlns:panelItem="xalan://org.openelis.metamap.PanelItemMetaMap"              
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.PanelMetaMap"/>
	</xalan:component>
	<xalan:component prefix="panelItem">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.PanelItemMetaMap"/>
	</xalan:component>	
	
	<xsl:preserve-space elements='resource:getString($constants,"moveUp")'/>
	<xsl:preserve-space elements='resource:getString($constants,"moveDown")'/>
	
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
				<HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="450px">
						<azTable colwidths="175"  key="azTable" maxRows="20" tablewidth="auto" title="{resource:getString($constants,'panel')}" width="100%">
							<buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
							</buttonPanel>
						</azTable>
					</CollapsePanel>					
					<VerticalPanel spacing="0">
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
						<VerticalPanel width = "10px"/>
						<VerticalPanel>
						<VerticalPanel>						
							<TablePanel style="Form">									
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget colspan = "6">	
									<textbox key="meta:getName($panel)" case = "mixed"  max="20" width="145px"/>																									    								   
								  </widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "6">
										<textbox  key="meta:getDescription($panel)" case = "mixed" max="60" width="425px"/>
									</widget>
								</row>														
						     </TablePanel>	
						  <VerticalPanel height = "20px"/>   
						  <HorizontalPanel>
						      <!--<VerticalPanel>
						       <table key="allTestsTable" manager="this" maxRows="16" showError="false" showScroll="true" title="" width="auto">												
												<headers> 
												 <xsl:value-of select="resource:getString($constants,'test')"/>,
												 <xsl:value-of select="resource:getString($constants,'method')"/>
												</headers>
												<widths>80,80</widths>
												<editors>
													<label/>
													<label/>													
												</editors>
												<fields>
													<string key="test"/>
													<string key="method"/>
												</fields>
												<sorts>false</sorts>
												<filters>false</filters>
												<colAligns>left</colAligns>
								</table>
								
						      </VerticalPanel>
						      <VerticalPanel>
						      <table key="addedTestTable" manager="this" maxRows="16" showError="false" showScroll="true" title="" width="auto">												
												<headers> 
												 <xsl:value-of select="resource:getString($constants,'test')"/>,
												 <xsl:value-of select="resource:getString($constants,'method')"/>
												</headers>
												<widths>80,80</widths>
												<editors>
													<label/>
													<label/>													
												</editors>
												<fields>
													<string key="{panelItem:getTestName($pi)}" required="true"/>
													<string key="{panelItem:getMethodName($pi)}" required="true"/>
												</fields>
												<sorts>false</sorts>
												<filters>false</filters>
												<colAligns>left</colAligns>
								</table>			
						      </VerticalPanel>-->						      
						      <VerticalPanel style="Form">
						       <text style="Prompt"><xsl:value-of select="resource:getString($constants,'allTests')"/></text>
						       <scrolllist key="allTests" targets="addedTests" drop="this" width="150px" maxRows="16" maxHeight="true">
                                  <widths>150</widths>
                               </scrolllist>
                              </VerticalPanel> 
                               <VerticalPanel style="Form">
                                <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testsThisPanel')"/></text>
                                <scrolllist key="addedTests" drop="this" width="150px" maxRows="16" maxHeight="true">
                                 <widths>150</widths>
                                </scrolllist>
                               </VerticalPanel>
						      <VerticalPanel>	
						         <VerticalPanel height = "120px" />			
						         		        
						        <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="moveUp" onclick="this" style="Button" key="moveUpButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"moveUp")'/>   </text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						         </widget>						         
						         <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="removeRow" onclick="this" style="Button" key="moveDownButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"moveDown")'/> </text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						        </widget>       
						         <widget style="WhiteContentPanel" valign="middle">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeTestButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						         </widget>
						      </VerticalPanel>
						     </HorizontalPanel>						   
						</VerticalPanel>
					 </VerticalPanel>  	 						  			
					</VerticalPanel>					
				</HorizontalPanel>
				
			</display>
			<rpc key="display">			 
			 <string key="meta:getName($panel)" max = "20" required="true" />			 
			 <string key="meta:getDescription($panel)" max="60" required="false"/>		 			 					
		     <model key="allTests"/>
		  </rpc>
		  <rpc key="query">			 
			 <queryString key="meta:getName($panel)"/>			 
			 <queryString key="meta:getDescription($panel)"/>			 			 							
			</rpc>
			<rpc key="queryByLetter">
			 <queryString key="meta:getName($panel)"/>				
			</rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>