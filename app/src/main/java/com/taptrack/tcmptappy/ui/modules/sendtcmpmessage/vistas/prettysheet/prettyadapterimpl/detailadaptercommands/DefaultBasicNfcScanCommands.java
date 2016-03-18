package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.PollingModes;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.ScanNdefCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.ScanTagCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StreamNdefCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.StreamTagsCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.DefaultPrettySheetAdapter;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.ScanCommandAdapter;

public class DefaultBasicNfcScanCommands implements ScanCommandAdapter.TimeoutContinuousCommand {
    private CommandItem item;

    public DefaultBasicNfcScanCommands(CommandItem item) {
        this.item = item;
    }

    @Override
    public TCMPMessage getMessage(byte timeout, boolean isContinuous) {
        switch (item.getCommandType()) {
            case DefaultPrettySheetAdapter.KEY_SCAN_NDEF:
                if(isContinuous)
                    return new StreamNdefCommand((byte) timeout, PollingModes.MODE_GENERAL);
                else
                    return new ScanNdefCommand((byte) timeout, PollingModes.MODE_GENERAL);
            case DefaultPrettySheetAdapter.KEY_SCAN_UID:
                if(isContinuous)
                    return new StreamTagsCommand((byte) timeout, PollingModes.MODE_GENERAL);
                else
                    return new ScanTagCommand((byte) timeout, PollingModes.MODE_GENERAL);
        }
        return null;
    }

    @Override
    public CommandItem getItem() {
        return item;
    }
}
