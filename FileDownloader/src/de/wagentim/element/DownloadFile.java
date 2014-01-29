package de.wagentim.element;

import java.util.Vector;

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
	private Vector<Chunk> downloadedBlock = null;
	
	public DownloadFile()
	{
		setDownloadedBlock(new Vector<Chunk>());
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

	public Vector<Chunk> getDownloadedBlock() {
		return downloadedBlock;
	}

	public void setDownloadedBlock(Vector<Chunk> downloadedBlock) {
		this.downloadedBlock = downloadedBlock;
	}
	
	public synchronized Chunk requireChunk()
	{
		int start = 0;
		int end = 0;
		boolean findFreeBlock = false;
		
		for( Chunk c : downloadedBlock )
		{
			
		}
		
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
	
}
