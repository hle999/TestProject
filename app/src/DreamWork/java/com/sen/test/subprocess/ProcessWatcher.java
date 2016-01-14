/*
 * Copyright (c) 2014 droidwolf(droidwolf2006@gmail.com)
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.sen.test.subprocess;

import android.content.Intent;

import com.droidwolf.fix.FileObserver;
import com.sen.test.TestService;
import com.sen.test.TestService1;

import java.io.File;
import java.io.IOException;

public class ProcessWatcher {
    private FileObserver mFileObserver;
    private final String mPath;
    private final File mFile;
    private final WatchDog mWatchDog;

    public ProcessWatcher(int pid, WatchDog watchDog) {
        mPath = "/proc/" + pid;
        mFile = new File(mPath);
        mWatchDog = watchDog;
    }

    public void start() {
        if (mFileObserver == null) {
            mFileObserver = new MyFileObserver(mPath, FileObserver.CLOSE_NOWRITE);
        }
        mFileObserver.startWatching();
    }

    public void stop() {
        if (mFileObserver != null) {
            mFileObserver.stopWatching();
        }
    }

    private void doSomething() {
//		try {
//			Runtime.getRuntime().exec("am start --user 0 -n com.droidwolf.example/com.droidwolf.example.WatchDogActivity");
//		} catch (IOException e) {}
        /*mWatchDog.getContext().startService(new Intent(mWatchDog.getContext(), TestService.class));
		mWatchDog.getContext().startService(new Intent(mWatchDog.getContext(), TestService1.class));*/
		/*try {
			Runtime.getRuntime().exec("am start --user 0 -a android.intent.action.VIEW -d http://my.oschina.net/droidwolf");
		} catch (IOException e) {
		}*/
    }

    private final class MyFileObserver extends FileObserver {
        private final Object mWaiter = new Object();

        public MyFileObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            if ((event & FileObserver.CLOSE_NOWRITE) == FileObserver.CLOSE_NOWRITE) {
                try {
                    synchronized (mWaiter) {
                        mWaiter.wait(10000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!mFile.exists()) {
                    doSomething();
                    stopWatching();
                    mWatchDog.exit();
                }
                System.out.println("EventReceiver.....0");
                try {
                    Runtime.getRuntime().exec("am broadcast --user 0 -a com.readboy.parentmanager.TIME_PICK");
                } catch (IOException e) {
                }
//                mWatchDog.getContext().sendBroadcast(new Intent("com.readboy.parentmanager.TIME_PICK"));
                System.out.println("EventReceiver.....1");
            }
        }
    }
}