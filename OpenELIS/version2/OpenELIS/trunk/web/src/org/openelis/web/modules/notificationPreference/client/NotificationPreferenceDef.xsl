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
  xmlns:fn="http://www.w3.org/2005/xpath-functions"
  xmlns:locale="xalan://java.util.Locale"
  xmlns:resource="xalan://org.openelis.util.UTFResource"
  xmlns:xalan="http://xml.apache.org/xalan"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xsi:noNamespaceSchemaLocation="http://openelis.uhl.uiowa.edu/schema/ScreenSchema.xsd"
  xsi:schemaLocation="http://www.w3.org/1999/XSL/Transform http://openelis.uhl.uiowa.edu/schema/XSLTSchema.xsd">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource" />
  </xalan:component>
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale" />
  </xalan:component>
  <xsl:variable name="language">
    <xsl:value-of select="doc/locale" />
  </xsl:variable>
  <xsl:variable name="props">
    <xsl:value-of select="doc/props" />
  </xsl:variable>
  <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))" />
  <xsl:template match="doc">
    <screen id="NotificationPreference" name="{resource:getString($constants,'notificationPreference')}">
      <HorizontalPanel padding="0" spacing="0" style="ContentPanel" width = "800">
        <VerticalPanel>
         <TablePanel style="help">
           <row>
              <html> <![CDATA[<span class = \"helpHeader\"> Setting email notification: </span> <p/> SHL can send an email to specified email addresses whenever a new sample is received or when the result are available. You can choose whether the notification emails are sent for either or both events. <p/>]]></html>
           </row>
         </TablePanel>
          <widget valign="top">
            <table key="orgTable" maxRows="10" showScroll="ALWAYS" style="ScreenTableWithSides" title="" width="auto">
              <col header="{resource:getString($constants,'organization')}" key="organizationId" width="200">
                <dropdown field="Integer" width="250" />
              </col>
              <col header="{resource:getString($constants,'email')}" key="email" width="250">
                <label field="String" />
              </col>
              <col header="{resource:getString($constants,'received')}" key="received" width="57">
                <check/>
              </col>
              <col header="{resource:getString($constants,'released')}" key="released" width="57">
                <check />
              </col>
            </table>
          </widget>
          <HorizontalPanel style="TableFooterPanel">
            <appButton key="addButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="AddRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'add')" />
                </text>
              </HorizontalPanel>
            </appButton>
            <widget halign="center">
              <appButton key="editButton" style="Button">
                <HorizontalPanel>
                  <AbsolutePanel style="UpdateButtonImage" />
                  <text>
                    <xsl:value-of select="resource:getString($constants,'edit')" />
                  </text>
                </HorizontalPanel>
              </appButton>
            </widget>
            <appButton key="removeButton" style="Button">
              <HorizontalPanel>
                <AbsolutePanel style="RemoveRowButtonImage" />
                <text>
                  <xsl:value-of select="resource:getString($constants,'remove')" />
                </text>
              </HorizontalPanel>
            </appButton>
          </HorizontalPanel>
        </VerticalPanel>
            <VerticalPanel height="280" padding="0" spacing="0" style="help" width="340">
              <TablePanel width="100%">
               <row> 
                 <html> <![CDATA[<span class = \"helpHeader\">To add your email address: </span> <ul> <li> Click on the \"Add\" button to open the selection popup screen</li><li>From the selection screen, choose the organization associated with samples</li><li>Check \"For Received Samples\" or \"For Released Samples\" or both to receive notification for each type of event</li><li>Enter a valid email address in \"Email\" entry area</li><li>Click \"OK\" to add this notification or \"Cancel\" to abort</li></ul><p/> <span class = \"helpHeader\">To edit an existing email address: </span> <ul> <li>Click on a row in the table to select it</li><li>Click on \"Edit\" button to open the selection popup screen</li><li>Change the email address, notification preferences, or the organization</li><li>Click \"OK\" to save your changes or \"Cancel\" to abort your changes</li></ul><span class = \"helpHeader\">To remove an email notification:</span> <ul> <li>Click on a row in the table to select it</li><li>Click on \"Remove\" button to remove the email address</li></ul>]]></html>
               </row>               
              </TablePanel>
            </VerticalPanel>
      </HorizontalPanel>
    </screen>
  </xsl:template>
</xsl:stylesheet>