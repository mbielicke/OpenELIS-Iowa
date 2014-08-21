<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.server.constants.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.server.constants.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

  <xsl:template match="doc">
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<screen id="main" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display  constants="OpenELISConstants">
		<panel height="100%" layout="vertical" style="AppBackground" width="100%" xsi:type="Panel">
		<panel layout="absolute" style="topMenuBar">
		<menuPanel layout="horizontal" xsi:type="Panel" style="topBarItemHolder" spacing="0" padding="0">
		    <menuItem>
		        <menuDisplay>
			    	  <label style="topMenuBarItem" text="{resource:getString($constants,'application')}" hover="Hover"/>
				</menuDisplay>
				  <menuPanel style="topMenuContainer" layout="vertical" xsi:type="Panel" position="below">
				    <menuItem style="TopMenuRowContainer" hover="Hover" 
				              icon="preferenceIcon" 
				              label="{resource:getString($constants,'preference')}" 
				              description="{resource:getString($constants,'preferenceDescription')}"/>
					<menuItem style="TopMenuRowContainer" hover="Hover" 
					          icon="favoritesMenuIcon"
					          label="{resource:getString($constants,'favoritesMenu')}"
					          description="{resource:getString($constants,'favoritesMenuDescription')}"/>
					<menuItem style="TopMenuRowContainer" hover="Hover" 
					          icon="logoutIcon"
					          label="{resource:getString($constants,'logout')}"
					          description="{resource:getString($constants,'logoutDescription')}"/>
				  </menuPanel>
		    </menuItem>
		    <menuItem>
		      <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'edit')}" hover="Hover"/>
			  </menuDisplay>
	            <menuPanel style="topMenuContainer" layout="vertical" position="below">
				  <menuItem style="TopMenuRowContainer" hover="Hover" 
				            icon="cutIcon"
				            label="{resource:getString($constants,'cut')}"
				            description=""/>
				  <menuItem style="TopMenuRowContainer" hover="Hover" 
				            icon="copyIcon"
				            label="{resource:getString($constants,'copy')}"
				            description=""/>
	              <menuItem style="TopMenuRowContainer" hover="Hover" 
	                        icon="pasteIcon"
	                        label="{resource:getString($constants,'paste')}"
	                        description=""/>
			    </menuPanel>
			</menuItem>
    	    <menuItem>
    	      <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'sample')}" hover="Hover"/>
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				  <menuItem style="TopMenuRowContainer" hover="Hover" 
				            icon="fullLoginIcon"
				            label="{resource:getString($constants,'fullLogin')}"
				            description="{resource:getString($constants,'fullLoginDescription')}"/>
                  <menuItem style="TopMenuRowContainer" hover="Hover"
                            icon="quickEntryIcon"
                            label="{resource:getString($constants,'quickEntry')}"
                            description="{resource:getString($constants,'quickEntryDescription')}"/>
				  <menuItem style="TopMenuRowContainer" hover="Hover"
				            icon="secondEntryIcon"
				            label="{resource:getString($constants,'secondEntry')}"
				            description="{resource:getString($constants,'secondEntryDescription')}"/>
				  <menuItem style="TopMenuRowContainer"
				            hover="Hover"
				            icon="trackingIcon"
				            label="{resource:getString($constants,'tracking')}"
				            description="{resource:getString($constants,'trackingDescription')}"/>
				  <widget>
					<html>&lt;hr/&gt;</html>
				  </widget>
				  <menuItem style="TopMenuRowContainer"  
				            hover="Hover"
				            icon="projectIcon"
				            label="{resource:getString($constants,'project')}"
				            description="{resource:getString($constants,'projectDescription')}"/>
                  <menuItem style="TopMenuRowContainer" 
                            hover="Hover"
                            icon="providerIcon"
                            label="{resource:getString($constants,'provider')}"
                            description="{resource:getString($constants,'providerDescription')}"
                            onClick="this"
                            value="ProviderScreen"/>
                  <menuItem style="TopMenuRowContainer"  
                            hover="Hover"
                            icon="organizationIcon"
                            label="{resource:getString($constants,'organization')}"
                            description="{resource:getString($constants,'organizationDescription')}"
                            onClick="this"
                            value="OrganizationScreen"/>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'analysis')}"  hover="Hover"/>
		      </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				  <menuItem style="TopMenuRowContainer"  
				            hover="Hover"
				            icon="worksheetCreationIcon"
				            label="{resource:getString($constants,'worksheetCreation')}"
				            description="{resource:getString($constants,'worksheetCreationDescription')}"/>
                  <menuItem style="TopMenuRowContainer" 
                            hover="Hover"
                            icon="worksheetCompletionIcon"
                            label="{resource:getString($constants,'worksheetCompletion')}"
                            description="{resource:getString($constants,'worksheetCompletionDescription')}"/>
                  <menuItem style="TopMenuRowContainer" 
                            hover="Hover"
                            icon="addOrCancelIcon"
                            label="{resource:getString($constants,'addOrCancel')}"
                            description="{resource:getString($constants,'addOrCancelDescription')}"/>
                  <menuItem style="TopMenuRowContainer" 
                            hover="Hover"
                            icon="reviewAndReleaseIcon"
                            label="{resource:getString($constants,'reviewAndRelease')}"
                            description="{resource:getString($constants,'reviewAndReleaseDescription')}"/>
                  <menuItem style="TopMenuRowContainer" 
                            hover="Hover"
                            icon="toDoIcon"
                            label="{resource:getString($constants,'toDo')}"
                            description="{resource:getString($constants,'toDoDescription')}"/>
				  <menuItem style="TopMenuRowContainer" 
				            hover="Hover"
				            icon="labelForIcon"
				            label="{resource:getString($constants,'labelFor')}"
				            description="{resource:getString($constants,'labelForDescription')}"/>
				  <menuItem style="TopMenuRowContainer" 
				            hover="Hover"
				            icon="storageIcon"
				            label="{resource:getString($constants,'storage')}"
				            description="{resource:getString($constants,'storageDescription')}"/>
				  <menuItem style="TopMenuRowContainer" 
				            hover="Hover"
				            icon="QCIcon"
				            label="{resource:getString($constants,'QC')}"
				            description="{resource:getString($constants,'QCDescription')}"/>
				</menuPanel>
			</menuItem>
	        <menuItem>  
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'inventoryOrder')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="orderIcon"
				          label="{resource:getString($constants,'order')}"
				          description="{resource:getString($constants,'orderDescription')}"/>
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="inventoryIcon"
				          label="{resource:getString($constants,'inventory')}"
				          description="{resource:getString($constants,'inventoryDescription')}"/>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'instrument')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="instrumentIcon"
				          label="{resource:getString($constants,'instrument')}"
				          description="{resource:getString($constants,'instrumentDescription')}"/>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'maintenance')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="testIcon"
				          label="{resource:getString($constants,'test')}"
				          description="{resource:getString($constants,'testDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="methodIcon"
				          label="{resource:getString($constants,'method')}"
				          description="{resource:getString($constants,'methodDescription')}"/>
 				<menuItem style="TopMenuRowContainer" 
 				          hover="Hover"
 				          icon="panelIcon"
 				          label="{resource:getString($constants,'panel')}"
 				          description="{resource:getString($constants,'panelDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="QAEventIcon"
				          label="{resource:getString($constants,'QAEvent')}"
				          description="{resource:getString($constants,'QAEventDescription')}"
				          onClick="this"
				          value="QAEventScreen"/>
 				<menuItem style="TopMenuRowContainer"  
 				          hover="Hover"
 				          icon="labelSectionIcon"
 				          label="{resource:getString($constants,'labSection')}"
 				          description="{resource:getString($constants,'labSectionDescription')}"/>
			    <widget>
				   <html>&lt;hr/&gt;</html>
				</widget>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="analyteIcon"
				          label="{resource:getString($constants,'analyte')}"
				          description="{resource:getString($constants,'analyteDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="dictionaryIcon"
				          label="{resource:getString($constants,'dictionary')}"
				          description="{resource:getString($constants,'dictionaryDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="auxiliaryPromptIcon"
				          label="{resource:getString($constants,'auxiliaryPrompt')}"
				          description="{resource:getString($constants,'auxiliaryPromptDescription')}"/>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
			    <menuItem style="TopMenuRowContainer"  
			              hover="Hover"
			              icon="barcodeLabelIcon"
			              label="{resource:getString($constants,'barcodeLabel')}"
			              description="{resource:getString($constants,'barcodeLabelDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="standardNoteIcon"
				          label="{resource:getString($constants,'standardNote')}"
				          description="{resource:getString($constants,'standardNoteDescription')}"/>
				<menuItem style="TopMenuRowContainer"  
				          hover="Hover"
				          icon="trailerForTestIcon"
				          label="{resource:getString($constants,'trailerForTest')}"
				          description="{resource:getString($constants,'trailerForTestDescription')}"/>
			    <widget>
					<html>&lt;hr/&gt;</html>
			    </widget>
			    <menuItem style="TopMenuRowContainer"  
			              hover="Hover"
			              icon="storageUnitIcon"
			              label="{resource:getString($constants,'storageUnit')}"
			              description="{resource:getString($constants,'storageUnitDescription')}"
			              onClick="this"
			              value="StorageUnitScreen"/>
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="storageLocationIcon"
				          label="{resource:getString($constants,'storageLocation')}"
				          description="{resource:getString($constants,'storageLocationDescription')}"
				          onClick="this"
				          value="StorageLocationScreen"/>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="instrumentIcon"
				          label="{resource:getString($constants,'instrument')}"
				          description="{resource:getString($constants,'instrumentMainDescription')}"/>
				<widget>
					<html>&lt;hr/&gt;</html>
				</widget>
			    <menuItem style="TopMenuRowContainer"
			              hover="Hover"
			              icon="scriptletIcon"
			              label="{resource:getString($constants,'scriptlet')}"
			              description="{resource:getString($constants,'scriptletDescription')}"/>
				<menuItem style="TopMenuRowContainer" 
				          hover="Hover"
				          icon="systemVariableIcon"
				          label="{resource:getString($constants,'systemVariable')}"
				          description="{resource:getString($constants,'systemVariableDescription')}"/>
				</menuPanel>
		    </menuItem>
	        <menuItem>
	          <menuDisplay>
					<label style="topMenuBarItem" text="{resource:getString($constants,'report')}" hover="Hover"/>
			  </menuDisplay>
				<menuPanel layout="vertical" style="topMenuContainer" position="below">
				    <menuItem style="TopMenuRowContainer"  
				              hover="Hover"
				              icon="finalReportIcon"
				              label="{resource:getString($constants,'finalReport')}"
				              description=""/>
					<menuItem style="TopMenuRowContainer"  
					          hover="Hover"
					          icon="sampleDataExportIcon"
					          label="{resource:getString($constants,'sampleDataExport')}"
					          description=""/>
					<menuItem style="TopMenuRowContainer"  
					          hover="Hover"
					          icon="loginLabelIcon"
					          label="{resource:getString($constants,'loginLabel')}"
					          description=""/>
					<widget>
						<html>&lt;hr/&gt;</html>
					</widget>
					<menuItem style="TopMenuRowContainer"  
					          hover="Hover"
					          icon="referenceIcon"
					          label="{resource:getString($constants,'reference')}"
					          description="">
						  <menuPanel layout="vertical" style="topMenuContainer" position="side">
							<menuItem style="TopMenuRowContainer" 
							          hover="Hover"
							          icon="organizationReportIcon"
							          label="{resource:getString($constants,'organization')}"
							          description=""/>
							<menuItem style="TopMenuRowContainer" 
							          hover="Hover"
							          icon="testReportIcon"
							          label="{resource:getString($constants,'test')}"
							          description=""/>
							<menuItem style="TopMenuRowContainer" 
							          hover="Hover"
							          icon="qaEventReportIcon"
							          label="{resource:getString($constants,'QAEvent')}"
							          description=""/>
						  </menuPanel>
					</menuItem>
					<menuItem style="TopMenuRowContainer" 
						      hover="Hover"
						      icon="summaryIcon"
						      label="{resource:getString($constants,'summary')}"
						      description="">
							<menuPanel layout="vertical" style="topMenuContainer" position="side">
								<menuItem style="TopMenuRowContainer" 
								          hover="Hover"
								          icon="QAByOrganizationIcon"
								          label="{resource:getString($constants,'QAByOrganization')}"
								          description=""/>
								<menuItem style="TopMenuRowContainer" 
								          hover="Hover"
								          icon="sampleDataExportIcon"
								          label="{resource:getString($constants,'testCountByFacility')}"
								          description=""/>
								<menuItem style="TopMenuRowContainer" 
								          hover="Hover"
								          icon="loginLabelIcon"
								          label="{resource:getString($constants,'turnaround')}"
								          description=""/>
								<menuItem style="TopMenuRowContainer" 
								          hover="Hover"
								          icon="referenceIcon"
								          label="{resource:getString($constants,'positiveTestCount')}"
								          description=""/>
							</menuPanel>
					 </menuItem>
				</menuPanel>
		    </menuItem>
        </menuPanel>
       </panel>
	   <panel layout="vertical" width="100%" xsi:type="Panel" spacing="5">
	    	<widget>
		    	<winbrowser key="browser" sizeToWindow="true"/>
	    	</widget>
	   </panel>
	</panel>
	</display>
	<rpc key="display"/>
	<rpc key="query"/>
</screen>
  </xsl:template>
</xsl:stylesheet>