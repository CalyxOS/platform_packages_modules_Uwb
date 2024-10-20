/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.uwb.support.aliro;

import android.os.Build.VERSION_CODES;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;
import java.util.Arrays;

/** Defines parameters for CCC pulse shape combo object */
@RequiresApi(VERSION_CODES.LOLLIPOP)
public class AliroPulseShapeCombo extends AliroParams {
    private static final int ALIRO_PACKED_BYTE_COUNT = 1;

    private static final int BUNDLE_VERSION_1 = 1;
    private static final int BUNDLE_VERSION_CURRENT = BUNDLE_VERSION_1;

    private static final String KEY_INITIATOR_TX = "initiator_tx";
    private static final String KEY_RESPONDER_TX = "responder_tx";

    @PulseShape private final int mInitiatorTx;
    @PulseShape private final int mResponderTx;

    public AliroPulseShapeCombo(@PulseShape int initiatorTx, @PulseShape int responderTx) {
        mInitiatorTx = initiatorTx;
        mResponderTx = responderTx;
    }

    public static int bytesUsed() {
        return ALIRO_PACKED_BYTE_COUNT;
    }

    public static AliroPulseShapeCombo fromBytes(byte[] data, int startIndex) {
        byte initiatorTx = (byte) ((data[startIndex] >> 4) & 0x0F);
        byte responderTx = (byte) (data[startIndex] & 0x0F);
        return new AliroPulseShapeCombo(initiatorTx, responderTx);
    }

    public byte[] toBytes() {
        byte pulseShapeCombo = (byte) (mInitiatorTx << 4 | mResponderTx);
        return ByteBuffer.allocate(bytesUsed()).put((byte) pulseShapeCombo).array();
    }

    @Override
    protected int getBundleVersion() {
        return BUNDLE_VERSION_CURRENT;
    }

    public String toString() {
        return getBundleVersion()
                + "."
                + getProtocolName()
                + "."
                + mInitiatorTx
                + "."
                + mResponderTx;
    }

    public static AliroPulseShapeCombo fromString(String aliroPulseShapeCombo) {
        String[] parts = aliroPulseShapeCombo.split("\\.", -1);
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid pulse shape combo: "
                    + aliroPulseShapeCombo);
        }

        int bundleVersion = Integer.parseInt(parts[0]);

        switch (bundleVersion) {
            case BUNDLE_VERSION_1:
                return parseBundleVersion1(aliroPulseShapeCombo);

            default:
                throw new IllegalArgumentException("unknown bundle version");
        }
    }

    private static AliroPulseShapeCombo parseBundleVersion1(String aliroPulseShapeCombo) {
        String[] parts = aliroPulseShapeCombo.split("\\.", -1);
        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "Invalid version 1 pulse shape combo: " + aliroPulseShapeCombo);
        }

        String protocolName = parts[1];

        if (!isCorrectProtocol(protocolName)) {
            throw new IllegalArgumentException("Invalid protocol");
        }

        @PulseShape int initiatorTx = Integer.parseInt(parts[2]);
        @PulseShape int responderTx = Integer.parseInt(parts[3]);

        return new AliroPulseShapeCombo(initiatorTx, responderTx);
    }

    @PulseShape
    public int getInitiatorTx() {
        return mInitiatorTx;
    }

    @PulseShape
    public int getResponderTx() {
        return mResponderTx;
    }

    @Override
    public boolean equals(@Nullable Object other) {
        if (other instanceof AliroPulseShapeCombo) {
            AliroPulseShapeCombo otherPulseShapeCombo = (AliroPulseShapeCombo) other;
            return otherPulseShapeCombo.mInitiatorTx == mInitiatorTx
                    && otherPulseShapeCombo.mResponderTx == mResponderTx;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new int[] {mInitiatorTx, mResponderTx});
    }
}
