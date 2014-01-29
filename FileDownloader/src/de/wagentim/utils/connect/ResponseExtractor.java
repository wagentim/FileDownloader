package de.wagentim.utils.connect;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

public class ResponseExtractor 
{
	public static Header[] getHeaders(final HttpResponse response)
	{
		return null == response ? null : response.getAllHeaders();
	}
	
	
}
