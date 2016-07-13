package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.LockTagCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TimeoutCommandAdapter;

public class LockTagCommandWrapper implements TimeoutCommandAdapter.TimeoutCommand {
    private CommandItem commandItem;

    public LockTagCommandWrapper(CommandItem commandItem) {
        this.commandItem = commandItem;
    }

    @Override
    public TCMPMessage getMessage(byte timeout) {
        switch (commandItem.getCommandType()) {
            case DefaultPrettySheetAdapter.KEY_LOCK:
                return new LockTagCommand((byte) timeout, new byte[0]);
        }
        return null;
    }

    @Override
    public CommandItem getItem() {
        return commandItem;
    }
}
