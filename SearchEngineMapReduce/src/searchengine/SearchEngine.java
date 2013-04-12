/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;


import org.apache.log4j.Logger;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/**
 *
 * @author gk
 */
public class SearchEngine extends Configured implements  Tool {
    
    private Path pin,pout;
    static Logger logger=Logger.getLogger(SearchEngine.class);
    
    
    /**
     * @param args the command line arguments
     */
    public String getSyntax() {
        String syntax = "SearchEngine.jar searchengine.SearchEngine "
                + "<inputHadoopDirectoryPath> " +
                "<outputHadoopDirectoryPath> ";
        return syntax;
    }
    public int run(String args[]) throws Exception
    {
        if( args.length != 3) {
            logger.error(" Invalid arguments " + Integer.toString(args.length)+
                    "\n " + getSyntax());
            return -1;
        }
        logger.info("IN "+args[1]+ " OUT "+args[2]);
       
        pin=new Path(args[1]);
        pout=new Path(args[2]);   
        
        logger.info( "Path for books.xml file: " + "Default Dir in hdfs" );
        
        FileSystem fs=FileSystem.get(getConf());
        if(!fs.exists(pin)) {
            logger.error("Error Input path does not : "+ args [1]);
            return -1;
            
        }
        if(fs.exists(pout)) {
            logger.warn(" Overwriting existing directory "+ args[2]);   
            fs.delete(pout,true);
        }
        
        Job job=new Job(getConf(), "SearchEngine");
        job.setJarByClass(SearchEngine.class);
        job.setMapperClass(SearchEngineMapper.class);
        job.setReducerClass(SearchEngineReducer.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(XmlOutputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(WordLocation.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        FileInputFormat.addInputPath(job, pin);
        FileOutputFormat.setOutputPath(job, pout);
        
        return job.waitForCompletion(true) ? 0 : 1;
        

    }
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        int result=ToolRunner.run(new Configuration(),new SearchEngine(),args);
        System.exit(result);
    }
}
