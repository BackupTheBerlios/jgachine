import java.io.*;

public class Test
{
   static public void main(String args[]) 
	{
		if (ClassLoader.getSystemClassLoader().getClass().getName().equals("MyClassLoader"))
			System.exit(0);
		System.exit(1);
	}
}
