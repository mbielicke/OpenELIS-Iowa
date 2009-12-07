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

<xsl:stylesheet 
version="1.0"
  extension-element-prefixes="resource"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd"
  xmlns:meta="xalan://org.openelis.meta.SampleMeta">
  
  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  
    <screen id="EnvironmentalSampleLogin" name="{resource:getString($constants,'environmentalSampleLogin')}">
      <VerticalPanel padding="0" spacing="0">

<!--button panel code-->

        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="queryButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="previousButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="nextButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="addButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="updateButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
            <xsl:call-template name="commitButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="abortButton">
              <xsl:with-param name="language">
                <xsl:value-of select="language" />
              </xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="buttonPanelDivider" />
   			<menuPanel key="optionsMenu" layout="vertical" style="topBarItemHolder">
    			<menuItem>
        			<menuDisplay>
	    				<appButton action="option" style="ButtonPanelButton">
							<HorizontalPanel>
	        					<text><xsl:value-of select='resource:getString($constants,"options")'/></text>
		    					<AbsolutePanel style="OptionsButtonImage" width="20px" height="20px"/>
			  				</HorizontalPanel>
						</appButton>
					</menuDisplay>
					<menuPanel style="buttonMenuContainer" layout="vertical" position="below">
						<xsl:call-template name="historyMenuItem"/>
			  		</menuPanel>
	    		</menuItem>
			</menuPanel>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel code-->

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'accessionNum')" />:
              </text>
              <textbox key="{meta:getAccessionNumber()}" width="75px" tab="orderNumber,billTo" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'orderNum')" />:
              </text>
              <textbox key="orderNumber" width="75px" tab="{meta:getCollectionDate()},{meta:getAccessionNumber()}" field="Integer" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'collected')" />:
              </text>
              <calendar key="{meta:getCollectionDate()}" begin="0" end="2" width="80px" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getCollectionTime()},orderNumber"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'time')" />:
              </text>
              <textbox key="{meta:getCollectionTime()}" begin="3" end="5" width="60px" pattern="{resource:getString($constants,'timePattern')}" tab="{meta:getReceivedDate()},{meta:getCollectionDate()}" field="Date" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'received')" />:
              </text>
              <calendar key="{meta:getReceivedDate()}" begin="0" end="4" width="110px" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getStatusId()},{meta:getCollectionTime()}"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="110px" popWidth="110px" tab="{meta:getClientReference()},{meta:getReceivedDate()}" field="Integer" required="true" />
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'clntRef')" />:
              </text>
              <widget colspan="3">
                <textbox key="{meta:getClientReference()}" width="175px" tab="{meta:getEnvIsHazardous()},{meta:getStatusId()}" field="String" />
              </widget>
            </row>
          </TablePanel>
          <VerticalPanel width="98%" style="subform">
            <text style="FormTitle">
              <xsl:value-of select="resource:getString($constants,'envInfo')" />
            </text>
            <TablePanel width="100%" style="Form">
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'hazardous')" />:
                </text>
                <check key="{meta:getEnvIsHazardous()}" tab="{meta:getEnvPriority()},{meta:getClientReference()}" />
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'priority')" />:
                </text>
                <textbox key="{meta:getEnvPriority()}" width="90px" tab="{meta:getEnvCollector()},{meta:getEnvIsHazardous()}" field="Integer" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'collector')" />:
                </text>
                <textbox key="{meta:getEnvCollector()}" width="235px" tab="{meta:getEnvCollectorPhone()},{meta:getEnvDescription()}" field="String"/>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'phone')" />:
                </text>
                <textbox key="{meta:getEnvCollectorPhone()}" width="120px" tab="{meta:getEnvSamplingLocation()},{meta:getEnvCollector()}" field="String" />
              </row>
              <row>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'location')" />:
                </text>
                <HorizontalPanel>
                  <textbox key="{meta:getEnvSamplingLocation()}" width="175px" field="String" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" />
                  <appButton key="locButton" style="LookupButton">
                    <AbsolutePanel style="LookupButtonImage" />
                  </appButton>
                </HorizontalPanel>
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'desc')" />:
                </text>
                <textbox key="{meta:getEnvDescription()}" width="315px" tab="itemsTestsTree,{meta:getEnvSamplingLocation()}" field="String" />
              </row>
            </TablePanel>
          </VerticalPanel>
          <HorizontalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')" />
              </text>
              <TablePanel spacing="0" padding="0">
              	<row>
	                <tree key="itemsTestsTree" width="auto" maxRows="4" showScroll="ALWAYS" tab="{meta:getProjectName()},{meta:getEnvSamplingLocation()}">
	                  <header>
	                    <col width="280" header="{resource:getString($constants,'itemAnalyses')}" />
	                    <col width="130" header="{resource:getString($constants,'typeStatus')}" />
	                  </header>
	                  <leaf key="sampleItem">
	                    <col>
	                      <label />
	                    </col>
	                    <col>
	                      <label />
	                    </col>
	                  </leaf>
	                  <leaf key="analysis">
	                    <col>
	                      <label />
	                    </col>
	                    <col>
	                      <dropdown width="110px" popWidth="110px" case="LOWER" field="String" />
	                    </col>
	                  </leaf>
	                </tree>
                </row>
                <row>
	                <widget style="TreeButtonFooter">
		                <HorizontalPanel>
		                  <appButton key="addItemButton" style="Button">
		                    <HorizontalPanel>
		                      <AbsolutePanel style="AddRowButtonImage" />
		                      <text>
		                        <xsl:value-of select="resource:getString($constants,'addItem')" />
		                      </text>
		                    </HorizontalPanel>
		                  </appButton>
		                  <appButton key="addAnalysisButton" style="Button">
		                    <HorizontalPanel>
		                      <AbsolutePanel style="AddRowButtonImage" />
		                      <text>
		                        <xsl:value-of select="resource:getString($constants,'addAnalysis')" />
		                      </text>
		                    </HorizontalPanel>
		                  </appButton>
		                  <appButton key="removeRowButton" style="Button" action="removeRow">
		                    <HorizontalPanel>
		                      <AbsolutePanel style="RemoveRowButtonImage" />
		                      <text>
		                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
		                      </text>
		                    </HorizontalPanel>
		                  </appButton>
		                </HorizontalPanel>
	                </widget>
                </row>
              </TablePanel>
            </VerticalPanel>
            <VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'organizationInfo')" />
              </text>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="{meta:getOrgName()},itemsTestsTree">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'reportTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getOrgName()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="{meta:getBillTo()},{meta:getProjectName()}">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="reportToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'billTo')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getBillTo()}" width="175px" case="UPPER" popWidth="auto" field="Integer" tab="sampleItemTabPanel,{meta:getOrgName()}">
                      <col width="180" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'street')}" />
                      <col width="100" header="{resource:getString($constants,'city')}" />
                      <col width="20" header="{resource:getString($constants,'st')}" />
                    </autoComplete>
                    <appButton key="billToLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
          <TabPanel key="sampleItemTabPanel" height="236px" width="715px">
            <tab key="tab0" text="{resource:getString($constants,'sampleItem')}">
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'sampleType')" />:</text>
                    <dropdown key="{meta:getItemTypeOfSampleId()}" width="150px" popWidth="150px" field="Integer" />
                  </row>
                  <row>
                  	<text style="Prompt"><xsl:value-of select="resource:getString($constants,'source')" />:</text>
                    <dropdown key="{meta:getItemSourceOfSampleId()}" width="150px" popWidth="150px" field="Integer" />
                   	<text style="Prompt"><xsl:value-of select="resource:getString($constants,'sourceOther')" />:</text>
                 	<textbox key="{meta:getItemSourceOther()}" width="215px" field="String" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'container')" />:</text>
                    <dropdown key="{meta:getItemContainerId()}" width="225px" popWidth="225px" field="Integer" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'containerReference')" />:</text>
                    <textbox key="{meta:getItemContainerReference()}" width="215px" field="String" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'qty')" />:</text>
                    <textbox key="{meta:getItemQuantity()}" width="150px" field="Double" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'unit')" />:</text>
                    <dropdown key="{meta:getItemUnitOfMeasureId()}" width="150px" popWidth="150px" field="Integer" />
                  </row>
                </TablePanel>
            </tab>
            <tab key="tab1" text="{resource:getString($constants,'analysis')}">
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'test')" />:</text>
                    <autoComplete key="{meta:getAnalysisTestName()}" width="150px" case="LOWER" popWidth="auto" field="Integer">
                      <col width="150" header="{resource:getString($constants,'test')}" />
                      <col width="150" header="{resource:getString($constants,'method')}" />
                      <col width="200" header="{resource:getString($constants,'description')}" />
                    </autoComplete>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'method')" />:</text>
                    <autoComplete key="{meta:getAnalysisMethodName()}" width="150px" case="LOWER" popWidth="auto" field="Integer">
                      <col width="150" header="{resource:getString($constants,'method')}" />
                    </autoComplete>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'status')" />:</text>
                    <dropdown key="{meta:getAnalysisStatusId()}" width="150px" popWidth="150px" field="Integer" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'revision')" />:</text>
                    <textbox key="{meta:getAnalysisRevision()}" width="60px" field="Integer" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'testReportable')" />:</text>
                    <check key="{meta:getAnalysisIsReportable()}" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'section')" />:</text>
                    <dropdown key="{meta:getAnalysisSectionName()}" width="150px" popWidth="150px" case="LOWER" field="Integer"/>
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'started')" />:</text>
                    <calendar key="{meta:getAnalysisStartedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'completed')" />:</text>
                    <calendar key="{meta:getAnalysisCompletedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </row>
                  <row>
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'released')" />:</text>
                    <calendar key="{meta:getAnalysisReleasedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
                    <text style="Prompt"><xsl:value-of select="resource:getString($constants,'printed')" />:</text>
                    <calendar key="{meta:getAnalysisPrintedDate()}" begin="0" end="2" pattern="{resource:getString($constants,'dateTimePattern')}" />
                  </row>
                </TablePanel>
            </tab>
            <tab key="tab2" text="{resource:getString($constants,'testResults')}">
				<TablePanel padding="0" spacing="0">
	              <row>
	                <table key="testResultsTable" width="697" maxRows="9" showScroll="ALWAYS" title="">
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                  <col width="200">
	                    <label />
	                  </col>
	                </table>
	                </row>
	                <row>
	                <widget style="TableButtonFooter">
	                <HorizontalPanel>
	                  <appButton key="addResultButton" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="AddRowButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'addRow')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                  <appButton key="removeResultButton" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="RemoveRowButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                  <appButton key="duplicateResultButton" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="DuplicateRowButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'duplicateRecord')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                </HorizontalPanel>
	                </widget>
	                </row>
	                </TablePanel>
            </tab>
            <tab key="tab3" text="{resource:getString($constants,'analysisNotes')}">
            <HorizontalPanel width="100%" height="100%" padding="0" spacing="0">
            <TablePanel padding="0" spacing="0" style="TabSubForm">
                <row>
		            <text style="Title"><xsl:value-of select="resource:getString($constants,'external')" />:</text>
		        </row>
		        <row>
		        	<notes key="anExNotesPanel" style="ScreenTableWithSides" width="351px" height="190px" />
		        </row>
		        <row>
			        <widget style="TableButtonFooter">
		                    <appButton key="anExNoteButton" style="Button">
		                      <HorizontalPanel>
		                        <AbsolutePanel style="StandardNoteButtonImage" />
		                        <text>
		                          <xsl:value-of select="resource:getString($constants,'editNote')" />
		                        </text>
		                      </HorizontalPanel>
		                    </appButton>
	                    </widget>
		        </row>
		    </TablePanel>
		    <AbsolutePanel style="Divider"/>
		    <TablePanel padding="0" spacing="0" style="TabSubForm">
			    <row>
			    	<text style="Title"><xsl:value-of select="resource:getString($constants,'internal')" />:</text>    
			    </row>
		     	<row>
           			<notes key="anIntNotesPanel" style="ScreenTableWithSides" width="351px" height="190px" />
           		</row>
                  <row>
		              <widget style="TableButtonFooter">
		               	<appButton key="anIntNoteButton" style="Button">
	                      <HorizontalPanel>
	                        <AbsolutePanel style="StandardNoteButtonImage" />
	                        <text>
	                          <xsl:value-of select="resource:getString($constants,'addNote')" />
	                        </text>
	                      </HorizontalPanel>
	                    </appButton>
                    </widget>
                  </row>
                </TablePanel>
                </HorizontalPanel>
            </tab>
            <tab key="tab4" text="{resource:getString($constants,'sampleNotes')}">
             <HorizontalPanel width="100%" height="100%" padding="0" spacing="0">
            <TablePanel padding="0" spacing="0" style="TabSubForm">
            	<row>
            		<text style="Title"><xsl:value-of select="resource:getString($constants,'external')" />:</text>
            	</row>
            	<row>
            		<notes key="sampleExtNotesPanel" style="ScreenTableWithSides" width="351px" height="190px" />
            	</row>
            	<row>
	            	<widget style="TableButtonFooter">
		                  <appButton key="sampleExtNoteButton" style="Button">
		                      <HorizontalPanel>
		                        <AbsolutePanel style="StandardNoteButtonImage" />
		                        <text>
		                          <xsl:value-of select="resource:getString($constants,'editNote')" />
		                        </text>
		                      </HorizontalPanel>
		                    </appButton>
	                    </widget>
            	</row>
          	</TablePanel>
          	<AbsolutePanel style="Divider"/>
          	<TablePanel padding="0" spacing="0" style="TabSubForm">
                <row>
        		    <text style="Title"><xsl:value-of select="resource:getString($constants,'internal')" />:</text>    
                </row>
                <row>
               		<notes key="sampleIntNotesPanel" style="ScreenTableWithSides" width="351px" height="190px" />
                  </row>
                  <row>
	               	<widget style="TableButtonFooter">
		               	<appButton key="sampleIntNoteButton" style="Button">
	                      <HorizontalPanel>
	                        <AbsolutePanel style="StandardNoteButtonImage" />
	                        <text>
	                          <xsl:value-of select="resource:getString($constants,'addNote')" />
	                        </text>
	                      </HorizontalPanel>
	                    </appButton>
                    </widget>
                  </row>
                </TablePanel>
                </HorizontalPanel>
            </tab>
            <tab key="tab5" text="{resource:getString($constants,'storage')}">
              <TablePanel padding="0" spacing="0">
              <row>
                <table key="storageTable" width="auto" maxRows="8" showScroll="ALWAYS" title="">
                  <col width="155" header="{resource:getString($constants,'user')}">
                    <label />
                  </col>
                  <col width="259" header="{resource:getString($constants,'location')}">
                    <autoComplete key="" width="215px" case="LOWER" popWidth="auto" field="Integer" required="true">
                      <col width="240" header="{resource:getString($constants,'name')}" />
                    </autoComplete>
                  </col>
                  <col width="135" header="{resource:getString($constants,'checkIn')}">
                    <calendar key="" begin="0" end="4" width="110" pattern="{resource:getString($constants,'dateTimePattern')}" required="true"/>
                  </col>
                  <col width="136" header="{resource:getString($constants,'checkOut')}">
                    <calendar key="" begin="0" end="4" width="110" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>
                </table>
                </row>
                <row>
                <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="addStorageButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="AddRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'addRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="removeStorageButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
                </widget>
                </row>
                </TablePanel>
            </tab>
            <tab key="tab6" text="{resource:getString($constants,'qaEvents')}">
              	<TablePanel padding="0" spacing="0" height="100%" style="TabSubForm">
              	<row>
	                 <table key="sampleQATable" style="ScreenTableWithSides" title="" width="auto" maxRows="8" showScroll="ALWAYS">
	                  <col width="172" key="{meta:getSampleSubQaName()}" header="{resource:getString($constants,'sampleQAEvent')}">
	                  	<label/>
	                  </col>
	                  <col width="90" key="{meta:getSampleQaTypeId()}" header="{resource:getString($constants,'type')}">
		                  <dropdown width="75px" popWidth="75px" field="Integer"/>
	                  </col>
	                  <col width="61" key="{meta:getSampleQaIsBillable()}" header="{resource:getString($constants,'billable')}">
	                  	<check/>
	                  </col>
	                </table>
	                <widget rowspan="3">
	                	<AbsolutePanel style="Divider"/>
	                </widget>
	                <table key="analysisQATable" style="ScreenTableWithSides" title="" width="auto" maxRows="8" showScroll="ALWAYS">
                  		<col width="172" key="{meta:getAnalysisAubQaName()}" header="{resource:getString($constants,'analysisQAEvent')}">
                  		<label/>
	                  </col>
	                  <col width="90" key="{meta:getAnalysisQaTypeId()}" header="{resource:getString($constants,'type')}">
		                  <dropdown width="75px" popWidth="75px" field="Integer"/>
	                  </col>
	                  <col width="60" key="meta:getAnalysisQaIsBillable()" header="{resource:getString($constants,'billable')}">
	                  	<check/>
	                  </col>
                </table>
                </row>
                <row>
                <widget style="TableButtonFooter">
	                <HorizontalPanel>
	                  <appButton key="removeSampleQAButton" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="RemoveRowButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                   <appButton key="sampleQAPicker" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="PickerButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'qaEvents')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
	                </HorizontalPanel>
	                </widget>
                <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="removeAnalysisQAButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="analysisQAPicker" style="Button">
	                    <HorizontalPanel>
	                      <AbsolutePanel style="PickerButtonImage" />
	                      <text>
	                        <xsl:value-of select="resource:getString($constants,'qaEvents')" />
	                      </text>
	                    </HorizontalPanel>
	                  </appButton>
                </HorizontalPanel>
                </widget>
                </row>
                </TablePanel>  
            </tab>
            <tab key="tab7" text="{resource:getString($constants,'auxData')}">
            <VerticalPanel>
                <TablePanel padding="0" spacing="0">
                <row>
                <widget colspan="6">
	              <table key="auxValsTable" title="" width="auto" maxRows="6" showScroll="ALWAYS">
	                 <col width="85" header="{resource:getString($constants,'reportable')}">
	                 <check/>
	                 </col>
	                  <col width="300" header="{resource:getString($constants,'name')}">
	                    <label />
	                  </col>
	                  <col width="303" class="org.openelis.modules.sample.client.AuxTableColumn" header="{resource:getString($constants,'value')}">
	                  	<label/>
	                  </col>
	                </table>
	                </widget>
                </row>
                <row>
                <widget colspan="6" style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="removeAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="addAuxButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="PickerButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'auxGroups')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
                </widget>
                </row>
                </TablePanel>
                <TablePanel style="Form">
                <row>
	                <text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'description')" />:
	              	</text>
	              	<widget colspan="3">
              			<textbox key="auxDesc" width="275px" style="ScreenTextboxDisplayOnly"/>
              		</widget>
	                <text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'method')" />:
	              	</text>
              		<textbox key="auxMethod" width="125px" style="ScreenTextboxDisplayOnly"/>
              		<text style="Prompt">
	                	<xsl:value-of select="resource:getString($constants,'unit')" />:
	              	</text>
              		<textbox key="auxUnits" width="125px" style="ScreenTextboxDisplayOnly"/>
                </row>
                </TablePanel>
                </VerticalPanel>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
