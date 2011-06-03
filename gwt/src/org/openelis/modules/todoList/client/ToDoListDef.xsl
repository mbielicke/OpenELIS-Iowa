
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
    <screen id="ToDoList" name="{resource:getString($constants,'toDo')}">
      <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
        <HorizontalPanel style="Form">
          <HorizontalPanel width = "10"/>
            <check key="mySection"/>  
            <text style="Prompt">Show my section's only</text>                                                                                  
          <HorizontalPanel width = "295"/>
          <appButton key="refreshButton" style="Button">
            <HorizontalPanel>
              <text>Refresh</text>
            </HorizontalPanel>
          </appButton>
          <appButton key="exportToExcelButton" style="Button">
            <HorizontalPanel>
              <text>Export To Excel</text>
            </HorizontalPanel>
          </appButton>
        </HorizontalPanel>
        <VerticalPanel height = "5"/>
        <TabPanel key="SampleContent" width="625" height="505">
          <tab text="To Be Verified" >
            <text/>
          </tab>
          <tab text="Logged In">
            <VerticalPanel>
              <widget valign="top">
                <table key="tablel" width="605" maxRows="15" showScroll="ALWAYS" style="ScreenTableWithSides" title="">                 
                  <col key="accessionNumber" width="75" sort="true" header="Accession #">
                    <textbox field="String" />
                  </col>        
                  <col key="domain" width="60" header="Domain">
                    <label field = "String"/>
                  </col>   
                  <col key="section" width="60" header="Section">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="100" sort="true" header="Test">
                    <textbox field="String" />
                  </col>
                  <col key="method" width="60" header="Method">
                    <label field = "String"/>
                  </col>        
                  <col key="datec" width="100" header="Date Collected">
                    <label field = "String"/>
                  </col>                                        
                  <col key="daterec" width="100" header="Date Received">
                    <check/>
                  </col>
                  <col key="mush" width="150" header="QA Result Override">
                    <check/>
                  </col>
                  <col key="mush" width="150" header="Domain Specific Field">
                    <check/>
                  </col>
                  <col key="reportTo" width="100" header="Report To">
                    <check/>
                  </col>
                </table>
              </widget>
            </VerticalPanel>
          </tab>
          <tab text="Initiated">
           <VerticalPanel>
                <table key="tablei" width="605" maxRows="15" showScroll="ALWAYS" style="ScreenTableWithSides" title="">            
                  <col key="accessionNumber" width="75" sort="true" header="Accession #">
                    <textbox field="String" />
                  </col>  
                  <col key="domain" width="60" header="Domain">
                    <label field = "String"/>
                  </col>         
                  <col key="section" width="60" header="Section">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="100" sort="true" header="Test">
                    <textbox field="String" />
                  </col>
                  <col key="method" width="60" header="Method">
                    <label field = "String"/>
                  </col>      
                  <col key="holdingTime" width="100" header="Holding">
                    <label field = "String"/>
                  </col>                                        
                  <col key="avgTATime" width="100" header="Avg. T.A.">
                    <label field = "String"/>
                  </col> 
                  <col key="daysInInitiated" width="100" header="Days in Initiated">
                    <label field = "String"/>
                  </col> 
                  <col key="mush" width="150" header="Domain Specific Field">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="100" header="Report To">
                    <label field = "String"/>
                  </col>
                </table>
              </VerticalPanel>
          </tab>
          <tab text="Completed">
           <VerticalPanel>
            <table key="tablec" width="605" maxRows="15" showScroll="ALWAYS" style="ScreenTableWithSides" title="">            
                  <col key="accessionNumber" width="75" sort="true" header="Accession #">
                    <textbox field="String" />
                  </col> 
                  <col key="domain" width="60" header="Domain">
                    <label field = "String"/>
                  </col>          
                  <col key="section" width="60" header="Section">
                    <label field = "String"/>
                  </col>
                  <col key="test" width="100" sort="true" header="Test">
                    <textbox field="String" />
                  </col>
                  <col key="method" width="60" header="Method">
                    <label field = "String"/>
                  </col>      
                  <col key="mush" width="150" header="QA Result Override">
                    <check/>
                  </col>
                  <col key="datei" width="100" header="Date Initiated">
                    <label field = "String"/>
                  </col> 
                  <col key="datec" width="100" header="Date Completed">
                    <label field = "String"/>
                  </col>                                                          
                  <col key="mush" width="150" header="Domain Specific Field">
                    <label field = "String"/>
                  </col>
                  <col key="reportTo" width="100" header="Report To">
                    <label field = "String"/>
                  </col>
                </table>
            </VerticalPanel>    
          </tab>
          <tab text="Other">
           <!-- <VerticalPanel>
            <table key="tablee" width="auto" maxRows="20" showScroll="ALWAYS" style="ScreenTableWithSides" title="">
                  <col key="section" width="100" header="{resource:getString($constants,'section')}">
                    <label field = "String"/>
                  </col>                  
                  <col key="accessionNumber" width="100" sort="true" header="Accession #">
                    <textbox max="255" field="String" required="true" />
                  </col>           
                  <col key="status" width="60" header="Status">
                    <label field = "String"/>
                  </col>
                  <col key="analysis" width="180" sort="true" header="Analysis">
                    <textbox max="255" field="String" />
                  </col>                                                
                  <col key="override" width="100" header="Result Override">
                    <check/>
                  </col>
                </table> 
            </VerticalPanel> --> 
            <text/>   
          </tab>
          <tab text="Worksheet">
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
          <tab text="Instrument">
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