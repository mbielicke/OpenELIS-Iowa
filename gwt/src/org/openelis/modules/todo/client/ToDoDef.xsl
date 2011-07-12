
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

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="ToDo" name="{resource:getString($constants,'toDo')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <HorizontalPanel style="Form">
          <HorizontalPanel width = "10"/>
            <check key="mySection"/>  
            <text style="Prompt"><xsl:value-of select="resource:getString($constants,'showMySectOnly')" /></text>                                                                                  
          <HorizontalPanel width = "200"/>
          <HorizontalPanel>
            <appButton key="refreshButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="refreshButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'refresh')" />
                    </text>
              </HorizontalPanel>
              </appButton>
          </HorizontalPanel>
          <appButton key="trackingButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="trackingIcon" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'tracking')" />
                  </text>
            </HorizontalPanel>
          </appButton>
          <appButton key="exportToExcelButton" style="Button">
            <HorizontalPanel>
              <text><xsl:value-of select="resource:getString($constants,'exportToXl')" /></text>
            </HorizontalPanel>
          </appButton>
        </HorizontalPanel>
        <VerticalPanel height = "5"/>
        <TabPanel key="tabPanel" width="625" height="550">
          <tab text="{resource:getString($constants,'loggedIn')}">
            <VerticalPanel>
              <widget valign="top">
                <table key="loggedInTable" width="605" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="">                 
                  <col key="accessionNumber" width="50" sort="true" header="{resource:getString($constants,'accNum')}">
                    <label field="Integer" />
                  </col>        
                  <col key="domain" width="50" header="{resource:getString($constants,'domain')}" filter = "true">
                    <label field = "String"/>
                  </col>   
                  <col key="section" width="100" header="{resource:getString($constants,'section')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="70" header="{resource:getString($constants,'test')}" sort = "true" filter = "true">
                    <label field="String" />
                  </col>
                  <col key="method" width="60" header="{resource:getString($constants,'method')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>        
                  <col key="datec" width="105" header="{resource:getString($constants,'collected')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>                                        
                  <col key="daterec" width="105" header="{resource:getString($constants,'received')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>
                  <col key="qaResOver" width="55" header="{resource:getString($constants,'override')}" filter = "true">
                    <check/>
                  </col>
                  <col key="mush" width="150" header="{resource:getString($constants,'domainSpecField')}">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="200" header="{resource:getString($constants,'reportTo')}" sort = "true" filter = "true">
                    <label field = "String" />
                  </col>
                </table>
              </widget>
              <VerticalPanel key = "loggedInPanel" width = "623" height = "200"/>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'initiated')}">
           <VerticalPanel>
                <table key="initiatedTable" width="605" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="">            
                  <col key="accessionNumber" width="50" sort="true" header="{resource:getString($constants,'accNum')}">
                    <label field="Integer" />
                  </col>        
                  <col key="domain" width="50" header="{resource:getString($constants,'domain')}" filter = "true">
                    <label field = "String"/>
                  </col>   
                  <col key="section" width="100" header="{resource:getString($constants,'section')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="70" header="{resource:getString($constants,'test')}" sort = "true" filter = "true">
                    <label field="String" />
                  </col>
                  <col key="method" width="60" header="{resource:getString($constants,'method')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>      
                  <col key="holdingTime" width="100"  sort = "true" header="{resource:getString($constants,'holding')}">
                    <percentBar>
                      <range threshold="25" color="green"/>
					  <range threshold="50" color="yellow"/>
					  <range threshold="75" color="orange"/>
					  <range threshold="100" color="red"/>
					</percentBar>
                  </col>                                        
                  <col key="avgTATime" width="105" sort = "true" header="{resource:getString($constants,'avgTA')}">
                    <percentBar>
                      <range threshold="25" color="green"/>
					  <range threshold="50" color="yellow"/>
					  <range threshold="75" color="orange"/>
					  <range threshold="100" color="red"/>
					</percentBar>					
                  </col> 
                  <col key="daysInInitiated" width="100" sort = "true" header="{resource:getString($constants,'daysInInitiated')}">
                    <label field = "Integer"/>
                  </col> 
                  <col key="mush" width="150" header="{resource:getString($constants,'domainSpecField')}">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="200" header="{resource:getString($constants,'reportTo')}" sort = "true" filter = "true">
                    <label field = "String" />
                  </col>
                </table>
                <VerticalPanel key = "initiatedPanel" width = "605" height = "200"/>
              </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'completed')}">
           <VerticalPanel>
            <table key="completedTable" width="605" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="">            
                  <col key="accessionNumber" width="50" sort="true" header="{resource:getString($constants,'accNum')}">
                    <label field="Integer" />
                  </col>        
                  <col key="domain" width="50" header="{resource:getString($constants,'domain')}" filter = "true">
                    <label field = "String"/>
                  </col>   
                  <col key="section" width="100" header="{resource:getString($constants,'section')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="70" header="{resource:getString($constants,'test')}" sort = "true" filter = "true">
                    <label field="String" />
                  </col>
                  <col key="method" width="60" header="{resource:getString($constants,'method')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>       
                  <col key="qaResOver" width="55" header="{resource:getString($constants,'override')}" filter = "true">
                    <check/>
                  </col>
                  <col key="datec" width="105" header="{resource:getString($constants,'completed')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>                                                          
                  <col key="mush" width="150" header="{resource:getString($constants,'domainSpecField')}">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="200" header="{resource:getString($constants,'reportTo')}" sort = "true" filter = "true">
                    <label field = "String" />
                  </col>
            </table>
            <VerticalPanel key = "completedPanel" width = "605" height = "200"/>
           </VerticalPanel>    
          </tab>
          <tab text= "{resource:getString($constants,'toBeVerified')}" >
            <VerticalPanel>
              <widget valign="top">
                <table key="toBeVerifiedTable" width="605" maxRows="14" showScroll="ALWAYS" style="ScreenTableWithSides" title="">                 
                  <col key="accessionNumber" width="50" sort="true" header="{resource:getString($constants,'accNum')}">
                    <label field="Integer" />
                  </col>        
                  <col key="domain" width="50" header="{resource:getString($constants,'domain')}" filter = "true">
                    <label field = "String"/>
                  </col>          
                  <col key="datec" width="105" header="{resource:getString($constants,'collected')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>                                        
                  <col key="daterec" width="105" header="{resource:getString($constants,'received')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>
                  <col key="qaResOver" width="55" header="{resource:getString($constants,'override')}" filter = "true">
                    <check/>
                  </col>
                  <col key="mush" width="150" header="{resource:getString($constants,'domainSpecField')}">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="200" header="{resource:getString($constants,'reportTo')}" sort = "true" filter = "true">
                    <label field = "String" />
                  </col>
                </table>
              </widget>
              <VerticalPanel key = "toBeVerifiedPanel" width = "623" height = "200"/>
            </VerticalPanel>
          </tab>
          <tab text="{resource:getString($constants,'other')}">
           <VerticalPanel>
                <table key="otherTable" width="605" maxRows="25" showScroll="ALWAYS" style="ScreenTableWithSides" title="">                 
                  <col key="accessionNumber" width="50" header="{resource:getString($constants,'accNum')}" sort="true">
                    <label field="Integer" />
                  </col>        
                  <col key="domain" width="50" header="{resource:getString($constants,'domain')}" filter = "true">
                    <label field = "String"/>
                  </col>   
                  <col key="section" width="100" header="{resource:getString($constants,'section')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>
                  <col key="status" width="100" header="{resource:getString($constants,'status')}" sort = "true" filter = "true">
                    <dropdown field="Integer" width="100" />
                  </col>
                  <col key="test" width="70" header="{resource:getString($constants,'test')}" sort = "true" filter = "true">
                    <label field="String" />
                  </col>
                  <col key="method" width="60" header="{resource:getString($constants,'method')}" sort = "true" filter = "true">
                    <label field = "String"/>
                  </col>        
                  <col key="datec" width="105" header="{resource:getString($constants,'collected')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>                                        
                  <col key="daterec" width="105" header="{resource:getString($constants,'received')}" sort = "true">
                    <label field = "Date" pattern="{resource:getString($constants,'dateTimePattern')}"/>
                  </col>
                  <col key="qaResOver" width="55" header="{resource:getString($constants,'override')}" filter = "true">
                    <check/>
                  </col>
                  <col key="mush" width="150" header="{resource:getString($constants,'domainSpecField')}">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="200" header="{resource:getString($constants,'reportTo')}" sort = "true" filter = "true">
                    <label field = "String" />
                  </col>
                </table> 
             </VerticalPanel> 
          </tab>
          <tab text="{resource:getString($constants,'worksheet')}">
           <!-- <VerticalPanel>
            <table key="tablel" width="auto" maxRows="20" showScroll="ALWAYS" style="ScreenTableWithSides" title=""> 
                  <col key="section" width="100" header="{resource:getString($constants,'section')}">
                    <label field = "String"/>
                  </col>                
                  <col key="wsNumber" width="80" sort="true" header="Worksheet #">
                    <textbox field="String"/>
                  </col>         
                  <col key="analysis" width="180" sort="true" header="Created By">
                    <textbox max="255" field="String" />
                  </col>  
                  <col key="analysis" width="180" sort="true" header="Analysis">
                    <textbox max="255" field="String" />
                  </col>
                  <col key="status" width="60" header="Status">
                    <label field = "String"/>
                  </col>      
                  <col key="datec" width="100" header="Date Created">
                    <label field = "String"/>
                  </col>                                                      
                </table>
            </VerticalPanel>-->
            <text/>    
          </tab>
          <tab text="{resource:getString($constants,'instrument')}">
            <!-- <VerticalPanel>
              <VerticalPanel>
                <label field = "String" text = "Pie Chart showing distribution of analyses by status on a given day" /> 
                <textarea key="piechart" width="625" height="90" />
              </VerticalPanel>
              <VerticalPanel height = "10"/>
              <table key="tablei" width="auto" maxRows="10" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
                  <col key="mon" width="100" header="Sunday">
                    <label field = "String"/>
                  </col>
                  <col key="mon" width="100" header="Monday">
                    <label field = "String"/>
                  </col>                  
                  <col key="mon" width="100" header="Tuesday">
                    <label field = "String"/>
                  </col>            
                  <col key="mon" width="100" header="Wednesday">
                    <label field = "String"/>
                  </col>     
                  <col key="mon" width="100" header="Thursday">
                    <label field = "String"/>
                  </col>     
                  <col key="mon" width="100" header="Friday">
                    <label field = "String"/>
                  </col>                                
                  <col key="mon" width="100" header="Saturday">
                    <label field = "String"/>
                  </col>     
                </table>
                <VerticalPanel height = "10"/>
                <VerticalPanel>
                  <label field = "String" text = "Histogram showing no. of analyses for each day"/> 
                  <textarea key="histo" width="625" height="90" />
                </VerticalPanel>
              </VerticalPanel>-->
              <text/>
          </tab>           
        </TabPanel>               
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>