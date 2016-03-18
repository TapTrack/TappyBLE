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

package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl;

import android.util.SparseArray;

import com.taptrack.tappyble.R;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandFamilyItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.PrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.NdefTextWriteCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.NdefWriteUriCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.DefaultNoParameterCommands;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.DefaultBasicNfcScanCommands;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.DefaultDetectCommands;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands.SendTransceiveApduCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.HexCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.NoParameterAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.ScanCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TextCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TimeoutCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.UrlCommandAdapter;

import java.util.ArrayList;
import java.util.List;

public class DefaultPrettySheetAdapter implements PrettySheetAdapter {
    private List<CommandFamilyItem> commandFamilies;

    private final SparseArray<CommandItem> commands;

    private final List<CommandItem> sortedSystemCommands;
    private final List<CommandItem> sortedBasicNfcCommands;
    private final List<CommandItem> sortedType4Commands;
    private final List<CommandItem> sortedClassicCommands;

    public static final int LIB_SYSTEM = 1;
    public static final int LIB_BASIC_NFC = 2;
    public static final int LIB_TYPE_4 = 3;
    public static final int LIB_CLASSIC = 4;

    public final static int KEY_SYS_LIBV = 0;
    public final static int KEY_BATT = 1;
    public final static int KEY_HARDV = 2;
    public final static int KEY_PING = 3;

    public static final int KEY_NFC_LIBV = 4;
    public static final int KEY_SCAN_NDEF = 5;
    public static final int KEY_SCAN_UID = 6;
    public static final int KEY_WRITE_TEXT = 7;
    public static final int KEY_WRITE_URL = 8;
    public static final int KEY_STOP = 9;

    public static final int KEY_DETECT_CLASSIC = 10;
    public static final int KEY_CLASSIC_LIBV = 11;
    public static final int KEY_READ_CLASSIC = 12;

    public static final int KEY_DETECT_TYPE4 = 13;
    public static final int KEY_TYPE4_LIBV = 14;
    public static final int KEY_TRANSCEIVE_APDU = 15;

