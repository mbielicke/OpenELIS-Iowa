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
  extension-element-prefixes="resource"
  version="1.0"
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.meta.SampleWebMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:variable name="language">
    <xsl:value-of select="doc/locale" />
  </xsl:variable>
  <xsl:variable name="props">
    <xsl:value-of select="doc/props" />
  </xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="FinalReport" name="{resource:getString($constants,'finalReport')}">
      <DeckPanel height="100%" key="deck" width="100%" style="ContentPanel">
        <deck>
          <HorizontalPanel padding="0" spacing="0">
            <VerticalPanel padding="0" spacing="0">
              <VerticalPanel style="subform">
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'dateReleased')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getReleasedDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getReleasedDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'dateCollected')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getCollectionDateFrom()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <calendar begin="0" end="2" key="{meta:getCollectionDateTo()}" pattern="{resource:getString($constants,'datePattern')}" width="90" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'accessionNumber')" />:
                    </text>
                    <widget>
                      <textbox field="Integer" key="{meta:getAccessionNumberFrom()}" max="60" width="86" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'to')" />:
                    </text>
                    <widget>
                      <textbox field="Integer" key="{meta:getAccessionNumberTo()}" max="60" width="86" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectorName")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getEnvCollector()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"clientReference")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getClientReference()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectionSite")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getEnvLocation()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"collectionTown")' />:
                    </text>
                    <widget colspan="4">
                      <textbox case="MIXED" field="String" key="{meta:getLocationAddrCity()}" max="60" width="202" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"projectCode")' />:
                    </text>
                    <widget colspan="4">
                      <dropdown field="Integer" key="{meta:getProjectId()}" tab="{meta:getReleasedDateFrom()},{meta:getLocationAddrCity()}" width="202" />
                    </widget>
                  </row>
                </TablePanel>
              </VerticalPanel>
              <TablePanel style="HorizontalDivider" width="100%">
                <row>
                  <html>&lt;hr/&gt;</html>
                </row>
              </TablePanel>
              <HorizontalPanel style="TableFooterPanel">
                <widget halign="center">
                  <appButton key="getSampleListButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"getReportFields")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
                <widget halign="center">
                  <appButton key="resetButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"reset")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </widget>
              </HorizontalPanel>              
            </VerticalPanel>
            <VerticalPanel height="280" padding="0" spacing="0" style="help" width="400">
              <TablePanel width="100%">
                <row>
                  <html>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat Lorem ipsum dolor sit amet, consectetuer  adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat</html>
                </row>
              </TablePanel>
            </VerticalPanel>
          </HorizontalPanel>
        </deck>
        <deck>
          <VerticalPanel padding="0" spacing="0">
          <HorizontalPanel>
            <VerticalPanel>
              <TablePanel>
                <row>
                  <widget>
                    <text style="heading" >
                      <xsl:value-of select='resource:getString($constants,"selectSampleField")' />
                    </text>                    
                  </widget>
                    <text >
                     -
                    </text> 
                        <widget halign="left" >
                            <appButton key="selectAllSampleFields" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="Checked" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'selectAll')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>             
                  </row>  
                  </TablePanel>
                  <HorizontalPanel>
                  <AbsolutePanel width="30px"/>
                  <TablePanel>               
                <row>  
                  <check key="{meta:getAccessionNumber()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"accessionNum")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getCollectionDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"dateCollected")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getReceivedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'received')" />
                  </text>
                 <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getReleasedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'released')" />
                  </text>
                </row>
                <row> 
                  <check key="{meta:getStatusId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"status")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getProjectIdHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"projectCode")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getClientReferenceHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'clientReference')" />
                  </text> 
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                      <check key="{meta:getEnvCollectorHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"collector")' />
                  </text>            
                </row>
                <row> 
                  <check key="{meta:getEnvLocationHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'collectionSite')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getEnvDescription()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"sampleDescription")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getEnvCollectorPhone()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"phoneNumber")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getItemTypeofSampleId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'sampleType')" />
                  </text>
                </row>
                <row>
               <check key="{meta:getItemSourceOfSampleId()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'source')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>                  
                </row>
              </TablePanel>
              </HorizontalPanel>
            </VerticalPanel>
            <VerticalPanel width = "20px"/>
            <VerticalPanel>
            <TablePanel>
                <row>
                  <widget>
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectOrganizationField")' />
                    </text>                    
                  </widget>
                  <text >
                     -
                    </text> 
                        <widget halign="left" >
                            <appButton key="selectAllOrganizationFields" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="Checked" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'selectAll')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>              
                  </row>
                   </TablePanel>
                   <HorizontalPanel>
                  <AbsolutePanel width="30px"/>
                  <TablePanel> 
                  <row>
                  <check key="{meta:getSampleOrgOrganizationName()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"name")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAddressMultipleUnit()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select='resource:getString($constants,"aptSuite")' />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAddressStreetAddress()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'address')" />
                  </text>
                  </row>
                  <row>
                  <check key="{meta:getAddressCity()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'city')" />
                  </text>    
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAddressState()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'state')" />
                  </text> 
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget> 
                  <check key="{meta:getAddressZipCode()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'zipcode')" />
                  </text>               
                </row>
                </TablePanel>
                </HorizontalPanel>
            </VerticalPanel>
            </HorizontalPanel>
            <VerticalPanel>
            <TablePanel>
                <row>
                  <widget>
                    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"selectAnalysisField")' />
                    </text>                    
                  </widget>
                   <text >
                     -
                    </text> 
                        <widget halign="left" >
                            <appButton key="selectAllAnalysisFields" style="Button">
                              <HorizontalPanel>
                                <AbsolutePanel style="Checked" />
                                <text>
                                  <xsl:value-of select="resource:getString($constants,'selectAll')" />
                                </text>
                              </HorizontalPanel>
                            </appButton>
                          </widget>               
                  </row>
                  </TablePanel>
                  <HorizontalPanel>
                  <AbsolutePanel width="30px"/>
                  <TablePanel>    
                  <row>
                  <check key="{meta:getAnalysisTestNameHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'test')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAnalysisMethodNameHeader()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'method')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAnalysisRevision()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'revision')" />
                  </text>
                  </row>
                  <row>
                  <check key="{meta:getAnalysisStartedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'started')" />
                  </text>  
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget>
                  <check key="{meta:getAnalysisCompletedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'completed')" />
                  </text>
                  <widget>
                    <HorizontalPanel width="10" />
                  </widget> 
                  <check key="{meta:getAnalysisReleasedDate()}" />
                  <text style="LeftAlignPrompt">
                    <xsl:value-of select="resource:getString($constants,'released')" />
                  </text>     
                </row>
                </TablePanel>
                </HorizontalPanel>
            </VerticalPanel>
            <VerticalPanel width="10px"></VerticalPanel>
            <HorizontalPanel>
            <VerticalPanel >
             <text style="heading">
               <xsl:value-of select='resource:getString($constants,"testAnalyteHeading")' />
             </text>
             <VerticalPanel style="Form">
            <widget valign="top">
                <table key="availAnalyteTable" maxRows="9" style = "ScreenTableWithSides" title="" width="auto">
                  <col key="select" width="20">
                      <check/>
                    </col>
                  <col header="{resource:getString($constants,'name')}" width="220">
                    <label field="String" />
                  </col>                  
                </table>
              </widget>
                 <HorizontalPanel>
                  <HorizontalPanel width="5" />
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="selectAllAnalyteButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="Checked"/>
                          <text>
                            <xsl:value-of select="resource:getString($constants,'selectAll')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="unselectAllAnalyteButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="Unchecked"/>
                          <text>
                            <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </HorizontalPanel>
                </VerticalPanel>
            </VerticalPanel>             
            <HorizontalPanel width="60px" />
            <VerticalPanel>           
             <text style="heading">
               <xsl:value-of select='resource:getString($constants,"auxDataHeading")' />
             </text>   
             <VerticalPanel style="Form">                     
            <widget valign="top">
                <table key="availAuxTable" maxRows="9" style = "ScreenTableWithSides"  title="" width="auto">
                  <col key="select" width="20">
                     <check/>
                  </col>
                  <col header="{resource:getString($constants,'name')}" width="220">
                    <label field="String" />
                  </col>                  
                </table>
              </widget>
              <HorizontalPanel>
                  <HorizontalPanel width="5" />
                  <widget style="TableButtonFooter">
                    <HorizontalPanel>
                      <appButton key="selectAllAuxButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="Checked" />
                          <text>
                            <xsl:value-of select="resource:getString($constants,'selectAll')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                      <appButton key="unselectAllAuxButton" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="Unchecked"/>
                          <text>
                            <xsl:value-of select="resource:getString($constants,'unselectAll')" />
                          </text>
                        </HorizontalPanel>
                      </appButton>
                    </HorizontalPanel>
                  </widget>
                </HorizontalPanel>
            </VerticalPanel> 
            </VerticalPanel>
            </HorizontalPanel>
                      
            <VerticalPanel>
            <widget>
               <VerticalPanel height="20" />
              </widget>
            </VerticalPanel>
            <TablePanel style="HorizontalDivider" width="100%">
                <row>
                  <html>&lt;hr/&gt;</html>
                </row>
              </TablePanel>
            <HorizontalPanel style="TableFooterPanel">            
             <widget halign="center">
                <appButton key="backButton" style="Button" visible="false">
                  <HorizontalPanel>
                    <AbsolutePanel />                   
                  </HorizontalPanel>
                </appButton>
              </widget>
              <widget halign="center">
                <appButton key="runReportButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"runReport")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
        </deck>
      </DeckPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>