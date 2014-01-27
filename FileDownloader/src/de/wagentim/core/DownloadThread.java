package de.wagentim.core;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

import de.wagentim.element.IFile;
import de.wagentim.element.IStatusListener;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class DownloadThread implements Runnable 
{
	private HttpRequestBase request = null;
	private IFile downloadFile = null;
	private IStatusListener listener = null;
	private CloseableHttpClient client = null;
	
	private WriteData wd = null;
	
	private volatile boolean cancel = false;
	private static final int BUFFER_SIZE = 4096;
	
	
	
	private final int id;
	
	private LogChannel log = null;
	
	public DownloadThread(final HttpRequestBase request, final IFile downloadFile, final IStatusListener listener, final CloseableHttpClient client, final int id, final WriteData wd)
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
			
			long offsetPoint = downloadFile.getOffset(id);
			
			int length = 0;
			
			while( !cancel && ( length = ins.read(buffer) ) > 0 )
			{
				wd.add(new DataBlock(buffer, id, offsetPoint));
				
				offsetPoint += length;
			}
			
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
