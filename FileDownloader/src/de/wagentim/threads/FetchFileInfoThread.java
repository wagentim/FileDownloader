package de.wagentim.threads;

import de.wagentim.element.DownloadFile;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class FetchFileInfoThread extends Thread
{
	private DownloadFile dFile = null;
	
	private LogChannel log = QLoggerService.getChannel(QLoggerService.addChannel(new DefaultChannel("Fetch Thread")));
	
	public FetchFileInfoThread( final DownloadFile downloadFile)
	{
		this.dFile = downloadFile;
	}
	
	@Override
	public void run()
	{
		log.log("Fetch File Info Thread Start...", Log.LEVEL_INFO);
		
		HttpGet get = new HttpGet(dFile.getDonwloadURL());
	}
}
