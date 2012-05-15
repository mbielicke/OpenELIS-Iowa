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
  xmlns:meta="xalan://org.openelis.meta.QcListMeta">
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="qcChart" name="{resource:getString($constants,'qcChart')}">
          <VerticalPanel width="500" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel >
              <VerticalPanel>
                <TablePanel style = "Form">
            	  <row >
            	  <widget colspan = "5">
            	    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"useDateRange")' />
                    </text>
                    </widget>
            	  </row>
            	  <row>
            	    <widget>
                      <HorizontalPanel width="50" />
                    </widget>
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"begin")' />:
                   </text>
                   <calendar key="{meta:getWorksheetCreatedDateFrom()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab = "{meta:getWorksheetCreatedDateTo()},plotDataTable"  />
            	  </row>
            	  <row>
            	  <widget>
                      <HorizontalPanel width="50" />
                    </widget>
                     <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"end")' />:
                     </text>
                     <calendar key="{meta:getWorksheetCreatedDateTo()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab = "numInstances,{meta:getWorksheetCreatedDateFrom()}"/>
            	  </row>
                </TablePanel>
              </VerticalPanel>
              <VerticalPanel>
              <TablePanel>
              <row><text>|</text></row>
              <row><text>|</text></row>
              <row>
                <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"or")' />
                </text>
              </row>
              <row><text>|</text></row>
              <row><text>|</text></row>
              </TablePanel>
              </VerticalPanel>
              <VerticalPanel>
              <TablePanel style = "Form">
              <row >
            	  <widget colspan = "5">
            	    <text style="heading">
                      <xsl:value-of select='resource:getString($constants,"mostRecentQc")' />
                    </text>
                    </widget>
            	  </row>
            	  <row>
            	  <widget>
                      <HorizontalPanel width="50" />
                    </widget>
                     <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"noToRetrieve")' />:
                     </text>
                     <widget>
                  <textbox key="numInstances" width="50" max="30" field="Integer" tab ="{meta:getQCName()},{meta:getWorksheetCreatedDateTo()}"/>
                </widget>
            	  </row>
              </TablePanel>
              </VerticalPanel>
              <VerticalPanel width="50"></VerticalPanel>
              <VerticalPanel>
              <widget>
                      <VerticalPanel height="40" />
                    </widget>
              <widget>
              <appButton key="getData" style="Button">
                      <HorizontalPanel>  
                      <AbsolutePanel style="FindButtonImage" />                   
                        <text>
                          <xsl:value-of select="resource:getString($constants,'getData')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
              </widget>
               <widget>
                      <VerticalPanel height="5" />
                    </widget>
               <widget>
              <appButton key="reCompute" style="Button" >
                      <HorizontalPanel>  
                      <AbsolutePanel style="FindButtonImage" />                   
                        <text>
                          <xsl:value-of select="resource:getString($constants,'reCompute')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
              </widget>
               <widget>
                      <VerticalPanel height="5" />
                    </widget>
                             <widget>
              <appButton key="plotdata" style="Button" >
                      <HorizontalPanel>  
                      <AbsolutePanel style="FindButtonImage" />                   
                        <text>
                          <xsl:value-of select="resource:getString($constants,'plotData')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
              </widget>
              </VerticalPanel>
            </HorizontalPanel>
            <VerticalPanel>
 			  <TablePanel style="Form">
 			  <row>
 			  <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'name')" />:
              </text>
              <widget colspan ="3">
                <autoComplete key="{meta:getQCName()}" width="230" field="String" required="true" tab = "plot,numInstances">
                  <col width="175" header = "{resource:getString($constants,'name')}">
                  <label field ="String" />
                  </col>
                  <col header = "{resource:getString($constants,'type')}" key ="{meta:getQCType()}" width="100">
                  	<dropdown field="Integer" width="100" />
                  </col>
                </autoComplete>
                </widget>
 			  </row>
 			  <row>
 			  <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"plotUsing")' />:
                    </text>
                    <widget colspan="4">
                      <dropdown field="Integer" key="plot" width ="175" tab="plotDataTable,{meta:getQCName()}"/>
                    </widget>
 			  </row>
 			  </TablePanel>	
            </VerticalPanel>
            
            <widget valign="top">
              <table key="plotDataTable" width="auto" maxRows="9" showScroll="ALWAYS" multiSelect = "true" style="ScreenTableWithSides"  title="" tab ="{meta:getWorksheetCreatedDateFrom()},plot">
                <col  width="30" align="left" header="{resource:getString($constants,'plot')}">
                  <check/>
                </col>
                <col  width="80" align="left" header="{resource:getString($constants,'accessionNum')}" sort="true">
                  <textbox field="String" />
                </col>
                <col width="110" align="left" header="{resource:getString($constants,'lotNum')}" sort="true">
                  <textbox field="String" />
                </col>
                <col width="110" align="left" header="{resource:getString($constants,'creationDate')}" sort="true">
                  <calendar begin="0" end="4" pattern="{resource:getString($constants,'dateTimePattern')}" />
                </col>
                <col  width="200" align="left" header="{resource:getString($constants,'analyte')}" sort="true">
                  <textbox field="String" />
                </col>
                <col  width="50" align="right" header="{resource:getString($constants,'v1')}">
                  <textbox field="String" />
                </col>
                <col  width="50" align="right" header="{resource:getString($constants,'v2')}">
                  <textbox field="String" />
                </col>
                <col  width="50" align="right" header="{resource:getString($constants,'plotValue')}">
                  <textbox field="Double" />
                </col>
                <col width="50" align="right" header="{resource:getString($constants,'mean')}">
                  <textbox field="Double" pattern = "{resource:getString($constants,'displayDoubleFormat')}" />
                </col>
                <col width="50" align="right" header="{resource:getString($constants,'uWL')}">
                  <textbox field="Double" pattern = "{resource:getString($constants,'displayDoubleFormat')}"/>
                </col>
                <col  width="50" align="right" header="{resource:getString($constants,'uCL')}">
                  <textbox field="Double" pattern = "{resource:getString($constants,'displayDoubleFormat')}"/>
                </col>
                <col width="50" align="right" header="{resource:getString($constants,'lWL')}">
                  <textbox field="Double" pattern = "{resource:getString($constants,'displayDoubleFormat')}"/>
                </col>
                <col  width="50" align="right" header="{resource:getString($constants,'lCL')}">
                  <textbox field="Double" pattern = "{resource:getString($constants,'displayDoubleFormat')}"/>
                </col>
              </table>
            </widget>
            <HorizontalPanel>            
             <widget halign="left">
                <appButton key="selectButton" style="Button" >
                  <HorizontalPanel>
                    <AbsolutePanel style="Checked" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"select")' />
                    </text>                   
                  </HorizontalPanel>
                </appButton>
              </widget>              
              <widget halign="right">
              <appButton key="unselectButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="Unchecked" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"unselect")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
              <HorizontalPanel width = "20"/>
              <widget halign="left">
				 <appButton key="selectAllButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="Checked" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"selectAll")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>             
              <widget halign="right">
                <appButton key="unselectAllButton" style="Button" >
                  <HorizontalPanel>
                    <AbsolutePanel style="Unchecked" />
                    <text>
                      <xsl:value-of select='resource:getString($constants,"unselectAll")' />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </widget>
            </HorizontalPanel>
          </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