    public DefaultPrettySheetAdapter() {
        commands = new SparseArray<>(10);

        sortedSystemCommands = new ArrayList<>(4);
        sortedSystemCommands.add(new CommandItem(
                KEY_PING,
                R.string.syscommand_ping_title,
                R.string.syscommand_ping_description,
                R.drawable.ic_wifi_tethering_black_48dp));
        sortedSystemCommands.add(new CommandItem(
                KEY_BATT,
                R.string.syscommand_get_battery_title,
                R.string.syscommand_get_battery_description,
                R.drawable.ic_battery_unknown_black_48dp));
        sortedSystemCommands.add(new CommandItem(
                KEY_HARDV,
                R.string.syscommand_get_hardware_title,
                R.string.syscommand_get_hardware_description,
                R.drawable.ic_info_outline_black_48dp));
        sortedSystemCommands.add(new CommandItem(
                KEY_SYS_LIBV,
                R.string.syscommand_get_libraryv_title,
                R.string.syscommand_get_libraryv_description,
                R.drawable.ic_help_outline_black_48dp));

        for(int i = 0; i < sortedSystemCommands.size(); i++) {
            CommandItem commandItem = sortedSystemCommands.get(i);
            commands.put(commandItem.getCommandType(),commandItem);
        }

        sortedBasicNfcCommands = new ArrayList<>(6);
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_SCAN_NDEF,
                R.string.nfccommand_scan_ndef_title,
                R.string.nfccommand_scan_ndef_description,
                R.drawable.ic_nmark_black_48dp));
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_SCAN_UID,
                R.string.nfccommand_scan_tag_title,
                R.string.nfccommand_scan_tag_description,
                R.drawable.ic_nfc_black_48dp));
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_STOP,
                R.string.nfccommand_stop_title,
                R.string.nfccommand_stop_description,
                R.drawable.ic_stop_black_48dp));
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_WRITE_URL,
                R.string.nfccommand_write_uri_title,
                R.string.nfccommand_write_uri_description,
                R.drawable.ic_link_black_48dp));
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_WRITE_TEXT,
                R.string.nfccommand_write_text_title,
                R.string.nfccommand_write_text_description,
                R.drawable.ic_description_black_48dp));
        sortedBasicNfcCommands.add(new CommandItem(
                KEY_NFC_LIBV,
                R.string.nfccommand_get_libraryv_title,
                R.string.nfccommand_get_libraryv_description,
                R.drawable.ic_help_outline_black_48dp));

        for(int i = 0; i < sortedBasicNfcCommands.size(); i++) {
            CommandItem commandItem = sortedBasicNfcCommands.get(i);
            commands.put(commandItem.getCommandType(),commandItem);
        }

        sortedClassicCommands = new ArrayList<>(3);
        sortedClassicCommands.add(new CommandItem(
                KEY_DETECT_CLASSIC,
                R.string.classiccommand_detect_title,
                R.string.classiccommand_detect_description,
                R.drawable.ic_nfc_black_48dp));
        sortedClassicCommands.add(new CommandItem(
                KEY_CLASSIC_LIBV,
                R.string.classiccommand_get_version_title,
                R.string.classiccommand_get_version_description,
                R.drawable.ic_help_outline_black_48dp));

        for(int i = 0; i < sortedClassicCommands.size(); i++) {
            CommandItem commandItem = sortedClassicCommands.get(i);
            commands.put(commandItem.getCommandType(),commandItem);
        }

        sortedType4Commands = new ArrayList<>(3);
        sortedType4Commands.add(new CommandItem(
                KEY_DETECT_TYPE4,
                R.string.type4command_detect_title,
                R.string.type4command_detect_description,
                R.drawable.ic_nfc_black_48dp));
        sortedType4Commands.add(new CommandItem(
                KEY_TRANSCEIVE_APDU,
                R.string.type4command_transceive_apdu_title,
                R.string.type4command_transceive_apdu_description,
                R.drawable.ic_compare_arrows_black_48dp));
        sortedType4Commands.add(new CommandItem(
                KEY_TYPE4_LIBV,
                R.string.type4command_get_version_title,
                R.string.type4command_get_version_description,
                R.drawable.ic_help_outline_black_48dp));

        for(int i = 0; i < sortedType4Commands.size(); i++) {
            CommandItem commandItem = sortedType4Commands.get(i);
            commands.put(commandItem.getCommandType(),commandItem);
        }

        commandFamilies = new ArrayList<>(4);
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_settings_black_24dp,
                R.string.system_commands_library_title,
                LIB_SYSTEM));
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_nfc_black_24dp,
                R.string.basic_nfc_library_title,
                LIB_BASIC_NFC));
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_type4source_black_24dp,
                R.string.type_4_library_title,
                LIB_TYPE_4));
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_classic_black_24dp,
                R.string.mifareclassic_library_title,
                LIB_CLASSIC));
    }

    @Override
    public List<CommandFamilyItem> getCommandFamilyOptions() {
        return commandFamilies;
    }

    @Override
    public List<CommandItem> getCommandsForFamily(int family) {
        if (family == LIB_SYSTEM)
            return sortedSystemCommands;
        else if(family == LIB_BASIC_NFC)
            return sortedBasicNfcCommands;
        else if (family == LIB_TYPE_4)
            return sortedType4Commands;
        else if (family == LIB_CLASSIC)
            return sortedClassicCommands;
        else
            return new ArrayList<>();
    }

    @Override
    public CommandItem getCommandItem(int itemId) {
        return commands.get(itemId);
    }

    @Override
    public CommandDetailViewAdapter getDetailAdapterForItem(int itemId) {
        if(itemId == KEY_SCAN_NDEF || itemId == KEY_SCAN_UID) {
            return new ScanCommandAdapter(new DefaultBasicNfcScanCommands(getCommandItem(itemId)));
        }
        else if(itemId == KEY_DETECT_CLASSIC || itemId == KEY_DETECT_TYPE4) {
            return new TimeoutCommandAdapter(new DefaultDetectCommands(getCommandItem(itemId)));
        }
        else if (itemId == KEY_WRITE_TEXT) {
            return new TextCommandAdapter(new NdefTextWriteCommand(getCommandItem(itemId)));
        }
        else if (itemId == KEY_WRITE_URL) {
            return new UrlCommandAdapter(new NdefWriteUriCommand(getCommandItem(itemId)));
        }
        else if (itemId == KEY_TRANSCEIVE_APDU) {
            return new HexCommandAdapter(new SendTransceiveApduCommand(getCommandItem(itemId)));
        }
        else {
            return new NoParameterAdapter(new DefaultNoParameterCommands(getCommandItem(itemId)));
        }
    }

}
