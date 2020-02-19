/**************************************************************************************************
 Filename:       BeaconDevice.java
 Revised:        $Date: 2013-08-30 12:08:11 +0200 (fr, 30 aug 2013) $
 Revision:       $Revision: 27477 $

 Copyright (c) 2013 - 2014 Texas Instruments Incorporated

 All rights reserved not granted herein.
 Limited License.

 Texas Instruments Incorporated grants a world-wide, royalty-free,
 non-exclusive license under copyrights and patents it now or hereafter
 owns or controls to make, have made, use, import, offer to sell and sell ("Utilize")
 this software subject to the terms herein.  With respect to the foregoing patent
 license, such license is granted  solely to the extent that any such patent is necessary
 to Utilize the software alone.  The patent license shall not apply to any combinations which
 include this software, other than combinations with devices manufactured by or for TI (襎I Devices�).
 No hardware patent is licensed hereunder.

 Redistributions must preserve existing copyright notices and reproduce this license (including the
 above copyright notice and the disclaimer and (if applicable) source code license limitations below)
 in the documentation and/or other materials provided with the distribution

 Redistribution and use in binary form, without modification, are permitted provided that the following
 conditions are met:
 * No reverse engineering, decompilation, or disassembly of this software is permitted with respect to any
 software provided in binary form.
 * any redistribution and use are licensed by TI for use only with TI Devices.
 * Nothing shall obligate TI to provide you with source code for the software licensed and provided to you in object code.

 If software source code is provided to you, modification and redistribution of the source code are permitted
 provided that the following conditions are met:
 * any redistribution and use of the source code, including any resulting derivative works, are licensed by
 TI for use only with TI Devices.
 * any redistribution and use of any object code compiled from the source code and any resulting derivative
 works, are licensed by TI for use only with TI Devices.

 Neither the name of Texas Instruments Incorporated nor the names of its suppliers may be used to endorse or
 promote products derived from this software without specific prior written permission.

 DISCLAIMER.

 THIS SOFTWARE IS PROVIDED BY TI AND TI誗 LICENSORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL TI AND TI誗 LICENSORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 **************************************************************************************************/
package com.beacon.moive.Beans;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.beacon.moive.Utils.HexUtil;


public class BeaconDevice implements Comparable<BeaconDevice> {
    // Data
    private BluetoothDevice mBtDevice;
    private String mName = "";
    private int mRssi;
    private boolean mBeaconFlg;
    private int mMinor;
    private byte[] mScanData;
    private float mDistance;

    private static final byte IBEACON_OFFSET1 = 7;
    private static final byte IBEACON_FEATURE1 = 0x02;
    private static final byte MINOR_OFFSET = 27;
    private static final byte GAP_ADTYPE_LOCAL_NAME_COMPLETE = 0x09;
    private static final byte GAP_ADTYPE_LOCAL_NAME_FLAG = 0x11;

    private static final String TAG = BeaconDevice.class.getSimpleName();

    public BeaconDevice(BluetoothDevice device, int rssi, final byte[] scanData) {
        mBtDevice = device;
        mRssi = rssi;
        mScanData = new byte[100];
        System.arraycopy(scanData, 0, mScanData, 0, scanData.length);
        parseParams(scanData, scanData.length, rssi);
    }

    public String getScanData() {
        return HexUtil.byte2hex(mScanData, 0, mScanData.length);
    }

    public BluetoothDevice getBluetoothDevice() {
        return mBtDevice;
    }

    public String getName() {
        return mName;
    }

    public int getRssi() {
        return mRssi;
    }

    public int getMinor() {
        //BE
        return mMinor;
    }

    public float getmDistance() {
        return mDistance;
    }

    public boolean isIBeacon() {
        return mBeaconFlg;
    }

    private void parseBeaconFlg() {
        if ((mScanData[IBEACON_OFFSET1] & 0xff) != IBEACON_FEATURE1) {
            Log.e(TAG, "IBEACON_OFFSET1: " + (mScanData[IBEACON_OFFSET1] & 0xff));
            mBeaconFlg = false;
            return;
        }
        mBeaconFlg = true;
        return;
    }


    private void parseMinor() {
        //BE
        mMinor = (mScanData[MINOR_OFFSET + 1] & 0xff) + (mScanData[MINOR_OFFSET] & 0xff) * 256;
    }

    private void parseName(byte[] scanData, int len) {
        mName = "";
        if (len < (32 + 16)) {
            Log.i(TAG, "length < 46 ,length=" + len);
            return;
        }
        if ((scanData[30] != GAP_ADTYPE_LOCAL_NAME_FLAG) || scanData[31] != GAP_ADTYPE_LOCAL_NAME_COMPLETE) {
            Log.i(TAG, "cannot find 11 or 09");
            return;
        }
        mName = new String(scanData, 32, 16);
        Log.i(TAG, "mName = " + mName);
    }

    private void parseParams(byte[] scanData, int len, int rssi) {
        parseName(scanData, len);
        parseMinor();
        parseBeaconFlg();
        mDistance = (float) parseDistance(rssi);
    }

    public void updateParameters(int rssi, String name, final byte[] scanData) {
        mRssi = rssi;
        mName = name;
        System.arraycopy(scanData, 0, mScanData, 0, scanData.length);
        parseParams(scanData, scanData.length, rssi);
    }

    /**
     *   首先是将rssi信号转换为距离：
     *   d=10^((ABS(RSSI)-A)/(10*n))
     *   其中d为距离，单位是m。
     *   RSSI为rssi信号强度，为负数。
     *   A为距离探测设备1m时的rssi值的绝对值，最佳范围在45-49之间。
     *   n为环境衰减因子，需要测试矫正，最佳范围在3.25-4.5之间。
     *
     * @param rssi
     * @return 主设备与从设备之间的距离dist
     */
    protected double parseDistance(double rssi) {
        if (rssi == 0) {
            return -1.0;
        }
        double absRssi = Math.abs(rssi);
        double power = (absRssi - 65) / (10 * 4.8);
        return Math.pow(10, power);
    }

    @Override
    public int compareTo(BeaconDevice o) {
        int rssi = o.getRssi();
        return rssi - this.mRssi;
    }
}
