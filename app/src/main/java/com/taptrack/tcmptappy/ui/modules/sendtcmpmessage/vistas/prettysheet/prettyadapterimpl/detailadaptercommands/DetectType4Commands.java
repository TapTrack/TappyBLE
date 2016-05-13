package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import android.support.annotation.Nullable;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4BSpecificAfiCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.type4.commands.DetectType4Command;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.DetectType4CommandAdapter;

public class DetectType4Commands implements DetectType4CommandAdapter.DetectType4Command {
    private CommandItem commandItem;

    public DetectType4Commands(CommandItem commandItem) {
        this.commandItem = commandItem;
    }

    @Override
    public CommandItem getItem() {
        return commandItem;
    }

    @Override
    public TCMPMessage getMessage(byte timeout, boolean typeA, @Nullable Byte afi) {
        if(typeA) {
            return new DetectType4Command(timeout);
        }
        else {
            if(afi == null) {
                return new DetectType4BCommand(timeout);
            }
            else {
                return new DetectType4BSpecificAfiCommand(timeout,afi);
            }
        }
    }
}
