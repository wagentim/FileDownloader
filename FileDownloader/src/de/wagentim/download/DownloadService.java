package de.wagentim.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;

import de.wagentim.entity.DownloadFile;
import de.wagentim.file.FileManager;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;
import de.wagentim.threads.DefaultDownloadConfig;
import de.wagentim.threads.DownloadThread;
import de.wagentim.utils.Converter;
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
	
	public synchronized void download(DownloadFile file)
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
		
		RandomAccessFile tfile = null;
		
		try {
			tfile = new RandomAccessFile(targetFile, "rwd");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String block = file.getInfo().trim();
		
		if( null == block || block.isEmpty() )
		{
			divideBlock(file);
		}
		
		for( int i = 0; i < checkThreadNumber(file.getThreadsNumber()); i++ )
		{
		
			block = file.getInfo().trim();
			
			if( null != block && !block.isEmpty() )
			{
				
				int index = block.indexOf("-");
				
				DefaultDownloadConfig config = new DefaultDownloadConfig(file, tfile);
				
				String start = block.substring(0, index);
				
				String end = block.substring(index + 1, block.length());
				
				config.setHttpClient(client);
	
				config.setStartPoint(Long.parseLong(start));
				
				Header header = null;

				if( null == end || end.isEmpty() )
				{
					config.setEndPoint(-1L);
					header = new BasicHeader("Range", "bytes=" + start + "-" );
				}else
				{
					config.setEndPoint(Long.parseLong(end));
					header = new BasicHeader("Range", "bytes=" + start + "-" + end);
				}
				
				file.addHeader(Converter.convertHeader(header));
				
				new Thread(new DownloadThread(config, log)).start();
			}
		}
	}

	private void divideBlock(DownloadFile file) {
		
		long length = file.getFileSize();
		
		if( length == 0)
		{
			file.getUnFinishedBlock().add("0-");
			return;
		}
		
		int tNum = file.getThreadsNumber();
		long start = 0;
		long end = -1;
		long size = length / tNum;
		
		StringBuffer sb = new StringBuffer();
		Vector<String> result = file.getUnFinishedBlock();
		
		while( end < length)
		{
			start = end + 1;
			
			sb.append(start);
			sb.append("-");
			
			end = start + size;
			
			if( end > length )
			{
				sb.append(length);
			}
			else
			{
				sb.append(end);
			}

			result.add(sb.toString());
			
			sb.delete(0, sb.length());
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
		
		HttpUriRequest request = RequestBuilder.create("Get")
										.setUri(file.getDonwloadURL())
										.addHeader(Converter.convertMyHeader(file.getHeaders()))
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

	private int checkThreadNumber(int threads) 
	{
	
		if( threads <= 0 || threads > 10 )
		{
			threads = DEFAULT_THREADS;
		}
		return threads;
	}

}
