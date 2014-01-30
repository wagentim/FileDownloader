package de.wagentim.element;

import java.util.Vector;

import org.apache.http.Header;

/**
 * All text content in this class is URLEncoded.
 * 
 * @author wagentim
 *
 */
public class DownloadFile extends AbstractFile
{
	
	private long id;
	private long fileSize = -1;
	private String donwloadURL = null;
	private String oriFileName = null;
	private volatile int threadsNumber = 1;
	private String targeFilePath = null;
	private Vector<Range> downloadedBlock = null;
	private Header[] headers = null;
	
	private static final int BLOCK_SIZE = 1024;
	
	public DownloadFile()
	{
		setDownloadedBlock(new Vector<Range>());
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

	public Vector<Range> getDownloadedBlock() {
		return downloadedBlock;
	}

	public void setDownloadedBlock(Vector<Range> downloadedBlock) {
		this.downloadedBlock = downloadedBlock;
	}
	
	public synchronized Range requireChunk()
	{
		
		return null;
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

	public void init() {
		
		long tmp = this.fileSize;
		
		long start = -1;
		long end = 0;
		long index = 0;
		
		if( this.fileSize > 0 )
		{
			while( end < tmp )
			{
				start = start + end + 1;
				end = end + BLOCK_SIZE;
				
				if( end > tmp )
				{
					end = tmp;
				}
				
				Range r = new Range(start, end);
				r.setIndex(index);
				
				downloadedBlock.add(r);
				
				index++;
			}
		}
		
	}
	
	public synchronized Range requireRange()
	{
		if( downloadedBlock.isEmpty() )
		{
			return null;
		}
		
		for( Range r : downloadedBlock )
		{
			if( r.getStatus() == Range.STATUS_FREE )
			{
				r.setStatus(Range.STATUS_REQUIRED);
				return r;
			}
		}
		
		return null;
	}
	
}
