package de.wagentim.element;

import java.io.RandomAccessFile;

import de.wagentim.core.DataBlock;

/**
 * All text content in this class is URLEncoded.
 * 
 * @author wagentim
 *
 */
public class DownloadFile extends AbstractFile
{
	private long downloadedSize = -1;
	private String donwloadURL = null;
	private String name = null;
	private int threadsNumber = 1;
	private RandomAccessFile targetFile = null;
	
	
	public RandomAccessFile getTargetFile() {
		return targetFile;
	}
	public void setTargetFile(RandomAccessFile targetFile) {
		this.targetFile = targetFile;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDownloadedSize() {
		return downloadedSize;
	}
	public void setDownloadedSize(long downloadedSize) {
		this.downloadedSize = downloadedSize;
	}
	public String getDonwloadURL() {
		return donwloadURL;
	}
	public void setDonwloadURL(String donwloadURL) {
		this.donwloadURL = donwloadURL;
	}
	public int getThreadsNumber() {
		return threadsNumber;
	}
	public void setThreadsNumber(int threadsNumber) {
		this.threadsNumber = threadsNumber;
	}
	
	@Override
	public void setDataBlock(DataBlock block) {
		
		
		
	}
	@Override
	public long getOffset(int threadID) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
