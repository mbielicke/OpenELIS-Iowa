<!--
 The contents of this file are subject to the Mozilla Public License
 Version 1.1 (the "License"); you may not use this file except in
 compliance with the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/

 Software distributed under the License is distributed on an "AS IS"
 basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 License for the specific language governing rights and limitations under
 the License.
 
 The Original Code is OpenELIS code.
 
 Copyright (C) The University of Iowa.  All Rights Reserved.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xalan="http://xml.apache.org/xalan"
                xmlns:resource="xalan://org.openelis.util.UTFResource"
                xmlns:locale="xalan://java.util.Locale"
                extension-element-prefixes="resource"
                version="1.0">

  <xalan:component prefix="resource">
    <xalan:script lang="javaclass" src="xalan://org.openelis.util.UTFResource"/>
  </xalan:component>
  
  <xalan:component prefix="locale">
    <xalan:script lang="javaclass" src="xalan://java.util.Locale"/>
  </xalan:component>

<xsl:output method="html" encoding="UTF-8" indent="yes"/>

<xsl:template match="login">
      <xsl:variable name="language"><xsl:value-of select="locale"/></xsl:variable>
    <xsl:variable name="props"><xsl:value-of select="props"/></xsl:variable>
    <xsl:variable name="constants" select="resource:getBundle(string($props),locale:new(string($language)))"/>
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
           border:solid 1px black;
            padding-left: 5px;
            background:transparent; 
        }
        .inputbackground
        {
            background-image: url("signInBg.jpg");
            background-repeat:no-repeat;
            width:433px;
            height:312px;
        }
        .submit
        {
            background: url("signIn.gif") no-repeat;
            border-width:0px;
            width: 75px;
            height: 20px;
            color:#486095;
            cursor:pointer;
        }
    </style>
</head>

<body onLoad="focusLogin()">

    <table align="center">
        <tr><td height="200px"></td></tr>

        <tr>
            <td>
                <form method="post" name="login_form" autocomplete="off" action="{action}">
                    <center>
                        <table border="0" cellspacing="5" align='center' class="inputbackground">
                            <tr><td colspan='2' style="height: 130px;"></td></tr>
                            <tr><td align="right" width='140'>
                                <font face="Arial,Helvetica">
                                    <xsl:value-of select="resource:getString($constants,'username')"/></font></td>
                                <td align="left"><input type="text" name="username" class="inputfield" value='' onChange="javascript:this.value=this.value.toLowerCase();"/></td>
                            </tr>
                            <tr><td align="right" class="ptext"><font face="Arial,Helvetica"><xsl:value-of select="resource:getString($constants,'password')"/></font></td>
                                <td align="left"><input type="password" name="password" class="inputfield"/></td>
                            </tr>
                            <tr><td align="right"></td>
                                <td align="left" height="100px" valign="top"><font face="Arial,Helvetica" color="#486095"><input type="submit" value="{resource:getString($constants,'signin')}" border="0" class="submit"/></font></td>
                            </tr>

                        </table>
                    </center>
               </form>
            </td>
        </tr>
        <xsl:for-each select="error">
 		 <tr>
 		    <xsl:variable name="errorKey" select="."/>
 		     <td style="color:#ac0000;"><font face="Arial,Helvetica"><xsl:value-of select="resource:getString($constants,$errorKey)"/></font></td>
		  </tr>
		</xsl:for-each>
    </table>
</body>
</html>
</xsl:template>


</xsl:stylesheet>