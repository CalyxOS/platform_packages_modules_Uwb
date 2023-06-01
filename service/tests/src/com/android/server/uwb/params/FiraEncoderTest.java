/*
 * Copyright (C) 2021 The Android Open Source Project
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

package com.android.server.uwb.params;


import static com.google.common.truth.Truth.assertThat;
import static com.google.uwb.support.fira.FiraParams.MULTI_NODE_MODE_ONE_TO_MANY;
import static com.google.uwb.support.fira.FiraParams.MULTI_NODE_MODE_UNICAST;
import static com.google.uwb.support.fira.FiraParams.RANGE_DATA_NTF_CONFIG_ENABLE_PROXIMITY_AOA_LEVEL_TRIG;
import static com.google.uwb.support.fira.FiraParams.RANGING_DEVICE_DT_TAG;
import static com.google.uwb.support.fira.FiraParams.RANGING_DEVICE_ROLE_RESPONDER;
import static com.google.uwb.support.fira.FiraParams.RANGING_DEVICE_TYPE_CONTROLLER;
import static com.google.uwb.support.fira.FiraParams.RANGING_DEVICE_TYPE_DT_TAG;
import static com.google.uwb.support.fira.FiraParams.RANGING_DEVICE_UT_TAG;
import static com.google.uwb.support.fira.FiraParams.RANGING_ROUND_USAGE_DL_TDOA;
import static com.google.uwb.support.fira.FiraParams.RANGING_ROUND_USAGE_SS_TWR_DEFERRED_MODE;
import static com.google.uwb.support.fira.FiraParams.RANGING_ROUND_USAGE_UL_TDOA;
import static com.google.uwb.support.fira.FiraParams.RFRAME_CONFIG_SP1;
import static com.google.uwb.support.fira.FiraParams.SESSION_TYPE_RANGING;
import static com.google.uwb.support.fira.FiraParams.STS_CONFIG_PROVISIONED;
import static com.google.uwb.support.fira.FiraParams.STS_CONFIG_STATIC;
import static com.google.uwb.support.fira.FiraParams.TX_TIMESTAMP_40_BIT;
import static com.google.uwb.support.fira.FiraParams.UL_TDOA_DEVICE_ID_16_BIT;

import static org.junit.Assume.assumeTrue;

import android.platform.test.annotations.Presubmit;
import android.test.suitebuilder.annotation.SmallTest;
import android.uwb.UwbAddress;

import androidx.test.runner.AndroidJUnit4;

import com.android.modules.utils.build.SdkLevel;
import com.android.server.uwb.util.UwbUtil;

import com.google.uwb.support.fira.FiraOpenSessionParams;
import com.google.uwb.support.fira.FiraParams;
import com.google.uwb.support.fira.FiraRangingReconfigureParams;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;


/**
 * Unit tests for {@link com.android.server.uwb.params.FiraEncoder}.
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
@Presubmit
public class FiraEncoderTest {
    private static final FiraOpenSessionParams.Builder TEST_FIRA_OPEN_SESSION_COMMON_PARAMS =
            new FiraOpenSessionParams.Builder()
                    .setProtocolVersion(FiraParams.PROTOCOL_VERSION_1_1) // Required Parameter
                    .setSessionId(1)
                    .setSessionType(SESSION_TYPE_RANGING)
                    .setRangeDataNtfConfig(RANGE_DATA_NTF_CONFIG_ENABLE_PROXIMITY_AOA_LEVEL_TRIG)
                    .setDeviceType(RANGING_DEVICE_TYPE_CONTROLLER)
                    .setDeviceRole(RANGING_DEVICE_ROLE_RESPONDER)
                    .setDeviceAddress(UwbAddress.fromBytes(new byte[]{0x4, 0x6}))
                    .setDestAddressList(Arrays.asList(UwbAddress.fromBytes(new byte[]{0x4, 0x6})))
                    .setMultiNodeMode(MULTI_NODE_MODE_UNICAST)
                    .setRangingRoundUsage(RANGING_ROUND_USAGE_SS_TWR_DEFERRED_MODE)
                    .setStsConfig(STS_CONFIG_STATIC)
                    .setVendorId(new byte[]{0x5, 0x78})
                    .setStaticStsIV(new byte[]{0x1a, 0x55, 0x77, 0x47, 0x7e, 0x7d})
                    .setRangeDataNtfAoaAzimuthLower(-1.5)
                    .setRangeDataNtfAoaAzimuthUpper(2.5)
                    .setRangeDataNtfAoaElevationLower(-1.5)
                    .setRangeDataNtfAoaElevationUpper(1.2);

    private static final FiraOpenSessionParams.Builder TEST_FIRA_OPEN_SESSION_PARAMS_V_1_1 =
            new FiraOpenSessionParams.Builder(TEST_FIRA_OPEN_SESSION_COMMON_PARAMS);

    private static final FiraOpenSessionParams.Builder TEST_FIRA_OPEN_SESSION_PARAMS_V_2_0 =
            new FiraOpenSessionParams.Builder(TEST_FIRA_OPEN_SESSION_COMMON_PARAMS)
                    .setProtocolVersion(FiraParams.PROTOCOL_VERSION_2_0)
                    .setInitiationTime(1000)
                    .setLinkLayerMode(1)
                    .setApplicationDataEndpoint(1);

    private static final FiraRangingReconfigureParams.Builder TEST_FIRA_RECONFIGURE_PARAMS =
            new FiraRangingReconfigureParams.Builder()
                    .setBlockStrideLength(6)
                    .setRangeDataNtfConfig(RANGE_DATA_NTF_CONFIG_ENABLE_PROXIMITY_AOA_LEVEL_TRIG)
                    .setRangeDataProximityFar(6)
                    .setRangeDataProximityNear(4)
                    .setRangeDataAoaAzimuthLower(-1.5)
                    .setRangeDataAoaAzimuthUpper(2.5)
                    .setRangeDataAoaElevationLower(-1.5)
                    .setRangeDataAoaElevationUpper(1.2);

    private static final byte[] TEST_FIRA_RECONFIGURE_TLV_DATA =
            UwbUtil.getByteArray("2D01060E01040F020400100206001D0807D59E4707D56022");

    private static final FiraOpenSessionParams.Builder TEST_FIRA_UT_TAG_OPEN_SESSION_PARAM =
            new FiraOpenSessionParams.Builder()
                    .setProtocolVersion(FiraParams.PROTOCOL_VERSION_1_1)
                    .setSessionId(2)
                    .setSessionType(SESSION_TYPE_RANGING)
                    .setDeviceType(RANGING_DEVICE_TYPE_CONTROLLER)
                    .setDeviceRole(RANGING_DEVICE_UT_TAG)
                    .setDeviceAddress(UwbAddress.fromBytes(new byte[]{0x4, 0x6}))
                    .setDestAddressList(Arrays.asList(UwbAddress.fromBytes(new byte[]{0x4, 0x6})))
                    .setMultiNodeMode(MULTI_NODE_MODE_UNICAST)
                    .setStsConfig(STS_CONFIG_STATIC)
                    .setVendorId(new byte[]{0x5, 0x78})
                    .setStaticStsIV(new byte[]{0x1a, 0x55, 0x77, 0x47, 0x7e, 0x7d})
                    .setRangingRoundUsage(RANGING_ROUND_USAGE_UL_TDOA)
                    .setUlTdoaTxIntervalMs(1200)
                    .setUlTdoaRandomWindowMs(30)
                    .setUlTdoaDeviceIdType(UL_TDOA_DEVICE_ID_16_BIT)
                    .setUlTdoaDeviceId(new byte[]{0x0B, 0x0A})
                    .setUlTdoaTxTimestampType(TX_TIMESTAMP_40_BIT);

    private static final String DEVICE_TYPE_CONTROLLER_TLV = "000101";
    private static final String RANGING_ROUND_USAGE_SS_TWR_TLV = "010101";
    private static final String RANGING_ROUND_USAGE_UL_TDOA_TLV = "010100";
    private static final String RANGING_ROUND_USAGE_Dl_TDOA_TLV = "010105";
    private static final String STS_CONFIG_STATIC_TLV = "020100";
    private static final String STS_CONFIG_PROVISIONED_TLV = "020103";
    private static final String MULTI_NODE_MODE_UNICAST_TLV = "030100";
    private static final String MULTI_NODE_MODE_ONE_TO_MANY_TLV = "030101";
    private static final String CHANNEL_NUMBER_TLV = "040109";
    private static final String NUMBER_OF_CONTROLEES_TLV = "050101";
    private static final String DEVICE_MAC_ADDRESS_TLV = "06020406";
    private static final String SLOT_DURATION_TLV = "08026009";
    private static final String MAC_FCS_TYPE_TLV = "0B0100";
    private static final String RANGING_ROUND_CONTROL_TLV = "0C0103";
    private static final String AOA_RESULT_REQ_TLV = "0D0101";
    private static final String RANGE_DATA_NTF_CONFIG_AOA_LEVEL_TLV = "0E0104";
    private static final String RANGE_DATA_NTF_CONFIG_ENABLE_TLV = "0E0101";
    private static final String RANGE_DATA_NTF_PROXIMITY_NEAR_TLV = "0F020000";
    private static final String RANGE_DATA_NTF_PROXIMITY_FAR_TLV = "1002204E";
    private static final String DEVICE_ROLE_TLV = "110100";
    private static final String DEVICE_ROLE_UT_TAG_TLV = "110104";
    private static final String DEVICE_ROLE_DT_TAG_TLV = "110108";
    private static final String RFRAME_CONFIG_TLV = "120103";
    private static final String RFRAME_CONFIG_DL_TDOA_TLV = "120101";
    private static final String RSSI_REPORTING_TLV = "130100";
    private static final String PREAMBLE_CODE_INDEX_TLV = "14010A";
    private static final String SFD_ID_TLV = "150102";
    private static final String PSDU_DATA_RATE_TLV = "160100";
    private static final String PREAMBLE_DURATION_TLV = "170101";
    private static final String LINK_LAYER_MODE_TLV = "180101";
    private static final String RANGING_TIME_STRUCT_TLV = "1A0101";
    private static final String SLOTS_PER_RR_TLV = "1B0119";
    private static final String TX_ADAPTIVE_PAYLOAD_POWER_TLV = "1C0100";
    private static final String PRF_MODE_TLV = "1F0100";
    private static final String SCHEDULED_MODE_TLV = "220101";
    private static final String KEY_ROTATION_TLV = "230100";
    private static final String KEY_ROTATION_RATE_TLV = "240100";
    private static final String SESSION_PRIORITY_TLV = "250132";
    private static final String MAC_ADDRESS_MODE_TLV = "260100";
    private static final String NUMBER_OF_STS_SEGMENTS_TLV = "290101";
    private static final String MAX_RR_RETRY_TLV = "2A020000";
    private static final String HOPPING_MODE_TLV = "2C0100";
    private static final String BLOCK_STRIDE_LENGTH_TLV = "2D0100";
    private static final String RESULT_REPORT_CONFIG_TLV = "2E0101";
    private static final String IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV = "2F0101";
    private static final String BPRF_PHR_DATA_RATE_TLV = "310100";
    private static final String MAX_NUMBER_OF_MEASUREMENTS_TLV = "32020000";
    private static final String STS_LENGTH_TLV = "350101";
    private static final String RANGING_INTERVAL_TLV = "0904C8000000";
    private static final String DST_MAC_ADDRESS_TLV = "07020406";
    private static final String UWB_INITIATION_TIME_TLV = "2B0400000000";
    private static final String UWB_INITIATION_TIME_2_0_TLV = "2B08E803000000000000";
    private static final String VENDOR_ID_TLV = "27020578";
    private static final String STATIC_STS_IV_TLV = "28061A5577477E7D";
    private static final String RANGE_DATA_NTF_AOA_BOUND_TLV = "1D0807D59E4707D56022";
    private static final String APPLICATION_DATA_ENDPOINT_TLV = "4C0101";
    private static final String UL_TDOA_TX_INTERVAL_TLV = "3304B0040000";
    private static final String UL_TDOA_RANDOM_WINDOW_TLV = "34041E000000";
    private static final String UL_TDOA_DEVICE_ID_TLV = "3803010B0A";
    private static final String UL_TDOA_TX_TIMESTAMP_TLV = "390101";
    private final FiraEncoder mFiraEncoder = new FiraEncoder();
    private byte[] mFiraOpenSessionTlvUtTag;
    private byte[] mFiraSessionv11TlvData;
    private byte[] mFiraSessionv20TlvData;

    @Before
    public void setUp() {
        if (!SdkLevel.isAtLeastU()) {
            mFiraSessionv11TlvData = UwbUtil.getByteArray(
                    "01010102010003010004010906020604080260090B01000C01030D01010E01040F02"
                            + "00001002204E11010012010313010014010A1501021601001701011A01011B01191C"
                            + "01001F01002201012301002401002501322601002901012A0200002C01002D01002E"
                            + "01012F0101310100320200003501010904C8000000000101050101070206042B0400"
                            + "0000002702780528061A5577477E7D1D0807D59E4707D56022");

            mFiraOpenSessionTlvUtTag = UwbUtil.getByteArray(
                    "01010002010003010004010906020604080260090B01000C01030D01010E01010F02"
                            + "00001002204E11010412010313010014010A1501021601001701011A01011B01191C"
                            + "01001F01002201012301002401002501322601002901012A0200002C01002D01002E"
                            + "01012F010131010032020000350101000101050101070206042B0400000000270278"
                            + "0528061A5577477E7D3304B004000034041E0000003803010B0A390101");
        } else {
            mFiraSessionv11TlvData = UwbUtil.getByteArray(RANGING_ROUND_USAGE_SS_TWR_TLV
                    + STS_CONFIG_STATIC_TLV + MULTI_NODE_MODE_UNICAST_TLV + CHANNEL_NUMBER_TLV
                    + DEVICE_MAC_ADDRESS_TLV + SLOT_DURATION_TLV + MAC_FCS_TYPE_TLV
                    + RANGING_ROUND_CONTROL_TLV + AOA_RESULT_REQ_TLV
                    + RANGE_DATA_NTF_CONFIG_AOA_LEVEL_TLV + RANGE_DATA_NTF_PROXIMITY_NEAR_TLV
                    + RANGE_DATA_NTF_PROXIMITY_FAR_TLV + DEVICE_ROLE_TLV + RFRAME_CONFIG_TLV
                    + RSSI_REPORTING_TLV + PREAMBLE_CODE_INDEX_TLV + SFD_ID_TLV + PSDU_DATA_RATE_TLV
                    + PREAMBLE_DURATION_TLV + RANGING_TIME_STRUCT_TLV + SLOTS_PER_RR_TLV
                    + TX_ADAPTIVE_PAYLOAD_POWER_TLV + PRF_MODE_TLV + SCHEDULED_MODE_TLV
                    + KEY_ROTATION_TLV + KEY_ROTATION_RATE_TLV + SESSION_PRIORITY_TLV
                    + MAC_ADDRESS_MODE_TLV + NUMBER_OF_STS_SEGMENTS_TLV + MAX_RR_RETRY_TLV
                    + HOPPING_MODE_TLV + BLOCK_STRIDE_LENGTH_TLV + RESULT_REPORT_CONFIG_TLV
                    + IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV + BPRF_PHR_DATA_RATE_TLV
                    + MAX_NUMBER_OF_MEASUREMENTS_TLV + STS_LENGTH_TLV + RANGING_INTERVAL_TLV
                    + DEVICE_TYPE_CONTROLLER_TLV + NUMBER_OF_CONTROLEES_TLV + DST_MAC_ADDRESS_TLV
                    + UWB_INITIATION_TIME_TLV + VENDOR_ID_TLV + STATIC_STS_IV_TLV
                    + RANGE_DATA_NTF_AOA_BOUND_TLV);

            mFiraSessionv20TlvData = UwbUtil.getByteArray(RANGING_ROUND_USAGE_SS_TWR_TLV
                    + STS_CONFIG_STATIC_TLV + MULTI_NODE_MODE_UNICAST_TLV + CHANNEL_NUMBER_TLV
                    + DEVICE_MAC_ADDRESS_TLV + SLOT_DURATION_TLV + MAC_FCS_TYPE_TLV
                    + RANGING_ROUND_CONTROL_TLV + AOA_RESULT_REQ_TLV
                    + RANGE_DATA_NTF_CONFIG_AOA_LEVEL_TLV + RANGE_DATA_NTF_PROXIMITY_NEAR_TLV
                    + RANGE_DATA_NTF_PROXIMITY_FAR_TLV + DEVICE_ROLE_TLV + RFRAME_CONFIG_TLV
                    + RSSI_REPORTING_TLV + PREAMBLE_CODE_INDEX_TLV + SFD_ID_TLV
                    + PSDU_DATA_RATE_TLV + PREAMBLE_DURATION_TLV + RANGING_TIME_STRUCT_TLV
                    + SLOTS_PER_RR_TLV + TX_ADAPTIVE_PAYLOAD_POWER_TLV
                    + PRF_MODE_TLV + SCHEDULED_MODE_TLV + KEY_ROTATION_TLV + KEY_ROTATION_RATE_TLV
                    + SESSION_PRIORITY_TLV + MAC_ADDRESS_MODE_TLV + NUMBER_OF_STS_SEGMENTS_TLV
                    + MAX_RR_RETRY_TLV + HOPPING_MODE_TLV + BLOCK_STRIDE_LENGTH_TLV
                    + RESULT_REPORT_CONFIG_TLV + IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV
                    + BPRF_PHR_DATA_RATE_TLV + MAX_NUMBER_OF_MEASUREMENTS_TLV + STS_LENGTH_TLV
                    + RANGING_INTERVAL_TLV + DEVICE_TYPE_CONTROLLER_TLV + NUMBER_OF_CONTROLEES_TLV
                    + DST_MAC_ADDRESS_TLV + UWB_INITIATION_TIME_2_0_TLV + LINK_LAYER_MODE_TLV
                    + APPLICATION_DATA_ENDPOINT_TLV + VENDOR_ID_TLV + STATIC_STS_IV_TLV
                    + RANGE_DATA_NTF_AOA_BOUND_TLV);

            mFiraOpenSessionTlvUtTag = UwbUtil.getByteArray(
                    RANGING_ROUND_USAGE_UL_TDOA_TLV
                    + STS_CONFIG_STATIC_TLV + MULTI_NODE_MODE_UNICAST_TLV + CHANNEL_NUMBER_TLV
                    + DEVICE_MAC_ADDRESS_TLV + SLOT_DURATION_TLV + MAC_FCS_TYPE_TLV
                    + RANGING_ROUND_CONTROL_TLV + AOA_RESULT_REQ_TLV
                    + RANGE_DATA_NTF_CONFIG_ENABLE_TLV + RANGE_DATA_NTF_PROXIMITY_NEAR_TLV
                    + RANGE_DATA_NTF_PROXIMITY_FAR_TLV + DEVICE_ROLE_UT_TAG_TLV
                    + RFRAME_CONFIG_TLV + RSSI_REPORTING_TLV + PREAMBLE_CODE_INDEX_TLV
                    + SFD_ID_TLV + PSDU_DATA_RATE_TLV + PREAMBLE_DURATION_TLV
                    + RANGING_TIME_STRUCT_TLV + SLOTS_PER_RR_TLV + TX_ADAPTIVE_PAYLOAD_POWER_TLV
                    + PRF_MODE_TLV + SCHEDULED_MODE_TLV + KEY_ROTATION_TLV + KEY_ROTATION_RATE_TLV
                    + SESSION_PRIORITY_TLV + MAC_ADDRESS_MODE_TLV + NUMBER_OF_STS_SEGMENTS_TLV
                    + MAX_RR_RETRY_TLV + HOPPING_MODE_TLV + BLOCK_STRIDE_LENGTH_TLV
                    + RESULT_REPORT_CONFIG_TLV + IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV
                    + BPRF_PHR_DATA_RATE_TLV + MAX_NUMBER_OF_MEASUREMENTS_TLV + STS_LENGTH_TLV
                    + DEVICE_TYPE_CONTROLLER_TLV + NUMBER_OF_CONTROLEES_TLV + DST_MAC_ADDRESS_TLV
                    + UWB_INITIATION_TIME_TLV + VENDOR_ID_TLV + STATIC_STS_IV_TLV
                    + UL_TDOA_TX_INTERVAL_TLV + UL_TDOA_RANDOM_WINDOW_TLV + UL_TDOA_DEVICE_ID_TLV
                    + UL_TDOA_TX_TIMESTAMP_TLV);
        }
    }


    @Test
    public void testFiraOpenSessionParams() throws Exception {
        // Test FiRa v1.1 Params
        FiraOpenSessionParams params = TEST_FIRA_OPEN_SESSION_PARAMS_V_1_1.build();
        TlvBuffer tlvs = mFiraEncoder.getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(45);
        assertThat(tlvs.getByteArray()).isEqualTo(mFiraSessionv11TlvData);

        // Test FiRa v2.0 Params
        if (SdkLevel.isAtLeastU()) {
            params = TEST_FIRA_OPEN_SESSION_PARAMS_V_2_0.build();
            tlvs = mFiraEncoder.getTlvBuffer(params);

            assertThat(tlvs.getNoOfParams()).isEqualTo(47);
            assertThat(tlvs.getByteArray()).isEqualTo(mFiraSessionv20TlvData);
        }
    }

    @Test
    public void testFiraRangingReconfigureParams() throws Exception {
        FiraRangingReconfigureParams params = TEST_FIRA_RECONFIGURE_PARAMS.build();
        TlvBuffer tlvs = mFiraEncoder.getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(5);
        assertThat(tlvs.getByteArray()).isEqualTo(TEST_FIRA_RECONFIGURE_TLV_DATA);
    }

    // This test could be changed to just check that TlvEncoder returns a FiraEncoder, as
    // above testFiraOpenSessionParams() already checks the encoding done by FiraEncoder.
    @Test
    public void testFiraOpenSessionParamsViaTlvEncoder() throws Exception {
        FiraOpenSessionParams params = TEST_FIRA_OPEN_SESSION_PARAMS_V_1_1.build();
        TlvBuffer tlvs = TlvEncoder.getEncoder(FiraParams.PROTOCOL_NAME).getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(45);
        assertThat(tlvs.getByteArray()).isEqualTo(mFiraSessionv11TlvData);
    }

    @Test
    public void testFiraRangingReconfigureParamsViaTlvEncoder() throws Exception {
        FiraRangingReconfigureParams params = TEST_FIRA_RECONFIGURE_PARAMS.build();
        TlvBuffer tlvs = TlvEncoder.getEncoder(FiraParams.PROTOCOL_NAME).getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(5);
        assertThat(tlvs.getByteArray()).isEqualTo(TEST_FIRA_RECONFIGURE_TLV_DATA);
    }

    @Test
    public void testFiraOpenSessionParamsUtTag() throws Exception {
        FiraOpenSessionParams params = TEST_FIRA_UT_TAG_OPEN_SESSION_PARAM.build();
        TlvBuffer tlvs = mFiraEncoder.getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(47);
        assertThat(tlvs.getByteArray()).isEqualTo(mFiraOpenSessionTlvUtTag);

    }

    @Test
    public void testFiraOpenSessionParamsProvisionedSts() throws Exception {
        FiraOpenSessionParams params =
                new FiraOpenSessionParams.Builder()
                        .setProtocolVersion(FiraParams.PROTOCOL_VERSION_1_1)
                        .setSessionId(1)
                        .setSessionType(SESSION_TYPE_RANGING)
                        .setRangeDataNtfConfig(
                                RANGE_DATA_NTF_CONFIG_ENABLE_PROXIMITY_AOA_LEVEL_TRIG)
                        .setDeviceType(RANGING_DEVICE_TYPE_CONTROLLER)
                        .setDeviceRole(RANGING_DEVICE_ROLE_RESPONDER)
                        .setDeviceAddress(UwbAddress.fromBytes(new byte[]{0x4, 0x6}))
                        .setDestAddressList(Arrays.asList(UwbAddress.fromBytes(
                                new byte[]{0x4, 0x6})))
                        .setMultiNodeMode(MULTI_NODE_MODE_UNICAST)
                        .setRangingRoundUsage(RANGING_ROUND_USAGE_SS_TWR_DEFERRED_MODE)
                        .setStsConfig(STS_CONFIG_PROVISIONED)
                        .setSessionKey(new byte[]{0x5, 0x78, 0x5, 0x78, 0x5, 0x78, 0x5, 0x78, 0x5,
                                0x78, 0x5, 0x78, 0x5, 0x78, 0x5, 0x78})
                        .setRangeDataNtfAoaAzimuthLower(-1.5)
                        .setRangeDataNtfAoaAzimuthUpper(2.5)
                        .setRangeDataNtfAoaElevationLower(-1.5)
                        .setRangeDataNtfAoaElevationUpper(1.2)
                        .build();

        byte[] expected_data;
        String session_key = "451005780578057805780578057805780578";
        if (!SdkLevel.isAtLeastU()) {
            expected_data = UwbUtil.getByteArray(
                    "01010102010303010004010906020604080260090B01000C01030D01010E01040F02"
                            + "00001002204E11010012010313010014010A1501021601001701011A01011B01191C"
                            + "01001F01002201012301002401002501322601002901012A0200002C01002D01002E"
                            + "01012F0101310100320200003501010904C8000000000101050101070206042B0400"
                            + "0000004510057805780578057805780578057805781D0807D59E4707D56022");
        } else {
            expected_data = UwbUtil.getByteArray(RANGING_ROUND_USAGE_SS_TWR_TLV
                    + STS_CONFIG_PROVISIONED_TLV + MULTI_NODE_MODE_UNICAST_TLV + CHANNEL_NUMBER_TLV
                    + DEVICE_MAC_ADDRESS_TLV + SLOT_DURATION_TLV + MAC_FCS_TYPE_TLV
                    + RANGING_ROUND_CONTROL_TLV + AOA_RESULT_REQ_TLV
                    + RANGE_DATA_NTF_CONFIG_AOA_LEVEL_TLV + RANGE_DATA_NTF_PROXIMITY_NEAR_TLV
                    + RANGE_DATA_NTF_PROXIMITY_FAR_TLV + DEVICE_ROLE_TLV + RFRAME_CONFIG_TLV
                    + RSSI_REPORTING_TLV + PREAMBLE_CODE_INDEX_TLV + SFD_ID_TLV
                    + PSDU_DATA_RATE_TLV + PREAMBLE_DURATION_TLV + RANGING_TIME_STRUCT_TLV
                    + SLOTS_PER_RR_TLV + TX_ADAPTIVE_PAYLOAD_POWER_TLV + PRF_MODE_TLV
                    + SCHEDULED_MODE_TLV + KEY_ROTATION_TLV + KEY_ROTATION_RATE_TLV
                    + SESSION_PRIORITY_TLV + MAC_ADDRESS_MODE_TLV + NUMBER_OF_STS_SEGMENTS_TLV
                    + MAX_RR_RETRY_TLV + HOPPING_MODE_TLV + BLOCK_STRIDE_LENGTH_TLV
                    + RESULT_REPORT_CONFIG_TLV + IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV
                    + BPRF_PHR_DATA_RATE_TLV + MAX_NUMBER_OF_MEASUREMENTS_TLV + STS_LENGTH_TLV
                    + RANGING_INTERVAL_TLV + DEVICE_TYPE_CONTROLLER_TLV + NUMBER_OF_CONTROLEES_TLV
                    + DST_MAC_ADDRESS_TLV + UWB_INITIATION_TIME_TLV + session_key
                    + RANGE_DATA_NTF_AOA_BOUND_TLV);
        }
        TlvBuffer tlvs = mFiraEncoder.getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(44);
        assertThat(tlvs.getByteArray()).isEqualTo(expected_data);
    }

    @Test
    public void testFiraDlTdoaDtTagSession() throws Exception  {
        assumeTrue(SdkLevel.isAtLeastU());
        FiraOpenSessionParams params =
                new FiraOpenSessionParams.Builder()
                        .setProtocolVersion(FiraParams.PROTOCOL_VERSION_1_1)
                        .setSessionId(1)
                        .setSessionType(SESSION_TYPE_RANGING)
                        .setDeviceType(RANGING_DEVICE_TYPE_DT_TAG)
                        .setDeviceRole(RANGING_DEVICE_DT_TAG)
                        .setDeviceAddress(UwbAddress.fromBytes(new byte[]{0x4, 0x6}))
                        .setDestAddressList(Arrays.asList(UwbAddress.fromBytes(
                                new byte[]{0x4, 0x6})))
                        .setMultiNodeMode(MULTI_NODE_MODE_ONE_TO_MANY)
                        .setRangingRoundUsage(RANGING_ROUND_USAGE_DL_TDOA)
                        .setRframeConfig(RFRAME_CONFIG_SP1)
                        .setVendorId(new byte[]{0x5, 0x78})
                        .setStaticStsIV(new byte[]{0x1a, 0x55, 0x77, 0x47, 0x7e, 0x7d})
                        .build();

        byte[] expected_data = UwbUtil.getByteArray(RANGING_ROUND_USAGE_Dl_TDOA_TLV
                + STS_CONFIG_STATIC_TLV + MULTI_NODE_MODE_ONE_TO_MANY_TLV + CHANNEL_NUMBER_TLV
                + DEVICE_MAC_ADDRESS_TLV + SLOT_DURATION_TLV + MAC_FCS_TYPE_TLV
                + RANGING_ROUND_CONTROL_TLV + AOA_RESULT_REQ_TLV
                + RANGE_DATA_NTF_CONFIG_ENABLE_TLV + RANGE_DATA_NTF_PROXIMITY_NEAR_TLV
                + RANGE_DATA_NTF_PROXIMITY_FAR_TLV + DEVICE_ROLE_DT_TAG_TLV
                + RFRAME_CONFIG_DL_TDOA_TLV + RSSI_REPORTING_TLV + PREAMBLE_CODE_INDEX_TLV
                + SFD_ID_TLV + PSDU_DATA_RATE_TLV + PREAMBLE_DURATION_TLV + RANGING_TIME_STRUCT_TLV
                + SLOTS_PER_RR_TLV + TX_ADAPTIVE_PAYLOAD_POWER_TLV + PRF_MODE_TLV
                + SCHEDULED_MODE_TLV + KEY_ROTATION_TLV + KEY_ROTATION_RATE_TLV
                + SESSION_PRIORITY_TLV + MAC_ADDRESS_MODE_TLV + NUMBER_OF_STS_SEGMENTS_TLV
                + MAX_RR_RETRY_TLV + HOPPING_MODE_TLV + BLOCK_STRIDE_LENGTH_TLV
                + RESULT_REPORT_CONFIG_TLV + IN_BAND_TERMINATION_ATTEMPT_COUNT_TLV
                + BPRF_PHR_DATA_RATE_TLV + MAX_NUMBER_OF_MEASUREMENTS_TLV + STS_LENGTH_TLV
                + RANGING_INTERVAL_TLV + VENDOR_ID_TLV + STATIC_STS_IV_TLV);

        TlvBuffer tlvs = mFiraEncoder.getTlvBuffer(params);

        assertThat(tlvs.getNoOfParams()).isEqualTo(40);
        assertThat(tlvs.getByteArray()).isEqualTo(expected_data);
    }
}
