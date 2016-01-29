package com.santiago.catbox.util;

import android.util.Log;
import com.santiago.catbox.common.Constant;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by test on 15-6-2.
 */
public class SequenceNumFile {
	private FileChannel channel = null;
	private RandomAccessFile randomAccessFile = null;
	private String tag = Constant.TAG + "-" + SequenceNumFile.class.getSimpleName();

	public SequenceNumFile(String fileName){
		File lockFile = null;
		try {
		lockFile = new File(fileName);
		if(!lockFile.exists()){
			lockFile.createNewFile();
		} else if(lockFile.isDirectory()){
			lockFile.delete();
			lockFile.createNewFile();
		}
			this.randomAccessFile = new RandomAccessFile(lockFile,"rw");
			this.channel = randomAccessFile.getChannel();
			this.channel.force(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileLock lock(){
		FileLock lock = null;
		if(this.channel != null){
			try {
				lock = this.channel.lock();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lock;
	}

	public void unLock(FileLock lock){
		if(lock != null){
			try {
				lock.release();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public int write(long seqNum){
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.clear();
		byteBuffer.putLong(seqNum);
		int size = 0;
		try {
			channel.position(0);
			byteBuffer.flip();
			size = channel.write(byteBuffer);
			Log.i(tag, "write seqNum: " + seqNum + "size: " + size);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return size;
	}

	public long read(){
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		byteBuffer.clear();
		try {
			channel.position(0);
			int size = channel.read(byteBuffer);
			Log.i(tag, "read size: " + size);
			if(size != 8){
				return 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		byteBuffer.flip();
		return byteBuffer.getLong();
	}

	public void close(){
		if(this.randomAccessFile != null){
			try {
				this.randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.randomAccessFile = null;
		}
	}
}
