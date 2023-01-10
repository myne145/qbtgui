package xyz.derkades.plex4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.print.Doc;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import xyz.derkades.plex4j.library.Library;
import xyz.derkades.plex4j.library.MusicLibrary;

public class Server {
	
	private DocumentBuilder builder;
	private URL baseUrl;
	private String token;
	
	public Server(URL baseUrl, String token) {
		this.baseUrl = baseUrl;
		this.token = token;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    factory.setValidating(true);
		    factory.setIgnoringElementContentWhitespace(true);
		    
		    builder = factory.newDocumentBuilder();
		    builder.setErrorHandler(new DefaultHandler());
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
	}
	
	public URL getBaseUrl() {
		return baseUrl;
	}
	
	public String getToken() {
		return token;
	}
	
	/**
	 * Meant for internal use only.
	 */
	public DocumentBuilder getDocumentBuilder() {
		return builder;
	}
	
	/**
	 * Checks if a connection to the base url can be established.
	 * @return An IOException if no connection could be established. 
	 * This method will return null if a connection could be established. 
	 * Please note that a successful connection does not mean that the token 
	 * is correct or that the targeted website is a plex media server at all, 
	 * just that it can reach some sort of web server at the given baseUrl.
	 */
	public IOException testConnection() {
		try {
			URLConnection connection = baseUrl.openConnection();
			connection.connect();
			return null;
		} catch (IOException e) {
			return e;
		}
	}
	
	/**
	 * Gets a list of all libraries of supported types. Supported types include:
	 * <ul>
	 * <li>Music ("artist")</li>
	 * </ul>
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	public List<Library> getLibraries() throws MalformedURLException, IOException, SAXException {
		URLConnection connection = new URL(baseUrl + "/library/sections?X-Plex-Token=" + token).openConnection();
	    Document doc = builder.parse(connection.getInputStream());
	    Node mediaContainer = doc.getFirstChild();
		NodeList libraryNodes = mediaContainer.getChildNodes();
		
		List<Library> libraries = new ArrayList<>();
		
		for (int i = 1; i < libraryNodes.getLength(); i++) {
			if (i % 2 == 0) continue;
			
			Node libraryNode = libraryNodes.item(i);
			
			String type = libraryNode.getAttributes().getNamedItem("type").getTextContent();
			String title = libraryNode.getAttributes().getNamedItem("title").getTextContent();
			int id = Integer.parseInt(libraryNode.getAttributes().getNamedItem("key").getTextContent());
			
			Library library;
			
			if (type.equals("artist")) {
				library = new MusicLibrary(this, id, title);
			} else {
				// Unsupported library. Feel free to add support for any missing library types.
				library = new Library(this, id, title, type);
			}

			libraries.add(library);
		}
		
		return libraries;
	}

	/**
	 * Refreshes specific library.
	 * @param libraryId library's that you want to refresh id number
	 * @throws IOException
	 */
	public boolean refreshLibrary(int libraryId) throws IOException {
		URL url = new URL(baseUrl + "/library/sections/" + libraryId + "/refresh?X-Plex-Token=" + token);
		int code = 401;
		try {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			code = connection.getResponseCode();
			url.openStream();
		} catch (UnknownHostException unknownHostException) {
			System.out.println(1);
		} catch (ConnectException connectException) {
			System.out.println(2);
		} catch (Exception e) {
			System.out.println(3);
		}
		return code == 200;
	}

	/**
	 * Refreshes all plex libraries
	 */
	public boolean refreshAllLibraries() throws MalformedURLException {
		URL url = new URL(baseUrl + "/library/sections/all/refresh?X-Plex-Token=" + token);
		HttpURLConnection connection;
		int code;
		try {
		 	connection = (HttpURLConnection) url.openConnection();
		 	code = connection.getResponseCode();
			url.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return code == 200;
	}

}
