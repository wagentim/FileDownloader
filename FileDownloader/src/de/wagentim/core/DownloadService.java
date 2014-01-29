package de.wagentim.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

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

	private void initialRemoteFileInformation(DownloadFile file) 
	{
		
		
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
			
			get.addHeader(new BasicHeader("Content-Range", "bytes 0-" + file.getFileSize() + "/" + file.getFileSize()));
			
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
