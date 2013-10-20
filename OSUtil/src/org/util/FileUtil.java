package org.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 根据表名写入磁盘.java文件
 * @author 苟伟
 */
public class FileUtil {
	private static  ByteBuffer buffer = ByteBuffer.allocate(1024);
	//写入文件
	public static void writeJavaFile(final String filePath,final String fileName,String javaBean) throws IOException{
		FileOutputStream outs = null;
		FileChannel fChannel  = null;
		try {
			File dir = new File(filePath+File.separatorChar+fileName);
			if(!dir.getParentFile().exists()){ //创建文件夹
				dir.getParentFile().mkdirs();
			}
			if(dir.isDirectory()){ //创建文件
				dir.createNewFile();
			}
			outs = new FileOutputStream(dir);
			fChannel = outs.getChannel();
			byte[] b = javaBean.getBytes();
			int offset = 0;
			while(offset < b.length){
				int length = (b.length - offset) > buffer.capacity() ? buffer.capacity() : (b.length - offset);
				buffer.clear();
				buffer.put(b, offset,  length ); 
				buffer.flip();
				fChannel.write(buffer);
				offset += length;
			}
			
		} catch (IOException e) {
			throw e;
		}finally{
				try {
					if(outs != null){
						outs.close();
					}
					if(fChannel != null){
						fChannel.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}
}
