package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.TransceiveApduCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.HexCommandAdapter;

public class SendTransceiveApduCommand implements HexCommandAdapter.HexCommand {
    private CommandItem item;

    public SendTransceiveApduCommand(CommandItem item) {
        this.item = item;
    }

    @Override
    public TCMPMessage getMessage(byte[] hex) {
        return new TransceiveApduCommand(hex);
    }

    @Override
    public CommandItem getItem() {
        return item;
    }
}
