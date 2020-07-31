import commands.Commands;

public class MsgCommandAuth extends MsgCommand {

    private String login;
    private String pass;



    public MsgCommandAuth(Commands command, String login, String pass) {
        super(command);
    }

    @Override
    public String getTextMessage() {
        return String.format("%s %s", login, pass);
    }

    @Override
    public String msgToString() {
        return String.format("%s %s %s", super.getCommands().toString(), login, pass);
    }
}
