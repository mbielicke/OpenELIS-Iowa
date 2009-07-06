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
                xmlns:meta="xalan://org.openelis.metamap.QcMetaMap"   
                xmlns:qcaMeta="xalan://org.openelis.metamap.QcAnalyteParameterMetaMap"          
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QcMetaMap"/>
	</xalan:component>
	<xalan:component prefix="qcaMeta">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QcAnalyteParameterMetaMap"/>
	</xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="qc" select="meta:new()"/>
	   <xsl:variable name="qca" select="meta:getQcAnalyte($qc)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Project" name="{resource:getString($constants,'QC')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="470px" style="LeftSidePanel">

					   <resultsTable width="100%" key="azTable" showError="false">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="20" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'name')"/>,<xsl:value-of select="resource:getString($constants,'lotNumber')"/></headers>
						     <widths>95,80</widths>
						     <editors>
						       <label/>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						       <string/>
						     </fields>
						   </table>
					   </resultsTable>
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
						 <VerticalPanel spacing="0" padding="0" height="235px" width="620px" style="WhiteContentPanel">
						  <HorizontalPanel spacing="0" padding="0" >
							<TablePanel style="Form">
								<row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
								  <widget colspan = "6">	
									<textbox key="{meta:getName($qc)}" tab="{meta:getTypeId($qc)},qcAnalyteTable" case = "lower" max="30" width="215px"  />																									    								   
								  </widget>								
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'type')"/>:</text>
								 <dropdown key="{meta:getTypeId($qc)}" tab="{meta:getInventoryItemId($qc)},{meta:getName($qc)}" width="100px" />																																	 								 							
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'inventoryItem')"/>:</text>
									<widget>
										<autoComplete case="mixed" cat="inventoryItem" key="{meta:getInventoryItemId($qc)}" tab="{meta:getSource($qc)},{meta:getTypeId($qc)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.qc.server.QCService" width="145px">
								       		<headers><xsl:value-of select="resource:getString($constants,'name')"/>,<xsl:value-of select="resource:getString($constants,'store')"/></headers>
											<widths>135,110</widths>
									  	</autoComplete>
									</widget>																
								</row>								
								<row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'source')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getSource($qc)}" tab="{meta:getLotNumber($qc)},{meta:getInventoryItemId($qc)}" case = "mixed" max="30" width="215px" />
									</widget>									
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'lotNumber')"/>:</text>
									<widget colspan = "6">
										<textbox  key="{meta:getLotNumber($qc)}" tab="{meta:getIsSingleUse($qc)},{meta:getSource($qc)}" case = "mixed" max="30" width="215px" />
									</widget>																									
                                </row>										
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"singleUse")'/>:</text>
									<check key="{meta:getIsSingleUse($qc)}" tab="{meta:getPreparedDate($qc)},{meta:getLotNumber($qc)}"/>																								
								</row>							
						    </TablePanel>
						    <TablePanel style="Form">
								<row>								  
								  <text style="Prompt"><xsl:value-of select='resource:getString($constants,"preparedDate")'/>:</text>
								  <calendar key="{meta:getPreparedDate($qc)}" tab="{meta:getPreparedVolume($qc)},{meta:getIsSingleUse($qc)}" begin="0" end="4" width = "140px" />
								</row>
								<row>								 
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'preparedVolume')"/>:</text>
								 <widget colspan = "6">
									<textbox  key="{meta:getPreparedVolume($qc)}" tab="{meta:getPreparedUnitId($qc)},{meta:getPreparedDate($qc)}" case = "mixed" width="100px" />
								 </widget>																							 								 								
								</row>
								<row>									
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'preparedUnit')"/>:</text>
								    <dropdown key="{meta:getPreparedUnitId($qc)}" tab="{meta:getPreparedById($qc)},{meta:getPreparedVolume($qc)}" width="150px" />									
								</row>								
								<row>								    
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'preparedBy')"/>:</text>
									<widget>
										<autoComplete case="mixed" cat="preparedBy" tab="{meta:getUsableDate($qc)},{meta:getPreparedUnitId($qc)}" key="{meta:getPreparedById($qc)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.qc.server.QCService" width="145px">
								       		<widths>145</widths>
									  	</autoComplete>
									</widget>
								</row>
								<row>										
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"usableDate")'/>:</text>
								    <calendar key="{meta:getUsableDate($qc)}" tab="{meta:getExpireDate($qc)},{meta:getPreparedById($qc)}" begin="0" end="4" width = "140px"/>															 
                                </row>										
								<row>										
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"expireDate")'/>:</text>
								    <calendar key="{meta:getExpireDate($qc)}" tab="qcAnalyteTable,{meta:getUsableDate($qc)}" begin="0" end="4" width = "140px" />														
								</row>							
						    </TablePanel>
						  </HorizontalPanel>  
						  <VerticalPanel height = "10px"/>			 
						  <HorizontalPanel width = "650px">							 
							   <widget valign="top">
							    <table key="qcAnalyteTable" tab="{meta:getName($qc)},{meta:getExpireDate($qc)}" manager="this" maxRows="9" showError="false" showScroll="ALWAYS" title="" width = "625px" >
												<headers>
												    <xsl:value-of select="resource:getString($constants,'analyte')"/>,
													<xsl:value-of select="resource:getString($constants,'type')"/>,													
													<xsl:value-of select="resource:getString($constants,'trendable')"/>,
													<xsl:value-of select="resource:getString($constants,'expectedValue')"/>																																																	
												</headers>
												<widths>270,55,55,400</widths>
												<editors>
													<autoComplete cellKey = "{qcaMeta:getAnalyteId($qca)}" cat="analyte" serviceUrl="OpenELISServlet?service=org.openelis.modules.qc.server.QCService" case="mixed" width="300px">												
										 				<widths>300</widths>
													</autoComplete> 
													<dropdown cellKey ="{qcaMeta:getTypeId($qca)}" case="mixed" width="75px"/>
													<check cellKey ="{qcaMeta:getIsTrendable($qca)}" />
													<textbox cellKey ="{qcaMeta:getValue($qca)}"/>																																		
												</editors>												
												<sorts>false,false,false,false</sorts>
												<filters>false,false,false,false</filters>
												<colAligns>left,left,center,left</colAligns>
								</table>						
						       </widget>							     						     
                            </HorizontalPanel>                                                       
						           <HorizontalPanel>    
                                       <widget>
							            <HorizontalPanel width = "7px"/>
							           </widget>                                       
                                       <widget  align="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeQCAnalyteButton">
									        <HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
						                 </appButton>
						              </widget>
						              <HorizontalPanel width = "420px"/>
						              <widget  align="center">
								       <appButton action="dictionaryLookUp" onclick="this" key="dictionaryLookUpButton" style="Button">
										<HorizontalPanel>
              							  <AbsolutePanel style="ButtonPanelButton"/>						              
                						    <text><xsl:value-of select='resource:getString($constants,"dictionary")'/></text>							             
							              </HorizontalPanel>
						                </appButton>
						               </widget>
						             </HorizontalPanel>						        
							  </VerticalPanel>						    	
						</VerticalPanel>   								
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <integer key="{meta:getId($qc)}" required="false"/>
			 <string key="{meta:getName($qc)}" max = "30" required="true" />
			 <dropdown key="{meta:getTypeId($qc)}" required="false" type="integer"/> 				 
			 <dropdown key="{meta:getInventoryItemId($qc)}" required="false" type="integer"/>	
			 <string key="{meta:getSource($qc)}" max="30" required="true"/>	 
			 <string key="{meta:getLotNumber($qc)}" max = "30" required="true" />
			 <date key="{meta:getPreparedDate($qc)}" begin="0" end="4" required="true"/>
			 <double key="{meta:getPreparedVolume($qc)}" required="true"/>
			 <dropdown key="{meta:getPreparedUnitId($qc)}" case="lower" required="false" type="integer"/>
			 <dropdown key="{meta:getPreparedById($qc)}" case="lower" required="true" type="integer"/>
			 <date key="{meta:getUsableDate($qc)}" begin="0" end="4" required="true"/>		
			 <date key="{meta:getExpireDate($qc)}" begin="0" end="4" required="true"/>	 			 			 
			 <check key="{meta:getIsSingleUse($qc)}" />		 								 
		     <table key="qcAnalyteTable">
		     	<dropdown key="{qcaMeta:getAnalyteId($qca)}" type="integer" required="true"/>
				<dropdown key="{qcaMeta:getTypeId($qca)}"  required="true" type="integer"/>
				<check key="{qcaMeta:getIsTrendable($qca)}" />
				<string key="{qcaMeta:getValue($qca)}" case="mixed" required="true"/>				
		     </table>  
		  </rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>