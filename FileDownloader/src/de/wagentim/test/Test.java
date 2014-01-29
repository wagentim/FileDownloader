package de.wagentim.test;

import de.wagentim.download.DownloadService;
import de.wagentim.element.DownloadFile;

public class Test {
	
	public static void main(String[] args)
	{
		String uri = "http://download.virtualbox.org/virtualbox/4.3.6/VirtualBox-4.3.6-91406-Win.exe";
		
		DownloadFile file = new DownloadFile();
		file.setDonwloadURL(uri);
		file.setTargeFilePath("c:\\temp\\");
		file.setThreadsNumber(1);
		
		DownloadService.INSTANCE.download(file, null);
	}
}
