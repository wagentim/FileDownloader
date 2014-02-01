package de.wagentim.threads;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.utils.connect.RequestBuilder;
import de.wagentim.utils.connect.ResponseExtractor;

/**
 * Download Data
 * <p>
 * Download Data from the remote server to the local predefined file
 * </p>
 * 
 * @author wagentim
 *
 */
public class DownloadThread implements Runnable
{
	
	private DownloadConfig config = null;
	private LogChannel log = null;
	
	private volatile boolean cancel = false;
	
	private int bufferSize;
	
	
	public DownloadThread( final DownloadConfig config, final LogChannel log )
	{
		this(config, log, 2048);
	}
	
	public DownloadThread( final DownloadConfig config, final LogChannel log, final int bufferSize )
	{
		this.config = config;
		this.log = log;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public void run()
	{
		if( null == config )
		{
			log.log("No defined config object", Log.LEVEL_CRITICAL_ERROR);
			return;
		}

		HttpClient client = config.getHttpClient();
		
		if( null == client )
		{
			log.log("HttpClient is null", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		RandomAccessFile target = config.getTargetFile();
		
		try {
			target.seek(config.getStartPoint());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// send request for the file
		HttpUriRequest request = RequestBuilder.create( config.getRequestMethod() )
										.setUri( config.getURI() )
										.addHeader( config.getHeaders() )
										.build();
		
		HttpResponse resp = null;
		
		try {
			resp = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if( null == resp || (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK && resp.getStatusLine().getStatusCode() != HttpStatus.SC_PARTIAL_CONTENT))
		{
			log.log("Response is null or Server return an unsuccessful code", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		if( resp.getStatusLine().getStatusCode() == HttpStatus.SC_PARTIAL_CONTENT )
		{
			
			InputStream ins = null;
			try {
				
				ins = resp.getEntity().getContent();
				
				byte[] buffer = new byte[bufferSize];
				
				int size;
				
				while( !cancel && (size = ins.read(buffer)) > 0 )
				{
					target.write(buffer);
					config.updateOffset(size);
					log.log("Donwload: " + config.getStartPoint() + "/" + config.getFileSize(), Log.LEVEL_INFO);
				}
				
				config.flushInfo();
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally
			{
				if( null != ins )
				{
					try {
						ins.close();
						target.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					ins = null;
					target = null;
				}
			}
			
		}else
		{		
			InputStream ins = null;
			try {
				
				ins = resp.getEntity().getContent();
				
				byte[] buffer = new byte[bufferSize];
				
				int size;
				
				while( !cancel && (size = ins.read(buffer)) > 0 )
				{
					target.write(buffer);
					config.updateOffset(size);
					log.log("Donwload: " + config.getStartPoint() + "/" + config.getFileSize(), Log.LEVEL_INFO);
				}
				
				config.flushInfo();
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally
			{
				if( null != ins )
				{
					try {
						ins.close();
						target.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					ins = null;
					target = null;
				}
			}
		}
	}
	
	public void stop()
	{
		cancel = true;
	}
	
}
