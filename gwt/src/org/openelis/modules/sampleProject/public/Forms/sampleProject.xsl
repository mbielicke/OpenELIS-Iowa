
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xalan" xmlns:resource="xalan://org.openelis.util.UTFResource"
	xmlns:locale="xalan://java.util.Locale"
	extension-element-prefixes="resource" version="1.0">
	<xsl:import href="button.xsl" />

	<xalan:component prefix="resource">
		<xalan:script lang="javaclass"
			src="xalan://org.openelis.util.UTFResource" />
	</xalan:component>

	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale" />
	</xalan:component>

	<xsl:template match="doc">
			<xsl:variable name="language">
			<xsl:value-of select="locale" />
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props" />
		</xsl:variable>
		<xsl:variable name="constants"
			select="resource:getBundle(string($props),locale:new(string($language)))" />
		<screen id="SampleProjectPicker" name="{resource:getString($constants,'sampleProject')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<VerticalPanel spacing="0" padding="0">
					<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0" width="300px">
						<table key="sampleProjectTable" width="auto" maxRows="10" title="" showScroll="ALWAYS" tab="sampleProjectTable,sampleProjectTable">
							<col header="Name" width="120">
								<autoComplete cat="project" case="upper" width="100px">		
									<col header="Name" width="115"/>
									<col header="Desc" width="190"/>		
								</autoComplete>
							</col>
							<col header="Description" width="160">
								<label/>
							</col>
							<col header="Is Perm" width="60">
								<check/>
							</col>
						</table>
						<HorizontalPanel style="WhiteContentPanel">
                      <widget halign="center" style="WhiteContentPanel">
                      <appButton action="projectAdd" key="projectAddButton" onclick="this" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="AddRowButtonImage"/>
                          <text><xsl:value-of select="resource:getString($constants,'addRow')"/></text>
                        </HorizontalPanel>
                      </appButton>
                      </widget>
                      <widget halign="center" style="WhiteContentPanel">
                      <appButton action="projectRemove" key="projectRemoveButton" onclick="this" style="Button">
                        <HorizontalPanel>
                          <AbsolutePanel style="RemoveRowButtonImage"/>
                          <text><xsl:value-of select="resource:getString($constants,'removeRow')"/></text>
                        </HorizontalPanel>

                      </appButton>
                      </widget>
                    </HorizontalPanel>
					</VerticalPanel>
					<!--button panel code-->
					<AbsolutePanel spacing="0" style="BottomButtonPanelContainer"
						align="center">
						<HorizontalPanel>
							<xsl:call-template name="popupSelectButton">
								<xsl:with-param name="language">
									<xsl:value-of select="language" />
								</xsl:with-param>
							</xsl:call-template>
							<xsl:call-template name="popupCancelButton">
								<xsl:with-param name="language">
									<xsl:value-of select="language" />
								</xsl:with-param>
							</xsl:call-template>
						</HorizontalPanel>
					</AbsolutePanel>
					<!--end button panel-->
				</VerticalPanel>
		</screen>
	</xsl:template>
</xsl:stylesheet>
