package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.DetectMifareClassicCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4Command;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TimeoutCommandAdapter;

public class DefaultDetectCommands implements TimeoutCommandAdapter.TimeoutCommand {
    private CommandItem commandItem;

    public DefaultDetectCommands(CommandItem commandItem) {
        this.commandItem = commandItem;
    }

    @Override
    public TCMPMessage getMessage(byte timeout) {
        switch (commandItem.getCommandType()) {
            case DefaultPrettySheetAdapter.KEY_DETECT_CLASSIC:
                return new DetectMifareClassicCommand((byte) timeout);
            case DefaultPrettySheetAdapter.KEY_DETECT_TYPE4:
                return new DetectType4Command((byte) timeout);
            case DefaultPrettySheetAdapter.KEY_DETECT_TYPE4B:
                return new DetectType4BCommand((byte) timeout);
        }
        return null;
    }

    @Override
    public CommandItem getItem() {
        return commandItem;
    }
}
