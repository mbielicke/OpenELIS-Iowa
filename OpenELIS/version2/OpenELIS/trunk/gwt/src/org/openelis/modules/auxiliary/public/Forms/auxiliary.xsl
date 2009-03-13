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
                xmlns:meta="xalan://org.openelis.metamap.AuxFieldMetaMap"
                xmlns:auxFieldValue="xalan://org.openelis.metamap.AuxFieldValueMetaMap"                       
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
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AuxFieldMetaMap"/>
	</xalan:component>
	<xalan:component prefix="auxFieldValue">
		<xalan:script lang="javaclass" src="xalan://org.openelis.metamap.AuxFieldValueMetaMap"/>
	</xalan:component>	
	
	<xsl:template match="doc">	
	   <xsl:variable name="auxf" select="meta:new()"/>
	   <xsl:variable name="auxfv" select="meta:getAuxFieldValue($auxf)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale"/>
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props"/>
		</xsl:variable>
		<xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
		<screen id="Auxiliary" name="{resource:getString($constants,'auxiliaryPrompt')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<HorizontalPanel padding="0" spacing="0" style="WhiteContentPanel">
					<!--left table goes here -->
					<CollapsePanel key="collapsePanel" height="480px">
						<azTable colwidths="175"  key="azTable" maxRows="20" tablewidth="auto" headers="{resource:getString($constants,'name')}" width="100%">
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
						<HorizontalPanel padding="0" spacing="0">
						<VerticalPanel width = "10px"/>
						 <VerticalPanel>						
								
				  <VerticalPanel height = "10px"/>						   							    																			
							<VerticalPanel>
							 <HorizontalPanel> 							 
							  <widget>
                                <tree-table key="auxiliaryTree" multiSelect = "true" targets = "auxiliaryTree" manager = "this" drop = "default" drag = "default" width="auto" showError="false" showScroll="ALWAYS" maxRows="8" enable="true">                                
                                 <headers>                                 
                                  <xsl:value-of select="resource:getString($constants,'analyte')"/>,													
								  <xsl:value-of select="resource:getString($constants,'description')"/>,
								  <xsl:value-of select="resource:getString($constants,'referenceTable')"/>,
								  <xsl:value-of select="resource:getString($constants,'method')"/>,
								  <xsl:value-of select="resource:getString($constants,'unit')"/>,
								  <xsl:value-of select="resource:getString($constants,'active')"/>,
								  <xsl:value-of select="resource:getString($constants,'required')"/>,
								  <xsl:value-of select="resource:getString($constants,'scriptlet')"/>
                                 </headers>
                                 <widths>200,100,100,50,40,60,60,60</widths>
                                 <leaves>
                                   <leaf type="top">
                                    <editors>
                                     <label/>
                                    </editors>
                                    <fields>
                                     <string/>
                                    </fields>
                                  </leaf>
                                  <leaf type="auxiliaryField">                                   
                                   <editors>
                                    <autoComplete cat="auxiliaryField" serviceUrl="OpenELISServlet?service=org.openelis.modules.auxiliary.server.AuxiliaryService" case="mixed" width="300px">												
										 <widths>300</widths>
									</autoComplete>
									<textbox/>
                                    <dropdown case="mixed" required = "true" width = "80px" type = "integer"/> 
                                    <dropdown case="mixed" width = "165px" type = "integer"/>
                                    <dropdown case="mixed" width = "165px" type = "integer"/>
                                    <check/>
                                    <check/> 
                                    <dropdown case="mixed" required = "true" width = "80px" type = "integer"/>   
                                   </editors>
                                   <fields>
                                    <dropdown key="{meta:getAnalyteId($auxf)}"/>
                                    <string key="{meta:getDescription($auxf)}"/>
                                    <dropdown key = "{meta:getReferenceTableId($auxf)}"/> 
                                    <dropdown key="{meta:getMethodId($auxf)}"/>
                                    <dropdown key="{meta:getUnitOfMeasureId($auxf)}"/>   
                                    <check key = "{meta:getIsActive($auxf)}"/>
                                    <check key = "{meta:getIsRequired($auxf)}"/>
                                    <dropdown key="{meta:getScriptletId($auxf)}" />                                                       
                                   </fields>
                                   <colAligns>left,left,left,left,left,center,center,left</colAligns>
                                  </leaf> 
                                 </leaves> 
                                </tree-table>
                              </widget> 
                              <HorizontalPanel width = "10px"/>
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
								<HorizontalPanel width = "225px"/>
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
						   							   
							<HorizontalPanel>
							  <widget valign="top">
							    <table key="auxFieldValueTable" manager="this" maxRows="7" showError="false" showScroll="ALWAYS" title="" width="auto">
												<headers>		 										    
													<xsl:value-of select="resource:getString($constants,'type')"/>,													
													<xsl:value-of select="resource:getString($constants,'value')"/>																																																																												
												</headers>
												<widths>170,520</widths>
												<editors>
												    <dropdown case="mixed" width="45px"/>													
													<textbox/>																																			
												</editors>
												<fields>											    
												    <dropdown key="{auxFieldValue:getTypeId($auxfv)}" type="integer" required="true"/>																										
													<string key="{auxFieldValue:getValue($auxfv)}" required="false"/>																																	
												</fields>
												<sorts>false,false</sorts>
												<filters>false,false</filters>
												<colAligns>left,left</colAligns>
								 </table>
							  </widget>
							 <HorizontalPanel width = "10px"/>                             
							</HorizontalPanel>     
                                     <HorizontalPanel>    
                                       <widget>
							            <HorizontalPanel width = "7px"/>
							           </widget>                                       
                                       <widget  align="center">
									       <appButton action="removeRow" onclick="this" style="Button" key="removeAuxFieldValueButton">
									        <HorizontalPanel>
													<AbsolutePanel style="RemoveRowButtonImage"/>
													<text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
												</HorizontalPanel>
						                 </appButton>
						              </widget>
						              <HorizontalPanel width = "509px"/>
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
						<HorizontalPanel width = "10px"/>
					  </HorizontalPanel>			
					</VerticalPanel>					
				</HorizontalPanel>
			</display>
			<rpc key="display">	
	         <tree key = "auxiliaryTree"/>
			 <table key = "auxFieldValueTable"/>					 										
		    </rpc>
		    <rpc key="query">			
			</rpc>
			<rpc key="queryByLetter">
			 	
			</rpc>
		</screen>
  </xsl:template>
</xsl:stylesheet>