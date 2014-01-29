package de.wagentim.threads;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import de.wagentim.core.DataBlock;
import de.wagentim.core.WriteData;
import de.wagentim.element.DownloadFile;
import de.wagentim.element.IStatusListener;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

/**
 * The thread to download the file
 * 
 * @author wagentim
 *
 */
public class DownloadThread extends Thread 
{
	private HttpRequestBase request = null;
	private DownloadFile downloadFile = null;
	private IStatusListener listener = null;
	private CloseableHttpClient client = null;
	
	private WriteData wd = null;
	
	private volatile boolean cancel = false;
	private static final int BUFFER_SIZE = 4096;
	
	private final int id;
	
	private LogChannel log = null;
	
	public DownloadThread(final HttpRequestBase request, final DownloadFile downloadFile, final IStatusListener listener, final CloseableHttpClient client, final int id, final WriteData wd)
	{
		this.request = request;
		this.downloadFile = downloadFile;
		this.listener = listener;
		this.client = client;
		this.id = id;
		this.wd = wd;
		
		log = QLoggerService.getChannel(QLoggerService.addChannel(new DefaultChannel("Thread " + id)));
	}

	@Override
	public void run() 
	{
		
		log.log("Thread: " + id + " Started!!", Log.LEVEL_INFO);
		
		if( null == request || null == client )
		{
			log.log("Request is null or Client is null", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		// step 1: send request
		
		HttpResponse resp = null;
		
		try {
			resp = client.execute(request);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( null == resp || resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK )
		{
			log.log("Cannot get respose from the server or request not successful", Log.LEVEL_CRITICAL_ERROR);
			return;
		}

		byte[] buffer = new byte[BUFFER_SIZE];
		
		try {

			InputStream ins = resp.getEntity().getContent();
			
			int length = 0;
			
			int offsetPoint = 0;
			
			while( !cancel && ( length = ins.read(buffer) ) > 0 )
			{
				wd.add( new DataBlock(buffer, id, offsetPoint) );
				
				offsetPoint += length;
				
				log.log("Write: " + offsetPoint, Log.LEVEL_INFO);
			}
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void cancelDownload(boolean value)
	{
		cancel = value;
	}
}
