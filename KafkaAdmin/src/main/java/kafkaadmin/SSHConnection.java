package kafkaadmin;

import com.jcraft.jsch.*;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;

public class SSHConnection {
    public void execute(String command) throws JSchException, IOException {
        Session session = getSSHConnection();
        session.connect();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.setInputStream(null);
        ((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.setOutputStream(System.out);
        channel.connect();

        byte[] tmp = new byte[1024];

        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) break;
            }

            if (channel.isClosed()) {
                System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }

            try {
                Thread.sleep(1000);
            } catch (Exception ee) {
            }
        }

        channel.disconnect();
        session.disconnect();
        System.out.println("done");
    }

    private Session getSSHConnection() throws JSchException {
        // Get host, username, password from .env
        Dotenv dotenv = null;
        dotenv = Dotenv.configure().load();

        // Open ssh connection and return session object
        Session session = new JSch().getSession(dotenv.get("SSH_LOGIN"), dotenv.get("SSH_HOST"), 22);
        session.setPassword(dotenv.get("SSH_PASSWORD"));
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.setConfig("StrictHostKeyChecking", "no");

        return session;
    }
}
