package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefTextRecordCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.TextCommandAdapter;

public class NdefTextWriteCommand implements TextCommandAdapter.TextCommand {
    private CommandItem item;

    public NdefTextWriteCommand(CommandItem item) {
        this.item = item;
    }

    @Override
    public TCMPMessage getMessage(byte timeout, String text) {
        return new WriteNdefTextRecordCommand((byte) timeout,false,text.getBytes());
    }

    @Override
    public CommandItem getItem() {
        return item;
    }
}
