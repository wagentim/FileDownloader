package de.wagentim.utils.connect;

import java.net.URI;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Build HTTP Request
 * 
 * @author wagentim
 *
 */
public class RequestBuilder 
{
	
	//#####################################
	//	constants
	//#####################################
	public static final int TYPE_GET = 0;
	
	//#####################################
	//	private fields
	//#####################################
	private int methodType = TYPE_GET;
	private Header[] headers = null;
	private URI uri = null;
	
	public HttpRequestBase build()
	{
		HttpRequestBase request = null;
		
		switch( this.methodType )
		{
			case TYPE_GET:
				request = new HttpGet();
				break;
		}
		
		if( null == request )
		{
			throw new RuntimeException( "Invailable Request Type: " + this.methodType );
		}
		
		if( null == uri )
		{
			throw new RuntimeException( "URI is null" );
		}
		
		if( null != this.headers && headers.length > 0 )
		{
			for( Header hd : headers )
			{
				request.addHeader(hd);
			}
		}
		
		return request;
	}
	
	public RequestBuilder setMethodType(final int methodType)
	{
		this.methodType = methodType;
		
		return this;
	}
	
	public RequestBuilder setHeaders(final Header[] headers) 
	{
		this.headers = headers;
		
		return this;
	}
	
	public RequestBuilder setURI(final URI uri)
	{
		this.uri = uri;
		
		return this;
	}
	
}
