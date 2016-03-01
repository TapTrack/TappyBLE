package com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily;

import com.taptrack.tcmptappy.tcmp.MalformedPayloadException;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetBatteryLevelCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetHardwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.GetFirmwareVersionCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.PingCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.commands.SetConfigItemCommand;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.ConfigItemResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.CrcMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.FirmwareVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.GetBatteryLevelResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.HardwareVersionResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.ImproperMessageFormatResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.LcsMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.LengthMismatchErrorResponse;
import com.taptrack.tcmptappy.tcmp.commandfamilies.systemfamily.responses.PingResponse;
import com.taptrack.tcmptappy.tcmp.common.CommandCodeNotSupportedException;
import com.taptrack.tcmptappy.tcmp.common.CommandFamily;
import com.taptrack.tcmptappy.tcmp.common.ResponseCodeNotSupportedException;

public class SystemCommandLibrary implements CommandFamily {
    public static final byte[] FAMILY_ID = new byte[]{0x00,0x00};

    @Override
    public com.taptrack.tcmptappy.tcmp.TCMPMessage parseCommand(com.taptrack.tcmptappy.tcmp.TCMPMessage message) throws CommandCodeNotSupportedException {
        switch(message.getCommandCode()) {
            case GetHardwareVersionCommand.COMMAND_CODE:
                return new GetHardwareVersionCommand(message.getPayload());

            case GetFirmwareVersionCommand.COMMAND_CODE:
                return new GetFirmwareVersionCommand(message.getPayload());

            case GetBatteryLevelCommand.COMMAND_CODE:
                return new GetBatteryLevelCommand(message.getPayload());

            case PingCommand.COMMAND_CODE:
                return new PingCommand(message.getPayload());

            case SetConfigItemCommand.COMMAND_CODE:
                return new SetConfigItemCommand(message.getPayload());

            default:
                throw new CommandCodeNotSupportedException(
                        SystemCommandLibrary.class.getSimpleName()+
                                " doesn't support response code "+String.format("%02X",message.getCommandCode()));
        }
    }

    @Override
    public com.taptrack.tcmptappy.tcmp.TCMPMessage parseResponse(com.taptrack.tcmptappy.tcmp.TCMPMessage message) throws ResponseCodeNotSupportedException, MalformedPayloadException {
        switch(message.getCommandCode()) {
            case ConfigItemResponse.COMMAND_CODE:
                return new ConfigItemResponse(message.getPayload());

            case CrcMismatchErrorResponse.COMMAND_CODE:
                return new CrcMismatchErrorResponse(message.getPayload());

            case FirmwareVersionResponse.COMMAND_CODE:
                return new FirmwareVersionResponse(message.getPayload());

            case GetBatteryLevelResponse.COMMAND_CODE:
                return new GetBatteryLevelResponse(message.getPayload());

            case HardwareVersionResponse.COMMAND_CODE:
                return new HardwareVersionResponse(message.getPayload());

            case ImproperMessageFormatResponse.COMMAND_CODE:
                return new ImproperMessageFormatResponse(message.getPayload());

            case LcsMismatchErrorResponse.COMMAND_CODE:
                return new LcsMismatchErrorResponse(message.getPayload());

            case LengthMismatchErrorResponse.COMMAND_CODE:
                return new LengthMismatchErrorResponse(message.getPayload());

            case PingResponse.COMMAND_CODE:
                return new PingResponse(message.getPayload());

            default:
                throw new ResponseCodeNotSupportedException(
                        SystemCommandLibrary.class.getSimpleName()+
                                " doesn't support response code "+String.format("%02X", message.getCommandCode()));
        }
    }

    @Override
    public byte[] getCommandFamilyId() {
        return FAMILY_ID;
    }
}
