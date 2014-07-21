package halma;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


public class PropertyTable 
{
	private static Properties propertyTable = null;
	
	public static boolean initialize(String propertyFile)
	{
		propertyTable = new Properties();
		try 
		{
			propertyTable.load(new FileInputStream(propertyFile));
			return true;
		} 
		catch (FileNotFoundException e) 
		{
			System.err.println("FileNotFoundException while initializing PropertyTable. " +  e.getMessage());
			return false;
		} 
		catch (IOException e) 
		{
			System.err.println("IOException while initializing PropertyTable. " +  e.getMessage());
			return false;
		}
	}
	
	public static synchronized Properties getInstance()
	{
		if(propertyTable == null)
		{
			System.err.println("Attempting to get PropertyTable's Properties object before it has been initialized.");
		}
		return propertyTable;
	}
}

