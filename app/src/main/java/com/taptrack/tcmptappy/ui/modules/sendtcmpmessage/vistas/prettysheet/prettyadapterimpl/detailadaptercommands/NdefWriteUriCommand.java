package com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadaptercommands;

import com.taptrack.tcmptappy.tappy.constants.NdefUriCodes;
import com.taptrack.tcmptappy.tcmp.TCMPMessage;
import com.taptrack.tcmptappy.tcmp.commandfamilies.basicnfc.commands.WriteNdefUriRecordCommand;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.CommandItem;
import com.taptrack.tcmptappy.ui.modules.sendtcmpmessage.vistas.prettysheet.prettyadapterimpl.detailadapters.UrlCommandAdapter;

public class NdefWriteUriCommand implements UrlCommandAdapter.UrlCommand {
    private CommandItem item;

    public NdefWriteUriCommand(CommandItem item) {
        this.item = item;
    }

    @Override
    public TCMPMessage getMessage(byte timeout, String uri) {
        if (uri.startsWith("http://")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_HTTP,
                    uri.substring("http://".length()).getBytes());
        } else if (uri.startsWith("https://")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_HTTPS,
                    uri.substring("https://".length()).getBytes());
        } else if (uri.startsWith("http://www.")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_HTTPWWW,
                    uri.substring("http://www.".length()).getBytes());
        } else if (uri.startsWith("https://www.")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_HTTPSWWW,
                    uri.substring("https://www.".length()).getBytes());
        } else if (uri.startsWith("tel:")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_TEL,
                    uri.substring("tel:".length()).getBytes());
        } else if (uri.startsWith("mailto:")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_MAILTO,
                    uri.substring("mailto:".length()).getBytes());
        } else if (uri.startsWith("sms:")) {
            return new WriteNdefUriRecordCommand((byte) timeout,
                    false,
                    NdefUriCodes.URICODE_NOPREFIX,
                    uri.substring("sms:".length()).getBytes());
        }
        else {
            return null;
        }
    }

    @Override
    public boolean isUserInError(byte timeout, String uri) {
        if (uri.startsWith("http://")) {
            return false;
        } else if (uri.startsWith("https://")) {
                return false;
        } else if (uri.startsWith("http://www.")) {
                return false;
        } else if (uri.startsWith("https://www.")) {
                return false;
        } else if (uri.startsWith("tel:")) {
                return false;
        } else if (uri.startsWith("mailto:")) {
                return false;
        } else if (uri.startsWith("sms:")) {
            return false;
        } else {
                return true;
        }
    }

    @Override
    public CommandItem getItem() {
        return item;
    }
}
