package de.wagentim.file;

import java.io.File;
import java.io.IOException;

import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class FileManager {
	
	private static final LogChannel log = QLoggerService.getChannel(QLoggerService.addChannel(new DefaultChannel("FileManager")));
	
	/**
	 * Check the give File. <br />
	 * If not exit, then create one. It also creates the directory recursively.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean checkFileExistance( final File file )
	{
		if( null == file )
		{
			log.log("The input File is null", Log.LEVEL_CRITICAL_ERROR);
			return false;
		}
		
		if( file.exists() )
		{
			return true;
		}
		
		if( file.isDirectory() )
		{
			return checkDirectory(file);
			
		}else
		{
			File parent = file.getParentFile();
			
			if( checkDirectory(parent) )
			{
				try {
					file.createNewFile();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		return false;
	}
	
	public static boolean checkDirectory( final File directory )
	{
		if( null == directory )
		{
			log.log("The input File is null", Log.LEVEL_CRITICAL_ERROR);
			return false;
		}
		
		if( directory.exists() )
		{
			return true;
		}
		
		checkDirectoryRecursively(directory);
		
		return directory.mkdir();
	}

	private static void checkDirectoryRecursively(File directory) {

		File parent = directory.getParentFile();
		
		if( parent.exists() )
		{
			return;
		}
		
		checkDirectoryRecursively(parent);
		
		if( ! parent.mkdir() )
		{
			log.log("Error by making directory:" + parent.getAbsolutePath(), Log.LEVEL_CRITICAL_ERROR);
		}
	}
}
