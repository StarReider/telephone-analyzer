package org.robe.ta.ui.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JOptionPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.browser.MozillaExecutor;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.robe.ta.data.DataProvider;

import ru.atomation.jbrowser.impl.JBrowserComponent;
import ru.atomation.jbrowser.interfaces.BrowserAdapter;

public class SearchListener extends BrowserAdapter
{
	private final Log log = LogFactory.getLog(SearchListener.class);
	private DataProvider dataFacade;
	private JBrowserComponent<?> browser;
	
	public SearchListener(DataProvider dataFacade, JBrowserComponent<?> browser)
	{
		this.dataFacade = dataFacade;
		this.browser = browser;
	}
	
	private void match(String text, nsIDOMNode node)
	{
		if(text == null || node.getNodeType() != nsIDOMNode.TEXT_NODE)
			return;
		String patternString = "((8|\\+[0-9]{1,4})?[\\-\\(]?[0-9]{3,6}[\\-\\)]\\s?[0-9\\-]{5,})";
		
		
		Pattern pattern;
		try 
		{
			pattern = Pattern.compile(patternString);
		} 
		catch (PatternSyntaxException e) 
		{
			JOptionPane.showMessageDialog(null, e.getMessage(), "Regular expression is incorrect", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Matcher matcher = pattern.matcher(text);
		String group = null;
		int start, end;

		while (matcher.find()) 
        {
        	group = matcher.group();
        	start = matcher.start();
        	end = matcher.end();
        	log.info("group " + group);
        	
        	String rep_group = group.replaceAll("[^\\d]", "");
        	if(rep_group.length() == 11)
        		rep_group = rep_group.substring(1);
        	else if(rep_group.length() != 10)
        		continue;
        	
        	log.info("rep_group " + rep_group);
        	
        	boolean isFinded = false;
        	try 
        	{						
				isFinded = dataFacade.isTelephoneExist(rep_group);
			} 
        	catch (Exception e) 
			{
        		log.error(e);
				return;
			}
        	
        	if(isFinded)
        	{
        		try 
        		{
        			String before, after;
        			int i1  = text.indexOf(group);
        			before = text.substring(0, i1);
        			i1 = i1 + group.length();
        			after = text.substring(i1, text.length());
        			nsIDOMNode parent = node.getParentNode();
        			nsIDOMElement span = node.getOwnerDocument().createElement("span");
        			
        			span.setAttribute("style", "color:green");
        			
        			span.appendChild(node.getOwnerDocument().createTextNode(group));
        			
        			Object[] ar = {node, parent, span, node.getOwnerDocument().createTextNode(before), node.getOwnerDocument().createTextNode(after)};
        			array.add(ar);
        			
//        			parent.removeChild(node);
//        			parent.appendChild(node.getOwnerDocument().createTextNode(before));
//        			parent.appendChild(span);
//        			parent.appendChild(node.getOwnerDocument().createTextNode(after));
				} 
        		catch (Exception e) 
        		{
        			log.warn(e);
				}
        		            		
        		log.info(group + " was finded");
        	}
        	else
        	{
        		try 
        		{
        			String before, after;
        			int i1  = text.indexOf(group);
        			before = text.substring(0, i1);
        			i1 = i1 + group.length();
        			after = text.substring(i1, text.length());
        			
        			nsIDOMNode parent = node.getParentNode();
        			nsIDOMElement span = node.getOwnerDocument().createElement("span");
        			nsIDOMNode textNodeNew = node.getOwnerDocument().createTextNode(group);
        			span.setAttribute("style", "color:red;cursor:pointer");
        			span.setAttribute("onclick", "javascript: window.location = 'call:" + group + "';");
        			//span.setAttribute("onmouseover", "window.alert('Hello!');");
        			
        			
        			//span.appendChild(node);
        			span.appendChild(node.getOwnerDocument().createTextNode(group));

        			Object[] ar = {node, parent, span, node.getOwnerDocument().createTextNode(before), node.getOwnerDocument().createTextNode(after)};
        			array.add(ar);
        			
//        			parent.removeChild(node);
//        			parent.appendChild(node.getOwnerDocument().createTextNode(before));
//        			parent.appendChild(span);
//        			parent.appendChild(node.getOwnerDocument().createTextNode(after));
				} 
        		catch (Exception e) 
        		{
        			log.warn(e);
				}
        		//t = t.replace(group, "<span style='color:red'>" + group + "</span>");
        		log.info(group + " wasn't finded");
        	}
        }
	}
	
	private void recursiveSearch(nsIDOMNode node)
	{
		if(!node.hasChildNodes())
		{
			match(node.getNodeValue(), node);
			return;
		}

		nsIDOMNodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++)
		{
			recursiveSearch(children.item(i));
		}
	}
	
	private ArrayList<Object[]> array;
	
	@Override
	public void onLoadingStarted() 
	{
		System.out.println("Loading starting...");
		// TODO Auto-generated method stub
		super.onLoadingStarted();
	}
	
	@Override
	public void onLoadingEnded() 
	{
		 MozillaExecutor.mozAsyncExec(new Runnable() 
		 {
			 
             @Override
             public void run() 
             {
            	 System.out.println("doc is loaded");
            	 nsIDOMDocument document = browser.getWebBrowser()
                                     		.getContentDOMWindow().getDocument();

                 nsIDOMNode containerDiv = document.getElementsByTagName("body").item(0);                                                          
                 
                 if(containerDiv == null)
                	 return;
                 
                 array = new ArrayList<Object[]>();
                 
                 recursiveSearch(containerDiv);
                 
                 System.out.println("SIZE IS " + array.size());
                 
                 ArrayList<List<Object>> dd = new ArrayList<List<Object>>();
                 for(Object[] ar : array)
                 {
                	 ArrayList<Object> p = new ArrayList<Object>();
                	 p.add(ar);
                	 for(Object[] ar2 : array)
                	 {
                		 if(ar[0].equals(ar2[0]))
                		 {
                			 p.add(ar2);
                		 }
                	 }
                	 if(p.size() > 1)
                		 dd.add(p);
                 }
                                              
                 for(Object[] ar : array)
                 {
         			nsIDOMNode node = (nsIDOMNode)ar[0];
         			nsIDOMNode parent = node.getParentNode();
         			
         			nsIDOMNode span = (nsIDOMNode)ar[2];
         			nsIDOMNode before = (nsIDOMNode)ar[3];
         			nsIDOMNode after = (nsIDOMNode)ar[4];
         			
         			if(node == null || parent == null)
         			{
         				System.out.println(node.getNodeValue() + " hasn't parent?");
         				continue;
         			}
         			
         			parent.removeChild(node);
         			parent.appendChild(before);
         			parent.appendChild(span);
         			parent.appendChild(after);
                 }
             }
     });
	}
}