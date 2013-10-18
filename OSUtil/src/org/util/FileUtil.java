package org.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * ���ݱ���д�����.java�ļ�
 * @author ��ΰ
 */
public class FileUtil {
	private static  ByteBuffer buffer = ByteBuffer.allocate(1024);
	//д���ļ�
	public static void writeJavaFile(final String filePath,final String fileName,String javaBean) throws IOException{
		FileOutputStream outs = null;
		FileChannel fChannel  = null;
		try {
			File dir = new File(filePath+File.separatorChar+fileName);
			if(!dir.getParentFile().exists()){ //�����ļ���
				dir.getParentFile().mkdirs();
			}
			if(dir.isDirectory()){ //�����ļ�
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
