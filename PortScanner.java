/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portscanner;

/**
 *
 * @author pradeep
 */
import java.io.*;
import java.util.*;
import java.net.*;

import java.util.concurrent.*;
import java.util.concurrent.Executor.*;
import java.util.concurrent.ExecutorService.*;
import java.util.concurrent.Future.*;
/*
         paralelizing code to reduce time .
*/

public class PortScanner {
    
    
    public static void main(String[] args) throws Exception
    {
        
        //openPorts = new ArrayList<>() ;
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Port Scanner\n");
        System.out.print("Enter url : ");
        
        String url = sc.next() ;
        
        if( ! url.startsWith("http://") || ! url.startsWith("https://") || ! url.startsWith("www.") )
        {
            url = "http://".concat(url);
        }
        
                  // getting port range.
        System.out.print("Enter Port Range : ");
        int start = sc.nextInt() ;
        System.out.print(" to ") ;
        int end = sc.nextInt() ;
        
        long st = System.currentTimeMillis() ;
                             // connecting to host .
        System.out.println("\nGetting host ip .........");
        
        InetAddress addr = InetAddress.getByName(new URL(url).getHost());
        
        String ip =  addr.getHostAddress() ; //ip from url
        
        long tm = System.currentTimeMillis() - st ;
        
        System.out.println("Time to get host : "+ (tm/1000.0) +" Seconds" );
        
        long startT = System.currentTimeMillis() ;     //starting time
        
            //  starting scanning port .
        System.out.println("Scanning ports for ip : "+ip+" ............. ");
        
        System.out.println("\nOpen - Ports are : ");
        
        
        final ExecutorService es = Executors.newFixedThreadPool(20);   //run 20 threads. 
        
        final List<Future<result>> futures = new ArrayList<>() ;
        
        for(int i = start ; i <= end ; i++ )
        {
            futures.add( portIsOpen(es , ip , i ) ) ;
            
        }
        
        es.shutdown();
        
        int openPorts = 0 ;
        
             // printing open ports .
        for(final Future<result> f : futures )
        {
            if( f.get().giveStatus() ) 
            {
                System.out.println("port : "+ f.get().givePort()+" . Time : "+ f.get().timeTaken +" Seconds" );
                openPorts++;
            }
        }
        
        long time = System.currentTimeMillis() - startT ;
        
        System.out.println("\nTotal "+ openPorts +" ports Open.");
        System.out.println("** Done. Scanned "+(end-start+1)+" ports In " + (time/(1000.0*60)) + " Minutes.");
        
    }
    
    
    
    public static class result
    {
        private final int port ;
        private final boolean isOpen ;
        private static double timeTaken = 0.00 ;
        
        result(int p , boolean b )
        {
            this.port = p;
            this.isOpen = b;
        }
        
        public int givePort()
        {
            return this.port;
        }
        
        public boolean giveStatus()
        {
            return this.isOpen ;
        }
    }
    
    
    //now trying ExecutorService and Future API for multiple threads running.
    
    public static Future<result> portIsOpen(final ExecutorService es , String ip , int port )
    {
        return es.submit(new Callable<result>()
        {
            @Override
            public result call()
            {
                result rl = new result(port , false);
                long strT = System.currentTimeMillis() ;
                
                try
                {
                    
                    Socket soc = new Socket();
                    
                    soc.connect(new InetSocketAddress(ip,port) , 200);  //timeout 2.0 sec
                    
                    soc.close();
                    
                    rl = new result(port , true);
                    
                    long te = System.currentTimeMillis() - strT ;
                    
                    rl.timeTaken = te/1000.0 ;
                    
                    return rl;
                }
                catch(Exception e)
                {
                    long te = System.currentTimeMillis() - strT ;
                    
                    rl.timeTaken = te/1000.0 ;
                    return rl ;
                }
            }
        }  ) ; //return statement end.
    }
    
    
    
    //this process is time consuming.
    private static boolean isOpen(String ip , int port )
    {
        
        try
        {
            System.out.println("Socket creating.");
            Socket socket = new Socket() ;
            
            socket.connect(new InetSocketAddress(ip,port), 200);
            
            socket.close();
            
            return true;
        }
        catch(Exception e)
        {
            //System.out.println("Returning false : "+e);
            return false;
        }
    }
    
}
