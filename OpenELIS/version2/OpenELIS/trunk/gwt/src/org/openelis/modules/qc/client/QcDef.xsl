
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
  xmlns:analyte="xalan://org.openelis.meta.AnalyteMeta"
  xmlns:invMeta="xalan://org.openelis.meta.InventoryItemMeta"
  xmlns:meta="xalan://org.openelis.metamap.QcMetaMap"
  xmlns:qcaMeta="xalan://org.openelis.metamap.QcAnalyteParameterMetaMap">

  <xsl:import href="IMPORT/aToZOneColumn.xsl" />
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xalan:component prefix="meta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QcMetaMap" />
  </xalan:component>
  <xalan:component prefix="qcaMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.QcAnalyteMetaMap" />
  </xalan:component>
  <xalan:component prefix="invMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.InventoryItemMeta" />
  </xalan:component>
  <xsl:template match="doc">
    <xsl:variable name="qc" select="meta:new()" />
    <xsl:variable name="qca" select="meta:getQcAnalyte($qc)" />
    <xsl:variable name="item" select="meta:getInventoryItem($qc)" />
    <xsl:variable name="ana" select="qcaMeta:getAnalyte($qca)" />
    <xsl:variable name="language">
      <xsl:value-of select="locale" />
    </xsl:variable>
    <xsl:variable name="props">
      <xsl:value-of select="props" />
    </xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />

<!-- main screen -->

    <screen id="Qc" name="{resource:getString($constants,'QC')}">
      <HorizontalPanel padding="0" spacing="0">

<!--left table goes here -->

        <CollapsePanel key="collapsePanel" style="LeftSidePanel">
          <HorizontalPanel width="225">
            <buttonGroup key="atozButtons">
              <xsl:call-template name="aToZLeftPanelButtons" />
            </buttonGroup>
            <VerticalPanel>
              <table key="atozTable" width="auto" maxRows="20" style="atozTable">
                <col width="95" header="{resource:getString($constants,'name')}">
                  <label />
                </col>
                <col width="95" header="{resource:getString($constants,'lotNumber')}">
                  <label />
                </col>
              </table>
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

<!--button panel code-->

        <VerticalPanel padding="0" spacing="0">
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
            </HorizontalPanel>
          </AbsolutePanel>

<!--end button panel-->

          <VerticalPanel width="620" height="235" padding="0" spacing="0" style="WhiteContentPanel">
            <HorizontalPanel padding="0" spacing="0">
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'name')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getName($qc)}" width="215" case="LOWER" max="30" tab="{meta:getTypeId($qc)},qcAnalyteTable" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'type')" />:
                  </text>
                  <dropdown key="{meta:getTypeId($qc)}" width="100" tab="{invMeta:getName($item)},{meta:getName($qc)}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'inventoryItem')" />:
                  </text>
                  <widget>
                    <autoComplete key="{invMeta:getName($item)}" width="145" tab="{meta:getSource($qc)},{meta:getTypeId($qc)}" field="Integer">
                      <col width="135" header="{resource:getString($constants,'name')}" />
                      <col width="110" header="{resource:getString($constants,'store')}" />
                    </autoComplete>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'source')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getSource($qc)}" width="215" max="30" tab="{meta:getLotNumber($qc)},{invMeta:getName($item)}" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'lotNumber')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getLotNumber($qc)}" width="215" max="30" tab="{meta:getIsSingleUse($qc)},{meta:getSource($qc)}" />
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"singleUse")' />:
                  </text>
                  <check key="{meta:getIsSingleUse($qc)}" tab="{meta:getPreparedDate($qc)},{meta:getLotNumber($qc)}" />
                </row>
              </TablePanel>
              <TablePanel style="Form">
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"preparedDate")' />:
                  </text>
                  <calendar key="{meta:getPreparedDate($qc)}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getPreparedVolume($qc)},{meta:getIsSingleUse($qc)}" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedVolume')" />:
                  </text>
                  <widget colspan="6">
                    <textbox key="{meta:getPreparedVolume($qc)}" width="100" tab="{meta:getPreparedUnitId($qc)},{meta:getPreparedDate($qc)}" field="Double"/>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedUnit')" />:
                  </text>
                  <dropdown key="{meta:getPreparedUnitId($qc)}" width="150" tab="{meta:getPreparedById($qc)},{meta:getPreparedVolume($qc)}" field="Integer" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select="resource:getString($constants,'preparedBy')" />:
                  </text>
                  <widget>
                    <autoComplete key="{meta:getPreparedById($qc)}" width="145" tab="{meta:getUsableDate($qc)},{meta:getPreparedUnitId($qc)}" field="Integer">
                      <col width="145" />
                    </autoComplete>
                  </widget>
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"usableDate")' />:
                  </text>
                  <calendar key="{meta:getUsableDate($qc)}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="{meta:getExpireDate($qc)},{meta:getPreparedById($qc)}" />
                </row>
                <row>
                  <text style="Prompt">
                    <xsl:value-of select='resource:getString($constants,"expireDate")' />:
                  </text>
                  <calendar key="{meta:getExpireDate($qc)}" begin="0" end="4" width="140" pattern="{resource:getString($constants,'dateTimePattern')}" tab="qcAnalyteTable,{meta:getUsableDate($qc)}" />
                </row>
              </TablePanel>
            </HorizontalPanel>
            <VerticalPanel height="10" />
            <VerticalPanel>
              <table key="QcAnalyteTable" width="625" maxRows="10" showScroll="ALWAYS" tab="{meta:getName($qc)},{meta:getExpireDate($qc)}"  style="atozTable">
                <col key="{analyte:getName($ana)}" width="270" align="left" sort="false" header="{resource:getString($constants,'analyte')}">
                  <autoComplete popWidth="auto" field="Integer" required="true">
                    <col width="300" />
                  </autoComplete>
                </col>
                <col key="{qcaMeta:getTypeId($qca)}" width="55" align="left" sort="false" header="{resource:getString($constants,'type')}">
                  <dropdown width="55" field="Integer" required="true" />
                </col>
                <col key="{qcaMeta:getIsTrendable($qca)}" width="55" align="center" sort="false" header="{resource:getString($constants,'trendable')}">
                  <check />
                </col>
                <col key="{qcaMeta:getValue($qca)}" width="400" align="left" sort="false" header="{resource:getString($constants,'expectedValue')}">
                  <textbox max="80" field="String" required="true"/>
                </col>
              </table>
              <widget style="TableButtonFooter">
                <HorizontalPanel>
                  <appButton key="removeAnalyteButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="RemoveRowButtonImage" />
                      <text>
                        <xsl:value-of select="resource:getString($constants,'removeRow')" />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                  <appButton key="dictionaryButton" style="Button">
                    <HorizontalPanel>
                      <AbsolutePanel style="ButtonPanelButton" />
                      <text>
                        <xsl:value-of select='resource:getString($constants,"dictionary")' />
                      </text>
                    </HorizontalPanel>
                  </appButton>
                </HorizontalPanel>
              </widget>
            </VerticalPanel>
          </VerticalPanel>
        </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
