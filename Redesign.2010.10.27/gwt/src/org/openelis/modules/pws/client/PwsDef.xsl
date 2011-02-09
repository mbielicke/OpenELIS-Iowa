
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
  xmlns:meta="xalan://org.openelis.meta.PwsMeta">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
<!-- main screen -->
    <screen name="{resource:getString($constants,'pwsInformation')}">
      <HorizontalPanel padding="0" spacing="0">
<!--left table goes here -->
        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" rows="20">
                <col width="175" header="{resource:getString($constants,'name')}">
                  <label field="String" />
                </col>
              </table>
              <widget halign="center">
                <HorizontalPanel>
                  <button key="atozPrev" style="Button" enabled="false">
                    <AbsolutePanel style="PreviousButtonImage" />
                  </button>
                  <button key="atozNext" style="Button" enabled="false">
                    <AbsolutePanel style="NextButtonImage" />
                  </button>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </HorizontalPanel>
        </CollapsePanel>
<!--button panel code-->
        <VerticalPanel padding="0" spacing="0">
          <AbsolutePanel spacing="0" style="ButtonPanelContainer">
            <HorizontalPanel>
              <xsl:call-template name="queryButton"/>
              <xsl:call-template name="previousButton"/>
              <xsl:call-template name="nextButton"/>
              <xsl:call-template name="buttonPanelDivider" />
              <xsl:call-template name="commitButton"/>
              <xsl:call-template name="abortButton"/>
              <xsl:call-template name="selectButton"/>
            </HorizontalPanel>
          </AbsolutePanel>
<!--end button panel-->
          <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel>
              <VerticalPanel>
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'pwsId')" />:
                    </text>
                    <textbox key="{meta:getNumber0()}" width="68" case="UPPER" tab="{meta:getAlternateStNum()},{meta:getEffEndDt()}" field="String" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'fieldOfficeNum')" />:
                    </text>
                    <textbox key="{meta:getAlternateStNum()}" width="40" case="UPPER" max="5" tab="{meta:getName()},{meta:getNumber0()}" field="String" />
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'name')" />:
                    </text>
                    <widget colspan="3">
                      <textbox key="{meta:getName()}" width="285" case="UPPER" max="40" tab="{meta:getDPrinCitySvdNm()},{meta:getAlternateStNum()}" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'city')" />:
                    </text>
                    <widget colspan="3">
                      <textbox key="{meta:getDPrinCitySvdNm()}" width="285" case="UPPER" max="40" tab="{meta:getDPrinCntySvdNm()},{meta:getName()}" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'county')" />:
                    </text>
                    <widget colspan="3">
                      <textbox key="{meta:getDPrinCntySvdNm()}" width="285" case="UPPER" max="40" tab="{meta:getDPwsStTypeCd()},{meta:getDPrinCitySvdNm()}" field="String" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'stateType')" />:
                    </text>
                    <textbox key="{meta:getDPwsStTypeCd()}" width="33" max="4" tab="{meta:getActivityStatusCd()},{meta:getDPrinCntySvdNm()}" field="String" />
                  </row>
                </TablePanel>
              </VerticalPanel>
              <VerticalPanel>
                <TablePanel style="Form">
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'status')" />:
                    </text>
                    <textbox key="{meta:getActivityStatusCd()}" width="12" case="UPPER" max="1" tab="{meta:getDPopulationCount()},{meta:getDPwsStTypeCd()}" field="String" />
                    <widget colspan="2">
                      <text style="Prompt">
                        <xsl:value-of select="resource:getString($constants,'population')" />:
                      </text>
                    </widget>
                    <widget colspan="3">
                      <textbox key="{meta:getDPopulationCount()}" width="61" tab="{meta:getActivityRsnTxt()},{meta:getActivityStatusCd()}" field="Integer" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'reason')" />:
                    </text>
                    <widget colspan="7">
                      <textarea key="{meta:getActivityRsnTxt()}" width="275" height="50" tab="{meta:getStartDay()},{meta:getDPopulationCount()}" />
                    </widget>
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'startDay')" />:
                    </text>
                    <textbox key="{meta:getStartDay()}" width="19" max="2" tab="{meta:getStartMonth()},{meta:getActivityRsnTxt()}" field="Integer" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'month')" />:
                    </text>
                    <textbox key="{meta:getStartMonth()}" width="19" max="2" tab="{meta:getEffBeginDt()},{meta:getStartDay()}" field="Integer" />
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"effectiveBegin")' />:
                    </text>
                    <calendar key="{meta:getEffBeginDt()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getEndDay()},{meta:getStartMonth()}" />
                  </row>
                  <row>
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'endDay')" />:
                    </text>
                    <textbox key="{meta:getEndDay()}" width="19" max="2" tab="{meta:getEndMonth()},{meta:getEffBeginDt()}" field="Integer" />
                    <text style="Prompt">
                      <xsl:value-of select="resource:getString($constants,'month')" />:
                    </text>
                    <textbox key="{meta:getEndMonth()}" width="19" max="2" tab="{meta:getEffEndDt()},{meta:getEndDay()}" field="Integer" />
                    <text style="Prompt">
                      <xsl:value-of select='resource:getString($constants,"effectiveEnd")' />:
                    </text>
                    <calendar key="{meta:getEffEndDt()}" begin="0" end="2" width="90" pattern="{resource:getString($constants,'datePattern')}" tab="{meta:getNumber0()},{meta:getEndMonth()}" />
                  </row>
                </TablePanel>
              </VerticalPanel>
            </HorizontalPanel>
