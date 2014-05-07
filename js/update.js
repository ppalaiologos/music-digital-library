/*Convert all span fields to editable fields*/
function convertToForm(id)
{
	//find root element
	var root = document.getElementById(id);
	var sp = root.getElementsByTagName("span");
	
	//find all <span> elements and make them editable
	for(var i=0; i<sp.length; i++)
	{
		sp[i].setAttribute("contenteditable","true");
		sp[i].setAttribute("onclick","");
	}
	
	//find control container
	var rgh = root.getElementsByTagName("div")[1];
	
	rgh.childNodes[4].setAttribute("onclick","sendBack('"+id+"');return false;");//change onclick of remove to submit function
	rgh.childNodes[4].textContent = "Submit";
	
	//create add member field button	
	window.document.getElementById(id).getElementsByTagName("div")[2].childNodes[7].innerHTML += "<button type='button' onclick='addMemberF(window.document.getElementById(\""+id+"\").getElementsByTagName(\"div\")[2].childNodes[7]);'>Add member field</button><br/>";
	
	
}

//
function startEditMode(id)
{
	if(window.editMode == "false")
	{
		window.editMode = "true";
		convertToForm(id);
	}
}
//
function endEditMode()
{
	if(window.editMode == "true") window.editMode = "false";
}
//
function createEditMode()
{
	window.editMode = "false";
}
//
function addMemberF(root)
{
	root.innerHTML += "<input type='text' name='r0'/><br/>";
}
//

