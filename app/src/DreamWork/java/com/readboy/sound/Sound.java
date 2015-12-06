/**
 * 声音类
 * 支持MP3，WAV格式的数据<br>
 * 支持语音变速<br>
 * 支持流模式，Buffer模式，如果需要解密，你可以继承流，或者将解密的数据直接放到Buffer中。<br>
 * @version 1.0<br> 
 * @author Arty<br>
 */
package com.readboy.sound;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;


import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * 播放声音流程：<br>
 * 1. Sound s = new Sound();//新实例<br>
 * 2. s.setDataSource(...);//设置数据源<br>
 * 3. s.setXXX();//设置其他参数<br>
 * 4. s.start();//开始<br>
 * 5. s.pause();/s.start();//暂停/继续<br>
 * 6. s.stop();//停止<br>
 * 7. s.release();//释放
 */
public class Sound
{

    /**
     * 播放结束侦听接口
     */
    public interface OnCompletionListener
    {

        /**
         * @param sound
         */
        void onComplete(Sound sound);
    }

    /**
     * 解密接口
     */
    public interface BufferDecoder
    {
        /**
         * 解密接口
         *
         * @return 返回解密的数据
         */
        byte[] decode();
    }

    /**
     * 播放出错侦听接口
     */
    public interface OnErrorListener
    {

        /**
         * @param sound
         * @param what  事件原因
         * @param extra 附加参数
         * @return 返回true表示处理了此错误，否则返回false
         */
        boolean onError(Sound sound, int what, int extra);
    }

    /**
     * 设置出错处理侦听器
     *
     * @param listener 出错侦听器
     */
    public void setOnErrorListener(OnErrorListener listener)
    {
        mOnErrorListener = listener;
    }

    /**
     * 设置播放结束处理器
     *
     * @param listener
     */
    public void setOnCompletionListener(OnCompletionListener listener)
    {
        mOnCompletionListener = listener;
    }

    /**
     * 构造声音，JNI内部打开一个声音通道，并添加到内部引擎
     */
    public Sound()
    {
        tag = this;
        WeakReference<Sound> sound = new WeakReference<>(this);

        mHandle = nativeOpen(sound);

        Looper looper;
        if ((looper = Looper.myLooper()) != null)
        {
            mEventHandler = new EventHandler(this, looper);
        }
        else if ((looper = Looper.getMainLooper()) != null)
        {
            mEventHandler = new EventHandler(this, looper);
        }
        else
        {
            mEventHandler = null;
        }
    }

    /**
     * 设置数据
     *
     * @param buffer 数据Buffer
     * @param offset 相对Buffer开始的偏移量
     * @param length 长度
     */
    public void setDataSource(byte[] buffer, long offset, long length)
    {
        stop();
        if (mState != STATE_RELEASED)
        {
            mBuffer = buffer;
            mOffset = offset;
            mLength = length;
            //设置数据返回0表示正常的。
            if (0 == nativeSetData(mHandle, mBuffer, (int) mOffset, (int) mLength, FORMAT_DEFAULT))
            {
                mState = STATE_SET;
            }
            else
            {
                triggerError(ERROR_INTERNAL_SETDATA, 0);
            }
        }
        else
        {
            Log.w(TAG, "bad state.");
            triggerError(ERROR_BAD_STATE, 0);
        }
    }

    /**
     * 设置数据
     *
     * @param buffer 数据Buffer
     */
    public void setDataSource(byte[] buffer)
    {
        setDataSource(buffer, 0, buffer.length);
    }

    /**
     * 设置数据
     *
     * @param context
     * @param resid   资源id
     * @throws java.io.IOException
     */
    public void setDataSource(Context context, int resid) throws NotFoundException, IOException
    {
        setDataSource(context.getResources().openRawResource(resid));
    }

    /**
     * 设置数据
     *
     * @param fd     文件句柄
     * @param offset 偏移
     * @param length 长度
     * @throws java.io.IOException
     */
    public void setDataSource(FileDescriptor fd, int offset, int length) throws IOException
    {
        FileInputStream fis = new FileInputStream(fd);
        setDataSource(fis, offset, length);
    }

