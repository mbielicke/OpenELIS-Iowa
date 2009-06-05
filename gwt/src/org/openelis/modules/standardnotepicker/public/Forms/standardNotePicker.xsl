
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
			<display>
				<VerticalPanel spacing="0" padding="0">
					<HorizontalPanel style="WhiteContentPanel" spacing="0" padding="0">
					<TablePanel style="Form">
				<row>
					<text style="Prompt">Subject:</text>
					<textbox case="mixed" key="subject" width="250px" enabledStates="default"/>
				</row>
				<row>
					<text style="Prompt">Text:</text>
					<textarea case="mixed" key="text" width="250px" height="415px" enabledStates="default"/>
				</row>
				<row>
				</row>
				</TablePanel>
					<VerticalPanel spacing="0" padding="0" width="250px">
						<HorizontalPanel spacing="0">
							<HorizontalPanel spacing="3">
								<textbox key="findTextBox" width="190px" showError="false" enabledStates="default" />
							</HorizontalPanel>
							<appButton action="find" onclick="this" style="Button" key="findButton" enabledStates="default">
								<HorizontalPanel>
									<AbsolutePanel style="FindButtonImage" />
									<text>
										<xsl:value-of select='resource:getString($constants,"find")' />
									</text>
								</HorizontalPanel>
							</appButton>
						</HorizontalPanel>
							<tree-table key="noteTree" width="auto" showScroll="ALWAYS" manager="this" treeCall="this" maxRows="10" enable="true" showError="false">
								<headers>Category/Name</headers>
								<widths>230</widths>
								<leaves>
									<leaf type="top">
										<editors>
											<label />
										</editors>
										<fields>
											<string />
										</fields>
									</leaf>
									<leaf type="leaf">
										<editors>
											<label />
										</editors>
										<fields>
											<string />
										</fields>
									</leaf>
								</leaves>
							</tree-table>
						<appButton action="move" onclick="this" style="Button" key="moveButton" enabledStates="default">
								<widget>
                					<text><xsl:value-of select='resource:getString($constants,"moveLeft")'/> </text>
							    </widget>			
							</appButton>
							<textarea key="moveText" width="255px" height="150px" showError="false" />
					</VerticalPanel>
					</HorizontalPanel>
					<!--button panel code-->
					<AbsolutePanel spacing="0" style="BottomButtonPanelContainer"
						align="center">
						<buttonPanel key="buttons">
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
						</buttonPanel>
					</AbsolutePanel>
					<!--end button panel-->
				</VerticalPanel>
			</display>
			<rpc key="display">
				<tree key="noteTree" />
			</rpc>
			<!--
			<rpc key="queryByNameDescription">
				<queryString key="{standardNoteMeta:getName($meta)}"
					type="string" required="false" />
				<queryString key="{standardNoteMeta:getDescription($meta)}"
					required="false" />
			</rpc>
			-->
		</screen>
	</xsl:template>
</xsl:stylesheet>
