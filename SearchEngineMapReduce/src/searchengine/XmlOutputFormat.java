/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;


import java.io.DataOutputStream;
import java.io.IOException;




import java.io.IOException;
 
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/**
 *
 * @author gk
 */
public class XmlOutputFormat <K, V> extends FileOutputFormat {
    
  final static String fileName = "books.xml";
  final static String filePath = ".";
  static boolean isSplitOutput = true; // set it to false for single file output
  
  public RecordWriter<K, V> getRecordWriter(TaskAttemptContext job) 
          throws IOException,InterruptedException {
    Configuration conf = job.getConfiguration();
    Path file = getDefaultWorkFile(job, "");  
    FileSystem fs = file.getFileSystem(conf);
    FSDataOutputStream fileOut = fs.create(file, false);
    return new XmlRecordWriter<K,V>(new DataOutputStream(fileOut),
            filePath+"/"+fileName, isSplitOutput);
  }
}