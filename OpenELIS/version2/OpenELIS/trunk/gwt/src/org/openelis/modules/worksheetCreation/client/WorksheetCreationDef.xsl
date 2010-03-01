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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCreationMeta">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCreation" name="{resource:getString($constants,'worksheetCreation')}">
  	  <VerticalPanel padding="0" spacing="0">
	    <AbsolutePanel spacing="0" style="ButtonPanelContainer">
	      <HorizontalPanel>
		    <appButton key="saveButton" style="ButtonPanelButton" action="save">
		      <HorizontalPanel>
		        <AbsolutePanel style="SaveButtonImage" />
		        <text>
		          <xsl:value-of select="resource:getString($constants,'save')" />
		        </text>
		      </HorizontalPanel>
		    </appButton>
		    <appButton key="exitButton" style="ButtonPanelButton" action="exit">
		      <HorizontalPanel>
		        <AbsolutePanel style="ExitButtonImage" />
		        <text>
		          <xsl:value-of select="resource:getString($constants,'exit')" />
		        </text>
		      </HorizontalPanel>
		    </appButton>
	      </HorizontalPanel>
	    </AbsolutePanel>
        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
	          <text style="Prompt">
	            <xsl:value-of select="resource:getString($constants,'worksheetNumber')" />:
	          </text>
	          <textbox key="{meta:getWorksheetId()}" width="100px" case="LOWER" field="String"/>
	          <text style="Prompt">
	            <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
	          </text>
	          <HorizontalPanel>
	            <textbox key="{meta:getWorksheetRelatedWorksheetId()}" width="100px" case="LOWER" field="String"/>
	            <appButton key="lookupWorksheetButton" style="LookupButton" action="lookupWorksheet" tab="worksheetItemTable,removeRowButton">
                  <AbsolutePanel style="LookupButtonImage" />
	            </appButton>
	          </HorizontalPanel>
            </row>
          </TablePanel>
          <table key="worksheetItemTable" width="800" maxRows="9" showScroll="ALWAYS" tab="insertQCWorksheetButton,lookupWorksheetButton" title="" style="ScreenTableWithSides">
            <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}" sort="false">
              <label />
            </col>
            <col key="{meta:getSampleAccessionNumber()}" width="90" header="{resource:getString($constants,'accessionNum')}" sort="true">
              <label />
            </col>
            <col key="{meta:getSampleDescription()}" width="110" header="{resource:getString($constants,'description')}" sort="true">
              <label />
            </col>
            <col key="{meta:getWorksheetAnalysisWorksheetAnalysisId}" width="90" header="{resource:getString($constants,'qcLink')}" sort="false">
              <dropdown width="70" field="Integer"/>
            </col>
            <col key="{meta:getAnalysisTestName()}" width="100" header="{resource:getString($constants,'test')}" sort="true">
              <label />
            </col>
            <col key="{meta:getAnalysisTestMethodName()}" width="100" header="{resource:getString($constants,'method')}" sort="true">
              <label />
            </col>
            <col key="{meta:getAnalysisStatusId()}" width="75" header="{resource:getString($constants,'status')}" sort="true">
              <dropdown width="55"/>
            </col>
            <col key="{meta:getSampleCollectionDate()}" width="75" header="{resource:getString($constants,'collected')}" sort="true">
              <calendar pattern="{resource:getString($constants,'datePattern')}" begin="0" end="2"/>
            </col>
            <col key="{meta:getSampleReceivedDate()}" width="110" header="{resource:getString($constants,'received')}" sort="true">
              <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
            </col>
            <col key="{meta:getAnalysisDueDays()}" width="50" header="{resource:getString($constants,'due')}" sort="true">
              <label />
            </col>
            <col key="{meta:getAnalysisExpireDate()}" width="110" header="{resource:getString($constants,'expire')}" sort="true">
              <calendar pattern="{resource:getString($constants,'dateTimePattern')}" begin="0" end="4"/>
            </col>
          </table>
          <widget style="TableFooterPanel">
            <HorizontalPanel>
              <appButton key="insertQCWorksheetButton" style="Button" action="insertQCWorksheet" tab="insertQCLookupButton,worksheetItemTable">
                <HorizontalPanel>
                  <AbsolutePanel style="AddRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'insertQCWorksheet')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <appButton key="insertQCLookupButton" style="Button" action="insertQCLookup" tab="removeRowButton,insertQCWorksheetButton">
                <HorizontalPanel>
                  <AbsolutePanel style="AddRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'insertQCLookup')" />
                  </text>
                </HorizontalPanel>
              </appButton>
              <appButton key="removeRowButton" style="Button" action="removeRow" tab="{meta:getWorksheetRelatedWorksheetId()},insertQCLookupButton">
                <HorizontalPanel>
                  <AbsolutePanel style="RemoveRowButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'removeRow')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </HorizontalPanel>
          </widget>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
