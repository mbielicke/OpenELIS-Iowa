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
	xmlns:xalan="http://xml.apache.org/xalan" 
	xmlns:resource="xalan://org.openelis.util.UTFResource"
	xmlns:locale="xalan://java.util.Locale"
	xmlns:envMeta="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap"
    xmlns:addressMeta="xalan://org.openelis.meta.AddressMeta"
	extension-element-prefixes="resource" version="1.0">
	<xsl:import href="button.xsl" />

	<xalan:component prefix="resource">
		<xalan:script lang="javaclass"
			src="xalan://org.openelis.util.UTFResource" />
	</xalan:component>

	<xalan:component prefix="locale">
		<xalan:script lang="javaclass" src="xalan://java.util.Locale" />
	</xalan:component>
	
	<xalan:component prefix="envMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.metamap.SampleEnvironmentalMetaMap"/>
  	</xalan:component>
  
  	<xalan:component prefix="addressMeta">
    	<xalan:script lang="javaclass" src="xalan://org.openelis.meta.AddressMeta"/>
  	</xalan:component>
	
	<xsl:template match="doc">
		<xsl:variable name="env" select="envMeta:new()"/>
	    <xsl:variable name="address" select="envMeta:getAddress($env)"/>
		<xsl:variable name="language">
		<xsl:value-of select="locale" />
		</xsl:variable>
		<xsl:variable name="props">
			<xsl:value-of select="props" />
		</xsl:variable>
		<xsl:variable name="constants"
			select="resource:getBundle(string($props),locale:new(string($language)))" />
		<screen id="SampleLocationPicker" name="{resource:getString($constants,'sampleLocation')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
			<display>
				<VerticalPanel spacing="0" padding="0">
					<VerticalPanel style="WhiteContentPanel" spacing="0" padding="0" width="300px">
						<TablePanel style="Form">
					<row>
					<text style="Prompt"><xsl:value-of select='resource:getString($constants,"location")'/>:</text>
						<widget colspan="3">
							<textbox case="mixed" key="{envMeta:getSamplingLocation($env)}" width="214px" max="30" alwaysEnabled="true" tab="{addressMeta:getMultipleUnit($address)},{addressMeta:getCountry($address)}"/>
						</widget>
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"aptSuite")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addressMeta:getMultipleUnit($address)}" width="214px" max="30" alwaysEnabled="true" tab="{addressMeta:getStreetAddress($address)},{envMeta:getSamplingLocation($env)}"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"address")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addressMeta:getStreetAddress($address)}" width="214px" max="30" alwaysEnabled="true" tab="{addressMeta:getCity($address)},{addressMeta:getMultipleUnit($address)}"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"city")'/>:</text>
						<widget colspan="3">
							<textbox case="upper" key="{addressMeta:getCity($address)}" width="214px" max="30" alwaysEnabled="true" tab="{addressMeta:getState($address)},{addressMeta:getStreetAddress($address)}"/>
						</widget>		
					</row>
					<row>
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"state")'/>:</text>
						<widget>
							<dropdown case="upper" key="{addressMeta:getState($address)}" width="40px" alwaysEnabled="true" tab="{addressMeta:getZipCode($address)},{addressMeta:getCity($address)}"/>
						</widget>
						
						<text style="Prompt"><xsl:value-of select='resource:getString($constants,"zipcode")'/>:</text>
						<widget>
							<textbox case="upper" key="{addressMeta:getZipCode($address)}" width="76px" max="30" alwaysEnabled="true" tab="{addressMeta:getCountry($address)},{addressMeta:getState($address)}"/>
						</widget>
					</row>
					<row>
					<text style="Prompt"><xsl:value-of select="resource:getString($constants,'country')"/>:</text>
					<widget colspan="3">
						<dropdown case="mixed" key="{addressMeta:getCountry($address)}" width="198px" alwaysEnabled="true" tab="{envMeta:getSamplingLocation($env)},{addressMeta:getZipCode($address)}"/>
					</widget>
					</row>
					</TablePanel>
					</VerticalPanel>
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
				<string key="{envMeta:getSamplingLocation($env)}" required="false"/>
				<string key="{addressMeta:getMultipleUnit($address)}"/>
				<string key="{addressMeta:getStreetAddress($address)}"/>
				<string key="{addressMeta:getCity($address)}"/>
				<dropdown key="{addressMeta:getState($address)}"/>
				<string key="{addressMeta:getZipCode($address)}"/>
				<dropdown key="{addressMeta:getCountry($address)}"/>
			</rpc>
		</screen>
	</xsl:template>
</xsl:stylesheet>