    /**
     * 设置数据
     *
     * @param fd 文件句柄
     * @throws java.io.IOException
     */
    public void setDataSource(FileDescriptor fd) throws IOException
    {
        FileInputStream fis = new FileInputStream(fd);
        setDataSource(fis);
    }

    /**
     * 设置数据
     *
     * @param is 输入流
     * @throws java.io.IOException
     */
    public void setDataSource(InputStream is) throws IOException
    {
        int length = is.available();
        if (length <= BUFF_LIMIT)
        {
            byte buff[] = new byte[length];
            is.read(buff, 0, length);
            setDataSource(buff);
        }
        else
        {
            throw new IllegalArgumentException("data too long" + length);
        }
    }

    /**
     * 设置数据
     *
     * @param is     输入流
     * @param offset 相对流开始的偏移量
     * @param length 长度
     * @throws java.io.IOException
     */
    public void setDataSource(InputStream is, int offset, int length) throws IOException
    {
        if (length <= BUFF_LIMIT)
        {
            byte buff[] = new byte[length];
            is.skip(offset);
            is.read(buff, 0, length);
            setDataSource(buff);
        }
        else
        {
            throw new IllegalArgumentException("data too long:" + length);
        }
    }

    /**
     * 设置数据
     *
     * @param filePath 文件路径
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    public void setDataSource(String filePath) throws FileNotFoundException, IOException
    {
        setDataSource(new FileInputStream(filePath));
    }

    /**
     * @param decoder
     */
    public void setDataSource(BufferDecoder decoder)
    {
        setDataSource(decoder.decode());
    }

    /**
     * 播放或者继续播放暂停的声音
     * 如果已经开始播放或者暂停，并且需要从头播放，调用一下stop后再调用此函数
     */
    public void start()
    {
        if (mState == STATE_STARTED)
        {
            return;
        }
        else if (mState == STATE_STOPPED)
        {
            if (0 == nativeSetData(mHandle, mBuffer, (int) mOffset, (int) mLength, FORMAT_DEFAULT))
            {
                mState = STATE_SET;
                if (0 == nativeStart(mHandle))
                {
                    mState = STATE_STARTED;
                    mStateWatcher.start();
                }
                else
                {
                    triggerError(ERROR_INTERNAL_START, mState);
                }
            }
            else
            {
                triggerError(ERROR_INTERNAL_SETDATA, mState);
            }
        }
        else if (mState == STATE_SET || mState == STATE_PAUSED)
        {
            if (0 == nativeStart(mHandle))
            {
                mState = STATE_STARTED;
                mStateWatcher.start();
            }
            else
            {
                triggerError(ERROR_INTERNAL_START, 0);
            }
        }
        else
        {
            Log.w(TAG, "ignoring start on state: " + mState);
        }
    }

    /**
     * 停止播放
     * 在已经播放或者暂停播放状态下调用有效，否则忽略
     */
    public void stop()
    {
        if (mState == STATE_STOPPED)
        {
            return;
        }
        else if (mState == STATE_PAUSED || mState == STATE_STARTED)
        {
            mStateWatcher.setNotified(true);
            nativeStop(mHandle);
            mState = STATE_STOPPED;
        }
        else
        {
            Log.w(TAG, "ignoring stop on state: " + mState);
        }
    }

    /**
     * 暂停播放
     * 在已经播放时调用，否则忽略
     */
    public void pause()
    {
        if (mState == STATE_PAUSED)
        {
            return;
        }
        else if (mState == STATE_STARTED)
        {
            nativePause(mHandle);
            mState = STATE_PAUSED;
        }
        else
        {
            Log.w(TAG, "ignoring pause on state: " + mState);
        }
    }

    /**
     * 设置循环播放
     *
     * @param loop true表示循环播放
     */
    public void setLoop(boolean loop)
    {
        mIsLoop = loop;
    }

    /**
     * 判断是否循环
     *
     * @return true表示循环，false表示不循环
     */
    public boolean isLoop()
    {
        return mIsLoop;
    }

    /**
     * 设置语音速度
     *
     * @param speed 速度值（-100~100）
     *              设置语音速度只对该语音实例有效，并不能改变全局的语速
     */
    public void setSpeed(int speed)
    {
        nativeSet(mHandle, SOUND_KEY_SPEED, speed);

    }

