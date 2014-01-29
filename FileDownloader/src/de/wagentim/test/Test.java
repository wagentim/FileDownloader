package de.wagentim.test;

import de.wagentim.core.DownloadService;
import de.wagentim.element.DownloadFile;

public class Test {
	
	public static void main(String[] args)
	{
		String uri = "http://intra.esolutions.de/wiki/lib/exe/fetch.php?media=company:essensplan_kantine_kw05.pdf";
		
		DownloadFile file = new DownloadFile();
		file.setDonwloadURL(uri);
		file.setTargeFilePath("c:\\temp\\");
		file.setThreadsNumber(1);
		file.setName("essensplan_kantine_kw05.pdf");
		
		DownloadService.INSTANCE.download(file, null);
	}
}
