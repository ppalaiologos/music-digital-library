package DigitalLibraries.org.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@WebServlet(name = "Service", urlPatterns = { "/Service" })
public class Service extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
       
	Document xmlDoc;
	PrintWriter xmlOut;
	Transformer transformer;
	StreamResult result;
	DOMSource source;
	String webCont = "/home/plato/apache-tomcat-7.0.34/wtpwebapps/DigitalLibraries.org/";
	
    public Service() 
    {
        super();
        try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			xmlDoc = db.parse(webCont+"library.xml");
			TransformerFactory tf = TransformerFactory.newInstance();
			transformer = tf.newTransformer();
			result = new StreamResult(new File(webCont+"library.xml"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		request.setCharacterEncoding("UTF-8");
		String requestType = request.getHeader("request-type");
		String target;
		Node root = xmlDoc.getElementsByTagName("artists").item(0);
		NodeList nl = root.getChildNodes();
		//Remove band
		if((requestType.startsWith("remove"))&&((target = request.getParameter("target"))!=null))
		{
			if(removeNodeByAttrValue(nl,"id",target)) 
			{
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("Success");
			}
			else
			{
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("Failure");
				return;
			}
		}
		//Remove multiple
		else if((requestType.startsWith("mulRemove"))&&((target = request.getParameter("target"))!=null))
		{
			StringTokenizer strtok = new StringTokenizer(target, ",");
			String str = removeMulByAttrValue(nl, "id", strtok);
			if(str==null) 
			{
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("Success");
			}
			else
			{
				response.setContentType("text/html; charset=UTF-8");
				if(str.startsWith("Could"))	response.getWriter().write(str);
				else response.getWriter().write("Could not find ids: "+str);
				return;
			}
		}
		//add a new band
		else if(requestType.startsWith("add"))
		{
			if(addBand(root, request))
			{
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("Success");
			}
			else
			{
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("Failure");
			}
		}
		//requests info of a band
		else if((requestType.startsWith("reqInfo"))&&((target = request.getParameter("target"))!=null))
		{
			response.setContentType("text/xml; charset=UTF-8");
			response.getWriter().write(info(nl, target));
		}
		//updates info of a band or rather replaces the current info with the new
		else if(requestType.startsWith("update"))
		{
			response.setContentType("text/html; charset=UTF-8");
			if(update(request, root, nl))
			{
				response.getWriter().write("Success");
			}
			else
			{
				response.getWriter().write("fFailure");
			}
		}
		//If invalid request
		else
		{
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("Failure");
			return;
		}
	}
	
	private Node getAttrByName(String name, Node node)
	{
		return node.getAttributes().getNamedItem(name);
	}
	
	private boolean removeNodeByAttrValue(NodeList children, String attName, String attValue)
	{
		boolean sFlag = false;
		//remove from cached document
		Node nTarget;
		for(int i=0; i<children.getLength();i++)
		{
			nTarget = children.item(i);				
			if((nTarget!=null)&&(nTarget.hasAttributes()))
			{
				if(getAttrByName(attName,nTarget).getNodeValue().startsWith(attValue))
				{
					nTarget.getParentNode().removeChild(nTarget);
					sFlag = true;
					break;
				}
			}
		}
		if(!sFlag) return sFlag;
		source = new DOMSource(xmlDoc);
		try 
		{
			transformer.transform(source, result);
		} 
		catch (TransformerException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private String removeMulByAttrValue(NodeList children, String attName, StringTokenizer strtok)
	{
		String val, res = null;
		boolean vFlag=false;
		Node nTarget;
		int i, j=0;
		//iterate through the ids and search for each one of them
		while((val=strtok.nextToken())!=null)
		{
			if(val.startsWith("#end")) break;
			//the ids have been parsed in sequence, so
			//every time one is found, we know the rest are after it
			for(i=j; i<children.getLength();i++)
			{
				nTarget = children.item(i);				
				if((nTarget!=null)&&(nTarget.hasAttributes()))
				{
					if(getAttrByName(attName,nTarget).getNodeValue().startsWith(val))
					{
						
						nTarget.getParentNode().removeChild(nTarget);
						vFlag = true;
						j=i;
						break;
					}
				}
			}
			if(!vFlag) res = res + val + ", ";
		}
		//save changes
		source = new DOMSource(xmlDoc);
		try 
		{
			transformer.transform(source, result);
		} 
		catch (TransformerException e) 
		{
			e.printStackTrace();
			return "Could not change xml file....";
		}
		return res;
	}

	private boolean addBand(Node root, HttpServletRequest request)
	{
		
		root.appendChild(createNode(request));
		//write to xml file
		source = new DOMSource(xmlDoc);
		try 
		{
			transformer.transform(source, result);
		} 
		catch (TransformerException e) 
		{
			e.printStackTrace();
			return false;
		}		
		return true;
	}

	private String info(NodeList rootCh, String id)
	{
		Node temp = null;
		String response = "<artist";
		//find the artist tag with id=id
		for(int i=0; i<rootCh.getLength(); i++)
		{
			temp = rootCh.item(i);
			if(!temp.hasAttributes()) continue;
			if(getAttrByName("id",temp).getNodeValue().startsWith(id))
			{
				break;
			}
		}
		Node artist = temp;
		//temp is the tag in question; start building the xml output
		response = response+ " id='"+id+"'><name>";
		rootCh = artist.getChildNodes();
		for(int i=0; i<rootCh.getLength(); i++)
		{
			temp = rootCh.item(i);
			if(temp.getNodeName().startsWith("name"))
			{
				response = response + temp.getTextContent()+"</name><genre>";
			}
			if(temp.getNodeName().startsWith("genre"))
			{
				response = response + temp.getTextContent()+"</genre><fyear>";
			}
			if(temp.getNodeName().startsWith("fyear"))
			{
				response = response + temp.getTextContent()+"</fyear><albums EPs='";
			}
			if(temp.getNodeName().startsWith("albums"))
			{
				response = response + getAttrByName("EPs", temp).getNodeValue()+"' SPs='"+
							getAttrByName("SPs",temp).getNodeValue()+"'>"+temp.getTextContent()+
							"</albums><current_members number='";
			}
			if(temp.getNodeName().startsWith("current_members"))
			{
				//add the attribute "number"
				response = response + getAttrByName("number", temp).getNodeValue() +"'>";
				//some variables
				Node temp2, temp3;
				NodeList tmp, tmp2;
				//iterate through the <member> tags
				tmp = temp.getChildNodes();
				for(int j=0; j<tmp.getLength(); j++)
				{
					if((temp2 = tmp.item(j)).getNodeName().startsWith("member"))
					{//if current is a member, then let's get its children
						response = response + "<member><mname>";
						tmp2 = temp2.getChildNodes();
						for(int k=0; k<tmp2.getLength(); k++)
						{
							temp3 = tmp2.item(k);
							if(temp3.getNodeName().startsWith("mname")) 
								response = response + temp3.getTextContent()+"</mname>";
							if(temp3.getNodeName().startsWith("pos"))
								response = response + "<pos>"+temp3.getTextContent()+"</pos>";
						}
						response = response + "</member>";
					}
										
				}
				response = response + "</current_members></artist>";
				break;
			}
		}
		return response;
	}

	private Element createNode(HttpServletRequest request)
	{
		// create artist element as band root and set the id
		Element artist = xmlDoc.createElement("artist");
		artist.setAttribute("id", request.getParameter("id"));
		//name tag
		Element name = xmlDoc.createElement("name");
		name.appendChild(xmlDoc.createTextNode(request.getParameter("bname")));
		artist.appendChild(name);
		//genre tag
		Element genre = xmlDoc.createElement("genre");
		genre.appendChild(xmlDoc.createTextNode(request.getParameter("genre")));
		artist.appendChild(genre);
		//fyear tag
		Element fyear = xmlDoc.createElement("fyear");
		fyear.appendChild(xmlDoc.createTextNode(request.getParameter("fyear")));
		artist.appendChild(fyear);
		//albums tag
		Element albums = xmlDoc.createElement("albums");
		albums.appendChild(xmlDoc.createTextNode(request.getParameter("albums")));
		albums.setAttribute("EPs", request.getParameter("EPS"));
		albums.setAttribute("SPs", request.getParameter("SPs"));
		artist.appendChild(albums);
		//current members tag
		Element cMembers = xmlDoc.createElement("current_members");
		cMembers.setAttribute("number", request.getParameter("noOfMemb"));
		//create and append children of current_members
		String positions = "";
		StringTokenizer strtok;
		for(int i=0; i<Integer.parseInt(request.getParameter("noOfMemb"));i++)
		{
			Element member = xmlDoc.createElement("member");
			Element mname = xmlDoc.createElement("mname");
			mname.appendChild(xmlDoc.createTextNode(request.getParameter("mname"+i)));
			member.appendChild(mname);
			positions = request.getParameter("mpos"+i);
			strtok = new StringTokenizer(positions,",");
			while(strtok.hasMoreTokens())
			{
				Element pos = xmlDoc.createElement("pos");
				pos.appendChild(xmlDoc.createTextNode(strtok.nextToken()));
				member.appendChild(pos);
			}
			cMembers.appendChild(member);
		}
		artist.appendChild(cMembers);
		return artist;
	}
	
	private boolean update(HttpServletRequest request, Node root, NodeList children)
	{
		//create updated subtree
		Element artist = createNode(request);
		//get the id
		String id = request.getParameter("id");
		//find the artist in the xmlDoc
		boolean sFlag = false;
		//remove from cached document
		Node nTarget;
		for(int i=0; i<children.getLength();i++)
		{
			nTarget = children.item(i);	
			if((nTarget!=null)&&(nTarget.hasAttributes()))
			{
				if(getAttrByName("id", nTarget).getNodeValue().startsWith(id))
				{
					root.replaceChild(artist, nTarget);
					sFlag = true;
					break;
				}
			}
		}
		if(!sFlag) return sFlag;
		//write out and return
		source = new DOMSource(xmlDoc);
		try 
		{
			transformer.transform(source, result);
		} 
		catch (TransformerException e) 
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
