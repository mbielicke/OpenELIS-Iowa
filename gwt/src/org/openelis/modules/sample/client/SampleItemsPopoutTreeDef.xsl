
<!--
		Exhibit A - UIRF Open-source Based Public Software License. The
		contents of this file are subject to the UIRF Open-source Based Public
		Software License(the "License"); you may not use this file except in
		compliance with the License. You may obtain a copy of the License at
		openelis.uhl.uiowa.edu Software distributed under the License is
		distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either
		express or implied. See the License for the specific language
		governing rights and limitations under the License. The Original Code
		is OpenELIS code. The Initial Developer of the Original Code is The
		University of Iowa. Portions created by The University of Iowa are
		Copyright 2006-2008. All Rights Reserved. Contributor(s):
		______________________________________. Alternatively, the contents of
		this file marked "Separately-Licensed" may be used under the terms of
		a UIRF Software license ("UIRF Software License"), in which case the
		provisions of a UIRF Software License are applicable instead of those
		above.
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
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xsl:import href="IMPORT/button.xsl" />
  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" id="SampleTreePopout" name="{resource:getString($constants,'itemsAndAnalyses')}">
      <VerticalPanel padding="0" spacing="0">
        <HorizontalPanel style = "WhiteContentPanel">
          <VerticalPanel>
            <tree key="itemsTestsTree" style = "ScreenTableWithSides" width="auto" maxRows="25" showScroll="ALWAYS">
              <header>
                <col width="30" header=""/>
                <col width="330" header="{resource:getString($constants,'itemAnalyses')}" />
                <col width="180" header="{resource:getString($constants,'typeStatus')}" />
              </header>
              <leaf key="sampleItem">
                <col>
                  <label field="String" />
                </col>
                <col>
                  <label field="String" />
                </col>
              </leaf>
              <leaf key="analysis">
                <col>
                  <check/>
                </col>
                <col>
                  <label field="String" />
                </col>
                <col>
                  <dropdown width="50" case="LOWER" popWidth="110" field="String" />
                </col>                
              </leaf>
            </tree>
          </VerticalPanel>
          <HorizontalPanel width = "10"/>
          <VerticalPanel spacing="0">
            <widget>
              <table key="sampleItemTable" width="auto" style = "ScreenTableWithSides" maxRows="12" showScroll="ALWAYS" title="">
                <col key="include" width="30" header="">
                  <check />
                </col>
                <col key="name" width="35" header="{resource:getString($constants,'item')}">
                  <label width="35" field="String" />
                </col>
              </table>              
            </widget>     
            <widget style="TableButtonFooter">
              <HorizontalPanel>
                <appButton key="moveButton" style="Button">
                  <HorizontalPanel>
                    <AbsolutePanel style="refreshButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'move')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </HorizontalPanel>
            </widget>       
          </VerticalPanel>        
        </HorizontalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>