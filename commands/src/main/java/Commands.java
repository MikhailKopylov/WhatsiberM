public enum Commands {

    AUTH_OK("/authOK"),
    AUTH_WRONG("/authWrong"),
    CHECK_AUTH("/auth"),
    CHANGE_NICK("/changeNick"),
    CHANGE_NICK_OK("/changeNickOK"),
    CHANGE_NICK_WRONG("/changeNickWrong"),
    EXIT("/end"),
//    EXISTS_NICK("/existsNick"),
    ONLINE_WRONG("/onlineWrong"),
    PRIVATE_MESSAGE("/privateMsg"),
    REG_OK("/regOk"),
    REG_WRONG("/regWrong"),
    TRY_REG("/reg"),
    USER_LIST("/userList"),
    USER_ONLINE("/userOnline");


    private final String command;

    Commands(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return command + " ";
    }

    static public Commands convertToCommand(String command) {
        for (int i = 0; i < Commands.values().length; i++) {
            if (command.equals(Commands.values()[i].toString().trim())) {
                return Commands.values()[i];
            }
        }
        return null;
    }

}
