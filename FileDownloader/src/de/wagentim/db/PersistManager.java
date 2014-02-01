package de.wagentim.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.wagentim.download.DownloadFile;
import de.wagentim.qlogger.channel.DefaultChannel;
import de.wagentim.qlogger.channel.LogChannel;
import de.wagentim.qlogger.logger.Log;
import de.wagentim.qlogger.service.QLoggerService;

public class PersistManager {
	
	public static PersistManager INSTANCE = new PersistManager();
	private final EntityManagerFactory factory;
	private final EntityManager em;
	private static final String DB_NAME = "./db/download.odb";
	private final LogChannel log;
	
	private PersistManager()
	{
		factory = Persistence.createEntityManagerFactory(DB_NAME);
		em = factory.createEntityManager();
		int logID = QLoggerService.addChannel(new DefaultChannel("Download Persist"));
		log = QLoggerService.getChannel(logID);
	}
	
	public synchronized void persis(final DownloadFile file)
	{
		if( null == file )
		{
			return;
		}
		
		long id = file.getID();
		
		if( -1 == id )	// this entity has not been persisted 
		{
			em.getTransaction().begin();
			em.persist(file);
			em.getTransaction().commit();
		}else
		{
			DownloadFile savedFile = em.find(DownloadFile.class, id);
			
			if( null == savedFile )
			{
				log.log("Cannot file download file from the DB with the id: " + id, Log.LEVEL_ERROR);
				return;
			}
			
			em.getTransaction().begin();
			savedFile.setDonwloadURL(file.getDonwloadURL());
			savedFile.setUnFinishedBlock(file.getUnFinishedBlock());
			em.getTransaction().commit();
		}
	}
}