<!-- TAB PANEL -->
            <TabPanel key="tabPanel" width="729" height="270">
<!-- TAB 1 -->
              <tab key="facilityTab" text="{resource:getString($constants,'facility')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="facilityTable" width="725" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getFacilityName()}" width="250" header="{resource:getString($constants,'name')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getFacilityTypeCode()}" width="50" header="{resource:getString($constants,'type')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getFacilityStAsgnIdentCd()}" width="70" header="{resource:getString($constants,'stateId')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getFacilityActivityStatusCd()}" width="40" header="{resource:getString($constants,'status')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getFacilityWaterTypeCode()}" width="70" header="{resource:getString($constants,'waterType')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getFacilityAvailabilityCode()}" width="60" header="{resource:getString($constants,'available')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getFacilityIdentificationCd()}" width="90" header="{resource:getString($constants,'identification')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getFacilityDescriptionText()}" width="180" header="{resource:getString($constants,'description')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getFacilitySourceTypeCode()}" width="50" header="{resource:getString($constants,'source')}">
                      <textbox field="String" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>
<!-- TAB 2 -->
              <tab key="addressTab" text="{resource:getString($constants,'address')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="addressTable" width="707" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getAddressTypeCode()}" width="60" header="{resource:getString($constants,'type')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressActiveIndCd()}" width="50" header="{resource:getString($constants,'active')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getAddressName()}" width="250" header="{resource:getString($constants,'name')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getAddressAddressLineOneText()}" width="200" header="{resource:getString($constants,'addr1')}">
                      <textbox case="UPPER" field="String" />
                    </col>
                    <col key="{meta:getAddressAddressLineTwoText()}" width="200" header="{resource:getString($constants,'addr2')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressAddressCityName()}" width="100" header="{resource:getString($constants,'city')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressAddressStateCode()}" width="60" header="{resource:getString($constants,'state')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressAddressZipCode()}" width="100" header="{resource:getString($constants,'zipcode')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressStateFipsCode()}" width="60" header="{resource:getString($constants,'fips')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getAddressPhoneNumber()}" width="90" header="{resource:getString($constants,'phone')}">
                      <textbox mask="{resource:getString($constants,'phoneWithExtensionPattern')}" field="String" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>
<!-- TAB 3 -->
              <tab key="monitorTab" text="{resource:getString($constants,'monitor')}">
                <VerticalPanel padding="0" spacing="0">
                  <table key="monitorTable" width="707" rows="11" vscroll="ALWAYS" hscroll="ALWAYS">
                    <col key="{meta:getMonitorStAsgnIdentCd()}" width="60" header="{resource:getString($constants,'type')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getMonitorName()}" width="200" header="{resource:getString($constants,'name')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getMonitorTiaanlgpTiaanlytName()}" width="250" header="{resource:getString($constants,'series')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getMonitorNumberSamples()}" width="60" header="{resource:getString($constants,'numSample')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getMonitorCompBeginDate()}" width="80" header="{resource:getString($constants,'beginDate')}">
                      <textbox pattern="{resource:getString($constants,'datePattern')}" field="Date" />
                    </col>
                    <col key="{meta:getMonitorCompEndDate()}" width="80" header="{resource:getString($constants,'endDate')}">
                      <textbox pattern="{resource:getString($constants,'datePattern')}" field="Date" />
                    </col>
                    <col key="{meta:getMonitorFrequencyName()}" width="130" header="{resource:getString($constants,'frequency')}">
                      <textbox field="String" />
                    </col>
                    <col key="{meta:getMonitorPeriodName()}" width="90" header="{resource:getString($constants,'period')}">
                      <textbox field="String" />
                    </col>
                  </table>
                </VerticalPanel>
              </tab>
            </TabPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
