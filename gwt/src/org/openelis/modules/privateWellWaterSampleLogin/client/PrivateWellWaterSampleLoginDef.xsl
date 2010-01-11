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
  
  <xsl:import href="IMPORT/button.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AnalysisNotesTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/AuxDataTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/QAEventsTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleItemTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/SampleNotesTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/StorageTabDef.xsl"/>
  <xsl:import href="OPENELIS/org/openelis/modules/sample/client/ResultTabDef.xsl"/>
  
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  
    <screen id="PrivateWellSampleLogin" name="{resource:getString($constants,'privateWellWaterSampleLogin')}">
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
          <VerticalPanel style="subform" width="99%">
            <text style="FormTitle">
					Private Well Info</text>
					<HorizontalPanel width="100%">
					<TablePanel style="Form">
					<row>
							<text style="Prompt">Report To:</text>
							<widget colspan="3">
								<textbox key="name" tab="??,??" width="180px"/>
							</widget>
							<text style="Prompt">Org Id:</text>
							<textbox key="name" tab="??,??" width="60px"/>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="1" width="180px" max="30" field="String"/>
							</widget>
						</row>	
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="2" width="180px" max="30" field="String"/>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="3" width="180px" max="30" field="String"/>
							</widget>
							<text style="Prompt">Phone:</text>
							<textbox key="name" tab="??,??" width="100px"/>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<dropdown case="UPPER" key="4" width="40px" tab="??,??" field="String"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<textbox case="UPPER" key="5" width="73px" max="30" field="String"/>
							<text style="Prompt">Fax:</text>
							<textbox key="name" tab="??,??" width="100px"/>
						</row>
						<row>

						</row>
					</TablePanel>
					<TablePanel style="Form" halign="right">
					<row>	
						<text style="Prompt"><xsl:value-of select="resource:getString($constants,'location')" />:</text>
						<widget colspan="3">
							<textbox key="{meta:getEnvLocation()}" width="180px" field="String" tab="{meta:getEnvDescription()},{meta:getEnvCollectorPhone()}" />
						</widget>	
					</row>
					<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="1" width="180px" max="30" field="String"/>
							</widget>	
					</row>
					<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="2" width="180px" max="30" field="String"/>
							</widget>
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
							<widget colspan="3">
								<textbox case="UPPER" key="3" width="180px" max="30" field="String"/>
							</widget>		
						</row>
						<row>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
							<dropdown case="UPPER" key="4" width="40px" tab="??,??" field="String"/>
							<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
							<textbox case="UPPER" key="5" width="73px" max="30" field="String"/>
						</row>
				</TablePanel>
				</HorizontalPanel>
			</VerticalPanel>
				<HorizontalPanel>
					<VerticalPanel style="subform">
              <text style="FormTitle">
                <xsl:value-of select="resource:getString($constants,'itemsAndAnalyses')" />
              </text>
              <TablePanel spacing="0" padding="0">
              	<row>
	                <tree key="itemsTestsTree" width="auto" maxRows="4" showScroll="ALWAYS" tab="{meta:getProjectName()},{meta:getEnvLocation()}">
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
	                  	<text style="FormTitle">Well/Collector Info</text>
	                  <TablePanel style="Form">
	                  <row>
						<text style="Prompt">Owner:</text>
						<textbox key="owner" tab="??,??" width="200px" field="String"/>
					</row>
					<row>
						<text style="Prompt">
                  			Collector:
                		</text>
                		<textbox key="collector" width="200px" field="String"/>
					</row>
					<row>
						<text style="Prompt">Well Num:</text>
						<textbox key="depth" width="80px" field="String"/>	
					</row>
					<row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'project')" />:
                  </text>
                  <HorizontalPanel>
                    <autoComplete key="{meta:getProjectName()}" width="182px" case="UPPER" popWidth="auto" field="Integer" tab="{meta:getOrgName()},itemsTestsTree">
                      <col width="115" header="{resource:getString($constants,'name')}" />
                      <col width="190" header="{resource:getString($constants,'desc')}" />
                    </autoComplete>
                    <appButton key="projectLookup" style="LookupButton">
                      <AbsolutePanel style="LookupButtonImage" />
                    </appButton>
                  </HorizontalPanel>
                </row>
					</TablePanel>
					</VerticalPanel>
				</HorizontalPanel>
          <TabPanel key="sampleItemTabPanel" height="236px" width="715px">
            <tab key="tab0" text="{resource:getString($constants,'sampleItem')}" tab="{meta:getItemTypeOfSampleId()},{meta:getItemUnitOfMeasureId()}">
              <xsl:call-template name="SampleItemTab"/>
            </tab>
            <tab key="tab1" text="{resource:getString($constants,'analysis')}" tab="{meta:getAnalysisTestName()},{meta:getAnalysisPrintedDate()}">
			  <xsl:call-template name="AnalysisTab"/>
            </tab>
            <tab key="tab2" text="{resource:getString($constants,'testResults')}" tab="testResultsTable,testResultsTable">
			  <xsl:call-template name="ResultTab"/>
            </tab>
            <tab key="tab3" text="{resource:getString($constants,'analysisNotes')}" tab="anExNoteButton,anIntNoteButton">
			  <xsl:call-template name="AnalysisNotesTab"/>
            </tab>
            <tab key="tab4" text="{resource:getString($constants,'sampleNotes')}" tab="sampleExtNoteButton,sampleIntNoteButton">
              <xsl:call-template name="SampleNotesTab"/>
            </tab>
            <tab key="tab5" text="{resource:getString($constants,'storage')}" tab="storageTable,storageTable">
			  <xsl:call-template name="StorageTab"/>
            </tab>
            <tab key="tab6" text="{resource:getString($constants,'qaEvents')}" tab="sampleQATable,analysisQATable">
			  <xsl:call-template name="QAEventsTab"/>
            </tab>
            <tab key="tab7" text="{resource:getString($constants,'auxData')}" tab="auxValsTable,auxValsTable">
			  <xsl:call-template name="AuxDataTab"/>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
