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
  xmlns:locale="xalan://java.util.Locale"
  xmlns:meta="xalan://org.openelis.meta.SampleMeta"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="turnaroundStatistic" name="{resource:getString($constants,'turnAroundStatistic')}" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
      <VerticalPanel height="235" padding="0" spacing="0" style="WhiteContentPanel" width="500">
        <HorizontalPanel>
          <VerticalPanel>
            <TablePanel style="Form">
              <row>                
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'stRelDt')" />:
                </text>
                <calendar begin="0" end="4" key="releasedFrom" pattern="{resource:getString($constants,'dateTimePattern')}" tab="releasedTo,plotDataTable" width="150" required = "true"  />
              </row>
              <row>                
                <text style="Prompt">
                  <xsl:value-of select="resource:getString($constants,'endRelDt')" />:
                </text>
                <calendar begin="0" end="4" key="releasedTo" pattern="{resource:getString($constants,'dateTimePattern')}" tab="showAnalysis,releasedFrom" width="150" required = "true" />
              </row>  
               <row>
             <text style="Prompt">
               <xsl:value-of select="resource:getString($constants,'showAnalysis')" />:
             </text>
             <check key="showAnalysis" tab = "plot,releasedTo"/>
             </row>   
              <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"plotInterval")' />:
              </text>
              <widget >
                <dropdown field="Integer" key="plot" width="150" required = "true" tab = "stats,showAnalysis"/>
              </widget>
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select='resource:getString($constants,"statistic")' />:
              </text>
              <widget >
                <dropdown field="Integer" key="stats" width="150" required = "true" tab = "section,plot"/>
              </widget>
            </row>               
            </TablePanel>
          </VerticalPanel>
          <VerticalPanel width="50"></VerticalPanel>
          <VerticalPanel>
            <TablePanel style="Form">             
              <row>                
                <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'section')" />:
              </text>
              <widget colspan="4">
                <dropdown field="Integer" key="section" width="180" tab = "test, stats"/>
              </widget> 
             </row>
             <row>                
                <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'test')" />:
              </text>
              <widget colspan="4">
                <dropdown field="Integer" key="test" width="180" tab = "excludePTSample,section"/>
              </widget> 
             </row>
             <row>
             <text style="Prompt">
               <xsl:value-of select="resource:getString($constants,'exclPTSample')" />:
             </text>
             <check key="excludePTSample" tab = "organization,test"/>
             </row>
             <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'organization')" />:
              </text>
              <HorizontalPanel>
            <autoComplete case="UPPER" field="Integer" key="organization" popWidth="auto" width="200" tab = "printer,excludePTSample">
              <col header="{resource:getString($constants,'name')}" width="180" />
              <col header="{resource:getString($constants,'street')}" width="110" />
              <col header="{resource:getString($constants,'city')}" width="100" />
              <col header="{resource:getString($constants,'st')}" width="20" />
            </autoComplete>            
          </HorizontalPanel>
              </row>
              <row>                
                <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'printer')" />:
              </text>
              <widget colspan="4">
                <dropdown field="String" key="printer" width="180" tab = "plotDataTable,organization"/>
              </widget> 
             </row>
            </TablePanel>
          </VerticalPanel>
           <VerticalPanel width="50"></VerticalPanel>
          <VerticalPanel>
            <widget>
              <VerticalPanel height="5" />
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
              <VerticalPanel height="5" />
            </widget>
            <widget>
              <appButton key="plotdata" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="chartButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'plotData')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
          </VerticalPanel>
        </HorizontalPanel>
        <HorizontalPanel>
        <widget valign="top">
          <table key="plotDataTable" maxRows="9" multiSelect="true" showScroll="ALWAYS" style="ScreenTableWithSides" tab="releasedFrom,printer" title="" width="auto">
            <col align="left" header="{resource:getString($constants,'plot')}" width="30">
              <check />
            </col>
            <col align="left" header="{resource:getString($constants,'plotDate')}" sort="true" width="110">
              <calendar begin="0" end="4" pattern="{resource:getString($constants,'datePattern')}" />
            </col>
            <col align="left" header="{resource:getString($constants,'test')}" sort="true" width="180">
              <textbox field="String" />
            </col>
            <col align="left" header="{resource:getString($constants,'method')}" sort="true" width="170">
              <textbox field="String" />
            </col>            
            <col align="right" header="{resource:getString($constants,'statType')}" width="50">
              <textbox field="String" />
            </col>            
            <col align="right" header="{resource:getString($constants,'minimum')}" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'maximum')}" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'average')}" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>          
            <col align="right" header="{resource:getString($constants,'sd')}" width="40">
              <textbox field="Double" pattern="###0.00" />
            </col>
            <col align="right" header="{resource:getString($constants,'numTested')}" width="30">
              <textbox field="Integer" />
            </col>
          </table>
        </widget>
         <VerticalPanel width="5"></VerticalPanel>
         <widget valign="top">
          <table key="plotAnalysisDataTable" maxRows="9" multiSelect="true" showScroll="ALWAYS" style="ScreenTableWithSides" tab="releasedFrom,printer" title="" width="auto">
            <col align="left" header="{resource:getString($constants,'plot')}" width="30">
              <check />
            </col>
             <col align="right" header="{resource:getString($constants,'accNum')}" sort="true" width="30">
              <textbox field="Integer" />
            </col>
            <col align="right" header="{resource:getString($constants,'rev')}" sort="true" width="30">
              <textbox field="Integer" />
            </col>            
            <col align="right" header="{resource:getString($constants,'col-rec')}" sort="true" width="30">
              <textbox field="Double" pattern="###0.00"/>
            </col>            
            <col align="right" header="{resource:getString($constants,'col-rdy')}" sort="true" width="30">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'col-rel')}" sort="true" width="30">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'rec-rdy')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'rec-cmp')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'rec-rel')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'ini-cmp')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'ini-rel')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
            <col align="right" header="{resource:getString($constants,'cmp-rel')}" sort="true" width="40">
              <textbox field="Double" pattern="###0.00"/>
            </col>
          </table>
        </widget>
        </HorizontalPanel>
        <HorizontalPanel>          
          <widget halign="left">
            <appButton key="selectAllPlotDataButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Checked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"selectAll")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="right">
            <appButton key="unselectAllPlotDataButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Unchecked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"unselectAll")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
           <HorizontalPanel width="610" />
          <widget halign="left">
            <appButton key="selectAllAnalysisButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="Checked" />
                <text>
                  <xsl:value-of select='resource:getString($constants,"selectAll")' />
                </text>
              </HorizontalPanel>
            </appButton>
          </widget>
          <widget halign="right">
            <appButton key="unselectAllAnalysisButton" style="Button">
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