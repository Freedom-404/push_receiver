/**
 * Modified MIT License
 *
 * Copyright 2017 OneSignal
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * 1. The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * 2. All copies of substantial portions of the Software may only be used in connection
 * with services provided by OneSignal.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.artemkjv.push_receiver.notification;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;


class OSUtils {

   public static final int UNINITIALIZABLE_STATUS = -999;

   public static int MAX_NETWORK_REQUEST_ATTEMPT_COUNT = 3;
   static final int[] NO_RETRY_NETWROK_REQUEST_STATUS_CODES = {401, 402, 403, 404, 410};

   /*static boolean isGMSInstalledAndEnabled() {
      return packageInstalledAndEnabled(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE);
   }*/

   private static boolean packageInstalledAndEnabled(@NonNull String packageName) {
      try {
         PackageManager pm = AppContextKeeper.appContext.getPackageManager();
         PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
         return info.applicationInfo.enabled;
      } catch (PackageManager.NameNotFoundException e) {
         return false;
      }
   }

   static void sleep(int ms) {
      try {
         Thread.sleep(ms);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }

}