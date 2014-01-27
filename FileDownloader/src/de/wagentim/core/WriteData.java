package de.wagentim.core;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import de.wagentim.element.DownloadFile;

public class WriteData implements Runnable {

	private BlockingQueue<DataBlock> queue = null;
	
	private RandomAccessFile targetFile = null;
	
	private DownloadFile dFile;
	
	public WriteData(final RandomAccessFile targetFile, final DownloadFile dFile)
	{
		this.targetFile = targetFile;
		this.dFile = dFile;
		
		queue = new LinkedBlockingDeque<DataBlock>();
	}
	
	public WriteData add(DataBlock block)
	{
		queue.add(block);
		return this;
	}
	
	@Override
	public void run() {
		
		DataBlock b = null;
		
		try {
			while( (b = queue.take()) != null )
			{
				targetFile.seek(b.getOffsetPoint());
				targetFile.write(b.getData());
				dFile.setDataBlock(b);
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	

}
