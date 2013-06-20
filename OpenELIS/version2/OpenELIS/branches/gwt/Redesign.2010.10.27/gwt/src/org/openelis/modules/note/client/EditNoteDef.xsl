

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

    <screen name="{resource:getString($constants,'standardNoteSelection')}">
      <VerticalPanel padding="0" spacing="0">

        <VerticalPanel padding="0" spacing="0" style="WhiteContentPanel">
          <TablePanel style="Form">
            <row>
              <text key="subjectText" style="Prompt">Subject:</text>
              <textbox key="subject" width="500" />
            </row>
            <row>
              <widget valign="top">
                <text style="Prompt">Text:</text>
              </widget>
              <textarea key="text" width="500" height="150" />
            </row>
            <row></row>
          </TablePanel>
          <TablePanel width="500" style="Form">
            <row>
              <HorizontalPanel spacing="0">
                <HorizontalPanel spacing="3">
                  <textbox key="findTextBox" width="190" />
                </HorizontalPanel>
                <button key="findButton" icon="FindButtonImage" text="{resource:getString($constants,'find')}" style="Button" action="find"/>
              </HorizontalPanel>
              <widget halign="right">
                <button key="pasteButton" icon="pasteIcon" text="{resource:getString($constants,'paste')}" style="Button" action="paste"/>
              </widget>
            </row>
            <row>
              <widget halign="left" valign="top">
                <tree key="noteTree" rows="10" vscroll="ALWAYS" hscroll="ALWAYS" style="ScreenTableWithSides">
                  <columns>
                    <col width="230" header="Category/Name" />
                  </columns>
                  <node key="category">
                    <col>
                      <label />
                    </col>
                  </node>
                  <node key="note">
                    <col>
                      <label />
                    </col>
                  </node>
                </tree>
              </widget>
              <widget halign="right" valign="top">
                <textarea key="preview" width="286" height="230" style="ScreenTableWithSides,ScreenTextArea" />
              </widget>
            </row>
          </TablePanel>
        </VerticalPanel>

<!--button panel code-->

        <AbsolutePanel align="center" spacing="0" style="BottomButtonPanelContainer">
          <HorizontalPanel>
            <xsl:call-template name="okButton"/>
            <xsl:call-template name="cancelButton"/>
          </HorizontalPanel>
        </AbsolutePanel>

<!--end button panel-->

      </VerticalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>