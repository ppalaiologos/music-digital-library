<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml">
	<xsl:template match="/">
		<html>
			<head>
				<title>Bands of Rock and Metal</title>
				<link rel="stylesheet" type="text/css" href="library.css"/>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<script type="text/javascript" src="js/add.js" language="javascript"></script>
				<script type="text/javascript" src="js/remove.js" language="javascript"></script>
				<script type="text/javascript" src="js/update.js" language="javascript"></script>
				
			</head>
			
			<body onload='createEditMode();'>
				<section class="head">
					<h1 class="that">Just some major bands...</h1>
				</section>
				
				<section id="top">
					<br/><br/><br/><br/><br/><br/><br/>
					<h3 class="that"> 
						Use the buttons below to either add a new band<br/>
						or remove bands you have marked<br/>
					</h3>
					<ul>
						<li>
							<button onclick="openForm();">New</button>Add a new band!<br/>
							The new band will be appended to the end of the list
						</li>
						<li>
							<button onclick="removeMul();">Remove</button> Remove marked bands!<br/>
							You can remove individual bands by pressing the "Remove" button<br/>
							to the right of each band
						</li>
					</ul>
				</section>
				<br/><br/><br/><br/>
				
				<section class="head">
					<h3 class="that">The Bands</h3>
				</section>
				<br/><br/><br/><br/>
				<div id="bCont">
				<xsl:apply-templates/>
				</div>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="artist">
		<section id="{@id}" class="container">
			
			<div class="left_col">
				<img src="images/lefts.jpg"/><br/><br/>
				
				<xsl:apply-templates select="albums"/>
				
			</div>
			
			<div class="right_col">
				<img src="images/guitar.jpg"/><br/><br/>
				
				<h4>Controls</h4>
				<button type="button" onclick="reMove('{@id}');">Remove</button><br/>
				<button type="button" onclick="startEditMode('{@id}');">Update</button><br/>
				<input type="checkbox" name="status" value="{@id}">Mark</input>
				
			</div>
			
			<div class="page_content">
				<br/><br/><br/>
				<p>			
				<h2><xsl:apply-templates select="name"/></h2>			
				</p>
				<p>
					A <span><xsl:value-of select="genre"/></span> band formed in <span><xsl:value-of select="fyear"/></span>
				</p>
				<br/>
				<h4 class="that"> Current Members </h4>
				<p>
					<xsl:apply-templates select="current_members"/>
				</p>
				<br/><br/><br/><br/>
				<h5><a href="#top">Back to top...</a></h5>
			</div>
			<br/><br/><br/><br/><br/>
		</section>
		
	</xsl:template>
	
	<xsl:template match="name">
		<span><xsl:value-of select="."/></span>
	</xsl:template>
	
	<xsl:template match="albums">
		<h4>Discography</h4>
		<span><xsl:value-of select="."/></span> studio albums <br/>
		<span><xsl:value-of select="@EPs"/></span> EPs<br/>
		<span><xsl:value-of select="@SPs"/></span> singles
	</xsl:template>
	
	<xsl:template match="current_members">
		<xsl:for-each select="member">
			<xsl:apply-templates select="."/>
		</xsl:for-each><br/>
	</xsl:template>
	
	<xsl:template match="member">
		<span><xsl:apply-templates select="mname"/> in <xsl:for-each select="./pos"><xsl:if test="position()=1"><xsl:apply-templates select="."/></xsl:if><xsl:if test="position()>1 and position!=last()">	, <xsl:apply-templates select="."/></xsl:if><xsl:if test="position()!=1 and position()=last()"> and <xsl:apply-templates select="."/></xsl:if></xsl:for-each></span><br/>
		
	</xsl:template>
	
	<xsl:template match="mname">
		<xsl:value-of select="."/>
	</xsl:template>
	
	<xsl:template match="pos">
		<xsl:value-of select="."/>
	</xsl:template>
	
</xsl:stylesheet>