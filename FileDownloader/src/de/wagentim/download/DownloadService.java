package de.wagentim.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import de.wagentim.element.DownloadFile;
import de.wagentim.element.IStatusListener;
import de.wagentim.file.FileManager;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;
import de.wagentim.threads.DownloadThread;

public class DownloadService {
	
	public static final DownloadService INSTANCE = new DownloadService();
	
	public static final int logID = QLoggerService.addChannel(new DefaultChannel("Download Service"));
	private static final LogChannel log = QLoggerService.getChannel(logID);
	
	private static PoolingHttpClientConnectionManager manager = null;
	private static final CloseableHttpClient client;
	private static RequestConfig globalConfig = null;
	
	private static final int DEFAULT_THREADS = 3;
	
	static
	{
		manager = new PoolingHttpClientConnectionManager();
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
		client = HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(globalConfig)
					.build();
	}
	
	public synchronized void download(DownloadFile file, IStatusListener listener)
	{
		if( null == file )
		{
			log.log("The give download file in null", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		// ########################################################
		// check if download URL is available
		// ########################################################
		
		String dlURL = file.getDonwloadURL();
		
		if( null == dlURL || dlURL.isEmpty() )
		{
			log.log("Invalidate donwload url", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		// ########################################################
		// fetch informations from the server
		// ########################################################
		
		initialRemoteFileInformation(file);
		
		// ########################################################
		// check if target file is available
		// ########################################################
		
		String tFilePath = file.getTargeFilePath();
		String tFileName = file.getName();
		
		if( null == tFilePath || tFilePath.isEmpty() || null == tFileName || tFileName.isEmpty() )
		{
			log.log("Cannot get target file information", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		File targetFile = null;
		
		if( !FileManager.checkFileExistance( ( targetFile = new File(tFilePath, tFileName) ) ) )
		{
			log.log("Cannot find or create target file!", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		// ########################################################
		// create download threads
		// ########################################################
		
		WriteData wd = null;
	
		try {
			
			wd = new WriteData(new RandomAccessFile(targetFile, "rwd"), file);
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		
		HttpRequestBase[] requests = constructRequest(file);
		
		if( null != requests && requests.length > 0 )
		{
			int index = 1;
			
			new Thread(wd).start();
			
			for( HttpRequestBase r : requests )
			{
				new DownloadThread(r, file, null, client, index, wd).start();
			}
		}
	}

	private boolean initialRemoteFileInformation(DownloadFile file) 
	{
		HttpGet get = new HttpGet(file.getDonwloadURL());
		
		HttpResponse resp = null;
		
		try {
			resp = client.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		if( null == resp )
		{
			log.log("Cannot get remote file Infos. Response is null", Log.LEVEL_CRITICAL_ERROR);
			return false;
		}
		
		int status = resp.getStatusLine().getStatusCode();
		
		if( HttpStatus.SC_OK != status )
		{
			log.log("Get Non Successful Code from Server: " + status, Log.LEVEL_ERROR);
			return false;
		}
		
		Header[] headers = resp.getAllHeaders();
		
		if( null != headers && headers.length > 0 )
		{
			for( Header h : headers )
			{
				if( h.getName().equals("Content-Length") )
				{
					file.setFileSize(Long.parseLong(h.getValue()));
				}
			}
		}
		
		file.setName("temp");
		
		return true;
	}

	private HttpRequestBase[] constructRequest(DownloadFile file) 
	{

		int threads = checkThreadNumber(file.getThreadsNumber());
		
		HttpRequestBase[] result = new HttpRequestBase[threads];
		
		HttpGet get = null;
		
		for( int i = 0; i < threads; i++ )
		{
			get = new HttpGet(file.getDonwloadURL());
			
			addHeaders(get, file.getHeaders());
			
			
			result[i] = get;
		}
		
		return result;
	}

	private int checkThreadNumber(int threads) 
	{
	
		if( threads <= 0 || threads > 10 )
		{
			threads = DEFAULT_THREADS;
		}
		return threads;
	}

	private void addHeaders(HttpGet get, Header[] headers) {
		
		if( null == headers || headers.length <= 0 )
		{
			return;
		}
		
		for( Header h : headers )
		{
			get.addHeader(h);
		}
		
	}
}
