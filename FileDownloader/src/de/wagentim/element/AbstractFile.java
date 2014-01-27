package de.wagentim.element;

import org.apache.http.Header;

public abstract class AbstractFile implements IFile {
	
	public Header[] getHeaders()
	{
		return new Header[]{};
	}
}
