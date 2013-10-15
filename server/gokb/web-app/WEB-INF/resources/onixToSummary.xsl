<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:xs="http://www.w3.org/2001/XMLSchema" 
                xmlns:ev="http://www.w3.org/2001/xml-events" 
                xmlns:xforms="http://www.w3.org/2002/xforms" 
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:oxf="http://www.orbeon.com/oxf/processors"
                xmlns:onix="http://www.editeur.org/onix-pl"
                xmlns:ople="http://www.editeur.org/ople"
                xmlns:p="http://www.orbeon.com/oxf/pipeline"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:context="java:org.orbeon.oxf.pipeline.StaticExternalContext"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:exist="http://exist.sourceforge.net/NS/exist"
                xmlns:xxforms="http://orbeon.org/oxf/xml/xforms">

  <xsl:output method="xhtml" exclude-result-prefixes="oxf xxforms context xs p exist ople saxon ev xforms onix"/>
  <xsl:variable name="apos">'</xsl:variable>
  <xsl:variable name="preposition">
    <Prepositions xmlns="">
      <Usage>
        <Type>onixPL:MakeDerivedWork</Type>
        <Value>from </Value>
      </Usage>
      <Usage>
        <Type>onixPL:MakeDigitalCopy</Type>
        <Value>of </Value>
      </Usage>
      <Usage>
        <Type>onixPL:MakeTemporaryDigitalCopy</Type>
        <Value>of </Value>
      </Usage>
      <Usage>
        <Type>onixPL:PrintCopy</Type>
        <Value>of </Value>
      </Usage>
      <Usage>
        <Type>onixPL:ProvideIntegratedAccess</Type>
        <Value>to </Value>
      </Usage>
      <Usage>
        <Type>onixPL:ProvideIntegratedIndex</Type>
        <Value>to </Value>
      </Usage>
      <Usage>
        <Type>onixPL:SupplyCopy</Type>
        <Value>of </Value>
      </Usage>
    </Prepositions>
  </xsl:variable>
  <xsl:variable name="valid-uri-schemes">
    <ValidURISchemes xmlns="">
      <Scheme>http</Scheme>
      <Scheme>ftp</Scheme>
      <Scheme>doi</Scheme>
    </ValidURISchemes>
  </xsl:variable>
  <xsl:template match="/">
    <h1>
      <xsl:text>Summary of ONIX-PL expression of license "</xsl:text>
      <xsl:value-of select="//onix:LicenseDetail/onix:Description"/>
      <xsl:text>"</xsl:text>
    </h1>
  </xsl:template>
</xsl:stylesheet>
