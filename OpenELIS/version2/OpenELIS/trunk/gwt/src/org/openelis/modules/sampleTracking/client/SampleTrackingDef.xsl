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
  
    <screen id="SampleTracking" name="SampleTracking">
      <HorizontalPanel padding="0" spacing="0">
      
        <CollapsePanel key="collapsePanel" style="LeftSidePanel" open="true">
          <HorizontalPanel width="225">
            <VerticalPanel>
              <tree key="atozTable" width="auto" maxRows="15" style="atozTable">
                <header> 
                   <col header="Sample" width="300"/>
                   <col header="Type/Status" width="125"/>
                </header>
                <leaf key="sample">
                  <col>
                    <label />
                  </col>
                  <col>
                    <label />
                  </col>
                </leaf>
                <leaf key="item">
                   <col>
                     <label/>
                   </col>
                   <col>
                     <label/>
                   </col>
                </leaf>
                <leaf key="analysis">
                   <col>
                      <label/>
                   </col>
                   <col>
                      <label/>
                   </col>
                </leaf>
                <leaf key="storage">
                  <col>
                    <label/>
                  </col>
                </leaf>
                <leaf key="qaevent">
                  <col>
                    <label/>
                  </col>
                </leaf>
                <leaf key="note">
                  <col>
                     <label/>
                  </col>
                </leaf>
                <leaf key="auxdata">
                  <col>
                     <label/>
                   </col>
                 </leaf>
                 <leaf key="result">
                   <col>
                     <label/>
                   </col>
                 </leaf>
              </tree>
              <widget halign="center">
                <HorizontalPanel>
                  <appButton key="atozPrev" style="Button" enable="false">
                    <AbsolutePanel style="prevNavIndex" />
                  </appButton>
                  <appButton key="atozNext" style="Button" enable="false">
                    <AbsolutePanel style="nextNavIndex" />
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>

 	<VerticalPanel padding="0" spacing="0">

<!--button panel code-->
        <AbsolutePanel spacing="0" style="ButtonPanelContainer">
          <HorizontalPanel>
            <menuItem>
              <menuDisplay>
            	<xsl:call-template name="queryButton">
              	  <xsl:with-param name="language">
                  <xsl:value-of select="language" />
                </xsl:with-param>
               </xsl:call-template>
             </menuDisplay>
             <menuPanel layout="vertical"  position="below" style="topMenuContainer">
                <menuItem key="environmentalSample" description="" icon="environmentalSampleLoginIcon" label="{resource:getString($constants,'environmentalSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="clinicalSample" description="" enable="false" icon="clinicalSampleLoginIcon" label="{resource:getString($constants,'clinicalSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="animalSample" description="" enable="false" icon="animalSampleLoginIcon" label="{resource:getString($constants,'animalSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="newbornScreeningSample" description="" enable="false" icon="newbornScreeningSampleLoginIcon" label="{resource:getString($constants,'newbornScreeningSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="ptSample" description="" enable="false" icon="ptSampleLoginIcon" label="{resource:getString($constants,'ptSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="sdwisSample" description="" enable="false" icon="sdwisSampleLoginIcon" label="{resource:getString($constants,'sdwisSampleLogin')}" style="TopMenuRowContainer" />
                <menuItem key="privateWellWaterSample" description="" enable="true" icon="privateWellWaterSampleLoginIcon" label="{resource:getString($constants,'privateWellWaterSampleLogin')}" style="TopMenuRowContainer" />
             </menuPanel>
            </menuItem>
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
          </HorizontalPanel>
        </AbsolutePanel>
     
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
          <TabPanel key="SampleContent" width="715" height="250">
            <tab text="             ">
              <AbsolutePanel/>
            </tab>
          </TabPanel>
      </VerticalPanel>
     </VerticalPanel>
   </HorizontalPanel>
  </screen>
</xsl:template>
</xsl:stylesheet>