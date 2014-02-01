package de.wagentim.threads;

import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;


/**
 * Configurations for <code>DownloadThread</code>
 * 
 * @author bihu8398
 *
 */
public interface DownloadConfig 
{

	String getURI();

	Header[] getHeaders();

	String getRequestMethod();

	HttpClient getHttpClient();

	void updateOffset(int size);

	RandomAccessFile getTargetFile();

	long getStartPoint();
	
	void flushInfo();

	Long getFileSize();
	
}
