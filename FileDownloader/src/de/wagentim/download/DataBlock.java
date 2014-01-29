package de.wagentim.download;

public class DataBlock
	{
		private byte[] data = null;
		private int threadID = -1;
		private long offsetPoint = -1;
		
		public DataBlock(final byte[] data, final int threadID, final long offsetPoint)
		{
			this.data = data;
			this.threadID = threadID;
			this.offsetPoint = offsetPoint;
		}

		public byte[] getData() {
			return data;
		}

		public int getThreadID() {
			return threadID;
		}

		public long getOffsetPoint() {
			return offsetPoint;
		}
		
		
	}