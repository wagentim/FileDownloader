package de.wagentim.download;

import java.util.Arrays;
import java.util.Vector;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.http.Header;

import de.wagentim.db.PersistManager;
import de.wagentim.http.MyHeader;
import de.wagentim.utils.Converter;

/**
 * All text content in this class is URLEncoded.
 * 
 * @author wagentim
 *
 */
@Entity
public class DownloadFile
{
	@Id
	@GeneratedValue
	private Long id = -1L;
	private long fileSize = -1;
	private String donwloadURL = null;
	private String oriFileName = null;
	private volatile int threadsNumber = 1;
	private String targeFilePath = null;
	private MyHeader[] headers = new MyHeader[]{};
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

	public MyHeader[] getHeaders() {
		return headers;
	}

	public void setHeaders(MyHeader[] headers) {
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
	
	public void setUnFinishedBlock(Vector<String> unFinishedBlock)
	{
		this.unFinishedBlock = unFinishedBlock;
	}
	
	public void addHeader(Header header)
	{
		if( null != header )
		{
			MyHeader[] tmp = Arrays.copyOf(headers, headers.length + 1);
			tmp[tmp.length - 1] = Converter.convertHeader(header);
			
			headers = tmp;
		}
	}

	public synchronized void persistInfo(final String info) {
		
		if( null != info && !info.isEmpty() )
		{
			unFinishedBlock.add(info);
			
			PersistManager.INSTANCE.persis(this);
		}
	}
	
	
	public long getID()
	{
		return id;
	}
}
