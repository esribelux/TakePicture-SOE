package takepicture.tools;

import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.system.ILog;

import java.io.IOException;

public class SOELogger {
    public final static int ERROR = 1;
    public final static int WARNING = 2;
    public final static int NORMAL = 3;
    public final static int DETAILED = 4;
    public final static int DEBUG = 5;
    private ILog serverLog;
    private int serverLogCode;

    public SOELogger(ILog serverLog, int serverLogCode) {
        this.serverLog = serverLog;
        this.serverLogCode = serverLogCode;
    }

    public void debug(String msg) {
        log(DEBUG, msg);
    }

    public void detailed(String msg) {
        log(DETAILED, msg);
    }

    public void normal(String msg) {
        log(NORMAL, msg);
    }

    public void warning(String msg) {
        log(WARNING, msg);
    }

    public void error(String msg) {
        log(ERROR, msg);
    }

    private void log(int level, String msg) {
        if (serverLog == null) {
            if (level == ERROR) {
                System.err.println(serverLogCode + "\t: " + msg);
            } else {
                System.out.println(serverLogCode + "\t: " + msg);
            }
        } else {
            try {
                this.serverLog.addMessage(level, serverLogCode, msg);
            } catch (AutomationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