    /**
     * 获取语音速度
     *
     * @return 语音速度（-100~100）
     */
    public int getSpeed()
    {
        return nativeGet(mHandle, SOUND_KEY_SPEED);
    }

    /**
     * 判断是否正在播放
     *
     * @return true表示正在播放。
     */
    public boolean isPlaying()
    {
        if (mState == STATE_STARTED || mState == STATE_PAUSED)
        {
            return (1 == nativeGet(mHandle, SOUND_KEY_PLAYSTAT));
        }
        return false;
    }

    /**
     * 设置标签，用于存储用户数据
     *
     * @param tag
     */
    public void setTag(Object tag)
    {
        this.tag = tag;
    }

    /**
     * 获取标签
     *
     * @return
     */
    public Object getTag()
    {
        return tag;
    }

    /**
     * 释放资源，如果有必要会停止播放
     */
    public void release()
    {
        if (mState != STATE_RELEASED)
        {
            try
            {
                mStateWatcher.exit();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            if (mHandle != 0)
            {
                nativeClose(mHandle);
                mHandle = 0;
            }
            mState = STATE_RELEASED;
        }
    }

    /**
     * 销毁时释放资源
     */
    @Override
    protected void finalize()
    {
        release();
    }

    /**
     * 此线程用来检测播放状态，结束后发播放结束消息
     * 通知listener处理, 内部使用
     */
    private class StateWatcher extends Thread
    {
        @Override
        public void run()
        {
            while (mIsStarted)
            {
                if (!isPlaying() && !mNotified)
                {
                    mNotified = true;
                    //在播放或者暂停状态下的时候，才可以发结束消息
                    if (mState == STATE_STARTED || mState == STATE_PAUSED)
                    {
                        mState = STATE_STOPPED;
                        if (mEventHandler != null)
                        {
                            Message m = mEventHandler.obtainMessage(EVENT_FINISH, 0, 0, Sound.this);
                            mEventHandler.sendMessage(m);
                        }
                    }
                }

                SystemClock.sleep(100);
            }
        }

        /**
         * 线程退出时调用
         *
         * @throws InterruptedException
         */
        public synchronized void exit() throws InterruptedException
        {
            mIsStarted = false;
            join();
        }


        /**
         * 由于线程是循环检测机制，结束后只需发送一次消息，然后标志起来
         * 防止多次发送
         *
         * @param notify
         */
        public synchronized void setNotified(boolean notify)
        {
            mNotified = notify;
        }

        /**
         * 检测线程开始
         *
         * @see Thread#start()
         */
        @Override
        public synchronized void start()
        {
            mNotified = false;

            if (!mIsStarted)
            {
                mIsStarted = true;
                super.start();
            }
        }

        private boolean mNotified = false;
        private boolean mIsStarted = false;
    }

    /**
     * 底层调用是发送消息到Java端，内部使用
     *
     * @param soundRef 对Sound的弱引用，见Sound的构造函数
     * @param what     消息类型
     * @param arg1     参数1
     * @param arg2     参数2
     * @param obj      发送者，类似于Userdata
     * @throws Exception
     */
    private static void nativePost(Object soundRef, int what, int arg1, int arg2, Object obj) throws Exception
    {
        Sound sound = (Sound) ((WeakReference<?>) soundRef).get();
        if (sound == null)
        {
            return;
        }

        if (sound.mEventHandler != null)
        {
            Message m = sound.mEventHandler.obtainMessage(what, arg1, arg2, obj);
            sound.mEventHandler.sendMessage(m);
        }

    }

    /**
     * 将状态置为错误，内部使用
     *
     * @param arg1 参数1
     * @param arg2 参数2
     */
    private void triggerError(int arg1, int arg2)
    {
        mState = STATE_ERROR;

        if (mOnErrorListener == null || !mOnErrorListener.onError(this, arg1, arg2))
        {
            if (mOnCompletionListener != null)
            {
                //用户没有处理错误，且设置了结束处理侦听器，则调用结束处理函数。
                mOnCompletionListener.onComplete(this);
            }
        }
    }

    /**
     * 内部事件处理类
     */
    private class EventHandler extends Handler
    {
        private Sound mSound;

        private static final int ERR_MASK = 0x8000;

        public EventHandler(Sound mp, Looper looper)
        {
            super(looper);
            mSound = mp;
        }

        /**
         * 接受消息，处理消息。
         *
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg)
        {
            if (mSound.mNativeContext == 0 || mState == STATE_RELEASED)
            {
                Log.w(TAG, "sound went away with unhandled events");
                return;
            }

            if ((msg.what & ERR_MASK) > 0)
            {
                Log.e(TAG, "received an error: " + msg.what + ", " +
                        "" + msg.arg1 + ", " + msg.arg2);
                triggerError(ERROR_INTERNAL, msg.what);
            }
            else if (msg.what == EVENT_FINISH)
            {
                if (mState != STATE_ERROR)
                {
                    if (mIsLoop)
                    {
                        if (mHandle != 0)
                        {
                            setDataSource(mBuffer, mOffset, mLength);
                            start();
                        }
                    }

                    else if (mOnCompletionListener != null)
                    {
                        mOnCompletionListener.onComplete(mSound);
                    }
                }
            }

        }
    }

    /**
     * 日志TAG
     */
    private static final String TAG = "arty.sound";

    //播放缓存，偏移量和长度
    private byte[] mBuffer;
    private long mOffset;
    private long mLength;

    //数据限制（32M）
    private static final int BUFF_LIMIT = 0x2000000;
    /**
     * 播放流程中的状态机
     */
    private int mState = STATE_INIT;

    //初始态
    private static final int STATE_INIT = 0;
    //数据被设置
    private static final int STATE_SET = 1;
    //已经开始
    private static final int STATE_STARTED = 2;
    //已经暂停
    private static final int STATE_PAUSED = 3;
    //已经停止
    private static final int STATE_STOPPED = 4;
    //资源被释放
    private static final int STATE_RELEASED = 5;
    //出错
    private static final int STATE_ERROR = 0xFF;

    //播放结束事件
    private static final int EVENT_FINISH = 200;

    //以下是错误码

    //无错误
    private static final int ERROR_NONE = 0;
    //内部错误
    private static final int ERROR_INTERNAL = 1;
    //状态错误
    private static final int ERROR_BAD_STATE = 2;
    //设置数据出错
    private static final int ERROR_INTERNAL_SETDATA = 11;
    //播放出错
    private static final int ERROR_INTERNAL_START = 12;

    /**
     * 存储用户数据
     */
    private Object tag;

    private boolean mIsLoop = false;

    /**
     * 状态侦听器
     */
    private StateWatcher mStateWatcher = new StateWatcher();

    /**
     * 事件处理器
     */
    private EventHandler mEventHandler;

    /**
     * 出错处理
     */
    private OnErrorListener mOnErrorListener = null;

    /**
     * 结束处理
     */
    private OnCompletionListener mOnCompletionListener = null;

    /**
     * 获取、设置播放状态KEY JNI使用
     */
    private static final int SOUND_KEY_PLAYSTAT = 0x102;

    /**
     * 获取、设置语速参数的KEY JNI使用
     */
    private static final int SOUND_KEY_SPEED = 0x103;


    //播放格式为：DEFAULT(自动检测)
    private static final int FORMAT_DEFAULT = 0;

    //-------------------------------------------------------------------------------------
    //以下为JNI调用实现接口,请勿随意修改
    /**
     * C声音句柄
     */
    private int mHandle;
    /**
     * Java声音Context
     */
    private int mNativeContext = 0;

    private native static final void nativeSetup();

    private native final int nativeOpen(Object obj);

    private native final int nativeClose(int handle);

    private native final int nativeSetData(int handle, byte[] buffer, int offset, int length, int format);

    private native final int nativeStart(int handle);

    private native final int nativeStop(int handle);

    private native final int nativePause(int handle);

    private native final int nativeSet(int handle, int key, int param);

    private native final int nativeGet(int handle, int key);
    //-------------------------------------------------------------------------------------

    /**
     * 加载JIN声音库，并启动OpenSL引擎。
     */
    static
    {

        System.loadLibrary("sound");
        nativeSetup();
    }
}
