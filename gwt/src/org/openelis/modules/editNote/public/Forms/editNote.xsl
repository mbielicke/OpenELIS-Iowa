
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
	xmlns:locale="xalan://java.util.Locale" xmlns:standardNoteMeta="xalan://org.openelis.metamap.StandardNoteMetaMap"
	extension-element-prefixes="resource" version="1.0">
	<xsl:import href="button.xsl" />

	<xalan:component prefix="resource">
		<xalan:script lang="javaclass"
			src="xalan://org.openelis.util.UTFResource" />
	</xalan:component>

	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale" />
	</xalan:component>

	<xalan:component prefix="standardNoteMeta">
		<xalan:script lang="javaclass"
			src="xalan://org.openelis.metamap.StandardNoteMetaMap" />
	</xalan:component>

	<xsl:template match="doc">
		<xsl:variable name="meta" select="standardNoteMeta:new()" />
		<xsl:variable name="language">
			<xsl:value-of select="locale" />
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props" />
		</xsl:variable>
		<xsl:variable name="constants"
			select="resource:getBundle(string($props),locale:new(string($language)))" />
		<screen id="StandardNotePicker"
			name="{resource:getString($constants,'standardNoteSelection')}"
			serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
				<VerticalPanel spacing="0" padding="0">
					<VerticalPanel style="WhiteContentPanel" spacing="0"
						padding="0">
						<TablePanel style="Form">
							<row>
								<text style="Prompt">Subject:</text>
								<textbox case="mixed" key="subject" width="500px"
									enabledStates="default" />
							</row>
							<row>
								<text style="Prompt">Text:</text>
								<textarea case="mixed" key="text" width="500px" height="150px"
									enabledStates="default" />
							</row>
							<row>
							</row>
						</TablePanel>
						<TablePanel style="Form" width="500px">
							<row>
								<HorizontalPanel spacing="0">
									<HorizontalPanel spacing="3">
										<textbox key="findTextBox" width="190px" showError="false"
											enabledStates="default" />
									</HorizontalPanel>
									<appButton action="find" onclick="this" style="Button"
										key="findButton" enabledStates="default">
										<HorizontalPanel>
											<AbsolutePanel style="FindButtonImage" />
											<text>
												<xsl:value-of select='resource:getString($constants,"find")' />
											</text>
										</HorizontalPanel>
									</appButton>
								</HorizontalPanel>
								<widget align="right">
									<appButton action="paste" onclick="this" style="Button"
										key="pasteButton" enabledStates="default">
										<HorizontalPanel>
											<AbsolutePanel style="pasteIcon" />
											<text>
												<xsl:value-of select='resource:getString($constants,"paste")' />
											</text>
										</HorizontalPanel>
									</appButton>
								</widget>
							</row>
							<row>
								<widget align="left" valign="top">
									<tree key="noteTree" width="auto" showScroll="ALWAYS" maxRows="10">
									<header>
										<col header="Category/Name" width="230" sort="model,category"/>
									</header>
										<leaf key="category">
											<col>
												<label />
											</col>
										</leaf>
										<leaf key="note">
											<col>
												<label />
											</col>
										</leaf>
									</tree>
								</widget>
								<widget align="right" valign="top">
									<textarea key="preview" width="286px" height="230px"
										showError="false" style="ScreenTable,ScreenTextArea" />
								</widget>
							</row>
						</TablePanel>
					</VerticalPanel>
					<!--button panel code-->
					<AbsolutePanel spacing="0" style="BottomButtonPanelContainer" align="center">
		              <HorizontalPanel>
		                <xsl:call-template name="commitButton">
		                  <xsl:with-param name="language">
		                    <xsl:value-of select="language"/>
		                  </xsl:with-param>
		                </xsl:call-template>
		                <xsl:call-template name="abortButton">
		                  <xsl:with-param name="language">
		                    <xsl:value-of select="language"/>
		                  </xsl:with-param>
		                </xsl:call-template>
		              </HorizontalPanel>
		            </AbsolutePanel>
					<!--end button panel-->
				</VerticalPanel>
		</screen>
	</xsl:template>
</xsl:stylesheet>
