<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                xmlns:standardNoteMeta="xalan://org.openelis.meta.StandardNoteMeta" 
                extension-element-prefixes="resource"
                version="1.0">
<xsl:import href="button.xsl"/>
                
  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xalan:component prefix="standardNoteMeta">
    <xalan:script lang="javaclass" src="xalan://org.openelis.meta.StandardNoteMeta"/>
  </xalan:component>
  
  <xsl:template match="doc"> 
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
<screen id="StandardNotePicker" name="{resource:getString($constants,'standardNoteSelection')}" serviceUrl="ElisService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
		<panel layout="vertical" style="WhiteContentPanel" spacing="0" width="300px" xsi:type="Panel">
		<panel layout="horizontal" spacing="0" xsi:type="Panel">
		<panel layout="horizontal" spacing="3" xsi:type="Panel">
		<textbox key="findTextBox" width="200px" showError="false" alwaysEnabled="true"/>
		</panel>
		<appButton action="find" onclick="this" style="Button" key="findButton" alwaysEnabled="true">
			<panel xsi:type="Panel" layout="horizontal">
            	<panel xsi:type="Absolute" layout="absolute" style="FindButtonImage"/>
					<widget>
                		<text><xsl:value-of select='resource:getString($constants,"find")'/></text>
					</widget>
				</panel>
		</appButton>
		</panel>
				<panel key="treeContainer" layout="vertical" height="250px" width="320px" overflow="auto" xsi:type="Panel">
				<!--tree-->
				<pagedtree key="noteTree" vertical="true" height = "250px" width = "320px" itemsPerPage="1000" title=""/>
				</panel>
				<panel layout="horizontal" xsi:type="Panel" spacing="10">
				<widget>
				<!-- text area-->
				<textarea key="noteText" width="300px" height="200px"/>
				</widget>
				</panel>
				
			<!--button panel code-->
		<panel xsi:type="Absolute" layout="absolute" spacing="0" style="ButtonPanelContainer" align="center">
			<widget>
    			<buttonPanel key="buttons">
    			<xsl:call-template name="popupSelectButton"/>
    			<xsl:call-template name="popupCancelButton"/>
				</buttonPanel>
 			</widget>
		</panel>
		<!--end button panel-->
			</panel>
	</display>
	<rpc key="display">
	<tree key="noteTree"/>
	</rpc>
	<rpc key="queryByNameDescription">
 	<queryString key="{standardNoteMeta:name()}" type="string" required="false"/>
  	<queryString key="{standardNoteMeta:description()}" required="false"/>
	</rpc>
</screen>
  </xsl:template>
</xsl:stylesheet>