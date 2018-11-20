
package com.mediatek.factorymode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.mediatek.factorymode.emsvr.AFMFunctionCallEx;
import com.mediatek.factorymode.emsvr.FunctionReturn;


public class ShellExe {
    public static String ERROR = "ERROR";
    private static final String OPERATION_ERROR_PREFIX = "#$ERROR^&";

    private static StringBuilder sb = new StringBuilder("");

    public static String getOutput() {
        return sb.toString();
    }

    public static int execCommand(String[] command) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command);
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

        sb.delete(0, sb.length());
        try {
            if (proc.waitFor() != 0) {
                sb.append(ERROR);
                return -1;
            } else {
                String line;
                line = bufferedreader.readLine();
                if (line != null) {
                    sb.append(line);
                } else {
                    return 0;
                }
                while (true) {
                    line = bufferedreader.readLine();
                    if (line == null) {
                        break;
                    } else {
                        sb.append('\n');
                        sb.append(line);
                    }
                }
                return 0;
            }
        } catch (InterruptedException e) {
            sb.append(ERROR);
            return -1;
        }
    }

     public static int execCommandOnServer(String[] command) throws IOException {
        if (command == null || command.length == 0) {
            throw new IllegalArgumentException("Invalid shell command to execute");
        }
        StringBuilder cmdBuilder = new StringBuilder();
        int i = 0;
        // ignore specified sh, use the server default sh
        if ("sh".equals(command[0]) || command[0].endsWith("/sh")) {
            if (command.length < 3) {
                throw new IllegalArgumentException("invalid or unknown cmd to execute");
            }
            i = 2;
        }
        for (; i < command.length; i++) {
            cmdBuilder.append(command[i]);
            if (i < command.length - 1) {
                cmdBuilder.append(" ");
            }
        }
        String cmd = cmdBuilder.toString();
        cmd = cmd.trim();
        sb.delete(0, sb.length());
        boolean ret = true;
        AFMFunctionCallEx functionCall = new AFMFunctionCallEx();
        ret = functionCall.startCallFunctionStringReturn(AFMFunctionCallEx.FUNCTION_EM_SHELL_CMD_EXECUTION);
        if (ret) {
            functionCall.writeParamNo(1);
            functionCall.writeParamString(cmd);
            FunctionReturn funcRet = null;
            do {
                funcRet = functionCall.getNextResult();
                if (funcRet.mReturnString != null && funcRet.mReturnString.length() > 0) {
                    sb.append(funcRet.mReturnString);
                }
            } while (funcRet.mReturnCode == AFMFunctionCallEx.RESULT_CONTINUE);
            // trim the tail newline character
            while (true) {
                int len = sb.length();
                if (len <= 0) {
                    break;
                }
                char c = sb.charAt(len - 1);
                if (c == '\n') {
                    sb.delete(len - 1, len);
                } else {
                    break;
                }
            }
            String output = sb.toString();
            if (output != null && output.startsWith(OPERATION_ERROR_PREFIX)) {
                
                sb.delete(0, OPERATION_ERROR_PREFIX.length() + 1);
                return -1;
            }
            return 0;
        }
        
        sb.append(ERROR);
        return -1;
    }
}
