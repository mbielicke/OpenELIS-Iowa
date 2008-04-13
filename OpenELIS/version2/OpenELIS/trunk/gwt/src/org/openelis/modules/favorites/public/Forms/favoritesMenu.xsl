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
 <screen id="favorites" serviceUrl="OpenELISService" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<display>
	<menuPanel layout="vertical" style="topMenuContainer">
	  <xsl:for-each select="favorite">
	    <xsl:variable name="icon"><xsl:value-of select="@label"/>Icon</xsl:variable>
	    <xsl:variable name="label"><xsl:value-of select="@label"/></xsl:variable>
	    <xsl:variable name="description"><xsl:value-of select="@label"/>Description</xsl:variable>
	    <xsl:variable name="value"><xsl:value-of select="@value"/></xsl:variable>
        <menuItem style="TopMenuRowContainer" enabled="false"
	              hover="Hover">
	      <menuDisplay>
	        <panel layout="table" xsi:type="Table">
	          <row>
	            <widget>
	              <panel xsi:type="Absolute" style="{$icon}" layout="absolute"/>
	            </widget>
	            <widget>
	              <panel layout="vertical" xsi:type="Panel">
	                <widget>
	                  <label text="{$label}" style="topMenuItem,locked"/>
	                </widget>
	                <widget>
	                  <label text="{$description}" style="topMenuItemDesc,locked"/>
	                </widget>
	             </panel>
	           </widget>
	           <widget>
	             <panel layout="horizontal" xsi:type="Panel">
	               <widget>
	                 <check key="{$value}"/>
	               </widget>
	               <widget>
	                 <appButton action="moveUp" key="arrowUp">
	                   <widget>
	                   <text>up</text>
	                   </widget>
	                 </appButton>
	               </widget>
	               <widget>
	                 <appButton action="moveDown" key="arrowDown">
	                   <widget>
	                   <text>down</text>
	                   </widget>
	                 </appButton>
	               </widget>
	             </panel>
	           </widget>
	         </row>  
	        </panel>
	      </menuDisplay>
	    </menuItem>
	  </xsl:for-each>
	</menuPanel>
   </display>
   <rpc>
   </rpc>
 </screen>
</xsl:template>
</xsl:stylesheet>