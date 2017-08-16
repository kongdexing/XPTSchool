/*
 * Copyright (C) 2015 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pwittchen.networkevents.library.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.github.pwittchen.networkevents.library.BusWrapper;
import com.github.pwittchen.networkevents.library.event.WifiSignalStrengthChanged;
import com.github.pwittchen.networkevents.library.logger.Logger;

public final class WifiSignalStrengthChangeReceiver extends BaseBroadcastReceiver {
    private Context context;

    public WifiSignalStrengthChangeReceiver(BusWrapper busWrapper, Logger logger, Context context) {
        super(busWrapper, logger, context);
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // We need to start WiFi scan after receiving an Intent
        // in order to get update with fresh data as soon as possible
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();
        onPostReceive();
    }

    public void onPostReceive() {
        postFromAnyThread(new WifiSignalStrengthChanged(logger, context));
    }
}
