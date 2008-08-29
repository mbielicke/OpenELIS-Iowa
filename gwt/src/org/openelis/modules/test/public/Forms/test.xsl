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
                xmlns:testPrep="xalan://org.openelis.metamap.TestPrepMetaMap"
                xmlns:testTOS="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap"                 
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
	<xalan:component prefix="testPrep">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestPrepMetaMap"/>
	</xalan:component>	
	<xalan:component prefix="testTOS">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap"/>
	</xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="test" select="meta:new()"/>
	   <xsl:variable name="tp" select="meta:getTestPrep($test)"/>
	   <xsl:variable name="tos" select="meta:getTestTypeOfSample($test)"/>	   
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
					<CollapsePanel key="collapsePanel" height="450px">
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
									<textbox  key="{meta:getId($test)}" tab="{meta:getName($test)},{meta:getMethodId($test)}" width="50px"/>
								</row> 	
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
									<textbox case="lower" key="{meta:getName($test)}" tab="{meta:getMethodId($test)},{meta:getId($test)}" max="20" width="145px"/>																	
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'method')"/>:</text>
								   <autoDropdown case="lower" key="{meta:getMethodId($test)}" tab="{meta:getId($test)}, {meta:getName($test)}" width="145px"/>
								</row>	
						     </TablePanel>	
						     <!--<VerticalPanel height = "50px"/>	-->
							<TabPanel halign="center" width = "590px" key="testTabPanel" >
							 <tab key="detailsTab" text="{resource:getString($constants,'testDetails')}"  >							  
							   <VerticalPanel padding="0" spacing="0"> 
							    <TablePanel style="Form">								
								 <row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "2">
										<textbox  key="{meta:getDescription($test)}" tab="{meta:getReportingDescription($test)},{meta:getRevisionMethodId($test)}" max="60" width="425px"/>
									</widget>
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportDescription')"/>:</text>
									<widget colspan = "2">
										<textbox key="{meta:getReportingDescription($test)}" tab="{meta:getIsReportable($test)},{meta:getDescription($test)}" max="60" width="425px"/>
									</widget>
								</row>
								<row>
								    <text style="Prompt"><xsl:value-of select='resource:getString($constants,"reportable")'/>:</text>
								    <check key="{meta:getIsReportable($test)}" tab="{meta:getTimeTaMax($test)},{meta:getReportingDescription($test)}"/>
								</row>    
								</TablePanel>
								<HorizontalPanel>
								<VerticalPanel style="Form">
								<titledPanel key="borderedPanel">
								 <legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"turnAround")'/></text></legend>
								 <content><TablePanel style="Form">
								 <row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundMax")'/>:</text>
									<textbox key="{meta:getTimeTaMax($test)}" tab="{meta:getTimeTaAverage($test)},{meta:getIsReportable($test)}" width = "50px"/>	
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeTransit')"/>:</text>
									<textbox key="{meta:getTimeTransit($test)}" tab="{meta:getTimeHolding($test)},{meta:getTimeTaWarning($test)}" max="30" width="50px"/>
								 </row>	
								 <row>						
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundAverage")'/>:</text>
									<textbox key="{meta:getTimeTaAverage($test)}" tab="{meta:getTimeTaWarning($test)},{meta:getTimeTaMax($test)}" width = "50px"/>

									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeHolding')"/>:</text>									
									<textbox key="{meta:getTimeHolding($test)}" tab="{meta:getIsActive($test)},{meta:getTimeTransit($test)}" max="30" width="50px"/>																																				   	 

								 </row>
								 <row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"turnAroundWarn")'/>:</text>
									<textbox key="{meta:getTimeTaWarning($test)}" tab="{meta:getTimeTransit($test)},{meta:getTimeTaAverage($test)}" width = "50px"/>									
								</row>								
								</TablePanel>
								</content>
								</titledPanel>
								<!--<TablePanel style = "Form">								  
								<row> 								   
																											   
								 </row>
								 </TablePanel>-->
								</VerticalPanel>
								<VerticalPanel style="Form"> 								
								<titledPanel key="borderedPanel1">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"activity")'/></text></legend>
								<content><TablePanel style="Form">
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($test)}" tab="{meta:getActiveBegin($test)},{meta:getTimeHolding($test)}"/>									
								<!--</row>
								<row>-->
								    
								</row>
								<row>
								 <text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($test)}" tab="{meta:getActiveEnd($test)},{meta:getIsActive($test)}" onChange="this" begin="0" end="2" width = "70px"/>																	
								 </row>
								 <row>
								 <text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($test)}" tab="{meta:getLabelId($test)},{meta:getActiveBegin($test)}" onChange="this" begin="0" end="2" width = "70px"/>
								</row>
																						
								</TablePanel>
								</content>
								</titledPanel>
								</VerticalPanel>							
								</HorizontalPanel>								
								<HorizontalPanel>	
								<VerticalPanel style="Form">				
								<titledPanel key="borderedPanel1">
								<legend><text style="LegendTitle"><xsl:value-of select='resource:getString($constants,"label")'/></text></legend>
								<content><TablePanel style="Form">
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									<widget>
										<autoDropdown key="{meta:getLabelId($test)}" tab="{meta:getLabelQty($test)},{meta:getActiveEnd($test)}"  case="mixed" width="215px" />
									</widget>
								</row>
								<row>
									<text style="CondensedPrompt"><xsl:value-of select='resource:getString($constants,"quantity")'/>:</text>
									<textbox key="{meta:getLabelQty($test)}" tab="{meta:getTestTrailerId($test)},{meta:getLabelId($test)}" width = "50px"/>
								</row>															
								</TablePanel>
								</content>
								</titledPanel>
								</VerticalPanel>
								<TablePanel style = "Form">
								<row>								  
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testTrailer')"/>:</text>
								   <widget>
								    <autoDropdown key="{meta:getTestTrailerId($test)}" tab="{meta:getTestFormatId($test)},{meta:getLabelQty($test)}" width="145px"/>
								   </widget>
								 </row>
								 <row>  
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testFormat')"/>:</text>
								   <widget> 
								    <autoDropdown  key="{meta:getTestFormatId($test)}" tab="{meta:getSectionId($test)},{meta:getTestTrailerId($test)}" width="145px"/>
								   </widget>
								   </row>
								   <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'section')"/>:</text>
								   <autoDropdown  key="{meta:getSectionId($test)}" tab="{meta:getScriptletId($test)},{meta:getTestFormatId($test)}" width="145px"/>
								  </row> 								
								   <!--</row> 
								   <row>-->								  
								 <row>
								  								   
								 </row>
                                  </TablePanel>                                                                     																										
								 </HorizontalPanel>								 
		                          <TablePanel style = "Form">
		                          <row>
		                           <text style="Prompt"><xsl:value-of select="resource:getString($constants,'scriptlet')"/>:</text>
								   <autoDropdown key="{meta:getScriptletId($test)}" tab="{meta:getRevisionMethodId($test)},{meta:getSectionId($test)}" width="285px"/>
								  </row>
								   <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'revisionMethod')"/>:</text>
								   <autoDropdown  key="{meta:getRevisionMethodId($test)}" tab="{meta:getDescription($test)},{meta:getScriptletId($test)}" width="145px"/>								   
								 </row>
		                         </TablePanel> 
		                         <VerticalPanel height = "5px"/>		                         
							  </VerticalPanel>							  							 
							</tab>
							<tab key="analyteTab" text="{resource:getString($constants,'testAnalyte')}">
							<HorizontalPanel>
							<VerticalPanel style="Form">
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
							<tab key="sampleAndPrepTab" text="{resource:getString($constants,'sampleAndPrep')}">
							 <VerticalPanel>
							   <VerticalPanel overflow="hidden" padding="0" spacing="0">
										<widget valign="top">
											<table key="sampleTypeTable" manager="SampleTypeTable" maxRows="5" showError="false" showScroll="true" title="" width="auto">
												<headers>
													<xsl:value-of select="resource:getString($constants,'sampleType')"/>,
													<xsl:value-of select="resource:getString($constants,'unitOfMeasure')"/>
												</headers>
												<widths>255,255</widths>
												<editors>
													<autoDropdown case = "mixed" width="240px" />
													<autoDropdown case = "mixed" width="240px"/>
												</editors>
												<fields>
													<dropdown key="{testTOS:getTypeOfSampleId($tos)}" required="true"/>
													<dropdown key="{testTOS:getUnitOfMeasureId($tos)}"/>
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
											</table>
											<query>
												<queryTable maxRows="5" showError="false" title="" width="auto">
													<headers>
														<xsl:value-of select="resource:getString($constants,'sampleType')"/>,
													    <xsl:value-of select="resource:getString($constants,'unitOfMeasure')"/>
													</headers>
													<widths>255,255</widths>
													<editors>													
														<autoDropdown case="mixed" multiSelect="true" width="240px"/>
														<autoDropdown case="mixed" multiSelect="true" width="240px"/>
													</editors>
													<fields>
														<xsl:value-of select='testTOS:getTypeOfSampleId($tos)'/>,
													    <xsl:value-of select='testTOS:getUnitOfMeasureId($tos)'/>
													</fields>
													<sorts>false,false</sorts>
												    <filters>false,false</filters>
												    <colAligns>left,left</colAligns>
												</queryTable>
											</query>
							 			 </widget>	
							 			 <widget style="WhiteContentPanel" halign="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeSampleTypeButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						                </widget> 
							 		</VerticalPanel>	
							 		<VerticalPanel overflow="hidden" padding="0" spacing="0">
										<widget valign="top">
											<table key="testPrepTable" manager="TestPrepTable" maxRows="5" showError="false" showScroll="true" title="" width="auto">
												<headers>
													<xsl:value-of select="resource:getString($constants,'prepTestMethod')"/>,													
													<xsl:value-of select="resource:getString($constants,'optional')"/>
												</headers>
												<widths>170,50</widths>
												<editors>
													<autoDropdown case="mixed" width="150px"/>
													<check/>													
												</editors>
												<fields>
													<dropdown key="{testPrep:getPrepTestId($tp)}" required="true"/>													
													<check key="{testPrep:getIsOptional($tp)}"/>													
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
											</table>
											 <query>
												<queryTable maxRows="5" showError="false" title="" width="auto">
													<headers>
														<xsl:value-of select="resource:getString($constants,'prepTestMethod')"/>,														
														<xsl:value-of select="resource:getString($constants,'optional')"/>													
													</headers>
													<widths>170,50</widths>
													<editors>													
														<autoDropdown case="mixed" multiSelect="true" width="150px"/>														
														<check threeState = "true"/>														
													</editors>
													<fields>
														<xsl:value-of select='testPrep:getPrepTestId($tp)'/>,														
														<xsl:value-of select='testPrep:getIsOptional($tp)'/>																												
													</fields>
													<sorts>false,false</sorts>
												    <filters>false,false</filters>
												    <colAligns>left,left</colAligns>
												</queryTable>
											</query>
							 			 </widget>
							 			 <widget style="WhiteContentPanel" halign="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removePrepTestButton">
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
							</tab>
						  </TabPanel>
						 	
						</VerticalPanel>
					</VerticalPanel>
				</HorizontalPanel>
			</display>
			<rpc key="display">
			 <number key="{meta:getId($test)}" required="false" type="integer"/>
			 <string key="{meta:getName($test)}" max = "20" required="true" />
			 <dropdown key="{meta:getMethodId($test)}" required="true" type="integer"/>
			 <string key="testTabPanel" reset="false">detailsTab</string>
			 <rpc key = "details">
				<string key="{meta:getDescription($test)}" max="60" required="true"/>
				<string key="{meta:getReportingDescription($test)}" max="60" required="false"/>
				<number key="{meta:getTimeTaMax($test)}"  type="integer" required="false"/>
				<number key="{meta:getTimeTaAverage($test)}" type="integer" required="false"/>
				<number key="{meta:getTimeTaWarning($test)}" type="integer" required="false"/>
				<number key="{meta:getTimeTransit($test)}" type="integer" required="false"/>	
				<check key="{meta:getIsActive($test)}" required="true"/>
				<check key="{meta:getIsReportable($test)}" required="true"/>
				<date key="{meta:getActiveBegin($test)}" begin="0" end="2" required="true"/>
				<date key="{meta:getActiveEnd($test)}" begin="0" end="2" required="true"/>
				<number key="{meta:getTimeHolding($test)}" type="integer" required="false"/> 			
				<dropdown key="{meta:getLabelId($test)}" required="false" type="integer"/>
				<number key="{meta:getLabelQty($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getTestTrailerId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getTestFormatId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getSectionId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getScriptletId($test)}" required="false" type="integer"/>
				<dropdown key="{meta:getRevisionMethodId($test)}" required="false" type="integer"/>				
			</rpc>			
			<rpc key = "sampleAndPrep"> 
			 <table key="sampleTypeTable"/>
			 <table key="testPrepTable"/>
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
				<dropdown key="{meta:getScriptletId($test)}" type="integer"/>
				<dropdown key="{meta:getLabelId($test)}"  type="integer"/>
				<dropdown key="{testTOS:getTypeOfSampleId($tos)}" required="false" type="integer"/>					
				<dropdown key="{testTOS:getUnitOfMeasureId($tos)}" required="false" type="integer"/> 
				<dropdown key="{testPrep:getPrepTestId($tp)}" required="false" type="integer"/>				
				<queryCheck key="{testPrep:getIsOptional($tp)}" />	
			</rpc>
			<rpc key="queryByLetter">
			 <queryString key="{meta:getName($test)}"/>				
			</rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>
