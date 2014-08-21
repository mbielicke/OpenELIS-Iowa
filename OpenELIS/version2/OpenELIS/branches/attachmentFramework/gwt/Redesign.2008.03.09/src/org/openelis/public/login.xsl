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

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="login">
    <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle('org.openelis.modules.main.server.constants.OpenELISConstants',locale:new(string($language)))"/>
<html>
<head>
    <title>OpenELIS Authentication Service</title>
    <script>
        function submit() {
            document.login_form.submit();
        }
   
        function focusLogin() {
            document.login_form.username.focus();
        }
    </script>
    <style type="text/css">
        .inputfield
        {
            border-style:solid; border-width:1px;
            border-color:black;
            padding-left: 0.4em;
        }
        .inputbackground
        {
            background-image: url("signInBg.jpg");
            background-repeat:no-repeat;
            width: 450px;
        }
        .submit
        {
            background-image: url("signIn.gif");
            border-width:0px;
            width: 75px;
            height: 20px;
            padding-left: 0.4em;
        }
    </style>
</head>

<body onLoad="focusLogin()">


    <font face="Arial,Helvetica">
    </font>
    <table align="center">
        <tr><td><br/></td></tr>
        <tr><td><br/></td></tr>
        <tr><td><br/></td></tr>
        <tr><td><br/></td></tr>
        <tr><td><br/></td></tr>
        <tr>
            <td>
                <form method="post" name="login_form" autocomplete="off" action="{action}">
                    <center>
                        <table border="0" cellspacing="5" align='center' class="inputbackground">
                            <tr><td colspan='2' style="height: 160px;"></td></tr>
                            <tr><th align="right" width='160'><xsl:value-of select="resource:getString($constants,'username')"/></th>
                                <td align="left"><input type="text" name="username" class="inputfield" value='' onChange="javascript:this.value=this.value.toLowerCase();"/></td>
                            </tr>
                            <tr><th align="right"><xsl:value-of select="resource:getString($constants,'password')"/></th>
                                <td align="left"><input type="password" name="password" class="inputfield"/></td>
                            </tr>
                            <tr><td align="right"></td>
                                <td align="left"><input type="submit" value="" border="0" class="submit"/></td>
                            </tr>
                            <tr>
                              <td colspan='2' align='left'></td>
                            </tr>
                            <tr><td colspan='2'><br/></td></tr>
                            <tr><td colspan='2'><br/></td></tr>
                        </table>
                    </center>
               </form>
            </td>
        </tr>
        <xsl:for-each select="error">
 		 <tr>
 		    <xsl:variable name="errorKey" select="."/>
		    <td style="color:red;"><xsl:value-of select="resource:getString($constants,$errorKey)"/></td>
		  </tr>
		</xsl:for-each>
    </table>
</body>
</html>
</xsl:template>


</xsl:stylesheet>