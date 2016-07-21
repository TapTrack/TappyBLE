/*
 * Copyright (c) 2016. Papyrus Electronics, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taptrack.tcmptappy.utils;

import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.tappy.constants.TagTypes;
import com.taptrack.tcmptappy.tcmp.StandardErrorResponse;
import com.taptrack.tcmptappy.tcmp.StandardLibraryVersionResponse;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.AbstractBasicNfcMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.LockTagCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.ScanNdefCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.ScanTagCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StopCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StreamNdefCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StreamTagsCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.BasicNfcErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.BasicNfcLibraryVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.NdefFoundResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.ScanTimeoutResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.TagFoundResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.TagLockedResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.responses.TagWrittenResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.AbstractMifareClassicMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.KeySetting;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.MifareClassicDetectedResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.MifareClassicLibraryErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.MifareClassicLibraryVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.MifareClassicReadSuccessResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.responses.MifareClassicTimeoutResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.AbstractSystemMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.CrcMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.FirmwareVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.GetBatteryLevelResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.HardwareVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.ImproperMessageFormatResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.LcsMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.LengthMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.PingResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.SystemErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.AbstractType4Message;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BSpecificAfiCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4Command;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.GetType4LibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.TransceiveApduCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.APDUTransceiveSuccessfulResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.Type4BDetectedResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.Type4DetectedResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.Type4ErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.Type4LibraryVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.responses.Type4TimeoutResponse;

import java.nio.charset.Charset;
import java.util.Arrays;

public class TcmpMessageDescriptor {
    public static String getCommandDescription(@NonNull TCMPMessage command,
                                               @NonNull Context ctx) {
        if(command instanceof AbstractBasicNfcMessage) {
            return getCommandDescriptionBasicNfc(command, ctx);
        }
        else if(command instanceof AbstractSystemMessage) {
            return getCommandDescriptionSystem(command, ctx);
        }
        else if(command instanceof AbstractMifareClassicMessage) {
            return getCommandDescriptionClassic(command, ctx);
        }
        else if(command instanceof AbstractType4Message) {
            return getCommandDescriptionType4(command, ctx);
        }
        else {
            return ctx.getString(R.string.unknown_command);
        }
    }

    protected static String getCommandDescriptionBasicNfc(@NonNull TCMPMessage command,
                                                   @NonNull Context ctx) {
        if(command instanceof GetBasicNfcLibraryVersionCommand) {
            return ctx.getString(R.string.get_basic_nfc_lib_version);
        }
        else if (command instanceof ScanNdefCommand) {
            if(((ScanNdefCommand) command).getTimeout() != 0) {
                String form = ctx.getString(R.string.scan_ndef_seconds);
                return String.format(form,(0xff&((ScanNdefCommand) command).getTimeout()));
            }
            else {
                return ctx.getString(R.string.scan_ndef_indefinite);
            }
        }
        else if (command instanceof StreamNdefCommand) {
            if(((StreamNdefCommand) command).getTimeout() != 0) {
                String form = ctx.getString(R.string.stream_ndef_seconds);
                return String.format(form,(0xff&((StreamNdefCommand) command).getTimeout()));
            }
            else {
                return ctx.getString(R.string.stream_tag_indefinitely);
            }
        }
        else if (command instanceof ScanTagCommand) {
            if(((ScanTagCommand) command).getTimeout() != 0) {
                String form = ctx.getString(R.string.scan_tag_seconds);
                return String.format(form,(0xff&((ScanTagCommand) command).getTimeout()));
            }
            else {
                return ctx.getString(R.string.scan_tag_indefinitely);
            }

        }
        else if (command instanceof StreamTagsCommand) {
            if(((StreamTagsCommand) command).getTimeout() != 0) {
                String form = ctx.getString(R.string.stream_tag_seconds);
                return String.format(form,(0xff&((StreamTagsCommand) command).getTimeout()));
            }
            else {
                return ctx.getString(R.string.stream_tag_indefinitely);
            }
        }
        else if (command instanceof WriteNdefTextRecordCommand) {
            WriteNdefTextRecordCommand cmd = (WriteNdefTextRecordCommand) command;
            if(cmd.getTimeout() != 0) {
                String form = ctx.getString(R.string.write_ndef_txt_seconds);
                return String.format(form, (0xff & (cmd.getTimeout())), new String(cmd.getText()));
            }
            else {
                String form = ctx.getString(R.string.write_ndef_txt_indefinite);
                return String.format(form,new String(cmd.getText()));
            }
        }
        else if (command instanceof WriteNdefUriRecordCommand) {
            WriteNdefUriRecordCommand cmd = (WriteNdefUriRecordCommand) command;
            String uri = NdefUriCodeUtils.decodeNdefUri(cmd.getUriCode(),cmd.getUriBytes());
            if(cmd.getTimeout() != 0) {
                String form = ctx.getString(R.string.write_ndef_uri_seconds);
                return String.format(form, (0xff & (cmd.getTimeout())),uri);
            }
            else {
                String form = ctx.getString(R.string.write_ndef_uri_indefinite);
                return String.format(form,uri);
            }
        }
        else if (command instanceof StopCommand) {
            return ctx.getString(R.string.stop_command);
        }
        else if (command instanceof LockTagCommand) {
            LockTagCommand cmd = (LockTagCommand) command;
            if(cmd.getTimeout() != 0) {
                String form = ctx.getString(R.string.lock_tags_seconds);
                return String.format(form, (0xff & cmd.getTimeout()));
            } else {
                return ctx.getString(R.string.lock_tags_indefinite);
            }
        }
        else {
            return ctx.getString(R.string.unknown_command);
        }
    }
    protected static String getCommandDescriptionSystem(@NonNull TCMPMessage command,
                                                          @NonNull Context ctx) {
        if (command instanceof GetBatteryLevelCommand) {
            return ctx.getString(R.string.get_battery_level);
        }
        else if (command instanceof GetFirmwareVersionCommand) {
            return ctx.getString(R.string.get_firmware_version);
        }
        else if (command instanceof GetHardwareVersionCommand) {
            return ctx.getString(R.string.get_hardware_version);
        }
        else if (command instanceof PingCommand) {
            return ctx.getString(R.string.ping_command);
        }
        else {
            return ctx.getString(R.string.unknown_command);
        }
    }

    protected static String getCommandDescriptionClassic(@NonNull TCMPMessage command,
                                                          @NonNull Context ctx) {
        if(command instanceof DetectMifareClassicCommand) {
            DetectMifareClassicCommand cmd = (DetectMifareClassicCommand) command;
            if(cmd.getTimeout() == 0x00) {
                return ctx.getString(R.string.detect_classic_indefinite);
            }
            else {
                return String.format(ctx.getString(R.string.detect_classic_seconds), cmd.getTimeout() & 0xff);
            }
        }
        else if (command instanceof GetMifareClassicLibraryVersionCommand) {
            return ctx.getString(R.string.get_classic_version);
        }
        else if (command instanceof ReadMifareClassicCommand) {
            ReadMifareClassicCommand cmd = (ReadMifareClassicCommand) command;
            if(cmd.getTimeout() == 0x00) {
                return String.format(ctx.getString(R.string.read_classic_indefinite),
                        cmd.getStartBlock(),
                        cmd.getEndBlock(),
                        (cmd.getKeySetting() == KeySetting.KEY_A ? "A" : "B"),
                        ByteUtils.bytesToHex(cmd.getKey()));
            }
            else {
                return String.format(ctx.getString(R.string.read_classic_seconds),
                        cmd.getStartBlock(),
                        cmd.getEndBlock(),
                        (cmd.getKeySetting() == KeySetting.KEY_A ? "A" : "B"),
                        ByteUtils.bytesToHex(cmd.getKey()),
                        cmd.getTimeout() & 0xff);
            }
        }
        else {
            return ctx.getString(R.string.unknown_command);
        }
    }

    protected static String getCommandDescriptionType4(@NonNull TCMPMessage command,
                                                          @NonNull Context ctx) {
        if(command instanceof DetectType4Command) {
            DetectType4Command cmd = ((DetectType4Command) command);
            if(cmd.getTimeout() == 0x00) {
                return ctx.getString(R.string.detect_type4_indefinite);
            }
            else {
                return String.format(ctx.getString(R.string.detect_type4_seconds),cmd.getTimeout() & 0xff);
            }
        }
        else if (command instanceof DetectType4BCommand) {
            DetectType4BCommand cmd = ((DetectType4BCommand) command);
            if(cmd.getTimeout() == 0x00) {
                return ctx.getString(R.string.detect_type4b_indefinite);
            }
            else {
                return String.format(ctx.getString(R.string.detect_type4b_seconds),cmd.getTimeout() & 0xff);
            }

        }
        else if (command instanceof DetectType4BSpecificAfiCommand) {
            DetectType4BSpecificAfiCommand cmd = ((DetectType4BSpecificAfiCommand) command);
            if(cmd.getTimeout() == 0x00) {
                return String.format(ctx.getString(R.string.detect_type4b_afi_indefinite),
                        ByteUtils.bytesToHex(new byte[]{cmd.getAfi()}));
            }
            else {
                return String.format(ctx.getString(R.string.detect_type4b_afi_seconds),
                        cmd.getTimeout() & 0xff,
                        ByteUtils.bytesToHex(new byte[]{cmd.getAfi()}));
            }

        }
        else if (command instanceof GetType4LibraryVersionCommand) {
            return ctx.getString(R.string.get_type4_version);
        }
        else if (command instanceof TransceiveApduCommand) {
            return String.format(ctx.getString(R.string.send_apdu), ByteUtils.bytesToHex(((TransceiveApduCommand) command).getApdu()));
        }
        else {
            return ctx.getString(R.string.unknown_command);
        }
    }

    public static String getResponseDescription(@NonNull TCMPMessage response,
                                                @NonNull Context ctx) {
        if (response instanceof StandardLibraryVersionResponse) {
            if(response instanceof HardwareVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx, R.string.resp_hardware, (StandardLibraryVersionResponse) response);
            }
            else if(response instanceof FirmwareVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx,R.string.resp_firmware, (StandardLibraryVersionResponse) response);
            }
            else if(response instanceof BasicNfcLibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx,R.string.family_basicnfc, (StandardLibraryVersionResponse) response);
            }
            else if (response instanceof MifareClassicLibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx,R.string.family_classic, (StandardLibraryVersionResponse) response);
            }
            else if (response instanceof Type4LibraryVersionResponse) {
                return parseStandardLibraryVersionResponse(ctx,R.string.family_type4, (StandardLibraryVersionResponse) response);
            }
            else {
                return parseStandardLibraryVersionResponse(ctx,R.string.family_unknown, (StandardLibraryVersionResponse) response);
            }
        }
        else if(response instanceof AbstractSystemMessage) {
            return getSystemResponseDescription(response, ctx);
        }
        else if (response instanceof AbstractBasicNfcMessage) {
            return getBasicNfcResponseDescription(response, ctx);
        }
        else if (response instanceof AbstractType4Message) {
            return getType4ResponseDescription(response, ctx);
        }
        else if (response instanceof AbstractMifareClassicMessage) {
            return getClassicResponseDescription(response, ctx);
        }
        else if(response instanceof StandardErrorResponse) {
            return parseStandardErrorResponse(ctx,
                    R.string.family_unknown,
                    (StandardErrorResponse) response);
        }
        else {
            return ctx.getString(R.string.unknown_response);
        }
    }

    private static String getSystemResponseDescription(@NonNull TCMPMessage response,
                                                      @NonNull Context ctx) {
        if(response instanceof CrcMismatchErrorResponse) {
            return ctx.getString(R.string.crc_error);
        }
        else if(response instanceof GetBatteryLevelResponse) {
            GetBatteryLevelResponse resp = (GetBatteryLevelResponse) response;
            return String.format(
                    ctx.getString(R.string.get_batt_response),
                    resp.getBatteryLevelPercent());

        }
        else if(response instanceof ImproperMessageFormatResponse) {
            return ctx.getString(R.string.improper_message_format_response);

        }
        else if(response instanceof LcsMismatchErrorResponse) {
            return ctx.getString(R.string.lcs_mismatch_response);

        }
        else if(response instanceof LengthMismatchErrorResponse) {
            return ctx.getString(R.string.length_check_failed);

        }
        else if(response instanceof PingResponse) {
            return ctx.getString(R.string.ping_response);

        }
        else if(response instanceof SystemErrorResponse) {
            SystemErrorResponse resp = (SystemErrorResponse) response;
            int errorRes;
            if(resp.getErrorCode() == SystemErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter;
            }
            else if(resp.getErrorCode() == SystemErrorResponse.ErrorCodes.UNSUPPORTED_COMMAND_FAMILY) {
                errorRes = R.string.err_unsupported_command_family;
            }
            else if(resp.getErrorCode() == SystemErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters;
            }
            else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_system,
                        (StandardErrorResponse) response);
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_system,
                    errorRes,
                    (StandardErrorResponse) response);
        }
        else {
            return ctx.getString(R.string.unknown_response);
        }
    }

    private static String getBasicNfcResponseDescription(@NonNull TCMPMessage response,
                                                       @NonNull Context ctx) {
        if(response instanceof NdefFoundResponse) {
            NdefFoundResponse resp = (NdefFoundResponse) response;
            return parseNdefFoundResponse(ctx, resp);
        }
        else if(response instanceof ScanTimeoutResponse) {
            return ctx.getString(R.string.scan_timeout_response);
        }
        else if(response instanceof TagFoundResponse) {
            TagFoundResponse resp = (TagFoundResponse) response;
            return String.format(
                    ctx.getString(R.string.tag_found_response),
                    ByteUtils.bytesToHex(resp.getTagCode()),
                    parseTagType(ctx, resp.getTagType()));
        }
        else if(response instanceof TagWrittenResponse) {
            TagWrittenResponse resp = (TagWrittenResponse) response;
            return String.format(
                    ctx.getString(R.string.tag_written_response),
                    ByteUtils.bytesToHex(resp.getTagCode()));
        }
        else if(response instanceof TagLockedResponse) {
            TagLockedResponse resp = (TagLockedResponse) response;
            return String.format(
                    ctx.getString(R.string.tag_locked_response),
                    ByteUtils.bytesToHex(resp.getTagCode()));
        }
        else if (response instanceof BasicNfcErrorResponse) {
            BasicNfcErrorResponse resp = (BasicNfcErrorResponse) response;
            int errorRes;
            if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.POLLING_ERROR) {
                errorRes = R.string.err_polling_error;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.NDEF_MESSAGE_TOO_LARGE) {
                errorRes = R.string.err_ndef_too_large;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.ERROR_CREATING_NDEF_CONTENT) {
                errorRes = R.string.err_ndef_creation_error;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.ERROR_WRITING_NDEF_CONTENT) {
                errorRes = R.string.err_ndef_writing_error;
            }
            else if(resp.getErrorCode() == BasicNfcErrorResponse.ErrorCodes.ERROR_LOCKING_TAG) {
                errorRes = R.string.err_locking_error;
            }
            else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_basicnfc,
                        (StandardErrorResponse) response);
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_basicnfc,
                    errorRes,
                    (StandardErrorResponse) response);
        }
        else {
            return ctx.getString(R.string.unknown_response);
        }
    }

    private static String getType4ResponseDescription(@NonNull TCMPMessage response,
                                             @NonNull Context ctx) {
        if(response instanceof APDUTransceiveSuccessfulResponse) {
            return String.format(ctx.getString(R.string.apdu_transceive_successful),
                    ByteUtils.bytesToHex(((APDUTransceiveSuccessfulResponse) response).getApdu()));
        }
        else if(response instanceof Type4DetectedResponse) {
            Type4DetectedResponse resp = (Type4DetectedResponse) response;
            if(resp.getAts() != null && resp.getAts().length != 0) {
                return String.format(ctx.getString(R.string.type4_detected_with_ats),
                        ByteUtils.bytesToHex(resp.getUid()),
                        ByteUtils.bytesToHex(resp.getAts()));
            }
            else {
                return String.format(ctx.getString(R.string.type4_detected_no_ats),
                        ByteUtils.bytesToHex(resp.getUid()));
            }
        }
        else if(response instanceof Type4BDetectedResponse) {
            Type4BDetectedResponse resp = (Type4BDetectedResponse) response;
            byte[] atqb = resp.getAtqb();
            byte[] attrib = resp.getAttrib();
            if(atqb == null || attrib == null) {
                //this should be impossible
                return ctx.getString(R.string.type4b_detected_nothing);
            }
            else if(attrib.length == 0){
                return String.format(ctx.getString(R.string.type4b_detected_no_attrib),
                        ByteUtils.bytesToHex(resp.getAtqb()));
            }
            else {
                return String.format(ctx.getString(R.string.type4b_detected_w_attrib),
                        ByteUtils.bytesToHex(resp.getAtqb()),
                        ByteUtils.bytesToHex(resp.getAttrib()));
            }
        }
        else if (response instanceof Type4TimeoutResponse) {
            return ctx.getString(R.string.type4_timeout);
        }
        else if (response instanceof Type4ErrorResponse) {
            Type4ErrorResponse resp = (Type4ErrorResponse) response;
            int errorRes;
            if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter;
            }
            else if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters;
            }
            else if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.TOO_MANY_PARAMETERS) {
                errorRes = R.string.err_too_many_parameters;
            }
            else if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.TRANSCEIVE_ERROR) {
                errorRes = R.string.err_transceive_error;
            }
            else if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.NO_TAG_PRESENT) {
                errorRes = R.string.err_no_tag_present;
            }
            else if(resp.getErrorCode() == Type4ErrorResponse.ErrorCodes.NFC_CHIP_ERROR) {
                errorRes = R.string.err_nfc_chip_error;
            }
            else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_type4,
                        (StandardErrorResponse) response);
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_type4,
                    errorRes,
                    (StandardErrorResponse) response);
        }
        else {
            return ctx.getString(R.string.unknown_response);
        }
    }

    private static String getClassicResponseDescription(@NonNull TCMPMessage response,
                                                        @NonNull Context ctx) {
        if(response instanceof MifareClassicDetectedResponse) {
            MifareClassicDetectedResponse resp = (MifareClassicDetectedResponse) response;
            if(resp.getType() == MifareClassicDetectedResponse.ClassicType.CLASSIC_1K) {
                return String.format(ctx.getString(R.string.mifareclassic_1k_detected), ByteUtils.bytesToHex(resp.getUid()));
            }
            else if(resp.getType() == MifareClassicDetectedResponse.ClassicType.CLASSIC_4K){
                return String.format(ctx.getString(R.string.mifareclassic_4k_detected), ByteUtils.bytesToHex(resp.getUid()));
            }
            else {
                return ctx.getString(R.string.mifareclassic_unkcap_detected);
            }

        }
        else if(response instanceof MifareClassicTimeoutResponse) {
            return ctx.getString(R.string.mifareclassic_timeout);
        }
        else if(response instanceof MifareClassicReadSuccessResponse) {
            MifareClassicReadSuccessResponse res = (MifareClassicReadSuccessResponse) response;
            return String.format(ctx.getString(R.string.mifareclassic_read_success),
                    ByteUtils.bytesToHex(res.getUid()),
                    res.getStartBlock(),
                    res.getEndBlock(),
                    ByteUtils.bytesToHex(res.getData()));
        }
        else if (response instanceof MifareClassicLibraryErrorResponse) {
            MifareClassicLibraryErrorResponse resp = (MifareClassicLibraryErrorResponse) response;
            int errorRes;
            if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_PARAMETER) {
                errorRes = R.string.err_invalid_parameter;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.TOO_FEW_PARAMETERS) {
                errorRes = R.string.err_too_few_parameters;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.TOO_MANY_PARAMETERS) {
                errorRes = R.string.err_too_many_parameters;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.POLLING_ERROR) {
                errorRes = R.string.err_polling_error;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.TAG_READ_ERROR) {
                errorRes = R.string.err_tag_read_error;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_BLOCK_ORDER) {
                errorRes = R.string.err_invalid_block_order;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.AUTHENTICATION_ERROR) {
                errorRes = R.string.err_authentication_error;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_BLOCK_NO) {
                errorRes = R.string.err_invalid_block_number;
            }
            else if(resp.getErrorCode() == MifareClassicLibraryErrorResponse.ErrorCodes.INVALID_KEY_NO) {
                errorRes = R.string.err_invalid_key_number;
            }
            else {
                return parseStandardErrorResponse(ctx,
                        R.string.family_classic,
                        (StandardErrorResponse) response);
            }

            return parseStandardErrorResponse(ctx,
                    R.string.family_classic,
                    errorRes,
                    (StandardErrorResponse) response);
        }
        else {
            return ctx.getString(R.string.unknown_response);
        }
    }

    private static String parseStandardErrorResponse(Context ctx,
                                                     @StringRes int libraryName,
                                                     @StringRes int description,
                                                     StandardErrorResponse response){
        return String.format(
                ctx.getString(R.string.standard_error_response_with_description),
                response.getErrorCode(),
                response.getInternalErrorCode(),
                response.getReaderStatus(),
                response.getErrorMessage(),
                ctx.getString(libraryName),
                ctx.getString(description));
    }

    private static String parseStandardErrorResponse(Context ctx,
                                                     @StringRes int libraryName,
                                                     StandardErrorResponse response){
            return String.format(
                    ctx.getString(R.string.standard_error_response),
                    response.getErrorCode(),
                    response.getInternalErrorCode(),
                    response.getReaderStatus(),
                    response.getErrorMessage(),
                    ctx.getString(libraryName));
    }

    private static String parseStandardLibraryVersionResponse(Context ctx, @StringRes int libraryName, StandardLibraryVersionResponse response) {
        return String.format(
                ctx.getString(R.string.standard_version_response),
                (0xff & response.getMajorVersion()),
                (0xff & response.getMinorVersion()),
                ctx.getString(libraryName));
    }

    private static String parseNdefFoundResponse(Context ctx, NdefFoundResponse resp) {
        NdefMessage msg = resp.getMessage();
        NdefRecord[] records = msg.getRecords();
        if(records.length == 0) {
            return ctx.getString(R.string.ndef_no_record);
        }
        else if (records.length == 1) {
            return String.format(ctx.getString(R.string.ndef_found_response_single_record),
                    ByteUtils.bytesToHex(resp.getTagCode()),
                    parseTagType(ctx, resp.getTagType()),
                    parseNdefRecord(ctx, records[0]));
        }
        else {
            return String.format(ctx.getString(R.string.ndef_found_response_multi_record),
                    ByteUtils.bytesToHex(resp.getTagCode()),
                    parseTagType(ctx, resp.getTagType()),
                    parseNdefRecord(ctx, records[0]));
        }
    }

    private static String parseNdefRecord(Context ctx, NdefRecord record) {
        if(record.getTnf() == NdefRecord.TNF_WELL_KNOWN) {
            if(Arrays.equals(record.getType(),NdefRecord.RTD_URI)) {
                return parseWellKnownUriRecord(ctx,record);
            }
            else if (Arrays.equals(record.getType(),NdefRecord.RTD_TEXT)) {
                return parseWellKnownTextRecord(ctx, record);
            }
            else {
                return parseGenericNdefRecord(ctx,record);
            }
        }
        else {
            return parseGenericNdefRecord(ctx,record);
        }
    }

    private static String parseWellKnownTextRecord(Context ctx, NdefRecord record) {

        byte[] payload = record.getPayload();

        int status = payload[0] & 0xff;
        int languageCodeLength = (status & 0x1F);
        //not needed currently
        //String languageCode = new String(payload, 1, languageCodeLength);

        Charset textEncoding = ((status & 0x80) != 0) ? Charset.forName("UTF-16") : Charset.forName("UTF-8");
        String field = new String(payload, 1 + languageCodeLength, payload.length - languageCodeLength - 1, textEncoding);
        return ctx.getString(R.string.ndef_record_text_display_format,field);
    }

    private static String parseWellKnownUriRecord(Context ctx, NdefRecord record) {
        byte[] payload = record.getPayload();
        if(payload.length > 1) {
            byte uriCode = payload[0];
            byte[] uri = new byte[payload.length - 1];
            System.arraycopy(payload,1,uri,0,payload.length - 1);
            return ctx.getString(R.string.ndef_record_uri_display_format, NdefUriCodeUtils.decodeNdefUri(uriCode, uri));
        }
        else {
            return parseGenericNdefRecord(ctx,record);
        }
    }

    private static String parseGenericNdefRecord(Context ctx, NdefRecord record) {
        return String.format(ctx.getString(R.string.ndef_record_generic_display_format),
                parseTnf(ctx,record),
                parseType(ctx,record),
                ByteUtils.bytesToHex(record.getPayload()));
    }

    private static String parseTnf(Context ctx, NdefRecord record) {
        short tnf = record.getTnf();
        switch(tnf) {
            case NdefRecord.TNF_ABSOLUTE_URI:
                return ctx.getString(R.string.tnf_abs_uri);
            case NdefRecord.TNF_EMPTY:
                return ctx.getString(R.string.tnf_empty);
            case NdefRecord.TNF_EXTERNAL_TYPE:
                return ctx.getString(R.string.tnf_external);
            case NdefRecord.TNF_MIME_MEDIA:
                return ctx.getString(R.string.tnf_mime_media);
            case NdefRecord.TNF_UNCHANGED:
                return ctx.getString(R.string.tnf_unchanged);
            case NdefRecord.TNF_WELL_KNOWN:
                return ctx.getString(R.string.tnf_well_known);
            default:
                return ctx.getString(R.string.tnf_unknown);
        }
    }

    private static String parseType(Context ctx, NdefRecord record) {
        byte[] type = record.getType();
        if(Arrays.equals(type, NdefRecord.RTD_URI))
            return ctx.getString(R.string.rtd_uri);
        else if(Arrays.equals(type,NdefRecord.RTD_ALTERNATIVE_CARRIER))
            return ctx.getString(R.string.rtd_alt_carrier);
        else if(Arrays.equals(type,NdefRecord.RTD_HANDOVER_CARRIER))
            return ctx.getString(R.string.rtd_handover_carrier);
        else if(Arrays.equals(type,NdefRecord.RTD_HANDOVER_REQUEST))
            return ctx.getString(R.string.rtd_handover_request);
        else if(Arrays.equals(type,NdefRecord.RTD_HANDOVER_SELECT))
            return ctx.getString(R.string.rtd_handover_select);
        else if(Arrays.equals(type,NdefRecord.RTD_SMART_POSTER))
            return ctx.getString(R.string.rtd_smart_poster);
        else if(Arrays.equals(type,NdefRecord.RTD_TEXT))
            return ctx.getString(R.string.rtd_text);
        else if (record.getTnf() == NdefRecord.TNF_MIME_MEDIA)
            return new String(type);
        else
            return ByteUtils.bytesToHex(type);
    }

    private static String parseTagType(Context ctx, byte flag) {
        switch(flag) {
            case TagTypes.MIFARE_ULTRALIGHT: {
                return ctx.getString(R.string.ultralight_title);
            }
            case TagTypes.NTAG203: {
                return ctx.getString(R.string.ntag203_title);
            }
            case TagTypes.MIFARE_ULTRALIGHT_C: {
                return ctx.getString(R.string.ultralight_c_title);
            }
            case TagTypes.MIFARE_STD_1K: {
                return ctx.getString(R.string.std_1k_title);
            }
            case TagTypes.MIFARE_STD_4K: {
                return ctx.getString(R.string.std_4k_title);
            }
            case TagTypes.MIFARE_DESFIRE_EV1_2K: {
                return ctx.getString(R.string.desfire_ev1_2k_title);
            }
            case TagTypes.TYPE_2_TAG: {
                return ctx.getString(R.string.unk_type2_title);
            }
            case TagTypes.MIFARE_PLUS_2K_CL2: {
                return ctx.getString(R.string.plus_2k_title);
            }
            case TagTypes.MIFARE_PLUS_4K_CL2: {
                return ctx.getString(R.string.plus_4k_title);
            }
            case TagTypes.MIFARE_MINI: {
                return ctx.getString(R.string.mini_title);
            }
            case TagTypes.OTHER_TYPE4: {
                return ctx.getString(R.string.other_type4_title);
            }
            case TagTypes.MIFARE_DESFIRE_EV1_4K: {
                return ctx.getString(R.string.desfire_ev1_4k_title);
            }
            case TagTypes.MIFARE_DESFIRE_EV1_8K: {
                return ctx.getString(R.string.desfire_ev1_8k);
            }
            case TagTypes.MIFARE_DESFIRE: {
                return ctx.getString(R.string.desfire_title);
            }
            case TagTypes.TOPAZ_512: {
                return ctx.getString(R.string.topaz_512_title);
            }
            case TagTypes.NTAG_210: {
                return ctx.getString(R.string.ntag_210_title);
            }
            case TagTypes.NTAG_212: {
                return ctx.getString(R.string.ntag_212_title);
            }
            case TagTypes.NTAG_213: {
                return ctx.getString(R.string.ntag_213_title);
            }
            case TagTypes.NTAG_215: {
                return ctx.getString(R.string.ntag_215_title);
            }
            case TagTypes.NTAG_216: {
                return ctx.getString(R.string.ntag_216_title);
            }
            case TagTypes.NO_TAG: {
                return ctx.getString(R.string.no_tag_title);
            }
            case TagTypes.TAG_UNKNOWN:
            default: {
                return ctx.getString(R.string.unk_type_title);
            }
        }
    }
}
