package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.GetBasicNfcLibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StopCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.GetMifareClassicLibraryVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.GetType4LibraryVersionCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.NoParameterAdapter;

public class DefaultNoParameterCommands implements NoParameterAdapter.NoParameterCommand {
    private CommandItem item;

    public DefaultNoParameterCommands(CommandItem item) {
        this.item = item;
    }

    @Override
    public TCMPMessage getMessage() {
        switch(item.getCommandType()) {
            case DefaultPrettySheetAdapter.KEY_SYS_LIBV:
                return new GetFirmwareVersionCommand();
            case DefaultPrettySheetAdapter.KEY_BATT:
                return new GetBatteryLevelCommand();
            case DefaultPrettySheetAdapter.KEY_HARDV:
                return new GetHardwareVersionCommand();
            case DefaultPrettySheetAdapter.KEY_PING:
                return new PingCommand();
            case DefaultPrettySheetAdapter.KEY_NFC_LIBV:
                return new GetBasicNfcLibraryVersionCommand();
            case DefaultPrettySheetAdapter.KEY_STOP:
                return new StopCommand();
            case DefaultPrettySheetAdapter.KEY_CLASSIC_LIBV:
                return new GetMifareClassicLibraryVersionCommand();
            case DefaultPrettySheetAdapter.KEY_TYPE4_LIBV:
                return new GetType4LibraryVersionCommand();
        }
        return null;
    }

    @Override
    public CommandItem getItem() {
        return item;
    }
}
