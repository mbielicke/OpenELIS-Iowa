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
                xmlns:meta="xalan://org.openelis.metamap.TestMetaMap"
                xmlns:method="xalan://org.openelis.meta.MethodMeta"
                xmlns:script="xalan://org.openelis.meta.ScriptletMeta"
                xmlns:label="xalan://org.openelis.meta.LabelMeta"
                xmlns:testPrep="xalan://org.openelis.metamap.TestPrepMetaMap"
                xmlns:testTOS="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap"
                xmlns:testRef="xalan://org.openelis.metamap.TestReflexMetaMap" 
                xmlns:testWrksht="xalan://org.openelis.metamap.TestWorksheetMetaMap"  
                xmlns:testWrkshtItm="xalan://org.openelis.metamap.TestWorksheetItemMetaMap"
                xmlns:testWrkshtAna="xalan://org.openelis.metamap.TestWorksheetAnalyteMetaMap"
                xmlns:testAnalyte="xalan://org.openelis.metamap.TestAnalyteMetaMap" 
                xmlns:testSection="xalan://org.openelis.metamap.TestSectionMetaMap"
                xmlns:testResult="xalan://org.openelis.metamap.TestResultMetaMap"                       
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
	<xalan:component prefix="method">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.MethodMeta"/>
	</xalan:component>
	<xalan:component prefix="script">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.ScriptletMeta"/>
    </xalan:component>
    <xalan:component prefix="label">
		<xalan:script lang="javaclass" src="xalan://org.openelis.meta.LabelMeta"/>
    </xalan:component>
	<xalan:component prefix="testPrep">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestPrepMetaMap"/>
	</xalan:component>	
	<xalan:component prefix="testTOS">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestTypeOfSampleMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testRef">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestReflexMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testWrksht">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testWrkshtItm">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetItemMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testWrkshtAna">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestWorksheetAnalyteMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testAnalyte">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestAnalyteMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testSection">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestSectionMetaMap"/>
	</xalan:component>
	<xalan:component prefix="testResult">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.TestResultMetaMap"/>
	</xalan:component>
	
	<xsl:template match="doc">	
	   <xsl:variable name="test" select="meta:new()"/>
	   <xsl:variable name="mt" select="meta:getMethod($test)"/>
	   <xsl:variable name="scpt" select="meta:getScriptlet($test)"/>
	   <xsl:variable name="lbl" select="meta:getLabel($test)"/>
	   <xsl:variable name="tp" select="meta:getTestPrep($test)"/>
	   <xsl:variable name="tos" select="meta:getTestTypeOfSample($test)"/>	 
	   <xsl:variable name="tref" select="meta:getTestReflex($test)"/>
	   <xsl:variable name="tws" select="meta:getTestWorksheet($test)"/> 
	   <xsl:variable name="twsi" select="meta:getTestWorksheetItem($test)"/>
	   <xsl:variable name="twsa" select="meta:getTestWorksheetAnalyte($test)"/>
	   <xsl:variable name="tana" select="meta:getTestAnalyte($test)"/>
	   <xsl:variable name="ts" select="meta:getTestSection($test)"/>
	   <xsl:variable name="tr" select="meta:getTestResult($test)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Test" name="{resource:getString($constants,'test')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="530px" style="LeftSidePanel">
						 <resultsTable width="100%" key="azTable" showError="false">
					       <buttonPanel key="atozButtons">
								<xsl:call-template name="aToZLeftPanelButtons"/>
						   </buttonPanel>
						   <table maxRows="27" width="auto">
						     <headers><xsl:value-of select="resource:getString($constants,'nameMethod')"/></headers>
						     <widths>175</widths>
						     <editors>
						       <label/>
						     </editors>
						     <fields>
						       <string/>
						     </fields>
						   </table>
					   </resultsTable>
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
								<xsl:call-template name="buttonPanelDivider"/>
    			                  <xsl:call-template name="optionsButton">
    			                    <xsl:with-param name="language">
    			                      <xsl:value-of select="language"/>
    			                    </xsl:with-param>
    			                  </xsl:call-template>
							</buttonPanel>
						</AbsolutePanel>
						<!--end button panel-->
						<HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
						 <VerticalPanel>						
							<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'id')"/>:</text>
									<textbox  key="{meta:getId($test)}" tab="{meta:getName($test)},{method:getName($mt)}" width="50px"/>
								</row> 	
								<row>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'name')"/>:</text>
									<textbox case="lower" key="{meta:getName($test)}" tab="{method:getName($mt)},{meta:getId($test)}" max="20" width="145px"/>																	
								    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'method')"/>:</text>
								    <widget>
								      <autoComplete case="mixed" cat="method" key="{method:getName($mt)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" tab="{meta:getId($test)}, {meta:getName($test)}" width="145px">
								       <widths>145</widths>
									  </autoComplete>								      
								    </widget> 
								</row>	
						     </TablePanel>	
						   <VerticalPanel height = "10px"/>						   							    
							<TabPanel halign="center" width = "635px" key="testTabPanel">
							 <tab key="detailsTab" text="{resource:getString($constants,'testDetails')}">							  
							   <VerticalPanel padding="0" spacing="0"> 
							    <TablePanel style="Form">								
								 <row>
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'description')"/>:</text>
									<widget colspan = "2">
										<textbox  key="{meta:getDescription($test)}" tab="{meta:getReportingDescription($test)},{script:getName($scpt)}" max="60" width="425px"/>
									</widget>
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportDescription')"/>:</text>
									<widget colspan = "2">
										<textbox key="{meta:getReportingDescription($test)}" tab="{meta:getTimeTaMax($test)},{meta:getDescription($test)}" max="60" width="425px"/>
									</widget>
								</row>								    
								</TablePanel>
								<HorizontalPanel>
								<VerticalPanel style="subform">
                                 <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"turnAround")'/></text>
								 <TablePanel style="Form">
								 <row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"turnAroundMax")'/>:</text>
									<textbox key="{meta:getTimeTaMax($test)}" tab="{meta:getTimeTaAverage($test)},{meta:getIsReportable($test)}" width = "50px"/>	
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeTransit')"/>:</text>
									<textbox key="{meta:getTimeTransit($test)}" tab="{meta:getTimeHolding($test)},{meta:getTimeTaWarning($test)}" max="30" width="50px"/>
								 </row>	
								 <row>						
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"turnAroundAverage")'/>:</text>
									<textbox key="{meta:getTimeTaAverage($test)}" tab="{meta:getTimeTaWarning($test)},{meta:getTimeTaMax($test)}" width = "50px"/>
									<text style="Prompt"><xsl:value-of select="resource:getString($constants,'timeHolding')"/>:</text>									
									<textbox key="{meta:getTimeHolding($test)}" tab="{meta:getIsActive($test)},{meta:getTimeTransit($test)}" max="30" width="50px"/>																																				   	 
								 </row>
								 <row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"turnAroundWarn")'/>:</text>
									<textbox key="{meta:getTimeTaWarning($test)}" tab="{meta:getTimeTransit($test)},{meta:getTimeTaAverage($test)}" width = "50px"/>									
								</row>																
								</TablePanel>
							   </VerticalPanel>							  
								<VerticalPanel style="subform"> 								
								<text style="FormTitle"><xsl:value-of select='resource:getString($constants,"activity")'/></text>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"active")'/>:</text>
									<check key="{meta:getIsActive($test)}" tab="{meta:getActiveBegin($test)},{meta:getTimeHolding($test)}"/>									
								</row>
								<row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"beginDate")'/>:</text>
								 <calendar key="{meta:getActiveBegin($test)}" tab="{meta:getActiveEnd($test)},{meta:getIsActive($test)}" begin="0" end="2" width = "90px"/>																	
								 </row>
								 <row>
								 <text style="Prompt"><xsl:value-of select='resource:getString($constants,"endDate")'/>:</text>
								 <calendar key="{meta:getActiveEnd($test)}" tab="{label:getName($lbl)},{meta:getActiveBegin($test)}" begin="0" end="2" width = "90px"/>
								</row>																						
								</TablePanel>								
								</VerticalPanel>							
								</HorizontalPanel>								
								<HorizontalPanel>	
								<VerticalPanel style="subform">				
								<text style="FormTitle"><xsl:value-of select='resource:getString($constants,"additionalLabel")'/></text>
								<TablePanel style="Form">
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"type")'/>:</text>
									<widget>										
									    <autoComplete cat="label" key="{label:getName($lbl)}" tab="{meta:getLabelQty($test)},{meta:getActiveEnd($test)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="lower" width="224px">												
										  <widths>180</widths>
								        </autoComplete>
									</widget>
								</row>
								<row>
									<text style="Prompt"><xsl:value-of select='resource:getString($constants,"quantity")'/>:</text>
									<textbox key="{meta:getLabelQty($test)}" width = "50px" tab="{meta:getIsReportable($test)},{label:getName($lbl)}"/>		
								</row>				
								<row>
									<widget><VerticalPanel height = "30px"/></widget>
								</row>							
								</TablePanel>								
								</VerticalPanel>
								<HorizontalPanel width = "1px"/>
								<VerticalPanel style="subform"> 								
								 <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"sections")'/></text>								  
										<widget valign="bottom">
											<table key="sectionTable" maxRows="4" manager = "this" showError="false" showScroll="ALWAYS" title="" width="auto" tab="removeTestSectionButton,{meta:getIsReportable($test)}">
												<headers>
													<xsl:value-of select="resource:getString($constants,'name')"/>,													
													<xsl:value-of select="resource:getString($constants,'options')"/>
												</headers>
												<widths>119,119</widths>
												<editors>
													<dropdown cellKey="{testSection:getSectionId($ts)}" case="mixed" width="85px"/>
													<dropdown cellKey="{testSection:getFlagId($ts)}" case="mixed" width="110px"/>													
												</editors>																																																	
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
											</table>
						
							 		  </widget>
							 			
							 		
							 		 <TablePanel width = "241px" spacing="0" padding="0" style="TableFooter">
                                      <row>
                                       <widget  align="center">
											<appButton  action="removeIdentifierRow" key="removeTestSectionButton" onclick="this" style="Button" tab="{meta:getRevisionMethodId($test)},sectionTable">											   
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
											</appButton>
								       </widget>								        
								     </row>	 							 			
								    </TablePanel>  
							 	  </VerticalPanel>	                                                                																										
								 </HorizontalPanel>

		                        <TablePanel style = "Form">
		                          <row>
		                           <text style="Prompt" halign="right"><xsl:value-of select='resource:getString($constants,"testReportable")'/>:</text>								    
								   <check key="{meta:getIsReportable($test)}" tab="sectionTable,{meta:getLabelQty($test)}"/>
		                          </row>
								  <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'revisionMethod')"/>:</text>
								   <dropdown  key="{meta:getRevisionMethodId($test)}" width="190px" tab="{meta:getSortingMethodId($test)},removeTestSectionButton"/>									   
								   <text style="Prompt" halign="left"><xsl:value-of select="resource:getString($constants,'testTrailer')"/>:</text>								   								   
								   <autoComplete cat="trailer" key="{meta:getTestTrailerId($test)}" tab="{meta:getTestFormatId($test)},{meta:getReportingSequence($test)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="lower" width="180px">												
										 <widths>180</widths>
								   </autoComplete>								   
								  </row>	
								  <row>	
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sortingMethod')"/>:</text>
								   <dropdown key="{meta:getSortingMethodId($test)}" tab="{meta:getReportingMethodId($test)},{meta:getRevisionMethodId($test)}" width="190px"/>									   						  								   							   								  								  																   
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testFormat')"/>:</text>								   
								   <dropdown  key="{meta:getTestFormatId($test)}"  width="180px" tab="{script:getName($scpt)},{meta:getTestTrailerId($test)}"/>								   
								  </row>													 
		                          <row>
		                           <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportingMethod')"/>:</text>
								   <dropdown key="{meta:getReportingMethodId($test)}" tab="{meta:getReportingSequence($test)},{meta:getSortingMethodId($test)}" width="190px"/>								  								    		                           								   								   								     								   							   								  								  																   
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'scriptlet')"/>:</text>								   								  
                                   <autoComplete cat="scriptlet" key="{script:getName($scpt)}" tab="{meta:getDescription($test)},{meta:getTestFormatId($test)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="lower" width="180px">												
										 <widths>180</widths>
								   </autoComplete> 								  
								  </row> 
								  <row>
								   <text style="Prompt"><xsl:value-of select="resource:getString($constants,'reportingSequence')"/>:</text>								  
								   <textbox key="{meta:getReportingSequence($test)}"  width="80px" tab="{meta:getTestTrailerId($test)},{meta:getReportingMethodId($test)}"/>					    
								  </row>								  								   
		                         </TablePanel>     
		                         <VerticalPanel height = "36px"/>                    
							  </VerticalPanel>							  							 
							</tab>
							
							<tab key="sampleTypeTab" text="{resource:getString($constants,'sampleType')}">
							 <VerticalPanel>
							 <HorizontalPanel>                           
							  	<widget valign="top">
											<table key="sampleTypeTable" manager="this" maxRows="21" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>
													<xsl:value-of select="resource:getString($constants,'sampleType')"/>,
													<xsl:value-of select="resource:getString($constants,'unitOfMeasure')"/>
												</headers>
												<widths>290,291</widths>
												<editors>
													<dropdown cellKey="{testTOS:getTypeOfSampleId($tos)}" case = "mixed" width="285px" />
													<dropdown cellKey="{testTOS:getUnitOfMeasureId($tos)}" case = "mixed" width="286px"/>
												</editors>																								
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
											</table>
							 			 </widget>	
							 			<!-- <HorizontalPanel width = "10px"/> -->
                                       </HorizontalPanel> 
							 										 		 	 
						                <TablePanel width = "567px" spacing="0" padding="0" style="TableFooter">
                                         <row>
                                          <widget  align="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeSampleTypeButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						                </widget>	
						              </row>  									 
						           </TablePanel>
						           <VerticalPanel height = "7px"/>
							 </VerticalPanel> 
					   </tab>
					   <tab key="analyteTab" text="{resource:getString($constants,'analytesResults')}">
							<VerticalPanel>
							 <HorizontalPanel> 							 
							  <widget>
                                <tree-table key="analyteTree" multiSelect = "true" targets = "analyteTree" manager = "this" drop = "default" drag = "default" width="auto" showError="false" showScroll="ALWAYS" maxRows="8" enable="true">                                
                                 <headers>                                 
                                  <xsl:value-of select="resource:getString($constants,'analyte')"/>,													
								  <xsl:value-of select="resource:getString($constants,'type')"/>,
								  <xsl:value-of select="resource:getString($constants,'analyteReportable')"/>,
								  <xsl:value-of select="resource:getString($constants,'scriptlet')"/>,
								  <xsl:value-of select="resource:getString($constants,'groupNum')"/>
                                 </headers>
                                 <widths>257,50,45,170,50</widths>
                                 <leaves>
                                   <leaf type="top">
                                    <editors>
                                     <label/>
                                    </editors>
                                    <fields>
                                     <string/>
                                    </fields>
                                  </leaf>
                                  <leaf type="analyte">                                   
                                   <editors>
                                    <autoComplete cat="analyte" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="mixed" width="300px">												
										 <widths>300</widths>
									</autoComplete>
                                    <dropdown case="mixed" width = "80px" type = "integer"/> 
                                    <check/>                                      
                                    <autoComplete cat="scriptlet" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="lower" width="165px">												
										 <widths>165</widths>
								    </autoComplete>
                                    <dropdown case="mixed" width = "40px" type = "integer" />
                                   </editors>
                                   <fields>
                                    <dropdown key="{testAnalyte:getAnalyteId($tana)}" required = "true" />
                                    <dropdown key="{testAnalyte:getTypeId($tana)}" required = "true"/>
                                    <check key = "{testAnalyte:getIsReportable($tana)}"/> 
                                    <dropdown key="{testAnalyte:getScriptletId($tana)}" />
                                    <dropdown key="{testAnalyte:getResultGroup($tana)}" type = "integer"/>
                                   </fields>
                                   <colAligns>left,left,center,left,left</colAligns>
                                  </leaf> 
                                 </leaves> 
                                </tree-table>
                              </widget> 
                             <!-- <HorizontalPanel width = "10px"/> -->
                             </HorizontalPanel>                              						 
							<HorizontalPanel style = "Form"> 
							    <widget halign="center">
											<appButton action="removeIdentifierRow" key="addRowButton" onclick="this" style="Button">												
												<HorizontalPanel>
												 <AbsolutePanel style="AddRowButtonImage"/>	
													 <text><xsl:value-of select="resource:getString($constants,'addRow')"/></text>												
												</HorizontalPanel>
											</appButton>
								</widget>
								<widget halign="center">
											<appButton action="removeIdentifierRow" key="deleteButton" onclick="this" style="Button">
												<HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
											</appButton>
								</widget> 
								<HorizontalPanel width = "117px"/>
								<widget halign="center">
											<appButton action="removeIdentifierRow" key="groupAnalytesButton" onclick="this" style="Button">
											 <HorizontalPanel>
													<AbsolutePanel style="groupRowButtonImage"/>												
													<text><xsl:value-of select="resource:getString($constants,'groupAnalytes')"/></text>	
										     </HorizontalPanel>														
										   </appButton>
								</widget>										                    
								<widget halign="center">
											<appButton action="removeIdentifierRow" key="ungroupAnalytesButton" onclick="this" style="Button">	
											 <HorizontalPanel>
													<AbsolutePanel style="ungroupRowButtonImage"/>											
													 <text><xsl:value-of select="resource:getString($constants,'ungroupAnalytes')"/></text>	
										     </HorizontalPanel> 														
											</appButton>
								</widget>							 																										
						  </HorizontalPanel>						  
						  <VerticalPanel height = "7px"/>
						   <HorizontalPanel>	
							<VerticalPanel> 
							  <TabPanel width = "491px" halign="center" key="resultTabPanel">
							   <tab key="tab" text="">
							    <VerticalPanel/>							    
							   </tab>
							  </TabPanel>							  
						    </VerticalPanel> 						      
							  <widget halign="center">
											<appButton action="removeIdentifierRow" key="addResultTabButton" onclick="this" style="Button">	
											 <HorizontalPanel>
													<AbsolutePanel/>																						
													 <text><xsl:value-of select="resource:getString($constants,'addGroup')"/></text>	
											 </HorizontalPanel>													
											</appButton>
							  </widget>
						  </HorizontalPanel>							   
							<HorizontalPanel width="624px">
							  <widget valign="top">
							    <table key="testResultsTable" manager="this" maxRows="7" showError="false" showScroll="ALWAYS" title="" width="589px">
												<headers>		
												    <xsl:value-of select="resource:getString($constants,'unit')"/>, 										    
													<xsl:value-of select="resource:getString($constants,'type')"/>,													
													<xsl:value-of select="resource:getString($constants,'value')"/>,
													<xsl:value-of select="resource:getString($constants,'quantLimit')"/>,
													<xsl:value-of select="resource:getString($constants,'contLevel')"/>,
													<xsl:value-of select="resource:getString($constants,'hazardLavel')"/>,
													<xsl:value-of select="resource:getString($constants,'flags')"/>,
													<xsl:value-of select="resource:getString($constants,'significantDigits')"/>,													
													<xsl:value-of select="resource:getString($constants,'roundingMethod')"/>																																																																												
												</headers>
												<widths>50,55,200,80,70,65,120,70,100</widths>
												<editors>
												    <dropdown cellKey="{testResult:getUnitOfMeasureId($tr)}" case="mixed" width="75px"/>													
													<dropdown cellKey="{testResult:getTypeId($tr)}" case="mixed" width="75px"/>
													<textbox cellKey="{testResult:getValue($tr)}"/>
													<textbox cellKey="{testResult:getQuantLimit($tr)}"/>
													<textbox cellKey="{testResult:getContLevel($tr)}"/>
													<textbox cellKey="{testResult:getHazardLevel($tr)}"/>
													<dropdown cellKey="{testResult:getFlagsId($tr)}" case="mixed" width="115px"/>
													<textbox cellKey="{testResult:getSignificantDigits($tr)}"/>													
													<dropdown cellKey="{testResult:getRoundingMethodId($tr)}" case="mixed" width="95px"/>																																			
												</editors>
												<!--this table has a special requirement which needs the fields tag to be here for the time being -->		
												<fields>
													<dropdown key="{testResult:getUnitOfMeasureId($tr)}" type="integer" required="false"/>										    
													<dropdown key="{testResult:getTypeId($tr)}" type="integer" required="true"/>																										
													<string key="{testResult:getValue($tr)}" required="false"/>	
													<string key="{testResult:getQuantLimit($tr)}"  required="false"/>	
													<string key="{testResult:getContLevel($tr)}"  required="false"/>
													<string key="{testResult:getHazardLevel($tr)}" required="false"/>	
													<dropdown key="{testResult:getFlagsId($tr)}" type="integer" required="false"/>	
													<integer key="{testResult:getSignificantDigits($tr)}"  required="false"/>														
													<dropdown key="{testResult:getRoundingMethodId($tr)}" type="integer" required="false"/>	
												</fields>										
												<sorts>false,false,false,false,false,false,false,false,false</sorts>
												<filters>false,false,false,false,false,false,false,false,false</filters>
												<colAligns>left,left,left,left,left,left,left,left,left</colAligns>
								 </table>
							  </widget>							                             
							</HorizontalPanel>     
                                     <HorizontalPanel>    
                                       <widget>
							            <HorizontalPanel width = "7px"/>
							           </widget>                                       
                                       <widget  align="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeTestResultButton">
									        <HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
						                 </appButton>
						              </widget>
						              <HorizontalPanel width = "401px"/>
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
					   </tab>	 
					   <tab key="prepAndReflexTab" text="{resource:getString($constants,'prepAndReflex')}">
							 <VerticalPanel>
							   <VerticalPanel padding="0" spacing="0">							    						     	
							 		<VerticalPanel padding="0" spacing="0">
							 		 <HorizontalPanel>
										<widget valign="top">
											<table key="testPrepTable" manager="this" maxRows="9" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>
													<xsl:value-of select="resource:getString($constants,'prepTestMethod')"/>,													
													<xsl:value-of select="resource:getString($constants,'optional')"/>
												</headers>
												<widths>489,92</widths>
												<editors>
													<autoComplete cellKey="{testPrep:getPrepTestId($tp)}" cat="testMethod" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="mixed" width="470px">												
										 				<widths>470</widths>
													</autoComplete>													
													<check cellKey="{testPrep:getIsOptional($tp)}" />													
												</editors>																																															
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
											</table>
							 			 </widget>
							 			<!-- <HorizontalPanel width = "10px"/> -->
                                       </HorizontalPanel> 
							 			  <TablePanel width = "567px" spacing="0" padding="0" style="TableFooter">
                                            <row>
                                             <widget  align="center">
									          <appButton action="removeRow" onclick="this" style="Button" key="removePrepTestButton">
									            <HorizontalPanel>
              						             <AbsolutePanel style="RemoveRowButtonImage"/>
						                          <widget>
                						           <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                      </widget>
							                    </HorizontalPanel>
						                      </appButton>
						                     </widget>
						                    </row> 
						                  </TablePanel> 
						                 <VerticalPanel height = "10px"/>
						     <HorizontalPanel>           
						      <widget valign="top">
							   <table key="testReflexTable" manager="this" maxRows="9" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>
												    <xsl:value-of select="resource:getString($constants,'reflexiveTest')"/>,
													<xsl:value-of select="resource:getString($constants,'testAnalyte')"/>,													
													<xsl:value-of select="resource:getString($constants,'result')"/>,
													<xsl:value-of select="resource:getString($constants,'flags')"/>																										
												</headers>
												<widths>150,181,140,104</widths>
												<editors>
													<autoComplete cellKey="{testRef:getAddTestId($tref)}" cat="testMethod" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="mixed" width="150px">												
										 				<widths>160</widths>
													</autoComplete>													
													<dropdown cellKey="{testRef:getTestAnalyteId($tref)}" case="mixed" width="181px" onchange = "this"/>
													<dropdown cellKey="{testRef:getTestResultId($tref)}" case="mixed" width="140px"/>
													<dropdown cellKey="{testRef:getFlagsId($tref)}" case="mixed" width="104px"/>													
												</editors>
												<sorts>false,false,false,false</sorts>
												<filters>false,false,false,false</filters>
												<colAligns>left,left,left,left</colAligns>
								</table>
						      </widget>						     
                            </HorizontalPanel> 
						        <TablePanel width = "565px" spacing="0" padding="0" style="TableFooter">
                                      <row>
                                        <widget  align="center">
									     <appButton action="removeRow" onclick="this" style="Button" key="removeReflexTestButton">
									        <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                  </widget>
							               </HorizontalPanel>
						                 </appButton>
						                </widget>	
						              </row>  									 
						        </TablePanel> 
							 </VerticalPanel>	  
							</VerticalPanel> 		
						   </VerticalPanel>	 
							</tab>
							<tab key="worksheetTab" text="{resource:getString($constants,'worksheetLayout')}">							
							<VerticalPanel>							
							 <VerticalPanel>
							  <TablePanel style="Form">
							   <row>
							     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'numberFormat')"/>:</text>
								 <widget colspan = "4">
								  <dropdown case="mixed" key="{testWrksht:getFormatId($tws)}" tab="{testWrksht:getBatchCapacity($tws)}, {testWrksht:getScriptletId($tws)}" width="145px"/>
								 </widget>
								</row>								
								<row>								
								  <text style="Prompt"><xsl:value-of select="resource:getString($constants,'batchCapacity')"/>:</text>																	 
								  <widget>
								   <textbox  key="{testWrksht:getBatchCapacity($tws)}" tab="{testWrksht:getTotalCapacity($tws)},{testWrksht:getFormatId($tws)}" width="40px"/>									
								  </widget>
								<widget valign="middle">								
								 <text style="Prompt"><xsl:value-of select="resource:getString($constants,'totalCapacity')"/>:</text>									
								</widget> 
								<widget>
								  <textbox key="{testWrksht:getTotalCapacity($tws)}" tab="{testWrksht:getScriptletId($tws)},{testWrksht:getBatchCapacity($tws)}" width="40px"/>																	
								 </widget>
								</row>	
								<row>
							     <text style="Prompt"><xsl:value-of select="resource:getString($constants,'scriptlet')"/>:</text>
								 <widget colspan = "3">								  
								  <autoComplete cat="scriptlet" key="{testWrksht:getScriptletId($tws)}" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="lower" width="235px">												
									<widths>235</widths>
								  </autoComplete>
								 </widget>
								</row>															    
							  </TablePanel>
							 </VerticalPanel>
							 <VerticalPanel style="subform"> 								
							  <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"qcItems")'/></text>
							  <HorizontalPanel>							 
							   <widget valign="top">
							    <table key="worksheetTable" manager="this" maxRows="7" showError="false" showScroll="ALWAYS" title="" width="auto" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}">
												<headers>
												    <xsl:value-of select="resource:getString($constants,'position')"/>,
													<xsl:value-of select="resource:getString($constants,'type')"/>,													
													<xsl:value-of select="resource:getString($constants,'qcName')"/>																																						
												</headers>
												<widths>74,147,347</widths>
												<editors>
													<textbox cellKey="{testWrkshtItm:getPosition($twsi)}"/>
													<dropdown cellKey="{testWrkshtItm:getTypeId($twsi)}" case="mixed" width="140px"/>
													<autoComplete cellKey = "{testWrkshtItm:getQcName($twsi)}" cat="qcName" serviceUrl="OpenELISServlet?service=org.openelis.modules.test.server.TestService" case="mixed" width="340px">												
										 				<widths>350</widths>
													</autoComplete>																					
												</editors>												
												<sorts>false,false,false</sorts>
												<filters>false,false,false</filters>
												<colAligns>left,left,left</colAligns>
								</table>						
						       </widget>							     						     
                            </HorizontalPanel>                                                       
						           <TablePanel width = "565px" spacing="0" padding="0" style="TableFooter">
                                      <row>
                                        <widget  align="center" >
									     <appButton action="removeRow" onclick="this" style="Button" key="removeWSItemButton" tab="worksheetTable, worksheetAnalyteTable">
									       <HorizontalPanel>
              						         <AbsolutePanel style="RemoveRowButtonImage"/>
						                      <widget>
                						       <text><xsl:value-of select='resource:getString($constants,"removeRow")'/></text>
							                 </widget>
							               </HorizontalPanel>
						                 </appButton>
						                </widget>	
						              </row>  									 
						           </TablePanel>						        
							  </VerticalPanel>
							  <VerticalPanel height = "3px"/>
							  <VerticalPanel style="subform"> 								
							  <text style="FormTitle"><xsl:value-of select='resource:getString($constants,"analytesWS")'/></text>
							  <HorizontalPanel>							 
							   <widget valign="top">
							    <table key="worksheetAnalyteTable" manager="this" maxRows="6" showError="false" showScroll="ALWAYS" title="" width="auto" tab="removeWSItemButton, {testWrksht:getScriptletId($tws)}">
												<headers>
												    <xsl:value-of select="resource:getString($constants,'analyte')"/>,
													<xsl:value-of select="resource:getString($constants,'availableWS')"/>,													
													<xsl:value-of select="resource:getString($constants,'repeat')"/>,	
													<xsl:value-of select="resource:getString($constants,'flag')"/>																																					
												</headers>
												<widths>275,156,65,72</widths>
												<editors>
													<label cellKey="analyteName"/>
													<check cellKey="available"/>
													<textbox cellKey="{testWrkshtAna:getRepeat($twsa)}"/>
													<dropdown cellKey="{testWrkshtAna:getFlagId($twsa)}" case="mixed" width="100px"/>																					
												</editors>																							    																																																															
												<sorts>false,false,false,false</sorts>
												<filters>false,false,false,false</filters>
												<colAligns>left,left,left,left</colAligns>
								</table>												
						       </widget>							     						     
                            </HorizontalPanel> 						        
						   </VerticalPanel>
						  </VerticalPanel>
						 </tab>
					   </TabPanel>							  								 					    			 	
					 </VerticalPanel>
						<HorizontalPanel width = "10px"/>
					  </HorizontalPanel>			
					</VerticalPanel>					
				</HorizontalPanel>
		   </display>
		   <rpc key="display">
			<integer key="{meta:getId($test)}" required="false" type="integer"/>
			<string key="{meta:getName($test)}" max = "20" required="true" />
			<dropdown key="{method:getName($mt)}" required="true" type="integer"/>
			<string key="testTabPanel" reset="false">sampleTypeTab</string>			 
			<string key="{meta:getDescription($test)}" max="60" required="true"/>
			<string key="{meta:getReportingDescription($test)}" max="60" required="false"/>
			<integer key="{meta:getTimeTaMax($test)}"  type="integer" required="false"/>
			<integer key="{meta:getTimeTaAverage($test)}" type="integer" required="false"/>
			<integer key="{meta:getTimeTaWarning($test)}" type="integer" required="false"/>
			<integer key="{meta:getTimeTransit($test)}" type="integer" required="false"/>	
			<check key="{meta:getIsActive($test)}" required="true"/>
			<check key="{meta:getIsReportable($test)}" required="true"/>
			<date key="{meta:getActiveBegin($test)}" begin="0" end="2" required="true"/>
			<date key="{meta:getActiveEnd($test)}" begin="0" end="2" required="true"/>
			<integer key="{meta:getTimeHolding($test)}" type="integer" required="false"/> 			
			<dropdown key="{label:getName($lbl)}" required="false" type="integer"/>
			<integer key="{meta:getLabelQty($test)}" required="false" type="integer"/>
			<dropdown key="{meta:getTestTrailerId($test)}" required="false" type="integer"/>
			<dropdown key="{meta:getTestFormatId($test)}" required="false" type="integer"/>				
			<dropdown key="{script:getName($scpt)}" required="false" type="integer"/>
			<dropdown key="{meta:getRevisionMethodId($test)}" required="false" type="integer"/>				
			<dropdown key="{meta:getReportingMethodId($test)}" required="false" type="integer"/>
			<integer key="{meta:getReportingSequence($test)}" required="false" type="integer"/>
			<dropdown key="{meta:getSortingMethodId($test)}" required="false" type="integer"/>
			<table key="sectionTable">
				<dropdown key="{testSection:getSectionId($ts)}" required="true"/>													
				<dropdown key="{testSection:getFlagId($ts)}" required="true"/>	
			</table>				
			<rpc key = "prepAndReflex"> 			 
			 <table key="testPrepTable">
			 	<dropdown type = "integer" key="{testPrep:getPrepTestId($tp)}" required="true"/>													
				<check key="{testPrep:getIsOptional($tp)}"/>
			 </table>
			 <table key="testReflexTable">
			 	<dropdown key="{testRef:getAddTestId($tref)}" required="true"/>
				<dropdown key="{testRef:getTestAnalyteId($tref)}" required="true"/>													
				<dropdown key="{testRef:getTestResultId($tref)}" required="true"/>	
				<dropdown key="{testRef:getFlagsId($tref)}" required="true"/>
			 </table>					 	 			 
			</rpc> 
			<rpc key = "sampleType"> 
		 	 <table key="sampleTypeTable">
		 	 	<dropdown key="{testTOS:getTypeOfSampleId($tos)}" required="true"/>
				<dropdown key="{testTOS:getUnitOfMeasureId($tos)}"/>
		 	 </table>
			</rpc>
			<rpc key = "worksheet">
			 <integer key="{testWrksht:getId($tws)}" required="false" type="integer"/>
			 <dropdown key="{testWrksht:getFormatId($tws)}" required="false" type="integer"/>
			 <dropdown key="{testWrksht:getScriptletId($tws)}" required="false" type="integer"/>
			 <integer key="{testWrksht:getBatchCapacity($tws)}" required="false" type="integer"/>
			 <integer key="{testWrksht:getTotalCapacity($tws)}" required="false" type="integer"/>
		 	 <table key="worksheetTable">
		 	 	<integer key="{testWrkshtItm:getPosition($twsi)}" required="false"/>
			  	<dropdown key="{testWrkshtItm:getTypeId($twsi)}" type="integer" required="true"/>													
			  	<dropdown key="{testWrkshtItm:getQcName($twsi)}" type="string" required="true"/>
		 	 </table>
		 	 <table key="worksheetAnalyteTable">
		 	 	<string key="analyteName"/>
				<check key="available"/>
				<integer key="{testWrkshtAna:getRepeat($twsa)}" required="false"/>												    
				<dropdown key="{testWrkshtAna:getFlagId($twsa)}" type="integer"/>
		 	 </table>
			</rpc>
			<rpc key = "testAnalyte">
			 <tree key = "analyteTree"/>
			 <table key = "testResultsTable">
	 			<dropdown key="{testResult:getUnitOfMeasureId($tr)}" type="integer" required="false"/>										    
				<dropdown key="{testResult:getTypeId($tr)}" type="integer" required="true"/>																										
				<string key="{testResult:getValue($tr)}" required="false"/>	
				<string key="{testResult:getQuantLimit($tr)}"  required="false"/>	
				<string key="{testResult:getContLevel($tr)}"  required="false"/>
				<string key="{testResult:getHazardLevel($tr)}" required="false"/>	
				<dropdown key="{testResult:getFlagsId($tr)}" type="integer" required="false"/>	
				<integer key="{testResult:getSignificantDigits($tr)}"  required="false"/>														
				<dropdown key="{testResult:getRoundingMethodId($tr)}" type="integer" required="false"/>		
			 </table>			 				 
			</rpc> 
		  </rpc>		  
		</screen>
  </xsl:template>
</xsl:stylesheet>