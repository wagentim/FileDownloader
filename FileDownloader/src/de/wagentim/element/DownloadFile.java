package de.wagentim.element;

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
	
	
}
