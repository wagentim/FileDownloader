package de.wagentim.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;

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
import de.wagentim.utils.connect.ConnectConstants;
import de.wagentim.utils.connect.RequestBuilder;
import de.wagentim.utils.connect.ResponseExtractor;

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
					.setDefaultRequestConfig(globalConfig)	// as default setting automatically redirection will be disabled. All redirection should be handled manually.
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
		// fetch informations from the server and saving them into DownladFile
		// ########################################################
		
		initialRemoteFileInformation(file);
		
		file.init();
		
		// ########################################################
		// check if target file is available
		// ########################################################
		
		String tFilePath = file.getTargeFilePath();
		String tFileName = file.getName();
		
		if( null == tFilePath || tFilePath.isEmpty() )
		{
			tFilePath = System.getProperty("user.home") + "\\Download\\";
		}
		
		if( null == tFileName || tFileName.isEmpty() )
		{
			tFileName = "temp";
		}
		
		File targetFile = null;
		
		if( !FileManager.checkFileExistance( ( targetFile = new File(tFilePath, tFileName) ) ) )	// in case the file is not existed, then the file will be automatically created
		{
			log.log("Cannot find or create target file!", Log.LEVEL_CRITICAL_ERROR);
			return;
		}
		
		log.log("the target file is: " + targetFile.getAbsolutePath(), Log.LEVEL_INFO);
		
		// ########################################################
		// create download threads
		// ########################################################
		
		WriteData wd = null;
	
		try {
			
			wd = new WriteData(new RandomAccessFile(targetFile, "rw"), file);
			
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

	/**
	 * Try to get the remove file informations, such as file length and save them into <code>DownloadFile</code>
	 * 
	 * @param file
	 * @return
	 */
	private boolean initialRemoteFileInformation( final DownloadFile file ) 
	{
		
		URI uri = null;
		
		try {
			uri = new URI(file.getDonwloadURL());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		if( null == uri )
		{
			log.log("Cannot create URI object", Log.LEVEL_CRITICAL_ERROR);
			
			return false;
		}
		
		HttpRequestBase request = new RequestBuilder()
										.setHeaders(file.getHeaders())
										.setMethodType(RequestBuilder.TYPE_GET)
										.setURI(uri)
										.build();
		
		HttpResponse resp = null;
		
		try {
			resp = client.execute(request);
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
		
		// get content length information
		String length = ResponseExtractor.getHeaderParameterValue(ConnectConstants.CONTENT_LENGTH, resp);
		
		if( null == length || length.isEmpty() )
		{
			log.log("Cannot get Content Length", Log.LEVEL_ERROR);
		}else
		{
			file.setFileSize(Long.parseLong(length));
		}
		
		// TODO set file name into targetFile
		
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
