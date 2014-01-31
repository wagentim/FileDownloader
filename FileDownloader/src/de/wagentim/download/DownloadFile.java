package de.wagentim.download;

import java.util.Arrays;
import java.util.Vector;

import org.apache.http.Header;

/**
 * All text content in this class is URLEncoded.
 * 
 * @author wagentim
 *
 */
public class DownloadFile
{
	
	private long fileSize = -1;
	private String donwloadURL = null;
	private String oriFileName = null;
	private volatile int threadsNumber = 1;
	private String targeFilePath = null;
	private Header[] headers = new Header[]{};
	private String method = "Get";
	private Vector<String> unFinishedBlock = null;
	
	public DownloadFile()
	{
		unFinishedBlock = new Vector<String>();
	}
	
	public String getName() {
		return oriFileName;
	}
	public void setName(String name) {
		this.oriFileName = name;
	}
	
	public String getDonwloadURL() {
		return donwloadURL;
	}
	public void setDonwloadURL(String donwloadURL) {
		this.donwloadURL = donwloadURL;
	}
	
	public String getTargetFilePath() {
		return null;
	}
	public String getTargeFilePath() {
		return targeFilePath;
	}
	public void setTargeFilePath(String targeFilePath) {
		this.targeFilePath = targeFilePath;
	}

	public int getThreadsNumber() {
		
		return threadsNumber;
	}

	public void setThreadsNumber(int threadsNumber) {
		this.threadsNumber = threadsNumber;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	
	public void writeInfo(String info)
	{
		unFinishedBlock.add(info);
	}
	
	public String getInfo()
	{
		if( unFinishedBlock.isEmpty() )
		{
			return "";
		}
		
		String result = unFinishedBlock.get(0);
		
		unFinishedBlock.remove(0);
		
		return result;
	}
	
	public Vector<String> getUnFinishedBlock()
	{
		return unFinishedBlock;
	}
	
	public void addHeader(Header header)
	{
		if( null != header )
		{
			Header[] tmp = Arrays.copyOf(headers, headers.length + 1);
			tmp[tmp.length - 1] = header;
			
			headers = tmp;
		}
	}
	
}
