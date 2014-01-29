package de.wagentim.core;

import java.util.ArrayList;
import java.util.List;

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
import de.wagentim.element.IFile;
import de.wagentim.element.IStatusListener;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class DownloadService {
	
	public static final DownloadService INSTANCE = new DownloadService();
	
	public static final int logID = QLoggerService.addChannel(new DefaultChannel("Download Service"));
	private static final LogChannel log = QLoggerService.getChannel(logID);
	
	private static PoolingHttpClientConnectionManager manager = null;
	private static final CloseableHttpClient client;
	private static RequestConfig globalConfig = null;
	
	private static final int DEFAULT_THREADS = 3;
	
	private List<Header> basicHeaders = null;
	
	static
	{
		manager = new PoolingHttpClientConnectionManager();
		globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.BEST_MATCH).build();
		client = HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(globalConfig)
					.build();
	}
	
	public void download(DownloadFile file, IStatusListener listener)
	{
		if( null == file )
		{
			log.log("The give download file in null", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		String dlURL = file.getDonwloadURL();
		
		if( null == dlURL || dlURL.isEmpty() )
		{
			log.log("Invalidate donwload url", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		HttpRequestBase[] requests = constructRequest(file);
		
		if( null != requests && requests.length > 0 )
		{
			for( HttpRequestBase r : requests )
			{
				
			}
		}
	}

	private HttpRequestBase[] constructRequest(DownloadFile file) 
	{

		int threads = checkThreadNumber(file.getThreadsNumber());
		
		HttpRequestBase[] result = new HttpRequestBase[threads];
		
		HttpGet get = new HttpGet(file.getDonwloadURL());
		
		addHeaders(get, getDefaultHeader());
		addHeaders(get, file.getHeaders());
		
		return result;
	}

	private Header[] getDefaultHeader() 
	{
		if( null == basicHeaders )
		{
			basicHeaders = new ArrayList<Header>();

			basicHeaders.add(new BasicHeader("", ""));
			basicHeaders.add(new BasicHeader("", ""));
			basicHeaders.add(new BasicHeader("", ""));
			basicHeaders.add(new BasicHeader("", ""));
		}
		
		return basicHeaders.toArray(new BasicHeader[basicHeaders.size()]);
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