function sendBack(id)
{
	//root element
	var root = document.getElementById(id);
	var tempRoot = root.innerHTML;
	//find spans and inputs
	var sp = root.getElementsByTagName("span");
	var inp = root.getElementsByTagName("input");
	//
	/*sp: 0 ==> albums
	 *	  1 ==> EPs
	 *    2 ==> SPs
	 *    3 ==> bname
	 *    4 ==> genre
	 *    5 ==> fyear
	 *    6+==> band members (mnamei, mposi)
	 *    sp.length -6 ==> noOfMemb
	 *id ==> id				*/
	//prepare parameters for xhr
	var params = "";
	//
	params += "id="+encodeURIComponent(id)+"&albums="+encodeURIComponent(sp[0].innerHTML)+
			  "&EPs="+encodeURIComponent(sp[1].innerHTML)+"&SPs="+encodeURIComponent(sp[2].innerHTML)+
			  "&bname="+encodeURIComponent(sp[3].innerHTML)+"&genre="+encodeURIComponent(sp[4].innerHTML)+
			  "&fyear="+encodeURIComponent(sp[5].innerHTML);
	//compute number of members and which of the member inputs have useful information
	var sP = new Array();
	var sf = 0;
	for(var i =6; i<sp.length; i++)
	{
		if((sp[i].innerHTML != "")&&(sp[i].innerHTML.substring(0,3)!="<br"))
		{//split and keep only useful parts
			sP[sf] = sp[i].innerHTML.split(" ");
			sf++;
		}
	}
	for(var i=1; i<inp.length; i++)
	{		
		if(inp[i].value!="")
		{	
			sP[sf] = inp[i].value.split(" ");
			sf++;
		}
	}//at this point, sf-1 is the number of band members
	params += "&noOfMemb="+encodeURIComponent(sP.length);//add no of members to the parameters
	
	//prepare the parameter values
	var temp = new Array();
	var k=0;
	for(var i=0; i<sP.length; i++)
	{
		//initialization
		temp[i] = new Array();
		k=0;
		temp[i][k] = "";
		//for each word in sP
		for(var j=0; j<sP[i].length; j++)
		{//check if useful
			if((sP[i][j]!="in")&&(sP[i][j]!=",")&&(sP[i][j]!="and")&&(sP[i][j]!=""))
			{//if useful, add to temp[i][k]
				temp[i][k]+= " "+sP[i][j];
			}
			else
			{//else change k and initialize
				k++;
				temp[i][k] = "";
			}
		}
	}
	
	//create member parameters
	var tmp;
	for(var i=0; i<temp.length; i++)
	{
		tmp="";
		//remove preceeding space from names and add the parameters
		if(temp[i][0].charAt(0)==" ") temp[i][0] = temp[i][0].substring(1);
		params += "&mname"+i+"="+encodeURIComponent(temp[i][0]);
		//create position parameter
		for(var j=1; j<temp[i].length; j++)
		{
			//remove preceeding spaces
			if(temp[i][j].charAt(0)==" ") temp[i][j] = temp[i][j].substring(1);
			//position1,position2,
			tmp+=temp[i][j]+",";			
		}
		//remove following comma and encode into parameters
		params +="&mpos"+i+"="+encodeURIComponent(tmp.substring(0, tmp.length-1));
	}
	//create xhr object
	var xhr;
	if (window.XMLHttpRequest)
	{// code for IE7+, Firefox, Chrome, Opera, Safari
			xhr=new XMLHttpRequest();
	}
	else
	{// code for IE6, IE5
			xhr=new ActiveXObject("Microsoft.XMLHTTP");
	}
	xhr.open("POST","Service",true);
	xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xhr.setRequestHeader("request-type","update");
	xhr.responseType = "text";
	xhr.onreadystatechange = function()
	{
		if(xhr.readyState==4 && xhr.status==200 && xhr.responseText=="Success")
		{
			
			//if successful, prepare to update the band field
			//get the section element
			broot = window.document.getElementById(id);
			//create the member string
			var mStr = "";
			for(var i=0; i<temp.length; i++)
			{//for each element in temp
				//mStr = mname in position0
				mStr +="<span>"+temp[i][0]+" in "+temp[i][1];
				//
				for(var j=2; j<temp[i].length-1; j++)
				{//mStr = mname in position0, position1, position2
					mStr +=", "+temp[i][j];
				}
				if(temp[i].length > 2)
				{
					mStr += " and "+temp[i][temp[i].length-1];
				}
				mStr += "</span><br/>";
			}
			
			//create the whole innerHTML string
			brStr = "<div class='left_col'>" +
			"<img src='images/lefts.jpg'/><br/><br/>"+"<h4>Discography</h4><span>"+sp[0].innerHTML + "</span> studio albums<br/><span>"+
			sp[1].innerHTML + "</span> EPs<br/><span>"+sp[2].innerHTML + "</span> singles"+"</div>"+"<div class='right_col'>"
			+"<img src='images/guitar.jpg'/><br/><br/>"+"<h4>Controls</h4>"+"<button type='button' onclick='reMove(\""+ id +"\")'>Remove</button><br/>"
			+"<button type='button' onclick='startEditMode(\""+ id + "\")'>Update</button><br/>"
			+"<input type='checkbox' name='status' value='"+id+"'>Mark</input>"+"</div>"+"<div class='page_content'><br/><br/><br/>"
			+"<p><h2><span>"+sp[3].innerHTML+"</span></h2></p>"+"<p>A <span>"+ sp[4].innerHTML + "</span> band formed in <span>" +sp[5].innerHTML+"</span></p><br/>"
			+"<h4 class='that'> Current Members </h4><p>"+mStr+"</p><br/><br/><br/><br/>"+"<h5><a href='#top'>Back to top...</a></h5>"
			+"<br/><br/><br/><br/><br/>"+"</div>";
			
			broot.innerHTML = brStr;
			
		}
		else
		{//if unsuccessful, convert back to normal
			broot.innerHTML = tempRoot;
		}
	};
	xhr.send(params);
	
	//end end
	endEditMode();
}
//
function requestInfo(ID)
{
	var xhr = new XMLHttpRequest();
	xhr.open("POST", "Service", true);
	xhr.setRequestHeader("Content-Type","application/x-www-form-urlencoded");
	xhr.setRequestHeader("request-type","reqInfo");
	xhr.responseType = "document";
	xhr.onreadystatechange = function()
	{
		if(xhr.readyState==4 && xhr.status==200)
		{
			convertToForm(ID);
		}
	};
	xhr.send("target="+encodeURIComponent(ID));
}