package de.wagentim.utils;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import de.wagentim.http.MyHeader;

public class Converter {

	public static Header convertMyHeader( final MyHeader header )
	{
		return new BasicHeader(header.getName(), header.getValue());
	}
	
	public static MyHeader convertHeader( final Header header )
	{
		return new MyHeader(header.getName(), header.getValue());
	}
	
	public static Header[] convertMyHeader( final MyHeader[] headers )
	{
		
		if( null == headers || headers.length <= 0 )
		{
			return new Header[]{};
		}
		
		Header[] result = new Header[headers.length];
		
		for( int i = 0; i < result.length; i++ )
		{
			MyHeader mh = headers[i];
			result[i] = new BasicHeader(mh.getName(), mh.getValue());
		}
		
		return result;
	}
	
	public static MyHeader[] convertHeader( final Header[] headers )
	{
		if( null == headers || headers.length <= 0 )
		{
			return new MyHeader[]{};
		}
		
		MyHeader[] result = new MyHeader[headers.length];
		
		for( int i = 0; i < result.length; i++ )
		{
			Header mh = headers[i];
			result[i] = new MyHeader(mh.getName(), mh.getValue());
		}
		
		return result;
	}
}	

