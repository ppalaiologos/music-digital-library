/*Removes the subtree with id=ID*/
function reMove(ID)
{
	var xhr;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
	  xhr=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
	  xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("POST", "Service", true);
	xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	xhr.setRequestHeader("request-type","remove");
	xhr.onreadystatechange = function()
	{
		if(xhr.readyState==4 && xhr.status==200 && xhr.responseText=="Success")
		{
			var base = document.getElementById("bCont");
			base.removeChild(document.getElementById(ID));
		}
	};
	xhr.send("target="+encodeURIComponent(ID));
}
/*Removes the checked bands*/
function removeMul()
{	
	//search for checked boxes and append the id of the band to the end of the list
	var params = "";
	var toR = new Array();
	var c = document.getElementsByTagName("input");
	var base = document.getElementById("bCont");
	var k = 0;
	for(var i=0; i<c.length; i++)
	{
		if(c[i].checked)
		{
			params = params+c[i].value+",";
			toR[k] = c[i].value;
			k++;
		}
	}
	
	params = params+"#end";
	//create the xmlhttprequest
	var xhr;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
	  xhr=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
	  xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("POST", "Service", true);
	xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	xhr.setRequestHeader("request-type","mulRemove");
	xhr.onreadystatechange = function()
	{
		if(xhr.readyState==4 && xhr.status==200 && xhr.responseText=="Success")
		{
			for(var i=0; i<toR.length; i++)
			{
				base.removeChild(document.getElementById(toR[i]));
			}
		}
	};
	xhr.send("target="+encodeURIComponent(params));
}