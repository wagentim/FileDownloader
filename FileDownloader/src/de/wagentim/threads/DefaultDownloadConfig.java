package de.wagentim.threads;

import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;

import de.wagentim.entity.DownloadFile;
import de.wagentim.entity.MyHeader;
import de.wagentim.utils.Converter;

public class DefaultDownloadConfig implements DownloadConfig 
{
	
	private HttpClient httpClient;
	private volatile Long startPoint;
	private Long endPoint;
	private RandomAccessFile targetFile;
	private DownloadFile file;
	
	public DefaultDownloadConfig(final DownloadFile file, final RandomAccessFile targetFile)
	{
		this.file = file;
		this.targetFile = targetFile;
	}

	@Override
	public String getURI() {
		
		return file.getDonwloadURL();
	}

	@Override
	public Header[] getHeaders() {
		
		return Converter.convertMyHeader(file.getHeaders());
	}

	@Override
	public String getRequestMethod() {
		
		return file.getMethod();
	}

	@Override
	public HttpClient getHttpClient() {
		return httpClient;
	}

	@Override
	public void updateOffset(int size) {
		
		startPoint += size;
	}

	@Override
	public RandomAccessFile getTargetFile() {
		
		return targetFile;
	}

	@Override
	public long getStartPoint() {
		
		return startPoint;
	}

	public Long getEndPoint() {
		return endPoint;
	}
	
	public void setStartPoint(Long start)
	{
		this.startPoint = start;
		
	}
	
	public void setEndPoint(Long end)
	{
		this.endPoint = end;
	}
	
	public void setHttpClient(HttpClient client)
	{
		this.httpClient = client;
	}
	

	public void flushInfo()
	{
		if( startPoint < endPoint)
		{
			StringBuffer sb = new StringBuffer(""+startPoint);
			sb.append("-");
			sb.append(endPoint);
			
			file.persistInfo(sb.toString());
		}
	}

	@Override
	public Long getFileSize() {
		return file.getFileSize();
	}

}
