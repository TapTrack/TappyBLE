package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.mifareclassic.commands.ReadMifareClassicCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.ReadClassicCommandAdapter;

public class ReadClassicCommandImpl implements ReadClassicCommandAdapter.ReadClassicCommand {
    private CommandItem item;

    public ReadClassicCommandImpl(CommandItem item) {
        this.item = item;
    }

    @Override
    public CommandItem getItem() {
        return item;
    }

    @Override
    public TCMPMessage getMessage(byte timeout, byte start, byte end, byte keySetting, byte[] key) {
        return new ReadMifareClassicCommand(timeout,start,end,keySetting,key);
    }
}
