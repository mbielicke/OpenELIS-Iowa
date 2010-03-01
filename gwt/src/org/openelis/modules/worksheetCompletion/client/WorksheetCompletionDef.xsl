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
  xmlns:meta="xalan://org.openelis.meta.WorksheetCompletionMeta">

  <xsl:template match="doc">
    <xsl:variable name="language" select="locale" />
    <xsl:variable name="props" select="props" />
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
    <screen id="WorksheetCompletion" name="{resource:getString($constants,'worksheetCompletion')}">
  	  <VerticalPanel padding="0" spacing="0">
	    <AbsolutePanel spacing="0" style="ButtonPanelContainer">
	      <HorizontalPanel>
		    <appButton key="printButton" style="ButtonPanelButton" action="print">
		      <HorizontalPanel>
		        <AbsolutePanel style="PrintButtonImage" />
		        <text>
		          <xsl:value-of select="resource:getString($constants,'print')" />
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
              <textbox key="{meta:getId()}" width="100px" case="LOWER" field="String"/>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'status')" />:
              </text>
              <dropdown key="{meta:getStatusId()}" width="100px" popWidth="100px" tab="lookupWorksheetButton,tabPanel" field="Integer" />
            </row>
            <row>
              <text style="Prompt">
                <xsl:value-of select="resource:getString($constants,'relatedWorksheetNumber')" />:
              </text>
              <HorizontalPanel>
                <textbox key="{meta:getRelatedWorksheetId()}" width="100px" case="LOWER" field="String"/>
	            <appButton key="lookupWorksheetButton" style="LookupButton" action="lookupWorksheet" tab="tabPanel,{meta:getStatusId()}">
                  <AbsolutePanel style="LookupButtonImage" />
	            </appButton>
	          </HorizontalPanel>
            </row>
          </TablePanel>
<!-- TAB PANEL -->
          <TabPanel key="tabPanel" width="605" height="285">
<!-- TAB 1 -->
            <tab key="worksheetItemTab" tab="worksheetItemTable,worksheetItemTable" text="{resource:getString($constants,'worksheet')}">
              <VerticalPanel padding="0" spacing="0">
                <table key="worksheetItemTable" width="587" maxRows="10" showScroll="ALWAYS" tab="{meta:getId()},{meta:getId()}">
                  <col key="{meta:getWorksheetItemPosition()}" width="50" header="{resource:getString($constants,'position')}" sort="false">
                    <label />
                  </col>
                  <col key="{meta:getWorksheetAnalysisAccessionNumber()}" width="90" header="{resource:getString($constants,'accessionNum')}" sort="true">
                    <label />
                  </col>
<!--
                  <col key="{meta:getSampleDescription()}" width="110" header="{resource:getString($constants,'description')}" sort="true">
                    <label />
                  </col>
-->
                </table>
                <widget style="TableButtonFooter">
                  <HorizontalPanel>
                    <appButton key="editMultipleButton" style="Button">
                      <HorizontalPanel>
                        <AbsolutePanel style="EditMultipleButtonImage" />
                        <text>
                          <xsl:value-of select="resource:getString($constants,'editMultiple')" />
                        </text>
                      </HorizontalPanel>
                    </appButton>
                  </HorizontalPanel>
                </widget>
              </VerticalPanel>
            </tab>
<!-- TAB 2 -->
            <tab key="notesTab" tab="standardNoteButton,standardNoteButton" text="{resource:getString($constants,'note')}">
              <VerticalPanel padding="0" spacing="0">
                <notes key="notesPanel" width="604" height="247" />
                <appButton key="standardNoteButton" style="Button" tab="{meta:getId()},{meta:getId()}">
                  <HorizontalPanel>
                    <AbsolutePanel style="StandardNoteButtonImage" />
                    <text>
                      <xsl:value-of select="resource:getString($constants,'addNote')" />
                    </text>
                  </HorizontalPanel>
                </appButton>
              </VerticalPanel>
            </tab>
          </TabPanel>
        </VerticalPanel>
      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>
