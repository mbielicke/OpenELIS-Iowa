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
                xmlns:meta="xalan://org.openelis.metamap.TestMetaMap"                 
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestMetaMap"/>
	</xalan:component>	
	<xsl:template match="doc">	
	   <xsl:variable name="test" select="meta:new()"/>		   
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Test" name="{resource:getString($constants,'test')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="425px">
						<azTable colwidths="175"  key="azTable" maxRows="20" tablewidth="auto" title="{resource:getString($constants,'name')}" width="100%">
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
						<VerticalPanel>
							<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
									<textbox  key="{meta:getId($test)}" width="50px"/>
								</row> 	
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
									<textbox case="lower" key="{meta:getName($test)}" max="20" width="145px"/>																	
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'method')"/>:</text>
								   <autoDropdown case="lower" key="{meta:getMethodId($test)}" width="145px"/>
								</row>	
						     </TablePanel>	
						     <!--<VerticalPanel height = "50px"/>	-->
							<TabPanel halign="center"  key="orgTabPanel" >
							 <tab key="detailsTab" text="{resource:getString($constants,'testDetails')}"  >							  
							   <VerticalPanel padding="0" spacing="0"> 
							    <TablePanel style="Form">								
								 <row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "2">
										<textbox  key="{meta:getDescription($test)}" max="30" width="425px"/>
									</widget>
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportDescription')"/>:</text>
									<widget colspan = "2">
										<textbox key="{meta:getReportingDescription($test)}" max="30" width="425px"/>
									</widget>
								</row>
								</TablePanel>
								<HorizontalPanel>
								<VerticalPanel>
								<titledPanel key="borderedPanel">
								 <legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"turnAround")'/></text></legend>
								 <content><TablePanel style="Form">
								 <row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundMax")'/>:</text>
									<textbox key="{meta:getTimeTaMax($test)}" width = "50px"/>	
								 </row>	
								 <row>						
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundAverage")'/>:</text>
									<textbox key="{meta:getTimeTaAverage($test)}" width = "50px"/>
								 </row>
								 <row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundWarn")'/>:</text>
									<textbox key="{meta:getTimeTaWarning($test)}" width = "50px"/>									
								</row>								
								</TablePanel>
								</content>
								</titledPanel>
								<TablePanel style = "Form">								  
								<row> 								   
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeTransit')"/>:</text>
									<textbox key="{meta:getTimeTransit($test)}" max="30" width="50px"/>																		   
								 </row>
								 </TablePanel>
								</VerticalPanel> 								
								<titledPanel key="borderedPanel1">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"activity")'/></text></legend>
								<content><TablePanel style="Form">
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($test)}"/>									
								<!--</row>
								<row>-->
								    <text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"reportable")'/>:</text>
									<check key="{meta:getIsReportable($test)}" />
								</row>
								<row>
								 <text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($test)}"  onChange="this" begin="0" end="2" width = "70px"/>																	
								 </row>
								 <row>
								 <text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($test)}"  onChange="this" begin="0" end="2" width = "70px"/>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeHolding')"/>:</text>									
									<textbox key="{meta:getTimeHolding($test)}" max="30" width="50px"/>																																				   	 
								</row>														
								</TablePanel>
								</content>
								</titledPanel>							
								</HorizontalPanel>								
								<HorizontalPanel>					
								<titledPanel key="borderedPanel1">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"label")'/></text></legend>
								<content><TablePanel style="Form">
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									<widget>
										<autoDropdown key="{meta:getLabelId($test)}" case="mixed" width="215px" />
									</widget>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"quantity")'/>:</text>
									<textbox key="{meta:getLabelQty($test)}" width = "50px"/>
								</row>															
								</TablePanel>
								</content>
								</titledPanel>
								<TablePanel style = "Form">
								<row>								  
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testTrailer')"/>:</text>
								   <widget>
								    <autoDropdown case="mixed" key="{meta:getTestTrailerId($test)}" width="145px"/>
								   </widget>
								 </row>
								 <row>  
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testFormat')"/>:</text>
								   <widget> 
								    <autoDropdown case="upper" key="{meta:getTestFormatId($test)}" width="145px"/>
								   </widget>
								   </row>
								   <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'section')"/>:</text>
								   <autoDropdown case="upper" key="{meta:getSectionId($test)}" width="145px"/>
								  </row> 								
								   <!--</row> 
								   <row>-->
								  <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'revisionMethod')"/>:</text>
								   <autoDropdown case="upper" key="{meta:getRevisionMethodId($test)}" width="145px"/>
								 </row>
                                  </TablePanel>																										
								 </HorizontalPanel>								 
		                         <!--<HorizontalPanel>
		                          <HorizontalPanel width = "270px"/>
		                          <TablePanel style = "Form">
		                           <row>
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'revisionMethod')"/>:</text>
								    <autoDropdown case="upper" key="revisionMethod" width="145px"/>
								   </row>
								  </TablePanel>
		                         </HorizontalPanel> -->
							  </VerticalPanel>							  							 
							</tab>
							<tab key="analyteTab" text="{resource:getString($constants,'testAnalyte')}">
							<HorizontalPanel>
							<VerticalPanel>
							 <VerticalPanel key="treeContainer" height="250px" width="375px" overflow="auto">
							  <pagedtree key="analyteTree" vertical="true" height = "200px" width = "325px" itemsPerPage="1000" title=""/>			                   			
							</VerticalPanel>
							<HorizontalPanel> 			                    
								<widget halign="center" style="WhiteContentPanel">
											<appButton action="removeIdentifierRow" key="removeIdentifierButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'addAnalyte')"/></text>
												</HorizontalPanel>
											</appButton>
										</widget>	
								<widget halign="center" style="WhiteContentPanel">
											<appButton action="removeIdentifierRow" key="removeIdentifierButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeAnalyte')"/></text>
												</HorizontalPanel>
											</appButton>
										</widget>							
							 </HorizontalPanel>
							</VerticalPanel>							
						  </HorizontalPanel>	 
							</tab>
						  </TabPanel>
						 	
						</VerticalPanel>
					</VerticalPanel>
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <number key="{meta:getId($test)}" required="false" type="integer"/>
			 <string key="{meta:getName($test)}" required="false" />
			 <dropdown key="{meta:getMethodId($test)}" required="false" type="integer"/>
			 <rpc key = "details">
				<string key="{meta:getDescription($test)}" max="40" required="true"/>
				<string key="{meta:getReportingDescription($test)}" max="30" required="true"/>
				<number key="{meta:getTimeTaMax($test)}"  type="integer" required="false"/>
				<number key="{meta:getTimeTaAverage($test)}" type="integer" required="true"/>
				<number key="{meta:getTimeTaWarning($test)}" type="integer" required="false"/>
				<number key="{meta:getTimeTransit($test)}" type="integer" required="false"/>	
				<check key="{meta:getIsActive($test)}" required="false"/>
				<check key="{meta:getIsReportable($test)}" required="false"/>
				<date key="{meta:getActiveBegin($test)}" begin="0" end="2" required="false"/>
				<date key="{meta:getActiveEnd($test)}" begin="0" end="2" required="false"/>
				<number key="{meta:getTimeHolding($test)}" required="false"/> 			
				<dropdown key="{meta:getLabelId($test)}" required="false" type="integer"/>
				<number key="{meta:getLabelQty($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getTestTrailerId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getTestFormatId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getSectionId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getRevisionMethodId($test)}" required="false" type="integer"/>		
			</rpc>
		  </rpc>
		  <rpc key="query">
			 <queryNumber key="{meta:getId($test)}"  type="integer"/>
			 <queryString key="{meta:getName($test)}" />
			 <dropdown key="{meta:getMethodId($test)}"  type="integer"/>
			 <queryString key="{meta:getDescription($test)}"/>
				<queryString key="{meta:getReportingDescription($test)}" />
				<queryNumber key="{meta:getTimeTaMax($test)}" type="integer"/>
				<queryNumber key="{meta:getTimeTaAverage($test)}" type="integer"/>
				<queryNumber key="{meta:getTimeTaWarning($test)}" type="integer"/>
				<queryNumber key="{meta:getTimeTransit($test)}" type="integer"/>	
				<queryCheck key="{meta:getIsActive($test)}" />
				<queryCheck key="{meta:getIsReportable($test)}" />				
				<queryNumber key="{meta:getTimeHolding($test)}" type="integer"/> 			
				<dropdown key="{meta:getLabelId($test)}"  type="integer"/>
				<queryNumber key="{meta:getLabelQty($test)}" type="integer"/>
				<dropdown key="{meta:getTestTrailerId($test)}" type="integer"/>
				<dropdown key="{meta:getTestFormatId($test)}"  type="integer"/>
				<dropdown key="{meta:getSectionId($test)}" type="integer"/>
				<dropdown key="{meta:getRevisionMethodId($test)}" type="integer"/>		
			</rpc>
			<rpc key="queryByLetter">
			 <queryString key="{meta:getName($test)}"/>				
			</rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>
