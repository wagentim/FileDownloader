package de.wagentim.element;

import java.io.RandomAccessFile;

import org.apache.http.Header;

import de.wagentim.core.DataBlock;

public interface IFile {
	
	String getDonwloadURL();

	Header[] getHeaders();

	int getThreadsNumber();
	
	RandomAccessFile getTargetFile();
	
	void setDataBlock(DataBlock block);
	
	long getOffset(int threadID);
}
