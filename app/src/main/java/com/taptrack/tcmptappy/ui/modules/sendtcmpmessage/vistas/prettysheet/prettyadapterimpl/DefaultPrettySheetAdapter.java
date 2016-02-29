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
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.commanddetail.CommandDetailViewAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandFamilyItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.PrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.NoParameterAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.ScanCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TextCommandAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.UrlCommandAdapter;

import java.util.ArrayList;
import java.util.List;

public class DefaultPrettySheetAdapter implements PrettySheetAdapter {
    private List<CommandFamilyItem> commandFamilies;

    private final SparseArray<CommandItem> commands;

    private final List<CommandItem> sortedSystemCommands;
    private final List<CommandItem> sortedBasicNfcCommands;

    public static final int LIB_SYSTEM = 1;
    public static final int LIB_BASIC_NFC = 2;

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

        commandFamilies = new ArrayList<>(2);
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_settings_black_24dp,
                R.string.system_commands_library_title,
                LIB_SYSTEM));
        commandFamilies.add(new CommandFamilyItem(R.drawable.ic_nfc_black_24dp,
                R.string.basic_nfc_library_title,
                LIB_BASIC_NFC));
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
        else
            return new ArrayList<>();
    }

    @Override
    public CommandItem getCommandItem(int itemId) {
        return commands.get(itemId);
    }

    @Override
    public CommandDetailViewAdapter getDetailAdapterForItem(int itemId) {
        if(itemId == KEY_SCAN_NDEF || itemId == KEY_SCAN_UID)
            return new ScanCommandAdapter(getCommandItem(itemId));
        else if (itemId == KEY_WRITE_TEXT)
            return new TextCommandAdapter(getCommandItem(itemId));
        else if (itemId == KEY_WRITE_URL)
            return new UrlCommandAdapter(getCommandItem(itemId));
        else
            return new NoParameterAdapter(getCommandItem(itemId));
    }

}
